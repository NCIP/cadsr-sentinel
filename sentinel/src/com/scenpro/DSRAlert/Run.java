// Copyright (c) 2004 ScenPro, Inc.

package com.scenpro.DSRAlert;

import java.sql.Timestamp;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Process the run.jsp.
 * 
 * @author Larry Hebel
 */

public class Run extends Action
{
    /**
     * Constructor.
     */
    public Run()
    {
    }

    /**
     * Process action to manually submit a Run of the working Alert.
     * 
     * @param mapping_
     *        The action map from the struts-config.xml.
     * @param form_
     *        The form bean for the edit.jsp page.
     * @param request_
     *        The servlet request object.
     * @param response_
     *        The servlet response object.
     */
    public ActionForward execute(ActionMapping mapping_, ActionForm form_,
        HttpServletRequest request_, HttpServletResponse response_)
    {
        // Get data.
        RunForm form = (RunForm) form_;
        AlertBean ub = (AlertBean) request_.getSession().getAttribute(
            AlertBean._SESSIONNAME);

        if (form.getNextScreen().equals(Constants._ACTBACK))
        {
            // Never mind...
            form.setNextScreen(ub.getRunPrev());
        }
        else if (form.getNextScreen().equals(Constants._ACTRUN))
        {
            // Setup to submit the Alert.
            AutoProcessAlerts pa = new AutoProcessAlerts();
            Timestamp start = AlertRec.parseDate(form.getStartDate());
            Timestamp end = AlertRec.parseDate(form.getEndDate());
            AlertRec rec = new AlertRec(ub.getWorking());
            if (form.getRecipients().charAt(0) == RunForm._RECIPIENTS)
            {
                // Only send to the Creator.
                String temp[] = new String[1];
                temp[0] = rec.getCreator();
                rec.setRecipients(temp);
            }
            // Always send a report even when empty - this is a manual run.
            rec.setReportEmpty('Y');

            // Run the alert and report.
            pa.manualRun(rec, start, end);

            // Tell the user we have submitted the Alert.
            request_.setAttribute(Constants._ACTRUN, ub.getWorking().getName());
            form.setNextScreen(ub.getRunPrev());
        }

        // Return to the screen that opened the Run screen.
        return mapping_.findForward(form.getNextScreen());
    }
}