// Copyright (c) 2004 ScenPro, Inc.

package com.scenpro.DSRAlert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Stack;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Encapsulate the logic to process an Alert definition. The process may be
 * started manually from the user interface and via command line script as an
 * "auto" run. It is intended the system administrator will create a scheduled
 * task to execute once every day using a command script - hence the reference
 * to the "auto" process as it runs unattended.
 * <p>
 * The differences in the logic between manual and auto runs is minimal. A
 * manual run executes the same methods as an auto run except that a single
 * Alert definition is used for manual and an array of definitions are used for
 * auto. Additionally the manual process starts a separate thread to process the
 * request which frees the main process to continue managing HTTP requests. The
 * auto process runs all Alerts sequentially in a single thread.
 * <p>
 * Because the manual run executes in a separate thread, any number may be
 * started by the same user or different users without fear of collision.
 * However, it is important to note at this time, the threads run on the same
 * application as the HTTP servlets which start them. Consequently, too many
 * manual requests can cause some performance impacts to responses. If this
 * becomes a problem a separate server will be created and all run requests,
 * auto and manual, will be passed to the server via an HTTP request which can
 * be configured to run anywhere.
 * <p>
 * Also important to note is that all output from the processes are stored in
 * files. There is no browser or other user interface to report progress. Once
 * the run is finished, the designated administrator receives an email of the
 * log and the recipients receive and email of the Alert output.
 * 
 * @author Larry Hebel
 */

public class AutoProcessAlerts
{
    /**
     * Constructor
     */
    public AutoProcessAlerts()
    {
        // Set the default output format for the report.
        _outForm = 2;

        // We have custom logic for logging the run.
        _log = null;
        _logErrors = false;
        _logErrorCnt = 0;

        // Default database information if the properties file is missing.
        _driver = "oci8";
        _tnsname = "cbdev";
        _user = "sbrext";
        _pswd = "jjuser";

        // A location to store all log and report files.
        _work = "alerts_";

        // The active database connection.
        _db = null;

        // The location of the properties file.
        _resourcePath = "";

        // For the insert logic to work easily when recording recipients and
        // the reports they receive, we seed the lists with a 'blank' entry.
        _recipients = new String[1];
        _recipients[0] = "";
        _reports = new ReportItem[1][];
        _reports[0] = new ReportItem[0];

        // Get the current date and time.
        Date now = new Date();
        _today = new Timestamp(now.getTime());
    }

    /**
     * This process is intended to be started by a system administrator
     * scheduled task or manually by an administrator when necessary. For
     * optimal performance this should run locally to the database server (i.e.
     * LAN proximity not WAN). The amount of database access is extremely high
     * even with the computed date ranges used in the SQL selects.
     * 
     * @param args_
     *        None at this time.
     */
    public static void main(String[] args_)
    {
        // All logic is contained in the object so create a new instance and
        // run.
        AutoProcessAlerts apa = new AutoProcessAlerts();
        apa.autoRun();
    }

    /**
     * This private class is used to track local process information about each
     * Alert. It seems best to have this wrapper for the extra informaiton
     * rather than add it to the AlertRec class.
     */
    private class ProcessRec
    {
        public AlertRec _alert;

        public String   _reportFile;

        public boolean  _errors;
    }

    /**
     * Reset the Auto Run time stamp in the Alert Definition.
     * 
     * @param rec_
     *        The Alert definition.
     */
    private void resetAutoRun(ProcessRec rec_)
    {
        _db.updateRun(rec_._alert.getAlertRecNum(), _today, true, rec_._alert
            .isActiveOnce());
        logError(_db.getError());
    }

    /**
     * Reset the Manual Run time stamp in the Alert Definitions.
     * 
     * @param rec_
     *        The Alert definition.
     */
    private void resetManualRun(ProcessRec rec_)
    {
        _db.updateRun(rec_._alert.getAlertRecNum(), _today, false, false);
        logError(_db.getError());
    }

    /**
     * Keep track of the name of the Alert and the file link for the emails.
     */
    private class ReportItem
    {
        public ReportItem()
        {
            _file = "";
            _name = "";
        }

        public String _file;

        public String _name;
    }

    /**
     * Record a new report for distribution to a specific user. This is part of
     * general logic to consolidate all reports to a recipient into a single
     * email.
     * 
     * @param pos_
     *        The position in the recipient list (who will receive the report).
     * @param file_
     *        The file containing the report output.
     * @param name_
     *        The name of the Alert definition.
     */
    private void appendReport(int pos_, String file_, String name_)
    {
        // Increase the array by 1 element.
        int len = _reports[pos_].length + 1;
        ReportItem temp[] = new ReportItem[len];

        // Copy the exiting report list for this recipient.
        int ndx;
        for (ndx = 0; ndx < _reports[pos_].length; ++ndx)
        {
            temp[ndx] = _reports[pos_][ndx];
        }

        // Add the new report to the end of the list.
        temp[ndx] = new ReportItem();
        temp[ndx]._file = file_;
        temp[ndx]._name = name_;

        // Discard the old list in preference to the new one.
        _reports[pos_] = temp;
    }

