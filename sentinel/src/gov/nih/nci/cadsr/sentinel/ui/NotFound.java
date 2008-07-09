// Copyright (c) 2008 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/NotFound.java,v 1.1 2008-07-02 15:55:11 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

import gov.nih.nci.cadsr.sentinel.tool.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author lhebel
 *
 */
public class NotFound extends Action
{

    /**
     * 
     */
    public NotFound()
    {
        super();
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
        response_.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return new ActionForward(mapping_.findForward("notfound"));
    }
}
