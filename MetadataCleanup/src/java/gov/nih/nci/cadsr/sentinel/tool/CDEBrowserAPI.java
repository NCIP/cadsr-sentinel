/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

/*
 * Copyright (c) 2005 ScenPro, Inc.
 */

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tool/CDEBrowserAPI.java,v 1.15 2008-07-14 14:52:46 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tool;

import java.sql.Connection;

import gov.nih.nci.cadsr.sentinel.database.DBAlert;
import gov.nih.nci.cadsr.sentinel.database.DBAlertUtil;

/**
 * This class encapsulates remote calls to the CDE Browser.
 * 
 * @author Larry Hebel Oct 20, 2005
 */

public class CDEBrowserAPI
{
    private String _url;
    
    private boolean _isPresent;

    /**
     * Constructor.
     * 
     * @param url_ The server URL for access to the CDE Browser.
     */
    public CDEBrowserAPI(String url_)
    {
        _url = url_;
        _isPresent = true;
    }
    
    /**
     * Constructor.
     * 
     * @param conn_ A connection to the database.
     */
    public CDEBrowserAPI(Connection conn_)
    {
        DBAlert db = DBAlertUtil.factory();
        _url = db.getCdeBrowserUrl(conn_);
        _isPresent = (_url != null);
    }
    
    /**
     * Form a URL suitable for use as the HREF attribute in an HTML Anchor tab &lt;A...&gt;
     * 
     * @param idseq_ The Data Element database ID to include in the URL.
     * @return The URL string suitable to open the details of a data element.
     */
    public String getLinkForDE(String idseq_)
    {
        return _url
            + "/CDEBrowser/search?dataElementDetails=9&p_de_idseq="
            + idseq_
            + "&PageId=DataElementsGroup&queryDE=yes&FirstTimer=0";
    }
    
    /**
     * Form a URL suitable for use as the HREF attribute in an HTML Anchor tab &lt;A...&gt;
     * 
     * @param idseq_ The Object Class database ID to include in the URL.
     * @return The URL string suitable to open the details of an object class.
     */
    public String getLinkForOC(String idseq_)
    {
        return _url
            + "/CDEBrowser/ocbrowser/ocDetailsAction.do?method=getObjectClass&objectClassIdseq="
            + idseq_;
    }
    
    /**
     * Report if the CDE Browser is configured in the Tool Options.
     * 
     * @return true if the CDE Browser URL was found.
     */
    public boolean isPresent()
    {
        return _isPresent;
    }
}
