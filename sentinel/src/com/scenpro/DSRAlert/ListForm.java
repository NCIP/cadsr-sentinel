// Copyright (c) 2004 ScenPro, Inc.

package com.scenpro.DSRAlert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * The struts form bean for list.jsp.
 * 
 * @author Larry Hebel
 */

public class ListForm extends ActionForm
{
    /**
     * Constructor.
     */
    public ListForm()
    {
        _nextScreen = Constants._ACTLIST;
        _listShow = String.valueOf(AlertBean._SHOWPRIV);
    }

    /**
     * Set the next action/screen to transfer control.
     * 
     * @param nextScreen_
     *        The action/screen name.
     */
    public void setNextScreen(String nextScreen_)
    {
        _nextScreen = nextScreen_;
    }

    /**
     * Get the next action/screen name.
     * 
     * @return The action/screen name.
     */
    public String getNextScreen()
    {
        return _nextScreen;
    }

    /**
     * Set the Show flag for the List content.
     * 
     * @param listShow_
     *        The show flag.
     */
    public void setListShow(String listShow_)
    {
        _listShow = listShow_;
    }

    /**
     * Get the Show flag.
     * 
     * @return The show flag.
     */
    public String getListShow()
    {
        return _listShow;
    }

    /**
     * Validate the List form.
     * 
     * @param ActionMapping
     *        mapping_; The action map from struts-config.xml.
     * @param HttpServletRequest
     *        request_; The servlet request object.
     * @return ActionErrors when an error is found, otherwise null.
     */
    public ActionErrors validate(ActionMapping mapping_,
        HttpServletRequest request_)
    {
        ActionErrors errors = new ActionErrors();

        HttpSession session = request_.getSession();
        AlertBean ub = (AlertBean) session.getAttribute(AlertBean._SESSIONNAME);
        if (ub == null)
        {
            errors.add("bean", new ActionMessage("error.nobean"));
            return errors;
        }

        // Show the appropriate list.
        ub.setListShow(_listShow);

        return errors;
    }

    // Class data elements.
    private String _listShow;

    private String _nextScreen;
}