// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/EditTag.java,v 1.2 2007-05-14 14:30:30 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

import gov.nih.nci.cadsr.sentinel.database.DBAlert;
import gov.nih.nci.cadsr.sentinel.database.DBAlertUtil;
import gov.nih.nci.cadsr.sentinel.tool.Constants;
import java.io.IOException;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.struts.util.MessageResources;
import org.apache.struts.Globals;

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
            else if (_section.equals("subject"))
            {
                temp = getSubject();
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
     * Get the email subject for display.
     * 
     * @return the subject
     */
    private String getSubject()
    {
        return _subject;
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
        DBAlert db = DBAlertUtil.factory();
        if (db.open(pageContext.getServletContext(), _ub.getUser()) == 0)
        {
            _subject = db.selectAlertReportEmailSubject();

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
            db.getProtos();
            _protoList = db.getProtoList();
            _protoVals = db.getProtoVals();
            _protoContext = db.getProtoContext();
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
            _cworkflowList = db.getCWorkflowList();
            _cworkflowVals = db.getCWorkflowVals();
            db.getRegistrations();
            _regstatusList = db.getRegStatusList();
            _regstatusVals = db.getRegStatusVals();
            _regcstatusList = db.getRegCStatusList();
            _regcstatusVals = db.getRegCStatusVals();
            db.getACTypes();
            _actypesList = db.getACTypesList();
            _actypesVals = db.getACTypesVals();
        }
        db.close();

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
            .findAttribute(Globals.MESSAGES_KEY);

        int tspot = 0;
        String temp[] = new String[106];
        temp[tspot++] = "var MstatusReason = false;\nvar Mprev = \""
            + _ub.getEditPrev() + "\";\n";

        // Everything is in name/value pairs as a minimum. The name is shown to
        // the user in the
        // browser and the value is sent back to the servlet on a submit.
        temp[tspot++] = "var DBnamesList = ";
        temp[tspot++] = stringList(_namesList);

        temp[tspot++] = "var DBnamesVals = ";
        temp[tspot++] = stringList(_namesVals);

        temp[tspot++] = "var DBcontextList = ";
        temp[tspot++] = stringList(_contextList);

        temp[tspot++] = "var DBcontextVals = ";
        temp[tspot++] = stringList(_contextVals);

        temp[tspot++] = "var DBgroupsList = ";
        temp[tspot++] = stringList(_groupsList);

        temp[tspot++] = "var DBgroupsVals = ";
        temp[tspot++] = stringList(_groupsVals);

        temp[tspot++] = "var DBworkflowList = ";
        temp[tspot++] = stringList(_workflowList);

        temp[tspot++] = "var DBworkflowVals = ";
        temp[tspot++] = stringList(_workflowVals);

        temp[tspot++] = "var DBcworkflowList = ";
        temp[tspot++] = stringList(_cworkflowList);

        temp[tspot++] = "var DBcworkflowVals = ";
        temp[tspot++] = stringList(_cworkflowVals);

        temp[tspot++] = "var DBregStatusList = ";
        temp[tspot++] = stringList(_regstatusList);

        temp[tspot++] = "var DBregStatusVals = ";
        temp[tspot++] = stringList(_regstatusVals);

        temp[tspot++] = "var DBregCStatusList = ";
        temp[tspot++] = stringList(_regcstatusList);

        temp[tspot++] = "var DBregCStatusVals = ";
        temp[tspot++] = stringList(_regcstatusVals);

        temp[tspot++] = "var RecSchemes = ";
        temp[tspot++] = stringList(_ub.getWorking().getSchemes());

        temp[tspot++] = "var RecSchemeItems = ";
        temp[tspot++] = stringList(_ub.getWorking().getSchemeItems());

        temp[tspot++] = "var DBschemeList = ";
        temp[tspot++] = stringList(_schemeList);

        temp[tspot++] = "var DBschemeVals = ";
        temp[tspot++] = stringList(_schemeVals);

        temp[tspot++] = "var DBschemeContexts = ";
        temp[tspot++] = stringList(_schemeContext);

        temp[tspot++] = "var DBschemeItemList = ";
        temp[tspot++] = stringList(_schemeItemList);

        temp[tspot++] = "var DBschemeItemVals = ";
        temp[tspot++] = stringList(_schemeItemVals);

        temp[tspot++] = "var DBschemeItemSchemes = ";
        temp[tspot++] = stringList(_schemeItemSchemes);

        temp[tspot++] = "var RecProtos = ";
        temp[tspot++] = stringList(_ub.getWorking().getProtocols());

        temp[tspot++] = "var DBprotoList = ";
        temp[tspot++] = stringList(_protoList);

        temp[tspot++] = "var DBprotoVals = ";
        temp[tspot++] = stringList(_protoVals);

        temp[tspot++] = "var DBprotoContexts = ";
        temp[tspot++] = stringList(_protoContext);

        temp[tspot++] = "var RecForms = ";
        temp[tspot++] = stringList(_ub.getWorking().getForms());

        temp[tspot++] = "var DBformsList = ";
        temp[tspot++] = stringList(_formsList);

        temp[tspot++] = "var DBformsVals = ";
        temp[tspot++] = stringList(_formsVals);

        temp[tspot++] = "var DBformsContexts = ";
        temp[tspot++] = stringList(_formsContext);

        temp[tspot++] = "var DBactypesList = ";
        temp[tspot++] = stringList(_actypesList);

        temp[tspot++] = "var DBactypesVals = ";
        temp[tspot++] = stringList(_actypesVals);

        temp[tspot++] = "var DBrecipients = ";
        temp[tspot++] = stringList(_ub.getWorking().getRecipients());

        temp[tspot++] = "var DBactVerNum = \"" + _ub.getWorking().getActVerNum()
            + "\";\n";

        // Most of the Javascript for the edit.jsp page is in the edit.js file.
        // The body onloaded function
        // is written to call "loaded0()" to complete the operation for the
        // dynamic part of the script.
        temp[tspot++] = "function loaded0() {\n";

        int ndx;

        // Add the exempt list to the display.
        if (_namesExempt == null)
            temp[tspot++] = "";
        else if (_namesExempt.length() > 256)
            temp[tspot++] =
                "exemptlist.innerHTML = \"The following users only receive Alert "
                + "Broadcasts when added as specific Recipients.<br>\\\n<textarea class=\\\"sstd100\\\" rows=\\\"3\\\" readonly>"
                + _namesExempt
                + "</textarea>\";\n";
        else
            temp[tspot++] = 
                "exemptlist.innerHTML = \"The following users only receive Alert "
                + "Broadcasts when added as specific Recipients.<br>"
                + _namesExempt + "\";\n";

        // You may wonder why these field values are set here when struts will
        // automatically load the
        // fields from the form bean. Actually, I couldn't find a way to have
        // the form bean access the
        // database on the outbound page.
        temp[tspot++] = "editForm.propName.value = \""
            + _ub.getWorking().getName() + "\";\n";
        temp[tspot++] = "editForm.propDesc.value = \""
            + _ub.getWorking().getSummary(true) + "\";\n";
        temp[tspot++] = "editForm.propCreator.value = \""
            + _ub.getWorking().getCreator() + "\";\n";
        temp[tspot++] = "editForm.propCreateDate.value = \""
            + _ub.getWorking().getCDate() + "\";\n";
        temp[tspot++] = "editForm.propLastRunDate.value = \""
            + _ub.getWorking().getADate() + "\";\n";
        temp[tspot++] = "editForm.propModifyDate.value = \""
            + _ub.getWorking().getMDate() + "\";\n";
        if (_ub.getWorking().getIncPropSect())
            temp[tspot++] = "editForm.repIncProp.checked = true;\n";
        else
            temp[tspot++] = "";

        temp[tspot++] = "editForm.infoAssocLvl.value = \"" + _ub.getWorking().getIAssocLvl()
            + "\";\n"
            + "editForm.infoAssocLvl.options[" + _ub.getWorking().getIAssocLvl() + "].selected = true;\n";
        
        if (_ub.getWorking().getIntro(false) == null)
            temp[tspot++] = "editForm.propIntro.value = \""
                + msgs.getMessage("edit.static") + "\";\n";
        else
            temp[tspot++] = "editForm.propIntro.value = \""
                + _ub.getWorking().getIntro(true) + "\";\n";

        if (_ub.getWorking().isFreqDay())
        {
            temp[tspot++] = "editForm.freqUnit[0].checked = true;\n";
        }
        else if (_ub.getWorking().isFreqWeek())
        {
            ndx = _ub.getWorking().getDay() - 1;
            temp[tspot++] = "editForm.freqUnit[1].checked = true;\n"
                + "editForm.freqWeekly.disabled = false;\n"
                + "editForm.freqWeekly.options[" + ndx + "].selected = true;\n";
        }
        else if (_ub.getWorking().isFreqMonth())
        {
            ndx = _ub.getWorking().getDay() - 1;
            temp[tspot++] = "editForm.freqUnit[2].checked = true;\n"
                + "editForm.freqMonthly.disabled = false;\n"
                + "editForm.freqMonthly.options[" + ndx
                + "].selected = true;\n";
        }
        else
            temp[tspot++] = "";

        if (_ub.getWorking().isActive())
        {
            temp[tspot++] = "editForm.propStatus[0].checked = true;\n"
                + "editForm.propStatusReason.value = \""
                + msgs.getMessage("edit.statusi2") + "\";\n";
            temp[tspot++] = "";
        }
        else if (_ub.getWorking().isActiveOnce())
        {
            temp[tspot++] = "editForm.propStatus[1].checked = true;\n"
                + "editForm.propStatusReason.value = \""
                + msgs.getMessage("edit.statusi2") + "\";\n";
            temp[tspot++] = "";
        }
        else if (_ub.getWorking().isActiveDates())
        {
            temp[tspot++] = "editForm.propStatus[2].checked = true;\n"
                + "editForm.propStatusReason.value = \""
                + msgs.getMessage("edit.statusi2") + "\";\n";
            temp[tspot++] = "";
        }
        else
        {
            temp[tspot++] = "editForm.propStatus[3].checked = true;\n"
                + "editForm.propStatusReason.value = \"";
            if (_ub.getWorking().getInactiveReason(false) == null)
            {
                temp[tspot++] = msgs.getMessage("edit.explain") + "\";\n";
            }
            else
            {
                temp[tspot++] = _ub.getWorking().getInactiveReason(true)
                    + "\";\n" + "MstatusReason = true;\n";
            }
        }

        if (_ub.getWorking().isSendEmptyReport())
            temp[tspot++] = "editForm.freqEmpty[1].checked = true;\n";
        else
            temp[tspot++] = "editForm.freqEmpty[0].checked = true;\n";

        temp[tspot++] = selectFromList("actWorkflowStatus", _workflowVals, _ub
                .getWorking().getAWorkflow(), true);
        temp[tspot++] = selectFromList("infoWorkflow", _cworkflowVals, _ub
            .getWorking().getCWorkflow(), true);
        temp[tspot++] = selectFromList("actRegStatus", _regstatusVals, _ub.getWorking()
                .getARegis(), true);
        temp[tspot++] = selectFromList("infoCreator", _namesVals, _ub.getWorking()
                .getCreators(), true);
        temp[tspot++] = selectFromList("infoModifier", _namesVals, _ub.getWorking()
                .getModifiers(), true);
        temp[tspot++] = selectFromList("infoACTypes", _actypesVals, _ub.getWorking()
                .getACTypes(), true);
        temp[tspot++] = selectFromList("infoRegStatus", _regcstatusVals, _ub.getWorking()
            .getCRegStatus(), true);
        temp[tspot++] = selectFromList("infoContext", _contextVals, _ub.getWorking()
                .getContexts(), true);
        temp[tspot++] = "changedContext();\n";
        temp[tspot++] = "setSelected(editForm.infoForms, RecForms);\n";
        temp[tspot++] = "setSelected(editForm.infoSchemes, RecSchemes);\n";
        temp[tspot++] = "setSelected(editForm.infoProtos, RecProtos);\n";
        temp[tspot++] = "changedCS();\n";
        temp[tspot++] = "setSelected(editForm.infoSchemeItems, RecSchemeItems);\n";

        switch (_ub.getWorking().getAVersion())
        {
            case DBAlert._VERANYCHG:
                temp[tspot++] = "editForm.actVersion[0].checked = true;\n";
                break;
            case DBAlert._VERMAJCHG:
                temp[tspot++] = "editForm.actVersion[1].checked = true;\n";
                break;
            case DBAlert._VERIGNCHG:
                temp[tspot++] = "editForm.actVersion[2].checked = true;\n";
                break;
            case DBAlert._VERSPECHG:
                temp[tspot++] = "editForm.actVersion[3].checked = true;\n";
                break;
            default:
                temp[tspot++] = "";
                break;
        }
        
        switch (_ub.getWorking().getDateFilter())
        {
            case DBAlert._DATECONLY:
                temp[tspot++] = "editForm.infoDateFilter.options[2].selected = true;\n";
                break;
            case DBAlert._DATEMONLY:
                temp[tspot++] = "editForm.infoDateFilter.options[1].selected = true;\n";
                break;
            case DBAlert._DATECM: 
            default:
                temp[tspot++] = "editForm.infoDateFilter.options[0].selected = true;\n";
                break;
        }

        if (_ub.getWorking().getStart() != null)
            temp[tspot++] = "editForm.propBeginDate.value = \""
                + _ub.getWorking().getSDate() + "\";\n";
        else
            temp[tspot++] = "";
        if (_ub.getWorking().getEnd() != null)
            temp[tspot++] = "editForm.propEndDate.value = \""
                + _ub.getWorking().getEDate() + "\";\n";
        else
            temp[tspot++] = "";

        String run = (String) pageContext.getRequest().getAttribute(
            Constants._ACTRUN);
        if (run == null)
            temp[tspot++] = "";
        else
            temp[tspot++] = "alert(\"Alert named '" + run
                + "' has been submitted.\");\n";

        temp[tspot++] = "if (MmainTab == tabMain1)\neditForm.propName.focus();\n}\n";

        temp[tspot++] = "function saveCheck() {\n";
        String saved = (String) pageContext.getRequest().getAttribute(
            Constants._ACTSAVE);
        if (saved == null)
            temp[tspot++] = "";
        else
            temp[tspot++] = "saved(\"" + saved + "\");\n";
        temp[tspot++] = "}\n";

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
        _namesList = null;
        _namesVals = null;
        _namesExempt = null;
        _groupsList = null;
        _groupsVals = null;
        _contextList = null;
        _contextVals = null;
        _schemeList = null;
        _schemeVals = null;
        _schemeContext = null;
        _protoList = null;
        _protoVals = null;
        _protoContext = null;
        _formsList = null;
        _formsVals = null;
        _formsContext = null;
        _schemeItemList = null;
        _schemeItemVals = null;
        _schemeItemSchemes = null;
        _workflowList = null;
        _workflowVals = null;
        _cworkflowList = null;
        _cworkflowVals = null;
        _regstatusList = null;
        _regstatusVals = null;
        _regcstatusList = null;
        _regcstatusVals = null;
        _buf = null;
        _bufLen = 0;
        super.release();
    }

    // Class data elements.
    private String         _section;

    private AlertBean      _ub;

    private String         _namesList[];

    private String         _namesVals[];

    private String         _namesExempt;

    private String         _groupsList[];

    private String         _groupsVals[];

    private String         _contextList[];

    private String         _contextVals[];

    private String         _schemeList[];

    private String         _schemeVals[];

    private String         _schemeContext[];

    private String         _protoList[];

    private String         _protoVals[];

    private String         _protoContext[];

    private String         _formsList[];

    private String         _formsVals[];

    private String         _formsContext[];

    private String         _schemeItemList[];

    private String         _schemeItemVals[];

    private String         _schemeItemSchemes[];

    private String         _workflowList[];

    private String         _workflowVals[];

    private String         _cworkflowList[];

    private String         _cworkflowVals[];

    private String         _regstatusList[];

    private String         _regstatusVals[];

    private String         _regcstatusList[];

    private String         _regcstatusVals[];
    
    private String         _actypesList[];
    
    private String         _actypesVals[];
    
    private StringBuffer   _buf;
    
    private int            _bufLen;
    
    private String       _subject;

    private static final long serialVersionUID = 1822843268148691725L;
}