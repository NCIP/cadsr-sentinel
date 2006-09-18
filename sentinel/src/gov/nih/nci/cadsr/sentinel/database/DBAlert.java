// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/database/DBAlert.java,v 1.2 2006-09-18 21:10:50 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.database;

import gov.nih.nci.cadsr.sentinel.tool.ACData;
import gov.nih.nci.cadsr.sentinel.tool.AlertRec;
import gov.nih.nci.cadsr.sentinel.tool.ConceptItem;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

/**
 * Encapsulate all database access to the tables relating to the Sentinel Alert
 * definitions.
 * <p>
 * For all access, the SQL statements are NOT placed in the properties file as
 * internationalization and translation should not affect them. We also want to
 * ease the maintenance by keeping the SQL with the database execute function
 * calls. If some SQL becomes duplicated, a single method with appropriate
 * parameters should be created to avoid difficulties with changing the SQL
 * statements over time as the table definitions evolve.
 * <p>
 * Start with setupPool() which only needs to be executed once. Then open() and
 * close() every time a new DBAlert object is created.
 * <p>
 * Also, just a reminder, all JDBC set...() and get...() methods use 1 (one)
 * based indexing unlike the Java language which uses 0 (zero) based.
 * 
 * @author Larry Hebel
 * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#open(HttpServletRequest, String, String) open() with HTTP request
 * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#open(ServletContext, String, String) open() with Servlet Context
 * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#open(String, String, String) open()
 * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#close close()
 */
public interface DBAlert
{

    /**
     * Required prior to using any other methods within this class. This method
     * checks for the existence of the pool attached to the Servlet Context.
     * Once the pool is successfully created subsequent invocations perform no
     * action. The method is static and synchronized to allow for possible
     * multiple invocations of the Sentinel Tool simultaneously. Although the
     * data referenced is not static we don't want to take the chance that the
     * ServletContext.getAttribute() is called, we loose the time slice and upon
     * return from the VM one invocation thinks the pool is missing when another
     * invocation has just successfully created it. This is only called from the
     * Logon Action currently so the overhead inherit with synchronized
     * functions is minimized.
     * <p>
     * To use this from non-browser servlet logic use the method which requires
     * the driver as the first argument.
     * 
     * @param session_
     *        The session object.
     * @param dsurl_
     *        The URL entry for the desired database.
     * @param username_
     *        The default database user logon id.
     * @param password_
     *        The password to match the user.
     * @return 0 if successful, otherwise the error code.
     */
    public  int setupPool(HttpSession session_,
        String dsurl_, String username_, String password_);

    /**
     * Required prior to using any other methods within this class. This method
     * checks for the existence of the pool attached to the Servlet Context.
     * Once the pool is successfully created subsequent invocations perform no
     * action. The method is static and synchronized to allow for possible
     * multiple invocations of the Sentinel Tool simultaneously. Although the
     * data referenced is not static we don't want to take the chance that the
     * ServletContext.getAttribute() is called, we loose the time slice and upon
     * return from the VM one invocation thinks the pool is missing when another
     * invocation has just successfully created it. This is only called from the
     * Logon Action currently so the overhead inherit with synchronized
     * functions is minimized.
     * <p>
     * To use this from non-browser servlet logic use the method which requires
     * the driver as the first argument.
     * 
     * @param request_
     *        The servlet request object.
     * @param dsurl_
     *        The URL entry for the desired database.
     * @param username_
     *        The default database user logon id.
     * @param password_
     *        The password to match the user.
     * @return 0 if successful, otherwise the error code.
     */
    public int setupPool(HttpServletRequest request_,
        String dsurl_, String username_, String password_);

    /**
     * Create a connection from the pool. This is not part of the constructor to
     * allow the method to have return codes that can be interrogated by the
     * caller. If Exception are desired, appropriate wrapper methods can be
     * created to provide both features and give the caller the flexibility to
     * use either without additional coding.
     * <p>
     * Be sure to call DBAlert.close() to complete the request before returning
     * to the client or loosing the object focus in the caller to "new
     * DBAlert()".
     * 
     * @param sc_
     *        The servlet context which holds the data source pool reference
     *        created by the DBAlert.setupPool() method.
     * @param user_
     *        The database user logon id.
     * @param pswd_
     *        The password to match the user.
     * @return 0 if successful, otherwise the error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#close close()
     */
    public int open(ServletContext sc_, String user_, String pswd_);

    /**
     * Create a connection from the pool. This is not part of the constructor to
     * allow the method to have return codes that can be interrogated by the
     * caller. If Exception are desired, appropriate wrapper methods can be
     * created to provide both features and give the caller the flexibility to
     * use either without additional coding.
     * <p>
     * Be sure to call DBAlert.close() to complete the request before returning
     * to the client or loosing the object focus in the caller to "new
     * DBAlert()".
     * 
     * @param ds_
     *        The datasource for database connections.
     * @param user_
     *        The database user logon id.
     * @param pswd_
     *        The password to match the user.
     * @return 0 if successful, otherwise the error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#close close()
     */
    public int open(DataSource ds_, String user_, String pswd_);

