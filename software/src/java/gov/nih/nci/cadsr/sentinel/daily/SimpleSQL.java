// Copyright (c) 2009 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/daily/SimpleSQL.java,v 1.2 2009-03-16 17:14:21 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.daily;

import gov.nih.nci.cadsr.sentinel.tool.Constants;

import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import oracle.jdbc.pool.OracleDataSource;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

 /**
  * This class is responsible for refreshing the materialized views and executing other simple SQL daily on
  * the caDSR database.
  * 
  * @author lhebel
  *
  */
public class SimpleSQL
{
    private static Logger       _logger     = Logger.getLogger(SimpleSQL.class);

    private String              _dsurl;

    private String              _user;

    private String              _pswd;

    private Properties          _propList;

    private Connection          _conn;

    private static final String _sqlMVRefresh = "begin sbrext_admin_mv.refresh_mvw; end;";

    /**
     * @param args_
     */
    public static void main(String[] args_)
    {
        if (args_.length != 2)
        {
            System.err.println(SimpleSQL.class.getName() + " log4j.xml config.xml");
            return;
        }

        DOMConfigurator.configure(args_[0]);

        SimpleSQL ss = new SimpleSQL();

        try
        {
            _logger.info("");
            _logger.info(CleanStrings.class.getClass().getName() + " begins");
            ss.doAll(args_[1]);
        }
        catch (Exception ex)
        {
            _logger.error(ex.toString(), ex);
        }
    }
    
    /**
     * A convenience method to do all the work.
     * 
     * @param propFile_ configuration profile
     * @throws Exception
     */
    public void doAll(String propFile_) throws Exception
    {
        loadProp(propFile_);
        open();

        try
        {
            _logger.info("Do " + _sqlMVRefresh);
            refreshMV();
        }
        catch (Exception ex)
        {
            _logger.error(ex.toString(), ex);
        }
        finally
        {
            _logger.info("End " + _sqlMVRefresh);
        }
        
        if (_conn != null)
        {
            _conn.close();
            _conn = null;
        }
    }

    /**
     * Refresh materialized views
     * 
     * @throws Exception
     */
    private void refreshMV() throws Exception
    {
        CallableStatement cs = null;
        try
        {
            cs = _conn.prepareCall(_sqlMVRefresh);
            cs.execute();
        }
        finally
        {
            if (cs != null)
                cs.close();
        }
    }

    /**
     * Load the properties from the XML file specified.
     *
     * @param propFile_ the properties file.
     */
    private void loadProp(String propFile_) throws Exception
    {
        _propList = new Properties();

        _logger.info("\n\nLoading properties " + gov.nih.nci.cadsr.common.Constants.BUILD_TAG + " ...\n\n");

        try
        {
            FileInputStream in = new FileInputStream(propFile_);
            _propList.loadFromXML(in);
            in.close();
        }
        catch (Exception ex)
        {
            throw ex;
        }

        _dsurl = _propList.getProperty(Constants._DSURL);
        if (_dsurl == null)
            _logger.error("Missing " + Constants._DSURL + " connection string in " + propFile_);

        _user = _propList.getProperty(Constants._DSUSER);
        if (_user == null)
            _logger.error("Missing " + Constants._DSUSER + " in " + propFile_);

        _pswd = _propList.getProperty(Constants._DSPSWD);
        if (_pswd == null)
            _logger.error("Missing " + Constants._DSPSWD + " in " + propFile_);
    }

    /**
     * Open a single simple connection to the database. No pooling is necessary.
     *
     * @param _dsurl
     *        The Oracle TNSNAME entry describing the database location.
     * @param user_
     *        The ORACLE user id.
     * @param pswd_
     *        The password which must match 'user_'.
     * @return The database error code.
     */
    private int open() throws Exception
    {
        // If we already have a connection, don't bother.
        if (_conn != null)
            return 0;

        try
        {
            OracleDataSource ods = new OracleDataSource();

            String parts[] = _dsurl.split("[:]");
            ods.setDriverType("thin");
            ods.setServerName(parts[0]);
            ods.setPortNumber(Integer.parseInt(parts[1]));
            ods.setServiceName(parts[2]);

            _conn = ods.getConnection(_user, _pswd);
            _conn.setAutoCommit(false);
            return 0;
        }
        catch (SQLException ex)
        {
            throw ex;
        }
    }
}
