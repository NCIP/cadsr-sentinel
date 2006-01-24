// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsralert/LogonForm.java,v 1.1 2006-01-24 16:54:17 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsralert;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.MessageResources;

/**
 * The form bean for the logon.jsp.
 * 
 * @author Larry Hebel
 */

public class LogonForm extends ActionForm
{
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
        ActionErrors errors = new ActionErrors();

        if (_userid.length() > 0)
        {
            // Verify the guest account is not being used.
            int msgnum = initialize(_userid, request_, null);
            if (msgnum == -1)
            {
                errors.add("logon", new ActionMessage("error.logon.guest"));
                return errors;
            }

            ActionMessage am;
            if (msgnum != 0)
            {
                // We had a problem.
                am = new ActionMessage("DB." + msgnum);
                if (am == null)
                    am = new ActionMessage("DB.prob");
                errors.add("logon", am);
            }
            else
            {
                // Verify user credentials.
                DBAlert db = new DBAlert();
                _userid = _userid.toUpperCase();
                msgnum = db.open(request_, _userid, _pswd);
                if (msgnum == 0)
                {
                    // Test database dependencies.
                    String msg = db.testDBoptions(); 
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
        }
        else
        {
            // Gotta enter something, don't like blank names.
            errors.add("logon", new ActionMessage("error.logon.blankuser"));
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
        MessageResources msgs = (MessageResources) request_.getSession()
            .getServletContext().getAttribute(Constants._RESOURCES);
        String tnsname = msgs.getMessage(Constants._DBTNSNAME);
        String username = msgs.getMessage(Constants._DBUSER);
        String password = msgs.getMessage(Constants._DBPSWD);
        
        if (accnt_ != null)
        {
            accnt_[0] = username;
            accnt_[1] = password;
        }

        // Setup the database pool.
        int msgnum = DBAlert.setupPool(request_, tnsname, username,
            password);

        return msgnum;
    }

    // Class data.
    private String _userid;

    private String _pswd;

    private String _userName;

    private static final long serialVersionUID = -6923074595312333195L;
}