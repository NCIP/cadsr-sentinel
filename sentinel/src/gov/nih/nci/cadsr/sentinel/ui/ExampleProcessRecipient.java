// Copyright (c) 2007 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/ExampleProcessRecipient.java,v 1.2 2007-12-07 22:15:50 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

/**
 * This class provides an example of a Process Recipient. Also see the ExampleProcessRecipientForm.java class.
 * 
 * @author lhebel
 *
 */
public class ExampleProcessRecipient extends Action
{
    private static final Logger _logger = Logger.getLogger(ExampleProcessRecipient.class.getName());

    /**
     * 
     */
    public ExampleProcessRecipient()
    {
        super();
    }

    /**
     * Action process for Process Recipient.
     * 
     * @param mapping_ The struts action mapping defined for this action in the
     *        struts-config.xml file.
     * @param form_ The struts form defined in the struts-config.xml file.
     * @param request_ The servlet request object.
     * @param response_ The servlet response object.
     * @return The action to continue processing.
     */
    public ActionForward execute(ActionMapping mapping_, ActionForm form_,
        HttpServletRequest request_, HttpServletResponse response_)
    {
        ExampleProcessRecipientForm form = (ExampleProcessRecipientForm) form_;
        
        try
        {
            response_.setStatus(HttpURLConnection.HTTP_OK);
            PrintWriter out = response_.getWriter();
            String url = form.getAlertreport();
            if (url.equals("http://identity"))
            {
                MessageResources msgs = (MessageResources) request_.getSession().getServletContext().getAttribute(Globals.MESSAGES_KEY);
                String version = msgs.getMessage("Appl.version");
                version = version.replace("&nbsp;", " ").trim();

                // Identify who we are.
                String html = "<html><body>\n";
                html += "<p>Example Process Recipient</p>\n"; 
                html += "<p>caDSR Sentinel Tool</p>\n"; 
                html += "<p>Version: " + version + "</p>\n"; 
                html += "<p>Author: NCICB</p>\n"; 
                html += "<p>Contact: Denise Warzel<br>\n";
                html += "Associate Director, NCICB, NCI Center for Biomedical Informatics and Information Technology<br>\n";
                html += "caCORE Product Line<br>\n";
                html += "caDSR Product Suite<br>\n";
                html += "<a href=\"mailto:warzeld@mail.nih.gov?subject=caDSR%20Sentinel%20Tool%20Process%20Recipients\">warzeld@mail.nih.gov</a></p>\n<br>\n";
                html += "303-722-9446 (o)<br>\n";
                html += "240-205-5445 (bb)<br>\n";
                html += "303-777-1419 (f)<br>\n";
                html += "<a href=\"http://ncicb.nci.nih.gov/infrastructure/cacore_overview\" target=\"_blank\">http://ncicb.nci.nih.gov/infrastructure/cacore_overview</a></p>\n";
                html += "</body></html>"; 
                out.println(html);
            }
            else
            {
                // Remember the report URL and schedule for processing.
                out.println("<html><body><a href=\"" + url + "\">" + url + "</a></body></html>");
            }
            
            // All additional process happens later. We must reply quickly to satisfy the Alert Report Notification per
            // published design standards for this feature.
            out.close();
        }
        catch (IOException ex)
        {
            _logger.error(ex.toString(), ex);
        }
        
        return null;
    }
}
