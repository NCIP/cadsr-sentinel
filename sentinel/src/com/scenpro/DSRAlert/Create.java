// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/com/scenpro/DSRAlert/Create.java,v 1.4 2006-01-06 16:14:26 hebell Exp $
// $Name: not supported by cvs2svn $

package com.scenpro.DSRAlert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Create a new Alert.
 * 
 * @author Larry Hebel
 */

public class Create extends Action
{
    /**
     * Constructor.
     */
    public Create()
    {
    }

    /**
     * Action process to Create an Alert.
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
        // Get the bean and other stuff.
        CreateForm form = (CreateForm) form_;
        AlertBean ub = (AlertBean) request_.getSession().getAttribute(
            AlertBean._SESSIONNAME);

        // If edit is used, we want to return to creat on a "back" operation.
        ub.setEditPrev(Constants._ACTCREATE);

        // Make the working alert definition.
        if (ub.getWorking() == null)
        {
            ub.setWorking(new AlertRec(ub.getUser(), ub.getUserName()));
        }

        switch (form.getInitial().charAt(0))
        {
            // Set the alert defaults to be all contexts the user can write to.
            case '1':
                DBAlert db = new DBAlert();
                db.open(request_, ub.getUser(), ub.getPswd());
                String temp[] = db.selectContexts(ub.getUser());
                db.close();
                ub.getWorking().setContexts(temp);
                break;

            // Set the alert defaults to be all things created by the user.
            case '6':
                String user[] = new String[1];
                user[0] = ub.getUser();
                ub.getWorking().setCreators(user);
                break;
        }

        // Set the alert name to whatever the user entered.
        ub.getWorking().setName(form.getPropName());

        // When going to edit, we can't query the databse for information as the
        // user hasn't
        // saved anything yet.
        if (form.getNextScreen().equals(Constants._ACTEDIT))
        {
            ub.getWorking().setSummary(form.getPropDesc(), true);
        }

        // The user doesn't want to edit, just save with current settings.
        else if (form.getNextScreen().equals(Constants._ACTCREATE))
        {
            // Save the new alert.
            DBAlert db = new DBAlert();
            if (db.open(request_, ub.getUser(), ub.getPswd()) == 0)
            {
                if (db.insertAlert(ub.getWorking()) == 0)
                {
                    request_.setAttribute(Constants._ACTSAVE, "Y");
                    form.setNextScreen(Constants._ACTLIST);
                }
                else
                    request_
                        .setAttribute(Constants._ACTSAVE, db.getErrorMsg(true));
            }
            else
                request_.setAttribute(Constants._ACTSAVE, db.getErrorMsg(true));
            db.close();
        }

        // Transfer to the next screen.
        return mapping_.findForward(form.getNextScreen());
    }
}