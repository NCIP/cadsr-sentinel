// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/com/scenpro/DSRAlert/DBAlert.java,v 1.18 2006-01-06 16:14:26 hebell Exp $
// $Name: not supported by cvs2svn $

package com.scenpro.DSRAlert;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import oracle.jdbc.pool.OracleDataSource;

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
 * @see com.scenpro.DSRAlert.DBAlert#setupPool(HttpServletRequest, String, String, String) setupPool() with HTTP request
 * @see com.scenpro.DSRAlert.DBAlert#setupPool(HttpSession , String, String, String) setupPool() with HTTP session
 * @see com.scenpro.DSRAlert.DBAlert#setupPool(String, String, String) setupPool()
 * @see com.scenpro.DSRAlert.DBAlert#open(HttpServletRequest, String, String) open() with HTTP request
 * @see com.scenpro.DSRAlert.DBAlert#open(ServletContext, String, String) open() with Servlet Context
 * @see com.scenpro.DSRAlert.DBAlert#open(String, String, String) open()
 * @see com.scenpro.DSRAlert.DBAlert#close close()
 */
public class DBAlert
{
    /**
     * Constructor.
     */
    public DBAlert()
    {
        _errorCode = 0;
        _nameID = new String[1];
        _nameID[0] = "";
        _nameText = new String[1];
        _nameText[0] = "";
        _conn = null;
        _sc = null;
    }

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
     * @return 0 if successful, otherwise the Oracle error code.
     * @see com.scenpro.DSRAlert.DBAlert#close close()
     */
    public int open(ServletContext sc_, String user_, String pswd_)
    {
        // If we already have a connection, don't bother.
        if (_conn != null)
            return 0;

        try
        {
            // Get a connection from the pool, if anything unexpected happens
            // the catch is
            // run.
            _sc = sc_;
            OracleConnectionPoolDataSource ocpds = (OracleConnectionPoolDataSource) _sc
                .getAttribute(_DBPOOL);
            _conn = ocpds.getConnection(user_, pswd_);

            // We handle the commit once in the close.
            _conn.setAutoCommit(false);
            _needCommit = false;

            return 0;
        }
        catch (SQLException ex)
        {
            // There seems to be a problem.
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 1: " + _errorCode + ": " + ex.toString();
            System.err.println(_errorMsg);
            _sc = null;
            _conn = null;
            return _errorCode;
        }
    }

    /**
     * Open a single simple connection to the database. No pooling is necessary.
     * 
     * @param tnsname_
     *        The Oracle TNSNAME entry describing the database location.
     * @param user_
     *        The ORACLE user id.
     * @param pswd_
     *        The password which must match 'user_'.
     * @return The database error code.
     */
    public int open(String tnsname_, String user_, String pswd_)
    {
        // If we already have a connection, don't bother.
        if (_conn != null)
            return 0;

        try
        {
            OracleDataSource ods = new OracleDataSource();
            if (tnsname_.indexOf(':') > 0)
            {
                String parts[] = tnsname_.split("[:]");
                ods.setDriverType("thin");
                ods.setServerName(parts[0]);
                ods.setPortNumber(Integer.parseInt(parts[1]));
                ods.setServiceName(parts[2]);
            }
            else
            {
                ods.setDriverType("oci8");
                ods.setTNSEntryName(tnsname_);
            }
            _conn = ods.getConnection(user_, pswd_);
            _conn.setAutoCommit(false);
            _needCommit = false;
            return 0;
        }
        catch (SQLException ex)
        {
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\n DBAlert 2: " + _errorCode + ": " + ex.toString();
            System.err.println(_errorMsg);
            return _errorCode;
        }
    }

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
     * @return 0 if successful, otherwise the Oracle error code.
     * @see com.scenpro.DSRAlert.DBAlert#close close()
     */
    public int open(HttpServletRequest request_, String user_, String pswd_)
    {
        return open(request_.getSession().getServletContext(), user_, pswd_);
    }

    /**
     * Required upon a successful return from open. When all database access is
     * completed for this user request. To optimize the database access, all
     * methods which perform actions that require a commmit only set a flag. It
     * is in the close() method the flag is interrogated and the commit actually
     * occurs.
     * 
     * @return 0 if successful, otherwise the Oracle error code.
     * @see com.scenpro.DSRAlert.DBAlert#open(HttpServletRequest, String, String) open() with HTTP request
     * @see com.scenpro.DSRAlert.DBAlert#open(ServletContext, String, String) open() with Servlet Context
     * @see com.scenpro.DSRAlert.DBAlert#open(String, String, String) open()
     */
    public int close()
    {
        // We only need to do something if a connection is obtained.
        if (_conn != null)
        {
            try
            {
                // Don't forget to commit if needed.
                if (_needCommit)
                    _conn.commit();

            }
            catch (SQLException ex)
            {
                // There seems to be a problem.
                _errorCode = ex.getErrorCode();
                _errorMsg = "\n\nDBAlert 3: " + _errorCode + ": "
                    + ex.toString();
                System.err.println(_errorMsg);
            }
            try
            {
                // Close the connection and release all pointers.
                _conn.close();
                _conn = null;
                _sc = null;
            }
            catch (SQLException ex)
            {
                // There seems to be a problem.
                _errorCode = ex.getErrorCode();
                _errorMsg = "\n\nDBAlert 3: " + _errorCode + ": "
                    + ex.toString();
                System.err.println(_errorMsg);
                _conn = null;
                _sc = null;
                return _errorCode;
            }
        }
        return 0;
    }
    
    /**
     * Get the database connection opened for this object.
     * 
     * @return java.sql.Connection opened by this object.
     */
    public Connection getConnection()
    {
        return _conn;
    }

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
     * @param tnsname_
     *        The tnsname entry for the desired database.
     * @param username_
     *        The default database user logon id.
     * @param password_
     *        The password to match the user.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    public static synchronized int setupPool(HttpSession session_,
        String tnsname_, String username_, String password_)
    {
        // Get the Servlet Context and see if a pool already exists.
        ServletContext sc = session_.getServletContext();
        OracleConnectionPoolDataSource ocpds = (OracleConnectionPoolDataSource) sc
            .getAttribute(_DBPOOL);
        if (ocpds != null)
            return 0;

        ocpds = setupPool(tnsname_, username_, password_);
        if (ocpds != null)
        {
            // Remember the pool in the Servlet Context.
            sc.setAttribute(_DBPOOL, ocpds);
            return 0;
        }
        return -1;
    }

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
     * @param tnsname_
     *        The tnsname entry for the desired database.
     * @param username_
     *        The default database user logon id.
     * @param password_
     *        The password to match the user.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    public static synchronized int setupPool(HttpServletRequest request_,
        String tnsname_, String username_, String password_)
    {
        // Pass it on...
        return setupPool(request_.getSession(), tnsname_, username_,
            password_);
    }

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
     * To use this from a browser servlet, use the method which requires an
     * HttpServletRequest as the first argument to the method.
     * 
     * @param tnsname_
     *        The tnsname entry for the desired database.
     * @param username_
     *        The default database user logon id.
     * @param password_
     *        The password to match the user.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    public static synchronized OracleConnectionPoolDataSource setupPool(
        String tnsname_, String username_, String password_)
    {
        // First register the database driver.
        OracleConnectionPoolDataSource ocpds = null;
        int rc = 0;
        String rcTxt = null;
        try
        {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        }
        catch (SQLException ex)
        {
            rc = ex.getErrorCode();
            rcTxt = "\n\nDBAlert 4: " + rc + ": " + ex.toString();
        }

        try
        {
            // Create an the connection pool data source and set the parameters.
            ocpds = new OracleConnectionPoolDataSource();
            if (tnsname_.indexOf(':') > 0)
            {
                String parts[] = tnsname_.split("[:]");
                ocpds.setDriverType("thin");
                ocpds.setServerName(parts[0]);
                ocpds.setPortNumber(Integer.parseInt(parts[1]));
                ocpds.setServiceName(parts[2]);
            }
            else
            {
                ocpds.setDriverType("oci8");
                ocpds.setTNSEntryName(tnsname_);
            }
            ocpds.setUser(username_);
            ocpds.setPassword(password_);
        }
        catch (SQLException ex)
        {
            // We have a problem.
            rc = ex.getErrorCode();
            rcTxt = "\n\nDBAlert 5: " + rc + ": " + ex.toString();
            ocpds = null;
        }

        if (rc != 0)
        {
            // Send a user friendly message to the Logon window and the more
            // detailed
            // message to the console.
            System.err.println(rcTxt);
        }
        return ocpds;
    }

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
    public AlertRec[] selectAlerts(String user_)
    {
        // Define the SQL Select
        String select = "select a.al_idseq, a.name, a.last_auto_run, a.auto_freq_unit, a.al_status, a.auto_freq_value, a.created_by, u.name "
            + "from sbrext.sn_alert_view_ext a, sbr.user_accounts_view u "
            + "where ";

        // If a user id was given, qualify the list with it.
        if (user_ != null)
            select = select + "a.created_by = ? and ";
        select = select + "u.ua_name = a.created_by";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector<AlertRec> results = new Vector<AlertRec>();

        try
        {
            // Prepare the statement.
            pstmt = _conn.prepareStatement(select);
            if (user_ != null)
                pstmt.setString(1, user_);

            // Get the list.
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                // For the list of alerts we only need basic information.
                AlertRec rec = new AlertRec();
                rec.setAlertRecNum(rs.getString(1));
                rec.setName(rs.getString(2));
                rec.setAdate(rs.getTimestamp(3));
                rec.setFreq(rs.getString(4));
                rec.setActive(rs.getString(5));
                rec.setDay(rs.getInt(6));
                rec.setCreator(rs.getString(7));
                rec.setCreatorName(rs.getString(8));

                // After much consideration, I thought it best to dynamically
                // generate the textual summary of the alert. This
                // could be done at the time the alert is saved and stored in
                // the database, however, the descriptions could become
                // very large and we have to worry about some things changing
                // and not being updated. For the first implementation
                // it seems best to generate it. In the future this may change.
                selectQuery(rec);
                select = rec.getSummary(false);
                rec.clearQuery();
                rec.setSummary(select);
                results.add(rec);
            }
            pstmt.close();
            rs.close();
        }
        catch (SQLException ex)
        {
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 6: " + _errorCode + ": " + select + "\n\n"
                + ex.toString();
            System.err.println(_errorMsg);
            return null;
        }

        // Now that we have the full results we can create a single simple array
        // to contain
        // them. This greatly simplifies access throughout the code.
        AlertRec database[] = null;
        int count = results.size();
        if (count > 0)
        {
            database = new AlertRec[count];
            for (int ndx = 0; ndx < count; ++ndx)
                database[ndx] = (AlertRec) results.get(ndx);
        }

