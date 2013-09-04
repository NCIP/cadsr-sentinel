/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tags/RunTag.java,v 1.2 2009-04-08 17:56:19 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tags;

import gov.nih.nci.cadsr.sentinel.database.DBAlert;
import gov.nih.nci.cadsr.sentinel.tool.AlertRec;
import gov.nih.nci.cadsr.sentinel.ui.AlertPlugIn;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import javax.servlet.jsp.JspWriter;

/**
 * Process the tags for run.jsp.
 * 
 * @author Larry Hebel
 */

public class RunTag extends AlertRootTag
{
    private static final long serialVersionUID = -1503025373047301625L;

    /**
     * Constructor.
     */
    public RunTag()
    {
        super();
    }

    /**
     * Process the appropriate section.
     * 
     * @return EVAL_PAGE to continue processing the JSP.
     */
    public int doEnd()
    {
        String temp = null;

        try
        {
            JspWriter out = pageContext.getOut();
            if (_section.equals("script"))
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

    private String getField()
    {
        return "\n<input type=\"hidden\" name=\"sessionKey\" value=\""
            + _ub.resetKey()
            + "\"/>";
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
        AlertPlugIn api = (AlertPlugIn) pageContext.getServletContext().getAttribute(DBAlert._DATASOURCE);

        String temp =
            "var helpUrl = \"" + api.getHelpUrl() + "\";\n\n"
            + "function loaded()\n{\n"
            + "\tvar obj = document.getElementsByName(\"startDate\");"
            + "\tobj[0].value = \"" + sdate + "\";\n"
            + "\tobj = document.getElementsByName(\"endDate\");"
            + "\tobj[0].value = \"" + snow + "\";\n" + "}\n";
        return temp;
    }
}