    /**
     * Open a single simple connection to the database. No pooling is necessary.
     * 
     * @param dsurl_
     *        The URL entry describing the database location.
     * @param user_
     *        The user id.
     * @param pswd_
     *        The password which must match 'user_'.
     * @return The database error code.
     */
    public int open(String dsurl_, String user_, String pswd_);

    /**
     * Create a connection from the pool. This is not part of the constructor to
     * allow the method to have return codes that can be interrogated by the
     * caller. If Exception are desired, appropriate wrapper methods can be
     * created to provide both features and give the caller the flexibility to
     * use either without additional coding.
     * <p>
     * Be sure to call DBAlert.close() to complete the request before returning
     * to the client or loosing the object focus in the caller to "new
     * DBAlert()".
     * 
     * @param request_
     *        The servlet request object.
     * @param user_
     *        The database user logon id.
     * @param pswd_
     *        The password to match the user.
     * @return 0 if successful, otherwise the error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#close close()
     */
    public int open(HttpServletRequest request_, String user_, String pswd_);

    /**
     * Required upon a successful return from open. When all database access is
     * completed for this user request. To optimize the database access, all
     * methods which perform actions that require a commmit only set a flag. It
     * is in the close() method the flag is interrogated and the commit actually
     * occurs.
     * 
     * @return 0 if successful, otherwise the error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#open(HttpServletRequest, String, String) open() with HTTP request
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#open(ServletContext, String, String) open() with Servlet Context
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#open(String, String, String) open()
     */
    public int close();
    
    /**
     * Get the database connection opened for this object.
     * 
     * @return java.sql.Connection opened by this object.
     */
    public Connection getConnection();

    /**
     * Retrieves the abbreviated list of all alerts. The AlertRec objects
     * returned are not fully populated with all the details of each alert. Only
     * basic information such as the database id, name, creator and a few other
     * basic properties are guaranteed.
     * 
     * @param user_
     *        The user id with which to qualify the results. This must be as it
     *        appears in the "created_by" column of the Alert tables. If null is
     *        used, a list of all Alerts is retrieved.
     * @return The array of Alerts retrieved.
     */
    public AlertRec[] selectAlerts(String user_);

    /**
     * Retrieve a full single Alert definition. All data elements of the
     * AlertRec will be populated to reflect the database content.
     * 
     * @param id_
     *        The database id (al_idseq) of the Alert definitions.
     * @return A complete definition record if successful or null if an error
     *         occurs.
     */
    public AlertRec selectAlert(String id_);


    /**
     * Perform an update on the complete record. No attempt is made to isolate
     * the specific changes so many times values will not actually be changed.
     * 
     * @param rec_
     *        The record containing the updated information. All data elements
     *        must be populated and correct.
     * @return 0 if successful, otherwise the error code.
     */
    public int updateAlert(AlertRec rec_);

    /**
     * Delete the Alert Definitions specified by the caller. The values must be
     * existing al_idseq values within the Alert table.
     * 
     * @param list_
     *        The al_idseq values which identify the definitions to delete.
     *        Other dependant tables in the database will automatically be
     *        cleaned up via cascades and triggers.
     * @return 0 if successful, otherwise the error code.
     */
    public int deleteAlerts(Vector list_);

    /**
     * Delete the Alert Definitions specified by the caller. The values must be
     * existing al_idseq values within the Alert table.
     * 
     * @param id_
     *        The al_idseq value which identifies the definition to delete.
     *        Other dependant tables in the database will automatically be
     *        cleaned up via cascades and triggers.
     * @return 0 if successful, otherwise the error code.
     */
    public int deleteAlert(String id_);

    /**
     * Delete the Alert Definitions specified by the caller. The values must be
     * existing al_idseq values within the Alert table.
     * 
     * @param list_
     *        The al_idseq values which identify the definitions to delete.
     *        Other dependant tables in the database will automatically be
     *        cleaned up via cascades and triggers.
     * @return 0 if successful, otherwise the error code.
     */
    public int deleteAlerts(String list_[]);

    /**
     * Build the summary text from the content of the alert definition.
     * 
     * @param rec_
     *        The alert definition.
     * @return The Alert Definition summary.
     */
    public String buildSummary(AlertRec rec_);

    /**
     * Set the owner of the Alert Definition.
     * 
     * @param rec_ The Alert Definition with the new creator already set.
     */
    public void setOwner(AlertRec rec_);

    /**
     * Get the type of the AC id from the database.
     * @param id_ The AC id.
     * @return The [0] is the type and the [1] is the name of the AC.
     */
    public String [] getACtype(String id_);
    
    /**
     * Look for an Alert owned by the user with a Query which
     * references the id specified.
     * 
     * @param id_ The Context, Form, CS, etc ID_SEQ value.
     * @param user_ The user who should own the Alert if it exists.
     * @return true if the user already watches the id, otherwise false.
     */
    public String checkQuery(String id_, String user_);
    
    /**
     * Check the specified user id for Sentinel Tool Administration privileges.
     * 
     * @param user_ The user id as used during Sentinel Tool Login.
     * @return true if the user has administration privileges, otherwise false.
     */
    public boolean checkToolAdministrator(String user_);

    /**
     * Retrieve the CDE Browser URL if available.
     * 
     * @return The URL string.
     */
    public String selectBrowserURL();

