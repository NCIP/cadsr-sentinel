// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tool/CDECuration.java,v 1.1.2.1 2006-01-31 18:33:45 hebell Exp $
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
 * Provide an interface for the CDECuration Tool.
 * 
 * @author Larry Hebel
 */

public class CDECuration extends Action
{
    /**
     * Constructor.
     */
    public CDECuration()
    {
    }

    /**
     * Action interface to launch the Sentinel Tool from CDE Curation.
     * 
     * @param mapping_
     *        The action map from the struts-config.xml.
     * @param form_
     *        The form bean for the edit.jsp page.
     * @param request_
     *        The servlet request object.
     * @param response_
     *        The servlet response object.
     * @return The action to continue processing.
     */
    public ActionForward execute(ActionMapping mapping_, ActionForm form_,
        HttpServletRequest request_, HttpServletResponse response_)
    {
        // Retrieve the User ID and Password from the Curation Tool Session and
        // pass it on to the Logon window.
        HttpSession session = request_.getSession();
        UserBean ub = (UserBean) session.getAttribute("Userbean");
        ActionForward af;
        if (ub == null)
        {
            af = new ActionForward("/do/logon");
        }
        else
        {
            String userid = ub.getUsername();
            String pswd = ub.getPassword();
            af = new ActionForward("/do/logon?userid=" + userid
                + "&pswd=" + pswd);
        }
        return af;
    }
}