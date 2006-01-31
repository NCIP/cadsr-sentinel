// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tool/Logon.java,v 1.2 2006-01-31 18:52:08 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Process the logon.jsp.
 * 
 * @author Larry Hebel
 */

public class Logon extends Action
{
    /**
     * Constructor.
     */
    public Logon()
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
        // Create the bean for all our processing and present the Alert List.
        LogonForm form = (LogonForm) form_;
        createBean(request_, form.getUserid(), form.getUserName(), form.getPswd());

        return mapping_.findForward(Constants._ACTLIST);
    }
    
    /**
     * Create the Alert Bean for the user session.
     * 
     * @param request_ The HTTP request.
     * @param user_ The user id.
     * @param name_ The user name.
     * @param pswd_ The user password.
     * @return The session specific bean.
     */
    public static AlertBean createBean(HttpServletRequest request_, String user_, String name_, String pswd_)
    {
        AlertBean ub = new AlertBean(user_, name_, pswd_);

        HttpSession session = request_.getSession();
        session.setAttribute(AlertBean._SESSIONNAME, ub);
        
        return ub;
    }
}