    /**
     * Insert a recipient into the queue. This is part of larger logic which
     * consolidates all reports for a single recipient into a single email. The
     * recipient list is sorted alphabetically ascending to allow for binary
     * searches.
     * 
     * @param pos_
     *        The position in the recipient array to insert the new email
     *        address.
     * @param email_
     *        The email address to store.
     */
    private void insertRecipient(int pos_, String email_)
    {
        // Increase the array by 1 entry.
        String temp[] = new String[_recipients.length + 1];
        ReportItem trep[][] = new ReportItem[_reports.length + 1][];

        // Copy the existing entries.
        int ndx;
        for (ndx = 0; ndx < pos_; ++ndx)
        {
            temp[ndx] = _recipients[ndx];
            trep[ndx] = _reports[ndx];
        }

        // Insert the new entry in the desired location in the sorted list.
        temp[ndx] = email_;
        trep[ndx] = new ReportItem[0];

        // Finish the copy of existing entries.
        for (int ndx2 = ndx; ndx2 < _recipients.length; ++ndx2)
        {
            temp[++ndx] = _recipients[ndx2];
            trep[ndx] = _reports[ndx2];
        }

        // Discard the old list in preference for the new.
        _recipients = temp;
        _reports = trep;
    }

    /**
     * Save the report with the list of recipients for later.
     * 
     * @param rec_
     *        The process record.
     */
    private void queueReport(ProcessRec rec_)
    {
        // Get the list of desired recipients.
        String list[] = rec_._alert.getRecipients();
        String file = (rec_._errors) ? "" : rec_._reportFile;
        String name = rec_._alert.getName();

        // The recipients are coded such that anything starting with
        // a "/" is the conte_idseq for the groups of curators for a
        // specific context. If an "@" appears in the recipient string
        // it is a well formed email address and can be used asis.
        // Finally the recipient name must be a single user id known to
        // the caDSR.
        for (int item = 0; item < list.length; ++item)
        {
            // Resolve all recipients to email addresses. By doing this
            // we can guarantee that should a creator place an email
            // address in one Alert and use the user id in another, only
            // 1 email will be sent to the recipient.
            String temp[] = null;
            if (list[item].charAt(0) == '/')
            {
                temp = _db.selectEmailsFromConte(list[item]);
                logError(_db.getError());
            }
            else if (list[item].indexOf('@') < 0)
            {
                temp = new String[1];
                temp[0] = _db.selectEmailFromUser(list[item]);
                logError(_db.getError());
            }
            else
            {
                temp = new String[1];
                temp[0] = list[item];
            }

            // Perform a binary search on the recipient email list.
            for (int ndx = 0; ndx < temp.length; ++ndx)
            {
                // Yes it's possible that a user does not have an email
                // address.
                if (temp[ndx] == null || temp[ndx].length() == 0)
                    continue;

                // Standard binary search implementation.
                int min = 0;
                int max = _recipients.length;
                while (true)
                {
                    int pos = (max + min) / 2;
                    int compare = temp[ndx].compareTo(_recipients[pos]);
                    if (compare == 0)
                    {
                        // When the recipient is already in the list we need
                        // only
                        // add the report name.
                        appendReport(pos, file, name);
                        break;
                    }
                    else if (compare > 0)
                    {
                        if (min == pos)
                        {
                            // When the recipient is not in the list be sure
                            // to position the name alphabetically and add
                            // the report also.
                            insertRecipient(++pos, temp[ndx]);
                            appendReport(pos, file, name);
                            break;
                        }
                        min = pos;
                    }
                    else
                    {
                        if (max == pos)
                        {
                            // When the recipient is not in the list be sure
                            // to position the name alphabetically and add
                            // the report also.
                            insertRecipient(pos, temp[ndx]);
                            appendReport(pos, file, name);
                            break;
                        }
                        max = pos;
                    }
                }
            }
        }
    }

    /**
     * Perform processing on all the eligible Alerts.
     */
    private void autoRun2()
    {
        // Pull a list of all active alerts from the database that should be run
        // at this time.
        AlertRec alerts[] = getAlertList();
        if (alerts == null)
        {
            log("\tNo Alerts available to run on " + _today.toString());
            return;
        }
        log("Alert Definitions to process: " + alerts.length + "\n");
        ProcessRec list[] = new ProcessRec[alerts.length];
        for (int ndx = 0; ndx < list.length; ++ndx)
        {
            list[ndx] = new ProcessRec();
            list[ndx]._alert = alerts[ndx];
        }

        // Calculate the ending date/time for the SQL queries in this run.
        _end = setToMidnight(_today);

        // Process each Alert definition and find the raw data.
        for (int ndx = 0; ndx < list.length && list[ndx] != null; ++ndx)
        {
            // The date range for each Alert will begin at midnight on the Last
            // Run Date in the
            // AlertRec and end at midnight of the date this process runs. If
            // the Last Run Date
            // is null it will be defaulted to the current date minus the
            // frequency unit.
            _start = list[ndx]._alert.getAdate();
            if (_start == null)
            {
                // Back up in time using the auto run frequency.
                _start = timeTravel(list[ndx]._alert, _end);
            }
            else
            {
                // Use midnight of the last run date as a start point.
                _start = setToMidnight(_start);
            }
            log("\nProcessing Alert Definition: " + ndx + ": "
                + list[ndx]._alert.getName() + " (" + _start.toString()
                + " TO " + _end.toString() + ")");

            // Pull the caDSR changes given the Alert definition and the date
            // range.
            pullDBchanges(list[ndx]);

            // If errors occurred, tell the user to contact the administrator.
            list[ndx]._errors = _logErrors;
            if (_logErrors)
            {
                queueReport(list[ndx]);
                _logErrors = false;
            }

            else if (_outRows > 0 || list[ndx]._alert.isSendEmptyReport())
            {
                // Build the recipient list for this report.
                queueReport(list[ndx]);

                // Reset the last run time in the database. If we don't queue a
                // report we
                // don't update the time as the processing produced no results.
                resetAutoRun(list[ndx]);
            }
        }
    }

