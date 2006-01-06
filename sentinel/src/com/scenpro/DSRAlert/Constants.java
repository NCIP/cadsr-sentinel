// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/com/scenpro/DSRAlert/Constants.java,v 1.3 2006-01-06 16:14:26 hebell Exp $
// $Name: not supported by cvs2svn $

package com.scenpro.DSRAlert;

/**
 * Constants. Used to insure consistency and avoid misspellings.
 * 
 * @author Larry Hebel
 */

public class Constants
{
    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _ACTBACK           = "back";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _ACTCREATE         = "create";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _ACTEDIT           = "edit";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _ACTDELETE         = "delete";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _ACTLIST           = "list";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _ACTLOGON          = "logon";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _ACTLOGOUT         = "logout";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _ACTNEWFROM        = "newfrom";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _ACTRUN            = "run";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _ACTSAVE           = "save";

    /**
     * The property name for the application version number.
     */
    public static final String _APLVERS           = "Appl.version";
    
    /**
     * The database "name" to display on Alert Reports to identify the source of
     * the data for the report coded in DSRAlert.properties.
     */
    public static final String _DBNAME            = "DB.displayname";

    /**
     * The property name for the database user id password value coded in
     * DSRAlert.properties.
     */
    public static final String _DBPSWD            = "DB.password";

    /**
     * The property name for the database tnsname.ora entry value coded in
     * DSRAlert.properties.
     */
    public static final String _DBTNSNAME         = "DB.tnsname";

    /**
     * The property name for the database user id value coded in
     * DSRAlert.properties.
     */
    public static final String _DBUSER            = "DB.username";

    /**
     * The Struts session id for the message rource.
     */
    public static final String _RESOURCES         = "org.apache.struts.action.MESSAGE";

    /**
     * The literal string used in lists to represent the entire contents, ie
     * All.
     */
    public static final String _STRALL            = "(All)";

    /**
     * The literal string used in lists to represent any change to the
     * attribute.
     */
    public static final String _STRANY            = "(Any Change)";

    /**
     * The literal string used in lists to represent the attribute should be
     * ignored.
     */
    public static final String _STRIGNORE         = "(Ignore)";

    /**
     * The literal string used in lists to represent the attribute is
     * blank or empty.
     */
    public static final String _STRNONE           = "(none)";

    /**
     * Constructor.
     */
    public Constants()
    {
    }
}