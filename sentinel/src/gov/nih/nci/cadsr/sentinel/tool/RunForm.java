// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tool/RunForm.java,v 1.7 2006-02-20 20:59:33 hardingr Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * Validate the run.jsp form.
 * 
 * @author Larry Hebel
 */

public class RunForm extends ActionForm
{
    /**
     * Constructor.
     */
    public RunForm()
    {
        _recipients = String.valueOf(_RECIPIENTS);
        _nextScreen = Constants._ACTRUN;
    }

    /**
     * Get the Recipients flag.
     * 
     * @return The recipients flag.
     */
    public String getRecipients()
    {
        return _recipients;
    }

    /**
     * Set the Recipients flag, default is creator only.
     * 
     * @param val_
     *        The recipient flag.
     */
    public void setRecipients(String val_)
    {
        _recipients = val_;
    }

    /**
     * Get the report range start date.
     * 
     * @return The date.
     */
    public String getStartDate()
    {
        return _start;
    }

    /**
     * Set the report range start date.
     * 
     * @param val_
     *        The date.
     */
    public void setStartDate(String val_)
    {
        _start = val_;
    }

    /**
     * Get the report range end date.
     * 
     * @return The date.
     */
    public String getEndDate()
    {
        return _end;
    }

    /**
     * Set the report range end date.
     * 
     * @param val_
     *        The date.
     */
    public void setEndDate(String val_)
    {
        _end = val_;
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
     * Set the next action/screen name to transfer control to.
     * 
     * @param val_
     *        The action/screen name.
     */
    public void setNextScreen(String val_)
    {
        _nextScreen = val_;
    }

    /**
     * Validate the page contents.
     * 
     * @param mapping_
     *        The action mapping from the struts-config.xml.
     * @param request_
     *        The servlet request object.
     * @return ActionErrors if something isn't quite right.
     */
    public ActionErrors validate(ActionMapping mapping_,
        HttpServletRequest request_)
    {
        ActionErrors errors = new ActionErrors();

        HttpSession session = request_.getSession();
        AlertBean ub = (AlertBean) session.getAttribute(AlertBean._SESSIONNAME);
        if (ub == null)
        {
            // Must start at logon.
            errors.add("bean", new ActionMessage("error.nobean"));
            return errors;
        }

        // If the user leaves out a date we will use the most appropriate
        // default.
        if (_start == null || _start.length() == 0)
            _start = ub.getWorking().getADate();

        return errors;
    }

    // Class data.
    private String _recipients;

    private String _start;

    private String _end;

    private String _nextScreen;
    
    /**
     * The recipients option value to send the report to the Creator only. 
     */
    public static final char _RECIPIENTS = 'C';

    private static final long serialVersionUID = 6780909133861681552L;
}