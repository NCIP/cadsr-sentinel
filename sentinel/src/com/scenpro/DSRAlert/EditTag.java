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
        _buf = null;
        _bufLen = 0;
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
            // We have input so add up the total length of the concatenation.
            int maxLen = 0;
            int listLen = list_.length;
            for (int ndx = 0; ndx < listLen; ++ndx)
            {
                maxLen += list_[ndx].length() + 4;
            }
            ++maxLen;
            
            // If the new length is larger than the buffer...
            if (_bufLen < maxLen)
            {
                // Get a bigger buffer.
                _bufLen = maxLen;
                _buf = new StringBuffer(_bufLen);
            }
            else
            {
                // Delete the old contents of the buffer.
                _buf.delete(0, _bufLen);
            }
            
            // Move the strings into the buffer and append the appropriate tokens.
            maxLen = 0;
            for (int ndx = 0; ndx < listLen; ++ndx)
            {
                if (ndx > 0)
                {
                    _buf.insert(maxLen, ",\n");
                    maxLen += 2;
                }
                _buf.insert(maxLen, "\"");
                _buf.insert(++maxLen, list_[ndx]);
                maxLen += list_[ndx].length();
                _buf.insert(maxLen++, "\"");
            }
            
            // Return the total string.
            temp = temp + _buf.substring(0, maxLen);
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

        String temp[] = new String[80];
        temp[0] = "var MstatusReason = false;\nvar Mprev = \""
            + _ub.getEditPrev() + "\";\n";

        // Everything is in name/value pairs as a minimum. The name is shown to
        // the user in the
        // browser and the value is sent back to the servlet on a submit.
        temp[1] = "var DBnamesList = ";
        temp[2] = stringList(_namesList);

        temp[3] = "var DBnamesVals = ";
        temp[4] = stringList(_namesVals);

        temp[5] = "var DBcontextList = ";
        temp[6] = stringList(_contextList);

        temp[7] = "var DBcontextVals = ";
        temp[8] = stringList(_contextVals);

        temp[9] = "var DBgroupsList = ";
        temp[10] = stringList(_groupsList);

        temp[11] = "var DBgroupsVals = ";
        temp[12] = stringList(_groupsVals);

        temp[13] = "var DBworkflowList = ";
        temp[14] = stringList(_workflowList);

        temp[15] = "var DBworkflowVals = ";
        temp[16] = stringList(_workflowVals);

        temp[17] = "var DBregStatusList = ";
        temp[18] = stringList(_regstatusList);

        temp[19] = "var DBregStatusVals = ";
        temp[20] = stringList(_regstatusVals);

        temp[21] = "var RecSchemes = ";
        temp[22] = stringList(_ub.getWorking().getSchemes());

        temp[23] = "var RecSchemeItems = ";
        temp[24] = stringList(_ub.getWorking().getSchemeItems());

        temp[25] = "var DBschemeList = ";
        temp[26] = stringList(_schemeList);

        temp[27] = "var DBschemeVals = ";
        temp[28] = stringList(_schemeVals);

        temp[29] = "var DBschemeContexts = ";
        temp[30] = stringList(_schemeContext);

        temp[31] = "var DBschemeItemList = ";
        temp[32] = stringList(_schemeItemList);

        temp[33] = "var DBschemeItemVals = ";
        temp[34] = stringList(_schemeItemVals);

        temp[35] = "var DBschemeItemSchemes = ";
        temp[36] = stringList(_schemeItemSchemes);

        temp[37] = "var RecForms = ";
        temp[38] = stringList(_ub.getWorking().getForms());

        temp[39] = "var DBformsList = ";
        temp[40] = stringList(_formsList);

        temp[41] = "var DBformsVals = ";
        temp[42] = stringList(_formsVals);

        temp[43] = "var DBformsContexts = ";
        temp[44] = stringList(_formsContext);

        temp[45] = "var DBrecipients = ";
        temp[46] = stringList(_ub.getWorking().getRecipients());

        temp[47] = "var DBactVerNum = \"" + _ub.getWorking().getActVerNum()
            + "\";\n";

        // Most of the Javascript for the edit.jsp page is in the edit.js file.
        // The body onloaded function
        // is written to call "loaded0()" to complete the operation for the
        // dynamic part of the script.
        temp[48] = "function loaded0() {\n";

        int ndx;

        // Add the exempt list to the display.
        if (_namesExempt == null)
            temp[49] = "";
        else
            temp[49] = 
                "exemptlist.innerHTML = \"The following users only receive Alert "
                + "Broadcasts when added as specific Recipients.<br>"
                + _namesExempt + "\";\n";

        // You may wonder why these field values are set here when struts will
        // automatically load the
        // fields from the form bean. Actually, I couldn't find a way to have
        // the form bean access the
        // database on the outbound page.
        temp[50] = "editForm.propName.value = \""
            + _ub.getWorking().getName() + "\";\n";
        temp[51] = "editForm.propDesc.value = \""
            + _ub.getWorking().getSummary(true) + "\";\n";
        temp[52] = "editForm.propCreator.value = \""
            + _ub.getWorking().getCreator() + "\";\n";
        temp[53] = "editForm.propCreateDate.value = \""
            + _ub.getWorking().getCDate() + "\";\n";
        temp[54] = "editForm.propLastRunDate.value = \""
            + _ub.getWorking().getADate() + "\";\n";
        temp[55] = "editForm.propModifyDate.value = \""
            + _ub.getWorking().getMDate() + "\";\n";
        if (_ub.getWorking().getIncPropSect())
            temp[56] = "editForm.repIncProp.checked = true;\n";
        else
            temp[56] = "";

        if (_ub.getWorking().getIntro(false) == null)
            temp[57] = "editForm.propIntro.value = \""
                + msgs.getMessage("edit.static") + "\";\n";
        else
            temp[57] = "editForm.propIntro.value = \""
                + _ub.getWorking().getIntro(true) + "\";\n";

        if (_ub.getWorking().isFreqDay())
        {
            temp[58] = "editForm.freqUnit[0].checked = true;\n";
        }
        else if (_ub.getWorking().isFreqWeek())
        {
            ndx = _ub.getWorking().getDay() - 1;
            temp[58] = "editForm.freqUnit[1].checked = true;\n"
                + "editForm.freqWeekly.disabled = false;\n"
                + "editForm.freqWeekly.options[" + ndx + "].selected = true;\n";
        }
        else if (_ub.getWorking().isFreqMonth())
        {
            ndx = _ub.getWorking().getDay() - 1;
            temp[58] = "editForm.freqUnit[2].checked = true;\n"
                + "editForm.freqMonthly.disabled = false;\n"
                + "editForm.freqMonthly.options[" + ndx
                + "].selected = true;\n";
        }
        else
            temp[58] = "";

        if (_ub.getWorking().isActive())
        {
            temp[59] = "editForm.propStatus[0].checked = true;\n"
                + "editForm.propStatusReason.value = \""
                + msgs.getMessage("edit.statusi2") + "\";\n";
            temp[60] = "";
        }
        else if (_ub.getWorking().isActiveOnce())
        {
            temp[59] = "editForm.propStatus[1].checked = true;\n"
                + "editForm.propStatusReason.value = \""
                + msgs.getMessage("edit.statusi2") + "\";\n";
            temp[60] = "";
        }
        else if (_ub.getWorking().isActiveDates())
        {
            temp[59] = "editForm.propStatus[2].checked = true;\n"
                + "editForm.propStatusReason.value = \""
                + msgs.getMessage("edit.statusi2") + "\";\n";
            temp[60] = "";
        }
        else
        {
            temp[59] = "editForm.propStatus[3].checked = true;\n"
                + "editForm.propStatusReason.value = \"";
            if (_ub.getWorking().getInactiveReason(false) == null)
            {
                temp[60] = msgs.getMessage("edit.explain") + "\";\n";
            }
            else
            {
                temp[60] = _ub.getWorking().getInactiveReason(true)
                    + "\";\n" + "MstatusReason = true;\n";
            }
        }

        if (_ub.getWorking().isSendEmptyReport())
            temp[61] = "editForm.freqEmpty[1].checked = true;\n";
        else
            temp[61] = "editForm.freqEmpty[0].checked = true;\n";

        temp[62] = selectFromList("actWorkflowStatus", _workflowVals, _ub
                .getWorking().getAWorkflow(), true);
        temp[63] = selectFromList("actRegStatus", _regstatusVals, _ub.getWorking()
                .getARegis(), true);
        temp[64] = selectFromList("infoCreator", _namesVals, _ub.getWorking()
                .getCreators(), true);
        temp[65] = selectFromList("infoModifier", _namesVals, _ub.getWorking()
                .getModifiers(), true);
        temp[66] = selectFromList("infoContext", _contextVals, _ub.getWorking()
                .getContexts(), true);
        temp[67] = "changedContext();\n";
        temp[68] = "setSelected(editForm.infoForms, RecForms);\n";
        temp[69] = "setSelected(editForm.infoSchemes, RecSchemes);\n";
        temp[70] = "changedCS();\n";
        temp[71] = "setSelected(editForm.infoSchemeItems, RecSchemeItems);\n";

        switch (_ub.getWorking().getAVersion())
        {
            case AlertRec._VERANYCHG:
                temp[72] = "editForm.actVersion[0].checked = true;\n";
                break;
            case AlertRec._VERMAJCHG:
                temp[72] = "editForm.actVersion[1].checked = true;\n";
                break;
            case AlertRec._VERIGNCHG:
                temp[72] = "editForm.actVersion[2].checked = true;\n";
                break;
            case AlertRec._VERSPECHG:
                temp[72] = "editForm.actVersion[3].checked = true;\n";
                break;
            default:
                temp[72] = "";
                break;
        }

        if (_ub.getWorking().getStart() != null)
            temp[73] = "editForm.propBeginDate.value = \""
                + _ub.getWorking().getSDate() + "\";\n";
        else
            temp[73] = "";
        if (_ub.getWorking().getEnd() != null)
            temp[74] = "editForm.propEndDate.value = \""
                + _ub.getWorking().getEDate() + "\";\n";
        else
            temp[74] = "";

        String run = (String) pageContext.getRequest().getAttribute(
            Constants._ACTRUN);
        if (run == null)
            temp[75] = "";
        else
            temp[75] = "alert(\"Alert named '" + run
                + "' has been submitted.\");\n";

        temp[76] = "if (MmainTab == tabMain1)\neditForm.propName.focus();\n}\n";

        temp[77] = "function saveCheck() {\n";
        String saved = (String) pageContext.getRequest().getAttribute(
            Constants._ACTSAVE);
        if (saved == null)
            temp[78] = "";
        else
            temp[78] = "saved(\"" + saved + "\");\n";
        temp[79] = "}\n";

        // Build the complete string.  This is a performance saving technique.
        int maxLen = 0;
        int listLen = temp.length;
        for (ndx = 0; ndx < listLen; ++ndx)
        {
            maxLen += temp[ndx].length();
        }
        ++maxLen;
        StringBuffer buf = new StringBuffer(maxLen);
        maxLen = 0;
        for (ndx = 0; ndx < listLen; ++ndx)
        {
            buf.insert(maxLen, temp[ndx]);
            maxLen += temp[ndx].length();
        }
        
        return buf.toString();
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
        _buf = null;
        _bufLen = 0;
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
    
    private StringBuffer   _buf;
    
    private int            _bufLen;
}