    /**
     * Perform the Auto Run process for all eligible Alerts in this caDSR
     * database. And eligible Alert must be active for today's run. For example,
     * if it is Tuesday and an Alert is defined to run on Wednesday it is not
     * eligible.
     */
    public void autoRun()
    {
        // Identify this run.
        _id = "Auto";

        // Load resources. Must be done first as we need configuration
        // information from the
        // properties file.
        getResources();

        // Flag the start of the log.
        log("caDSR Sentinel Alert Auto Run Process Starts... "
            + _today.toString() + "\n\tDatabase = " + _dbname + " (" + _tnsname
            + ")\n\tWorking folder prefix = " + _work);

        // Process the Alerts.
        autoRun2();

        // Done with the database.
        if (_db != null)
            _db.close();
        _db = null;

        // Send all emails now.
        sendEmails();

        // Flag the end of the log.
        log("caDSR Sentinel Alert Auto Run Process Ends... "
            + _today.toString());
        sendLog();
        closeLog();
    }

    /**
     * Send the reports to the recipients. Currently this is done by providing
     * URL links to the actual report files. A number of other options were
     * considered but all have advantages and disadvantages.
     * <p>
     * <ul>
     * <li>Send the reports as links in the body of the email. Advantages,
     * keeps the emails small. Disadvantages, the recipient must have access to
     * the server to use the link. Also requires some space maintenance on the
     * server by an administrator.</li>
     * <li>Send the reports as attachments to the email. Advantages, the
     * recipient has the file in his possesion and can manage his own email
     * list. Disadvantages, the files can be such a size that the email server
     * is overloaded for the distribution.</li>
     * <li>Zip all the reports and add as a file attachment to the eamil.
     * Advantages, keeps the email relative reasonable in size. Disadvantages,
     * must be able to extract the files from zip. Also some email servers strip
     * zip files to control spam and for security.</li>
     * <li>Embed the report into the body of the email. Advantages, none
     * really. Disadvantages, all those mentioned above relating to attaching
     * files additionally the email becomes difficult to read.</li>
     * </ul>
     * </p>
     */
    private void sendEmails()
    {
        log("\nSending emails...");

        // Be sure we have something to send.
        if (_recipients.length < 2)
        {
            log("No activity to email.");
            return;
        }

        // The first location (index 0) in the _recipients list is an empty
        // place holder.
        // We send one email to each recipient.
        for (int ndx = 1; ndx < _recipients.length; ++ndx)
        {
            // Form the body of the email.
            boolean hasErrors;
            hasErrors = false;
            String prefix = "";
            String body = "\n\n\"Alert Name\"\tLink\n\n";
            log("\tTo: " + _recipients[ndx] + "\n\t\tNumber of reports: "
                + _reports[ndx].length);
            for (int ndx2 = 0; ndx2 < _reports[ndx].length; ++ndx2)
            {
                String link = _reports[ndx][ndx2]._file;
                if (link.length() == 0)
                {
                    if (prefix.length() == 0)
                        prefix = "\n\n" + _adminIntroError;
                    link = "*** Errors ***";
                    hasErrors = true;
                }
                else
                {
                    link = _http
                        + link.substring(link.lastIndexOf(File.separator) + 1);
                    link = link.replaceAll(" ", "%20");
                }
                body = body + "\"" + _reports[ndx][ndx2]._name + "\"\t" + link
                    + "\n";
            }

            // Complete subject and body and send to recipient.
            String subject = _subject
                + ((_id.charAt(0) == 'A') ? " Auto Run" : " Manual Run")
                + " for " + _today.toString().substring(0, 10)
                + ((hasErrors) ? " has Errors" : "");
            body = _adminIntro + prefix + body;
            sendEmail((hasErrors) ? 1 : 0, _recipients[ndx], "", subject, body);
        }
        log("Completed emails...\n");
    }

    /**
     * Create a thread wrapper around the processing of a single Alert.
     */
    private class AlertThread extends Thread
    {
        public AlertThread(AlertRec rec_)
        {
            _rec = rec_;
        }

        /**
         * Process the single Alert.
         */
        public void run()
        {
            // Get configuration information.
            getResources();
            log("caDSR Sentinel Alert Manual Run Process Starts... "
                + _today.toString() + "\n\tDatabase = " + _dbname + " (" + _tnsname
                + ")\n\tWorking folder prefix = " + _work);

            log("\nProcessing Alert Definition: "
                + _rec.getName() + " (" + _start.toString()
                + " TO " + _end.toString() + ")");

            ProcessRec rec = new ProcessRec();
            rec._alert = _rec;

            // Get the caDSR changes.
            pullDBchanges(rec);

            // Record the report.
            rec._errors = _logErrors;
            queueReport(rec);

            // Reset the run date. We don't care if any activity was found
            // because
            // the recipients always get information about a manual run - error
            // or
            // real output.
            resetManualRun(rec);

            // Done with the database.
            if (_db != null)
                _db.close();
            _db = null;

            // Send to the recipients.
            sendEmails();

            // We're done.
            log("Manual Run for Sentinel Alert ends: " + _rec.getName());
            sendLog();
            closeLog();
        }

        // Class data elements.
        private AlertRec _rec;
    }

    /**
     * Invoke a single alert run using the start and end dates provided. This
     * method will execute in a separate thread.
     * 
     * @param rec_
     *        The alert definition to run. This does not have to be saved in the
     *        database.
     * @param start_
     *        The start of the date range.
     * @param end_
     *        The end of the date range.
     */
    public void manualRun(AlertRec rec_, Timestamp start_, Timestamp end_)
    {
        AlertThread at = new AlertThread(rec_);
        _start = start_;
        _end = end_;
        _id = "Manual " + rec_.getCreator();
        at.start();
    }

