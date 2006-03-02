/*
 * Copyright (c) 2005 ScenPro, Inc.
 */

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/util/DSRAlertV1.java,v 1.8 2006-03-02 22:57:59 hardingr Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class is used internally to the DSRAlert package and should not be referenced
 * externally. Use the DSRAlertImpl.factory() method and the DSRAlert Interface for
 * requests to the Sentinel Alert system.
 * 
 * @author Larry Hebel Oct 12, 2005
 */

public class DSRAlertV1 implements DSRAlert
{
    /**
     * Constructor
     * 
     * @param url_ The url to the Sentinel Tool
     */
    public DSRAlertV1(String url_)
    {
        _alertName = "";
        if (url_ == null || url_.length() == 0)
        {
            url_ = "http://cadsrsentinel.nci.nih.gov/cadsrsentinel/do/";
            return;
        }
        
        // Get the usable parts of the url.
        String tokens[] = url_.split("/");
        int tndx = 0;
        
        // Must start with http:
        _url = "http://";
        if (tndx < tokens.length && tokens[tndx].compareToIgnoreCase("http:") == 0)
            ++tndx;
        if (tndx < tokens.length && (tokens[tndx] == null || tokens[tndx].length() == 0))
            ++tndx;
        
        // Copy everything up to the cadsrsentinel part.
        for (; tndx < tokens.length && tokens[tndx].compareToIgnoreCase("cadsrsentinel") != 0; ++tndx)
        {
            _url = _url + tokens[tndx] + "/";
        }
        
        // Be sure we end with the proper suffix.
        _url = _url + "cadsrsentinel/do/";
    }

    /**
     * Create an Alert Definition for the specified ID and owned by the specified User.
     * 
     * @param user_ The user id as used to Login to the Sentinel Tool.
     * @param idseq_ The database entity ID, a 36 character value.
     * @return DSRAlert.RC_EXISTS if the Alert already exists, DSRAlert.RC_CREATED if a new Alert is created,
     * otherwise DSRAlert.RC_FAILED (refer to the web server log for the response message).
     */
    public int createAlert(String user_, String idseq_)
    {
        String url = _url + "crf?version=" + _version + "&user=" + user_ + "&idseq=" + idseq_;
        // System.err.println("DSRAlert 0: [" + url + "]");

        int rc = DSRAlert.RC_FAILED;
        _alertName = "";
        if (user_ != null && user_.length() > 0 &&
            idseq_ != null && idseq_.length() > 0)
        {
            try
            {
                URL rps = new URL(url);
                HttpURLConnection http = (HttpURLConnection) rps.openConnection();
                http.setUseCaches(false);
                switch (http.getResponseCode())
                {
                    case HttpURLConnection.HTTP_NOT_IMPLEMENTED: rc = DSRAlert.RC_INCOMPATIBLE; break;
                    case HttpURLConnection.HTTP_CREATED: rc = DSRAlert.RC_CREATED; break;
                    case HttpURLConnection.HTTP_OK: rc = DSRAlert.RC_EXISTS; break;
                    case HttpURLConnection.HTTP_FORBIDDEN: rc = DSRAlert.RC_UNAUTHORIZED; break;
                    default:
                        rc = DSRAlert.RC_FAILED;
                        System.err.println("DSRAlert 3: " + http.getResponseMessage());
                        break;
                }
                
                // Get the Alert Name returned from the create service.
                if (rc >= 0)
                {
                    BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
                    _alertName = in.readLine().trim();
                }
                http.disconnect();
            }
            catch(MalformedURLException ex)
            {
                System.err.println("DSRAlert 1: [" + url + "] " + ex.toString());
            }
            catch(IOException ex)
            {
                System.err.println("DSRAlert 2: [" + url + "] " + ex.toString());
            }
        }
        return rc;
    }
    
    /**
     * Get the Alert Name for the latest method call if available.
     * 
     * @return The alert name if available, otherwise and empty string.
     */
    public String getAlertName()
    {
        return _alertName;
    }
    
    private String _url;
    
    private String _alertName;
    
    private static final String _version = "1";
}
