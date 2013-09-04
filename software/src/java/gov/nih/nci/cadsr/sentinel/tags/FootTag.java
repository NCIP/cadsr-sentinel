/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tags/FootTag.java,v 1.3 2009-07-24 15:34:50 davet Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tags;

import gov.nih.nci.cadsr.sentinel.database.DBAlert;
import gov.nih.nci.cadsr.sentinel.database.DBAlertUtil;
import gov.nih.nci.cadsr.sentinel.tool.Constants;
import gov.nih.nci.cadsr.sentinel.ui.AlertPlugIn;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;
import org.jboss.Version;

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
        	
        	ServletContext sc = pageContext.getServletContext();        	
        	AlertPlugIn api = (AlertPlugIn) sc.getAttribute(DBAlert._DATASOURCE);        	
        	String privacyUrl = api.getPrivacyUrl();
        	if (privacyUrl == null){
        		
        		DBAlert db = DBAlertUtil.factory();
        		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        		String _userid = api.getUser();
        		String _pswd = api.getPswd();

        		int msgnum = db.open(request, _userid, _pswd);
        		if (msgnum == 0){
        			privacyUrl = db.selectPrivacyNoticeUrl();
        		}else {
        			privacyUrl = "http://www.nih.gov/about/privacy.htm";
        		}
        		db.close();
        		api.setPrivacyUrl(privacyUrl);
        	}
        	
            String jboss = Version.getInstance().getMajor() + "." + Version.getInstance().getMinor() + "." + Version.getInstance().getRevision();
            MessageResources msgs = (MessageResources) pageContext
                .findAttribute(Globals.MESSAGES_KEY);
            JspWriter out = pageContext.getOut();
            out
                .print("<table class=\"table3\"><colgroup></colgroup><tbody class=\"secttbody\" />\n"
                    + "<tr><td class=\"ncifmenu\"><span style=\"color: #dddddd\">"
                    + msgs.getMessage(Constants._APLVERS).replace(" ", "&nbsp;")
                    + "&nbsp;(" + jboss + "/" + System.getProperty("java.version") + ")"
                    + "</span></td></tr>\n"
                    + "<tr>\n<td class=\"nciftrtable\">\n"
                    + "<a href=\"mailto:ncicb@pop.nci.nih.gov?subject=caDSR%20Sentinel%20Tool\"><img border=\"0\" src=\"/cadsrsentinel/images/email_icon.gif\" alt=\"Email NCI Help Desk\" title=\"Email NCI Help Desk\"></a>\n"
                    + "<a target=\"_blank\" href=\"http://www.cancer.gov/\"><img border=\"0\" src=\"/cadsrsentinel/images/footer_nci.gif\" alt=\"National Cancer Institute Logo\" title=\"National Cancer Institute\"></a>\n"
                    + "<a target=\"_blank\" href=\"http://www.dhhs.gov/\"><img border=\"0\" src=\"/cadsrsentinel/images/footer_hhs.gif\" alt=\"Department of Health and Human Services Logo\" title=\"Department of Health and Human Services\"></a>\n"
                    + "<a target=\"_blank\" href=\"http://www.nih.gov/\"><img border=\"0\" src=\"/cadsrsentinel/images/footer_nih.gif\" alt=\"National Institutes of Health Logo\" title=\"National Institutes of Health\"></a>\n"
                    + "<a target=\"_blank\" href=\"http://www.usa.gov/\"><img border=\"0\" src=\"/cadsrsentinel/images/footer_usagov.gif\" alt=\"USA.gov\" title=\"USA.gov\"></a>\n"
                    + "<a target=\"_blank\" href=\""+privacyUrl+"\">Privacy Notice</a>\n"
                    + "</td>\n</tr>\n</table>\n");
        }
        catch (IOException ex)
        {
        }
        return EVAL_PAGE;
    }

    private static final long serialVersionUID = -4073456777204188509L;
}