// Copyright (c) 2004 ScenPro, Inc.

package com.scenpro.DSRAlert;

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
        _user = user_;
        _userName = userName_;
        _pswd = pswd_;

        // Set List.jsp defaults.
        _listShow = 'p';
        //        _listSortCol = 1;

        // Generate bean key.
        Date today = new Date();
        long tkey = (today.getTime() * 10000) + (long) (Math.random() * 10000);
        _key = Long.toString(tkey);

        _lastUserTab = "0";
        _lastMainTab = "0";
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
     * @return
     */
    public String getUserName()
    {
        return _userName;
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
        return (_listShow == 'p');
    }

    /**
     * Set the flag indicating which list is visible to the user.
     * 
     * @param flag_
     *        'p' when the private list is shown, 'a' when all alerts are shown.
     */
    public void setListShow(char flag_)
    {
        _listShow = (flag_ == 'p') ? 'p' : 'a';
    }

    /**
     * Set the flag indicating which list is visible to the user.
     * 
     * @param flag_
     *        "p" when the private list is shown, "a" when all alerts are shown.
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
     * Set the database list of id's for the Alerts shown.
     * 
     * @param list_
     *        The database id's for visible Alerts.
     */
    public void setDBlist(String list_[])
    {
        _listDBid = list_;
    }

    /**
     * Get the database id for the Alert item specified.
     * 
     * @param ndx_
     *        The desired Alert item.
     * @return The database id for the Alert.
     */
    public String getDBlist(int ndx_)
    {
        return _listDBid[ndx_];
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
     * The session name for the bean. Defined to ensure consistency and avoid
     * spelling errors.
     */
    public static final String _SESSIONNAME = "DSRAlertBean";

    /*
     * Class data.
     */
    private String             _user;

    private String             _pswd;

    private String             _userName;

    private char               _listShow;

    //    private int _listSortCol;
    private String             _key;

    private String             _listDBid[];

    private AlertRec           _working;

    private String             _editPrev;

    private String             _runPrev;

    private String             _lastUserTab;

    private String             _lastMainTab;
}