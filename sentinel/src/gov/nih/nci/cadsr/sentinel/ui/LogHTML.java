/*
 * Copyright (c) 2006 ScenPro, Inc.
 */

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/LogHTML.java,v 1.3 2007-07-19 15:26:45 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

import gov.nih.nci.cadsr.sentinel.database.DBAlert;
import gov.nih.nci.cadsr.sentinel.database.DBAlertUtil;
import gov.nih.nci.cadsr.sentinel.tool.ACData;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Provide the audit history for an Administered Component.
 * 
 * @author lhebel
 */
public class LogHTML extends Action
{
    /**
     * Constructor
     *
     */
    public LogHTML()
    {
    }
    
    /**
     * Action process for the user Login.
     * 
     * @param mapping_
     *        The action map defined in struts-config.xml.
     * @param form_
     *        The form bean for this jsp.
     * @param request_
     *        The servlet request object.
     * @param response_
     *        The servlet response object.
     * @return The action to forward to continue processing.
     */
    public ActionForward execute(ActionMapping mapping_, ActionForm form_,
        HttpServletRequest request_, HttpServletResponse response_)
    {
        // Check the requested output form. No reason to proceed if it's
        // an unsupported client.
        LogHTMLForm form = (LogHTMLForm) form_;
        int version = form.getVer(); 
        if (version < 1 || 1 < version)
        {
            response_.setStatus(HttpURLConnection.HTTP_NOT_IMPLEMENTED);
            return null;
        }

        // Use the Sentinel admin account.
        DBAlert db = DBAlertUtil.factory();
        String accnt[] = new String[2];
        int msgnum = LogonForm.initialize("sentinel", request_, accnt);
        if (msgnum != 0)
        {
            response_.setStatus(HttpURLConnection.HTTP_UNAUTHORIZED);
            return null;
        }

        // Remember the admin account.
        String userid = accnt[0];

        // Set default return and message.
        int rc = HttpURLConnection.HTTP_INTERNAL_ERROR;
        String html = "Unexpected Internal Error";

        // Connect to the database.
        if (db.open(request_, userid) == 0)
        {
            // Retrieve record history
            ACData[] history = db.selectWithIDSEQ(form.getIdseq());
            if (history.length == 0)
            {
                html = "No history available.";
                rc = HttpURLConnection.HTTP_FORBIDDEN;
            }
            else
            {
                ACData.resolveChanges(db, history);
                html = history[0].formatACHistoryHTML(db);
                rc = HttpURLConnection.HTTP_OK;
            }
            db.close();
        }

        // Send result to requestor.
        try
        {
            if (rc == HttpURLConnection.HTTP_OK)
            {
                response_.setStatus(rc);
                PrintWriter out;
                out = response_.getWriter();
                out.println(html);
                out.close();
            }
            else
            {
                response_.sendError(rc, html);
            }
        }
        catch (IOException ex)
        {
            _logger.error(ex.toString());
        }
        
        return null;
    }
    
    private static final Logger _logger = Logger.getLogger(LogHTML.class.getName());
}
