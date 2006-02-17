/*
 * Copyright (c) 2005 ScenPro, Inc.
 */

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/util/DSRAlert.java,v 1.6 2006-02-17 21:45:05 hardingr Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.util;

/**
 * This interface class defines features and methods which may be accessed 
 * programmtically via Java. To obtain an interface instance use the DSRAlertImpl.factory()
 * method.
 * 
 * @author Larry Hebel Oct 12, 2005
 */

public interface DSRAlert
{
    /**
     * Create an Alert Definition for the specified ID and owned by the specified User.
     * 
     * @param user_ The user id as used to Login to the Sentinel Tool.
     * @param idseq_ The database entity ID, a 36 character value.
     * @return DSRAlert.RC_EXISTS if the Alert already exists, DSRAlert.RC_CREATED if a new Alert is created,
     * otherwise DSRAlert.RC_FAILED (refer to the web server log for the response message).
     */
    public int createAlert(String user_, String idseq_);
    
    /**
     * Get the Alert Name for the latest method call if available.
     * 
     * @return The alert name if available, otherwise and empty string.
     */
    public String getAlertName();

    /**
     * An Alert Definition already exists and it was not necessary to create a new Alert.
     */
    public static final int RC_EXISTS = 0;
    
    /**
     * A new Alert was successfully created per the request.
     */
    public static final int RC_CREATED = 1;
    
    /**
     * The request failed. Check the arguments on the method call. Check the web server log for
     * additional information.
     */
    public static final int RC_FAILED = -1;
    
    /**
     * The request failed. The server provided in the DSRAlertImpl.factory() is not compatible
     * with this version of the client.
     */
    public static final int RC_INCOMPATIBLE = -2;
    
    /**
     * Unauthorized request.
     */
    public static final int RC_UNAUTHORIZED = -3;
}
