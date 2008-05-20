// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/CreateTag.java,v 1.6 2008-05-20 22:57:29 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

import gov.nih.nci.cadsr.sentinel.database.DBAlert;
import gov.nih.nci.cadsr.sentinel.database.DBAlertUtil;
import gov.nih.nci.cadsr.sentinel.tool.AlertRec;
import gov.nih.nci.cadsr.sentinel.tool.Constants;
import java.io.IOException;
import javax.servlet.jsp.JspWriter;

/**
 * The JSP tags specific to create.jsp. <dtags:create section="script" />
 * <dtags:create section="field" />
 * 
 * @author Larry Hebel
 */

public class CreateTag extends AlertRootTag
{
    private static final long serialVersionUID = -6781255356606529465L;

    /**
     * Constructor.
     */
    public CreateTag()
    {
        super();
    }

    /**
     * Process the end tag.
     * 
     * @return EVAL_PAGE to continue processing the JSP.
     */
    public int doEnd()
    {
        String temp = null;

        try
        {
            // Determine the section to be processed.
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

    /**
     * Process the script section. Insert the necessary java script for client
     * side processing.
     * 
     * @return The script text.
     */
    private String getScript()
    {
        _ub.setWorking(new AlertRec(_ub.getUser(), _ub.getUserName()));

        String saved = (String) pageContext.getRequest().getAttribute(
            Constants._ACTSAVE);
        if (saved == null)
            saved = "";

        DBAlert db = DBAlertUtil.factory();
        db.open(pageContext.getServletContext(), _ub.getUser());
        String contexts = db.selectContextString(_ub.getUser());
        db.close();

        String option1 = "\"Criteria:\\nContext must be " + contexts
            + "\\n\\nMonitors:\\nAll Change Activities\",\n";

        String temp = "var Muserid = \"" + _ub.getUserName() + "\";\n"
            + "var Mdesc = [\n" + "\" \",\n" + option1 + "\" \",\n"
            + "\" \",\n" + "\" \",\n" + "\"?\",\n"
            + "\"Criteria:\\nCreated By must be " + _ub.getUserName()
            + "\\n\\nMonitors:\\nAll Change Activities\"\n" + "];\n"
            + "function loaded2() { saved(\"" + saved + "\"); }\n";

        return temp;
    }

    /**
     * Process the field section. Insert the necessary dynamic form fields.
     * 
     * @return Hidden and other required field definitions.
     */
    private String getField()
    {
        String temp = "<input type=\"hidden\" name=\"sessionKey\" value=\""
            + _ub.resetKey() + "\">\n";
        return temp;
    }
}