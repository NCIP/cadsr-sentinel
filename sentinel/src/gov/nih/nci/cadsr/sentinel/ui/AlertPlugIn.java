// Copyright (c) 2006 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/AlertPlugIn.java,v 1.1 2006-09-08 22:32:55 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

import gov.nih.nci.cadsr.sentinel.database.DBAlert;
import gov.nih.nci.cadsr.sentinel.tool.Constants;
import javax.naming.Context;
import javax.naming.InitialContext;
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

    public void destroy()
    {
        _logger.info(" ");
        _logger.info("Sentinel Tool stopped ..................................................................");
    }

    public void init(ActionServlet arg0, ModuleConfig arg1) throws ServletException
    {
        _logger.info(" ");
        _logger.info("Sentinel Tool started ..................................................................");

        // Get the init parameters and remember them.
        String stDataSource = arg0.getInitParameter("jbossDataSource");
        _user = arg0.getInitParameter(Constants._DSUSER);
        _pswd = arg0.getInitParameter(Constants._DSPSWD);
 
        // Create database pool
        Context envContext = null;
        try 
        {
            envContext = new InitialContext();
            _ds = (DataSource)envContext.lookup("java:/" + stDataSource);
            if (_ds != null)
            {
                // Only set the context attribute if we can successfully retrieve the datasource
                // from JBoss.
                arg0.getServletContext().setAttribute(DBAlert._DATASOURCE, this);
                _logger.info("Using JBoss datasource configuration.");
            }
        }
        catch (Exception ex) 
        {
            String stErr = "Error retrieving datasource from JBoss [" + ex.getMessage() + "].";
            _logger.fatal(stErr, ex);
        }
    }

    /**
     * Get the Servlet datasource.
     * 
     * @return the datasource.
     */
    public DataSource getDataSource()
    {
        return _ds;
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
    
    private DataSource _ds;
    private String _user;
    private String _pswd;

    private static final Logger _logger = Logger.getLogger(AlertPlugIn.class.getName());
}
