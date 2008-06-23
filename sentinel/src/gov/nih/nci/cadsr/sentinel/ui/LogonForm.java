// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/LogonForm.java,v 1.8 2008-06-23 12:05:12 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

import gov.nih.nci.cadsr.sentinel.database.CaDsrUserCredentials;
import gov.nih.nci.cadsr.sentinel.database.DBAlert;
import gov.nih.nci.cadsr.sentinel.database.DBAlertUtil;
import gov.nih.nci.cadsr.sentinel.test.DSproperties;
import gov.nih.nci.cadsr.sentinel.tool.Constants;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.MessageResources;
import org.apache.struts.Globals;

/**
 * The form bean for the logon.jsp.
 * 
 * @author Larry Hebel
 */

public class LogonForm extends ActionForm
{

    // Class data.
    private String _userid;

    private String _pswd;

    private String _userName;

    private static final long serialVersionUID = -6923074595312333195L;

    private static final Logger _logger = Logger.getLogger(LogonForm.class);
    
    /**
     * Constructor.
     */
    public LogonForm()
    {
        _userid = "";
        _pswd = "";
    }

    /**
     * Get the user login id.
     * 
     * @return The user id.
     */
    public String getUserid()
    {
        return _userid;
    }

    /**
     * Get the user password.
     * 
     * @return The password.
     */
    public String getPswd()
    {
        return _pswd;
    }

    /**
     * Set the user login id.
     * 
     * @param userid_
     *        The user id.
     */
    public void setUserid(String userid_)
    {
        _userid = userid_;
    }

    /**
     * Set the user password.
     * 
     * @param pswd_
     *        The password.
     */
    public void setPswd(String pswd_)
    {
        _pswd = pswd_;
    }

    /**
     * Get the user name.
     * 
     * @return The user name.
     */
    public String getUserName()
    {
        return _userName;
    }

    /**
     * Validate the Login action
     * 
     * @param mapping_
     *        The action map defined in struts-config.xml.
     * @param request_
     *        The HTTP request object.
     * @return Any errors that occur.
     */
    public ActionErrors validate(ActionMapping mapping_,
        HttpServletRequest request_)
    {
        /*
        Enumeration attrs = request_.getAttributeNames();
        _logger.info("LogonForm.validate()");
        while (attrs.hasMoreElements())
        {
            String name = (String) attrs.nextElement();
            _logger.info("Attribute: " + name);
        }
        */
        
        ActionErrors errors = new ActionErrors();

        try
        {
            // A session can not already be in progress during a logon request.
            HttpSession session = request_.getSession();
            ServletContext sc = session.getServletContext();
            AlertPlugIn api = (AlertPlugIn) sc.getAttribute(DBAlert._DATASOURCE);
            api.setHelpUrl(null);
            
            AlertBean ub = (AlertBean) session.getAttribute(AlertBean._SESSIONNAME);
            if (ub != null)
            {
                errors.add("logon", new ActionMessage("error.sessionInProgress"));
                throw new Exception("error.sessionInProgress");
            }

            if (_userid.length() < 1)
            {
                // Gotta enter something, don't like blank names.
                errors.add("logon", new ActionMessage("error.logon.blankuser"));
                throw new Exception("error.logon.blankuser");
            }

            _userid = _userid.toUpperCase();
            if (_pswd == null)
            {
                // Do not allow a blank password.
                errors.add("logon", new ActionMessage("error.logon.blankuser"));
                throw new Exception("error.logon.blankuser");
            }
            
            // Verify the guest account is not being used.
            String[] credentials = new String[2];
            int msgnum = initialize(_userid, request_, credentials);
            if (msgnum == -1)
            {
                errors.add("logon", new ActionMessage("error.logon.guest"));
                throw new Exception("error.logon.guest");
            }

            ActionMessage am;
            if (msgnum != 0)
            {
                // We had a problem.
                String msgProp = "DB." + msgnum;
                am = new ActionMessage(msgProp);
                if (am == null)
                {
                    msgProp = "DB.prob";
                    am = new ActionMessage(msgProp);
                }
                errors.add("logon", am);
                throw new Exception(msgProp);
            }

            // Verify user credentials.
            CaDsrUserCredentials uc = new CaDsrUserCredentials();
            try
            {
                uc.validateCredentials(credentials[0], credentials[1], _userid, _pswd);
            }
            catch (Exception ex)
            {
                _logger.equals("Failed credential validation, code is " + uc.getCheckCode());
                errors.add("logon", new ActionMessage("DB.1017"));
                throw new Exception("DB.1017");
            }
                
            DBAlert db = DBAlertUtil.factory();
            msgnum = db.open(request_, _userid, _pswd);
            if (msgnum == 0)
            {
                // Test database dependencies.
                String cp = request_.getContextPath();
                String reqURL = null;
                if (cp != null && cp.length() > 0)
                    reqURL = request_.getRequestURL().toString();
                String msg = db.testSentinelOptions(reqURL); 
                if (msg != null)
                {
                    am = new ActionMessage("error.logon.baddb", msg);
                    errors.add("logon", am);
                }
                else
                {
                    // It's good.
                    _userName = db.selectUserName(_userid);
                    if (_userName == null || _userName.length() == 0)
                        errors.add("logon", new ActionMessage(
                            "error.logon.blankname"));
                }
            }
            else
            {
                // Credentials or something isn't right.
                am = new ActionMessage("DB." + msgnum);
                if (am == null)
                    am = new ActionMessage("error.logon.baduser");
                errors.add("logon", am);
            }
            db.close();
        }
        catch (Exception ex)
        {
            // Clear the user and password.
            _userid = null;
            _pswd = null;
        }

        return errors;
    }
    
    /**
     * Initialize the database pool.
     * 
     * @param userid_ The user id to establish the pool.
     * @param request_ The HTTP request.
     * @param accnt_ The account information returned from the properties file.
     * @return The database error code, 0 is successful.
     */
    public static int initialize(String userid_, HttpServletRequest request_, String accnt_[])
    {
        // Verify the guest account is not being used.
        if (userid_.compareToIgnoreCase("guest") == 0)
            return -1;
        
        // Get the default information needed to connect to the database.
        // This requires
        // an entry in the TNSNAMES.ORA file. If problems occur, first
        // verify the database
        // is accessible using the same information through SQL Plus.
        ServletContext sc = request_.getSession().getServletContext();
        
        int msgnum = 0;
        AlertPlugIn var = (AlertPlugIn) sc.getAttribute(DBAlert._DATASOURCE);
        if (var == null)
        {
            DSproperties p2 = (DSproperties) sc.getAttribute(Constants._DSTESTPROP);
            String dsurl;
            String username;
            String password;
            if (p2 != null)
            {
                dsurl = p2._dsurl;
                username = p2._dsusername;
                password = p2._dspassword;
            }
            else
            {
                MessageResources msgs = (MessageResources) sc.getAttribute(Globals.MESSAGES_KEY);
                dsurl = msgs.getMessage(Constants._DSURL);
                username = msgs.getMessage(Constants._DSUSER);
                password = msgs.getMessage(Constants._DSPSWD);
            }

            // Setup the database pool.
            DBAlert db = DBAlertUtil.factory();
            msgnum = db.setupPool(request_, dsurl, username, password);
            if (accnt_ != null)
            {
                accnt_[0] = username;
                accnt_[1] = password;
            }
        }
        else if (accnt_ != null)
        {
            accnt_[0] = var.getUser();
            accnt_[1] = var.getPswd();
        }

        return msgnum;
    }
}