    /**
     * Retrieve the Report Threshold
     * 
     * @return The number of rows to allow in a report.
     */
    public int selectReportThreshold();
    
    /**
     * Perform an insert of a new record. The record number element of the class
     * is not used AND it is not returned by this method. All other elements
     * must be complete and correct.
     * 
     * @param rec_
     *        The Alert definition to insert into the database table.
     * @return 0 if successful, otherwise the error code.
     */
    public int insertAlert(AlertRec rec_);

    /**
     * Insert a DE, DEC or VD into the user reserved CSI to be monitored.
     * 
     * @param idseq_ the database id of the AC to be monitored.
     * @param user_ the user id for the reserved CSI
     * @return the id of the CSI if successful, null if a problem.
     */
    public String insertAC(String idseq_, String user_);
    
    /**
     * Retrieve a more user friendly version of the user id.
     * 
     * @param id_
     *        The id as would be entered at logon.
     * @return null if the user id was not found in the sbr.user_accounts table,
     *         otherwise the 'name' value of the matching row.
     */
    public String selectUserName(String id_);
    
    /**
     * Return the CON_IDSEQ for referenced (used) concepts.
     * 
     * @return the con_idseq list
     */
    public String[] selectUsedConcepts();
    
    /**
     * Return the CON_IDSEQ for all concepts.
     * 
     * @return the con_idseq list
     */
    public String[] selectAllConcepts();
    
    /**
     * Retrieve the EVS properties in the tool options table
     * 
     * @return the array of properties.
     */
    public DBProperty[] selectEVSVocabs();

    /**
     * Select all the caDSR Concepts
     * 
     * @return the Concepts
     */
    public Vector<ConceptItem> selectConcepts();

    /**
     * Retrieve all the context id's for which a specific user has write
     * permission.
     * 
     * @param user_
     *        The user id as stored in user_accounts_view.ua_name.
     * @return The array of context id values.
     */
    public String[] selectContexts(String user_);

    /**
     * Retrieve the list of contexts for which a user has write permission.
     * 
     * @param user_
     *        The user id as stored in user_accounts_view.ua_name.
     * @return The concatenated comma separated string listing the context
     *         names.
     */
    public String selectContextString(String user_);

    /**
     * Retrieve the list of all users from the database with a suffix of the
     * context names for which each has write permission. An asterisk following
     * the name indicates the email address is missing.
     * <p>
     * The getUsers, getUserList and getUserVals are a set of methods that must
     * be used in a specific way. The getUsers() method is called first to
     * populate a set of temporary data elements which can be retrieved later.
     * The getUserList() method accesses the user names of the returned data and
     * subsequently sets the data element to null so the memory may be
     * reclaimed. The getUserVals() method accesses the user ids of the returned
     * data and subsequently sets the data element to null so the memory may be
     * reclaimed. Consequently getUsers() must be called first, followed by
     * either getUserList() or getUserVals(). Further getUserList() and
     * getUserVals() should be called only once after each invocation of
     * getUsers() as additional calls will always result in a null return. See
     * the comments for these other methods for more details.
     * 
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUserList getUserList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUserVals getUserVals()
     */
    public int getUsers();

    /**
     * Retrieve the valid user list. The method getUsers() must be called first.
     * Once this method is used the internal copy is deleted to reclaim the
     * memory space.
     * 
     * @return An array of strings from the sbr.user_accounts.name column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     */
    public String[] getUserList();

    /**
     * Retrieve the list of users exempt from Context Curator broadcasts. The
     * method getUsers() must be called first. Once this method is used the
     * internal copy is deleted to reclaim the memory space.
     * 
     * @return A comma separated list of names.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     */
    public String getUserExempts();

    /**
     * Retrieve the valid user list. The method getUsers() must be called first.
     * Once this method is used the internal copy is deleted to reclaim the
     * memory space.
     * 
     * @return An array of strings from the sbr.user_accounts.ua_name column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     */
    public String[] getUserVals();

    /**
     * Retrieve the Context names and id's from the database. This follows the
     * pattern documented with getUsers().
     * 
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getGroupList getGroupList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getGroupVals getGroupVals()
     */
    public int getGroups();

    /**
     * Retrieve the valid context list. The method getGroups() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     * 
     * @return An array of strings from the sbr.contexts_view.name column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getGroups getGroups()
     */
    public String[] getGroupList();

    /**
     * Retrieve the valid context id list. The method getGroups() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     * 
     * @return An array of strings from the sbr.contexts_view.conte_idseq column
     *         and prefixed with a '/' character.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getGroups getGroups()
     */
    public String[] getGroupVals();

    /**
     * Get the list of unused property records.
     * 
     * @return The list of name, public id and version
     */
    public String[] reportUnusedProperties();

    /**
     * Get the list of unused concepts
     * 
     * @param ids_ the list of unused concept ids
     * @return the list of name, public id and version
     */
    public String[] reportUnusedConcepts(String[] ids_);
    
    /**
     * Get the list of unused data element concept records.
     * 
     * @return The list of name, public id and version
     */
    public String[] reportUnusedDEC();

    /**
     * Get the list of unused object class records.
     * 
     * @return The list of name, public id and version
     */
    public String[] reportUnusedObjectClasses();
    
