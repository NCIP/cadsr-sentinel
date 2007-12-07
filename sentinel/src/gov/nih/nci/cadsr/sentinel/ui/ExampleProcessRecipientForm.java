// Copyright (c) 2007 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/ExampleProcessRecipientForm.java,v 1.2 2007-12-07 22:15:51 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * This class provides an example of a Process Recipient. Also see the ExampleProcessRecipient.java class.
 * 
 * @author lhebel
 *
 */
public class ExampleProcessRecipientForm extends ActionForm
{
    /**
     * 
     */
    private static final long serialVersionUID = 6923704102939647439L;
    private String _alertReportUrl;

    /**
     * Constructor
     */
    public ExampleProcessRecipientForm()
    {
        super();
    }

    /**
     * Get the URL
     * 
     * @return the URL
     */
    public String getAlertreport()
    {
        return _alertReportUrl;
    }
    
    /**
     * Set the URL
     * 
     * @param value_
     */
    public void setAlertreport(String value_)
    {
        _alertReportUrl = value_;
    }

    /**
     * Validate the List form.
     * 
     * @param mapping_ The action map from struts-config.xml.
     * @param request_ The servlet request object.
     * @return ActionErrors when an error is found, otherwise null.
     */
    public ActionErrors validate(ActionMapping mapping_,
        HttpServletRequest request_)
    {
        ActionErrors errors = new ActionErrors();
        return errors;
    }
}
