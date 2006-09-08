// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/FootTag.java,v 1.1 2006-09-08 22:32:55 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

import gov.nih.nci.cadsr.sentinel.tool.Constants;
import java.io.IOException;
import java.io.File;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.struts.util.MessageResources;

/**
 * This is used to place a standard footer on every JSP in the Sentinel Tool
 * interface.
 * 
 * @author Larry Hebel
 */

public class FootTag extends TagSupport
{
    /**
     * Constructor.
     */
    public FootTag()
    {
    }

    /**
     * Output the standard footer
     * 
     * @return EVAL_PAGE to continue processing the JSP.
     */
    public int doEndTag()
    {
        try
        {
            String jboss = System.getenv("JBOSS_HOME");
            jboss = (jboss == null) ? "" : jboss.substring(jboss.lastIndexOf(File.separatorChar) + 1);
            MessageResources msgs = (MessageResources) pageContext
                .findAttribute(Constants._RESOURCES);
            JspWriter out = pageContext.getOut();
            out
                .print("<table class=\"table3\"><colgroup></colgroup><tbody class=\"secttbody\" />\n"
                    + "<tr><td class=\"ncifmenu\"><span style=\"color: #dddddd\">"
                    + msgs.getMessage("Appl.version")
                    + "&nbsp;(" + jboss + "/" + System.getProperty("java.version") + ")"
                    + "</span></td></tr>\n"
                    + "<tr>\n<td class=\"nciftrtable\">\n"
                    + "<a href=\"mailto:ncicb@pop.nci.nih.gov?subject=caDSR%20Sentinel%20Tool\"><span class=\"wdemail\" title=\"Email NCICB Help Desk\">&#42;</span></a>\n"
                    + "<a target=\"_blank\" href=\"http://www.cancer.gov/\"><img border=\"0\" src=\"footer_nci.gif\" alt=\"National Cancer Institute Logo\" title=\"National Cancer Institute\"></a>\n"
                    + "<a target=\"_blank\" href=\"http://www.dhhs.gov/\"><img border=\"0\" src=\"footer_hhs.gif\" alt=\"Department of Health and Human Services Logo\" title=\"Department of Health and Human Services\"></a>\n"
                    + "<a target=\"_blank\" href=\"http://www.nih.gov/\"><img border=\"0\" src=\"footer_nih.gif\" alt=\"National Institutes of Health Logo\" title=\"National Institutes of Health\"></a>\n"
                    + "<a target=\"_blank\" href=\"http://www.firstgov.gov/\"><img border=\"0\" src=\"footer_firstgov.gif\" alt=\"FirstGov.gov\" title=\"FirstGov.gov\"></a>\n"
                    + "</td>\n</tr>\n</table>\n");
        }
        catch (IOException ex)
        {
        }
        return EVAL_PAGE;
    }

    private static final long serialVersionUID = -4073456777204188509L;
}