    /**
     * Get the list of Data Elements which do not have question text and are referenced by a Form.
     * 
     * @return the list of name, public id and version.
     */
    public String[] reportMissingQuestionText();
    
    /**
     * Get the list of Administered Component which do not have a public id.
     * 
     * @return the list of ac type, name, and idseq.
     */
    public String[] reportMissingPublicID();
    
    /**
     * Retrieve the Context names and id's from the database. Follows the
     * pattern documented in getUsers().
     * 
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getContextList getContextList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getContextVals getContextVals()
     */
    public int getContexts();

    /**
     * Retrieve the valid context list. The method getGroups() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     * 
     * @return An array of strings from the sbr.contexts_view.name column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getContexts getContexts()
     */
    public String[] getContextList();

    /**
     * Retrieve the valid context id list. The method getGroups() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     * 
     * @return An array of strings from the sbr.contexts_view.conte_idseq
     *         column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getContexts getContexts()
     */
    public String[] getContextVals();

    /**
     * Get the complete Workflow Status value list from the database. Follows
     * the pattern documented in getUsers().
     * 
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getWorkflowList getWorkflowList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getWorkflowVals getWorkflowVals()
     */
    public int getWorkflow();

    /**
     * Retrieve the valid workflow list. The method getWorkflow() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     * 
     * @return An array of strings from the sbr.ac_status_lov_view.asl_name
     *         column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getWorkflow getWorkflow()
     */
    public String[] getWorkflowList();

    /**
     * Retrieve the valid workflow values. The method getWorkflow() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the sbr.ac_status_lov_view.asl_name
     *         column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getWorkflow getWorkflow()
     */
    public String[] getWorkflowVals();

    /**
     * Retrieve the valid workflow list. The method getWorkflow() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     * 
     * @return An array of strings from the sbr.ac_status_lov_view.asl_name
     *         column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getWorkflow getWorkflow()
     */
    public String[] getCWorkflowList();

    /**
     * Retrieve the valid workflow values. The method getWorkflow() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the sbr.ac_status_lov_view.asl_name
     *         column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getWorkflow getWorkflow()
     */
    public String[] getCWorkflowVals();

    /**
     * Retrieve the valid registration statuses. Follows the pattern documented
     * in getUsers().
     * 
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getRegStatusList getRegStatusList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getRegStatusVals getRegStatusVals()
     */
    public int getRegistrations();

    /**
     * Retrieve the registration status list. The method getRegistrations() must
     * be called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbr.reg_status_lov_view.registration_status column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getRegistrations getRegistrations()
     */
    public String[] getRegStatusList();

    /**
     * Retrieve the registration status values list. The method
     * getRegistrations() must be called first. Once this method is used the
     * internal copy is deleted to reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbr.reg_status_lov_view.registration_status column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getRegistrations getRegistrations()
     */
    public String[] getRegStatusVals();

    /**
     * Retrieve the registration status list. The method getRegistrations() must
     * be called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbr.reg_status_lov_view.registration_status column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getRegistrations getRegistrations()
     */
    public String[] getRegCStatusList();

    /**
     * Retrieve the registration status values list. The method
     * getRegistrations() must be called first. Once this method is used the
     * internal copy is deleted to reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbr.reg_status_lov_view.registration_status column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getRegistrations getRegistrations()
     */
    public String[] getRegCStatusVals();

    /**
     * Retrieve the Protocols from the database. Follows the
     * pattern documented in getUsers().
     * 
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getProtoList getProtoList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getProtoVals getProtoVals()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getProtoContext getProtoContext()
     */
    public int getProtos();

    /**
     * Retrieve the protocol list. The method getProtos() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbrext.protocols_view_ext.long_name, version and context
     *         columns.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getProtos getProtos()
     */
    public String[] getProtoList();

    /**
     * Retrieve the protocol list. The method getProtos() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbrext.protocols_view_ext.proto_idseq column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getProtos getProtos()
     */
    public String[] getProtoVals();

    /**
     * Retrieve the protocol list. The method getProtos() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbrext.protocols_view_ext.conte_idseq column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getProtos getProtos()
     */
    public String[] getProtoContext();

    /**
     * Retrieve the Classification Schemes from the database. Follows the
     * pattern documented in getUsers().
     * 
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeList getSchemeList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeVals getSchemeVals()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeContext getSchemeContext()
     */
    public int getSchemes();

    /**
     * Retrieve the classification scheme list. The method getSchemes() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbr.classification_schemes_view.long_name, version and context
     *         columns.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemes getSchemes()
     */
    public String[] getSchemeList();

    /**
     * Retrieve the classification scheme id's. The method getSchemes() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbr.classification_schemes_view.cs_idseq column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemes getSchemes()
     */
    public String[] getSchemeVals();

    /**
     * Retrieve the context id's associated with the classification scheme id's
     * retrieved above. The method getSchemes() must be called first. Once this
     * method is used the internal copy is deleted to reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbr.classification_schemes_view.conte_idseq column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemes getSchemes()
     */
    public String[] getSchemeContext();

    /**
     * Retrieve the Classification Scheme Items from the database. Follows the
     * pattern documented in getUsers().
     * 
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeItemList getSchemeItemList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeItemVals getSchemeItemVals()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeItemSchemes
     *      getSchemeItemScheme()
     */
    public int getSchemeItems();

