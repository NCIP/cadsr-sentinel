// Copyright (c) 2004 ScenPro, Inc.

package com.scenpro.DSRAlert;

import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.struts.util.MessageResources;

/**
 * The JSP tags for the edit.jsp page.
 * 
 * @author Larry Hebel
 */

public class EditTag extends TagSupport
{
    /**
     * Constructor
     */
    public EditTag()
    {
    }

    /**
     * Set the section name.
     * 
     * @param section_
     *        Either "init", "script" or "field".
     */
    public void setSection(String section_)
    {
        _section = section_;
    }

    /**
     * Process the tag. It appears in the JSP as &lt;dtags:edit section="script"
     * /&gt;.
     * 
     * @return EVAL_PAGE to continue processing the remaining JSP.
     */
    public int doEndTag()
    {
        // Set the class data.
        HttpSession session = pageContext.getSession();
        _ub = (AlertBean) session.getAttribute(AlertBean._SESSIONNAME);
        _request = pageContext.getRequest();

        // Process the desired section.
        String temp = null;

        try
        {
            JspWriter out = pageContext.getOut();
            if (_section.equals("init"))
            {
                temp = getInit();
            }
            else if (_section.equals("script"))
            {
                temp = getScript();
            }
            else if (_section.equals("field"))
            {
                temp = getField();
            }
            if (temp != null)
                out.print(temp);
        }
        catch (IOException ex)
        {
        }
        return EVAL_PAGE;
    }

    /**
     * Define the special form fields.
     * 
     * @return The html field string.
     */
    private String getField()
    {
        String temp = "<input type=hidden name=sessionKey value=\""
            + _ub.getKey() + "\">\n"
            + "<input type=hidden name=creatorID value=\""
            + _ub.getWorking().getRecipients(0) + "\">\n"
            + "<input type=hidden name=usersTab value=\""
            + _ub.getLastUserTab() + "\">\n"
            + "<input type=hidden name=mainTab value=\"" + _ub.getLastMainTab()
            + "\">\n";
        return temp;
    }

    /**
     * Initialize the class data for the rest of the tags.
     * 
     * @return null as this does not generate any output.
     */
    private String getInit()
    {
        DBAlert db = new DBAlert();
        if (db.open(pageContext.getServletContext(), _ub.getUser(), _ub
            .getPswd()) == 0)
        {
            db.getUsers();
            _namesList = db.getUserList();
            _namesVals = db.getUserVals();
            _namesExempt = db.getUserExempts();
            db.getContexts();
            _contextList = db.getContextList();
            _contextVals = db.getContextVals();
            db.getSchemes();
            _schemeList = db.getSchemeList();
            _schemeVals = db.getSchemeVals();
            _schemeContext = db.getSchemeContext();
            db.getSchemeItems();
            _schemeItemList = db.getSchemeItemList();
            _schemeItemVals = db.getSchemeItemVals();
            _schemeItemSchemes = db.getSchemeItemSchemes();
            db.getForms();
            _formsList = db.getFormsList();
            _formsVals = db.getFormsVals();
            _formsContext = db.getFormsContext();
            db.getGroups();
            _groupsList = db.getGroupList();
            _groupsVals = db.getGroupVals();
            db.getWorkflow();
            _workflowList = db.getWorkflowList();
            _workflowVals = db.getWorkflowVals();
            db.getRegistrations();
            _regstatusList = db.getRegStatusList();
            _regstatusVals = db.getRegStatusVals();
            db.close();
        }

        return null;
    }

    /**
     * Create a concatenated comma separated single string from a String array.
     * 
     * @param list_
     *        The values.
     * @return The Java Script form.
     */
    private String stringList(String list_[])
    {
        String temp = "[";
        if (list_ != null)
        {
            for (int ndx = 0; ndx < list_.length; ++ndx)
            {
                if (ndx > 0)
                    temp = temp + ",\n";
                temp = temp + "\"" + list_[ndx] + "\"";
            }
        }
        return temp + "];\n";
    }

    /**
     * Create a concatenated comma separated single string from an int array.
     * 
     * @param list_
     *        The values.
     * @return The Java Script form.
     */
    private String intList(int list_[])
    {
        String temp = "[";
        if (list_ != null)
        {
            for (int ndx = 0; ndx < list_.length; ++ndx)
            {
                if (ndx > 0)
                    temp = temp + ",\n";
                temp = temp + "\"" + list_[ndx] + "\"";
            }
        }
        return temp + "];\n";
    }

