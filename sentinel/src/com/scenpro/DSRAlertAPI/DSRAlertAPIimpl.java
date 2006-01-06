/*
 * Copyright (c) 2005 ScenPro, Inc.
 */

// $Header: /share/content/gforge/sentinel/sentinel/src/com/scenpro/DSRAlertAPI/DSRAlertAPIimpl.java,v 1.1 2006-01-06 16:08:58 hebell Exp $
// $Name: not supported by cvs2svn $

package com.scenpro.DSRAlertAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class exists solely to create an instance of the DSRAlertAPI Interface object.
 * Use the static factory() method and then make all requests through the resulting
 * DSRAlertAPI Interface object.
 * 
 * @author Larry Hebel Oct 12, 2005
 */

public class DSRAlertAPIimpl
{
    /**
     * This method will create an implementation instance of the DSRAlertAPI Interface
     * class. All requests to the Sentinel Alert system must be through the interface
     * provided by DSRAlertAPI.
     * 
     * @param url_ The URL to the Sentinel Alert system. For NCI this is
     *      http://cadsrsentinel.nci.nih.gov. This is also stored in the caDSR sbrext.tool_options_view
     *      table as SENTINEL, URL.
     *      
     * @return An instance of the implementation of the DSRAlertAPI interface.
     */
    public static DSRAlertAPI factory(String url_)
    {
        return new SentinelAlertAPI(url_);
    }
    
    /**
     * This method will create an implementation instance of the DSRAlertAPI Interface
     * class. All requests to the Sentinel Alert system must be through the interface
     * provided by DSRAlertAPI.
     * 
     * @param conn_ The database connection to retrieve the URL from the Tool
     *      Options table.
     * @return null if the SENTINEL, URL entry is missing from the options table or any
     *      other database error occurs. Otherwise an instance of the implementation of
     *      the DSRAlertAPI interface.
     */
    public static DSRAlertAPI factory(Connection conn_)
    {
        String select = "select value from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' and property = 'URL'";
        
        String rc = null;

        try
        {
            PreparedStatement pstmt = conn_.prepareStatement(select);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                rc = rs.getString(1);
            rs.close();
            pstmt.close();
        }
        catch (SQLException ex)
        {
            int errorCode = ex.getErrorCode();
            System.err.println("DSRAlertAPIimpl 1: " + errorCode + ": " + select
                + "\n\n" + ex.toString());
        }

        return (rc == null) ? null : new SentinelAlertAPI(rc);
    }
}