    /**
     * Generate a file name to hold the report.
     * 
     * @param rec_
     *        The Alert definition.
     */
    private void getFileName(ProcessRec rec_)
    {
        String temp = rec_._alert.getName().replaceAll("\\W", "_");
        String ts = "_"
            + new Timestamp(new Date().getTime()).toString().replaceAll("\\D",
                "") + ".html";
        rec_._reportFile = _work + temp + ts;
        temp = _http + temp + ts;
        log("\tCreated by " + rec_._alert.getCreator()
            + "\n\tOutput File is\n\t\t" + rec_._reportFile + "\n\t\t" + temp);
    }

    /**
     * Dump the report records to output.
     */
    private void dump(Stack save_, ProcessRec rec_)
    {
        try
        {
            // Open the report file.
            FileOutputStream fout = new FileOutputStream(rec_._reportFile,
                false);

            // Creators email address.
            String cemail = _db.selectEmailFromUser(rec_._alert.getCreator());

            _outRows = 0;
            if (_outForm == 1)
            {
                // Header first.
                ACData.dumpHeader1(_dbname, _style, cemail, rec_._alert,
                    _start, _end, fout);

                // Body
                _outRows += ACData.dumpDetail1(_db, save_, _outRows, fout);
                logError(_db.getError());

                // Footer
                ACData.dumpFooter1((_outRows == 0), _version, rec_._alert, fout);
            }
            else if (_outForm == 2)
            {
                // Header first.
                ACData.dumpHeader2(_dbname, _style, cemail, rec_._alert,
                    _start, _end, fout);

                // Body
                _outRows += ACData.dumpDetail2(_db, save_, _outRows, fout);
                logError(_db.getError());

                // Footer
                ACData.dumpFooter2((_outRows == 0), _version, rec_._alert, fout);
            }

            // Close file.
            fout.close();
        }
        catch (FileNotFoundException ex)
        {
            logError("Error opening auto alert dump file: " + rec_._reportFile);
        }
        catch (IOException ex)
        {
            logError("Error writing to auto alert dump file: "
                + rec_._reportFile);
        }
    }

    /**
     * Filter the list based on monitors in the Alert definition.
     * 
     * @param list_
     *        The current data set.
     * @param rec_
     *        The Alert details.
     * @return The new data set.
     */
    private ACData[] filter(ACData list_[], ProcessRec rec_)
    {
        ACData temp[] = ACData.filter(list_, rec_._alert);
        ACData.resolveChanges(_db, temp);
        logError(_db.getError());
        return temp;
    }

