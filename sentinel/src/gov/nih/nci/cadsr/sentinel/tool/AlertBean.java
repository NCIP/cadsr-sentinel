// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tool/AlertBean.java,v 1.10 2006-05-17 20:17:01 hardingr Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tool;

import java.util.Date;

/**
 * The session persistant data.
 * 
 * @author Larry Hebel
 */

public class AlertBean
{
    /**
     * Constructor.
     * 
     * @param user_
     *        The user id.
     * @param userName_
     *        The user name.
     * @param pswd_
     *        The user password.
     */
    public AlertBean(String user_, String userName_, String pswd_)
    {
        init(null, user_, userName_, pswd_);
    }
    
    /**
     * Constructor for a proxy.
     * 
     * @param proxy_ The proxy user id.
     * @param user_ The user id.
     * @param userName_ The user name.
     * @param pswd_ The user password.
     */
    public AlertBean(String proxy_, String user_, String userName_, String pswd_)
    {
        init(proxy_, user_, userName_, pswd_);
    }
    
    private void init(String proxy_, String user_, String userName_, String pswd_)
    {
        _user = user_;
        _proxyUser = proxy_;
        _userName = userName_;
        _pswd = pswd_;
        _admin = false;

        // Set List.jsp defaults.
        _listShow = _SHOWPRIV;
        //        _listSortCol = 1;

        // Generate bean key.
        Date today = new Date();
        long tkey = (today.getTime() * 10000) + (long) (Math.random() * 10000);
        _key = Long.toString(tkey);

        _lastUserTab = "0";
        _lastMainTab = "0";

        _editPrev = Constants._ACTLIST;
        _runPrev = Constants._ACTLIST;
    }

    /**
     * Get the proxy user id.
     * 
     * @return The proxy user id.
     */
    public String getProxy()
    {
        return _proxyUser;
    }
    
    /**
     * Set the proxy user id.
     * 
     * @param val_ The proxy user id.
     */
    public void setProxy(String val_)
    {
        _proxyUser = val_;
    }
    
    /**
     * Return the user id.
     * 
     * @return The user id.
     */
    public String getUser()
    {
        return _user;
    }

    /**
     * Return the user id in all uppercase.
     * 
     * @return The user id.
     */
    public String getUserUpper()
    {
        return _user.toUpperCase();
    }

    /**
     * Return the user password.
     * 
     * @return The user Password.
     */
    public String getPswd()
    {
        return _pswd;
    }

    /**
     * Return the user name.
     * 
     * @return TBD
     */
    public String getUserName()
    {
        return _userName;
    }
    
    /**
     * Set the user name.
     * 
     * @param val_ The user name.
     */
    public void setUserName(String val_)
    {
        _userName = val_;
    }

    /**
     * Return the List flag. This indicates the list of Alerts last visible, the
     * private list or a full list.
     * 
     * @return The list flag.
     */
    public char getListShow()
    {
        return _listShow;
    }

    /**
     * Return true if the list is limited to the user only.
     * 
     * @return true if a private list, otherwise false.
     */
    public boolean isListShowPrivate()
    {
        return (_listShow == _SHOWPRIV);
    }

    /**
     * Set the flag indicating which list is visible to the user.
     * 
     * @param flag_
     *        _SHOWPRIV when the private list is shown, _SHOWALL when all alerts are shown.
     */
    public void setListShow(char flag_)
    {
        _listShow = (flag_ == _SHOWPRIV) ? _SHOWPRIV : _SHOWALL;
    }

    /**
     * Set the flag indicating which list is visible to the user.
     * 
     * @param flag_
     *        _SHOWPRIV when the private list is shown, _SHOWALL when all alerts are shown.
     */
    public void setListShow(String flag_)
    {
        setListShow(flag_.charAt(0));
    }

    /**
     * Return the logon generated key for this session bean.
     * 
     * @return The logon generated session key.
     */
    public String getKey()
    {
        return _key;
    }

    /**
     * Get the last tab visible on the Edit, Report Details, User list field.
     * 
     * @return The last visible tab page id.
     */
    public String getLastUserTab()
    {
        return _lastUserTab;
    }

    /**
     * Get the last tab page visible on the Edit screen.
     * 
     * @return The last visible tab page id.
     */
    public String getLastMainTab()
    {
        return _lastMainTab;
    }

    /**
     * Set the action/screen which transfers control to the Edit screen.
     * 
     * @param prev_
     *        The identifier of the prior action/screen.
     */
    public void setEditPrev(String prev_)
    {
        _editPrev = prev_;
    }

    /**
     * Get the action/screen which launched the Edit screen.
     * 
     * @return The previous action/screen.
     */
    public String getEditPrev()
    {
        return _editPrev;
    }

    /**
     * Set the action/screen which transfers control to the Run screen.
     * 
     * @param prev_
     *        The identifier of the prior action/screen.
     */
    public void setRunPrev(String prev_)
    {
        _runPrev = prev_;
    }

    /**
     * Get the action/screen which launched the Run screen.
     * 
     * @return The previoius action/screen.
     */
    public String getRunPrev()
    {
        return _runPrev;
    }

    /**
     * Get the working Alert record.
     * 
     * @return The working Alert record.
     */
    public AlertRec getWorking()
    {
        return _working;
    }

    /**
     * Set the working Alert record.
     * 
     * @param rec_
     *        The record to set in the working buffer.
     */
    public void setWorking(AlertRec rec_)
    {
        _working = rec_;
    }
    
    /**
     * Test for the user's administration privileges.
     * 
     * @return true if a tool administrator, otherwise false.
     */
    public boolean isAdmin()
    {
        return _admin;
    }
    
    /**
     * Set the user's administration privileges.
     * 
     * @param flag_ true if the user is granted privileges, otherwise false.
     */
    public void setAdmin(boolean flag_)
    {
        _admin = flag_;
    }

    /**
     * The session name for the bean. Defined to ensure consistency and avoid
     * spelling errors.
     */
    public static final String _SESSIONNAME = "DSRAlertBean";

    /*
     * Class data.
     */
    private String             _proxyUser;
    
    private String             _user;

    private String             _pswd;

    private String             _userName;
    
    private boolean            _admin;

    private char               _listShow;

    //    private int _listSortCol;
    private String             _key;

    private AlertRec           _working;

    private String             _editPrev;

    private String             _runPrev;

    private String             _lastUserTab;

    private String             _lastMainTab;

    /**
     * Value to display the Show All button on the List page.
     */
    public static final char _SHOWALL = 'a';
    
    /**
     * Value to display the Show Private button on the List page.
     */
    public static final char _SHOWPRIV = 'p';
}