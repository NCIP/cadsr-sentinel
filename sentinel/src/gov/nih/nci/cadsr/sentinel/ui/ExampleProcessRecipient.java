// Copyright (c) 2007 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/ExampleProcessRecipient.java,v 1.1 2007-12-07 21:52:53 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

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
            String url = form.getUrl();
            out.println("<html><body><a href=\"" + url + "\">" + url + "</a></body></html>");
            out.close();
        }
        catch (IOException ex)
        {
            _logger.error(ex.toString(), ex);
        }
        
        return null;
    }
}
