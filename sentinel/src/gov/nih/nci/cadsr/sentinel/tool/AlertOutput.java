// Copyright (c) 2006 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tool/AlertOutput.java,v 1.5 2008-01-14 14:55:36 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

/**
 * This class encapsulates the output to the Sentinel Alert Report Log files.
 * 
 * @author lhebel
 *
 */
public class AlertOutput
{
    /**
     * Constructor
     * 
     * @param work_ The working path for the file output stream.
     * @param http_ The URL prefix mapped to the working path.
     * @param name_ The basic file name.
     * @param version_ The version of the Sentinel Tool software.
     *
     */
    public AlertOutput(String work_, String http_, String name_, String version_)
    {
        // Get the current date and time.
        _today = Timemarker.timeNow();

        _work = work_;
        _http = http_;
        _name = name_;
        _version = version_;
        _logErrorCnt = 0;

        // Remove any suspicious special characters.
        _logFile = _name + "_" + _today.toString().replaceAll("[ .:\\-]", "");;

        // Of course the directory separator may be the escape character
        // so it's REAL special.
        if (File.separatorChar == '\\')
            _logFile = _logFile.replaceAll("[\\\\]", "_");
        else
            _logFile = _logFile.replaceAll(File.separator, "_");

        // This should be a unique workable file name.
        _logFile = _work + _logFile + ".html";
    }

    /**
     * Get the HTTP link to the output file.
     * 
     * @return The URL.
     */
    public String getHttpLink()
    {
        return new String(_http + _logFile.substring(_work.length())).replaceAll(" ", "%20");
    }

    /**
     * Get the Working Path link to the output file.
     * 
     * @return The full file path.
     */
    public String getPathLink()
    {
        return _logFile;
    }
    
    /**
     * Write a message to the log file and set the log errors flag. This is only
     * used to set the error flag to 'true'.
     * 
     * @param txt_
     *        The message to log.
     */
    public void writeError(String txt_)
    {
        if (txt_ != null)
        {
            _logErrors = true;
            ++_logErrorCnt;
            writeText("</p><p class=\"alert\"><pre>\n" + txt_ + "\n</pre>");
        }
    }
    
    /**
     * Test for calls to logError()
     * 
     * @return true if the logError() method was called.
     */
    public boolean hasErrors()
    {
        return _logErrors;
    }
    
    /**
     * Reset the flag which tracks calls to logError()
     * 
     */
    public void resetErrors()
    {
        _logErrors = false;
    }

    /**
     * Test that a call was made to logError() sometime during the life of this object. The
     * result is not altered by the resetErrors() method.
     * 
     * @return true if the logError() method was ever called.
     */
    public boolean someErrors()
    {
        return (_logErrorCnt > 0);
    }

    /**
     * Write a message to the log file. The error flag is not changed from its'
     * current setting.
     * 
     * @param txt_
     *        The text to write.
     */
    public synchronized void writeText(String txt_) 
    {
        try
        {
            // First message opens the log file.
            if (_log == null)
            {
                _log = new FileOutputStream(_logFile, false);
                _logger.info("Opened log: " + _today.toString() + ": "
                    + _logFile);
                String temp;
                temp = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n"
                    + "<html><head>\n"
                    + "<meta http-equiv=Content-Type content=\"text/html; charset=windows-1252\">\n"
                    + "<title>Sentinel Alert " + _name + " Report</title>\n"
                    + "<style>\n"
                    + "\tBODY { font-family: Arial; font-size: 10pt }\n"
                    + "\tP { margin: 0.1in 0.1in 0.0in 0.1in }\n"
                    + "\tP.ALERT { font-weight: bold }\n"
                    + "\tP.DETAIL0 { MARGIN-LEFT: 0.0in }\n"
                    + "\tP.DETAIL1 { margin-left: 0.5in }\n"
                    + "\tP.DETAIL2 { margin-left: 1.0in }\n"
                    + "\tTABLE { font-family: Arial; font-size: 10pt }\n"
                    + "\tTABLE.MATRIX { margin: 0.1in 0.1in 0.0in 1.0in; border-spacing: 0in; border-collapse: collapse }\n"
                    + "\tDIV.REPORT { margin: 0.1in 0.1in 0.0in 1.0in; font-weight: bold }\n"
                    + "\tSPAN.ACTION { cursor: default }\n"
                    + "</style>\n"
                    + "</head>\n"
                    + "<body>\n"
                    + "<p class=\"alert\">caDSR Sentinel "
                    + _version
                    + "</p>\n<p class=\"alert\">Created on " + _today.toString() + "</p>\n";
                _log.write(temp.getBytes());
            }

            // Write message to log file and add a new line.
            _log.write(txt_.getBytes());
            _log.write("\n".getBytes());

            // If something wierd happens we want the message in the file so
            // flush the buffers.
            _log.flush();
        }
        catch (FileNotFoundException ex)
        {
            _log = null;
            _logger.error(ex.toString());
        }
        catch (IOException ex)
        {
            _logger.error(ex.toString());
        }
    }

