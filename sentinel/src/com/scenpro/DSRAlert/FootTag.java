// Copyright (c) 2004 ScenPro, Inc.

package com.scenpro.DSRAlert;

import java.io.IOException;
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
            MessageResources msgs = (MessageResources) pageContext
                .findAttribute(Constants._RESOURCES);
            JspWriter out = pageContext.getOut();
            out
                .print("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n"
                    + "<tr><td>&nbsp;</td></tr>\n"
                    + "<tr><td class=\"ncifmenu\" valign=\"middle\" align=\"left\"><span style=\"color: #dddddd\">"
                    + msgs.getMessage("Appl.version")
                    + "</span></td></tr>\n"
                    + "<tr class=\"nciftrtable\">\n<td valign=\"top\">\n<div align=\"center\">\n"
                    + "<a target=\"_blank\" href=\"http://www.cancer.gov/\"><img border=\"0\" src=\"footer_nci.gif\" alt=\"National Cancer Institute Logo\" title=\"National Cancer Institute\"></a>\n"
                    + "<a target=\"_blank\" href=\"http://www.dhhs.gov/\"><img border=\"0\" src=\"footer_hhs.gif\" alt=\"Department of Health and Human Services Logo\" title=\"Department of Health and Human Services\"></a>\n"
                    + "<a target=\"_blank\" href=\"http://www.nih.gov/\"><img border=\"0\" src=\"footer_nih.gif\" alt=\"National Institutes of Health Logo\" title=\"National Institutes of Health\"></a>\n"
                    + "<a target=\"_blank\" href=\"http://www.firstgov.gov/\"><img border=\"0\" src=\"footer_firstgov.gif\" alt=\"FirstGov.gov\" title=\"FirstGov.gov\"></a>\n"
                    + "</div>\n</td>\n</tr>\n</table>\n");
            //            out.print("<hr class=hrf><p style=\"text-align: right;
            // margin-top: 0px\">" + msgs.getMessage("Appl.version") +"</p>");
        }
        catch (IOException ex)
        {
        }
        return EVAL_PAGE;
    }

    private static final long serialVersionUID = -4073456777204188509L;
}