    /**
     * Retrieve the classification scheme item list. The method getSchemeItems()
     * must be called first. Once this method is used the internal copy is
     * deleted to reclaim the memory space.
     * 
     * @return An array of strings from the sbr.class_scheme_items_view.csi_name
     *         column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeItems getSchemeItems()
     */
    public String[] getSchemeItemList();

    /**
     * Retrieve the classification scheme item id's. The method getSchemeItems()
     * must be called first. Once this method is used the internal copy is
     * deleted to reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbr.class_scheme_items_view.csi_idseq column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeItems getSchemeItems()
     */
    public String[] getSchemeItemVals();

    /**
     * Retrieve the class scheme id's associated with the classification scheme
     * item id's retrieved above. The method getSchemeItems() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     * 
     * @return An array of strings from the sbr.class_scheme_items_view.cs_idseq
     *         column.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getSchemeItems getSchemeItems()
     */
    public String[] getSchemeItemSchemes();

    /**
     * Retrieve the list of record types.  As this is coded in a constant
     * array, no database access is required.
     * 
     * @return 0 if successful.
     */
    public int getACTypes();

    /**
     * Return the descriptive names for the record types.
     * 
     * @return The list of display values.
     */
    public String[] getACTypesList();
    
    /**
     * Return the internal values used to identify the record types.
     * 
     * @return The list of internal record types.
     */
    public String[] getACTypesVals();
    
    /**
     * Retrieve the list of forms and templates from the database. Follows the
     * pattern documented in getUsers().
     * 
     * @return 0 if successful, otherwise the database error code.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getUsers getUsers()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getFormsList getFormsList()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getFormsVals getFormsVals()
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getFormsContext getFormsContext()
     */
    public int getForms();

    /**
     * Return the forms/templates composite names. The method getForms() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbrext.quest_contents_view_ext.long_name, ... columns.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getForms getForms()
     */
    public String[] getFormsList();

    /**
     * Return the forms/templates id values. The method getForms() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbrext.quest_contents_view_ext.qc_idseq columns.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getForms getForms()
     */
    public String[] getFormsVals();

    /**
     * Return the context id's associated with the forms/templates. The method
     * getForms() must be called first. Once this method is used the internal
     * copy is deleted to reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbrext.quest_contents_view_ext.conte_idseq columns.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getForms getForms()
     */
    public String[] getFormsContext();

    /**
     * Return the last recorded database error message. If the current error
     * code is zero (0) an empty string is returned.
     * 
     * @return The last database error message or an empty string.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getErrorCode getErrorCode()
     */
    public String getErrorMsg();

    /**
     * Return the last recorded database error message. If the current error
     * code is zero (0) an empty string is returned.
     * 
     * @param flag_
     *        True if the new lines ('\n') should be expanded to text for use in
     *        script. False to return the message unaltered.
     * @return The last database error message or an empty string.
     * @see gov.nih.nci.cadsr.sentinel.database.DBAlert#getErrorCode getErrorCode()
     */
    public String getErrorMsg(boolean flag_);

    /**
     * Return the last recorded database error code and then reset it to zero
     * (0).
     * 
     * @return The database error code.
     */
    public int getErrorCode();

    /**
     * Return any error message and reset the error code to zero for the next
     * possible error.
     * 
     * @return The database error message.
     */
    public String getError();

    /**
     * Get the Alerts which are active for the target date provided.
     * 
     * @param target_
     *        The target date, typically the date an Auto Run process is
     *        started.
     * @return null if an error, otherwise the list of valid alert definitions.
     */
    public AlertRec[] selectAlerts(Timestamp target_);

    /**
     * Pull all Permissible Values changed in the date range specified.
     * 
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectPV(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[]);

    /**
     * Pull all Value Meanings changed in the date range specified.
     * 
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_ 
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectVM(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[]);

    /**
     * Pull all Concepts changed in the date range specified.
     * 
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectCON(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[]);

    /**
     * Pull all Value Domains changed in the date range specified.
     * 
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectVD(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[]);

    /**
     * Pull all Conceptual Domain changed in the date range specified.
     * 
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectCD(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[]);

    /**
     * Pull all Classification Schemes changed in the date range specified.
     * 
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectCS(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[]);

    /**
     * Pull all Property changes in the date range
     * specified.
     * 
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectPROP(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[]);

    /**
     * Pull all Object Class changes in the date range
     * specified.
     * 
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectOC(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[]);

    /**
     * Pull all Forms/Templates Value Values changed in the date range
     * specified.
     * 
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectQCV(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[]);

    /**
     * Pull all Forms/Templates Questions changed in the date range specified.
     * 
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectQCQ(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[]);

    /**
     * Pull all Forms/Templates Modules changed in the date range specified.
     * 
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectQCM(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[]);

    /**
     * Pull all Protocols changed in the date range specified.
     * 
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectPROTO(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[]);

    /**
     * Pull all Forms/Templates changed in the date range specified.
     * 
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectQC(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[]);

    /**
     * Pull all Classification Scheme Items changed in the date range specified.
     * 
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectCSI(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[]);

    /**
     * Pull all Data Elements changed in the date range specified.
     * 
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @param rstatus_
     *        The list of desired Registration Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectDE(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[], String rstatus_[]);
    
    /**
     * Pull the change history log for a single record.
     * 
     * @param idseq_ The idseq of the record.
     * 
     * @return The data if any (array length of zero if none found).
     */
    public ACData[] selectWithIDSEQ(String idseq_);
    