    /**
     * Output a paragraph formatted as a detail class.
     * 
     * @param msg_ The text to appear flush left.
     */
    public void writeParagraph0(String msg_)
    {
        writeText("<p class=\"detail0\">" + msg_ + "</p>");
    }

    /**
     * Output a paragraph formatted as a detail class.
     * 
     * @param msg_ The text to appear indented.
     */
    public void writeParagraph1(String msg_)
    {
        writeText("<p class=\"detail1\">" + msg_ + "</p>");
    }

    /**
     * Output a paragraph formatted as a detail class.
     * 
     * @param msg_ The text to appear indented.
     */
    public void writeParagraph2(String msg_)
    {
        writeText("<p class=\"detail2\">" + msg_ + "</p>");
    }

    /**
     * Output a paragraph formatted as an alert class.
     * 
     * @param msg_ The text to appear as a section heading.
     */
    public void writeHeading(String msg_)
    {
        writeText("<p class=\"alert\">" + msg_ + "</p>");
    }

    /**
     * Output a formatted table.
     * 
     * @param msg_ The body of a table.
     */
    public void writeTable(String msg_)
    {
        writeText("<table style=\"font-size: 10pt\">" + msg_ + "</table>");
    }

    /**
     * Format a matrix into a table tag.
     * 
     * @param prefix_ table prefix
     * @param msg_ The TR and TD content tags of the table.
     * @param cols_ The number of columns in a single row.
     * @param right_ true if the last column should be right justified.
     * @param suffix_ table suffix
     */
    public void writeMatrix(String prefix_, String msg_, int cols_, boolean right_, String suffix_)
    {
        String extra = "";
        for (int i = 1; i < cols_; ++i)
        {
            extra = extra + "<col style=\"padding-right: 0.1in\" />";
        }
        if (right_)
        {
            extra = extra + "<col style=\"text-align: right\" />";
        }
        else
        {
            extra = extra + "<col />";
        }
        writeText(prefix_ + "<table class=\"matrix\"><colgroup>"
            + extra
            + "</colgroup><tbody />\n"
            + msg_ + "\n</table>\n" + suffix_);
    }

    /**
     * Insert an hr tag.
     *
     */
   public void writeHR()
    {
        writeText("<hr>");
    }

   /**
    * Close the log file.
    */
   public void close()
   {
       // As we are only sending a link to the log file and there may be an
       // error in the SMTP connection
       // we wait to close the file here.
       try
       {
           // Report the processing time.
           Timestamp today = Timemarker.timeNow();
           String msg = Timemarker.timeDiff(today, _today);
           writeParagraph1("End timestamp: " + today.toString());
           writeParagraph1("Elapsed processing time: " + msg);
           writeHeading("caDSR Sentinel " + _version);
           writeText("</body></html>");

           // Close the file and make it permanent.
           _log.flush();
           _log.close();
           _log = null;
           _logger.info("Closed log: " + _today.toString() + ": "
               + _logFile);
       }
       catch (IOException ex)
       {
           _logger.error(ex.toString());
       }
   }
    
    private String _work;
    private String _http;
    private String _name;
    private String _version;
    private FileOutputStream    _log;
    private boolean             _logErrors;
    private int                 _logErrorCnt;
    private String              _logFile;
    private Timestamp           _today;
    private static final Logger _logger = Logger.getLogger(AlertOutput.class.getName());
}
