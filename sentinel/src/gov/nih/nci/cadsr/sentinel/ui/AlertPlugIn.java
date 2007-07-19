// Copyright (c) 2006 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/AlertPlugIn.java,v 1.3 2007-07-19 15:26:45 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import gov.nih.nci.cadsr.sentinel.database.DBAlert;
import gov.nih.nci.cadsr.sentinel.tool.Constants;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;

/**
 * This class allows the interrogation of application intialization parameters. Specifically
 * the code pulls the JBoss Data Source setting and default User ID and Password for
 * database access.
 * 
 * @author lhebel
 *
 */
public class AlertPlugIn implements PlugIn
{
    
    private DataSource _dsTest;
    private String _user;
    private String _pswd;
    private String _dataSource;
    private String _authenticate;
    private static boolean _printFirstRequest = true;

    private static final Logger _logger = Logger.getLogger(AlertPlugIn.class.getName());

    public void destroy()
    {
        _logger.info(" ");
        _logger.info("Sentinel Tool stopped ..................................................................");
    }

    public void init(ActionServlet servlet_, ModuleConfig module_) throws ServletException
    {
        ResourceBundle props = PropertyResourceBundle.getBundle("gov.nih.nci.cadsr.sentinel.DSRAlert");
        String temp = (props != null) ? props.getString("Appl.version") : "Error loading Property file.";
        props = null;
        temp = temp.replace("&nbsp;", " ").trim();

        _logger.info(" ");
        _logger.info("Sentinel Tool " + temp + " started ..................................................................");
        
        // Get the init parameters and remember them.
        temp = servlet_.getInitParameter("jbossDataSource");
        if (temp == null)
        {
            ServletContext sc = servlet_.getServletContext();
            if (sc.getAttribute(DBAlert._DATASOURCE) != null)
                return;
            _dsTest = (DataSource) sc.getAttribute(DBAlert._DBPOOL + ".ds");
            _user = (String) sc.getAttribute(DBAlert._DBPOOL + ".user");
            _pswd = (String) sc.getAttribute(DBAlert._DBPOOL + ".pswd");
            sc.setAttribute(DBAlert._DATASOURCE, this);
            return;
        }

        _dataSource = "java:/" + servlet_.getInitParameter("jbossDataSource");
        _authenticate = "java:/" + servlet_.getInitParameter("jbossAuthenticate");
        _user = servlet_.getInitParameter(Constants._DSUSER);
        _pswd = servlet_.getInitParameter(Constants._DSPSWD);
        
        // Have to verify the context and datasource.
        Context envContext = null;
        try 
        {
            envContext = new InitialContext();
            DataSource ds = (DataSource)envContext.lookup(_dataSource);
            if (ds != null)
            {
                servlet_.getServletContext().setAttribute(DBAlert._DATASOURCE, this);
                _logger.info("Using JBoss datasource configuration. " + _dataSource);
            }
        }
        catch (Exception ex) 
        {
            String stErr = "Error retrieving datasource [" + _dataSource + "] from JBoss [" + ex.getMessage() + "].";
            _logger.error(stErr, ex);
        }
    }
    
    private DataSource getDataSource(String jndi_)
    {
        // The JUnit and Struts tests don't have JBoss so use the other datasource for connections.
        if (_dsTest != null)
            return _dsTest;

        // Get pool from Application Container, have to do this every time to avoid problems with the Connection Manager
        // being restarted and our pointer being stale.
        Context envContext = null;
        DataSource ds = null;
        try 
        {
            envContext = new InitialContext();
            ds = (DataSource)envContext.lookup(jndi_);
            if (ds == null)
            {
                if (_printFirstRequest)
                {
                    _printFirstRequest = false;
                    _logger.error("Failed context lookup for DataSource. " + jndi_);
                }
            }
        }
        catch (Exception ex) 
        {
            if (_printFirstRequest)
            {
                _printFirstRequest = false;
                String stErr = "Error retrieving datasource [" + jndi_ + "] from JBoss [" + ex.getMessage() + "].";
                _logger.error(stErr, ex);
            }
            ds = null;
        }
        
        return ds;
    }

    /**
     * Get the Servlet datasource for normal access.
     * 
     * @return the datasource.
     */
    public DataSource getDataSource()
    {
        return getDataSource(_dataSource);
    }

    /**
     * Get the Servlet datasource for authenticating the login credentials.
     * 
     * @return the datasource.
     */
    public DataSource getAuthenticate()
    {
        return getDataSource(_authenticate);
    }
    
    /**
     * Get the application default user.
     * 
     * @return the user id.
     */
    public String getUser()
    {
        return _user;
    }
    
    /**
     * Get the application default user password.
     * 
     * @return the password.
     */
    public String getPswd()
    {
        return _pswd;
    }
}
