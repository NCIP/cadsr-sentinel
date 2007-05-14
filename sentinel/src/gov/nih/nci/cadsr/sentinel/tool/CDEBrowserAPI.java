/*
 * Copyright (c) 2005 ScenPro, Inc.
 */

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tool/CDEBrowserAPI.java,v 1.12 2007-05-14 14:30:30 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * This class encapsulates remote calls to the CDE Browser.
 * 
 * @author Larry Hebel Oct 20, 2005
 */

public class CDEBrowserAPI
{
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
        _isPresent = false;
        String select = "select value from sbrext.tool_options_view_ext "
            + "where tool_name = 'CDEBrowser' and property = 'URL'";
        
        try
        {
            PreparedStatement pstmt = conn_.prepareStatement(select);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                _url = rs.getString(1);
                _isPresent = true;
            }
            rs.close();
            pstmt.close();
        }
        catch (SQLException ex)
        {
            // Ooops...
            int errorCode = ex.getErrorCode();
            String errorMsg = errorCode + ": " + select
                + "\n\n" + ex.toString();
            _logger.error(errorMsg);
        }
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
    
    private String _url;
    
    private boolean _isPresent;
    
    private static final Logger _logger = Logger.getLogger(CDEBrowserAPI.class.getName());
}
