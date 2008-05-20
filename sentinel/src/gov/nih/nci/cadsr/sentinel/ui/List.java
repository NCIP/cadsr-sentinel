// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/List.java,v 1.4 2008-05-20 21:41:20 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

import gov.nih.nci.cadsr.sentinel.database.DBAlert;
import gov.nih.nci.cadsr.sentinel.database.DBAlertUtil;
import gov.nih.nci.cadsr.sentinel.tool.AlertRec;
import gov.nih.nci.cadsr.sentinel.tool.Constants;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.struts.Globals;

/**
 * Manage the alert list.
 * 
 * @author Larry Hebel
 */

public class List extends Action
{
    private static final Logger _logger = Logger.getLogger(List.class);

    /**
     * Constructor.
     */
    public List()
    {
    }

    /**
     * Action process for a List of Alert Definitions.
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
        // Get the bean and stuff.
        ListForm form = (ListForm) form_;
        AlertBean ub = (AlertBean) request_.getSession().getAttribute(
            AlertBean._SESSIONNAME);

        // If going to edit we want to return to the list on a "back" operation.
        ub.setEditPrev(Constants._ACTLIST);
        ub.setRunPrev(Constants._ACTLIST);

        // Delete all selected definitions.
        int count = 0;
        try
        {
            count = Integer.parseInt(request_.getParameter("rowCount"));
        }
        catch (Exception ex)
        {
            _logger.error("List page rowCount has been compromised.", ex);
        }
        if (form.getNextScreen().equals(Constants._ACTDELETE))
        {
            form.setNextScreen(Constants._ACTLIST);
            Vector<String> dlist = new Vector<String>();
            for (int ndx = 0; ndx < count; ++ndx)
            {
                String temp = request_.getParameter("cb" + Integer.toString(ndx)); 
                if (temp != null)
                {
                    dlist.add(temp);
                }
            }
            if (dlist.size() > 0)
            {
                DBAlert db = DBAlertUtil.factory();
                db.open(request_, ub.getUser());
                db.deleteAlerts(dlist);
                db.close();
            }
        }

        // Create a new definition. Be sure we don't inherit any unexpected
        // values from previous working
        // operations.
        else if (form.getNextScreen().equals(Constants._ACTCREATE))
        {
            ub.setWorking(null);
        }

        // Process a specific alert.
        else if (!form.getNextScreen().equals(Constants._ACTLIST))
        {
            for (int ndx = 0; ndx < count; ++ndx)
            {
                // Look for the first check box flagged by the user.
                String idseq = request_.getParameter("cb" + Integer.toString(ndx));
                if (idseq != null)
                {
                    // The user wants to edit the definition.
                    if (form.getNextScreen().equals(Constants._ACTEDIT)
                        || form.getNextScreen().equals(Constants._ACTRUN))
                    {
                        DBAlert db = DBAlertUtil.factory();
                        db.open(request_, ub.getUser());
                        ub.setWorking(db.selectAlert(idseq));
                        db.close();
                    }

                    // The user wants to create a new definition using an
                    // existing one as a template.
                    else if (form.getNextScreen().equals(Constants._ACTNEWFROM))
                    {
                        DBAlert db = DBAlertUtil.factory();
                        db.open(request_, ub.getUser());
                        AlertRec temp = db.selectAlert(idseq);
                        db.close();

                        // Reset the record numbers to ensure an Insert is done
                        // when appropriate.
                        temp.setRecNumNull();

                        // Fix the name and creator of this new alert.
                        MessageResources msgs = (MessageResources) request_
                            .getSession().getServletContext().getAttribute(
                                Globals.MESSAGES_KEY);
                        temp.setCreator(ub.getUser());
                        temp.setCreatorName(ub.getUserName());
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