/*
 * Created on Jan 20, 2005
 *
 * Copyright (c) 2004 ScenPro, Inc
 */
package com.scenpro.DSRAlert.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.MessageResourcesFactory;
import org.apache.struts.util.PropertyMessageResources;

import com.scenpro.DSRAlert.AlertBean;
import com.scenpro.DSRAlert.Constants;
import com.scenpro.DSRAlert.DBAlert;

import servletunit.struts.MockStrutsTestCase;

/**
 * @author James McAndrew
 *
 */
public class DSRAlertTestCase extends MockStrutsTestCase
{
  protected String _validUserid;
  protected String _validPswd;
  protected String _invalidUserid;
  protected String _invalidPswd;

  public DSRAlertTestCase(String testName)
  {
    super(testName);
  }

  /**
   * The setUp method overrides the MockStrutsTestCase setUp to setup the
   * datasource.
   * 
   * @throws Exception Any exceptions encountered during setup
   */
  public void setUp() throws Exception
  {
    super.setUp();

    // Grab the useris/password combos from the properties file
    Properties testProperties = new Properties();
    testProperties.load(new FileInputStream(new File(System
        .getProperty("test.propfile"))));
    _validUserid = testProperties.getProperty("valid.userid", "pfowler");
    _validPswd = testProperties.getProperty("valid.pswd", "pfowler");
    _invalidUserid = testProperties.getProperty("invalid.userid", "invalid");
    _invalidPswd = testProperties.getProperty("invalid.pswd", "invalid");

    // Setup the MessageResources object since we are only in a mock environment
    getSession().getServletContext().setAttribute(
        Constants._RESOURCES,
        new PropertyMessageResources(MessageResourcesFactory.createFactory(),
            "com.scenpro.DSRAlert.DSRAlert"));
  }

  /**
   * The setUpLoginSession method sets up the datasource that is normally
   * created by the LogonForm and adds the alert bean to the session.
   */
  protected void setUpLoginSession()
  {
    // Get the default information needed to connect to the database.
    // This requires an entry in the TNSNAMES.ORA file. If problems
    // occur, first verify the database is accessible using the same
    //information through SQL Plus.
    MessageResources msgs = (MessageResources)getSession().getServletContext()
        .getAttribute(Constants._RESOURCES);
    String driver = msgs.getMessage(Constants._DBDRIVER);
    String tnsname = msgs.getMessage(Constants._DBTNSNAME);
    String username = msgs.getMessage(Constants._DBUSER);
    String password = msgs.getMessage(Constants._DBPSWD);

    // Setup the database pool.
    ActionMessage am;
    DBAlert.setupPool(getSession(), driver, tnsname, username, password);
    getSession().setAttribute(AlertBean._SESSIONNAME,
        new AlertBean(_validUserid, "", _validPswd));
  }

  protected String getPath(String page)
  {
    return ("/" + page);
  }
}