        // Return the results.
        return database;
    }

    /**
     * Pull the list of recipients for a specific alert.
     * 
     * @param rec_
     *        The alert for the desired recipients. The alertRecNum must be set
     *        prior to this method.
     * @return 0 if successful, otherwise the database error code.
     */
    private int selectRecipients(AlertRec rec_)
    {
        // A Report has a list of one or more recipients.
        String select = "select ua_name, email, conte_idseq "
            + "from sbrext.sn_recipient_view_ext " + "where rep_idseq = ?";
        try
        {
            // Get ready...
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, rec_.getReportRecNum());

            // Go!
            Vector<String> rlist = new Vector<String>();
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                String temp = rs.getString(1);
                // We use the ua_name as is.
                if (temp != null)
                    rlist.add(temp);
                else
                {
                    temp = rs.getString(2);
                    // The email address must have an "@" in it somewhere so no
                    // change.
                    if (temp != null)
                        rlist.add(temp);
                    else
                    {
                        temp = rs.getString(3);
                        // To distinguish groups from a ua_name we use a "/" as
                        // a prefix.
                        if (temp != null)
                            rlist.add("/" + temp);
                    }
                }
            }
            pstmt.close();
            rs.close();

            // Move the data to an array and drop the Vector.
            if (rlist.size() > 0)
            {
                String temp[] = new String[rlist.size()];
                for (int ndx = 0; ndx < temp.length; ++ndx)
                {
                    temp[ndx] = (String) rlist.get(ndx);
                }
                rec_.setRecipients(temp);
            }
            else
            {
                rec_.setRecipients(null);
            }
            return 0;
        }
        catch (SQLException ex)
        {
            // We've got trouble.
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 7: " + _errorCode + ": " + select + "\n\n"
                + ex.toString();
            System.err.println(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Pull the report information for a specific alert definition.
     * 
     * @param rec_
     *        The alert for the desired report. The alertRecNum must be set
     *        prior to this method.
     * @return 0 if successful, otherwise the database error code.
     */
    private int selectReport(AlertRec rec_)
    {
        // Each Alert has one Report definition.
        String select = "select rep_idseq, include_property_ind, style, send, acknowledge_ind, comments, assoc_lvl_num "
            + "from sbrext.sn_report_view_ext " + "where al_idseq = ?";
        try
        {
            // Get ready...
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, rec_.getAlertRecNum());

            // Strictly speaking if a record is not found it is a violation of a
            // business rule, however,
            // the code is written to default all values to avoid these types of
            // quirks.
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                rec_.setReportRecNum(rs.getString(1));
                rec_.setIncPropSect(rs.getString(2));
                rec_.setReportAck(rs.getString(3));
                rec_.setReportEmpty(rs.getString(4));
                rec_.setReportAck(rs.getString(5));
                rec_.setIntro(rs.getString(6), true);
                rec_.setIAssocLvl(rs.getInt(7));
            }
            pstmt.close();
            rs.close();

            return selectRecipients(rec_);
        }
        catch (SQLException ex)
        {
            // We've got trouble.
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 8: " + _errorCode + ": " + select + "\n\n"
                + ex.toString();
            System.err.println(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Pull the properties for a specific alert definition.
     * 
     * @param rec_
     *        The alert for the desired property values. The alertRecNum must be
     *        set prior to this method.
     * @return 0 if successful, otherwise the database error code.
     */
    private int selectProperties(AlertRec rec_)
    {
        // Define the SQL Select. The column names are expanded to ensure the
        // order of retrieval. If asterisk (*) is used
        // and the database definition changes it could rearrange the columns
        // and the subsequent ResultSet.get...() method
        // calls will fail.
        String select = "select a.name, a.last_auto_run, a.last_manual_run, a.auto_freq_unit, a.al_status, a.begin_date, a.end_date, "
            + "a.status_reason, a.date_created, nvl(a.date_modified, a.date_created) as mdate, nvl(a.modified_by, a.created_by) as mby, "
            + "a.created_by, a.auto_freq_value, u1.name, nvl(u2.name, u1.name) as name2 "
            + "from sbrext.sn_alert_view_ext a, sbr.user_accounts_view u1, sbr.user_accounts_view u2 "
            + "where a.al_idseq = ? and u1.ua_name = a.created_by and u2.ua_name(+) = a.modified_by";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            // Get ready...
            pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, rec_.getAlertRecNum());

            // Go!
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                // As the where clause uses a specific ID we should only
                // retrieve one result. And there's the
                // one (1) based indexing again.
                rec_.setName(rs.getString(1));
                rec_.setAdate(rs.getTimestamp(2));
                rec_.setRdate(rs.getTimestamp(3));
                rec_.setFreq(rs.getString(4));
                rec_.setActive(rs.getString(5));
                rec_.setStart(rs.getTimestamp(6));
                rec_.setEnd(rs.getTimestamp(7));
                rec_.setInactiveReason(rs.getString(8));
                rec_.setCdate(rs.getTimestamp(9));
                rec_.setMdate(rs.getTimestamp(10));
                //rec_.setModifier(rs.getString(11));
                rec_.setCreator(rs.getString(12));
                rec_.setDay(rs.getInt(13));
                rec_.setCreatorName(rs.getString(14));
                //rec_.setModifierName(rs.getString(15));
            }
            else
            {
                // This shouldn't happen but just in case...
                rec_.setAlertRecNum(null);
            }
            pstmt.close();
            rs.close();
            return 0;
        }
        catch (SQLException ex)
        {
            // We've got trouble.
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 9: " + _errorCode + ": " + select + "\n\n"
                + ex.toString();
            System.err.println(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Retrieve a full single Alert definition. All data elements of the
     * AlertRec will be populated to reflect the database content.
     * 
     * @param id_
     *        The database id (al_idseq) of the Alert definitions.
     * @return A complete definition record if successful or null if an error
     *         occurs.
     */
    public AlertRec selectAlert(String id_)
    {
        AlertRec rec = new AlertRec();
        rec.setAlertRecNum(id_);
        if (selectProperties(rec) != 0)
        {
            rec = null;
        }
        else if (selectReport(rec) != 0)
        {
            rec = null;
        }
        else if (selectQuery(rec) != 0)
        {
            rec = null;
        }

        // Give 'em what we've got.
        return rec;
    }

    /**
     * Update the database with the Alert properties stored in a memory record.
     * 
     * @param rec_
     *        The Alert definition to be stored.
     * @return 0 if successful, otherwise the database error code.
     * @throws java.sql.SQLException
     *         On an error with rollback().
     */
    private int updateProperties(AlertRec rec_) throws SQLException
    {
        // Define the update statement. Some columns are not updated as they are
        // controlled
        // by triggers, specifically date_created, date_modified, creator and
        // modifier.
        String update = "update sbrext.sn_alert_view_ext set " + "name = ?, "
            + "auto_freq_unit = ?, " + "al_status = ?, " + "begin_date = ?, "
            + "end_date = ?, " + "status_reason = ?, " + "auto_freq_value = ? "
            + "where al_idseq = ?";

        cleanRec(rec_);
        PreparedStatement pstmt = null;
        try
        {
            // Set all the SQL arguments.
            pstmt = _conn.prepareStatement(update);
            pstmt.setString(1, rec_.getName());
            pstmt.setString(2, rec_.getFreqString());
            pstmt.setString(3, rec_.getActiveString());
            pstmt.setTimestamp(4, rec_.getStart());
            pstmt.setTimestamp(5, rec_.getEnd());
            pstmt.setString(6, rec_.getInactiveReason(false));
            pstmt.setInt(7, rec_.getDay());
            pstmt.setString(8, rec_.getAlertRecNum());

            // Send it to the database. And remember to flag a commit for later.
            pstmt.executeUpdate();
            pstmt.close();
            _needCommit = true;
            return 0;
        }
        catch (SQLException ex)
        {
            // It's bad...
            _conn.rollback();
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 10: " + _errorCode + ": " + update
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Update the Report information for an Alert within the database.
     * 
     * @param rec_
     *        The Alert definition to be written to the database.
     * @return 0 if successful, otherwise the database error code.
     * @throws java.sql.SQLException
     *         On error with rollback().
     */
    private int updateReport(AlertRec rec_) throws SQLException
    {
        // Update the related Report definition.
        String update = "update sbrext.sn_report_view_ext set "
            + "comments = ?, include_property_ind = ?, style = ?, send = ?, acknowledge_ind = ?, assoc_lvl_num = ? "
            + "where rep_idseq = ?";
        try
        {
            // Set all the SQL arguments.
            PreparedStatement pstmt = _conn.prepareStatement(update);
            pstmt.setString(1, rec_.getIntro(false));
            pstmt.setString(2, rec_.getIncPropSectString());
            pstmt.setString(3, rec_.getReportStyleString());
            pstmt.setString(4, rec_.getReportEmptyString());
            pstmt.setString(5, rec_.getReportAckString());
            pstmt.setInt(6, rec_.getIAssocLvl());

            pstmt.setString(7, rec_.getReportRecNum());

            pstmt.executeUpdate();
            pstmt.close();
            _needCommit = true;
            return 0;
        }
        catch (SQLException ex)
        {
            // It's bad...
            _conn.rollback();
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 11: " + _errorCode + ": " + update
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Perform an update on the complete record. No attempt is made to isolate
     * the specific changes so many times values will not actually be changed.
     * 
     * @param rec_
     *        The record containing the updated information. All data elements
     *        must be populated and correct.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    public int updateAlert(AlertRec rec_)
    {
        // Ensure data is clean.
        rec_.setDependancies();
        
        // Update database.
        try
        {
            int xxx = updateProperties(rec_);
            if (xxx != 0)
                return xxx;
            xxx = updateReport(rec_);
            if (xxx != 0)
                return xxx;
            xxx = updateRecipients(rec_);
            if (xxx != 0)
                return xxx;
            return updateQuery(rec_);
        }
        catch (SQLException ex)
        {
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 12: " + _errorCode + ": " + ex.toString();
            System.err.println(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Delete the Alert Definitions specified by the caller. The values must be
     * existing al_idseq values within the Alert table.
     * 
     * @param list_
     *        The al_idseq values which identify the definitions to delete.
     *        Other dependant tables in the database will automatically be
     *        cleaned up via cascades and triggers.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    public int deleteAlerts(Vector list_)
    {
        // Be sure we have something to do.
        int count = list_.size();
        if (count == 0)
            return 0;

        // Create an array.
        String list[] = new String[count];
        for (count = 0; count < list_.size(); ++count)
        {
            list[count] = (String) list_.get(count);
        }
        return deleteAlerts(list);
    }

    /**
     * Delete the Alert Definitions specified by the caller. The values must be
     * existing al_idseq values within the Alert table.
     * 
     * @param id_
     *        The al_idseq value which identifies the definition to delete.
     *        Other dependant tables in the database will automatically be
     *        cleaned up via cascades and triggers.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    public int deleteAlert(String id_)
    {
        // Be sure we have something to do.
        if (id_ == null || id_.length() == 0)
            return 0;
        String list[] = new String[1];
        list[0] = id_;
        return deleteAlerts(list);
    }

    /**
     * Delete the Alert Definitions specified by the caller. The values must be
     * existing al_idseq values within the Alert table.
     * 
     * @param list_
     *        The al_idseq values which identify the definitions to delete.
     *        Other dependant tables in the database will automatically be
     *        cleaned up via cascades and triggers.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    public int deleteAlerts(String list_[])
    {
        // Be sure we have something to do.
        if (list_ == null || list_.length == 0)
            return 0;

        // Build the delete SQL statement.
        String delete = "delete " + "from sbrext.sn_alert_view_ext "
            + "where al_idseq in (?";

        for (int ndx = 1; ndx < list_.length; ++ndx)
        {
            delete = delete + ",?";
        }
        delete = delete + ")";

        // Delete all the specified definitions. We rely on cascades or triggers
        // to clean up
        // all related tables.
        PreparedStatement pstmt = null;
        try
        {
            // Set all the SQL arguments.
            pstmt = _conn.prepareStatement(delete);
            for (int ndx = 0; ndx < list_.length; ++ndx)
            {
                pstmt.setString(ndx + 1, list_[ndx]);
            }

            // Send it to the database. And remember to flag a commit for later.
            pstmt.executeUpdate();
            pstmt.close();
            _needCommit = true;
            return 0;
        }
        catch (SQLException ex)
        {
            // It's bad...
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 13: " + _errorCode + ": " + delete
                + "\n\n" + ex.toString();
            System.err.println();
            return _errorCode;
        }
    }

    /**
     * Add the Report display attributes to the sn_rep_contents_view_ext table.
     * One (1) row per attribute.
     * 
     * @param rec_
     *        The Alert definition to store in the database.
     * @return 0 if successful, otherwise the Oracle error code.
     * @throws java.sql.SQLException
     */
    private int insertDisplay(AlertRec rec_) throws SQLException
    {
        return 0;
    }

    /**
     * Update the recipients list for the Alert report.
     * 
     * @param rec_
     *        The Alert definition to be saved to the database.
     * @return 0 if successful, otherwise the database error code.
     * @throws java.sql.SQLException
     *         On error with rollback().
     */
    private int updateRecipients(AlertRec rec_) throws SQLException
    {
        // We do not try to keep up with individual changes to the list. We
        // simply
        // wipe out the existing list and replace it with the new one.
        String update = "delete " + "from sn_recipient_view_ext "
            + "where rep_idseq = ?";
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(update);
            pstmt.setString(1, rec_.getReportRecNum());

            pstmt.executeUpdate();
            pstmt.close();

            return insertRecipients(rec_);
        }
        catch (SQLException ex)
        {
            // Ooops...
            _conn.rollback();
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 15: " + _errorCode + ": " + update
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Add the Report recipients to the sn_report_view_ext table. One (1) row
     * per recipient.
     * 
     * @param rec_
     *        The Alert definition to store in the database.
     * @return 0 if successful, otherwise the Oracle error code.
     * @throws java.sql.SQLException
     *         On error with rollback().
     */
    private int insertRecipients(AlertRec rec_) throws SQLException
    {
        // Add the Recipient record(s).
        String insert = "";
        try
        {
            for (int ndx = 0; ndx < rec_.getRecipients().length; ++ndx)
            {
                // As we only populate 1 of possible 3 columns, the Insert
                // statement
                // will be dynamically configured.
                insert = "insert into sbrext.sn_recipient_view_ext ";
                String temp = rec_.getRecipients(ndx);
                if (temp.charAt(0) == '/')
                {
                    // It must be a Context name.
                    insert = insert + "(rep_idseq, conte_idseq)";
                    temp = temp.substring(1);
                }
                else if (temp.indexOf('@') > -1)
                {
                    // It must be an email address.
                    insert = insert + "(rep_idseq, email)";
                    if (temp.length() > DBAlert._MAXEMAILLEN)
                    {
                        temp = temp.substring(0, DBAlert._MAXEMAILLEN);
                        rec_.setRecipients(ndx, temp);
                    }
                }
                else
                {
                    // It's a user name.
                    insert = insert + "(rep_idseq, ua_name)";
                }
                insert = insert + " values (?, ?)";

                // Update
                PreparedStatement pstmt = _conn.prepareStatement(insert);
                pstmt.setString(1, rec_.getReportRecNum());
                pstmt.setString(2, temp);
                pstmt.executeUpdate();
                pstmt.close();
            }
            // Remember to commit. It appears that we may flagging a commit when
            // the recipients list
            // is empty, however, the recipients list is never to be empty and
            // the other calling methods
            // depend on this to set the flag.
            _needCommit = true;
            return 0;
        }
        catch (SQLException ex)
        {
            // Ooops...
            _conn.rollback();
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 16: " + _errorCode + ": " + insert
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * A utility function that will modify the "in" clause on a SQL select to
     * contain the correct number of argument replacements to match the value
     * array provided.
     * 
     * @param select_
     *        The SQL select that must contain a single "?" replacement within
     *        an "in" clause as this is the placeholder for expansion to the
     *        appropriate number of "?" arguments to match the length of the
     *        values array. Of course if the array only has a single value the
     *        "in" can be an "=" (equals) operator.
     * @param values_
     *        The array of values to use as bind arguments in the select.
     * @return The comma separated string containing the concatenated results
     *         from the select query.
     */
    private String selectText(String select_, String values_[])
    {
        return selectText(select_, values_, 0);
    }

    /**
     * A utility function that will modify the "in" clause on a SQL select to
     * contain the correct number of argument replacements to match the value
     * array provided.
     * 
     * @param select_
     *        The SQL select that must contain a single "?" replacement within
     *        an "in" clause as this is the placeholder for expansion to the
     *        appropriate number of "?" arguments to match the length of the
     *        values array. Of course if the array only has a single value the
     *        "in" can be an "=" (equals) operator.
     * @param values_
     *        The array of values to use as bind arguments in the select.
     * @param flag_
     *        The separator to use in the concatenated string.
     * @return The comma separated string containing the concatenated results
     *         from the select query.
     */
    private String selectText(String select_, String values_[], int flag_)
    {
        // There must be a single "?" in the select to start the method.
        int pos = select_.indexOf('?');
        if (pos < 0)
            return "";

        // As one "?" is already in the select we only add more if the array
        // length is greater than 1.
        String temp = select_.substring(0, pos + 1);
        for (int ndx = 1; ndx < values_.length; ++ndx)
            temp = temp + ",?";
        temp = temp + select_.substring(pos + 1);

        try
        {
            // Now bind each value in the array to the expanded "?" list.
            PreparedStatement pstmt = _conn.prepareStatement(temp);
            for (int ndx = 0; ndx < values_.length; ++ndx)
            {
                pstmt.setString(ndx + 1, values_[ndx]);
            }
            ResultSet rs = pstmt.executeQuery();

            // Concatenate the results into a single comma separated string.
            temp = "";
            String sep = (flag_ == 0) ? ", " : "\" OR \"";
            while (rs.next())
            {
                temp = temp + sep + rs.getString(1).replaceAll("[\\r\\n]", " ");
            }
            rs.close();
            pstmt.close();

            // We always start the string with a comma so be sure to remove it
            // before returning.
            if (temp.length() > 0)
            {
                temp = temp.substring(sep.length());
                if (flag_ == 1)
                    temp = "\"" + temp + "\"";
            }
            else
                temp = "\"(unknown)\"";
        }
        catch (SQLException ex)
        {
            temp = ex.toString();
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 17: " + _errorCode + ": " + select_
                + "\n\n" + temp;
            System.err.println(_errorMsg);
        }
        return temp;
    }

    /**
     * Build the summary text from the content of the alert definition.
     * 
     * @param rec_
     *        The alert definition.
     * @return The Alert Definition summary.
     */
    public String buildSummary(AlertRec rec_)
    {
        String select;

        // Build the Summary text. You may wonder why we access the database
        // multiple times when it
        // is possible to collapse all of this into a single query. The
        // additional complexity of the
        // SQL and resulting logic made it unmanageable. If this proves to be a
        // performance problem
        // we can revisit it again in the future. Remember as more features are
        // added for the criteria
        // and monitors the complexity lever will increase.
        String criteria = "";
        int specific = 0;
        int marker = 1;
        if (rec_.getContexts() != null)
        {
            if (rec_.isCONTall())
                criteria = criteria + "Context may be anything ";
            else
            {
                select = "select name || ' (v' || version || ')' as label from sbr.contexts_view "
                    + "where conte_idseq in (?) order by upper(name) ASC";
                criteria = criteria + "Context must be "
                    + selectText(select, rec_.getContexts(), 1);
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getProtocols() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.isPROTOall())
                criteria = criteria + "Protocols may be anything ";
            else
            {
                select = "select long_name || ' (' || proto_id || 'v' || version || ')' as label "
                    + "from sbrext.protocols_view_ext "
                    + "where proto_idseq in (?) order by upper(long_name) ASC";
                criteria = criteria + "Protocols must be "
                    + selectText(select, rec_.getProtocols(), 1);
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getForms() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.isFORMSall())
                criteria = criteria + "Forms / Templates may be anything ";
            else
            {
                select = "select long_name || ' (' || qc_id || 'v' || version || ')' as label "
                    + "from sbrext.quest_contents_view_ext "
                    + "where qc_idseq in (?) order by upper(long_name) ASC";
                criteria = criteria + "Forms / Templates must be "
                    + selectText(select, rec_.getForms(), 1);
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getSchemes() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.isCSall())
                criteria = criteria + "Classification Schemes may be anything ";
            else
            {
                select = "select long_name || ' (' || cs_id || 'v' || version || ')' as label "
                    + "from sbr.classification_schemes_view "
                    + "where cs_idseq in (?) order by upper(long_name) ASC";
                criteria = criteria + "Classification Schemes must be "
                    + selectText(select, rec_.getSchemes(), 1);
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getSchemeItems() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.isCSIall())
                criteria = criteria
                + "Classification Scheme Items may be anything ";
            else
            {
                select = "select csi_name "
                    + "from sbr.class_scheme_items_view "
                    + "where csi_idseq in (?) order by upper(csi_name) ASC";
                criteria = criteria + "Classification Scheme Items must be "
                    + selectText(select, rec_.getSchemeItems(), 1);
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getACTypes() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.isACTYPEall())
                criteria = criteria + "Administered Component Types may be anything ";
            else
            {
                criteria = criteria + "Administered Component Types must be ";
                String list = "";
                for (int ndx = 0; ndx < rec_.getACTypes().length; ++ndx)
                {
                    list = list + " OR \"" + binarySearch(_DBMAP3KEYS, _DBMAP3VALS, rec_.getACTypes(ndx)) + "\"";
                }
                criteria = criteria + list.substring(4);
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getCWorkflow() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.isCWFSall())
                criteria = criteria + "Workflow Status may be anything ";
            else
            {
                select = "select asl_name from sbr.ac_status_lov_view "
                    + "where asl_name in (?) order by upper(asl_name) ASC";
                criteria = criteria + "Workflow Status must be "
                    + selectText(select, rec_.getCWorkflow(), 1);
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getCRegStatus() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.isCRSall())
                criteria = criteria + "Registration Status may be anything ";
            else
            {
                select = "select registration_status "
                    + "from sbr.reg_status_lov_view "
                    + "where registration_status in (?) "
                    + "order by upper(registration_status) ASC";
                String txt = selectText(select, rec_.getCRegStatus(), 1); 
                criteria = criteria + "Registration Status must be ";
                if (rec_.isCRSnone())
                {
                    criteria = criteria + "\"(none)\"";
                    if (txt.length() > 0)
                        criteria = criteria + " OR ";
                }
                criteria = criteria + txt;
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getCreators() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.getCreators(0).charAt(0) == '(')
                criteria = criteria + "Created By may be anyone ";
            else
            {
                select = "select name from sbr.user_accounts_view "
                    + "where ua_name in (?) order by upper(name) ASC";
                criteria = criteria + "Created By must be "
                    + selectText(select, rec_.getCreators(), 1);
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getModifiers() != null)
        {
            if (criteria.length() > 0)
                criteria = criteria + " AND\n";
            if (rec_.getModifiers(0).charAt(0) == '(')
                criteria = criteria + "Modified By may be anyone ";
            else
            {
                select = "select name from sbr.user_accounts_view "
                    + "where ua_name in (?) order by upper(name) ASC";
                criteria = criteria + "Modified By must be "
                    + selectText(select, rec_.getModifiers(), 1);
                specific += marker;
            }
        }
        marker *= 2;
        if (criteria.length() > 0)
            criteria = criteria + " AND\n";
        switch (rec_.getDateFilter())
        {
            case _DATECONLY:
                criteria = criteria + "Reporting Dates are compared to Date Created only ";
                specific += marker;
                break;

            case _DATEMONLY:
                criteria = criteria + "Reporting Dates are compared to Date Modified only ";
                specific += marker;
                break;

            case _DATECM:
            default:
                criteria = criteria + "Reporting Dates are compared to Date Created and Date Modified ";
                break;
        }

        if (specific > 0)
            criteria = "Criteria:\n" + criteria + "\n";
        else
            criteria = "Criteria:\nAll records within the caDSR\n";

        String monitors = "";
        specific = 0;
        marker = 1;
        if (rec_.getAWorkflow() != null)
        {
            if (rec_.getAWorkflow(0).charAt(0) != '(')
            {
                select = "select asl_name from sbr.ac_status_lov_view "
                    + "where asl_name in (?) order by upper(asl_name) ASC";
                monitors = monitors + "Workflow Status changes to "
                    + selectText(select, rec_.getAWorkflow(), 1);
            }
            else if (rec_.getAWorkflow(0).equals("(Ignore)"))
                monitors = monitors + ""; // "Workflow Status changes are
            // ignored ";
            else
            {
                monitors = monitors + "Workflow Status changes to anything ";
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getARegis() != null)
        {
            if (rec_.getARegis(0).charAt(0) != '(')
            {
                select = "select registration_status "
                    + "from sbr.reg_status_lov_view "
                    + "where registration_status in (?) "
                    + "order by upper(registration_status) ASC";
                if (monitors.length() > 0)
                    monitors = monitors + " OR\n";
                monitors = monitors + "Registration Status changes to "
                    + selectText(select, rec_.getARegis(), 1);
            }
            else if (rec_.getARegis(0).equals("(Ignore)"))
                monitors = monitors + ""; // "Registration Status changes are
            // ignored ";
            else
            {
                if (monitors.length() > 0)
                    monitors = monitors + " OR\n";
                monitors = monitors
                    + "Registration Status changes to anything ";
                specific += marker;
            }
        }
        marker *= 2;
        if (rec_.getAVersion() != DBAlert._VERIGNCHG)
        {
            if (rec_.getAVersion() == DBAlert._VERANYCHG)
                specific += marker;
            if (monitors.length() > 0)
                monitors = monitors + " OR\n";
            switch (rec_.getAVersion())
            {
                case DBAlert._VERANYCHG:
                    monitors = monitors + "Version changes to anything\n";
                    break;
                case DBAlert._VERMAJCHG:
                    monitors = monitors
                        + "Version Major Revision changes to anything\n";
                    break;
                case DBAlert._VERIGNCHG:
                    monitors = monitors + "";
                    break; // "Version changes are ignored\n"; break;
                case DBAlert._VERSPECHG:
                    monitors = monitors + "Version changes to \""
                        + rec_.getActVerNum() + "\"\n";
                    break;
            }
        }
        if (monitors.length() == 0)
            monitors = "\nMonitors:\nIgnore all changes. Reports are always empty.\n";
        else if (specific == 7)
            monitors = "\nMonitors:\nAll Change Activities\n";
        else
            monitors = "\nMonitors:\n" + monitors;

        return criteria + monitors;
    }

    /**
     * Set the owner of the Alert Definition.
     * 
     * @param rec_ The Alert Definition with the new creator already set.
     */
    public void setOwner(AlertRec rec_)
    {
        // Ensure data is clean.
        rec_.setDependancies();

        // Update creator in database.
        String update = "update sbrext.sn_alert_view_ext set created_by = ? where al_idseq = ?";
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(update);
            pstmt.setString(1, rec_.getCreator());
            pstmt.setString(2, rec_.getAlertRecNum());
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch (SQLException ex)
        {
            // Ooops...
            int _errorCode = ex.getErrorCode();
            String _errorMsg = "\n\nDBAlert 18: " + _errorCode + ": " + update
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
        }
    }

    /**
     * Get the type of the AC id from the database.
     * @param id_ The AC id.
     * @return The [0] is the type and the [1] is the name of the AC.
     */
    public String [] getACtype(String id_)
    {
        String out[] = new String[2];
        String select =
            "select 'conte', name from sbr.contexts_view where conte_idseq = ? "
            + "union "
            + "select 'cs', long_name from sbr.classification_schemes_view where cs_idseq = ? "
            + "union "
            + "select 'csi', csi_name from sbr.class_scheme_items_view where csi_idseq = ? "
            + "union "
            + "select 'qc', long_name from sbrext.quest_contents_view_ext where qc_idseq = ? and qtl_name in ('TEMPLATE','CRF') "
            + "union "
            + "select 'proto', long_name from sbrext.protocols_view_ext where proto_idseq = ?";

        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, id_);
            pstmt.setString(2, id_);
            pstmt.setString(3, id_);
            pstmt.setString(4, id_);
            pstmt.setString(5, id_);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                out[0] = rs.getString(1);
                out[1] = rs.getString(2);
            }
            else
            {
                out[0] = null;
                out[1] = null;
            }
            rs.close();
            pstmt.close();
        }
        catch (SQLException ex)
        {
            // Ooops...
            int _errorCode = ex.getErrorCode();
            String _errorMsg = "\n\nDBAlert 18: " + _errorCode + ": " + select
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
        }
        return out;
    }
    
    /**
     * Look for an Alert owned by the user with a Query which
     * references the id specified.
     * 
     * @param id_ The Context, Form, CS, etc ID_SEQ value.
     * @param user_ The user who should own the Alert if it exists.
     * @return true if the user already watches the id, otherwise false.
     */
    public String checkQuery(String id_, String user_)
    {
        String select = "select al.name "
            + "from sbrext.sn_alert_view_ext al, sbrext.sn_query_view_ext qur "
            + "where al.created_by = ? and qur.al_idseq = al.al_idseq and qur.value = ?";

        String rc = null;
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, user_);
            pstmt.setString(2, id_);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                rc = rs.getString(1);
            rs.close();
            pstmt.close();
        }
        catch (SQLException ex)
        {
            // Ooops...
            int _errorCode = ex.getErrorCode();
            String _errorMsg = "\n\nDBAlert 18: " + _errorCode + ": " + select
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
        }

        return rc;
    }
    
    /**
     * Check the specified user id for Sentinel Tool Administration privileges.
     * 
     * @param user_ The user id as used during Sentinel Tool Login.
     * @return true if the user has administration privileges, otherwise false.
     */
    public boolean checkToolAdministrator(String user_)
    {
        String select = "select 1 from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' "
            + "and property like 'ADMIN.%' "
            + "and value like '%0%' "
            + "and ua_name = '" + user_ + "' "
            + "and rownum < 2";
        int rows = testDB(select);
        return rows == 1;
    }

    /**
     * Retrieve the CDE Browser URL if available.
     * 
     * @return The URL string.
     */
    public String selectBrowserURL()
    {
        String select = "select value from sbrext.tool_options_view_ext "
            + "where tool_name = 'BROWSER' and property = 'URL'";
        
        String rc = null;

        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                rc = rs.getString(1);
            rs.close();
            pstmt.close();
        }
        catch (SQLException ex)
        {
            // Ooops...
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 18: " + _errorCode + ": " + select
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
        }

        return rc;
    }

    /**
     * Retrieve the Report Threshold
     * 
     * @return The number of rows to allow in a report.
     */
    public int selectReportThreshold()
    {
        String select = "select value from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' and property = 'REPORT.THRESHOLD.LIMIT'";
        
        int rc = 100;

        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                rc = rs.getInt(1);
            rs.close();
            pstmt.close();
        }
        catch (SQLException ex)
        {
            // Ooops...
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 18: " + _errorCode + ": " + select
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
        }

        return rc;
    }
    
    /**
     * Read the Query clause from the database for the Alert definition
     * specified.
     * 
     * @param rec_
     *        The Alert record to contain the Query clause.
     * @return 0 if successful, otherwise the database error code.
     */
    private int selectQuery(AlertRec rec_)
    {
        String select = "select record_type, data_type, property, value "
            + "from sbrext.sn_query_view_ext " + "where al_idseq = ?"; // order
        // by
        // record_type
        // ASC,
        // data_type
        // ASC,
        // property
        // ASC";
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, rec_.getAlertRecNum());
            ResultSet rs = pstmt.executeQuery();
            Vector<String> context = new Vector<String>();
            Vector<String> actype = new Vector<String>();
            Vector<String> scheme = new Vector<String>();
            Vector<String> schemeitem = new Vector<String>();
            Vector<String> form = new Vector<String>();
            Vector<String> protocol = new Vector<String>();
            Vector<String> creator = new Vector<String>();
            Vector<String> modifier = new Vector<String>();
            Vector<String> workflow = new Vector<String>();
            Vector<String> regis = new Vector<String>();
            Vector<String> cregis = new Vector<String>();
            Vector<String> cwork = new Vector<String>();

            // After reading the query set we have to partition it into the
            // appropriate individual
            // variables. As the data stored in the query is internal
            // representations there is
            // no point to attempting to order it. Also for any one Alert there
            // isn't enough rows
            // to warrant the extra coding or logical overhead.
            while (rs.next())
            {
                char rtype = rs.getString(1).charAt(0);
                String dtype = rs.getString(2);
                String value = rs.getString(4);
                if (rtype == _CRITERIA)
                {
                    if (dtype.equals(_CONTEXT))
                        context.add(value);
                    else if (dtype.equals(_FORM))
                        form.add(value);
                    else if (dtype.equals(_PROTOCOL))
                        protocol.add(value);
                    else if (dtype.equals(_SCHEME))
                        scheme.add(value);
                    else if (dtype.equals(_SCHEMEITEM))
                        schemeitem.add(value);
                    else if (dtype.equals(_CREATOR))
                        creator.add(value);
                    else if (dtype.equals(_MODIFIER))
                        modifier.add(value);
                    else if (dtype.equals(_ACTYPE))
                        actype.add(value);
                    else if (dtype.equals(_REGISTER))
                        cregis.add(value);
                    else if (dtype.equals(_STATUS))
                        cwork.add(value);
                    else if (dtype.equals(_DATEFILTER))
                    {
                        rec_.setDateFilter(value);
                    }
                }
                else if (rtype == _MONITORS)
                {
                    if (dtype.equals(_STATUS))
                        workflow.add(value);
                    else if (dtype.equals(_REGISTER))
                        regis.add(value);
                    else if (dtype.equals(_VERSION))
                    {
                        rec_.setAVersion(rs.getString(3));
                        rec_.setActVerNum(value);
                    }
                }
            }
            rs.close();
            pstmt.close();

            // Move the data into appropriate arrays within the Alert record to
            // simplify use
            // downstream.
            String list[] = null;
            if (context.size() == 0)
            {
                rec_.setContexts(null);
            }
            else
            {
                list = new String[context.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) context.get(ndx);
                rec_.setContexts(list);
            }

            if (actype.size() == 0)
            {
                rec_.setACTypes(null);
            }
            else
            {
                list = new String[actype.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) actype.get(ndx);
                rec_.setACTypes(list);
            }

            if (protocol.size() == 0)
            {
                rec_.setProtocols(null);
            }
            else
            {
                list = new String[protocol.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) protocol.get(ndx);
                rec_.setProtocols(list);
            }

            if (form.size() == 0)
            {
                rec_.setForms(null);
            }
            else
            {
                list = new String[form.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) form.get(ndx);
                rec_.setForms(list);
            }

            if (scheme.size() == 0)
            {
                rec_.setSchemes(null);
            }
            else
            {
                list = new String[scheme.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) scheme.get(ndx);
                rec_.setSchemes(list);
            }

            if (schemeitem.size() == 0)
            {
                rec_.setSchemeItems(null);
            }
            else
            {
                list = new String[schemeitem.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) schemeitem.get(ndx);
                rec_.setSchemeItems(list);
            }
            
            if (cregis.size() == 0)
            {
                rec_.setCRegStatus(null);
            }
            else
            {
                list = new String[cregis.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) cregis.get(ndx);
                rec_.setCRegStatus(list);
            }
            
            if (cwork.size() == 0)
            {
                rec_.setCWorkflow(null);
            }
            else
            {
                list = new String[cwork.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) cwork.get(ndx);
                rec_.setCWorkflow(list);
            }

            if (creator.size() == 0)
            {
                rec_.setCreators(null);
            }
            else
            {
                list = new String[creator.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) creator.get(ndx);
                rec_.setCreators(list);
            }

            if (modifier.size() == 0)
            {
                rec_.setModifiers(null);
            }
            else
            {
                list = new String[modifier.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) modifier.get(ndx);
                rec_.setModifiers(list);
            }

            if (workflow.size() == 0)
            {
                rec_.setAWorkflow(null);
            }
            else
            {
                list = new String[workflow.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) workflow.get(ndx);
                rec_.setAWorkflow(list);
            }

            if (regis.size() == 0)
            {
                rec_.setARegis(null);
            }
            else
            {
                list = new String[regis.size()];
                for (int ndx = 0; ndx < list.length; ++ndx)
                    list[ndx] = (String) regis.get(ndx);
                rec_.setARegis(list);
            }

            // Create the summary string now the data is loaded.
            rec_.setSummary(buildSummary(rec_));

            return 0;
        }
        catch (SQLException ex)
        {
            // Ooops...
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 18: " + _errorCode + ": " + select
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Update the Query details for the Alert.
     * 
     * @param rec_
     *        The Alert definition to be updated.
     * @return 0 if successful, otherwise the database error code.
     * @throws java.sql.SQLException
     *         On error with rollback().
     */
    private int updateQuery(AlertRec rec_) throws SQLException
    {
        // First we delete the existing Query details as it's easier to wipe out
        // the old and add
        // the new than trying to track individual changes.
        String update = "delete " + "from sbrext.sn_query_view_ext "
            + "where al_idseq = ?";
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(update);
            pstmt.setString(1, rec_.getAlertRecNum());
            pstmt.executeUpdate();
            pstmt.close();

            return insertQuery(rec_);
        }
        catch (SQLException ex)
        {
            // Ooops...
            _conn.rollback();
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 19: " + _errorCode + ": " + update
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Add the Query details to the Alert in the database.
     * 
     * @param rec_
     *        The Alert definition to be added to the database.
     * @return 0 if successful, otherwise the database error code.
     * @throws java.sql.SQLException
     *         On error with rollback().
     */
    private int insertQuery(AlertRec rec_) throws SQLException
    {
        String insert = "insert into sbrext.sn_query_view_ext (al_idseq, record_type, data_type, property, value) "
            + "values (?, ?, ?, ?, ?)";

        int marker = 0;
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(insert);
            pstmt.setString(1, rec_.getAlertRecNum());
            pstmt.setString(2, "C");

            // We only want to record those items selected by the user that
            // require special processing. For
            // example, if (All) contexts were selected by the user we do not
            // record (All) in the database
            // because the downstream processing of the Alert only cares about
            // looking for specific criteria
            // and monitors. In other words, we don't want to waste time
            // checking the context when (All) was
            // selected because it will always logically test true.
            ++marker;
            if (!rec_.isCONTall())
            {
                pstmt.setString(3, _CONTEXT);
                pstmt.setString(4, "CONTE_IDSEQ");
                for (int ndx = 0; ndx < rec_.getContexts().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getContexts(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (!rec_.isPROTOall())
            {
                pstmt.setString(3, _PROTOCOL);
                pstmt.setString(4, "PROTO_IDSEQ");
                for (int ndx = 0; ndx < rec_.getProtocols().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getProtocols(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (!rec_.isFORMSall())
            {
                pstmt.setString(3, _FORM);
                pstmt.setString(4, "QC_IDSEQ");
                for (int ndx = 0; ndx < rec_.getForms().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getForms(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (!rec_.isCSall())
            {
                pstmt.setString(3, _SCHEME);
                pstmt.setString(4, "CS_IDSEQ");
                for (int ndx = 0; ndx < rec_.getSchemes().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getSchemes(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (!rec_.isCSIall())
            {
                pstmt.setString(3, _SCHEMEITEM);
                pstmt.setString(4, "CSI_IDSEQ");
                for (int ndx = 0; ndx < rec_.getSchemeItems().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getSchemeItems(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (rec_.getCreators(0).equals(Constants._STRALL) == false)
            {
                pstmt.setString(3, _CREATOR);
                pstmt.setString(4, "UA_NAME");
                for (int ndx = 0; ndx < rec_.getCreators().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getCreators(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (rec_.getModifiers(0).equals(Constants._STRALL) == false)
            {
                pstmt.setString(3, _MODIFIER);
                pstmt.setString(4, "UA_NAME");
                for (int ndx = 0; ndx < rec_.getModifiers().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getModifiers(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (!rec_.isACTYPEall())
            {
                pstmt.setString(3, _ACTYPE);
                pstmt.setString(4, "ABBREV");
                for (int ndx = 0; ndx < rec_.getACTypes().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getACTypes(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (rec_.getDateFilter() != DBAlert._DATECM)
            {
                pstmt.setString(3, _DATEFILTER);
                pstmt.setString(4, "CODE");
                pstmt.setString(5, Integer.toString(rec_.getDateFilter()));
                pstmt.executeUpdate();
            }
            
            ++marker;
            if (!rec_.isCRSall())
            {
                pstmt.setString(3, _REGISTER);
                pstmt.setString(4, "REGISTRATION_STATUS");
                for (int ndx = 0; ndx < rec_.getCRegStatus().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getCRegStatus(ndx));
                    pstmt.executeUpdate();
                }
            }
            
            ++marker;
            if (!rec_.isCWFSall())
            {
                pstmt.setString(3, _STATUS);
                pstmt.setString(4, "ASL_NAME");
                for (int ndx = 0; ndx < rec_.getCWorkflow().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getCWorkflow(ndx));
                    pstmt.executeUpdate();
                }
            }

            marker += 100;
            pstmt.setString(2, "M");

            ++marker;
            if (rec_.getAWorkflow(0).equals(Constants._STRANY) == false)
            {
                pstmt.setString(3, _STATUS);
                pstmt.setString(4, "ASL_NAME");
                for (int ndx = 0; ndx < rec_.getAWorkflow().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getAWorkflow(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (rec_.getARegis(0).equals(Constants._STRANY) == false)
            {
                pstmt.setString(3, _REGISTER);
                pstmt.setString(4, "REGISTRATION_STATUS");
                for (int ndx = 0; ndx < rec_.getARegis().length; ++ndx)
                {
                    // Update
                    pstmt.setString(5, rec_.getARegis(ndx));
                    pstmt.executeUpdate();
                }
            }

            ++marker;
            if (rec_.getAVersion() != DBAlert._VERANYCHG)
            {
                pstmt.setString(3, _VERSION);
                pstmt.setString(4, rec_.getAVersionString());
                pstmt.setString(5, rec_.getActVerNum());
                pstmt.executeUpdate();
            }

            // Remember to commit.
            ++marker;
            pstmt.close();
            _needCommit = true;
            return 0;
        }
        catch (SQLException ex)
        {
            // Ooops...
            _conn.rollback();
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 20 (" + marker + "): " + _errorCode + ": "
                + insert + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Clean the name of any illegal characters and truncate if needed.
     * 
     * @param name_
     *        The name of the Alert.
     * @return The corrected name.
     */
    private String cleanName(String name_)
    {
        String name = null;
        if (name_ != null)
        {
            name = name_.replaceAll("[\"]", "");
            if (name.length() > DBAlert._MAXNAMELEN)
                name = name.substring(0, DBAlert._MAXNAMELEN);
        }
        return name;
    }

    /**
     * Clean the inactive reason and truncate if needed.
     * 
     * @param reason_
     *        The reason message.
     * @return The corrected message.
    private String cleanReason(String reason_)
    {
        String reason = reason_;
        if (reason != null && reason.length() > DBAlert._MAXREASONLEN)
        {
            reason = reason.substring(0, DBAlert._MAXREASONLEN);
        }
        return reason;
    }
     */

    /**
     * Clean the Alert Report introduction and truncate if needed.
     * 
     * @param intro_
     *        The introduction.
     * @return The cleaned introduction.
     */
    private String cleanIntro(String intro_)
    {
        String intro = intro_;
        if (intro != null && intro.length() > DBAlert._MAXINTROLEN)
        {
            intro = intro.substring(0, DBAlert._MAXINTROLEN);
        }
        return intro;
    }

    /**
     * Clean all the user enterable parts of an Alert and truncate to the
     * database allowed length.
     * 
     * @param rec_
     *        The Alert to be cleaned.
     */
    private void cleanRec(AlertRec rec_)
    {
        String temp = cleanName(rec_.getName());
        rec_.setName(temp);
        temp = rec_.getInactiveReason(false);
        rec_.setInactiveReason(temp);
        temp = cleanIntro(rec_.getIntro(false));
        rec_.setIntro(temp, false);
    }

    /**
     * Insert the properties for the Alert definition and retrieve the new
     * database generated ID for this Alert.
     * 
     * @param rec_
     *        The Alert to be stored in the database.
     * @return 0 if successful, otherwise the database error code.
     * @throws java.sql.SQLException
     *         On error with rollback().
     */
    private int insertProperties(AlertRec rec_) throws SQLException
    {
        // Define the SQL insert. Remember date_created, date_modified, creator
        // and modifier are controlled
        // by triggers. Also (as of 10/21/2004) after the insert the
        // date_modified is still set by the insert
        // trigger.
        String insert = "begin insert into sbrext.sn_alert_view_ext "
            + "(name, auto_freq_unit, al_status, begin_date, end_date, status_reason, auto_freq_value) "
            + "values (?, ?, ?, ?, ?, ?, ?) return al_idseq into ?; end;";

        CallableStatement pstmt = null;
        cleanRec(rec_);
        try
        {
            // Set all the SQL arguments.
            pstmt = _conn.prepareCall(insert);
            pstmt.setString(1, rec_.getName());
            pstmt.setString(2, rec_.getFreqString());
            pstmt.setString(3, rec_.getActiveString());
            pstmt.setTimestamp(4, rec_.getStart());
            pstmt.setTimestamp(5, rec_.getEnd());
            pstmt.setString(6, rec_.getInactiveReason(false));
            pstmt.setInt(7, rec_.getDay());
            pstmt.registerOutParameter(8, Types.CHAR);

            // Insert the new record and flag a commit for later.
            pstmt.executeUpdate();

            // We need the record id to populate the foreign keys for other
            // tables.
            rec_.setAlertRecNum(pstmt.getString(8));
            pstmt.close();
            return 0;
        }
        catch (SQLException ex)
        {
            // Ooops...
            rec_.setAlertRecNum(null);
            _conn.rollback();
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 21: " + _errorCode + ": " + insert
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Insert the Report details for the Alert definition into the database and
     * retrieve the new report id.
     * 
     * @param rec_
     *        The Alert definition to be stored in the database.
     * @return 0 if successful, otherwise the database error code.
     * @throws java.sql.SQLException
     *         On error with rollback().
     */
    private int insertReport(AlertRec rec_) throws SQLException
    {
        // Add the Report record.
        String insert = "begin insert into sbrext.sn_report_view_ext "
            + "(al_idseq, comments, include_property_ind, style, send, acknowledge_ind, assoc_lvl_num) "
            + "values (?, ?, ?, ?, ?, ?, ?) return rep_idseq into ?; end;";

        CallableStatement pstmt = null;
        try
        {
            pstmt = _conn.prepareCall(insert);
            pstmt.setString(1, rec_.getAlertRecNum());
            pstmt.setString(2, rec_.getIntro(false));
            pstmt.setString(3, rec_.getIncPropSectString());
            pstmt.setString(4, rec_.getReportStyleString());
            pstmt.setString(5, rec_.getReportEmptyString());
            pstmt.setString(6, rec_.getReportAckString());
            pstmt.setInt(7, rec_.getIAssocLvl());
            pstmt.registerOutParameter(8, Types.CHAR);
            pstmt.executeUpdate();

            // We need the record id to populate the foreign keys for other
            // tables.
            rec_.setReportRecNum(pstmt.getString(8));
            pstmt.close();
            return 0;
        }
        catch (SQLException ex)
        {
            // Ooops...
            rec_.setAlertRecNum(null);
            _conn.rollback();
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 22: " + _errorCode + ": " + insert
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Perform an insert of a new record. The record number element of the class
     * is not used AND it is not returned by this method. All other elements
     * must be complete and correct.
     * 
     * @param rec_
     *        The Alert definition to insert into the database table.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    public int insertAlert(AlertRec rec_)
    {
        // Ensure required data dependancies.
        rec_.setDependancies();
        
        // Update the database.
        try
        {
            int xxx = insertProperties(rec_);
            if (xxx == 0)
            {
                xxx = insertReport(rec_);
                if (xxx == 0)
                {
                    xxx = insertRecipients(rec_);
                    if (xxx == 0)
                    {
                        xxx = insertDisplay(rec_);
                        if (xxx == 0)
                        {
                            xxx = insertQuery(rec_);
                            if (xxx != 0)
                                rec_.setAlertRecNum(null);
                        }
                        else
                            rec_.setAlertRecNum(null);
                    }
                    else
                        rec_.setAlertRecNum(null);
                }
                else
                    rec_.setAlertRecNum(null);
            }
            else
                rec_.setAlertRecNum(null);
            return xxx;
        }
        catch (SQLException ex)
        {
            // Ooops...
            rec_.setAlertRecNum(null);
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 23: " + _errorCode + ": " + ex.toString();
            System.err.println(_errorMsg);
            return _errorCode;
        }
    }

    /**
     * Retrieve a more user friendly version of the user id.
     * 
     * @param id_
     *        The id as would be entered at logon.
     * @return null if the user id was not found in the sbr.user_accounts table,
     *         otherwise the 'name' value of the matching row.
     */
    public String selectUserName(String id_)
    {
        // Define the SQL select.
        String select = "select uav.name "
            + "from sbr.user_accounts_view uav, sbrext.user_contexts_view ucv "
            + "where uav.ua_name = ? and ucv.ua_name = uav.ua_name and ucv.privilege = 'W'";
        PreparedStatement pstmt = null;
        String result;
        ResultSet rs = null;

        try
        {
            // Get ready...
            pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, id_);

            // Go!
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                // Get the name, remember 1 indexing.
                result = rs.getString(1);
            }
            else
            {
                // User must have some kind of write permissions.
                result = null;
            }
            pstmt.close();
            rs.close();
        }
        catch (SQLException ex)
        {
            // We've got trouble.
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 24: " + _errorCode + ": " + select
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            result = null;
        }
        return result;
    }

    /**
     * Used for method return values.
     * 
     * @see com.scenpro.DSRAlert.DBAlert#getBasicData1 getBasicData1()
     */
    private class returnData1
    {
        /**
         * The return code from the database.
         */
        public int    _rc;

        /**
         * The label results from the database query.
         */
        public String _labels[];

        /**
         * The value results from the database query.
         */
        public String _vals[];
    }

    /**
     * Execute the specified SQL select query and return a label/value pair.
     * 
     * @param select_
     *        The SQL select statement.
     * @param flag_
     *        True to prefix "(All)" to the result set. False to return the
     *        result set unaltered.
     * @return 0 if successful, otherwise the Oracle error code.
     */
    private returnData1 getBasicData1(String select_, boolean flag_)
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        class tempData
        {
            public String _label;

            public String _val;
        }
        ;
        Vector<tempData> results = new Vector<tempData>();
        returnData1 data = new returnData1();

        try
        {
            // Prepare the statement.
            pstmt = _conn.prepareStatement(select_);

            // Get the list.
            rs = pstmt.executeQuery();
            tempData rec;
            while (rs.next())
            {
                // Remember about the 1 (one) based indexing.
                rec = new tempData();
                rec._val = rs.getString(1);
                rec._label = rs.getString(2);
                results.add(rec);
            }
            pstmt.close();
            rs.close();

            // We know there will always be someone in the table but we should
            // follow good
            // programming.
            if (results.size() == 0)
            {
                data._labels = null;
                data._vals = null;
            }
            else
            {
                // Move the list from a Vector to an array and add "(All)" to
                // the beginning.
                int count = results.size() + ((flag_) ? 1 : 0);
                data._labels = new String[count];
                data._vals = new String[count];
                int ndx;
                if (flag_)
                {
                    data._labels[0] = Constants._STRALL;
                    data._vals[0] = Constants._STRALL;
                    ndx = 1;
                }
                else
                {
                    ndx = 0;
                }
                int cnt = 0;
                for (; ndx < count; ++ndx)
                {
                    rec = (tempData) results.get(cnt++);
                    data._labels[ndx] = rec._label.replaceAll("[\\s]", " ");
                    data._vals[ndx] = rec._val;
                }
            }
            data._rc = 0;
        }
        catch (SQLException ex)
        {
            // Bad...
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 25: " + _errorCode + ": " + select_
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            data._rc = _errorCode;
        }
        return data;
    }

    /**
     * Retrieve all the context id's for which a specific user has write
     * permission.
     * 
     * @param user_
     *        The user id as stored in user_accounts_view.ua_name.
     * @return The array of context id values.
     */
    public String[] selectContexts(String user_)
    {
        String select = "select cv.conte_idseq "
            + "from sbr.contexts_view cv, sbrext.user_contexts_view ucv "
            + "where ucv.ua_name = ? and ucv.privilege = 'W' and ucv.name not in ('TEST','Test','TRAINING','Training') and cv.name = ucv.name";

        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, user_);
            ResultSet rs = pstmt.executeQuery();
            Vector<String> list = new Vector<String>();
            while (rs.next())
            {
                list.add(rs.getString(1));
            }
            rs.close();
            pstmt.close();

            String temp[] = new String[list.size()];
            for (int ndx = 0; ndx < temp.length; ++ndx)
            {
                temp[ndx] = (String) list.get(ndx);
            }
            return temp;
        }
        catch (SQLException ex)
        {
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 26: " + _errorCode + ": " + select
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return null;
        }
    }

    /**
     * Retrieve the list of contexts for which a user has write permission.
     * 
     * @param user_
     *        The user id as stored in user_accounts_view.ua_name.
     * @return The concatenated comma separated string listing the context
     *         names.
     */
    public String selectContextString(String user_)
    {
        String select = "select name "
            + "from sbrext.user_contexts_view "
            + "where ua_name in (?) and privilege = 'W' and name not in ('TEST','Test','TRAINING','Training') "
            + "order by upper(name) ASC";
        String temp[] = new String[1];
        temp[0] = user_;
        return selectText(select, temp);
    }

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
     * @see com.scenpro.DSRAlert.DBAlert#getUserList getUserList()
     * @see com.scenpro.DSRAlert.DBAlert#getUserVals getUserVals()
     */
    public int getUsers()
    {
        // Get the user names and id's.
        String select = "select ua_name, nvl2(electronic_mail_address, 'y', 'n') || alert_ind as eflag, name "
            + "from sbr.user_accounts_view order by upper(name) ASC";
        returnData2 rec2 = getBasicData2(select);
        if (rec2._rc == 0)
        {
            _namesList = rec2._labels;
            _namesVals = rec2._id1;

            // Build the list of names that are exempt from Context Curator
            // Group broadcasts.
            _namesExempt = "";

            // Get the context names for which each id has write permission.
            select = "select uav.name, ucv.name "
                + "from sbrext.user_contexts_view ucv, sbr.user_accounts_view uav "
                + "where ucv.privilege = 'W' and ucv.ua_name = uav.ua_name "
                + "order by upper(uav.name) ASC, upper(ucv.name) ASC";
            returnData1 rec = getBasicData1(select, false);
            if (rec._rc == 0)
            {
                // Build the user list in the format <user name>[*] [(context
                // list)] where <user name> is the
                // in user presentable form followed by an optional asterisk to
                // indicate the email address is missing
                // from the user record followed by an optional list of context
                // names for those contexts which the
                // user has write permissions.
                int vcnt = 0;
                int ncnt = 1;
                String prefix = " (";
                String fixname = "";
                while (ncnt < _namesList.length && vcnt < rec._vals.length)
                {
                    int test = _namesList[ncnt].compareToIgnoreCase(rec._vals[vcnt]);

                    if (test < 0)
                    {
                        // Add the missing email flag to the suffix.
                        String suffix = "";
                        if (rec2._id2[ncnt].charAt(0) == 'n')
                            suffix = "*";

                        // Add the Context list to the suffix.
                        if (prefix.charAt(0) == ',')
                        {
                            if (rec2._id2[ncnt].charAt(1) == 'N')
                                _namesExempt = _namesExempt + ", "
                                    + _namesList[ncnt];
                            suffix = suffix + fixname + ")";
                            prefix = " (";
                            fixname = "";
                        }

                        // Add the suffix to the name.
                        _namesList[ncnt] = _namesList[ncnt] + suffix;
                        ++ncnt;
                    }
                    else if (test > 0)
                    {
                        ++vcnt;
                    }
                    else
                    {
                        fixname = fixname + prefix + rec._labels[vcnt];
                        prefix = ", ";
                        ++vcnt;
                    }
                }
            }

            // Fix the exempt list.
            if (_namesExempt.length() == 0)
                _namesExempt = null;
            else
                _namesExempt = _namesExempt.substring(2);
        }
        return rec2._rc;
    }

    /**
     * Retrieve the valid user list. The method getUsers() must be called first.
     * Once this method is used the internal copy is deleted to reclaim the
     * memory space.
     * 
     * @return An array of strings from the sbr.user_accounts.name column.
     * @see com.scenpro.DSRAlert.DBAlert#getUsers getUsers()
     */
    public String[] getUserList()
    {
        String temp[] = _namesList;
        _namesList = null;
        return temp;
    }

    /**
     * Retrieve the list of users exempt from Context Curator broadcasts. The
     * method getUsers() must be called first. Once this method is used the
     * internal copy is deleted to reclaim the memory space.
     * 
     * @return A comma separated list of names.
     * @see com.scenpro.DSRAlert.DBAlert#getUsers getUsers()
     */
    public String getUserExempts()
    {
        String temp = _namesExempt;
        _namesExempt = null;
        return temp;
    }

    /**
     * Retrieve the valid user list. The method getUsers() must be called first.
     * Once this method is used the internal copy is deleted to reclaim the
     * memory space.
     * 
     * @return An array of strings from the sbr.user_accounts.ua_name column.
     * @see com.scenpro.DSRAlert.DBAlert#getUsers getUsers()
     */
    public String[] getUserVals()
    {
        String temp[] = _namesVals;
        _namesVals = null;
        return temp;
    }

    /**
     * Retrieve the Context names and id's from the database. This follows the
     * pattern documented with getUsers().
     * 
     * @return 0 if successful, otherwise the database error code.
     * @see com.scenpro.DSRAlert.DBAlert#getUsers getUsers()
     * @see com.scenpro.DSRAlert.DBAlert#getGroupList getGroupList()
     * @see com.scenpro.DSRAlert.DBAlert#getGroupVals getGroupVals()
     */
    public int getGroups()
    {
        String select = "select uav.ua_name, '/' || cv.conte_idseq as id, 0, "
            + "uav.name || nvl2(uav.electronic_mail_address, '', '*') as name, ucv.name "
            + "from sbrext.user_contexts_view ucv, sbr.user_accounts_view uav, sbr.contexts_view cv "
            + "where ucv.privilege = 'W' "
            + "and ucv.ua_name = uav.ua_name "
            + "and uav.alert_ind = 'Yes' "
            + "and ucv.name = cv.name "
            + "and cv.conte_idseq NOT IN ( "
            + "select tov.value "
            + "from sbrext.tool_options_view_ext tov "
            + "where tov.tool_name = 'SENTINEL' and "
            + "tov.property like 'BROADCAST.EXCLUDE.CONTEXT.%.CONTE_IDSEQ') "
            + "order by upper(ucv.name) ASC, upper(uav.name) ASC";
        
        returnData3 rec = getBasicData3(select, false);
        if (rec._rc == 0)
        {
            // Count the number of groups.
            String temp = rec._id2[0];
            int cnt = 1;
            for (int ndx = 1; ndx < rec._id2.length; ++ndx)
            {
                if (!temp.equals(rec._id2[ndx]))
                {
                    temp = rec._id2[ndx];
                    ++cnt;
                }
            }
            
            // Allocate space for the lists.
            _groupsList = new String[cnt + rec._id1.length];
            _groupsVals = new String[_groupsList.length];
            
            // Copy the data.
            temp = "";
            cnt = 0;
            for (int ndx = 0; ndx < rec._id1.length; ++ndx)
            {
                if (!temp.equals(rec._id2[ndx]))
                {
                    temp = rec._id2[ndx];
                    _groupsList[cnt] = rec._label2[ndx];
                    _groupsVals[cnt] = rec._id2[ndx];
                    ++cnt;
                }
                _groupsList[cnt] = rec._label1[ndx];
                _groupsVals[cnt] = rec._id1[ndx];
                ++cnt;
            }
            return 0;
        }
        return rec._rc;
    }

    /**
     * Retrieve the valid context list. The method getGroups() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     * 
     * @return An array of strings from the sbr.contexts_view.name column.
     * @see com.scenpro.DSRAlert.DBAlert#getGroups getGroups()
     */
    public String[] getGroupList()
    {
        String temp[] = _groupsList;
        _groupsList = null;
        return temp;
    }

    /**
     * Retrieve the valid context id list. The method getGroups() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     * 
     * @return An array of strings from the sbr.contexts_view.conte_idseq column
     *         and prefixed with a '/' character.
     * @see com.scenpro.DSRAlert.DBAlert#getGroups getGroups()
     */
    public String[] getGroupVals()
    {
        String temp[] = _groupsVals;
        _groupsVals = null;
        return temp;
    }

    /**
     * Get the list of unused property records.
     * 
     * @return The list of name, public id and version
     */
    public String[] getUnusedProperties()
    {
        String select =
            "SELECT prop.prop_idseq, prop.long_name || '::' || prop.prop_id || 'v' || prop.version "
            + "FROM sbrext.properties_view_ext prop "
            + "WHERE prop.prop_idseq NOT IN "
            + "(SELECT decv.prop_idseq "
            + "FROM sbr.data_element_concepts_view decv "
            + "WHERE decv.prop_idseq = prop.prop_idseq) "
            + "order by upper(prop.long_name) asc, prop.prop_idseq asc";

        returnData1 rec = getBasicData1(select, false);
        if (rec._rc == 0)
        {
            return rec._labels;
        }
        return null;
    }

    /**
     * Get the list of unused data element concept records.
     * 
     * @return The list of name, public id and version
     */
    public String[] getUnusedDEC()
    {
        String select =
            "SELECT dec.dec_idseq, dec.long_name || '::' || dec.dec_id || 'v' || dec.version "
            + "FROM sbr.data_element_concepts_view dec "
            + "WHERE dec.dec_idseq NOT IN "
            + "(SELECT de.dec_idseq FROM sbr.data_elements_view de WHERE de.dec_idseq = dec.dec_idseq) "
            + "order by upper(dec.long_name) asc, dec.dec_idseq asc";

        returnData1 rec = getBasicData1(select, false);
        if (rec._rc == 0)
        {
            return rec._labels;
        }
        return null;
    }

    /**
     * Get the list of unused object class records.
     * 
     * @return The list of name, public id and version
     */
    public String[] getUnusedObjectClasses()
    {
        String select =
            "SELECT oc.oc_idseq, oc.long_name || '::' || oc.oc_id || 'v' || oc.version "
            + "FROM sbrext.object_classes_view_ext oc "
            + "WHERE oc.oc_idseq NOT IN " 
            +    "(SELECT decv.oc_idseq " 
            +    "FROM sbr.data_element_concepts_view decv " 
            +    "WHERE decv.OC_IDSEQ = oc.oc_idseq) "
            + "order by upper(oc.long_name), oc.oc_idseq";

        returnData1 rec = getBasicData1(select, false);
        if (rec._rc == 0)
        {
            return rec._labels;
        }
        return null;
    }
    
    /**
     * Retrieve the Context names and id's from the database. Follows the
     * pattern documented in getUsers().
     * 
     * @return 0 if successful, otherwise the database error code.
     * @see com.scenpro.DSRAlert.DBAlert#getUsers getUsers()
     * @see com.scenpro.DSRAlert.DBAlert#getContextList getContextList()
     * @see com.scenpro.DSRAlert.DBAlert#getContextVals getContextVals()
     */
    public int getContexts()
    {
        // Get the context names and id's.
        String select = "select conte_idseq, name from sbr.contexts_view "
            + "order by upper(name) ASC";
        returnData1 rec = getBasicData1(select, true);
        if (rec._rc == 0)
        {
            _contextList = rec._labels;
            _contextVals = rec._vals;
            return 0;
        }
        return rec._rc;
    }

    /**
     * Retrieve the valid context list. The method getGroups() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     * 
     * @return An array of strings from the sbr.contexts_view.name column.
     * @see com.scenpro.DSRAlert.DBAlert#getContexts getContexts()
     */
    public String[] getContextList()
    {
        String temp[] = _contextList;
        _contextList = null;
        return temp;
    }

    /**
     * Retrieve the valid context id list. The method getGroups() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     * 
     * @return An array of strings from the sbr.contexts_view.conte_idseq
     *         column.
     * @see com.scenpro.DSRAlert.DBAlert#getContexts getContexts()
     */
    public String[] getContextVals()
    {
        String temp[] = _contextVals;
        _contextVals = null;
        return temp;
    }

    /**
     * Get the complete Workflow Status value list from the database. Follows
     * the pattern documented in getUsers().
     * 
     * @return 0 if successful, otherwise the database error code.
     * @see com.scenpro.DSRAlert.DBAlert#getUsers getUsers()
     * @see com.scenpro.DSRAlert.DBAlert#getWorkflowList getWorkflowList()
     * @see com.scenpro.DSRAlert.DBAlert#getWorkflowVals getWorkflowVals()
     */
    public int getWorkflow()
    {
        // For compatibility and consistency we treat this view as all others as
        // if it has id and name
        // columns. For some reason this view is designed to expose the real id
        // to the end user.
        String select = "select asl_name, 'C' "
            + "from sbr.ac_status_lov_view order by upper(asl_name) ASC";

        returnData1 rec = getBasicData1(select, false);
        if (rec._rc == 0)
        {
            // Add the special values "(All)", "(Any Change)" and "(Ignore)"
            _workflowList = new String[rec._labels.length + 3];
            _workflowVals = new String[rec._labels.length + 3];
            int ndx = 0;
            _workflowList[ndx] = Constants._STRALL;
            _workflowVals[ndx++] = Constants._STRALL;
            _workflowList[ndx] = Constants._STRANY;
            _workflowVals[ndx++] = Constants._STRANY;
            _workflowList[ndx] = Constants._STRIGNORE;
            _workflowVals[ndx++] = Constants._STRIGNORE;
            for (int cnt = 0; cnt < rec._labels.length; ++cnt)
            {
                _workflowList[ndx] = rec._vals[cnt];
                _workflowVals[ndx++] = rec._vals[cnt];
            }

            // Add the special values "(All)", "(Any Change)" and "(Ignore)"
            _cworkflowList = new String[rec._labels.length + 1];
            _cworkflowVals = new String[rec._labels.length + 1];
            ndx = 0;
            _cworkflowList[ndx] = Constants._STRALL;
            _cworkflowVals[ndx++] = Constants._STRALL;
            for (int cnt = 0; cnt < rec._labels.length; ++cnt)
            {
                _cworkflowList[ndx] = rec._vals[cnt];
                _cworkflowVals[ndx++] = rec._vals[cnt];
            }
        }
        return rec._rc;
    }

    /**
     * Retrieve the valid workflow list. The method getWorkflow() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     * 
     * @return An array of strings from the sbr.ac_status_lov_view.asl_name
     *         column.
     * @see com.scenpro.DSRAlert.DBAlert#getWorkflow getWorkflow()
     */
    public String[] getWorkflowList()
    {
        String temp[] = _workflowList;
        _workflowList = null;
        return temp;
    }

    /**
     * Retrieve the valid workflow values. The method getWorkflow() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the sbr.ac_status_lov_view.asl_name
     *         column.
     * @see com.scenpro.DSRAlert.DBAlert#getWorkflow getWorkflow()
     */
    public String[] getWorkflowVals()
    {
        String temp[] = _workflowVals;
        _workflowVals = null;
        return temp;
    }

    /**
     * Retrieve the valid workflow list. The method getWorkflow() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     * 
     * @return An array of strings from the sbr.ac_status_lov_view.asl_name
     *         column.
     * @see com.scenpro.DSRAlert.DBAlert#getWorkflow getWorkflow()
     */
    public String[] getCWorkflowList()
    {
        String temp[] = _cworkflowList;
        _cworkflowList = null;
        return temp;
    }

    /**
     * Retrieve the valid workflow values. The method getWorkflow() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the sbr.ac_status_lov_view.asl_name
     *         column.
     * @see com.scenpro.DSRAlert.DBAlert#getWorkflow getWorkflow()
     */
    public String[] getCWorkflowVals()
    {
        String temp[] = _cworkflowVals;
        _cworkflowVals = null;
        return temp;
    }

    /**
     * Retrieve the valid registration statuses. Follows the pattern documented
     * in getUsers().
     * 
     * @return 0 if successful, otherwise the database error code.
     * @see com.scenpro.DSRAlert.DBAlert#getUsers getUsers()
     * @see com.scenpro.DSRAlert.DBAlert#getRegStatusList getRegStatusList()
     * @see com.scenpro.DSRAlert.DBAlert#getRegStatusVals getRegStatusVals()
     */
    public int getRegistrations()
    {
        // For compatibility and consistency we treat this view as all others as
        // if it has id and name
        // columns. For some reason this view is designed to expose the real id
        // to the end user.
        String select = "select registration_status, 'C' "
            + "from sbr.reg_status_lov_view "
            + "order by upper(registration_status) ASC";

        returnData1 rec = getBasicData1(select, false);
        if (rec._rc == 0)
        {
            // Add the special values "(All)", "(Any Change)" and "(Ignore)"
            _regStatusList = new String[rec._labels.length + 3];
            _regStatusVals = new String[rec._labels.length + 3];
            int ndx = 0;
            _regStatusList[ndx] = Constants._STRALL;
            _regStatusVals[ndx++] = Constants._STRALL;
            _regStatusList[ndx] = Constants._STRANY;
            _regStatusVals[ndx++] = Constants._STRANY;
            _regStatusList[ndx] = Constants._STRIGNORE;
            _regStatusVals[ndx++] = Constants._STRIGNORE;
            for (int cnt = 0; cnt < rec._labels.length; ++cnt)
            {
                _regStatusList[ndx] = rec._vals[cnt];
                _regStatusVals[ndx++] = rec._vals[cnt];
            }

            // Add the special value "(All)" and "(none)" for the Criteria entries
            _regCStatusList = new String[rec._labels.length + 2];
            _regCStatusVals = new String[rec._labels.length + 2];
            ndx = 0;
            _regCStatusList[ndx] = Constants._STRALL;
            _regCStatusVals[ndx++] = Constants._STRALL;
            _regCStatusList[ndx] = Constants._STRNONE;
            _regCStatusVals[ndx++] = Constants._STRNONE;
            for (int cnt = 0; cnt < rec._labels.length; ++cnt)
            {
                _regCStatusList[ndx] = rec._vals[cnt];
                _regCStatusVals[ndx++] = rec._vals[cnt];
            }
        }
        return rec._rc;
    }

    /**
     * Retrieve the registration status list. The method getRegistrations() must
     * be called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbr.reg_status_lov_view.registration_status column.
     * @see com.scenpro.DSRAlert.DBAlert#getRegistrations getRegistrations()
     */
    public String[] getRegStatusList()
    {
        String temp[] = _regStatusList;
        _regStatusList = null;
        return temp;
    }

    /**
     * Retrieve the registration status values list. The method
     * getRegistrations() must be called first. Once this method is used the
     * internal copy is deleted to reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbr.reg_status_lov_view.registration_status column.
     * @see com.scenpro.DSRAlert.DBAlert#getRegistrations getRegistrations()
     */
    public String[] getRegStatusVals()
    {
        String temp[] = _regStatusVals;
        _regStatusVals = null;
        return temp;
    }

    /**
     * Retrieve the registration status list. The method getRegistrations() must
     * be called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbr.reg_status_lov_view.registration_status column.
     * @see com.scenpro.DSRAlert.DBAlert#getRegistrations getRegistrations()
     */
    public String[] getRegCStatusList()
    {
        String temp[] = _regCStatusList;
        _regCStatusList = null;
        return temp;
    }

    /**
     * Retrieve the registration status values list. The method
     * getRegistrations() must be called first. Once this method is used the
     * internal copy is deleted to reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbr.reg_status_lov_view.registration_status column.
     * @see com.scenpro.DSRAlert.DBAlert#getRegistrations getRegistrations()
     */
    public String[] getRegCStatusVals()
    {
        String temp[] = _regCStatusVals;
        _regCStatusVals = null;
        return temp;
    }

    /**
     * Retrieve the Protocols from the database. Follows the
     * pattern documented in getUsers().
     * 
     * @return 0 if successful, otherwise the database error code.
     * @see com.scenpro.DSRAlert.DBAlert#getUsers getUsers()
     * @see com.scenpro.DSRAlert.DBAlert#getProtoList getProtoList()
     * @see com.scenpro.DSRAlert.DBAlert#getProtoVals getProtoVals()
     * @see com.scenpro.DSRAlert.DBAlert#getProtoContext getProtoContext()
     */
    public int getProtos()
    {
        String select = "select pr.conte_idseq, pr.proto_idseq, pr.long_name || ' (v' || pr.version || ' / ' || CV.name || ')' AS lname "
            + "from sbrext.protocols_view_ext pr, sbr.contexts_view cv "
            + "where cv.conte_idseq = pr.conte_idseq order by UPPER(lname) asc";

        returnData2 rec = getBasicData2(select);
        if (rec._rc == 0)
        {
            _protoList = rec._labels;
            _protoVals = rec._id2;
            _protoContext = rec._id1;
        }
        return rec._rc;
    }

    /**
     * Retrieve the protocol list. The method getProtos() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbrext.protocols_view_ext.long_name, version and context
     *         columns.
     * @see com.scenpro.DSRAlert.DBAlert#getProtos getProtos()
     */
    public String[] getProtoList()
    {
        String temp[] = _protoList;
        _protoList = null;
        return temp;
    }

    /**
     * Retrieve the protocol list. The method getProtos() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbrext.protocols_view_ext.proto_idseq column.
     * @see com.scenpro.DSRAlert.DBAlert#getProtos getProtos()
     */
    public String[] getProtoVals()
    {
        String temp[] = _protoVals;
        _protoVals = null;
        return temp;
    }

    /**
     * Retrieve the protocol list. The method getProtos() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbrext.protocols_view_ext.conte_idseq column.
     * @see com.scenpro.DSRAlert.DBAlert#getProtos getProtos()
     */
    public String[] getProtoContext()
    {
        String temp[] = _protoContext;
        _protoContext = null;
        return temp;
    }

    /**
     * Retrieve the Classification Schemes from the database. Follows the
     * pattern documented in getUsers().
     * 
     * @return 0 if successful, otherwise the database error code.
     * @see com.scenpro.DSRAlert.DBAlert#getUsers getUsers()
     * @see com.scenpro.DSRAlert.DBAlert#getSchemeList getSchemeList()
     * @see com.scenpro.DSRAlert.DBAlert#getSchemeVals getSchemeVals()
     * @see com.scenpro.DSRAlert.DBAlert#getSchemeContext getSchemeContext()
     */
    public int getSchemes()
    {
        String select = "select csv.conte_idseq, csv.cs_idseq, csv.long_name || ' (v' || csv.version || ' / ' || cv.name || ')' as lname "
            + "from sbr.classification_schemes_view csv, sbr.contexts_view cv "
            + "where cv.conte_idseq = csv.conte_idseq order by upper(lname) ASC";

        returnData2 rec = getBasicData2(select);
        if (rec._rc == 0)
        {
            _schemeList = rec._labels;
            _schemeVals = rec._id2;
            _schemeContext = rec._id1;
        }
        return rec._rc;
    }

    /**
     * Retrieve the classification scheme list. The method getSchemes() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbr.classification_schemes_view.long_name, version and context
     *         columns.
     * @see com.scenpro.DSRAlert.DBAlert#getSchemes getSchemes()
     */
    public String[] getSchemeList()
    {
        String temp[] = _schemeList;
        _schemeList = null;
        return temp;
    }

    /**
     * Retrieve the classification scheme id's. The method getSchemes() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbr.classification_schemes_view.cs_idseq column.
     * @see com.scenpro.DSRAlert.DBAlert#getSchemes getSchemes()
     */
    public String[] getSchemeVals()
    {
        String temp[] = _schemeVals;
        _schemeVals = null;
        return temp;
    }

    /**
     * Retrieve the context id's associated with the classification scheme id's
     * retrieved above. The method getSchemes() must be called first. Once this
     * method is used the internal copy is deleted to reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbr.classification_schemes_view.conte_idseq column.
     * @see com.scenpro.DSRAlert.DBAlert#getSchemes getSchemes()
     */
    public String[] getSchemeContext()
    {
        String temp[] = _schemeContext;
        _schemeContext = null;
        return temp;
    }

    private class schemeTree
    {
        /**
         * Constructor.
         * 
         * @param name_ The composite name for sorting.
         * @param ndx_ The index of the scheme item in the original list.
         */
        public schemeTree(String name_, int ndx_)
        {
            _fullName = name_;
            _ndx = ndx_;
        }

        /**
         * The composite name used for sorting.
         */
        public String _fullName;

        /**
         * The index in the original list.
         */
        public int    _ndx;
    }

    /**
     * Build the concatenated strings for the Class Scheme Items display.
     * 
     * @param rec_
     *        The data returned from Oracle.
     * @return An array of the full concatenated names for sorting later.
     */
    private schemeTree[] buildSchemeItemList(returnData3 rec_)
    {
        // Get the maximum number of levels and the maximum length of a single
        // name.
        int maxLvl = 0;
        int maxLen = 0;
        for (int ndx = 1; ndx < rec_._id3.length; ++ndx)
        {
            if (maxLvl < rec_._id3[ndx])
                maxLvl = rec_._id3[ndx];
            if (rec_._label1[ndx] != null
                && maxLen < rec_._label1[ndx].length())
                maxLen = rec_._label1[ndx].length();
            if (rec_._label2[ndx] != null
                && maxLen < rec_._label2[ndx].length())
                maxLen = rec_._label2[ndx].length();
        }
        ++maxLvl;

        // Build and array of prefixes for the levels.
        String prefix[] = new String[maxLvl];
        prefix[0] = "";
        if (maxLvl > 1)
        {
            prefix[1] = "";
            for (int ndx = 2; ndx < prefix.length; ++ndx)
            {
                prefix[ndx] = prefix[ndx - 1] + "    ";
            }
        }

        // In addition to creating the display labels we must also
        // create an array used to sort everything alphabetically.
        // The easiest is to create a fully concatenated label of a
        // individuals hierarchy.
        _schemeItemList = new String[rec_._label1.length];
        maxLvl *= maxLen;
        StringBuffer fullBuff = new StringBuffer(maxLvl);
        fullBuff.setLength(maxLvl);
        schemeTree tree[] = new schemeTree[_schemeItemList.length];

        // Loop through the name list.
        _schemeItemList[0] = rec_._label1[0];
        tree[0] = new schemeTree("", 0);
        for (int ndx = 1; ndx < _schemeItemList.length; ++ndx)
        {
            // Create the concatenated sort string.
            int buffOff = (rec_._id3[ndx] < 2) ? 0 : (rec_._id3[ndx] * maxLen);
            fullBuff.replace(buffOff, maxLvl, rec_._label1[ndx]);
            fullBuff.setLength(maxLvl);

            // Create the display label.
            if (rec_._id3[ndx] == 1)
            {
                if (rec_._label2[ndx] == null)
                {
                    _schemeItemList[ndx] = rec_._label1[ndx];
                }
                else
                {
                    _schemeItemList[ndx] = rec_._label1[ndx] + " ("
                        + rec_._label2[ndx] + ")";
                    fullBuff.replace(buffOff + maxLen, maxLvl,
                        rec_._label2[ndx]);
                    fullBuff.setLength(maxLvl);
                }
            }
            else
            {
                _schemeItemList[ndx] = prefix[rec_._id3[ndx]]
                    + rec_._label1[ndx];
            }
            tree[ndx] = new schemeTree(fullBuff.toString(), ndx);
        }
        return tree;
    }

    /**
     * Retrieve the Classification Scheme Items from the database. Follows the
     * pattern documented in getUsers().
     * 
     * @return 0 if successful, otherwise the database error code.
     * @see com.scenpro.DSRAlert.DBAlert#getUsers getUsers()
     * @see com.scenpro.DSRAlert.DBAlert#getSchemeItemList getSchemeItemList()
     * @see com.scenpro.DSRAlert.DBAlert#getSchemeItemVals getSchemeItemVals()
     * @see com.scenpro.DSRAlert.DBAlert#getSchemeItemSchemes
     *      getSchemeItemScheme()
     */
    public int getSchemeItems()
    {
        String select = "select cv.cs_idseq, cv.csi_idseq, level as lvl, "
            + "(select csi.csi_name from sbr.class_scheme_items_view csi where csi.csi_idseq = cv.csi_idseq), "
            + "(select cs.long_name || ' / v' || cs.version as xname from sbr.classification_schemes_view cs where cs.cs_idseq = cv.cs_idseq) "
            + "from sbr.cs_csi_view cv "
            + "start with cv.p_cs_csi_idseq is null "
            + "connect by prior cv.cs_csi_idseq = cv.p_cs_csi_idseq";

        returnData3 rec = getBasicData3(select, true);
        if (rec._rc == 0)
        {
            schemeTree tree[] = buildSchemeItemList(rec);
            _schemeItemVals = rec._id2;
            _schemeItemSchemes = rec._id1;
            sortSchemeItems(tree);
        }
        return rec._rc;
    }

    /**
     * Sort the scheme items lists and make everything right on the display.
     * 
     * @param tree_
     *        The concatenated name tree list.
     */
    private void sortSchemeItems(schemeTree tree_[])
    {
        // Too few items don't bother.
        if (tree_.length < 2)
            return;

        // The first element is the "All" indicator, so don't include it.
        schemeTree sort[] = new schemeTree[tree_.length];
        sort[0] = tree_[0];
        sort[1] = tree_[1];
        int top = 2;

        // Perform a binary search-insert.
        for (int ndx = 2; ndx < tree_.length; ++ndx)
        {
            int min = 1;
            int max = top;
            int check = 0;
            while (true)
            {
                check = (max + min) / 2;
                int test = tree_[ndx]._fullName
                    .compareToIgnoreCase(sort[check]._fullName);
                if (test == 0)
                    break;
                else if (test > 0)
                {
                    if (min == check)
                    {
                        ++check;
                        break;
                    }
                    min = check;
                }
                else
                {
                    if (max == check)
                        break;
                    max = check;
                }
            }
            // Add the record to the proper position in the sorted array.
            if (check < top)
                System.arraycopy(sort, check, sort, check + 1, top - check);
            ++top;
            sort[check] = tree_[ndx];
        }

        // Now arrange all the arrays based on the sorted index.
        String tempList[] = new String[_schemeItemList.length];
        String tempVals[] = new String[_schemeItemList.length];
        String tempSchemes[] = new String[_schemeItemList.length];
        for (int ndx = 0; ndx < sort.length; ++ndx)
        {
            int pos = sort[ndx]._ndx;
            tempList[ndx] = _schemeItemList[pos];
            tempVals[ndx] = _schemeItemVals[pos];
            tempSchemes[ndx] = _schemeItemSchemes[pos];
        }
        _schemeItemList = tempList;
        _schemeItemVals = tempVals;
        _schemeItemSchemes = tempSchemes;
    }

    /**
     * Retrieve the classification scheme item list. The method getSchemeItems()
     * must be called first. Once this method is used the internal copy is
     * deleted to reclaim the memory space.
     * 
     * @return An array of strings from the sbr.class_scheme_items_view.csi_name
     *         column.
     * @see com.scenpro.DSRAlert.DBAlert#getSchemeItems getSchemeItems()
     */
    public String[] getSchemeItemList()
    {
        String temp[] = _schemeItemList;
        _schemeItemList = null;
        return temp;
    }

    /**
     * Retrieve the classification scheme item id's. The method getSchemeItems()
     * must be called first. Once this method is used the internal copy is
     * deleted to reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbr.class_scheme_items_view.csi_idseq column.
     * @see com.scenpro.DSRAlert.DBAlert#getSchemeItems getSchemeItems()
     */
    public String[] getSchemeItemVals()
    {
        String temp[] = _schemeItemVals;
        _schemeItemVals = null;
        return temp;
    }

    /**
     * Retrieve the class scheme id's associated with the classification scheme
     * item id's retrieved above. The method getSchemeItems() must be called
     * first. Once this method is used the internal copy is deleted to reclaim
     * the memory space.
     * 
     * @return An array of strings from the sbr.class_scheme_items_view.cs_idseq
     *         column.
     * @see com.scenpro.DSRAlert.DBAlert#getSchemeItems getSchemeItems()
     */
    public String[] getSchemeItemSchemes()
    {
        String temp[] = _schemeItemSchemes;
        _schemeItemSchemes = null;
        return temp;
    }

    /**
     * Class used to return method results.
     */
    private class returnData2
    {
        /**
         * The database return code.
         */
        public int    _rc;

        /**
         * The database record id.
         */
        public String _id1[];

        /**
         * The database "parent" record id.
         */
        public String _id2[];

        /**
         * The label resutls.
         */
        public String _labels[];
    }

    /**
     * Perform the database access for a simple query which results in a 3
     * column value per returned row.
     * 
     * @param select_
     *        The SQL select to run.
     * @return 0 if successful, otherwise the database error code.
     */
    private returnData2 getBasicData2(String select_)
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        class tempData2
        {
            public String _id1;

            public String _id2;

            public String _label;
        }
        ;
        Vector<tempData2> results = new Vector<tempData2>();
        returnData2 data = new returnData2();

        try
        {
            // Prepare the statement.
            pstmt = _conn.prepareStatement(select_);

            // Get the list.
            rs = pstmt.executeQuery();
            tempData2 rec;
            while (rs.next())
            {
                // Remember about the 1 (one) based indexing.
                rec = new tempData2();
                rec._id1 = rs.getString(1);
                rec._id2 = rs.getString(2);
                rec._label = rs.getString(3);
                results.add(rec);
            }
            pstmt.close();
            rs.close();

            // Move the list from a Vector to an array and add "(All)" to
            // the beginning.
            int count = results.size() + 1;
            data._labels = new String[count];
            data._id1 = new String[count];
            data._id2 = new String[count];
            data._labels[0] = Constants._STRALL;
            data._id1[0] = Constants._STRALL;
            data._id2[0] = Constants._STRALL;
            int cnt = 0;
            for (int ndx = 1; ndx < count; ++ndx)
            {
                rec = (tempData2) results.get(cnt++);
                data._labels[ndx] = rec._label.replaceAll("[\\s]", " ");
                data._id1[ndx] = rec._id1;
                data._id2[ndx] = rec._id2;
            }
            
            data._rc = 0;
        }
        catch (SQLException ex)
        {
            // Bad...
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 27: " + _errorCode + ": " + select_
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            data._rc = _errorCode;
        }
        return data;
    }

    /**
     * Class used to return method results.
     */
    private class returnData3
    {
        /**
         * The database return code.
         */
        public int    _rc;

        /**
         * The database record id.
         */
        public String _id1[];

        /**
         * The database "parent" id.
         */
        public String _id2[];

        /**
         * The record public id.
         */
        public int    _id3[];

        /**
         * The primary label results.
         */
        public String _label1[];

        /**
         * The second label results.
         */
        public String _label2[];
    }

    /**
     * Perform the database access for a simple query which results in a 4
     * column value per returned row.
     * 
     * @param select_
     *        The SQL select to run.
     * @param flag_ true if the list should be prefixed with "All".
     * @return 0 if successful, otherwise the database error code.
     */
    private returnData3 getBasicData3(String select_, boolean flag_)
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        class tempData3
        {
            public String _id1;

            public String _id2;

            public int    _id3;

            public String _label1;

            public String _label2;
        }
        ;
        Vector<tempData3> results = new Vector<tempData3>();
        returnData3 data = new returnData3();

        try
        {
            // Prepare the statement.
            pstmt = _conn.prepareStatement(select_);

            // Get the list.
            rs = pstmt.executeQuery();
            tempData3 rec;
            while (rs.next())
            {
                // Remember about the 1 (one) based indexing.
                rec = new tempData3();
                rec._id1 = rs.getString(1);
                rec._id2 = rs.getString(2);
                rec._id3 = rs.getInt(3);
                rec._label1 = rs.getString(4);
                rec._label2 = rs.getString(5);
                results.add(rec);
            }
            pstmt.close();
            rs.close();

            // Move the list from a Vector to an array and add "(All)" to
            // the beginning.
            int offset = (flag_) ? 1 : 0;
            int count = results.size() + offset;
            data._label1 = new String[count];
            data._label2 = new String[count];
            data._id1 = new String[count];
            data._id2 = new String[count];
            data._id3 = new int[count];
            if (flag_)
            {
                data._label1[0] = Constants._STRALL;
                data._label2[0] = Constants._STRALL;
                data._id1[0] = Constants._STRALL;
                data._id2[0] = Constants._STRALL;
                data._id3[0] = 0;
            }
            int cnt = 0;
            for (int ndx = offset; ndx < count; ++ndx)
            {
                rec = (tempData3) results.get(cnt++);
                data._label1[ndx] = rec._label1.replaceAll("[\\s]", " ");
                data._label2[ndx] = rec._label2.replaceAll("[\\s]", " ");
                data._id1[ndx] = rec._id1;
                data._id2[ndx] = rec._id2;
                data._id3[ndx] = rec._id3;
            }
            data._rc = 0;
        }
        catch (SQLException ex)
        {
            // Bad...
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 27: " + _errorCode + ": " + select_
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            data._rc = _errorCode;
        }
        return data;
    }

    /**
     * Retrieve the list of record types.  As this is coded in a constant
     * array, no database access is required.
     * 
     * @return 0 if successful.
     */
    public int getACTypes()
    {
        String[] list = new String[_DBMAP3VALS.length + 1];
        String[] vals = new String[_DBMAP3KEYS.length + 1];
        list[0] = Constants._STRALL;
        vals[0] = Constants._STRALL;
        list[1] = _DBMAP3VALS[0];
        vals[1] = _DBMAP3KEYS[0];

        // Put the descriptive text in alphabetic order for display.
        // Of course we have to keep the key-value pairs intact.
        for (int ndx = 1; ndx < _DBMAP3VALS.length; ++ndx)
        {
            int min = 1;
            int max = ndx + 1;
            int pos = 1;
            while (true)
            {
                pos = (max + min) / 2;
                int compare = _DBMAP3VALS[ndx].compareTo(list[pos]);
                if (compare == 0)
                {
                    // Can't happen.
                }
                else if (compare > 0)
                {
                    if (min == pos)
                    {
                        ++pos;
                        break;
                    }
                    min = pos;
                }
                else
                {
                    if (max == pos)
                    {
                        break;
                    }
                    max = pos;
                }
            }

            // Preserve existing entries - an insert.
            if (pos <= ndx)
            {
                System.arraycopy(list, pos, list, pos + 1, ndx - pos + 1);
                System.arraycopy(vals, pos, vals, pos + 1, ndx - pos + 1);
            }
            
            // Insert new item in list.
            list[pos] = _DBMAP3VALS[ndx];
            vals[pos] = _DBMAP3KEYS[ndx];
        }
        
        // Keep the results.
        _actypesList = list;
        _actypesVals = vals;
        return 0;
    }

    /**
     * Return the descriptive names for the record types.
     * 
     * @return The list of display values.
     */
    public String[] getACTypesList()
    {
        String temp[] = _actypesList;
        _actypesList = null;
        return temp;
    }
    
    /**
     * Return the internal values used to identify the record types.
     * 
     * @return The list of internal record types.
     */
    public String[] getACTypesVals()
    {
        String temp[] = _actypesVals;
        _actypesVals = null;
        return temp;
    }
    
    /**
     * Look for the selection of a specific record type.
     * 
     * @param val_ The AC type code.
     * @return false if the record type is not found.
     */
    static public int isACTypeUsed(String val_)
    {
        return binarySearch(_DBMAP3KEYS, val_);
    }
    
    /**
     * Retrieve the list of forms and templates from the database. Follows the
     * pattern documented in getUsers().
     * 
     * @return 0 if successful, otherwise the database error code.
     * @see com.scenpro.DSRAlert.DBAlert#getUsers getUsers()
     * @see com.scenpro.DSRAlert.DBAlert#getFormsList getFormsList()
     * @see com.scenpro.DSRAlert.DBAlert#getFormsVals getFormsVals()
     * @see com.scenpro.DSRAlert.DBAlert#getFormsContext getFormsContext()
     */
    public int getForms()
    {
        // Build a composite descriptive string for this form.
        String select = 
            "select qcv.conte_idseq, qcv.qc_idseq, qcv.long_name || "
            + "' (v' || qcv.version || ' / ' || qcv.qtl_name || ' / ' || nvl(proto.long_name, '(' || cv.name || ')') || ')' as lname "
            + "from sbrext.quest_contents_view_ext qcv, sbr.contexts_view cv, "
            + "sbrext.protocol_qc_ext pq, sbrext.protocols_view_ext proto "
            + "where qcv.qtl_name in ('TEMPLATE','CRF') "
            + "and cv.conte_idseq = qcv.conte_idseq "
            + "and qcv.qc_idseq = pq.qc_idseq(+) "
            + "and pq.proto_idseq = proto.proto_idseq(+) "
            + "order by upper(lname)";

        returnData2 rec = getBasicData2(select);
        if (rec._rc == 0)
        {
            _formsList = rec._labels;
            for (int ndx = 0; ndx < _formsList.length; ++ndx)
            {
                // Can you believe that some people put quotes in the name? We
                // have to escape them or it causes
                // problems downstream.
                _formsList[ndx] = _formsList[ndx].replaceAll("[\"]", "\\\\\"");
                _formsList[ndx] = _formsList[ndx].replaceAll("[\\r\\n]", " ");
            }
            _formsVals = rec._id2;
            _formsContext = rec._id1;
        }
        return rec._rc;
    }

    /**
     * Return the forms/templates composite names. The method getForms() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbrext.quest_contents_view_ext.long_name, ... columns.
     * @see com.scenpro.DSRAlert.DBAlert#getForms getForms()
     */
    public String[] getFormsList()
    {
        String temp[] = _formsList;
        _formsList = null;
        return temp;
    }

    /**
     * Return the forms/templates id values. The method getForms() must be
     * called first. Once this method is used the internal copy is deleted to
     * reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbrext.quest_contents_view_ext.qc_idseq columns.
     * @see com.scenpro.DSRAlert.DBAlert#getForms getForms()
     */
    public String[] getFormsVals()
    {
        String temp[] = _formsVals;
        _formsVals = null;
        return temp;
    }

    /**
     * Return the context id's associated with the forms/templates. The method
     * getForms() must be called first. Once this method is used the internal
     * copy is deleted to reclaim the memory space.
     * 
     * @return An array of strings from the
     *         sbrext.quest_contents_view_ext.conte_idseq columns.
     * @see com.scenpro.DSRAlert.DBAlert#getForms getForms()
     */
    public String[] getFormsContext()
    {
        String temp[] = _formsContext;
        _formsContext = null;
        return temp;
    }

    /**
     * Return the last recorded database error message. If the current error
     * code is zero (0) an empty string is returned.
     * 
     * @return The last database error message or an empty string.
     * @see com.scenpro.DSRAlert.DBAlert#getErrorCode getErrorCode()
     */
    public String getErrorMsg()
    {
        return (_errorCode != 0) ? _errorMsg : null;
    }

    /**
     * Return the last recorded database error message. If the current error
     * code is zero (0) an empty string is returned.
     * 
     * @param flag_
     *        True if the new lines ('\n') should be expanded to text for use in
     *        script. False to return the message unaltered.
     * @return The last database error message or an empty string.
     * @see com.scenpro.DSRAlert.DBAlert#getErrorCode getErrorCode()
     */
    public String getErrorMsg(boolean flag_)
    {
        return (_errorCode != 0) ? ((flag_) ? _errorMsg.replaceAll("[\\n]",
            "\\\\n") : _errorMsg) : null;
    }

    /**
     * Return the last recorded database error code and then reset it to zero
     * (0).
     * 
     * @return The database error code.
     */
    public int getErrorCode()
    {
        int rc = _errorCode;
        _errorCode = 0;
        return rc;
    }

    /**
     * Return any error message and reset the error code to zero for the next
     * possible error.
     * 
     * @return The database error message.
     */
    public String getError()
    {
        String temp = getErrorMsg();
        if (temp != null)
            _errorCode = 0;
        return temp;
    }

    /**
     * Get the Alerts which are active for the target date provided.
     * 
     * @param target_
     *        The target date, typically the date an Auto Run process is
     *        started.
     * @return null if an error, otherwise the list of valid alert definitions.
     */
    public AlertRec[] selectAlerts(Timestamp target_)
    {
        String select = "select al_idseq, name, created_by "
            + "from sbrext.sn_alert_view_ext " + "where al_status <> 'I' AND "
            + "(auto_freq_unit = 'D' OR "
            + "(auto_freq_unit = 'W' AND auto_freq_value = ?) OR "
            + "(auto_freq_unit = 'M' AND auto_freq_value = ?)) "
            + "order by upper(created_by) asc, upper(name) asc";

        // Get day and date from target to qualify the select.
        GregorianCalendar tdate = new GregorianCalendar();
        tdate.setTimeInMillis(target_.getTime());
        int dayWeek = tdate.get(Calendar.DAY_OF_WEEK);
        int dayMonth = tdate.get(Calendar.DAY_OF_MONTH);

        try
        {
            // Set SQL arguments
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setInt(1, dayWeek);
            pstmt.setInt(2, dayMonth);

            // Retrieve all applicable definition ids.
            ResultSet rs = pstmt.executeQuery();
            Vector<String> list = new Vector<String>();
            while (rs.next())
            {
                list.add(rs.getString(1));
            }
            rs.close();
            pstmt.close();

            // There may be nothing to do.
            if (list.size() == 0)
                return new AlertRec[0];

            // retrieve the full alert definition, we will need it.
            AlertRec recs[] = new AlertRec[list.size()];
            int keep = 0;
            int ndx;
            for (ndx = 0; ndx < recs.length; ++ndx)
            {
                // Be sure we can read the Alert Definition.
                recs[keep] = selectAlert((String) list.get(ndx));
                if (recs[keep] == null)
                    return null;

                // Check the date. We do this here and not in the SQL because
                // 99.99% of the time this will return true and complicating the
                // SQL isn't necessary.
                if (recs[keep].isActive(target_))
                    ++keep;

                // In the RARE situation that the alert is inactive at this
                // point,
                // we reset the object pointer to release the memory.
                else
                    recs[keep] = null;
            }

            // Return the results. It is possible that sometimes the last entry
            // in the
            // list will be null. Consequently the use of the list should be in
            // a loop
            // with the following condition: "cnt < recs.length && recs[cnt] !=
            // null"
            if (keep == ndx)
                return recs;
 
            // Only process the ones that are Active.
            AlertRec trecs[] = new AlertRec[keep];
            for (ndx = 0; ndx < keep; ++ndx)
                trecs[ndx] = recs[ndx];
            return trecs;
        }
        catch (SQLException ex)
        {
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 28: " + _errorCode + ": " + select
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return null;
        }
    }

    /**
     * Convert a Vector of Strings to an array.
     * 
     * @param list_
     *        The vector.
     * @return The string array.
     */
    private String[] paste(Vector<String> list_)
    {
        String temp[] = new String[list_.size()];
        for (int ndx = 0; ndx < temp.length; ++ndx)
            temp[ndx] = list_.get(ndx);
        return temp;
    }
    
    /**
     * Convert a Vector of Timestamps to an array.
     * 
     * @param list_ The vector.
     * @return The Timestamp array.
     */
    private Timestamp[] paste(Vector<Timestamp> list_)
    {
        Timestamp temp[] = new Timestamp[list_.size()];
        for (int ndx = 0; ndx < temp.length; ++ndx)
            temp[ndx] = list_.get(ndx);
        return temp;
    }

    /**
     * Copy the result set to an ACData array.
     * 
     * @param rs_
     *        The query result set.
     * @return The ACData array if successful, otherwise null.
     * @throws java.sql.SQLException
     *         When there is a problem with the result set.
     */
    private ACData[] copyResults(ResultSet rs_) throws SQLException
    {
        Vector<ACData> data = new Vector<ACData>();
        Vector<String> changes = new Vector<String>();
        Vector<String> oval = new Vector<String>();
        Vector<String> nval = new Vector<String>();
        Vector<String> tabl = new Vector<String>();
        Vector<String> chgby = new Vector<String>();
        Vector<Timestamp> dval = new Vector<Timestamp>();
        String clist[] = null;
        String olist[] = null;
        String nlist[] = null;
        String tlist[] = null;
        String blist[] = null;
        Timestamp dlist[] = null;
        ACData oldrec = null;
        int cols = rs_.getMetaData().getColumnCount();
        while (rs_.next())
        {
            ACData rec = new ACData();
            rec.set(
                rs_.getString(1).charAt(0),
                rs_.getInt(2),
                rs_.getString(3),
                rs_.getString(4),
                rs_.getString(5),
                rs_.getInt(6),
                rs_.getString(7),
                rs_.getString(8),
                rs_.getTimestamp(9),
                rs_.getTimestamp(10),
                rs_.getString(11),
                rs_.getString(12),
                rs_.getString(13),
                rs_.getString(14),
                rs_.getString(15));

            // We don't want to waste time or space with records that are
            // identical. We can't use a SELECT DISTINCT for this logic as the
            // ACData.equals doesn't look at all data elements but only specific ones.
            if (oldrec != null)
            {
                if (!oldrec.isEquivalent(rec))
                {
                    clist = paste(changes);
                    olist = paste(oval);
                    nlist = paste(nval);
                    dlist = paste(dval);
                    tlist = paste(tabl);
                    blist = paste(chgby);
                    oldrec.setChanges(clist, olist, nlist, dlist, tlist, blist);
                    data.add(oldrec);

                    changes = new Vector<String>();
                    oval = new Vector<String>();
                    nval = new Vector<String>();
                    dval = new Vector<Timestamp>();
                    tabl = new Vector<String>();
                    chgby = new Vector<String>();
                }
            }

            // Build the list of specific changes if we can get them. We must
            // save
            // always save the information if present.
            //
            // NOTE we only record the first 18 columns of the result set but
            // there may be
            // more to make the SQL work as desired.
            if (cols > 17)
            {
                String ctext = rs_.getString(16);

                // If the "change" column is blank don't waste the space.
                if (ctext != null && ctext.length() > 0)
                {
                    changes.add(ctext);
                    oval.add(rs_.getString(17));
                    nval.add(rs_.getString(18));
                    dval.add(rs_.getTimestamp(19));
                    tabl.add(rs_.getString(20));
                    chgby.add(rs_.getString(21));
                }
            }
            oldrec = rec;
        }
        if (oldrec != null)
        {
            clist = paste(changes);
            olist = paste(oval);
            nlist = paste(nval);
            dlist = paste(dval);
            tlist = paste(tabl);
            blist = paste(chgby);
            oldrec.setChanges(clist, olist, nlist, dlist, tlist, blist);
            data.add(oldrec);
        }

        ACData list[] = new ACData[data.size()];
        if (data.size() > 0)
        {
            for (int ndx = 0; ndx < list.length; ++ndx)
            {
                list[ndx] = (ACData) data.get(ndx);
            }
        }
        return list;
    }

    /**
     * Pull rows changed in the date range specified. There are 3 different
     * patterns to handle:
     * <p>
     * <ul>
     * <li>[from/to, from/to] {2, 4} - This represents the from/to date pair
     * which may occur 2 or 4 times in the SQL. This pattern is handled by this
     * method argument list.</li>
     * <li>[in, from/to, from/to] {2, 4} - This represents a single "in" clause
     * of creators or modifiers followed by the from/to pair which may occur 2
     * or 4 times in the SQL in this order.</li>
     * <li>[in, in, from/to, from/to] {2,4} - This represents an "in" clause
     * for the creators and an "in" clause for the modifiers followed by the
     * from/to pair which in total may appear 1 or 2 times.</li>
     * </ul>
     * </p>
     * 
     * @param select_
     *        The SQL select for the specific data and table.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param pairs_
     *        The number of pairs of (start, end) that appear in the SQL.
     * @return 0 if successful, otherwise the database error code.
     */
    private ACData[] selectAC(String select_, Timestamp start_, Timestamp end_,
        int pairs_)
    {
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select_);
            for (int ndx = 0; ndx < pairs_; ++ndx)
            {
                pstmt.setTimestamp((ndx * 2) + 1, start_);
                pstmt.setTimestamp((ndx * 2) + 2, end_);
            }

            ResultSet rs = pstmt.executeQuery();
            ACData list[] = copyResults(rs);
            rs.close();
            pstmt.close();
            return list;
        }
        catch (SQLException ex)
        {
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 29: " + _errorCode + ": " + select_
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return null;
        }
    }

    /**
     * Generate a string of comma separated SQL arguments for us in an "in"
     * clause.
     * 
     * @param cnt_
     *        The number of place holders needed.
     * @return String The comma separated string without parentheses.
     */
    private String expandMarkers(int cnt_)
    {
        String markers = "?";
        for (int ndx = 1; ndx < cnt_; ++ndx)
        {
            markers = markers + ",?";
        }
        return markers;
    }

    /**
     * Pull rows changed in the date range specified. There are 3 different
     * patterns to handle:
     * <p>
     * <ul>
     * <li>[from/to, from/to] {2, 4} - This represents the from/to date pair
     * which may occur 2 or 4 times in the SQL.</li>
     * <li>[in, from/to, from/to] {2, 4} - This represents a single "in" clause
     * of creators or modifiers followed by the from/to pair which may occur 2
     * or 4 times in the SQL in this order. This pattern is handled by this
     * method argument list.</li>
     * <li>[in, in, from/to, from/to] {2,4} - This represents an "in" clause
     * for the creators and an "in" clause for the modifiers followed by the
     * from/to pair which in total may appear 1 or 2 times.</li>
     * </ul>
     * </p>
     * 
     * @param select_
     *        The SQL select for the specific data and table.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param pairs_
     *        The number of pairs of (start, end) that appear in the SQL.
     * @param vals_
     *        The additional values used by an "in" clause.
     * @return 0 if successful, otherwise the database error code.
     */
    private ACData[] selectAC(String select_, Timestamp start_, Timestamp end_,
        int pairs_, String vals_[])
    {
        // Expand the "in" clause.
        int loop = pairs_ / 2;
        String markers = expandMarkers(vals_.length);

        String parts[] = select_.split("\\?");
        int pos = 0;
        String select = parts[pos++];
        for (int cnt = 0; cnt < loop; ++cnt)
        {
            select = select + markers + parts[pos++];
            for (int ndx = 0; ndx < 2; ++ndx)
            {
                select = select + "?" + parts[pos++] + "?" + parts[pos++];
            }
        }

        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            int arg = 1;
            for (int cnt = 0; cnt < loop; ++cnt)
            {
                for (int ndx = 0; ndx < vals_.length; ++ndx)
                {
                    pstmt.setString(arg++, vals_[ndx]);
                }
                for (int ndx = 0; ndx < 2; ++ndx)
                {
                    pstmt.setTimestamp(arg++, start_);
                    pstmt.setTimestamp(arg++, end_);
                }
            }
            ResultSet rs = pstmt.executeQuery();
            ACData list[] = copyResults(rs);
            rs.close();
            pstmt.close();
            return list;
        }
        catch (SQLException ex)
        {
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 30: " + _errorCode + ": " + select
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return null;
        }
    }

    /**
     * Pull rows changed in the date range specified. There are 3 different
     * patterns to handle:
     * <p>
     * <ul>
     * <li>[from/to, from/to] {2, 4} - This represents the from/to date pair
     * which may occur 2 or 4 times in the SQL.</li>
     * <li>[in, from/to, from/to] {2, 4} - This represents a single "in" clause
     * of creators or modifiers followed by the from/to pair which may occur 2
     * or 4 times in the SQL in this order.</li>
     * <li>[in, in, from/to, from/to] {2,4} - This represents an "in" clause
     * for the creators and an "in" clause for the modifiers followed by the
     * from/to pair which in total may appear 1 or 2 times. This pattern is
     * handled by this method argument list.</li>
     * </ul>
     * </p>
     * 
     * @param select_
     *        The SQL select for the specific data and table.
     * @param start_
     *        The date to start.
     * @param end_
     *        The date to end.
     * @param pairs_
     *        The number of pairs of (start, end) that appear in the SQL.
     * @param vals1_
     *        The additional values used by an "in" clause.
     * @param vals2_
     *        The additional values used by a second "in" clause.
     * @return 0 if successful, otherwise the database error code.
     */
    private ACData[] selectAC(String select_, Timestamp start_, Timestamp end_,
        int pairs_, String vals1_[], String vals2_[])
    {
        // Expand the "in" clauses.
        String parts[] = select_.split("\\?");
        int loop = pairs_ / 2;

        String markers1 = expandMarkers(vals1_.length);
        String markers2 = expandMarkers(vals2_.length);
        int pos = 0;
        String select = parts[pos++];

        for (int cnt = 0; cnt < loop; ++cnt)
        {
            select = select + markers1 + parts[pos++] + markers2 + parts[pos++];
            for (int ndx = 0; ndx < 2; ++ndx)
            {
                select = select + "?" + parts[pos++] + "?" + parts[pos++];
            }
        }

        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            int arg = 1;
            for (int cnt = 0; cnt < loop; ++cnt)
            {
                for (int ndx = 0; ndx < vals1_.length; ++ndx)
                {
                    pstmt.setString(arg++, vals1_[ndx]);
                }
                for (int ndx = 0; ndx < vals2_.length; ++ndx)
                {
                    pstmt.setString(arg++, vals2_[ndx]);
                }
                for (int ndx = 0; ndx < 2; ++ndx)
                {
                    pstmt.setTimestamp(arg++, start_);
                    pstmt.setTimestamp(arg++, end_);
                }
            }
            ResultSet rs = pstmt.executeQuery();
            ACData list[] = copyResults(rs);
            rs.close();
            pstmt.close();
            return list;
        }
        catch (SQLException ex)
        {
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 31: " + _errorCode + ": " + select
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return null;
        }
    }

    private ACData[] selectAC(String select_)
    {
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select_);
            ResultSet rs = pstmt.executeQuery();
            ACData list[] = copyResults(rs);
            rs.close();
            pstmt.close();
            return list;
        }
        catch (SQLException ex)
        {
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 31: " + _errorCode + ": " + select_
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return null;
        }
    }
    
    /**
     * Build a complex SQL from individual phrases. The calling method provides
     * an array of strings for the SQL SELECT with each representing a specific
     * part of the combined statement. Using the presence or absence of other
     * argument values, a composite statement is formed and executed.
     * 
     * @param select_
     *        The array of component parts of the SQL SELECT.
     * @param start_
     *        The start date of the date range. The record date must be greater
     *        than or equal to this value.
     * @param end_
     *        The end date of the date range. The record date must be less than
     *        this value.
     * @param pairs_
     *        The number of date pairs that appear in the master array.
     * @param creators_
     *        The creator (created by) ids of the records or null.
     * @param modifiers_
     *        The modifier (modified by) ids of the records or null.
     * @return The result of the composite SQL.
     */
    private ACData[] selectAC(String select_[], Timestamp start_,
        Timestamp end_, int pairs_, String creators_[], String modifiers_[])
    {
        String select = select_[0];
        int pattern = 0;
        if (creators_ != null && creators_[0].charAt(0) != '(')
        {
            pattern += 1;
            select = select + select_[1];
        }
        if (modifiers_ != null && modifiers_[0].charAt(0) != '(')
        {
            pattern += 2;
            select = select + select_[2];
        }
        select = select + select_[3];
        if (pairs_ == 4)
        {
            if (creators_ != null && creators_[0].charAt(0) != '(')
                select = select + select_[4];
            if (modifiers_ != null && modifiers_[0].charAt(0) != '(')
                select = select + select_[5];
            select = select + select_[6];
        }

        switch (pattern)
        {
            case 1:
                return selectAC(select, start_, end_, pairs_, creators_);
            case 2:
                return selectAC(select, start_, end_, pairs_, modifiers_);
            case 3:
                return selectAC(select, start_, end_, pairs_, creators_,
                    modifiers_);
            default:
                return selectAC(select, start_, end_, pairs_);
        }
    }

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
        String creators_[], String modifiers_[])
    {
        // There's always one that doesn't fit the pattern. Any changes to
        // selectBuild() must also be checked here for consistency.
        
        String start = "to_date('" + start_.toString().substring(0, 10) + "', 'yyyy/mm/dd')";
        String end = "to_date('" + end_.toString().substring(0, 10) + "', 'yyyy/mm/dd')";
        
        String select =
            "select 'p', 1, 'pv', zz.pv_idseq as id, '', -1, zz.value, '', "
            + "zz.date_modified, zz.date_created, zz.modified_by, zz.created_by, '', "
            + "'', '', ach.changed_column, ach.old_value, ach.new_value, ach.change_datetimestamp, ach.changed_table, ach.changed_by "
            + "from sbrext.ac_change_history_ext ach, sbr.permissible_values_view zz ";

        select = select + "where ach.change_datetimestamp >= " + start + " and ach.change_datetimestamp < " + end + " ";
        if (modifiers_ != null && modifiers_.length > 0 && modifiers_[0].charAt(0) != '(')
            select = select + "AND ach.changed_by in " + selectIN(modifiers_);
        select = select + whereACH(_ACTYPE_PV) 
            + "AND zz.pv_idseq = ach.ac_idseq ";
        if (creators_ != null && creators_.length > 0 && creators_[0].charAt(0) != '(')
            select = select + "AND zz.created_by in " + selectIN(creators_);
        
        if (dates_ == _DATECONLY)
            select = select + "AND zz.date_created >= " + start + " and zz.date_created < " + end + " ";
        else if (dates_ == _DATEMONLY)
            select = select + "AND zz.date_modified is not NULL ";

        select = select + _orderbyACH;

        return selectAC(select);
    }

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
        String creators_[], String modifiers_[], String wstatus_[])
    {
        return selectAC(
            selectBuild(_ACTYPE_VD,
                dates_, start_, end_, creators_, modifiers_, wstatus_, null));
    }

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
        String creators_[], String modifiers_[], String wstatus_[])
    {
        String wfs_clause;
        if (wstatus_ == null || wstatus_.length == 0)
        {
            wfs_clause = "";
        }
        else
        {
            wfs_clause = "AND cd.asl_name IN " + selectIN(wstatus_);
        }

        int pairs;
        String select[] = new String[7];
        select[0] = "(select 'p', 1, 'cd', cd.cd_idseq as id, cd.version, cd.cd_id, cd.long_name, cd.conte_idseq as cid, "
            + "cd.date_modified as ctime, cd.date_created, cd.modified_by, cd.created_by, cd.change_note, c.name, '' "
            + "from sbr.conceptual_domains_view cd, sbr.contexts_view c "
            + "where c.conte_idseq = cd.conte_idseq and ";
        select[1] = "cd.created_by in (?) and ";
        select[2] = "cd.modified_by in (?) and ";

        pairs = 2;
        select[3] = "((cd.date_modified is not null and cd.date_modified "
            + _DATECHARS[dates_][0] + " ? and cd.date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (cd.date_created is not null and cd.date_created "
            + _DATECHARS[dates_][2] + " ? and cd.date_created " + _DATECHARS[dates_][3] + " ?)) "
            + wfs_clause
            + ") order by id asc, cid asc";

        return selectAC(select, start_, end_, pairs, creators_, modifiers_);
    }

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
        String creators_[], String modifiers_[], String wstatus_[])
    {
        return selectAC(
            selectBuild(_ACTYPE_CS,
                dates_, start_, end_, creators_, modifiers_, wstatus_, null));
    }

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
        String creators_[], String modifiers_[], String wstatus_[])
    {
        return selectAC(
            selectBuild(_ACTYPE_PROP,
                dates_, start_, end_, creators_, modifiers_, wstatus_, null));
    }

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
        String creators_[], String modifiers_[], String wstatus_[])
    {
        return selectAC(
            selectBuild(_ACTYPE_OC,
                dates_, start_, end_, creators_, modifiers_, wstatus_, null));
    }

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
        String creators_[], String modifiers_[], String wstatus_[])
    {
        String wfs_clause;
        if (wstatus_ == null || wstatus_.length == 0)
        {
            wfs_clause = "";
        }
        else
        {
            wfs_clause = "AND qc.asl_name IN " + selectIN(wstatus_);
        }

        String select[] = new String[4];
        select[0] = "select 'p', 1, 'qcv', qc.qc_idseq as id, qc.version, qc.qc_id, "
            + "qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified as ctime, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, '' "
            + "from sbrext.quest_contents_view_ext qc, sbr.contexts_view c "
            + "where qc.qtl_name = 'VALID_VALUE' and c.conte_idseq = qc.conte_idseq and ";
        select[1] = "qc.created_by in (?) and ";
        select[2] = "qc.modified_by in (?) and ";
        select[3] = "((qc.date_modified is not null and qc.date_modified "
            + _DATECHARS[dates_][0] + " ? and qc.date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (qc.date_created is not null and qc.date_created "
            + _DATECHARS[dates_][2] + " ? and qc.date_created " + _DATECHARS[dates_][3] + " ?)) "
            + wfs_clause
            + "order by id asc, cid asc";

        return selectAC(select, start_, end_, 2, creators_, modifiers_);
    }

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
        String creators_[], String modifiers_[], String wstatus_[])
    {
        String wfs_clause;
        if (wstatus_ == null || wstatus_.length == 0)
        {
            wfs_clause = "";
        }
        else
        {
            wfs_clause = "AND qc.asl_name IN " + selectIN(wstatus_);
        }

        String select[] = new String[4];
        select[0] = "select 'p', 1, 'qcq', qc.qc_idseq as id, qc.version, qc.qc_id, "
            + "qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified as ctime, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, '' "
            + "from sbrext.quest_contents_view_ext qc, sbr.contexts_view c "
            + "where qc.qtl_name in ('QUESTION', 'QUESTION_INSTR') and c.conte_idseq = qc.conte_idseq and ";
        select[1] = "qc.created_by in (?) and ";
        select[2] = "qc.modified_by in (?) and ";
        select[3] = "((qc.date_modified is not null and qc.date_modified "
            + _DATECHARS[dates_][0] + " ? and qc.date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (qc.date_created is not null and qc.date_created "
            + _DATECHARS[dates_][2] + " ? and qc.date_created " + _DATECHARS[dates_][3] + " ?)) "
            + wfs_clause
            + "order by id asc, cid asc";

        return selectAC(select, start_, end_, 2, creators_, modifiers_);
    }

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
        String creators_[], String modifiers_[], String wstatus_[])
    {
        String wfs_clause;
        if (wstatus_ == null || wstatus_.length == 0)
        {
            wfs_clause = "";
        }
        else
        {
            wfs_clause = "AND qc.asl_name IN " + selectIN(wstatus_);
        }

        String select[] = new String[4];
        select[0] = "select 'p', 1, 'qcm', qc.qc_idseq as id, qc.version, qc.qc_id, "
            + "qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified as ctime, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, '' "
            + "from sbrext.quest_contents_view_ext qc, sbr.contexts_view c "
            + "where qc.qtl_name = 'MODULE' and c.conte_idseq = qc.conte_idseq and ";
        select[1] = "qc.created_by in (?) and ";
        select[2] = "qc.modified_by in (?) and ";
        select[3] = "((qc.date_modified is not null and qc.date_modified "
            + _DATECHARS[dates_][0] + " ? and qc.date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (qc.date_created is not null and qc.date_created "
            + _DATECHARS[dates_][2] + " ? and qc.date_created " + _DATECHARS[dates_][3] + " ?)) "
            + wfs_clause
            + "order by id asc, cid asc";

        return selectAC(select, start_, end_, 2, creators_, modifiers_);
    }

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
        String creators_[], String modifiers_[], String wstatus_[])
    {
        String wfs_clause;
        if (wstatus_ == null || wstatus_.length == 0)
        {
            wfs_clause = "";
        }
        else
        {
            wfs_clause = "AND proto.asl_name IN " + selectIN(wstatus_);
        }

        String select[] = new String[4];
        select[0] = "select 'p', 1, 'proto', proto.proto_idseq as id, proto.version, proto.proto_id, "
            + "proto.long_name, proto.conte_idseq as cid, "
            + "proto.date_modified as ctime, proto.date_created, proto.modified_by, proto.created_by, proto.change_note, c.name, '' "
            + "from sbrext.protocols_view_ext proto, sbr.contexts_view c "
            + "where c.conte_idseq = proto.conte_idseq and ";
        select[1] = "proto.created_by in (?) and ";
        select[2] = "proto.modified_by in (?) and ";
        select[3] = "((proto.date_modified is not null and proto.date_modified "
            + _DATECHARS[dates_][0] + " ? and proto.date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (proto.date_created is not null and proto.date_created "
            + _DATECHARS[dates_][2] + " ? and proto.date_created " + _DATECHARS[dates_][3] + " ?)) "
            + wfs_clause
            + "order by id asc, cid asc";

        return selectAC(select, start_, end_, 2, creators_, modifiers_);
    }

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
        String creators_[], String modifiers_[], String wstatus_[])
    {
        String wfs_clause;
        if (wstatus_ == null || wstatus_.length == 0)
        {
            wfs_clause = "";
        }
        else
        {
            wfs_clause = "AND qc.asl_name IN " + selectIN(wstatus_);
        }

        String select[] = new String[4];
        select[0] = "select 'p', 1, 'qc', qc.qc_idseq as id, qc.version, qc.qc_id, "
            + "qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified as ctime, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, '' "
            + "from sbrext.quest_contents_view_ext qc, sbr.contexts_view c "
            + "where qc.qtl_name in ('FORM', 'TEMPLATE') and c.conte_idseq = qc.conte_idseq and ";
        select[1] = "qc.created_by in (?) and ";
        select[2] = "qc.modified_by in (?) and ";
        select[3] = "((qc.date_modified is not null and qc.date_modified "
            + _DATECHARS[dates_][0] + " ? and qc.date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (qc.date_created is not null and qc.date_created "
            + _DATECHARS[dates_][2] + " ? and qc.date_created " + _DATECHARS[dates_][3] + " ?)) "
            + wfs_clause
            + "order by id asc, cid asc";

        return selectAC(select, start_, end_, 2, creators_, modifiers_);
    }

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
        String creators_[], String modifiers_[])
    {
        String select[] = new String[4];
        select[0] = "select 'p', 1, 'csi', csi_idseq as id, '', -1, csi_name, '', "
            + "date_modified, date_created, modified_by, created_by, comments, '', '' "
            + "from sbr.class_scheme_items_view " + "where ";
        select[1] = "created_by in (?) and ";
        select[2] = "modified_by in (?) and ";
        select[3] = "((date_modified is not null and date_modified "
            + _DATECHARS[dates_][0] + " ? and date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (date_created is not null and date_created "
            + _DATECHARS[dates_][2] + " ? and date_created " + _DATECHARS[dates_][3] + " ?)) "
            + "order by id asc";

        return selectAC(select, start_, end_, 2, creators_, modifiers_);
    }

    /**
     * Expand the list to an IN clause.
     * 
     * @param regs_ The list of DE registration statuses.
     * @return The expanded IN clause.
     */
    private String selectIN(String regs_[])
    {
        String temp = "";
        for (int ndx = 0; ndx < regs_.length; ++ndx)
        {
            temp = temp + ", '" + regs_[ndx] + "'";
        }
        return "(" + temp.substring(2) + ") ";
    }

    /**
     * Construct the standard change history table where clause.
     * 
     * @param table_ The primary changed_table value, one of _ACTYPE_...
     * @return The where clause.
     */
    private String whereACH(int table_)
    {
        String temp = "AND ach.ac_idseq in "
        + "(select distinct ch2.changed_table_idseq from sbrext.ac_change_history_ext ch2 "
        + "where ch2.changed_table = '" + _DBMAP3CHGCOL[table_] + "' and ch2.changed_table_idseq = ach.ac_idseq) "
        + "and ach.changed_column not in ('DATE_CREATED', 'DATE_MODIFIED', 'LAE_NAME') "
        + "and (ach.changed_table = '" + _DBMAP3CHGCOL[table_] + "' or "
        + "(ach.changed_table = 'AC_CSI' and ach.changed_column = 'CS_CSI_IDSEQ') or "
        + "(ach.changed_table = 'DESIGNATIONS' and ach.changed_column in ('CONTE_IDSEQ', 'DETL_NAME', 'LAE_NAME')) or "
        + "(ach.changed_table = 'REFERENCE_DOCUMENTS' and ach.changed_column in ('DCTL_NAME', 'DISPLAY_ORDER', 'DOC_TEXT', 'RDTL_NAME', 'URL')) or "
        + "(ach.changed_table = 'VD_PVS' and ach.changed_column = 'PV_IDSEQ')) ";
        return temp;
    }
    
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
        String creators_[], String modifiers_[], String wstatus_[], String rstatus_[])
    {
        return selectAC(
            selectBuild(_ACTYPE_DE,
                dates_, start_, end_, creators_, modifiers_, wstatus_, rstatus_));
    }
    
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
        String creators_[], String modifiers_[])
    {
        String select[] = new String[4];
        select[0] = "select 'p', 1, 'conte', conte_idseq as id, version, -1, name, '', "
            + "date_modified, date_created, modified_by, created_by, '', '', '' "
            + "from sbr.contexts_view " + "where ";
        select[1] = "created_by in (?) and ";
        select[2] = "modified_by in (?) and ";
        select[3] = "((date_modified is not null and date_modified "
            + _DATECHARS[dates_][0] + " ? and date_modified " + _DATECHARS[dates_][1] + " ?) "
            + "or (date_created is not null and date_created "
            + _DATECHARS[dates_][2] + " ? and date_created " + _DATECHARS[dates_][3] + " ?)) "
            + "order by id asc";

        return selectAC(select, start_, end_, 2, creators_, modifiers_);
    }

    /**
     * Build the SQL select to retrieve changes for an Administered Component.
     * 
     * @param type_ The AC type, one of _ACTYPE_...
     * @param dates_ The flag for how dates are compared, _DATECM, _DATECONLY, _DATEMONLY
     * @param start_ The start date for the query.
     * @param end_ The end date for the query.
     * @param creators_ The specific created_by if any.
     * @param modifiers_ The specific modified_by if any.
     * @param wstatus_ The specific Workflow Status if any.
     * @param rstatus_ The specific Registration Status if any.
     * @return The SQL select string.
     */
    private String selectBuild(int type_,
        int dates_, Timestamp start_, Timestamp end_,
        String creators_[], String modifiers_[], String wstatus_[], String rstatus_[])
    {
        // Due to the very conditional nature of this logic, the SQL SELECT is built
        // without the use of substitution arguments ('?').
        String prefix = _DBMAP3KEYS[type_];
        
        // The 'de' type is the only one that doesn't use the same prefix for the public id
        // database column - ugh.
        String prefix2 = (type_ == _ACTYPE_DE) ? "cde" : prefix;
        
        // Build the start and end dates.
        String start = "to_date('" + start_.toString().substring(0, 10) + "', 'yyyy/mm/dd')";
        String end = "to_date('" + end_.toString().substring(0, 10) + "', 'yyyy/mm/dd')";
        
        // Build the basic select and from.
        String select =
            "select 'p', 1, '" + prefix
            + "', zz." + prefix
            + "_idseq as id, zz.version, zz." + prefix2
            + "_id, zz.long_name, zz.conte_idseq, "
            + "zz.date_modified, zz.date_created, zz.modified_by, zz.created_by, zz.change_note, "
            + "c.name, '', ach.changed_column, ach.old_value, ach.new_value, ach.change_datetimestamp, ach.changed_table, ach.changed_by "
            + "from sbrext.ac_change_history_ext ach, " + _DBMAP3TABLES[type_] + " zz, ";

        // If registration status is not used we only need to add the context
        // table.
        String reg_clause;
        if (rstatus_ == null || rstatus_.length == 0)
        {
            select = select + "sbr.contexts_view c ";
            reg_clause = "";
        }
        // If registration status is used we need to add the context and registration
        // status tables.
        else
        {
            select = select + "sbr.contexts_view c, sbr.ac_registrations_view ar ";
            reg_clause = "AND ar.ac_idseq = zz." + prefix
                + "_idseq AND NVL(ar.registration_status, '(none)') IN " + selectIN(rstatus_);
        }

        // If workflow status is not used we need to be sure and use an empty
        // string.
        String wfs_clause;
        if (wstatus_ == null || wstatus_.length == 0)
        {
            wfs_clause = "";
        }
        // If workflow status is used we need to qualify by the content of the list.
        else
        {
            wfs_clause = "AND zz.asl_name IN " + selectIN(wstatus_);
        }

        // Building the 'where' clause should be done to keep all qualifications together, e.g.
        // first qualify all for ACH then join to the primary table (ZZ) complete the qualifications
        // then join to the context table.
        
        // Always checking the date range first.
        select = select + "where ach.change_datetimestamp >= " + start + " and ach.change_datetimestamp < " + end + " ";
        
        // If modifiers are provided be sure to get everything.
        if (modifiers_ != null && modifiers_.length > 0 && modifiers_[0].charAt(0) != '(')
            select = select + "AND ach.changed_by in " + selectIN(modifiers_);
        
        // Now qualify by the record type of interest and join to the primary table.
        select = select + whereACH(type_) 
            + "AND zz." + prefix + "_idseq = ach.ac_idseq ";
        
        // If creators are provided they must be qualified by the primary table not the change table.
        if (creators_ != null && creators_.length > 0 && creators_[0].charAt(0) != '(')
            select = select + "AND zz.created_by in " + selectIN(creators_);
        
        // When looking for both create and modified dates no extra clause is needed. For create
        // date only qualify against the primary table.
        if (dates_ == _DATECONLY)
            select = select + "AND zz.date_created >= " + start + " and zz.date_created < " + end + " ";
        
        // For modify date only qualify the primary table. The actual date is not important because we
        // qualified the records from the history table by date already.
        else if (dates_ == _DATEMONLY)
            select = select + "AND zz.date_modified is not NULL ";

        // Put everything together including the join to the context table and the sort order clause.
        return select + wfs_clause + reg_clause + "AND c.conte_idseq = zz.conte_idseq " + _orderbyACH;
    }
    
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
        String creators_[], String modifiers_[], String wstatus_[])
    {
        return selectAC(
            selectBuild(_ACTYPE_DEC,
                dates_, start_, end_, creators_, modifiers_, wstatus_, null));
    }

    /**
     * Select the dependant data. In Oracle an "in" clause may have a maximum of
     * 1000 items. If the array length (ids_) is greater than 1000 it is broken
     * up into pieces. The result is that should an order by clause also appear,
     * the end result may not be correct as the SQL had to be performed in
     * multiple pieces.
     * 
     * @param select_
     *        The SQL select.
     * @param ids_
     *        The array holding the id's for the query.
     * @return The ACData array of the results.
     */
    private ACData[] selectAC(String select_, ACData ids_[])
    {
        if (ids_ == null || ids_.length == 0)
            return new ACData[0];

        // Oracle limit on "in" clauses.
        int limit = 1000;
        if (ids_.length < limit)
            return selectAC2(select_, ids_);

        // When more than 1000 we have to break up the list
        // and merge the results together.
        ACData results[] = new ACData[0];
        int group = limit;
        int indx = 0;
        while (indx < ids_.length)
        {
            ACData tset[] = new ACData[group];
            System.arraycopy(ids_, indx, tset, 0, group);
            indx += group;
            ACData rset[] = selectAC2(select_, tset);
            tset = results;
            results = new ACData[tset.length + rset.length];
            
            // Now that we have a place to store the composite
            // list perform a simple merge as both are already
            // sorted.
            int tndx = 0;
            int rndx = 0;
            int ndx = 0;
            if (tset.length > 0 && rset.length > 0)
            {
                while (ndx < results.length)
                {
                    if (tset[tndx].compareUsingIDS(rset[rndx]) <= 0)
                    {
                        results[ndx++] = tset[tndx++];
                        if (tndx == tset.length)
                            break;
                    }
                    else
                    {
                        results[ndx++] = rset[rndx++];
                        if (rndx == rset.length)
                            break;
                    }
                }
            }
            
            // We've exhausted the 'temp' list so copy the rest of the
            // 'rc' list.
            if (tndx == tset.length)
                System.arraycopy(rset, rndx, results, ndx, rset.length - rndx);
            
            // We've exhausted the 'rc' list so copy the rest of the
            // 'temp' list.
            else
                System.arraycopy(tset, tndx, results, ndx, tset.length - tndx);

            // Do next group.
            tndx = ids_.length - indx;
            if (group > tndx)
                group = tndx;
            
            // Force conservation of memory.
            tset = null;
            rset = null;
        }
        return results;
    }

    /**
     * Select the dependant data. This method does not test the length of the
     * array (ids_) and therefore should only be called when 1000 ids or less
     * are needed.
     * 
     * @param select_
     *        The SQL containing the "in" clause.
     * @param ids_
     *        The id values to be bound.
     * @return The result of the query.
     */
    private ACData[] selectAC2(String select_, ACData ids_[])
    {
        String markers = expandMarkers(ids_.length);

        // Split the string based on "?" markers.
        String parts[] = select_.split("\\?");
        String select = null;
        if (parts.length == 2)
        {
            select = parts[0] + markers + parts[1];
        }
        else if (parts.length == 3)
        {
            select = parts[0] + markers + parts[1] + markers + parts[2];
        }
        else
        {
            // Only needed during development.
            System.err.println("DBAlert: DEVELOPMENT ERROR 1: ==>\n" + select_
                + "\n<== unexpected SQL form.");
            return null;
        }

        try
        {
            // Build, bind and execute the statement.
            PreparedStatement pstmt = _conn.prepareStatement(select);

            int cnt = 1;
            for (int ndx = 0; ndx < ids_.length; ++ndx)
            {
                pstmt.setString(cnt++, ids_[ndx].getIDseq());
            }
            if (parts.length == 3)
            {
                for (int ndx = 0; ndx < ids_.length; ++ndx)
                {
                    pstmt.setString(cnt++, ids_[ndx].getIDseq());
                }
            }

            ResultSet rs = pstmt.executeQuery();
            ACData list[] = copyResults(rs);
            rs.close();
            pstmt.close();

            return list;
        }
        catch (SQLException ex)
        {
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 32: " + _errorCode + ": " + select
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return null;
        }
    }

    /**
     * Find the Value Domains that are affected by changes to the Permissible
     * Values.
     * 
     * @param pv_
     *        The list of permissible values identified as changed or created.
     * @return The array of value domains.
     */
    public ACData[] selectVDfromPV(ACData pv_[])
    {
        String select = "(select 's', 1, 'vd', vd.vd_idseq as id, vd.version, vd.vd_id, vd.long_name, vd.conte_idseq as cid, "
            + "vd.date_modified, vd.date_created, vd.modified_by, vd.created_by, vd.change_note, c.name, pv.pv_idseq "
            + "from sbr.value_domains_view vd, sbr.vd_pvs_view vp, sbr.permissible_values_view pv, sbr.contexts_view c "
            + "where pv.pv_idseq in (?) and vp.pv_idseq = pv.pv_idseq and vd.vd_idseq = vp.vd_idseq and "
            + "c.conte_idseq = vd.conte_idseq "
            + "union "
            + "select 's', 1, 'vd', ac.ac_idseq as id, ac.version, xx.vd_id, ac.long_name, dv.conte_idseq as cid, "
            + "ac.date_modified, ac.date_created, ac.modified_by, ac.created_by, ac.change_note, c.name, pv.pv_idseq "
            + "from sbr.permissible_values_view pv, sbr.vd_pvs_view vp, sbr.value_domains_view xx, "
            + "sbr.admin_components_view ac, sbr.designations_view dv, sbr.contexts_view c "
            + "where pv.pv_idseq in (?) and vp.pv_idseq = pv.pv_idseq and xx.vd_idseq = vp.vd_idseq "
            + "and ac.ac_idseq = xx.vd_idseq and ac.actl_name = 'VALUEDOMAIN' and "
            + "dv.ac_idseq = ac.ac_idseq and c.conte_idseq = dv.conte_idseq) "
            + "order by id asc, cid asc";

        return selectAC(select, pv_);
    }

    /**
     * Find the Conceptual Domains affected by changes to the Value Domains
     * provided.
     * 
     * @param vd_
     *        The list of value domains.
     * @return The array of conceptual domains.
     */
    public ACData[] selectCDfromVD(ACData vd_[])
    {
        String select = "(select 's', 1, 'cd', cd.cd_idseq as id, cd.version, cd.cd_id, cd.long_name, cd.conte_idseq as cid, "
            + "cd.date_modified, cd.date_created, cd.modified_by, cd.created_by, cd.change_note, c.name, vd.vd_idseq "
            + "from sbr.conceptual_domains_view cd, sbr.contexts_view c, sbr.value_domains_view vd "
            + "where vd.vd_idseq in (?) and cd.cd_idseq = vd.cd_idseq and c.conte_idseq = cd.conte_idseq "
            + "union "
            + "select 's', 1, 'cd', ac.ac_idseq as id, ac.version, xx.cd_id, ac.long_name, dv.conte_idseq as cid, "
            + "ac.date_modified, ac.date_created, ac.modified_by, ac.created_by, ac.change_note, c.name, vd.vd_idseq "
            + "from sbr.admin_components_view ac, sbr.conceptual_domains_view xx, "
            + "sbr.designations_view dv, sbr.contexts_view c, sbr.value_domains_view vd "
            + "where vd.vd_idseq in (?) and xx.cd_idseq = vd.cd_idseq and ac.ac_idseq = xx.cd_idseq and "
            + "ac.actl_name = 'CONCEPTUALDOMAIN' and dv.ac_idseq = ac.ac_idseq and c.conte_idseq = dv.conte_idseq) "
            + "order by id asc, cid asc";

        return selectAC(select, vd_);
    }

    /**
     * Find the Conceptual Domains affected by changes to the Data Element Concepts
     * provided.
     * 
     * @param dec_
     *        The list of data element concepts.
     * @return The array of conceptual domains.
     */
    public ACData[] selectCDfromDEC(ACData dec_[])
    {
        String select = "(select 's', 1, 'cd', cd.cd_idseq as id, cd.version, cd.cd_id, cd.long_name, cd.conte_idseq as cid, "
            + "cd.date_modified, cd.date_created, cd.modified_by, cd.created_by, cd.change_note, c.name, dec.dec_idseq "
            + "from sbr.conceptual_domains_view cd, sbr.contexts_view c, sbr.data_element_concepts_view dec "
            + "where dec.dec_idseq in (?) and cd.cd_idseq = dec.dec_idseq and c.conte_idseq = cd.conte_idseq "
            + "union "
            + "select 's', 1, 'cd', ac.ac_idseq as id, ac.version, xx.cd_id, ac.long_name, dv.conte_idseq as cid, "
            + "ac.date_modified, ac.date_created, ac.modified_by, ac.created_by, ac.change_note, c.name, dec.dec_idseq "
            + "from sbr.admin_components_view ac, sbr.conceptual_domains_view xx, "
            + "sbr.designations_view dv, sbr.contexts_view c, sbr.data_element_concepts_view dec "
            + "where dec.dec_idseq in (?) and xx.cd_idseq = dec.cd_idseq and ac.ac_idseq = xx.cd_idseq and "
            + "ac.actl_name = 'CONCEPTUALDOMAIN' and dv.ac_idseq = ac.ac_idseq and c.conte_idseq = dv.conte_idseq) "
            + "order by id asc, cid asc";

        return selectAC(select, dec_);
    }

    /**
     * Select the Data Elements affected by the Value Domains provided.
     * 
     * @param vd_
     *        The value domain list.
     * @return The array of related data elements.
     */
    public ACData[] selectDEfromVD(ACData vd_[])
    {
        String select = "(select 's', 1, 'de', de.de_idseq as id, de.version, de.cde_id, de.long_name, de.conte_idseq as cid, "
            + "de.date_modified, de.date_created, de.modified_by, de.created_by, de.change_note, c.name, vd.vd_idseq "
            + "from sbr.data_elements_view de, sbr.contexts_view c, sbr.value_domains_view vd "
            + "where vd.vd_idseq in (?) and de.vd_idseq = vd.vd_idseq and c.conte_idseq = de.conte_idseq "
            + "union "
            + "select 's', 1, 'de', ac.ac_idseq as id, ac.version, xx.cde_id, ac.long_name, dv.conte_idseq as cid, "
            + "ac.date_modified, ac.date_created, ac.modified_by, ac.created_by, ac.change_note, c.name, vd.vd_idseq "
            + "from sbr.admin_components_view ac, sbr.data_elements_view xx, "
            + "sbr.designations_view dv, sbr.contexts_view c, sbr.value_domains_view vd "
            + "where vd.vd_idseq in (?) and xx.vd_idseq = vd.vd_idseq and xx.de_idseq = ac.ac_idseq and ac.actl_name = 'DATAELEMENT' and "
            + "dv.ac_idseq = ac.ac_idseq and c.conte_idseq = dv.conte_idseq) "
            + "order by id asc, cid asc";

        return selectAC(select, vd_);
    }

    /**
     * Select the Data Element Concepts affected by the Properties provided.
     * 
     * @param prop_
     *        The property list.
     * @return The array of related data element concepts.
     */
    public ACData[] selectDECfromPROP(ACData prop_[])
    {
        String select = "(select 's', 1, 'dec', dec.dec_idseq as id, dec.version, dec.dec_id, dec.long_name, dec.conte_idseq as cid, "
            + "dec.date_modified, dec.date_created, dec.modified_by, dec.created_by, dec.change_note, c.name, prop.prop_idseq "
            + "from sbr.data_element_concepts_view dec, sbr.contexts_view c, sbrext.properties_view_ext prop "
            + "where prop.prop_idseq in (?) and dec.prop_idseq = prop.prop_idseq and c.conte_idseq = dec.conte_idseq "
            + "union "
            + "select 's', 1, 'dec', ac.ac_idseq as id, ac.version, xx.dec_id, ac.long_name, dv.conte_idseq as cid, "
            + "ac.date_modified, ac.date_created, ac.modified_by, ac.created_by, ac.change_note, c.name, prop.prop_idseq "
            + "from sbr.admin_components_view ac, sbr.data_element_concepts_view xx, "
            + "sbr.designations_view dv, sbr.contexts_view c, sbrext.properties_view_ext prop "
            + "where prop.prop_idseq in (?) and xx.prop_idseq = prop.prop_idseq and xx.dec_idseq = ac.ac_idseq and ac.actl_name = 'DE_CONCEPT' and "
            + "dv.ac_idseq = ac.ac_idseq and c.conte_idseq = dv.conte_idseq) "
            + "order by id asc, cid asc";

        return selectAC(select, prop_);
    }

    /**
     * Select the Data Element Concepts affected by the Object Classes provided.
     * 
     * @param oc_
     *        The object class list.
     * @return The array of related data element concepts.
     */
    public ACData[] selectDECfromOC(ACData oc_[])
    {
        String select = "(select 's', 1, 'dec', dec.dec_idseq as id, dec.version, dec.dec_id, dec.long_name, dec.conte_idseq as cid, "
            + "dec.date_modified, dec.date_created, dec.modified_by, dec.created_by, dec.change_note, c.name, oc.oc_idseq "
            + "from sbr.data_element_concepts_view dec, sbr.contexts_view c, sbrext.object_classes_view_ext oc "
            + "where oc.oc_idseq in (?) and dec.oc_idseq = oc.oc_idseq and c.conte_idseq = dec.conte_idseq "
            + "union "
            + "select 's', 1, 'dec', ac.ac_idseq as id, ac.version, xx.dec_id, ac.long_name, dv.conte_idseq as cid, "
            + "ac.date_modified, ac.date_created, ac.modified_by, ac.created_by, ac.change_note, c.name, oc.oc_idseq "
            + "from sbr.admin_components_view ac, sbr.data_element_concepts_view xx, "
            + "sbr.designations_view dv, sbr.contexts_view c, sbrext.object_classes_view_ext oc "
            + "where oc.oc_idseq in (?) and xx.oc_idseq = oc.oc_idseq and xx.dec_idseq = ac.ac_idseq and ac.actl_name = 'DE_CONCEPT' and "
            + "dv.ac_idseq = ac.ac_idseq and c.conte_idseq = dv.conte_idseq) "
            + "order by id asc, cid asc";

        return selectAC(select, oc_);
    }

    /**
     * Select the Data Elements affected by the Data Element Concepts provided.
     * 
     * @param dec_
     *        The data element concepts list.
     * @return The array of related data elements.
     */
    public ACData[] selectDEfromDEC(ACData dec_[])
    {
        String select = "(select 's', 1, 'de', de.de_idseq as id, de.version, de.cde_id, de.long_name, de.conte_idseq as cid, "
            + "de.date_modified, de.date_created, de.modified_by, de.created_by, de.change_note, c.name, dec.dec_idseq "
            + "from sbr.data_elements_view de, sbr.contexts_view c, sbr.data_element_concepts_view dec "
            + "where dec.dec_idseq in (?) and de.dec_idseq = dec.dec_idseq and c.conte_idseq = de.conte_idseq "
            + "union "
            + "select 's', 1, 'de', ac.ac_idseq as id, ac.version, xx.cde_id, ac.long_name, dv.conte_idseq as cid, "
            + "ac.date_modified, ac.date_created, ac.modified_by, ac.created_by, ac.change_note, c.name, dec.dec_idseq "
            + "from sbr.admin_components_view ac, sbr.data_elements_view xx, "
            + "sbr.designations_view dv, sbr.contexts_view c, sbr.data_element_concepts_view dec "
            + "where dec.dec_idseq in (?) and xx.dec_idseq = dec.dec_idseq and xx.de_idseq = ac.ac_idseq and ac.actl_name = 'DATAELEMENT' and "
            + "dv.ac_idseq = ac.ac_idseq and c.conte_idseq = dv.conte_idseq) "
            + "order by id asc, cid asc";

        return selectAC(select, dec_);
    }

    /**
     * Select the Classification Scheme Item affected by the Data Elements
     * provided.
     * 
     * @param de_
     *        The data element list.
     * @return The array of related classification scheme items.
     */
    public ACData[] selectCSIfromDE(ACData de_[])
    {
        String select = "select 's', 1, 'csi', civ.csi_idseq as id, '', -1, civ.csi_name, '', "
            + "civ.date_modified, civ.date_created, civ.modified_by, civ.created_by, civ.comments, '', de.de_idseq "
            + "from sbr.class_scheme_items_view civ, sbr.data_elements_view de, sbr.admin_components_view ac, "
            + "sbr.ac_csi_view ai, sbr.cs_csi_view ci "
            + "where de.de_idseq in (?) and ac.ac_idseq = de.de_idseq and ai.ac_idseq = ac.ac_idseq and "
            + "ci.cs_csi_idseq = ai.cs_csi_idseq and civ.csi_idseq = ci.csi_idseq "
            + "order by id asc";

        return selectAC(select, de_);
    }

    /**
     * Select the Classification Scheme Item affected by the Data Element Concepts
     * provided.
     * 
     * @param dec_
     *        The data element concept list.
     * @return The array of related classification scheme items.
     */
    public ACData[] selectCSIfromDEC(ACData dec_[])
    {
        String select = "select 's', 1, 'csi', civ.csi_idseq as id, '', -1, civ.csi_name, '', "
            + "civ.date_modified, civ.date_created, civ.modified_by, civ.created_by, civ.comments, '', dec.dec_idseq "
            + "from sbr.class_scheme_items_view civ, sbr.data_element_concepts_view dec, sbr.admin_components_view ac, "
            + "sbr.ac_csi_view ai, sbr.cs_csi_view ci "
            + "where dec.dec_idseq in (?) and ac.ac_idseq = dec.dec_idseq and ai.ac_idseq = ac.ac_idseq and "
            + "ci.cs_csi_idseq = ai.cs_csi_idseq and civ.csi_idseq = ci.csi_idseq "
            + "order by id asc";

        return selectAC(select, dec_);
    }

    /**
     * Select the Classification Scheme Item affected by the Value Domains
     * provided.
     * 
     * @param vd_
     *        The value domain list.
     * @return The array of related classification scheme items.
     */
    public ACData[] selectCSIfromVD(ACData vd_[])
    {
        String select = "select 's', 1, 'csi', civ.csi_idseq as id, '', -1, civ.csi_name, '', "
            + "civ.date_modified, civ.date_created, civ.modified_by, civ.created_by, civ.comments, '', vd.vd_idseq "
            + "from sbr.class_scheme_items_view civ, sbr.value_domains_view vd, sbr.admin_components_view ac, "
            + "sbr.ac_csi_view ai, sbr.cs_csi_view ci "
            + "where vd.vd_idseq in (?) and ac.ac_idseq = vd.vd_idseq and ai.ac_idseq = ac.ac_idseq and "
            + "ci.cs_csi_idseq = ai.cs_csi_idseq and civ.csi_idseq = ci.csi_idseq "
            + "order by id asc";

        return selectAC(select, vd_);
    }

    /**
     * Select the Forms/Templates affected by the Data Elements provided.
     * 
     * @param de_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCQfromDE(ACData de_[])
    {
        String select = "select 's', 1, 'qcq', qc.qc_idseq as id, qc.version, qc.qc_id, qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, de.de_idseq "
            + "from sbrext.quest_contents_view_ext qc, sbr.data_elements_view de, sbr.contexts_view c "
            + "where de.de_idseq in (?) and qc.de_idseq = de.de_idseq and qc.qtl_name in ('QUESTION', 'QUESTION_INSTR') and c.conte_idseq = qc.conte_idseq "
            + "order by id asc, cid asc";

        return selectAC(select, de_);
    }

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     * 
     * @param vd_
     *        The value domain list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCQfromVD(ACData vd_[])
    {
        String select = "select 's', 1, 'qcq', qc.qc_idseq as id, qc.version, qc.qc_id, qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, vd.vd_idseq "
            + "from sbrext.quest_contents_view_ext qc, sbr.value_domains_view vd, sbr.contexts_view c "
            + "where vd.vd_idseq in (?) and qc.dn_vd_idseq = vd.vd_idseq and qc.qtl_name in ('QUESTION', 'QUESTION_INSTR') and c.conte_idseq = qc.conte_idseq "
            + "order by id asc, cid asc";

        return selectAC(select, vd_);
    }

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     * 
     * @param vd_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCVfromVD(ACData vd_[])
    {
        String select = "select 's', 1, 'qcv', qc.qc_idseq as id, qc.version, qc.qc_id, qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, vd.vd_idseq "
            + "from sbrext.quest_contents_view_ext qc, sbr.value_domains_view vd, sbr.vd_pvs_view vp, sbr.contexts_view c "
            + "where vd.vd_idseq in (?) and vp.vd_idseq = vd.vd_idseq and qc.vp_idseq = vp.vp_idseq and "
            + "qc.qtl_name = 'VALID_VALUE' and c.conte_idseq = qc.conte_idseq "
            + "order by id asc, cid asc";

        return selectAC(select, vd_);
    }

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     * 
     * @param qcv_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCQfromQCV(ACData qcv_[])
    {
        String select = "select 's', 1, 'qcq', qc.qc_idseq as id, qc.version, qc.qc_id, qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, qc2.qc_idseq "
            + "from sbrext.quest_contents_view_ext qc, sbrext.quest_contents_view_ext qc2, sbr.contexts_view c "
            + "where qc2.qc_idseq in (?) and qc2.qtl_name = 'VALID_VALUE' and qc.qc_idseq = qc2.p_qst_idseq and c.conte_idseq = qc.conte_idseq "
            + "order by id asc, cid asc";

        return selectAC(select, qcv_);
    }

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     * 
     * @param qcq_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCMfromQCQ(ACData qcq_[])
    {
        String select = "select 's', 1, 'qcm', qc.qc_idseq as id, qc.version, qc.qc_id, qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, qc2.qc_idseq "
            + "from sbrext.quest_contents_view_ext qc, sbrext.quest_contents_view_ext qc2, sbr.contexts_view c "
            + "where qc2.qc_idseq in (?) and qc2.qtl_name in ('QUESTION', 'QUESTION_INSTR') and qc.qc_idseq = qc2.p_mod_idseq and c.conte_idseq = qc.conte_idseq "
            + "order by id asc, cid asc";

        return selectAC(select, qcq_);
    }

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     * 
     * @param qcm_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCfromQCM(ACData qcm_[])
    {
        String select = "select 's', 1, 'qc', qc.qc_idseq as id, qc.version, qc.qc_id, qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, qc2.qc_idseq "
            + "from sbrext.quest_contents_view_ext qc, sbrext.quest_contents_view_ext qc2, sbr.contexts_view c "
            + "where qc2.qc_idseq in (?) and qc2.qtl_name = 'MODULE' and qc.qc_idseq = qc2.dn_crf_idseq and c.conte_idseq = qc.conte_idseq "
            + "order by id asc, cid asc";

        return selectAC(select, qcm_);
    }

    /**
     * Select the Forms/Templates affected by the Value Domains provided.
     * 
     * @param qcq_
     *        The data element list.
     * @return The array of related forms/templates.
     */
    public ACData[] selectQCfromQCQ(ACData qcq_[])
    {
        String select = "select 's', 1, 'qc', qc.qc_idseq as id, qc.version, qc.qc_id, qc.long_name, qc.conte_idseq as cid, "
            + "qc.date_modified, qc.date_created, qc.modified_by, qc.created_by, qc.change_note, c.name, qc2.qc_idseq "
            + "from sbrext.quest_contents_view_ext qc, sbrext.quest_contents_view_ext qc2, sbr.contexts_view c "
            + "where qc2.qc_idseq in (?) and qc2.qtl_name in ('QUESTION', 'QUESTION_INSTR') and qc2.p_mod_idseq is null and "
            + "qc.qc_idseq = qc2.dn_crf_idseq and c.conte_idseq = qc.conte_idseq "
            + "order by id asc, cid asc";

        return selectAC(select, qcq_);
    }

    /**
     * Select the Classification Schemes affected by the Classification Scheme
     * Items provided.
     * 
     * @param csi_
     *        The classification scheme items list.
     * @return The array of related classification schemes.
     */
    public ACData[] selectCSfromCSI(ACData csi_[])
    {
        String select = "(select 's', 1, 'cs', cs.cs_idseq as id, cs.version, cs.cs_id, cs.long_name, cs.conte_idseq as cid, "
            + "cs.date_modified, cs.date_created, cs.modified_by, cs.created_by, cs.change_note, c.name, civ.csi_idseq "
            + "from sbr.classification_schemes_view cs, sbr.contexts_view c, sbr.cs_csi_view ci, "
            + "sbr.class_scheme_items_view civ "
            + "where civ.csi_idseq in (?) and ci.csi_idseq = civ.csi_idseq and cs.cs_idseq = ci.cs_idseq and "
            + "c.conte_idseq = cs.conte_idseq "
            + "union "
            + "select 's', 1, 'cs', ac.ac_idseq as id, ac.version, xx.cs_id, ac.long_name, dv.conte_idseq as cid, "
            + "ac.date_modified, ac.date_created, ac.modified_by, ac.created_by, ac.change_note, c.name, civ.csi_idseq "
            + "from sbr.admin_components_view ac, sbr.classification_schemes_view xx, sbr.cs_csi_view ci, "
            + "sbr.designations_view dv, sbr.contexts_view c, sbr.class_scheme_items_view civ "
            + "where civ.csi_idseq in (?) and ci.csi_idseq = civ.csi_idseq and xx.cs_idseq = ci.cs_idseq and "
            + "ac.ac_idseq = xx.cs_idseq and ac.actl_name = 'CLASSIFICATION' and "
            + "dv.ac_idseq = ac.ac_idseq and c.conte_idseq = dv.conte_idseq) "
            + "order by id asc, cid asc";

        return selectAC(select, csi_);
    }

    /**
     * Select the Contexts affected by the Classification Schemes provided.
     * 
     * @param cs_
     *        The classification schemes list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromCS(ACData cs_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', cs.cs_idseq "
            + "from sbr.contexts_view c, sbr.classification_schemes_view cs "
            + "where cs.cs_idseq in (?) and c.conte_idseq = cs.conte_idseq "
            + "order by id asc";

        return selectAC(select, cs_);
    }

    /**
     * Select the Contexts affected by the Conceptual Domains provided.
     * 
     * @param cd_
     *        The conceptual domains list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromCD(ACData cd_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', cd.cd_idseq "
            + "from sbr.contexts_view c, sbr.conceptual_domains_view cd "
            + "where cd.cd_idseq in (?) and c.conte_idseq = cd.conte_idseq "
            + "order by id asc";

        return selectAC(select, cd_);
    }

    /**
     * Select the Contexts affected by the Value Domains provided.
     * 
     * @param vd_
     *        The value domains list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromVD(ACData vd_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', vd.vd_idseq "
            + "from sbr.contexts_view c, sbr.value_domains_view vd "
            + "where vd.vd_idseq in (?) and c.conte_idseq = vd.conte_idseq "
            + "order by id asc";

        return selectAC(select, vd_);
    }

    /**
     * Select the Contexts affected by the Data Elements provided.
     * 
     * @param de_
     *        The data elements list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromDE(ACData de_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', de.de_idseq "
            + "from sbr.contexts_view c, sbr.data_elements_view de "
            + "where de.de_idseq in (?) and c.conte_idseq = de.conte_idseq "
            + "order by id asc";

        return selectAC(select, de_);
    }

    /**
     * Select the Contexts affected by the Properties provided.
     * 
     * @param prop_
     *        The properties list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromPROP(ACData prop_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', prop.prop_idseq "
            + "from sbr.contexts_view c, sbrext.properties_view_ext prop "
            + "where prop.prop_idseq in (?) and c.conte_idseq = prop.conte_idseq "
            + "order by id asc";

        return selectAC(select, prop_);
    }

    /**
     * Select the Contexts affected by the Object Classes provided.
     * 
     * @param oc_
     *        The object class list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromOC(ACData oc_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', oc.oc_idseq "
            + "from sbr.contexts_view c, sbrext.object_classes_view_ext oc "
            + "where oc.oc_idseq in (?) and c.conte_idseq = oc.conte_idseq "
            + "order by id asc";

        return selectAC(select, oc_);
    }

    /**
     * Select the Contexts affected by the Protocols provided.
     * 
     * @param proto_
     *        The protocols list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromPROTO(ACData proto_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', proto.proto_idseq "
            + "from sbr.contexts_view c, sbrext.protocols_view_ext proto "
            + "where proto.proto_idseq in (?) and c.conte_idseq = proto.conte_idseq "
            + "order by id asc";

        return selectAC(select, proto_);
    }

    /**
     * Select the Protocols affected by the Forms/Templates provided.
     * 
     * @param qc_
     *        The forms/templates list.
     * @return The array of related contexts.
     */
    public ACData[] selectPROTOfromQC(ACData qc_[])
    {
        String select = "select 's', 1, 'proto', proto.proto_idseq as id, proto.version, proto.proto_id, proto.long_name, c.conte_idseq, "
            + "proto.date_modified, proto.date_created, proto.modified_by, proto.created_by, proto.change_note, c.name, qc.qc_idseq "
            + "from sbr.contexts_view c, sbrext.protocols_view_ext proto, sbrext.protocol_qc_ext pq, sbrext.quest_contents_view_ext qc "
            + "where qc.qc_idseq in (?) "
            + "and pq.qc_idseq = qc.qc_idseq "
            + "and proto.proto_idseq = pq.proto_idseq "
            + "and c.conte_idseq = proto.conte_idseq "
            + "order by id asc";

        return selectAC(select, qc_);
    }

    /**
     * Select the Contexts affected by the Forms/Templates provided.
     * 
     * @param qc_
     *        The forms/templates list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromQC(ACData qc_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', qc.qc_idseq "
            + "from sbr.contexts_view c, sbrext.quest_contents_view_ext qc "
            + "where qc.qc_idseq in (?) and c.conte_idseq = qc.conte_idseq "
            + "order by id asc";

        return selectAC(select, qc_);
    }

    /**
     * Select the Contexts affected by the Data Element Concepts provided.
     * 
     * @param dec_
     *        The data element concepts list.
     * @return The array of related contexts.
     */
    public ACData[] selectCONTEfromDEC(ACData dec_[])
    {
        String select = "select 's', 1, 'conte', c.conte_idseq as id, c.version, -1, c.name, '', "
            + "c.date_modified, c.date_created, c.modified_by, c.created_by, '', '', dec.dec_idseq "
            + "from sbr.contexts_view c, sbr.data_element_concepts_view dec "
            + "where dec.dec_idseq in (?) and c.conte_idseq = dec.conte_idseq "
            + "order by id asc";

        return selectAC(select, dec_);
    }

    /**
     * For performance reasons as "names" are the most common data required, a
     * cache is created to avoid hitting the database with too many individual
     * requests. This cache is good for the life of this DBAlert object and will
     * be rebuilt as needed with each new DBAlert.
     * 
     * @param id_
     *        The name id to look up in the database.
     * @return When > 0, the position of the name in the cache. When < 0, the
     *         position it should be in the cache when added later.
     */
    private int findName(String id_)
    {
        int min = 0;
        int max = _nameID.length;

        // Use a binary search. It seems the most efficient for this purpose.
        while (true)
        {
            int ndx = (max + min) / 2;
            int compare = id_.compareTo(_nameID[ndx]);
            if (compare == 0)
            {
                return ndx;
            }
            else if (compare > 0)
            {
                if (min == ndx)
                {
                    ++min;
                    return -min;
                }
                min = ndx;
            }
            else
            {
                if (max == ndx)
                    return -max;
                max = ndx;
            }
        }
    }

    /**
     * Cache names internally as they are encountered. If the findName() method
     * can not locate a name in the cache it will be added by this method.
     * 
     * @param pos_
     *        The insert position returned from findName().
     * @param id_
     *        The name id to use as a key.
     * @param name_
     *        The name to return for this id.
     */
    private void cacheName(int pos_, String id_, String name_)
    {
        // Don't save null names, use the id if needed.
        if (name_ == null)
            name_ = id_;

        // Move all existing records down to make room for the new name.
        String nid[] = new String[_nameID.length + 1];
        String ntxt[] = new String[nid.length];

        int ndx;
        int ndx2 = 0;
        for (ndx = 0; ndx < pos_; ++ndx)
        {
            nid[ndx] = _nameID[ndx2];
            ntxt[ndx] = _nameText[ndx2++];
        }

        // Add the new name.
        nid[ndx] = new String(id_);
        ntxt[ndx] = new String(name_);

        // Copy the rest and reset the arrays.
        for (++ndx; ndx < nid.length; ++ndx)
        {
            nid[ndx] = _nameID[ndx2];
            ntxt[ndx] = _nameText[ndx2++];
        }
        _nameID = nid;
        _nameText = ntxt;
    }

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
    public String selectName(String table_, String col_, String id_)
    {
        // Can't work without a column name.
        if (col_ == null)
            return id_;
        if (id_ == null || id_.length() == 0)
            return null;

        // Determine the real table and column names to use.
        int npos = 0;
        String table = table_;
        String name = "long_name";
        String col = col_;
        String extra = "";
        if (table == null || table.length() == 0)
        {
            int ndx = binarySearch(_DBMAP2KEYS, col);
            if (ndx == -1)
                return id_;
            table = _DBMAP2VALS[ndx];
            name = _DBMAP2SUBS[ndx];
            col = _DBMAP2COLS[ndx];
            extra = _DBMAP2XTRA[ndx];
            if (col.equals("ua_name"))
            {
                // Is the name cached?
                npos = findName(id_);
                if (npos >= 0)
                    return _nameText[npos];
            }
        }

        // Build a select and retrieve the "name".
        String select = "select " + name + " from " + table + " where " + col
            + " = ?" + extra;
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, id_);
            ResultSet rs = pstmt.executeQuery();
            name = "";
            while (rs.next())
                name = name + "\n" + rs.getString(1);
            if (name.length() == 0)
                name = null;
            else
                name = name.substring(1);
            rs.close();
            pstmt.close();

            if (col.equals("ua_name") && npos < 0)
            {
                cacheName(-npos, id_, name);
            }
        }
        catch (SQLException ex)
        {
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 33: " + _errorCode + ": " + select
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            name = "(*error*)";
        }
        return (name == null) ? id_ : name;
    }

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
    public void selectNames(String cols_[], String ids_[])
    {
        for (int ndx = 0; ndx < cols_.length; ++ndx)
        {
            ids_[ndx] = selectName(null, cols_[ndx], ids_[ndx]);
        }
    }

    /**
     * Perform a binary search on a sorted list of keys.
     * 
     * @param keys_
     *        The key array to be searched.
     * @param vals_
     *        The values which match the key entries.
     * @param key_
     *        The key to search for.
     * @return The value corresponding to the matching key if found or the
     *         original key (key_) if not found.
     */
    static public String binarySearch(String keys_[], String vals_[],
        String key_)
    {
        int ndx = binarySearch(keys_, key_);
        if (ndx == -1)
            return key_;
        return vals_[ndx];
    }

    /**
     * Perform a binary search on a sorted list of keys.
     * 
     * @param keys_
     *        The key array to be searched.
     * @param key_
     *        The key to search for.
     * @return The index into the keys_ array.
     */
    static public int binarySearch(String keys_[], String key_)
    {
        int min = 0;
        int max = keys_.length;
        while (true)
        {
            int ndx = (max + min) / 2;
            int compare = key_.compareTo(keys_[ndx]);
            if (compare == 0)
            {
                return ndx;
            }
            else if (compare > 0)
            {
                if (min == ndx)
                    return -1;
                min = ndx;
            }
            else
            {
                if (max == ndx)
                    return -1;
                max = ndx;
            }
        }
    }

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
    static public String translateColumn(String namespace_, String val_)
    {
        // First search global name space as most are consistent.
        String rc = binarySearch(_DBMAP1KEYS, _DBMAP1VALS, val_);
        if (rc == val_)
        {
            // We didn't find it in global now look in the specific name space.
            if (namespace_.compareTo("DESIGNATIONS") == 0)
                rc = binarySearch(_DBMAP1KEYS_DESIG, _DBMAP1VALS_DESIG, val_);

            else if (namespace_.compareTo("REFERENCE_DOCUMENTS") == 0)
                rc = binarySearch(_DBMAP1KEYS_RD, _DBMAP1VALS_RD, val_);

            else if (namespace_.compareTo("AC_CSI") == 0)
                rc = binarySearch(_DBMAP1KEYS_CSI, _DBMAP1VALS_CSI, val_);

            else if (namespace_.compareTo("COMPLEX_DATA_ELEMENTS") == 0)
                rc = binarySearch(_DBMAP1KEYS_COMPLEX, _DBMAP1VALS_COMPLEX, val_);

            else
                rc = binarySearch(_DBMAP1KEYS_OTHER, _DBMAP1VALS_OTHER, val_);
            return rc;
        }
        return rc;
    }

    /**
     * Translate the table names for the user.
     * 
     * @param val_
     *        The internal table name.
     * @return The user readable name.
     */
    static public String translateTable(String val_)
    {
        if (val_ == null)
            return "<null>";
        return binarySearch(_DBMAP3KEYS, _DBMAP3VALS, val_);
    }

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
        boolean setInactive_)
    {
        String update = "update sbrext.sn_alert_view_ext set "
            + ((run_) ? "last_auto_run" : "last_manual_run") + " = ?"
            + ((setInactive_) ? ", al_status = 'I' " : " ")
            + "where al_idseq = ?";
        try
        {
            PreparedStatement pstmt = null;
            // Set all the SQL arguments.
            pstmt = _conn.prepareStatement(update);
            pstmt.setTimestamp(1, stamp_);
            pstmt.setString(2, id_);
            pstmt.executeUpdate();
            pstmt.close();
            _needCommit = true;
            return 0;
        }
        catch (SQLException ex)
        {
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 34: " + _errorCode + ": " + update
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return _errorCode;
        }
    }

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
    public String selectRecipientNames(String recipients_[])
    {
        // Check input.
        if (recipients_ == null || recipients_.length == 0)
            return "(none)";

        // Break the recipients list apart.
        String contexts = "";
        String users = "";
        String emails = "";
        for (int ndx = 0; ndx < recipients_.length; ++ndx)
        {
            if (recipients_[ndx].charAt(0) == '/')
                contexts = contexts + ", '" + recipients_[ndx].substring(1) + "'";
            else if (recipients_[ndx].indexOf('@') < 0)
                users = users + ", '" +  recipients_[ndx] + "'";
            else
                emails = emails + ", " +  recipients_[ndx];
        }
        
        // Determine the tables to join.
        String tables = "sbr.user_accounts_view ua";
        if (contexts.length() > 0)
            tables = tables + ", sbrext.user_contexts_view uc, sbr.contexts_view c";
        
        // Start the SQL select statement.
        String select = "select distinct ua.name from " + tables +" where ";
        if (contexts.length() > 0)
        {
            // Expand the broadcast context groups.
            select = select + "(c.conte_idseq in (" + contexts.substring(2)
                + ") and uc.name = c.name "
                + "and uc.privilege = 'W' "
                + "and ua.ua_name = uc.ua_name "
                + "and ua.alert_ind = 'Yes' "
                + "and ua.electronic_mail_address is not null) ";

            // If there are individual users this is an OR operation.
            if (users.length() > 0)
                select = select + "or ";
        }

        // Include the individual users.
        if (users.length() > 0)
            select = select
                + "(ua.ua_name in (" + users.substring(2) + ") "
                + "and ua.electronic_mail_address is not null) ";
        
        // Sort the results.
        select = select + "order by UPPER(ua.name)";
        
        try
        {
            // Retrieve the user names from the database.
            PreparedStatement pstmt = _conn.prepareStatement(select);
            ResultSet rs = pstmt.executeQuery();
            String names = "";
            
            // Make this a comma separated list.
            while (rs.next())
            {
                names = names + ", " + rs.getString(1);
            }
            rs.close();
            pstmt.close();

            // Append the freeform email addresses.
            if (emails.length() > 0)
                names = names + emails;
            return (names.length() > 0) ? names.substring(2) : "(none)";
        }
        catch (SQLException ex)
        {
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 35: " + _errorCode + ": " + select
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return null;
        }
    }
    
    /**
     * Given the idseq of a Context, retrieve all the users with write access to
     * that context.
     * 
     * @param conte_
     *        The context idseq.
     * @return The array of user ids with write access.
     */
    public String[] selectEmailsFromConte(String conte_)
    {
        String select = "select ua.electronic_mail_address "
            + "from sbrext.user_contexts_view uc, sbr.user_accounts_view ua, sbr.contexts_view c "
            + "where c.conte_idseq = ? and uc.name = c.name and uc.privilege = 'W' and ua.ua_name = uc.ua_name "
            + "and ua.alert_ind = 'Yes'";

        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            if (conte_.charAt(0) == '/')
                pstmt.setString(1, conte_.substring(1));
            else
                pstmt.setString(1, conte_);
            ResultSet rs = pstmt.executeQuery();
            Vector<String> temp = new Vector<String>();
            while (rs.next())
            {
                temp.add(rs.getString(1));
            }
            rs.close();
            pstmt.close();

            String curators[] = new String[temp.size()];
            for (int ndx = 0; ndx < curators.length; ++ndx)
                curators[ndx] = (String) temp.get(ndx);
            return curators;
        }
        catch (SQLException ex)
        {
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 35: " + _errorCode + ": " + select
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return null;
        }
    }

    /**
     * Given the id for a user, retrieve the email address.
     * 
     * @param user_
     *        The user id.
     * @return The array of user ids with write access.
     */
    public String selectEmailFromUser(String user_)
    {
        String select = "select ua.electronic_mail_address "
            + "from sbr.user_accounts_view ua " + "where ua.ua_name = ?";

        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select);
            pstmt.setString(1, user_);
            ResultSet rs = pstmt.executeQuery();
            String temp = null;
            if (rs.next())
            {
                temp = rs.getString(1);
            }
            rs.close();
            pstmt.close();
            return temp;
        }
        catch (SQLException ex)
        {
            _errorCode = ex.getErrorCode();
            _errorMsg = "\n\nDBAlert 36: " + _errorCode + ": " + select
                + "\n\n" + ex.toString();
            System.err.println(_errorMsg);
            return null;
        }
    }

    /**
     * Run a specific SELECT for the testDBdependancies() method.
     * 
     * @param select_
     *        The select statement.
     * @return >0 if successful with the number of rows returned, otherwise
     *         failed.
     */
    private int testDB(String select_)
    {
        try
        {
            PreparedStatement pstmt = _conn.prepareStatement(select_);
            ResultSet rs = pstmt.executeQuery();
            int rows;
            for (rows = 0; rs.next(); ++rows)
                ;
            rs.close();
            pstmt.close();
            return rows;
        }
        catch (SQLException ex)
        {
            _errorCode = ex.getErrorCode();
            _errorMsg = select_ + "\n" + ex.toString();
            return -1;
        }
    }

    /**
     * Test the database dependencies within this class. This method will check
     * the existence of tables, columns and required values.
     * 
     * @return null if all dependencies are present, otherwise a string
     *         detailing those that failed.
     */
    public String testDBdependancies()
    {
        String results = "";
        String select = "select ua_name, name, electronic_mail_address, alert_ind "
            + "from sbr.user_accounts_view "
            + "where (ua_name is null or name is null or alert_ind is null) and rownum < 2";
        int rows = testDB(select);
        if (rows != 0)
        {
            if (rows < 0)
                results += _errorMsg;
            else
                results += "One of the columns ua_name, name or alert_ind in the table sbr.user_accounts_view is NULL";
            results += "\n\n";
        }

        select = "select * from sbrext.sn_alert_view_ext where rownum < 2";
        rows = testDB(select);
        if (rows < 0)
            results += _errorMsg + "\n\n";

        select = "select * from sbrext.sn_query_view_ext where rownum < 2";
        rows = testDB(select);
        if (rows < 0)
            results += _errorMsg + "\n\n";

        select = "select * from sbrext.sn_recipient_view_ext where rownum < 2";
        rows = testDB(select);
        if (rows < 0)
            results += _errorMsg + "\n\n";

        select = "select * from sbrext.sn_report_view_ext where rownum < 2";
        rows = testDB(select);
        if (rows < 0)
            results += _errorMsg + "\n\n";

        select = "select ua_name, name, privilege from sbrext.user_contexts_view where rownum < 2";
        rows = testDB(select);
        if (rows < 0)
            results += _errorMsg + "\n\n";

        select = "select tool_name, property, ua_name, value from sbrext.tool_options_view_ext where rownum < 2";
        rows = testDB(select);
        if (rows < 0)
            results += _errorMsg + "\n\n";
        
        _errorCode = 0;
        _errorMsg = "";
        return (results.length() == 0) ? null : results;
    }

    /**
     * Test the content of the tool options table.
     * 
     * @return null if no errors, otherwise the error message.
     */
    public String testDBoptions()
    {
        String results = "";
        String select = "select tool_idseq from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' and property = 'URL' and value is not null";
        int rows = testDB(select);
        if (rows != 1)
            results += "Missing the Sentinel Tool URL setting.\n\n";
        
        select = "select tool_idseq from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' AND property LIKE 'ADMIN.%' and value like '%0%'";
        rows = testDB(select);
        if (rows < 1)
            results += "Missing the Sentinel Tool Alert Administrator setting.\n\n";
        
        select = "select tool_idseq from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' AND property LIKE 'ADMIN.%' and value like '%1%'";
        rows = testDB(select);
        if (rows < 1)
            results += "Missing the Sentinel Tool Report Administrator setting.\n\n";
        
        select = "select tool_idseq from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' AND property = 'ALERT.NAME.FORMAT' and value is not null";
        rows = testDB(select);
        if (rows != 1)
            results += "Missing the Sentinel Tool ALERT.NAME.FORMAT setting.\n\n";
        
        if (selectAlertReportAdminEmails() == null)
            results += "Missing email addresses for the specified Alert Report Administrator(s) setting.\n\n";
        
        select = "select tool_idseq from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' AND property LIKE 'BROADCAST.EXCLUDE.CONTEXT.%.NAME'";
        rows = testDB(select);
        if (rows > 0)
        {
            int optcnt = rows;
            select = "select cov.name "
                + "from sbrext.tool_options_view_ext to1, sbrext.tool_options_view_ext to2, sbr.contexts_view cov "
                + "where to1.tool_name = 'SENTINEL' AND to2.tool_name = to1.tool_name "
                + "and to1.property LIKE 'BROADCAST.EXCLUDE.CONTEXT.%.NAME' "
                + "and to2.property LIKE 'BROADCAST.EXCLUDE.CONTEXT.%.CONTE_IDSEQ' "
                + "and SUBSTR(to1.property, 1, 29) = SUBSTR(to2.property, 1, 29) "
                + "and to1.value = cov.name "
                + "and to2.value = cov.conte_idseq";
            rows = testDB(select);
            if (rows != optcnt)
                results += "Missing or invalid BROADCAST.EXCLUDE.CONTEXT settings.";
        }
        
        select = "select tool_idseq from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' AND property like 'RSVD.CS.%'";
        rows = testDB(select);
        select = "select tool_idseq from sbrext.tool_options_view_ext "
            + "where tool_name = 'SENTINEL' AND property = 'RSVD.CSI.FORMAT' AND value like '%$ua_name$%'";
        rows += testDB(select);
        if (rows > 0)
        {
            if (rows == 3)
            {
                select = "select cs.long_name "
                    + "from sbrext.tool_options_view_ext to1, sbrext.tool_options_view_ext to2, sbr.classification_schemes_view cs "
                    + "where to1.tool_name = 'SENTINEL' AND to2.tool_name = to1.tool_name "
                    + "and to1.property = 'RSVD.CS.LONG_NAME' "
                    + "and to2.property = 'RSVD.CS.CS_IDSEQ' "
                    + "and to1.value = cs.long_name "
                    + "and to2.value = cs.cs_idseq";
                rows = testDB(select);
                if (rows != 1)
                    results += "Missing or invalid RSVD.CS settings.";
            }
            else
                results += "Missing or invalid RSVD.CS settings.";
        }
        
        _errorCode = 0;
        _errorMsg = "";
        return (results.length() == 0) ? null : results;
    }

    /**
     * Return the email addresses for all the administrators that should receive a log report.
     * 
     * @return The list of email addresses.
     */
    public String[] selectAlertReportAdminEmails()
    {
        String select = "select ua.electronic_mail_address, ua.name "
            + "from sbr.user_accounts_view ua, sbrext.tool_options_view_ext opt "
            + "where opt.tool_name = 'SENTINEL' and "
            + "opt.property like 'ADMIN.%' and "
            + "opt.value like '%1%' and ua.ua_name = opt.ua_name "
            + "and ua.electronic_mail_address is not null "
            + "order by opt.property";

        returnData1 rec = getBasicData1(select, false);
        if (rec._rc == 0 && rec._vals != null && rec._vals.length > 0)
        {
            return rec._vals;
        }
        
        return null;
    }

    /**
     * Return the Alert Definition name format string. 
     * 
     * @return The list of email addresses.
     */
    public String selectAlertNameFormat()
    {
        String select = "select opt.value, 'x' "
            + "from sbrext.tool_options_view_ext opt "
            + "where opt.tool_name = 'SENTINEL' and "
            + "opt.property = 'ALERT.NAME.FORMAT' ";

        returnData1 rec = getBasicData1(select, false);
        if (rec._rc == 0 && rec._vals != null && rec._vals.length > 0)
        {
            return rec._vals[0];
        }
        
        return null;
    }

    /**
     * Return the reserved CS id if the reserved CSI is passed to the method.
     * 
     * @param idseq_ The CSI id to check.
     * 
     * @return The reserved CS id or null if the CSI is not reserved.
     */
    public String selectCSfromReservedCSI(String idseq_)
    {
        String select = "select opt.value, 'x' "
            + "from sbrext.tool_options_view_ext opt, sbr.classification_schemes_view cs, "
            + "sbr.cs_csi_view ci "
            + "where ci.csi_idseq = '" + idseq_ + "' and cs.cs_idseq = ci.cs_idseq and opt.value = cs.cs_idseq and "
            + "opt.tool_name = 'SENTINEL' and opt.property = 'RSVD.CS.CS_IDSEQ'";

        returnData1 rec = getBasicData1(select, false);
        if (rec._rc == 0 && rec._vals != null && rec._vals.length > 0)
        {
            return rec._vals[0];
        }
        
        return null;
    }
    
    /**
     * Retrieve the row counts for all the tables used by the Alert Report.
     * The values may be indexed using the _ACTYPE_* variables and an index
     * of _ACTYPE_LENGTH is the count of the change history table.  
     * 
     * @return The numbers for each table.
     */
    public String[] getRowCounts()
    {
        String counts[] = new String[_ACTYPE_LENGTH + 1];
        
        String select = "select count(*) from ";
        String table;
        String name;
        
        for (int ndx = 0; ndx < counts.length; ++ndx)
        {
            if (ndx == _ACTYPE_LENGTH)
            {
                table = "sbrext.ac_change_history_ext";
                name = "History Table";
            }
            else if (_DBMAP3TABLES[ndx] == null)
            {
                counts[ndx] = null;
                continue;
            }
            else
            {
                table = _DBMAP3TABLES[ndx];
                name = _DBMAP3VALS[ndx];
            }

            String temp = select + table;
            try
            {
                PreparedStatement pstmt = _conn.prepareStatement(temp);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next())
                {
                    counts[ndx] = name + "::" + rs.getString(1);
                }
                rs.close();
                pstmt.close();
            }
            catch (SQLException ex)
            {
                _errorCode = ex.getErrorCode();
                _errorMsg = temp + "\n" + ex.toString();
                counts[ndx] = name + ": " + _errorMsg;
            }
        }
        
        return counts;
    }

    // Class data elements.
    private String              _namesList[];

    private String              _namesVals[];

    private String              _namesExempt;

    private String              _contextList[];

    private String              _contextVals[];

    private String              _schemeList[];

    private String              _schemeVals[];

    private String              _schemeContext[];

    private String              _protoList[];

    private String              _protoVals[];

    private String              _protoContext[];

    private String              _schemeItemList[];

    private String              _schemeItemVals[];

    private String              _schemeItemSchemes[];

    private Connection          _conn;

    private ServletContext      _sc;

    private boolean             _needCommit;

    private String              _groupsList[];

    private String              _groupsVals[];

    private String              _formsList[];

    private String              _formsVals[];

    private String              _formsContext[];

    private String              _workflowList[];

    private String              _workflowVals[];

    private String              _cworkflowList[];

    private String              _cworkflowVals[];

    private String              _regStatusList[];

    private String              _regStatusVals[];

    private String              _regCStatusList[];

    private String              _regCStatusVals[];

    private String              _nameID[];

    private String              _nameText[];

    private int                 _errorCode;

    private String              _errorMsg;
    
    private String              _actypesList[];
    
    private String              _actypesVals[];

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

    private static final String _DBPOOL       = "DSRAlertPool";

    private static final String _CONTEXT      = "CONTEXT";

    private static final String _FORM         = "FORM";
    
    private static final String _PROTOCOL     = "PROTOCOL";

    private static final String _SCHEME       = "CS";

    private static final String _SCHEMEITEM   = "CSI";

    private static final String _CREATOR      = "CREATOR";

    private static final String _MODIFIER     = "MODIFIER";

    private static final String _REGISTER     = "REG_STATUS";

    private static final String _STATUS       = "AC_STATUS";

    private static final String _ACTYPE       = "ACTYPE";

    private static final String _DATEFILTER   = "DATEFILTER";

    // The following DBMap1 arrays must be kept in sync. The number of entries
    // must match
    // and the order must match as these are key/value pairs. They are written
    // as String
    // arrays to make the methods more generic and usable outside this class.
    private static final String _DBMAP1KEYS[] = { 
        "ASL_NAME", 
        "BEGIN_DATE",
        "CDE_ID",
        "CDR_IDSEQ",
        "CD_IDSEQ",
        "CHANGE_NOTE",
        "CONDR_IDSEQ",
        "CON_IDSEQ",
        "CREATED_BY",
        "CSTL_NAME",
        "CS_ID",
        "C_DEC_IDSEQ",
        "C_DE_IDSEQ",
        "C_VD_IDSEQ",
        "DATE_CREATED",
        "DATE_MODIFIED",
        "DECIMAL_PLACE",
        "DEC_ID",
        "DEC_IDSEQ",
        "DEC_REC_IDSEQ",
        "DEFINITION_SOURCE",
        "DELETED_IND",
        "DESCRIPTION",
        "DESIG_IDSEQ",
        "DE_IDSEQ",
        "DE_REC_IDSEQ",
        "DISPLAY_ORDER",
        "DTL_NAME", 
        "END_DATE", 
        "FORML_NAME", 
        "HIGH_VALUE_NUM",
        "LABEL_TYPE_FLAG",
        "LATEST_VERSION_IND",
        "LONG_NAME",
        "LOW_VALUE_NUM",
        "MAX_LENGTH_NUM",
        "METHODS",
        "MIN_LENGTH_NUM",
        "MODIFIED_BY",
        "OBJ_CLASS_QUALIFIER",
        "OCL_NAME",
        "OC_ID",
        "OC_IDSEQ",
        "ORIGIN", 
        "PREFERRED_DEFINITION",
        "PREFERRED_NAME",
        "PROPERTY_QUALIFIER",
        "PROPL_NAME",
        "PROP_ID",
        "PROP_IDSEQ",
        "PV_IDSEQ",
        "P_DEC_IDSEQ",
        "P_DE_IDSEQ",
        "P_VD_IDSEQ",
        "QUALIFIER_NAME",
        "QUESTION", 
        "RD_IDSEQ",
        "REP_IDSEQ",
        "RL_NAME",
        "RULE",
        "SHORT_MEANING",
        "UOML_NAME",
        "URL",
        "VALUE",
        "VD_ID",
        "VD_IDSEQ",
        "VD_REC_IDSEQ",
        "VD_TYPE_FLAG",
        "VERSION" };

    private static final String _DBMAP1VALS[] = { 
        "Workflow Status",
        "Begin Date",
        "Public ID",
        "Complex DE association",
        "Conceptual Domain association",
        "Change Note",
        "Concept Class association",
        "Concept Class association",
        "Created By",
        "Category",
        "Public ID",
        "Child DEC association",
        "Child DE association",
        "Child VD association",
        "Created Date",
        "Modified Date", 
        "Number of Decimal Places",
        "Public ID",
        "Data Element Concept association",
        "DEC_REC_IDSEQ",
        "Definition Source",
        "Deleted Indicator",
        "Description",
        "Designation association",
        "Data Element association",
        "DE_REC_IDSEQ",
        "Display Order", 
        "Data Type",
        "End Date",
        "Data Format",
        "Maximum Value",
        "Label Type",
        "Latest Version Indicator",
        "Long Name",
        "Minimum Value",
        "Maximum Length",
        "Methods",
        "Minimum Length",
        "Modified By",
        "Object Class Qualifier",
        "Object Class Name",
        "Public ID",
        "Object Class association",
        "Origin",
        "Preferred Definition",
        "Preferred Name",
        "Property Qualifier",
        "Property Name",
        "Public ID",
        "Property",
        "Permissible Value",
        "Parent DEC association",
        "Parent DE association",
        "Parent VD association",
        "Qualifier",
        "Question",
        "Reference Document association",
        "Representation association",
        "Relationship Name",
        "Rule",
        "Meaning",
        "Unit Of Measure",
        "URL",
        "Value",
        "Public ID",
        "Value Domain association",
        "VD_REC_IDSEQ",
        "Enumerated/Non-enumerated",
        "Version" };

    private static final String _DBMAP1KEYS_OTHER[] = {
        "CONTE_IDSEQ",
        "LAE_NAME",
        "NAME"
    };
    
    private static final String _DBMAP1VALS_OTHER[] = {
        "Owned By Context",
        "Language",
        "Name"
    };

    private static final String _DBMAP1KEYS_DESIG[] = {
        "CONTE_IDSEQ",
        "DETL_NAME",
        "LAE_NAME"
    };
    
    private static final String _DBMAP1VALS_DESIG[] = {
        "Designation Context",
        "Designation Type",
        "Designation Language"
    };
    
    private static final String _DBMAP1KEYS_CSI[] = {
        "CS_CSI_IDSEQ"
    };
    
    private static final String _DBMAP1VALS_CSI[] = {
        "Classification Scheme Item association"
    };
    
    private static final String _DBMAP1KEYS_RD[] = {
        "DCTL_NAME",
        "DISPLAY_ORDER",
        "DOC_TEXT",
        "RDTL_NAME",
        "URL"
    };
    
    private static final String _DBMAP1VALS_RD[] = {
        "Document Type",
        "Document Display Order",
        "Document Text",
        "Document Text Type",
        "Document URL"
    };
    
    private static final String _DBMAP1KEYS_COMPLEX[] = {
        "CONCAT_CHAR",
        "CRTL_NAME",
    };
    
    private static final String _DBMAP1VALS_COMPLEX[] = {
        "Concatenation Character",
        "Complex Type",
    };
    
    private static final String _DBMAP2KEYS[] = {
        "CD_IDSEQ",
        "CONDR_IDSEQ",
        "CONTE_IDSEQ",
        "CON_IDSEQ",
        "CREATED_BY",
        "CS_CSI_IDSEQ",
        "DEC_IDSEQ",
        "DE_IDSEQ",
        "MODIFIED_BY",
        "OC_IDSEQ",
        "PROP_IDSEQ",
        "PV_IDSEQ",
        "RD_IDSEQ",
        "REP_IDSEQ",
        "UA_NAME",
        "VD_IDSEQ"};

    private static final String _DBMAP2VALS[] = {
        "sbr.conceptual_domains_view",
        "sbrext.component_concepts_view_ext ccv, sbrext.concepts_view_ext cv",
        "sbr.contexts_view",
        "sbrext.concepts_view_ext",
        "sbr.user_accounts_view",
        "sbr.cs_csi_view cci, sbr.class_scheme_items_view csi",
        "sbr.data_element_concepts_view",
        "sbr.data_elements_view",
        "sbr.user_accounts_view",
        "sbrext.object_classes_view_ext",
        "sbrext.properties_view_ext",
        "sbr.permissible_values_view",
        "sbr.reference_documents_view",
        "sbrext.representations_view_ext",
        "sbr.user_accounts_view",
        "sbr.value_domains_view"};

    private static final String _DBMAP2COLS[] = {
        "cd_idseq",
        "ccv.condr_idseq",
        "conte_idseq",
        "con_idseq",
        "ua_name",
        "cci.cs_csi_idseq",
        "dec_idseq",
        "de_idseq",
        "ua_name",
        "oc_idseq",
        "prop_idseq",
        "pv_idseq",
        "rd_idseq",
        "rep_idseq",
        "ua_name",
        "vd_idseq"};

    private static final String _DBMAP2XTRA[] = {
        "",
        " and cv.con_idseq = ccv.con_idseq order by ccv.display_order desc",
        "",
        "",
        "",
        " and csi.csi_idseq = cci.csi_idseq",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        ""};

    private static final String _DBMAP2SUBS[] = {
        "long_name || ' (' || cd_id || 'v' || version || ')' as label",
        "cv.long_name || ' (' || cv.con_id || 'v' || cv.version || ') (' || cv.origin || ':' || cv.preferred_name || ')' as label",
        "name || ' (v' || version || ')' as label",
        "long_name || ' (' || con_id || 'v' || version || ') (' || origin || ':' || preferred_name || ')' as label",
        "name as label",
        "csi.csi_name as label",
        "long_name || ' (' || dec_id || 'v' || version || ')' as label",
        "long_name || ' (' || cde_id || 'v' || version || ')' as label",
        "name as label",
        "long_name || ' (' || oc_id || 'v' || version || ')' as label",
        "long_name || ' (' || prop_id || 'v' || version || ')' as label",
        "value || ' (' || short_meaning || ')' as label",
        "name || ' (' || nvl(doc_text, url) || ')' as label",
        "long_name || ' (' || rep_id || 'v' || version || ')' as label",
        "name as label",
        "long_name || ' (' || vd_id || 'v' || version || ')' as label" };

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_CD     = 0;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_CONTE  = 1;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_CS     = 2;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_CSI    = 3;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_DE     = 4;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_DEC    = 5;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_OC     = 6;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_PROP   = 7;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_PROTO  = 8;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_PV     = 9;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_QC     = 10;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_QCM    = 11;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_QCQ    = 12;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_QCV    = 13;

    /**
     * The AC Type numeric equivalents. Must match the index into the
     * _DBMAP3KEYS array.
     */
    public static final int _ACTYPE_VD     = 14;

    /**
     * The AC Type total number of values.
     */
    public static final int _ACTYPE_LENGTH = 15;
    
    private static final String _DBMAP3KEYS[] = {
        "cd", "conte", "cs", "csi",
        "de", "dec", "oc", "prop",
        "proto", "pv", "qc", "qcm",
        "qcq", "qcv", "vd" };

    private static final String _DBMAP3VALS[] = {
        "Conceptual Domain", "Context", "Classification Scheme", "Classification Scheme Item",
        "Data Element", "Data Element Concept", "Object Class", "Property",
        "Protocol", "Permissible Value", "Form/Template", "Module",
        "Question", "Valid Value", "Value Domain" };
    
    private static final String _DBMAP3TABLES[] = {
        "sbr.conceptual_domains_view",
        "sbr.contexts_view",
        "sbr.classification_schemes_view",
        "sbr.class_scheme_items_view",
        "sbr.data_elements_view",
        "sbr.data_element_concepts_view",
        "sbrext.object_classes_view_ext",
        "sbrext.properties_view_ext",
        "sbrext.protocols_view_ext",
        "sbr.permissible_values_view",
        "sbrext.quest_contents_view_ext",
        null,
        null,
        null,
        "sbr.value_domains_view"
    };
    
    private static final String _DBMAP3CHGCOL[] = {
        null,
        null,
        "CLASSIFICATION_SCHEMES",
        null,
        "DATA_ELEMENTS",
        "DATA_ELEMENT_CONCEPTS",
        "OBJECT_CLASSES_EXT",
        "PROPERTIES_EXT",
        null,
        "PERMISSIBLE_VALUES",
        null,
        null,
        null,
        null,
        "VALUE_DOMAINS"
    };

    private static final char   _CRITERIA     = 'C';

    private static final char   _MONITORS     = 'M';

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
    
    private static final String _DATECHARS[][] = {
        {"<", ">", ">=", "<"},
        {">=", "<", "<", ">"},
        {">=", "<", ">=", "<"}
    };
    
    private static final String _orderbyACH = 
        "order by id asc, "
//        + "cid asc, "
//        + "zz.date_modified asc, "
        + "ach.change_datetimestamp asc, "
        + "ach.changed_table asc, "
        + "ach.changed_table_idseq asc, "
        + "ach.changed_column asc";
}