    /**
     * Create a dataset of all changes from the caDSR.
     * 
     * @param rec_
     *        The Alert definition.
     */
    private void pullDBchanges(ProcessRec rec_)
    {
        // Be sure we have a database connection.
        if (_db == null)
        {
            _db = new DBAlert();
            if (_db.open(_driver, _tnsname, _user, _pswd) != 0)
            {
                logError(_db.getError());
                _db = null;
                return;
            }
        }
        
        // Determine file name that will eventually hold the output.
        getFileName(rec_);

        // Check for changes to Valid Values from the Questions on
        // Forms/Templates.
        String creators[] = rec_._alert.getCreators();
        String modifiers[] = rec_._alert.getModifiers();
        ACData qcv[] = _db.selectQCV(_start, _end, creators, modifiers);
        logError(_db.getError());
        qcv = filter(qcv, rec_);

        // Check for changes to Questions on Forms/Templates.
        ACData qcq[] = _db.selectQCQ(_start, _end, creators, modifiers);
        logError(_db.getError());
        qcq = filter(qcq, rec_);

        // Check for changes to Modules on Forms/Templates.
        ACData qcm[] = _db.selectQCM(_start, _end, creators, modifiers);
        logError(_db.getError());
        qcm = filter(qcm, rec_);

        // Check for changes to Forms/Templates.
        ACData qc[] = _db.selectQC(_start, _end, creators, modifiers);
        logError(_db.getError());
        qc = filter(qc, rec_);

        // Check for changes to Permissible Values
        ACData pv[] = _db.selectPV(_start, _end, creators, modifiers);
        logError(_db.getError());
        pv = filter(pv, rec_);

        // Check for changes to Value Domains
        ACData vd[] = _db.selectVD(_start, _end, creators, modifiers);
        logError(_db.getError());
        vd = filter(vd, rec_);

        // Don't worry about Conceptual Domains yet, that comes later in the
        // logis below.
        ACData cd[] = new ACData[0];

        // Check for changes to Data Element Concepts
        ACData dec[] = _db.selectDEC(_start, _end, creators, modifiers);
        logError(_db.getError());
        dec = filter(dec, rec_);

        // Check for changes to Data Elements
        ACData de[] = _db.selectDE(_start, _end, creators, modifiers);
        logError(_db.getError());
        de = filter(de, rec_);

        // Check for changes to Classification Scheme Items
        ACData csi[] = _db.selectCSI(_start, _end, creators, modifiers);
        logError(_db.getError());
        csi = filter(csi, rec_);

        // Check for changes to Classification Schemes
        ACData cs[] = _db.selectCS(_start, _end, creators, modifiers);
        logError(_db.getError());
        cs = filter(cs, rec_);

        // Check for changes to Contexts
        ACData conte[] = _db.selectCONTE(_start, _end, creators, modifiers);
        logError(_db.getError());
        conte = filter(conte, rec_);

        // Report on the raw change counts.
        log("\tChange count:" + " qcv " + qcv.length + " qcq " + qcq.length
            + ", qcm " + qcm.length + " qc " + qc.length + ", pv " + pv.length
            + ", vd " + vd.length + ", cd " + cd.length + ", dec " + dec.length
            + ", de " + de.length + ", csi " + csi.length + ", cs " + cs.length
            + ", conte " + conte.length);

        // Get the data associated to the changed data. This is done one step at
        // a time using the following
        // assocications.
        //      Permissible Values associate to Value Domains
        //      Value Domains associate to Data Elements and Valid Values
        //      Valid Values associate to Questions
        //      Data Element Concepts associate to Data Elements.
        //      Data Elements associate to Questions and Classification Scheme Items
        //      Questions associate to Modules and Forms/Templates
        //      Forms/Templates associate to Contexts
        //      Classification Scheme Items associate to Classification Schemes
        //      Classification Schemes associate to Contexts.
        ACData vdm[] = ACData.merge(vd, _db.selectVDfromPV(pv));
        logError(_db.getError());
        ACData dem[] = ACData.merge(de, _db.selectDEfromVD(vdm));
        logError(_db.getError());
        dem = ACData.merge(dem, _db.selectDEfromDEC(dec));
        logError(_db.getError());
        ACData qcvm[] = ACData.merge(qcv, _db.selectQCVfromVD(vdm));
        logError(_db.getError());
        ACData qcqm[] = ACData.merge(qcq, _db.selectQCQfromQCV(qcvm));
        logError(_db.getError());
        qcqm = ACData.merge(qcqm, _db.selectQCQfromVD(vdm));
        logError(_db.getError());
        qcqm = ACData.merge(qcqm, _db.selectQCQfromDE(dem));
        logError(_db.getError());
        ACData qcmm[] = ACData.merge(qcm, _db.selectQCMfromQCQ(qcqm));
        logError(_db.getError());
        ACData qca[] = ACData.merge(qc, _db.selectQCfromQCM(qcm));
        logError(_db.getError());
        qca = ACData.merge(qca, _db.selectQCfromQCQ(qcqm));
        logError(_db.getError());
        ACData cdm[] = new ACData[0];
        ACData csim[] = ACData.merge(csi, _db.selectCSIfromDE(dem));
        logError(_db.getError());
        csim = ACData.merge(csim, _db.selectCSIfromDEC(dec));
        logError(_db.getError());
        csim = ACData.merge(csim, _db.selectCSIfromVD(vdm));
        logError(_db.getError());
        ACData csm[] = ACData.merge(cs, _db.selectCSfromCSI(csim));
        logError(_db.getError());
        ACData contem[] = ACData.merge(conte, _db.selectCONTEfromCS(csm));
        logError(_db.getError());
        contem = ACData.merge(contem, _db.selectCONTEfromQC(qca));
        logError(_db.getError());

        // If a CSI, CS or Form is specified, we can not get to a Context
        // directly from the
        // Administered Components.
        if (rec_._alert.isCSIall() && rec_._alert.isCSall()
            && rec_._alert.isFORMSall())
        {
            cd = _db.selectCD(_start, _end, creators, modifiers);
            logError(_db.getError());
            cd = filter(cd, rec_);
            cdm = ACData.merge(cd, _db.selectCDfromVD(vdm));
            logError(_db.getError());
            cdm = ACData.merge(cdm, _db.selectCDfromDEC(dec));
            logError(_db.getError());
            contem = ACData.merge(contem, _db.selectCONTEfromCD(cdm));
            logError(_db.getError());
            contem = ACData.merge(contem, _db.selectCONTEfromVD(vdm));
            logError(_db.getError());
            contem = ACData.merge(contem, _db.selectCONTEfromDE(dem));
            logError(_db.getError());
            contem = ACData.merge(contem, _db.selectCONTEfromDEC(dec));
            logError(_db.getError());
        }

        // Report on the related counts.
        log("\tRelated count:" + " qca " + ((qca == null) ? -1 : qca.length)
            + ", vdm " + ((vdm == null) ? -1 : vdm.length) + ", cdm "
            + ((cdm == null) ? -1 : cdm.length) + ", dem "
            + ((dem == null) ? -1 : dem.length) + ", csim "
            + ((csim == null) ? -1 : csim.length) + ", csm "
            + ((csm == null) ? -1 : csm.length) + ", contem "
            + ((contem == null) ? -1 : contem.length));

        // Scrub the lists and remove everything that is not part of the
        // Criteria.
        conte = ACData.clean(rec_._alert.getContexts(), conte);
        contem = ACData.clean(rec_._alert.getContexts(), contem);
        if (!rec_._alert.isCSIall())
        {
            csim = ACData.clean(rec_._alert.getSchemeItems(), csim);
            csi = ACData.clean(rec_._alert.getSchemeItems(), csi);
        }
        if (!rec_._alert.isCSall())
        {
            csm = ACData.clean(rec_._alert.getSchemes(), csm);
            cs = ACData.clean(rec_._alert.getSchemes(), cs);
        }
        if (!rec_._alert.isFORMSall())
        {
            qca = ACData.clean(rec_._alert.getForms(), qca);
            qc = ACData.clean(rec_._alert.getForms(), qc);
        }

        // Report the scrubbed numbers.
        log("\tClean count:" + " qca " + qca.length + ", csim " + csim.length
            + ", csm " + csm.length + ", contem " + contem.length + ", conte "
            + conte.length);

        // We are about to do a lot of searches using the related id for each
        // record so take
        // a little time to resort everything.
        ACData.sortRelated(qca);
        ACData.sortRelated(vdm);
        ACData.sortRelated(cdm);
        ACData.sortRelated(dem);
        ACData.sortRelated(csim);
        ACData.sortRelated(csm);
        ACData.sortRelated(contem);

        // Build the network for traversing possible related objects.
        ACDataLink lconte = new ACDataLink(contem, true);
        ACDataLink lpv = new ACDataLink(pv);
        ACDataLink lvd = new ACDataLink(vd);
        ACDataLink lde = new ACDataLink(de);
        ACDataLink ldec = new ACDataLink(dec);
        ACDataLink lcs = new ACDataLink(cs);
        ACDataLink lcsi = new ACDataLink(csi);
        ACDataLink lcd = new ACDataLink(cd);
        ACDataLink lqcv = new ACDataLink(qcv);
        ACDataLink lqcq = new ACDataLink(qcq);
        ACDataLink lqcm = new ACDataLink(qcm);
        ACDataLink lqc = new ACDataLink(qc);
        ACDataLink chainVD = new ACDataLink(vdm);
        ACDataLink chainDE = new ACDataLink(dem);
        ACDataLink chainCS = new ACDataLink(csm);
        ACDataLink chainCSI = new ACDataLink(csim);
        ACDataLink chainCD = new ACDataLink(cdm);
        ACDataLink chainQC = new ACDataLink(qca);
        ACDataLink chainQCM = new ACDataLink(qcmm);
        ACDataLink chainQCQ = new ACDataLink(qcqm);
        ACDataLink chainQCV = new ACDataLink(qcvm);

        // In all cases we must be able to follow a principle change all the way
        // to a Context.
        // If we can't it means the CSI, CS, FORM or Context was removed from
        // the list in earlier
        // logic and the change should be ignored by the report.
        ACDataLink head = new ACDataLink(null);
        Stack results = new Stack();
        if (rec_._alert.isFORMSall())
        {
            chainCS.add(lconte);
            chainCSI.add(chainCS);
            chainDE.add(chainCSI);
            chainCD.add(lconte);
            chainVD.add(chainDE);
            chainVD.add(chainCD);
            chainVD.add(chainCSI);
            lpv.add(chainVD);
            lvd.add(chainCD);
            lvd.add(chainDE);
            lvd.add(chainCSI);
            ldec.add(chainCD);
            ldec.add(chainDE);
            ldec.add(chainCSI);
            lde.add(chainCSI);
            lcsi.add(chainCS);
            lcs.add(lconte);
        }
        if (rec_._alert.isCSIall() && rec_._alert.isCSall())
        {
            chainQC.add(lconte);
            chainQCM.add(chainQC);
            chainQCQ.add(chainQCM);
            chainQCQ.add(chainQC);
            chainQCV.add(chainQCQ);
            chainVD.add(chainQCV);
            chainVD.add(chainDE);
            chainDE.add(chainQCQ);
            lvd.add(chainQCV);
            lde.add(chainQCQ);
            lqcv.add(chainQCQ);
            lqcq.add(chainQCM);
            lqcq.add(chainQC);
            lqcm.add(chainQC);
            lqc.add(lconte);
            lpv.add(chainVD);
            lvd.add(chainDE);
            lvd.add(chainQCQ);
            ldec.add(chainDE);
        }
        if (rec_._alert.isCSIall() && rec_._alert.isCSall()
            && rec_._alert.isFORMSall())
        {
            lvd.add(lconte);
            ldec.add(lconte);
            lde.add(lconte);
            lcd.add(lconte);
        }
        chainVD = null;
        chainDE = null;
        chainCS = null;
        chainCSI = null;
        chainCD = null;
        chainQC = null;
        chainQCM = null;
        chainQCQ = null;
        chainQCV = null;
        lconte = null;

        lpv.follow(results, 0, lpv.getRange());
        lvd.follow(results, 0, lvd.getRange());
        lde.follow(results, 0, lde.getRange());
        ldec.follow(results, 0, ldec.getRange());
        lcs.follow(results, 0, lcs.getRange());
        lcsi.follow(results, 0, lcsi.getRange());
        lcd.follow(results, 0, lcd.getRange());
        lqcv.follow(results, 0, lqcv.getRange());
        lqcq.follow(results, 0, lqcq.getRange());
        lqcm.follow(results, 0, lqcm.getRange());
        lqc.follow(results, 0, lqc.getRange());
        ACData.remember(results, conte);

        // What ever is placed on the stack ("results") should be output to a
        // file.
        dump(results, rec_);

        // Did we do anything?
        if (_outRows == 0)
            log("\tNo activity to report.");
        else
            log("\tActivity to report: " + _outRows + " rows.");
    }