    /**
     * Create a concatenated comma separated single string from a 2 dimensional
     * int array.
     * 
     * @param list_
     *        The values.
     * @return The Java Script form.
     */
    private String intList2(int list_[][])
    {
        String temp = "[";
        if (list_ != null)
        {
            for (int ndx = 0; ndx < list_.length; ++ndx)
            {
                if (ndx > 0)
                    temp = temp + ",\n";
                temp = temp + "[";

                for (int ndx2 = 0; ndx2 < list_[ndx].length; ++ndx2)
                {
                    if (ndx2 > 0)
                        temp = temp + ",";
                    temp = temp + "\"" + list_[ndx][ndx2] + "\"";
                }
                temp = temp + "]";
            }
        }
        return temp + "];\n";
    }

    /**
     * Create the Javascript for selected options in a list with matching
     * values.
     * 
     * @param lname_
     *        The name of the field on the editForm.
     * @param vals_
     *        The array of possible values.
     * @param selected_
     *        The array of actual values to be selected.
     * @param flag_
     *        When true and no matches are found between vals_ and selected_ the
     *        first option in lname_ is set.
     * @return The Java Script reflecting the options list items to select.
     */
    private String selectFromList(String lname_, String vals_[],
        String selected_[], boolean flag_)
    {
        String temp = "";
        for (int ndx2 = 0; ndx2 < selected_.length; ++ndx2)
        {
            for (int ndx = 0; ndx < vals_.length; ++ndx)
            {
                if (vals_[ndx].equals(selected_[ndx2]))
                {
                    temp = temp + "editForm." + lname_ + ".options[" + ndx
                        + "].selected = true;\n";
                    break;
                }
            }
        }
        if (flag_ && temp.length() == 0)
            temp = "editForm." + lname_ + ".options[0].selected = true;\n";
        return temp;
    }

