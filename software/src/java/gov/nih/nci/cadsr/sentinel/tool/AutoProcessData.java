/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

// Copyright (c) 2006 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tool/AutoProcessData.java,v 1.3 2007-12-17 18:13:54 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tool;

import gov.nih.nci.cadsr.sentinel.database.DBAlert;

/**
 * @author lhebel
 *
 */
public class AutoProcessData
{
    /**
     * Constructor
     *
     */
    public AutoProcessData()
    {
    }

    /**
     * Load the options for the Auto Process run.
     * 
     * @param db_ the database access object
     */
    public void getOptions(DBAlert db_)
    {
        _adminEmail = db_.selectAlertReportAdminEmails();
        _statReportEmail = db_.selectStatReportEmails();
        _threshold = db_.selectReportThreshold();
        _adminIntro = db_.selectAlertReportEmailIntro().replaceAll("[\\n]", "<br/>");
        _adminIntroError = db_.selectAlertReportEmailError().replaceAll("[\\n]", "<br/>");
        _adminName = db_.selectAlertReportAdminTitle();
        _emailAddr = db_.selectAlertReportEmailAddr();
        _emailHost = db_.selectAlertReportEmailHost();
        _emailUser = db_.selectAlertReportEmailHostUser();
        _emailPswd = db_.selectAlertReportEmailHostPswd();
        _http = db_.selectAlertReportHTTP();
        _dtd = db_.selectDtdHTTP();
        _subject = db_.selectAlertReportEmailSubject();
        _work = db_.selectAlertReportOutputDir();
        _dbname = db_.selectAlertReportDBName();
    }
    
    /**
     * The Alert Report administrator emails.
     */
    public String[] _adminEmail;

    /**
     * The statistics report recipient emails.
     */
    public String[] _statReportEmail;

    /**
     * The Alert Report threshold count to guard against larger reports.
     */
    public int _threshold;

    /**
     * The email introduction for all Alert emails.
     */
    public String _adminIntro;

    /**
     * The email introduction when errors occur.
     */
    public String _adminIntroError;

    /**
     * The Alert Report administrator name for the "From" on emails.
     */
    public String _adminName;

    /**
     * The SMTP host DNS 
     */
    public String _emailHost;

    /**
     * The SMTP server access account.
     */
    public String _emailUser;

    /**
     * The SMTP server access password.
     */
    public String _emailPswd;

    /**
     * The Reply To email address.
     */
    public String _emailAddr;

    /**
     * The HTTP prefix for links to the reports.
     */
    public String _http;

    /**
     * The HTTP prefix for links to the dtd files.
     */
    public String _dtd;

    /**
     * The email subject for report distribution.
     */
    public String _subject;

    /**
     * The working folder prefix which holds all generated files.
     */
    public String _work;

    /**
     * The user readable database name.
     */
    public String _dbname;
}
