// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/ListForm.java,v 1.3 2008-05-20 21:41:20 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

import gov.nih.nci.cadsr.sentinel.tool.Constants;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

/**
 * The struts form bean for list.jsp.
 * 
 * @author Larry Hebel
 */

public class ListForm extends AlertRootForm
{
    /**
     * Constructor.
     */
    public ListForm()
    {
        super();
        _nextScreen = Constants._ACTLIST;
        _listShow = String.valueOf(AlertBean._SHOWPRIV);
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
     * @param mapping_ The action map from struts-config.xml.
     * @param request_ The servlet request object.
     * @param ub_ the user AlertBean
     * @param errors_ the current errors list
     * @return ActionErrors when an error is found, otherwise null.
     */
    public ActionErrors validate(ActionMapping mapping_,
        HttpServletRequest request_, AlertBean ub_, ActionErrors errors_)
    {
        // Show the appropriate list.
        ub_.setListShow(_listShow);

        return errors_;
    }

    // Class data elements.
    private String _listShow;

    private static final long serialVersionUID = -7463737781075371910L;
}