    /**
     * Create the body of the script section.
     * 
     * @return Java Script which reflects the alert being edited.
     */
    private String getScript()
    {
        MessageResources msgs = (MessageResources) pageContext
            .findAttribute(Constants._RESOURCES);

        String temp = "var MstatusReason = false;\n" + "var Mprev = \""
            + _ub.getEditPrev() + "\";\n";

        // Everything is in name/value pairs as a minimum. The name is shown to
        // the user in the
        // browser and the value is sent back to the servlet on a submit.
        temp = temp + "var DBnamesList = ";
        temp = temp + stringList(_namesList);

        temp = temp + "var DBnamesVals = ";
        temp = temp + stringList(_namesVals);

        temp = temp + "var DBcontextList = ";
        temp = temp + stringList(_contextList);

        temp = temp + "var DBcontextVals = ";
        temp = temp + stringList(_contextVals);

        temp = temp + "var DBgroupsList = ";
        temp = temp + stringList(_groupsList);

        temp = temp + "var DBgroupsVals = ";
        temp = temp + stringList(_groupsVals);

        temp = temp + "var DBworkflowList = ";
        temp = temp + stringList(_workflowList);

        temp = temp + "var DBworkflowVals = ";
        temp = temp + stringList(_workflowVals);

        temp = temp + "var DBregStatusList = ";
        temp = temp + stringList(_regstatusList);

        temp = temp + "var DBregStatusVals = ";
        temp = temp + stringList(_regstatusVals);

        temp = temp + "var RecSchemes = ";
        temp = temp + stringList(_ub.getWorking().getSchemes());

        temp = temp + "var RecSchemeItems = ";
        temp = temp + stringList(_ub.getWorking().getSchemeItems());

        temp = temp + "var DBschemeList = ";
        temp = temp + stringList(_schemeList);

        temp = temp + "var DBschemeVals = ";
        temp = temp + stringList(_schemeVals);

        temp = temp + "var DBschemeContexts = ";
        temp = temp + stringList(_schemeContext);

        temp = temp + "var DBschemeItemList = ";
        temp = temp + stringList(_schemeItemList);

        temp = temp + "var DBschemeItemVals = ";
        temp = temp + stringList(_schemeItemVals);

        temp = temp + "var DBschemeItemSchemes = ";
        temp = temp + stringList(_schemeItemSchemes);

        temp = temp + "var RecForms = ";
        temp = temp + stringList(_ub.getWorking().getForms());

        temp = temp + "var DBformsList = ";
        temp = temp + stringList(_formsList);

        temp = temp + "var DBformsVals = ";
        temp = temp + stringList(_formsVals);

        temp = temp + "var DBformsContexts = ";
        temp = temp + stringList(_formsContext);

        temp = temp + "var DBrecipients = ";
        temp = temp + stringList(_ub.getWorking().getRecipients());

        temp = temp + "var DBactVerNum = \"" + _ub.getWorking().getActVerNum()
            + "\";\n";

        // Most of the Javascript for the edit.jsp page is in the edit.js file.
        // The body onloaded function
        // is written to call "loaded0()" to complete the operation for the
        // dynamic part of the script.
        temp = temp + "function loaded0() {\n";

        int ndx;

        // Add the exempt list to the display.
        if (_namesExempt != null)
            temp = temp
                + "exemptlist.innerHTML = \"The following users only receive Alert "
                + "Broadcasts when added as specific Recipients.<br>"
                + _namesExempt + "\";\n";

        // You may wonder why these field values are set here when struts will
        // automatically load the
        // fields from the form bean. Actually, I couldn't find a way to have
        // the form bean access the
        // database on the outbound page.
        temp = temp + "editForm.propName.value = \""
            + _ub.getWorking().getName() + "\";\n";
        temp = temp + "editForm.propDesc.value = \""
            + _ub.getWorking().getSummary(true) + "\";\n";
        temp = temp + "editForm.propCreator.value = \""
            + _ub.getWorking().getCreator() + "\";\n";
        temp = temp + "editForm.propCreateDate.value = \""
            + _ub.getWorking().getCDate() + "\";\n";
        temp = temp + "editForm.propLastRunDate.value = \""
            + _ub.getWorking().getADate() + "\";\n";
        temp = temp + "editForm.propModifyDate.value = \""
            + _ub.getWorking().getMDate() + "\";\n";
        if (_ub.getWorking().getIncPropSect())
            temp = temp + "editForm.repIncProp.checked = true;\n";

        if (_ub.getWorking().getIntro(false) == null)
            temp = temp + "editForm.propIntro.value = \""
                + msgs.getMessage("edit.static") + "\";\n";
        else
            temp = temp + "editForm.propIntro.value = \""
                + _ub.getWorking().getIntro(true) + "\";\n";

        if (_ub.getWorking().isFreqDay())
        {
            temp = temp + "editForm.freqUnit[0].checked = true;\n";
        }
        else if (_ub.getWorking().isFreqWeek())
        {
            ndx = _ub.getWorking().getDay() - 1;
            temp = temp + "editForm.freqUnit[1].checked = true;\n"
                + "editForm.freqWeekly.disabled = false;\n"
                + "editForm.freqWeekly.options[" + ndx + "].selected = true;\n";
        }
        else if (_ub.getWorking().isFreqMonth())
        {
            ndx = _ub.getWorking().getDay() - 1;
            temp = temp + "editForm.freqUnit[2].checked = true;\n"
                + "editForm.freqMonthly.disabled = false;\n"
                + "editForm.freqMonthly.options[" + ndx
                + "].selected = true;\n";
        }
        if (_ub.getWorking().isActive())
        {
            temp = temp + "editForm.propStatus[0].checked = true;\n"
                + "editForm.propStatusReason.value = \""
                + msgs.getMessage("edit.statusi2") + "\";\n";
        }
        else if (_ub.getWorking().isActiveOnce())
        {
            temp = temp + "editForm.propStatus[1].checked = true;\n"
                + "editForm.propStatusReason.value = \""
                + msgs.getMessage("edit.statusi2") + "\";\n";
        }
        else if (_ub.getWorking().isActiveDates())
        {
            temp = temp + "editForm.propStatus[2].checked = true;\n"
                + "editForm.propStatusReason.value = \""
                + msgs.getMessage("edit.statusi2") + "\";\n";
        }
        else
        {
            temp = temp + "editForm.propStatus[3].checked = true;\n"
                + "editForm.propStatusReason.value = \"";
            if (_ub.getWorking().getInactiveReason(false) == null)
            {
                temp = temp + msgs.getMessage("edit.explain") + "\";\n";
            }
            else
            {
                temp = temp + _ub.getWorking().getInactiveReason(true)
                    + "\";\n" + "MstatusReason = true;\n";
            }
        }

        if (_ub.getWorking().isSendEmptyReport())
        {
            temp = temp + "editForm.freqEmpty[1].checked = true;\n";
        }
        else
        {
            temp = temp + "editForm.freqEmpty[0].checked = true;\n";
        }

        temp = temp
            + selectFromList("actWorkflowStatus", _workflowVals, _ub
                .getWorking().getAWorkflow(), true);
        temp = temp
            + selectFromList("actRegStatus", _regstatusVals, _ub.getWorking()
                .getARegis(), true);
        temp = temp
            + selectFromList("infoCreator", _namesVals, _ub.getWorking()
                .getCreators(), true);
        temp = temp
            + selectFromList("infoModifier", _namesVals, _ub.getWorking()
                .getModifiers(), true);
        temp = temp
            + selectFromList("infoContext", _contextVals, _ub.getWorking()
                .getContexts(), true);
        temp = temp + "changedContext();\n";
        temp = temp + "setSelected(editForm.infoForms, RecForms);\n";
        temp = temp + "setSelected(editForm.infoSchemes, RecSchemes);\n";
        temp = temp + "changedCS();\n";
        temp = temp
            + "setSelected(editForm.infoSchemeItems, RecSchemeItems);\n";

        switch (_ub.getWorking().getAVersion())
        {
            case AlertRec._VERANYCHG:
                temp = temp + "editForm.actVersion[0].checked = true;\n";
                break;
            case AlertRec._VERMAJCHG:
                temp = temp + "editForm.actVersion[1].checked = true;\n";
                break;
            case AlertRec._VERIGNCHG:
                temp = temp + "editForm.actVersion[2].checked = true;\n";
                break;
            case AlertRec._VERSPECHG:
                temp = temp + "editForm.actVersion[3].checked = true;\n";
                break;
        }

        if (_ub.getWorking().getStart() != null)
        {
            temp = temp + "editForm.propBeginDate.value = \""
                + _ub.getWorking().getSDate() + "\";\n";
        }
        if (_ub.getWorking().getEnd() != null)
        {
            temp = temp + "editForm.propEndDate.value = \""
                + _ub.getWorking().getEDate() + "\";\n";
        }

        String run = (String) pageContext.getRequest().getAttribute(
            Constants._ACTRUN);
        if (run != null)
            temp = temp + "alert(\"Alert named '" + run
                + "' has been submitted.\");\n";

        temp = temp
            + "if (MmainTab == tabMain1)\neditForm.propName.focus();\n}\n";

        temp = temp + "function saveCheck() {\n";
        String saved = (String) pageContext.getRequest().getAttribute(
            Constants._ACTSAVE);
        if (saved != null)
        {
            temp = temp + "saved(\"" + saved + "\");\n";
        }
        temp = temp + "}\n";

        return temp;
    }

