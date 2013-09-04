/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/CreateForm.java,v 1.3 2008-05-20 21:41:20 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

import gov.nih.nci.cadsr.sentinel.tool.Constants;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

/**
 * The struts form bean for the create.jsp page.
 * 
 * @author Larry Hebel
 */

public class CreateForm extends AlertRootForm
{

    // Class data elements.
    private String _propName;

    private String _initial;

    private String _propDesc;

    private static final long serialVersionUID = -6363464073035486126L;

    /**
     * Constructor
     */
    public CreateForm()
    {
        super();
        _propName = "New Alert";
        _initial = "1";
        _nextScreen = Constants._ACTCREATE;
    }

    /**
     * Set the next action/screen to which to transfer.
     * 
     * @param nextScreen_
     *        The action/screen name.
     */
    public void setNextScreen(String nextScreen_)
    {
        _nextScreen = nextScreen_;
    }

    /**
     * Get the next action/screen.
     * 
     * @return The action/screen name.
     */
    public String getNextScreen()
    {
        return _nextScreen;
    }

    /**
     * Set the Sentinel Name.
     * 
     * @param propName_
     *        The name text.
     */
    public void setPropName(String propName_)
    {
        _propName = propName_;
    }

    /**
     * Get the Sentinel Name.
     * 
     * @return The name text.
     */
    public String getPropName()
    {
        return _propName;
    }

    /**
     * Set the initial selection of the create options.
     * 
     * @param initial_
     *        The create option selection.
     */
    public void setInitial(String initial_)
    {
        _initial = initial_;
    }

    /**
     * Get the initial create options selection.
     * 
     * @return The create option selection.
     */
    public String getInitial()
    {
        return _initial;
    }

    /**
     * Get the default Summary for the create option selection.
     * 
     * @return The summary description text.
     */
    public String getPropDesc()
    {
        return _propDesc;
    }

    /**
     * Set the default Summary for the create option selection.
     * 
     * @param desc_
     *        The summary description text.
     */
    public void setPropDesc(String desc_)
    {
        _propDesc = desc_;
    }

    /**
     * Validate the state of the page.
     * 
     * @param mapping_ The action mapping from the struts-config.xml.
     * @param request_ The servlet request object.
     * @param ub_ the user AlertBean
     * @param errors_ the current errors list
     * @return ActionErrors if something isn't quite right.
     */
    public ActionErrors validate(ActionMapping mapping_,
        HttpServletRequest request_, AlertBean ub_, ActionErrors errors_)
    {
        // If we are not going back to the list page, be sure we have defaults.
        if (!_nextScreen.equals(Constants._ACTLIST))
        {
            if (_propName == null || _propName.length() == 0)
                ub_.getWorking().setName("New Sentinel");
            else
                ub_.getWorking().setName(_propName);
            ub_.getWorking().setSummary(_propDesc);
        }

        return errors_;
    }
}