    /**
     * Using the Auto Run frequency and an anchor date, backup the appropriate
     * number of days to calculate a working start.
     * 
     * @param rec_
     *        The Alert definition.
     * @param anchor_
     *        The anchor date (effectively becomes the end date for the query)
     * @return The new target date.
     */
    private Timestamp timeTravel(AlertRec rec_, Timestamp anchor_)
    {
        if (rec_.isFreqDay())
        {
            // Back up 24 hours.
            return new Timestamp(anchor_.getTime() - (1 * 24 * 60 * 60 * 1000));
        }
        if (rec_.isFreqWeek())
        {
            // Back up 7 days
            return new Timestamp(anchor_.getTime() - (7 * 24 * 60 * 60 * 1000));
        }
        if (rec_.isFreqMonth())
        {
            // Back up 1 month (31 days).
            return new Timestamp(anchor_.getTime() - (31 * 24 * 60 * 60 * 1000));
        }
        return null;
    }

    /**
     * Set the time portion of the date to 00:00:00.0 (midnight).
     * 
     * @param time_
     *        The specified date and time.
     * @return The same date as provided with the time set to midnight.
     */
    private Timestamp setToMidnight(Timestamp time_)
    {
        String time = time_.toString().substring(0, 10);
        return Timestamp.valueOf( time + " 00:00:00.0");
    }

    /**
     * Get the list of Alert definitions that are active for the date specified.
     * 
     * @return The list of alerts.
     */
    private AlertRec[] getAlertList()
    {
        AlertRec recs[] = null;
        if (_db == null)
        {
            // Haven't connected to the database yet.
            _db = new DBAlert();
            if (_db.open(_driver, _tnsname, _user, _pswd) != 0)
            {
                logError(_db.getError());
                _db = null;
                return recs;
            }
        }

        // Get the eligible Alerts.
        recs = _db.selectAlerts(_today);

        // Did we get anything?
        if (recs != null && (recs.length == 0 || recs[0] == null))
            recs = null;
        return recs;
    }