    /**
     * Pull all Contexts changed in the date range specified.
     * 
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectCONTE(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[]);
    
    /**
     * Pull all Data Element Concepts changed in the date range specified.
     * 
     * @param dates_
     *        The date comparison index.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param creators_
     *        The list of desired creator user ids.
     * @param modifiers_
     *        The list of desired modifier user ids.
     * @param wstatus_
     *        The list of desired Workflow Statuses.
     * @return 0 if successful, otherwise the database error code.
     */
    public ACData[] selectDEC(int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[]);

    /**
     * Find the Value Domains that are affected by changes to the Permissible
     * Values.
     * 
     * @param pv_
     *        The list of permissible values identified as changed or created.
     * @return The array of value domains.
     */
    public ACData[] selectVDfromPV(ACData pv_[]);

    /**
     * Find the Permissible Values that are affected by changes to the Value Meanings.
     * 
     * @param vm_
     *        The list of value meanings identified as changed or created.
     * @return The array of value domains.
     */
    public ACData[] selectPVfromVM(ACData vm_[]);

    /**
     * Find the Conceptual Domains affected by changes to the Value Domains
     * provided.
     * 
     * @param vd_
     *        The list of value domains.
     * @return The array of conceptual domains.
     */
    public ACData[] selectCDfromVD(ACData vd_[]);

    /**
     * Find the Conceptual Domains affected by changes to the Data Element Concepts
     * provided.
     * 
     * @param dec_
     *        The list of data element concepts.
     * @return The array of conceptual domains.
     */
    public ACData[] selectCDfromDEC(ACData dec_[]);

    /**
     * Select the Data Elements affected by the Value Domains provided.
     * 
     * @param vd_
     *        The value domain list.
     * @return The array of related data elements.
     */
    public ACData[] selectDEfromVD(ACData vd_[]);

    /**
     * Select the Data Element Concepts affected by the Properties provided.
     * 
     * @param prop_
     *        The property list.
     * @return The array of related data element concepts.
     */
    public ACData[] selectDECfromPROP(ACData prop_[]);

    /**
     * Select the Properties affected by the Concepts provided.
     * 
     * @param con_
     *        The concept list.
     * @return The array of related properties.
     */
    public ACData[] selectPROPfromCON(ACData con_[]);

    /**
     * Select the Object Classes affected by the Concepts provided.
     * 
     * @param con_
     *        The concept list.
     * @return The array of related object classes.
     */
    public ACData[] selectOCfromCON(ACData con_[]);

    /**
     * Select the Data Element Concepts affected by the Object Classes provided.
     * 
     * @param oc_
     *        The object class list.
     * @return The array of related data element concepts.
     */
    public ACData[] selectDECfromOC(ACData oc_[]);

    /**
     * Select the Data Elements affected by the Data Element Concepts provided.
     * 
     * @param dec_
     *        The data element concepts list.
     * @return The array of related data elements.
     */
    public ACData[] selectDEfromDEC(ACData dec_[]);

    /**
     * Select the Classification Scheme Item affected by the Data Elements
     * provided.
     * 
     * @param de_
     *        The data element list.
     * @return The array of related classification scheme items.
     */
    public ACData[] selectCSIfromDE(ACData de_[]);

    /**
     * Select the Classification Scheme Item affected by the Data Element Concepts
     * provided.
     * 
     * @param dec_
     *        The data element concept list.
     * @return The array of related classification scheme items.
     */
    public ACData[] selectCSIfromDEC(ACData dec_[]);

    /**
     * Select the Classification Scheme Item affected by the Value Domains
     * provided.
     * 
     * @param vd_
     *        The value domain list.
     * @return The array of related classification scheme items.
     */
    public ACData[] selectCSIfromVD(ACData vd_[]);

    /**
     * Select the Forms/Templates affected by the Data Elements provided.
     * 
     * @param de_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCQfromDE(ACData de_[]);

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     * 
     * @param vd_
     *        The value domain list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCQfromVD(ACData vd_[]);

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     * 
     * @param vd_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCVfromVD(ACData vd_[]);

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     * 
     * @param qcv_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCQfromQCV(ACData qcv_[]);

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     * 
     * @param qcq_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCMfromQCQ(ACData qcq_[]);

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     * 
     * @param qcm_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCfromQCM(ACData qcm_[]);

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     * 
     * @param qcq_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCfromQCQ(ACData qcq_[]);

    /**
     * Select the Classification Schemes affected by the Classification Scheme
     * Items provided.
     * 
     * @param csi_
     *        The classification scheme items list.
     * @return The array of related classification schemes.
     */
    public ACData[] selectCSfromCSI(ACData csi_[]);

