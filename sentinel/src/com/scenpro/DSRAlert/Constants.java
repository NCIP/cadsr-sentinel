// Copyright (c) 2004 ScenPro, Inc.

package com.scenpro.DSRAlert;

/**
 * Constants. Used to insure consistency and avoid misspellings.
 * 
 * @author Larry Hebel
 */

public class Constants
{
    /**
     * The Auto Process administrator email address coded in
     * DSRAlert.properties.
     */
    public static final String _ADMINEMAIL      = "AutoProcess.admin.email";

    /**
     * The Auto Process email introduction for recipient distributed messages
     * coded in DSRAlert.properties.
     */
    public static final String _ADMININTRO      = "AutoProcess.intro";

    /**
     * The Auto Process email introduction addendum when errors occurred
     * generating the report file coded in DSRAlert.properties.
     */
    public static final String _ADMININTROERROR = "AutoProcess.introError";

    /**
     * The Auto Process administrator name/title to appear in the "From" field
     * on recipient distributed emails coded in DSRAlert.properties.
     */
    public static final String _ADMINNAME       = "AutoProcess.admin.name";

    /**
     * The literal string used in lists to represent the entire contents, ie
     * All.
     */
    public static final String _ALL             = "(All)";

    /**
     * The literal string used in lists to represent any change to the
     * attribute.
     */
    public static final String _ANY             = "(Any Change)";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _BACK            = "back";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _CREATE          = "create";

    /**
     * The property name for the database driver value coded in
     * DSRAlert.properties.
     */
    public static final String _DBDRIVER        = "DB.driver";

    /**
     * The database "name" to display on Alert Reports to identify the source of
     * the data for the report coded in DSRAlert.properties.
     */
    public static final String _DBNAME          = "DB.displayname";

    /**
     * The property name for the database user id password value coded in
     * DSRAlert.properties.
     */
    public static final String _DBPSWD          = "DB.password";

    /**
     * The property name for the database tnsname.ora entry value coded in
     * DSRAlert.properties.
     */
    public static final String _DBTNSNAME       = "DB.tnsname";

    /**
     * The property name for the database user id value coded in
     * DSRAlert.properties.
     */
    public static final String _DBUSER          = "DB.username";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _EDIT            = "edit";

    /**
     * The Auto Process email password for connection to the email host coded in
     * DSRAlert.properties.
     */
    public static final String _EMAILPSWD       = "AutoProcess.emailpassword";

    /**
     * The Auto Process email server host IP or Name for message distribution
     * coded in DSRAlert.properties.
     */
    public static final String _EMAILHOST       = "AutoProcess.emailhost";

    /**
     * The Auto Process email user id for connection to the email host coded in
     * DSRAlert.properties.
     */
    public static final String _EMAILUSER       = "AutoProcess.emailusername";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _DELETE          = "delete";

    /**
     * The Auto Process HTTP prefix string for links set to Alert email
     * recipients coded in DSRAlert.properties
     */
    public static final String _HTTP            = "AutoProcess.http";

    /**
     * The literal string used in lists to represent the attribute should be
     * ignored.
     */
    public static final String _IGNORE          = "(Ignore)";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _LIST            = "list";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _LOGON           = "logon";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _NEWFROM         = "newfrom";

    /**
     * The Struts session id for the message rource.
     */
    public static final String _RESOURCES       = "org.apache.struts.action.MESSAGE";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _RUN             = "run";

    /**
     * The action literal defined for consistency and to avoid spelling errors.
     */
    public static final String _SAVE            = "save";

    /**
     * The Auto Process CSS style definitions used for the Alert report coded in
     * DSRAlert.properties.
     */
    public static final String _STYLE           = "AutoProcess.style";

    /**
     * The Auto Process email subject line for all distribution to recipients
     * and administrators coded in DSRAlert.properties.
     */
    public static final String _SUBJECT         = "AutoProcess.subject";

    /**
     * The Auto Process working forder for temporary files and report output.
     * Must be relative to the machine on which the process executes and coded
     * in DSRAlert.properties.
     */
    public static final String _WORKING         = "AutoProcess.workingfolder";

    /**
     * Constructor.
     */
    public Constants()
    {
    }
}