    /**
     * Standard tag release method.
     */
    public void release()
    {
        _section = null;
        _ub = null;
        _request = null;
        _namesList = null;
        _namesVals = null;
        _namesExempt = null;
        _displayList = null;
        _displayVals = null;
        _groupsList = null;
        _groupsVals = null;
        _contextList = null;
        _contextVals = null;
        _schemeList = null;
        _schemeVals = null;
        _schemeContext = null;
        _formsList = null;
        _formsVals = null;
        _formsContext = null;
        _schemeItemList = null;
        _schemeItemVals = null;
        _schemeItemSchemes = null;
        _workflowList = null;
        _workflowVals = null;
        _regstatusList = null;
        _regstatusVals = null;
        super.release();
    }

    // Class data elements.
    private String         _section;

    private AlertBean      _ub;

    private ServletRequest _request;

    private String         _namesList[];

    private String         _namesVals[];

    private String         _namesExempt;

    private String         _displayList[];

    private String         _displayVals[];

    private String         _groupsList[];

    private String         _groupsVals[];

    private String         _contextList[];

    private String         _contextVals[];

    private String         _schemeList[];

    private String         _schemeVals[];

    private String         _schemeContext[];

    private String         _formsList[];

    private String         _formsVals[];

    private String         _formsContext[];

    private String         _schemeItemList[];

    private String         _schemeItemVals[];

    private String         _schemeItemSchemes[];

    private String         _workflowList[];

    private String         _workflowVals[];

    private String         _regstatusList[];

    private String         _regstatusVals[];
}