    /**
     * Select the Contexts affected by the Classification Schemes provided.
     * 
     * @param cs_
     *        The classification schemes list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromCS(ACData cs_[]);

    /**
     * Select the Contexts affected by the Conceptual Domains provided.
     * 
     * @param cd_
     *        The conceptual domains list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromCD(ACData cd_[]);

    /**
     * Select the Contexts affected by the Value Domains provided.
     * 
     * @param vd_
     *        The value domains list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromVD(ACData vd_[]);

    /**
     * Select the Contexts affected by the Data Elements provided.
     * 
     * @param de_
     *        The data elements list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromDE(ACData de_[]);

    /**
     * Select the Contexts affected by the Properties provided.
     * 
     * @param prop_
     *        The properties list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromPROP(ACData prop_[]);

    /**
     * Select the Contexts affected by the Concepts provided.
     * 
     * @param con_
     *        The object class list.
     * @return The array of related concepts.
     */
    public ACData[] selectCONTEfromCON(ACData con_[]);

    /**
     * Select the Contexts affected by the Object Classes provided.
     * 
     * @param oc_
     *        The object class list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromOC(ACData oc_[]);

    /**
     * Select the Contexts affected by the Protocols provided.
     * 
     * @param proto_
     *        The protocols list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromPROTO(ACData proto_[]);

    /**
     * Select the Protocols affected by the Forms/Templates provided.
     * 
     * @param qc_
     *        The forms/templates list.
     * @return The array of related contexts.
     */
    public ACData[] selectPROTOfromQC(ACData qc_[]);

    /**
     * Select the Contexts affected by the Forms/Templates provided.
     * 
     * @param qc_
     *        The forms/templates list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromQC(ACData qc_[]);

    /**
     * Select the Contexts affected by the Data Element Concepts provided.
     * 
     * @param dec_
     *        The data element concepts list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromDEC(ACData dec_[]);

    /**
     * Retrieve a string name representation for the "object" id provided.
     * 
     * @param table_
     *        The known database table name or null if the method should use a
     *        default based on the col_ value.
     * @param col_
     *        The column name which corresponds to the id_ provided.
     * @param id_
     *        The id of the specific database record desired.
     * @return The "name" from the record, this may correspond to the long_name,
     *         prefferred_name, etc database columns depending on the table
     *         being used.
     */
    public String selectName(String table_, String col_, String id_);

    /**
     * Retrieve the "names" for a list of columns and ids. WARNING this is a
     * destructive method. It changes the content of ids_ by replacing the
     * original value with the retrieved name.
     * 
     * @param cols_
     *        The names of the columns corresponding to the ids.
     * @param ids_
     *        On input the ids of the specific records to query. On return the
     *        names of the records if they could be determined.
     */
    public void selectNames(String cols_[], String ids_[]);

    /**
     * Update the Auto Run or Manual Run timestamp.
     * 
     * @param id_
     *        The alert id to update.
     * @param stamp_
     *        The new time.
     * @param run_
     *        true to update the auto run time, false to update the manual run
     *        time
     * @param setInactive_
     *        true to set the alert status to inactive, false to leave the
     *        status unchanged
     * @return 0 if successful, otherwise the database error code.
     */
    public int updateRun(String id_, Timestamp stamp_, boolean run_,
        boolean setInactive_);

    /**
     * Return the recipients names in ascending order by first name as a single
     * string. If the recipient is a broadcast context group the group is expanded.
     * Those who have elected not to receive broadcasts from a context group are
     * not included. All freeform email addresses are listed after the names
     * retrieved from the account table.
     * 
     * @param recipients_ The Alert recipient list.
     * @return A single comma separate list of names and email addresses with
     *      the broadcast context groups expanded.
     */
    public String selectRecipientNames(String recipients_[]);
    
    /**
     * Given the idseq of a Context, retrieve all the users with write access to
     * that context.
     * 
     * @param conte_
     *        The context idseq.
     * @return The array of user ids with write access.
     */
    public String[] selectEmailsFromConte(String conte_);

    /**
     * Given the id for a user, retrieve the email address.
     * 
     * @param user_
     *        The user id.
     * @return The array of user ids with write access.
     */
    public String selectEmailFromUser(String user_);

    /**
     * Test the database dependencies within this class. This method will check
     * the existence of tables, columns and required values.
     * 
     * @return null if all dependencies are present, otherwise a string
     *         detailing those that failed.
     */
    public String testDBdependancies();

    /**
     * Test the content of the tool options table.
     * 
     * @return null if no errors, otherwise the error message.
     */
    public String testSentinelOptions();

    /**
     * Return the email addresses for all the administrators that should receive a log report.
     * 
     * @return The list of email addresses.
     */
    public String[] selectAlertReportAdminEmails();

    /**
     * Return the email introduction for the Alert Report
     * 
     * @return The introduction.
     */
    public String selectAlertReportEmailIntro();

    /**
     * Return the email error introduction for the Alert Report
     * 
     * @return The error introduction.
     */
    public String selectAlertReportEmailError();

    /**
     * Return the email admin title which appears in the "From:" field.
     * 
     * @return The admin title.
     */
    public String selectAlertReportAdminTitle();

    /**
     * Return the Alert Report email reply to address
     * 
     * @return The reply to address.
     */
    public String selectAlertReportEmailAddr();

    /**
     * Return the email SMTP host.
     * 
     * @return The email SMTP host.
     */
    public String selectAlertReportEmailHost();

