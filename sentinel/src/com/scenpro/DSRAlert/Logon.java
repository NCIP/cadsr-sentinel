// Copyright (c) 2004 ScenPro, Inc.

package com.scenpro.DSRAlert;

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
        // Creat the bean for all our processing and present the Alert List.
        LogonForm form = (LogonForm) form_;
        AlertBean ub = new AlertBean(form.getUserid(), form.getUserName(), form
            .getPswd());

        HttpSession session = request_.getSession();
        session.setAttribute(AlertBean._SESSIONNAME, ub);

        return mapping_.findForward(Constants._LIST);
    }
}