    /**
     * Search for the DSRAlert.properties file on the current drive.
     * 
     * @param path_
     */
    private void findResources(String path_)
    {
        if (new File(path_ + File.separator + _RESOURCES).exists())
        {
            _resourcePath = path_ + File.separator + _RESOURCES;
            return;
        }

        String list[] = new File(path_).list();
        for (int ndx = 0; ndx < list.length; ++ndx)
        {
            String dir = path_ + File.separator + list[ndx];
            File temp = new File(dir);
            if (temp.isDirectory())
            {
                findResources(temp.getAbsolutePath());
                if (_resourcePath.length() > 0)
                    return;
            }
        }
    }

    /**
     * Load the configuration options from the resource/property file.
     * 
     * @return 0 if successful, otherwise !0
     */
    private int getResources()
    {
        ResourceBundle prb = PropertyResourceBundle.getBundle(_RESOURCES);
        if (prb == null)
        {
            System.err
                .println("DSRAlert: AutoProcessAlerts: Can not find properties resource "
                    + _RESOURCES);
            return -1;
        }
        _driver = prb.getString(Constants._DBDRIVER);
        _tnsname = prb.getString(Constants._DBTNSNAME);
        _user = prb.getString(Constants._DBUSER);
        _pswd = prb.getString(Constants._DBPSWD);
        _dbname = prb.getString(Constants._DBNAME);
        _version = prb.getString(Constants._APLVERS);
        _work = prb.getString(_WORKING);
        _subject = prb.getString(_SUBJECT);
        _adminEmail = prb.getString(_ADMINEMAIL);
        _adminName = prb.getString(_ADMINNAME);
        _adminIntro = prb.getString(_ADMININTRO);
        _adminIntroError = prb.getString(_ADMININTROERROR);
        _emailHost = prb.getString(_EMAILHOST);
        _emailUser = prb.getString(_EMAILUSER);
        _emailPswd = prb.getString(_EMAILPSWD);
        _http = prb.getString(_HTTP);
        _style = prb.getString(_STYLE).replaceAll("}", "}\n");
        return 0;
    }

    /**
     * Write a message to the log file and set the log errors flag. This is only
     * used to set the error flag to 'true'.
     * 
     * @param txt_
     *        The message to log.
     */
    private void logError(String txt_)
    {
        if (txt_ != null)
        {
            _logErrors = true;
            ++_logErrorCnt;
            log(txt_);
        }
    }

    /**
     * Write a message to the log file. The error flag is not changed from its'
     * current setting.
     * 
     * @param txt_
     *        The message to log.
     */
    private void log(String txt_)
    {
        try
        {
            // First message opens the log file.
            if (_log == null)
            {
                // Remove any suspicious special characters.
                _logFile = "RunLog_" + _id.replaceAll("[ .:\\-]", "_") + "_"
                    + _today.toString().replaceAll("[ .:\\-]", "");

                // Of course the directory separator may be the escape character
                // so it's REAL special.
                if (File.separatorChar == '\\')
                    _logFile = _logFile.replaceAll("[\\\\]", "_");
                else
                    _logFile = _logFile.replaceAll(File.separator, "_");

                // This should be a unique workable file name.
                _logFile = _work + _logFile + ".txt";
                _log = new FileOutputStream(_logFile, false);
                System.out.println("Opened log: " + _today.toString() + ": "
                    + _logFile);
            }

            // Write message to log file and add a newline.
            _log.write(txt_.getBytes());
            _log.write("\n".getBytes());

            // If something wierd happens we want the message in the file so
            // flush the buffers.
            _log.flush();
        }
        catch (FileNotFoundException ex)
        {
            _log = null;
            System.err.println(ex.toString());
        }
        catch (IOException ex)
        {
            System.err.println(ex.toString());
        }
    }

    /**
     * Send the Log file to the system administrator.
     */
    private void sendLog()
    {
        String body;
        if (_log == null)
        {
            // This is bad - a log file should ALWAYS be created.
            body = "*** Alert Run Log file was not created.";
            _logErrorCnt = 1;
            System.err.println(body);
        }
        else
        {
            // Send link to log file.
            String temp = _http
                + _logFile.substring(_logFile.lastIndexOf(File.separator) + 1);
            temp = temp.replaceAll(" ", "%20");
            body = "Please review the LOG from the caDSR Alert Run.\n\n\""
                + _logFile + "\"\n\n\"" + temp + "\"";
        }

        // Create subject.
        String subject;
        subject = _subject
            + ((_id.charAt(0) == 'A') ? " Auto Run" : " Manual Run") + " LOG"
            + ((_logErrorCnt > 0) ? " with ERRORS" : "");

        // Send email.
        sendEmail(_logErrorCnt, _adminEmail, _adminName, subject, body);
    }

    /**
     * Send an email through the transport.
     * 
     * @param errorCnt_
     *        The error count if problems occurred during the report build.
     * @param toEmail_
     *        The recipient email address.
     * @param toName_
     *        The recipient name.
     * @param subject_
     *        The email subject line.
     * @param body_
     *        The email body.
     */
    private void sendEmail(int errorCnt_, String toEmail_, String toName_,
        String subject_, String body_)
    {
        try
        {
            // Create mail Session.
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            // Create message.
            MimeMessage message = new MimeMessage(session);

            // Set priority, "1" is high, "3" is normal - this is where the
            // error flag comes in.
            message.addHeader("X-Priority", (errorCnt_ > 0) ? "1" : "3");

            // Set basics and give the administrator a quick clue about any
            // errors during the
            // run.
            message.setSubject(subject_);
            message.setFrom(new InternetAddress(_adminEmail, _adminName));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                toEmail_, toName_));

