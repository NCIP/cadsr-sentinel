/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

/*
 * Copyright (c) 2005 ScenPro, Inc.
 */

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/util/DSRAlertImpl.java,v 1.14 2008-07-14 14:52:46 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.util;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * This class exists solely to create an instance of the DSRAlert Interface object.
 * Use the static factory() method and then make all requests through the resulting
 * DSRAlert Interface object.
 * 
 * @author Larry Hebel Oct 12, 2005
 */

public class DSRAlertImpl
{
    /**
     * This method will create an implementation instance of the DSRAlert Interface
     * class. All requests to the Sentinel Alert system must be through the interface
     * provided by DSRAlert.
     * 
     * @param url_ The URL to the Sentinel Alert system. For NCI this is
     *      http://cadsrsentinel.nci.nih.gov. This is also stored in the caDSR
     *      within the Tool Options table.
     *      
     * @return An instance of the implementation of the DSRAlert interface.
     */
    public static DSRAlert factory(String url_)
    {
        return new DSRAlertV1(url_);
    }
    
    /**
     * This method will create an implementation instance of the DSRAlert Interface
     * class. All requests to the Sentinel Alert system must be through the interface
     * provided by DSRAlert.
     * 
     * @param conn_ The database connection to retrieve the URL from the Tool
     *      Options table.
     * @return null if the SENTINEL, URL entry is missing from the options table or any
     *      other database error occurs. Otherwise an instance of the implementation of
     *      the DSRAlert interface.
     */
    public static DSRAlert factory(Connection conn_)
    {
        String select = "select value from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' and property = 'URL' ";
        
        String url = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            pstmt = conn_.prepareStatement(select);
            rs = pstmt.executeQuery();
            if (rs.next())
                url = rs.getString(1);
        }
        catch (SQLException ex)
        {
            int errorCode = ex.getErrorCode();
            _logger.error("DSRAlertImpl: " + errorCode + ": " + select
                + "\n\n" + ex.toString());
        }
        finally
        {
            if (rs != null)
            {
                try { rs.close(); } catch(Exception ex) { }
            }
            if (pstmt != null)
            {
                try { pstmt.close(); } catch(Exception ex) { }
            }
        }

        return (url == null) ? null : new DSRAlertV1(url);
    }
    
    private static final Logger _logger = Logger.getLogger(DSRAlertImpl.class.getName());
}
