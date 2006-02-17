// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tool/RunTag.java,v 1.6 2006-02-17 21:45:05 hardingr Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tool;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Process the tags for run.jsp.
 * 
 * @author Larry Hebel
 */

public class RunTag extends TagSupport
{
    /**
     * Constructor.
     */
    public RunTag()
    {
    }

    /**
     * Set the section name.
     * 
     * @param section_
     *        Currently only "script".
     */
    public void setSection(String section_)
    {
        _section = section_;
    }

    /**
     * Process the appropriate section.
     * 
     * @return EVAL_PAGE to continue processing the JSP.
     */
    public int doEndTag()
    {
        HttpSession session = pageContext.getSession();
        _ub = (AlertBean) session.getAttribute(AlertBean._SESSIONNAME);
        String temp = null;

        try
        {
            JspWriter out = pageContext.getOut();
            if (_section.equals("script"))
            {
                temp = getScript();
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
     * Process the Script section.
     * 
     * @return The Java Script to include in the JSP.
     */
    private String getScript()
    {
        // The end date is today.
        Date now = new Date();
        Timestamp today = new Timestamp(now.getTime());
        String snow = AlertRec.dateToString(today, false);

        // The start date is the last manual run date if set, otherwise
        // it's the last auto run date if set, otherwise it's the last
        // modified date of the alert record.
        String sdate = _ub.getWorking().getRDate();
        if (sdate.length() == 0)
            sdate = _ub.getWorking().getADate();
        if (sdate.length() == 0)
            sdate = _ub.getWorking().getMDate();

        // Drop the time part and just keep the date.
        int pos = sdate.indexOf(' ');
        if (pos > 0)
            sdate = sdate.substring(0, pos);

        // Build the Javascript for the JSP.
        String temp = "function loaded()\n{\n"
            + "\trunForm.startDate.value = \"" + sdate + "\";\n"
            + "\trunForm.endDate.value = \"" + snow + "\";\n" + "}\n";
        return temp;
    }

    // Class data.
    private String    _section;

    private AlertBean _ub;

    private static final long serialVersionUID = -1503025373047301625L;
}