            // Give the real location and the "http" location.
            message.setText(body_);

            // Give to the mail server.
            message.saveChanges();
            Store store = null;
            Transport transport = session.getTransport("smtp");
            if (_emailUser == null || _emailUser.length() == 0)
            {
                transport.connect(_emailHost, null, null);
            }
            else
            {
                store = session.getStore("pop3");
                store.connect(_emailHost, _emailUser, _emailPswd);
                transport.connect(_emailHost, _emailUser, _emailPswd);
            }
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            if (store != null)
                store.close();
        }

        catch (AuthenticationFailedException ex)
        {
            String temp = ex.toString();
            logError(temp);
            System.err.println(temp);
        }
        catch (MessagingException ex)
        {
            String temp = ex.toString();
            logError(temp);
            System.err.println(temp);
        }
        catch (UnsupportedEncodingException ex)
        {
            String temp = ex.toString();
            logError(temp);
            System.err.println(temp);
        }
    }

    /**
     * Close the log file.
     */
    private void closeLog()
    {
        // As we are only sending a link to the log file and there may be an
        // error in the SMTP connection
        // we wait to close the file here.
        try
        {
            // Report the processing time.
            Date now = new Date();
            Timestamp today = new Timestamp(now.getTime());
            long elapsed = today.getTime() - _today.getTime();
            int hrs = (int) (elapsed / (1000 * 60 * 60));
            elapsed -= hrs * 1000 * 60 * 60;
            hrs += 100;
            int mins = (int) (elapsed / (1000 * 60));
            elapsed -= mins * 1000 * 60;
            mins += 100;
            int secs = (int) (elapsed / 1000);
            elapsed -= secs * 1000;
            secs += 100;
            elapsed += 1000;
            String msg = String.valueOf(hrs).substring(1) + ":"
                + String.valueOf(mins).substring(1) + ":"
                + String.valueOf(secs).substring(1) + "."
                + String.valueOf(elapsed).substring(1);
            msg = "\nEnd timestamp: " + today.toString()
                + "\nElapsed processing time: " + msg + "\n";
            _log.write(msg.getBytes());
            msg = "\ncaDSR Sentinel " + _version + "\n";
            _log.write(msg.getBytes());

            // Close the file and make it permanent.
            _log.flush();
            _log.close();
            _log = null;
            System.out.println("Closed log: " + _today.toString() + ": "
                + _logFile);
        }
        catch (IOException ex)
        {
            System.err.println(ex.toString());
        }
    }

    // Class data elements.
    private String              _driver;

    private String              _tnsname;

    private String              _user;

    private String              _pswd;

    private String              _dbname;

    private String              _version;

    private int                 _outRows;

    private String              _work;

    private DBAlert             _db;

    private String              _resourcePath;

    private String              _recipients[];

    private ReportItem          _reports[][];

    private String              _subject;

    private String              _adminEmail;

    private String              _adminName;

    private String              _adminIntro;

    private String              _adminIntroError;

    private String              _emailHost;

    private String              _emailUser;

    private String              _emailPswd;

    private String              _http;

    private String              _style;

    private Timestamp           _start;

    private Timestamp           _end;

    private Timestamp           _today;

    private FileOutputStream    _log;

    private boolean             _logErrors;

    private int                 _logErrorCnt;

    private String              _logFile;

    private String              _id;

    private int                 _outForm;

    private static final String _RESOURCES       = "com.scenpro.DSRAlert.DSRAlert";

    /**
     * The Auto Process administrator email address coded in
     * DSRAlert.properties.
     */
    private static final String _ADMINEMAIL      = "AutoProcess.admin.email";

    /**
     * The Auto Process email introduction for recipient distributed messages
     * coded in DSRAlert.properties.
     */
    private static final String _ADMININTRO      = "AutoProcess.intro";

    /**
     * The Auto Process email introduction addendum when errors occurred
     * generating the report file coded in DSRAlert.properties.
     */
    private static final String _ADMININTROERROR = "AutoProcess.introError";

    /**
     * The Auto Process administrator name/title to appear in the "From" field
     * on recipient distributed emails coded in DSRAlert.properties.
     */
    private static final String _ADMINNAME       = "AutoProcess.admin.name";

    /**
     * The Auto Process email password for connection to the email host coded in
     * DSRAlert.properties.
     */
    private static final String _EMAILPSWD       = "AutoProcess.emailpassword";

    /**
     * The Auto Process email server host IP or Name for message distribution
     * coded in DSRAlert.properties.
     */
    private static final String _EMAILHOST       = "AutoProcess.emailhost";

    /**
     * The Auto Process email user id for connection to the email host coded in
     * DSRAlert.properties.
     */
    private static final String _EMAILUSER       = "AutoProcess.emailusername";

    /**
     * The Auto Process HTTP prefix string for links set to Alert email
     * recipients coded in DSRAlert.properties
     */
    private static final String _HTTP            = "AutoProcess.http";

    /**
     * The Auto Process CSS style definitions used for the Alert report coded in
     * DSRAlert.properties.
     */
    private static final String _STYLE           = "AutoProcess.style";

    /**
     * The Auto Process email subject line for all distribution to recipients
     * and administrators coded in DSRAlert.properties.
     */
    private static final String _SUBJECT         = "AutoProcess.subject";

    /**
     * The Auto Process working forder for temporary files and report output.
     * Must be relative to the machine on which the process executes and coded
     * in DSRAlert.properties.
     */
    private static final String _WORKING         = "AutoProcess.workingfolder";
}