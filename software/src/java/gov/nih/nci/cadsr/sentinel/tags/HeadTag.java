/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tags/HeadTag.java,v 1.1 2008-11-07 14:11:10 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tags;

import java.io.IOException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.struts.util.MessageResources;
import org.apache.struts.Globals;

/**
 * This is used to place a standard header on every JSP in the Sentinel Tool
 * interface.
 *
 * @author Larry Hebel Nov 7, 2005
 */

public class HeadTag extends TagSupport
{
    /**
     * Constructor.
     */
    public HeadTag()
    {
    }

    /**
     * Set the name of the page.
     *
     * @param key_ The name of the page also used as the Title.
     */
    public void setKey(String key_)
    {
        _key = key_;
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
                .findAttribute(Globals.MESSAGES_KEY);
            JspWriter out = pageContext.getOut();
           out.print(

              "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n"
              + "<tr>\n"
              + "<td width=\"25%\" valign=\"center\" align=\"left\"><a href=\"https://www.cancer.gov\" target=\"_blank\" alt=\"NCI Logo\">\n"
              + "<img src=\"/cadsrsentinel/images/CBIIT-36px-Logo-COLOR_contrast.png\" border=\"0\" alt=\"Brand Type\"></a></td>"
              + "<td align=\"center\"><img style=\"border: 0px solid black\" title=\"NCICB caDSR\" src=\"/cadsrsentinel/images/sentinel_banner_2.gif\" alt=\"Sentinel Banner\"></td>\n"
              + "<td align=\"right\"><a target=\"_blank\" href=\"https://www.nih.gov\">U.S. National Institutes of Health</a></td></tr>\n"
              + "<td valign=\"center\" align=\"right\"><a href=\"https://www.cancer.gov\" target=\"_blank\" alt=\"NCI Logo\">\n"              
              + "</table>\n"
              + "<table class=\"secttable\"><colgroup><col /></colgroup><tbody class=\"secttbody\" />\n"
              + "<tr><td><a target=\"_blank\" href=\"https://cbiit.nci.nih.gov/ncip/biomedical-informatics-resources/interoperability-and-semantics/metadata-and-models\"><img style=\"border: 0px solid black\" title=\"NCICB caDSR\" src=\"/cadsrsentinel/images/caDSR_logo2_contrast.png\" alt=\"caDSR Logo\"></a></td></tr>\n"
              + "<tr><td align=\"center\"><p class=\"ttl18\">" + msgs.getMessage(_key) + "</p></td></tr>\n"
              + "</table>\n"
              );

        }
        catch (IOException ex)
        {
        }
        return EVAL_PAGE;
    }

    private String _key;

    private static final long serialVersionUID = 3350479920470665919L;
}
