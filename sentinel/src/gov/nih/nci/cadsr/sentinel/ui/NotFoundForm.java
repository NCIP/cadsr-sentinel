// Copyright (c) 2008 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/NotFoundForm.java,v 1.1 2008-07-02 15:55:11 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author lhebel
 *
 */
public class NotFoundForm extends ActionForm
{
    private static final long serialVersionUID = 8002819345083108755L;

    /**
     * 
     */
    public NotFoundForm()
    {
        super();
    }

    /**
     * Validate the Login action
     * 
     * @param mapping_
     *        The action map defined in struts-config.xml.
     * @param request_
     *        The HTTP request object.
     * @return Any errors that occur.
     */
    public ActionErrors validate(ActionMapping mapping_,
        HttpServletRequest request_)
    {
        return null;
    }
}
