// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tags/ListTag.java,v 1.2 2008-12-03 00:16:26 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tags;

import gov.nih.nci.cadsr.sentinel.database.DBAlert;
import gov.nih.nci.cadsr.sentinel.database.DBAlertUtil;
import gov.nih.nci.cadsr.sentinel.tool.AlertRec;
import gov.nih.nci.cadsr.sentinel.tool.Constants;
import gov.nih.nci.cadsr.sentinel.ui.AlertBean;
import gov.nih.nci.cadsr.sentinel.ui.AlertPlugIn;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import org.apache.struts.util.MessageResources;
import org.apache.struts.Globals;

/**
 * The List tags used on list.jsp.
 * 
 * @author Larry Hebel
 */

public class ListTag extends AlertRootTag
{
    private static final long serialVersionUID = -5247677342477707453L;

    /**
     * Constructor.
     */
    public ListTag()
    {
        super();
    }

    /**
     * Process the requested section.
     * 
     * @return EVAL_PAGE to continue processing the JSP.
     */
    public int doEnd()
    {
        String temp = null;

        try
        {
            JspWriter out = pageContext.getOut();
            if (_section.equals("init"))
            {
                temp = getInit();
            }
            else if (_section.equals("table"))
            {
                temp = getTable();
            }
            else if (_section.equals("field"))
            {
                temp = getField();
            }
            else if (_section.equals("button"))
            {
                temp = getButton();
            }
            else if (_section.equals("script"))
            {
                temp = getScript();
            }
            else if (_section.equals("info"))
            {
                temp = getInfo();
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
     * Process the Field section.
     * 
     * @return The Java Script for the special form fields.
     */
    private String getField()
    {
        return "\n<input type=\"hidden\" name=\"sessionKey\" value=\"" + _ub.resetKey()
            + "\"/>\n" + "<input type=\"hidden\" name=\"listShow\" value=\""
            + _ub.getListShow() + "\"/>\n"
            + "<input type=hidden name=rowCount value=\""
            + pageContext.getRequest().getAttribute("count") + "\"/>";
    }

    /**
     * Process the Button section.
     * 
     * @return The Java Script for the Show* button.
     */
    private String getButton()
    {
        String temp;
        MessageResources msgs = (MessageResources) pageContext
            .findAttribute(Globals.MESSAGES_KEY);
        temp = (_ub.isListShowPrivate()) ? msgs.getMessage("list.showall")
            : msgs.getMessage("list.showprivate");
        temp = "<button type=button class=\"but1a\" onclick=\"cmdList();\">"
            + temp + "</button>";
        return temp;
    }

    /**
     * Process the Script section.
     * 
     * @return The Java Script for dynamic values.
     */
    private String getScript()
    {
        String temp = (String) pageContext.getRequest().getAttribute(
            Constants._ACTSAVE);

        AlertPlugIn api = (AlertPlugIn) pageContext.getServletContext().getAttribute(DBAlert._DATASOURCE);

        String script =
            "var helpUrl = \"" + api.getHelpUrl() + "\";\n"
            + "var Muserid = \"" + _ub.getUserUpper() + "\";\n"
            + "var Madmin = " + _ub.isAdmin() + ";\n"
            + "\nfunction cmdList() { "
            + "checkCount = 0; "
            + "listForm.listShow.value = (listForm.listShow.value == \"" + AlertBean._SHOWPRIV + "\") ? \"" + AlertBean._SHOWALL + "\" : \"" + AlertBean._SHOWPRIV + "\"; "
            + "listForm.submit(); }";

        if (temp == null)
        {
            String run = (String) pageContext.getRequest().getAttribute(
                Constants._ACTRUN);
            if (run == null)
                run = "";
            else
                run = "alert(\"Alert named '" + run
                    + "' has been submitted.\");";
            script = script + "\nfunction saved() { " + run + " }";
        }
        else
        {
            script = script
                + "\nfunction saved() { alert(\"Successful Save\"); }";
        }
        return script;
    }

    /**
     * Process the Info section.
     * 
     * @return The informational/introduction paragraph for new users.
     */
    private String getInfo()
    {
        AlertRec list[] = (AlertRec[]) pageContext.getRequest().getAttribute(
            "alertList");
        if (list != null)
            return "";
        return "<div style=\"text-align: left; margin-left: 0.4in; margin-right: 0.4in\"><p class=std12>"
            + "<b>Welcome</b> to the caDSR Sentinel Tool.\nThis tool allows the creation and maintenance of Alert Definitions "
            + "for monitoring activity within a caDSR database.\n"
            + "To begin, please use one of the following procedures.</p></div>\n"
            + "<div style=\"text-align: left; margin-left: 0.8in; margin-right: 0.8in\"><ol>"
            + "<li><p class=std12>"
            + "Select the <b>Create</b> button to create a new Alert Definition.</p></li>\n"
            + "<li><p class=std12>"
            + "Select the <b>Show All</b> button for a list of existing Alert Definitions. Select the one which "
            + "appears to most closely match the desired criteria. Select the <b>Create Using</b> button to "
            + "customize and save the new definition.</p></li>\n"
            + "</ol></div>\n<div style=\"text-align: left; margin-left: 0.4in; margin-right: 0.4in\"><p class=std12>"
            + "Additionally, you may Edit, Delete and Run any Alert Definition which you create.</p></div>";
    }

    /**
     * Process the Init section.
     * 
     * @return null as this does not generate any output.
     */
    private String getInit()
    {
        AlertRec database[] = null;
        int count = 0;

        DBAlert db = DBAlertUtil.factory();
        if (db.open(pageContext.getServletContext(), _ub.getUser()) == 0)
        {
            if (_ub.isListShowPrivate())
                database = db.selectAlerts(_ub.getUser());
            else
                database = db.selectAlerts((String) null);

            if (database != null && database.length > 0)
                count = database.length;

            _ub.setAdmin(db.checkToolAdministrator(_ub.getUser()));
            db.close();
        }
        pageContext.getRequest().setAttribute("alertList", database);
        pageContext.getRequest().setAttribute("count", Integer.toString(count));

        return null;
    }

    /**
     * Process the Table section.
     * 
     * @return The list table headers.
     */
    private String getTable()
    {
        String temp = "\n";
        AlertRec list[] = (AlertRec[]) pageContext.getRequest().getAttribute(
            "alertList");

        MessageResources msgs = (MessageResources) pageContext
            .findAttribute(Globals.MESSAGES_KEY);
        if (list != null && list.length > 4)
            temp = temp + "<div style=\"height: 7.5in; border: solid black 1px; overflow: auto\">\n";
        temp = temp
            + "<table id=\"theList\" class=\"table4\" summary=\"" + msgs.getMessage("list.table") + "\">\n"
            + "<colgroup></colgroup><tbody style=\"padding: 0.1in 0.1in 0.1in 0.1in\" /><tr>\n"
            + "<th id=\"t0\"><input type=\"checkbox\" value=\"Y\" onclick=\"setAllChecks();\"></th>\n"
            + "<th id=\"t1\"><span class=\"link1\" onmouseover=\"setCursor(this, 'default');\" onmouseout=\"setCursor(this, 'auto');\"\n"
            + "onclick=\"sortCol(cName, 1);\" title=\"" + msgs.getMessage("list.nametitle") + "\">" + msgs.getMessage("list.name") + "</span><img id=\"cName\" src=\"../images/blank.gif\"/></th>\n"
            + "<th id=\"t2\">" + msgs.getMessage("list.summary") + "</th>\n"
            + "<th id=\"t3\"><span class=\"link1\" onmouseover=\"setCursor(this, 'default');\" onmouseout=\"setCursor(this, 'auto');\"\n"
            + "onclick=\"sortCol(cFreq, 3);\" title=\"" + msgs.getMessage("list.freqtitle") + "\">" + msgs.getMessage("list.freq") + "</span><img id=\"cFreq\" src=\"../images/blank.gif\" /></th>\n"
            + "<th id=\"t4\"><span class=\"link1\" onmouseover=\"setCursor(this, 'default');\" onmouseout=\"setCursor(this, 'auto');\"\n"
            + "onclick=\"sortCol(cLast, 4);\" title=\"" + msgs.getMessage("list.runtitle") + "\">" + msgs.getMessage("list.run") + "</span><img id=\"cLast\" src=\"../images/blank.gif\" /></th>\n"
            + "<th id=\"t5\"><span class=\"link1\" onmouseover=\"setCursor(this, 'default');\" onmouseout=\"setCursor(this, 'auto');\"\n"
            + "onclick=\"sortCol(cStatus, 5);\" title=\"" + msgs.getMessage("list.statustitle") + "\">" + msgs.getMessage("list.status") + "</span><img id=\"cStatus\" src=\"../images/blank.gif\" /></th>\n"
            + "<th id=\"t6\"><span class=\"link1\" onmouseover=\"setCursor(this, 'default');\" onmouseout=\"setCursor(this, 'auto');\"\n"
            + "onclick=\"sortCol(cCreator, 6);\" title=\"" + msgs.getMessage("list.creatortitle") + "\">" + msgs.getMessage("list.creator") + "</span><img id=\"cCreator\" src=\"../images/blank.gif\" /></th>\n"
            + "</tr>";

        if (list != null)
        {
            for (int ndx = 0; ndx < list.length; ++ndx)
            {
                String aDate = list[ndx].getADate();
                if (aDate.length() == 0)
                    aDate = "(not yet run)";
                temp = temp
                    + "<tr>\n"
                    + "<td headers=\"t0\" class=td1a><input type=checkbox value=\"" + list[ndx].getAlertRecNum() + "\" name=\"cb"
                    + ndx + "\" onclick=\"fixButs(this);\"></td>\n"
                    + "<td headers=\"t1\" class=td1b>" + list[ndx].getName()
                    + "</td>\n" + "<td headers=\"t2\" class=td1b>"
                    + AlertRec.getHTMLString(list[ndx].getSummary(false)) + "</td>\n"
                    + "<td headers=\"t3\" class=td1a>"
                    + list[ndx].getFreq(true) + "</td>\n"
                    + "<td headers=\"t4\" class=td1a>" + aDate
                    + "</td>\n";

                if (list[ndx].isInactive())
                    temp = temp
                        + "<td headers=\"t5\" class=\"td1a\"><img src=\"../images/delete.gif\" cstSortKey=\"Z\" /></td>\n";
                else
                    temp = temp
                        + "<td headers=\"t5\" class=\"td1a\"><img src=\"../images/check.gif\" cstSortKey=\"A\" /></td>\n";
                temp = temp + "<td headers=\"t6\" class=td1a>"
                    + list[ndx].getCreator() + "</td>\n</tr>\n";
            }
        }
        
        temp = temp + "</table>\n";
        if (list!= null && list.length > 4)
            temp = temp + "</div>\n";

        return temp;
    }
}