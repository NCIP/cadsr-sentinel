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

              "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#A90101\">\n"
              + "<tr bgcolor=\"#A90101\">\n"
              + "<td valign=\"center\" align=\"left\"><a href=\"http://www.cancer.gov\" target=\"_blank\" alt=\"NCI Logo\">\n"
              + "<img src=\"/cadsrsentinel/images/brandtype.gif\" border=\"0\" alt=\"Brand Type\"></a></td>\n"
              + "<td valign=\"center\" align=\"right\"><a href=\"http://www.cancer.gov\" target=\"_blank\" alt=\"NCI Logo\">\n"
              + "<img src=\"/cadsrsentinel/images/tagline_nologo.gif\" border=\"0\" alt=\"Tagline Nologo\"></a></td></tr>\n"
              + "</table>\n"
              + "<table class=\"secttable\"><colgroup><col /></colgroup><tbody class=\"secttbody\" />\n"
              + "<tr><td><a target=\"_blank\" href=\"http://ncicb.nci.nih.gov/NCICB/infrastructure/cacore_overview/cadsr\"><img style=\"border: 0px solid black\" title=\"NCICB caDSR\" src=\"/cadsrsentinel/images/sentinel_banner.gif\" alt=\"Sentinel Banner\"></a></td></tr>\n"
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
