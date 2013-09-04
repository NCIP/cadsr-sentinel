/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/RunForm.java,v 1.3 2008-05-20 21:41:20 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

import gov.nih.nci.cadsr.sentinel.tool.Constants;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

/**
 * Validate the run.jsp form.
 * 
 * @author Larry Hebel
 */

public class RunForm extends AlertRootForm
{
    /**
     * Constructor.
     */
    public RunForm()
    {
        super();
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
     * Validate the page contents.
     * 
     * @param mapping_
     *        The action mapping from the struts-config.xml.
     * @param request_
     *        The servlet request object.
     * @param ub_ the user AlertBean
     * @param errors_ the current errors list
     * @return ActionErrors if something isn't quite right.
     */
    public ActionErrors validate(ActionMapping mapping_,
        HttpServletRequest request_, AlertBean ub_, ActionErrors errors_)
    {
        // If the user leaves out a date we will use the most appropriate
        // default.
        if (_start == null || _start.length() == 0)
            _start = ub_.getWorking().getADate();

        return errors_;
    }

    // Class data.
    private String _recipients;

    private String _start;

    private String _end;
    
    /**
     * The recipients option value to send the report to the Creator only. 
     */
    public static final char _RECIPIENTS = 'C';

    private static final long serialVersionUID = 6780909133861681552L;
}