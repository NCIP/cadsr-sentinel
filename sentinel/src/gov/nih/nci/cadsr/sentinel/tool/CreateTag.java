// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tool/CreateTag.java,v 1.5 2006-02-14 21:38:12 hardingr Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tool;

import java.io.IOException;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * The JSP tags specific to create.jsp. <dtags:create section="script" />
 * <dtags:create section="field" />
 * 
 * @author Larry Hebel
 */

public class CreateTag extends TagSupport
{
    /**
     * Constructor.
     */
    public CreateTag()
    {
    }

    /**
     * Set section name.
     * 
     * @param section_
     *        Either "script" or "field".
     */
    public void setSection(String section_)
    {
        _section = section_;
    }

    /**
     * Process the end tag.
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

        DBAlert db = new DBAlert();
        db.open(pageContext.getServletContext(), _ub.getUser(), _ub.getPswd());
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
        String temp = "<input type=hidden name=sessionKey value=\""
            + _ub.getKey() + "\">\n";
        return temp;
    }

    /**
     * Standard tag release.
     */
    public void release()
    {
        _section = null;
        _ub = null;
        super.release();
    }

    // Class data.
    private String    _section;

    private AlertBean _ub;

    private static final long serialVersionUID = -6781255356606529465L;
}