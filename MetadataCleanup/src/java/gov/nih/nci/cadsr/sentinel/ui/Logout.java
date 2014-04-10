/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/Logout.java,v 1.2 2007-07-19 15:26:45 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

import gov.nih.nci.cadsr.sentinel.tool.Constants;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Process the Logout.
 * 
 * @author Larry Hebel
 */

public class Logout extends Action
{
    /**
     * Constructor.
     */
    public Logout()
    {
    }

    /**
     * Action process for a Logout.
     * 
     * @param mapping_
     *        The action map.
     * @param form_
     *        The action form.
     * @param request_
     *        The HTTP request.
     * @param response_
     *        The response.
     * @return The next action/screen to transfer to.
     */
    public ActionForward execute(ActionMapping mapping_, ActionForm form_,
        HttpServletRequest request_, HttpServletResponse response_)
    {
        // Clean up session memory.
        HttpSession session = request_.getSession();
        session.removeAttribute(AlertBean._SESSIONNAME);

        // Have to logon.
        return mapping_.findForward(Constants._ACTLOGON);
    }
}