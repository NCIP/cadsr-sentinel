// Copyright (c) 2004 ScenPro, Inc.

package com.scenpro.DSRAlert;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

/**
 * Manage the alert list.
 * 
 * @author Larry Hebel
 */

public class List extends Action
{
    /**
     * Constructor.
     */
    public List()
    {
    }

    /**
     * Action process for a List of Alert Definitions.
     * 
     * @param ActionMapping
     *        mapping_; The struts action mapping defined for this action in the
     *        struts-config.xml file.
     * @param ActionForm
     *        form_; The struts form defined in the struts-config.xml file.
     * @param HttpServletRequest
     *        request_; The servlet request object.
     * @param HttpServletResponse
     *        response_; The servlet response object.
     */
    public ActionForward execute(ActionMapping mapping_, ActionForm form_,
        HttpServletRequest request_, HttpServletResponse response_)
    {
        // Get the bean and stuff.
        ListForm form = (ListForm) form_;
        AlertBean ub = (AlertBean) request_.getSession().getAttribute(
            AlertBean._SESSIONNAME);

        // If going to edit we want to return to the list on a "back" operation.
        ub.setEditPrev(Constants._LIST);
        ub.setRunPrev(Constants._LIST);

        // Delete all selected definitions.
        int count = Integer.parseInt(request_.getParameter("rowCount"));
        if (form.getNextScreen().equals(Constants._DELETE))
        {
            form.setNextScreen(Constants._LIST);
            Vector dlist = new Vector();
            for (int ndx = 0; ndx < count; ++ndx)
            {
                if (request_.getParameter("cb" + Integer.toString(ndx)) != null)
                {
                    dlist.add(ub.getDBlist(ndx));
                }
            }
            if (dlist.size() > 0)
            {
                DBAlert db = new DBAlert();
                db.open(request_, ub.getUser(), ub.getPswd());
                db.deleteAlerts(dlist);
                db.close();
            }
        }

        // Create a new definition. Be sure we don't inherit any unexpected
        // values from previous working
        // operations.
        else if (form.getNextScreen().equals(Constants._CREATE))
        {
            ub.setWorking(null);
        }

        // Process a specific alert.
        else
        {
            for (int ndx = 0; ndx < count; ++ndx)
            {
                // Look for the first check box flagged by the user.
                if (request_.getParameter("cb" + Integer.toString(ndx)) != null)
                {
                    // The user wants to edit the definition.
                    if (form.getNextScreen().equals(Constants._EDIT)
                        || form.getNextScreen().equals(Constants._RUN))
                    {
                        DBAlert db = new DBAlert();
                        db.open(request_, ub.getUser(), ub.getPswd());
                        ub.setWorking(db.selectAlert(ub.getDBlist(ndx)));
                        db.close();
                    }

                    // The user wants to create a new definition using an
                    // existing one as a template.
                    else if (form.getNextScreen().equals(Constants._NEWFROM))
                    {
                        DBAlert db = new DBAlert();
                        db.open(request_, ub.getUser(), ub.getPswd());
                        AlertRec temp = db.selectAlert(ub.getDBlist(ndx));
                        db.close();

                        // Reset the record numbers to ensure an Insert is done
                        // when appropriate.
                        temp.setRecNumNull();

                        // Fix the name and creator of this new alert.
                        MessageResources msgs = (MessageResources) request_
                            .getSession().getServletContext().getAttribute(
                                Constants._RESOURCES);
                        temp.setCreator(ub.getUser());
                        temp.setName(msgs.getMessage("all.createusing") + " "
                            + temp.getName());
                        ub.setWorking(temp);
                    }
                    break;
                }
            }
        }

        // Pass to the next appropriate screen.
        return mapping_.findForward(form.getNextScreen());
    }
}