    /**
     * Return the email SMTP host user account.
     * 
     * @return The email SMTP host user account.
     */
    public String selectAlertReportEmailHostUser();

    /**
     * Return the email SMTP host user account password.
     * 
     * @return The email SMTP host user account password.
     */
    public String selectAlertReportEmailHostPswd();

    /**
     * Return the email subject.
     * 
     * @return The email subject.
     */
    public String selectAlertReportEmailSubject();

    /**
     * Return the HTTP link prefix for all report output references.
     * 
     * @return The HTTP link prefix
     */
    public String selectAlertReportHTTP();

    /**
     * Return the output directory for all generated files.
     * 
     * @return The output directory prefix
     */
    public String selectAlertReportOutputDir();

    /**
     * Return the database name as it should appear on reports.
     * 
     * @return The database name
     */
    public String selectAlertReportDBName();

    /**
     * Return the email addresses for all the recipients of the statistic report.
     * 
     * @return The list of email addresses.
     */
    public String[] selectStatReportEmails();

    /**
     * Return the Alert Definition name format string. 
     * 
     * @return The list of email addresses.
     */
    public String selectAlertNameFormat();

    /**
     * Return the EVS URL from the tool options. 
     * 
     * @return The EVS URL.
     */
    public String selectEvsUrl();

    /**
     * Return the reserved CS id if the reserved CSI is passed to the method.
     * 
     * @param idseq_ The CSI id to check.
     * 
     * @return The reserved CS id or null if the CSI is not reserved.
     */
    public String selectCSfromReservedCSI(String idseq_);
    
    /**
     * Retrieve the row counts for all the tables used by the Alert Report.
     * The values may be indexed using the _ACTYPE_* variables and an index
     * of _ACTYPE_LENGTH is the count of the change history table.  
     * 
     * @return The numbers for each table.
     */
    public String[] reportRowCounts();

    /**
     * Translate the internal column names to something the user can easily
     * read.
     * 
     * @param namespace_
     *        The scope of the namespace to lookup the val_.
     * @param val_
     *        The internal column name.
     * @return The translated value.
     */
    public String translateColumn(String namespace_, String val_);

    /**
     * Translate the table names for the user.
     * 
     * @param val_
     *        The internal table name.
     * @return The user readable name.
     */
    public String translateTable(String val_);
    
    /**
     * Look for the selection of a specific record type.
     * 
     * @param val_ The AC type code.
     * @return false if the record type is not found.
     */
    public int isACTypeUsed(String val_);

    /**
     * Test if the string table code represents the record type of interest.
     * 
     * @param type_ One of the DBAlert._ACTYPE* constants.
     * @param tableCode_ The string type to test.
     * @return true if the type and string are equivalent.
     */
    public boolean isACType(int type_, String tableCode_);

    // Class data elements.

    /**
     * The internal code for Version.
     */
    public static final String  _VERSION      = "VERSION";

    /**
     * The internal code for Workflow Status.
     */
    public static final String  _WFS          = "ASL_NAME";

    /**
     * The internal code for Registration Status.
     */
    public static final String  _RS           = "REGISTRATION_STATUS";

    /**
     * The internal code for User ID.
     */
    public static final String  _UNAME        = "UA_NAME";

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_CD     = 0;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_CON = 1;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_CONTE  = 2;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_CS     = 3;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_CSI    = 4;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_DE     = 5;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_DEC    = 6;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_OC     = 7;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_PROP   = 8;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_PROTO  = 9;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_PV     = 10;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_QC     = 11;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_QCM    = 12;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_QCQ    = 13;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_QCV    = 14;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_VD     = 15;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_VM     = 16;

    /**
     * The AC Type total number of values.
     */
    public static final int _ACTYPE_LENGTH = 17;
    
    /**
     * Version Any Change value.
     */
    public static final char    _VERANYCHG    = 'C';

    /**
     * Version Major (whole) number change value.
     */
    public static final char    _VERMAJCHG    = 'M';

    /**
     * Version Ignore change value.
     */
    public static final char    _VERIGNCHG    = 'I';

    /**
     * Version Specific Value change value.
     */
    public static final char    _VERSPECHG    = 'S';

    /**
     * Maximum length of the Alert Definition Name.
     */
    public static final int     _MAXNAMELEN   = 30;

    /**
     * Maximum length of the Inaction Reason description.
     */
    public static final int     _MAXREASONLEN = 2000;

    /**
     * Maximum length of the Report Introduction description.
     */
    public static final int     _MAXINTROLEN  = 2000;

    /**
     * Maximum length of a freeform email address.
     */
    public static final int     _MAXEMAILLEN  = 255;
    
    /**
     * The Date comparison Created Only value.
     */
    public static final int     _DATECONLY = 0;
    
    /**
     * The Date comparison Modified Only value.
     */
    public static final int     _DATEMONLY = 1;
    
    /**
     * The Date comparison Created and Modified value.
     */
    public static final int     _DATECM = 2;

    /**
     * The name of the attribute for the database pool in the application session.
     */
    public static final String _DBPOOL       = "cadsrsentinelAlertPool";
    
    /**
     * The global data source within JBoss
     */
    public static final String _DATASOURCE = "cadsrsentinelDataSource";
}
