// Copyright (c) 2004 ScenPro, Inc.

package com.scenpro.DSRAlert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Process the edit.jsp page.
 * 
 * @author Larry Hebel
 */

public class Edit extends Action
{
    /**
     * Constructor.
     */
    public Edit()
    {
    }

    /**
     * Action process to Edit an Alert Definition.
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
        EditForm form = (EditForm) form_;
        AlertBean ub = (AlertBean) request_.getSession().getAttribute(
            AlertBean._SESSIONNAME);
        ub.setRunPrev(Constants._ACTEDIT);

        // If we are going back this will effectively cancel any edit operation.
        if (form.getNextScreen().equals(Constants._ACTBACK))
        {
            form.setNextScreen(ub.getEditPrev());
        }

        // The user requests a Run.
        else if (form.getNextScreen().equals(Constants._ACTRUN))
        {
            // Get the data from the form.
            AlertRec rec = readForm(ub, form);
            ub.setWorking(rec);
        }

        // We must save the edits made.
        else if (form.getNextScreen().equals(Constants._ACTSAVE))
        {
            // We will return to the edit screen to display the message box.
            form.setNextScreen(Constants._ACTEDIT);

            // Get the data from the form.
            AlertRec rec = readForm(ub, form);

            // Connect to the database.
            DBAlert db = new DBAlert();
            if (db.open(request_, ub.getUser(), ub.getPswd()) == 0)
            {
                // If we started with a database record this is an update
                // operation.
                if (rec.getAlertRecNum() != null)
                {
                    // This should always work but you never know.
                    if (db.updateAlert(rec) == 0)
                    {
                        request_.setAttribute(Constants._ACTSAVE, "Y");
                        form.setNextScreen(Constants._ACTLIST);
                    }
                    else
                        request_.setAttribute(Constants._ACTSAVE, db
                            .getErrorMsg(true));
                }

                // This is a new alert definition.
                else
                {
                    // Of course it should work.
                    if (db.insertAlert(rec) == 0)
                    {
                        request_.setAttribute(Constants._ACTSAVE, "Y");
                        form.setNextScreen(Constants._ACTLIST);
                    }
                    else
                        request_.setAttribute(Constants._ACTSAVE, db
                            .getErrorMsg(true));
                }
            }
            else
            {
                // We couldn't connect.
                request_.setAttribute(Constants._ACTSAVE, db.getErrorMsg(true));
            }

            // Always close the connection.
            db.close();
            ub.setWorking(rec);
        }

        // On to the next page.
        return mapping_.findForward(form.getNextScreen());
    }

    /**
     * Move the data from the form into the standard AlertRec object.
     * 
     * @param ub_
     *        The session bean.
     * @param form_
     *        The edit form from the request.
     * @return An AlertRec object.
     */
    private AlertRec readForm(AlertBean ub_, EditForm form_)
    {
        AlertRec rec = new AlertRec(ub_.getWorking());

        rec.setName(form_.getPropName());
        rec.setSummary(form_.getPropDesc(), true);
        rec.setFreq(form_.getFreqUnit(), form_.getFreqWeekly(), form_
            .getFreqMonthly());
        rec.setActive(form_.getPropStatus(), form_.getPropBeginDate(), form_
            .getPropEndDate());
        rec.setInactiveReason(form_.getPropStatusReason());
        rec.setInfoVerNum(form_.getInfoVerNum());
        rec.setActVerNum(form_.getActVerNum());
        rec.setIntro(form_.getPropIntro(), true);
        rec.setIncPropSect(form_.getRepIncProp());
        rec.setReportStyle(form_.getRepStyle());
        rec.setReportEmpty(form_.getFreqEmpty());
        rec.setReportAck(form_.getFreqAck());
        rec.setVDTE(form_.getInfoVDTE());
        rec.setVDTN(form_.getInfoVDTN());
        rec.setIUse(form_.getInfoContextUse());
        rec.setAUse(form_.getActContextUse());
        rec.setTerm(form_.getInfoSearchTerm());
        rec.setIVersion(form_.getInfoVersion());
        rec.setAVersion(form_.getActVersion());
        rec.setRelated(form_.getActDependantChg());
        rec.setAdminChg(form_.getActAdminChg());
        rec.setAdminCopy(form_.getActAdminCopy());
        rec.setAdminNew(form_.getActAdminNew());
        rec.setAdminDel(form_.getActAdminDel());
        rec.setAVDT(form_.getActVDT());
        rec.setRecipients(form_.getPropRecipients());
        rec.setAttrs(form_.getRepAttributes());
        rec.setSearchIn(form_.getInfoSearchIn());
        rec.setAWorkflow(form_.getActWorkflowStatus());
        rec.setIWorkflow(form_.getInfoWorkflowStatus());
        rec.setARegis(form_.getActRegStatus());
        rec.setIRegis(form_.getInfoRegStatus());
        rec.setCreators(form_.getInfoCreator());
        rec.setModifiers(form_.getInfoModifier());
        rec.setSearchAC(form_.getInfoSearchFor());
        rec.setSchemes(form_.getInfoSchemes());
        rec.setSchemeItems(form_.getInfoSchemeItems());
        rec.setDomains(form_.getInfoConceptDomain());
        rec.setContexts(form_.getInfoContext());
        rec.setForms(form_.getInfoForms());

        return rec;
    }
}