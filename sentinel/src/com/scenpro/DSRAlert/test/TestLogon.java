/*
 * Created on Jan 20, 2005
 *
 * Copyright (c) 2004 ScenPro, Inc.
 */
package com.scenpro.DSRAlert.test;

import com.scenpro.DSRAlert.AlertBean;
import com.scenpro.DSRAlert.Constants;
import com.scenpro.DSRAlert.LogonForm;

/**
 * @author James McAndrew
 */
public class TestLogon extends DSRAlertTestCase
{
  public static void main(String[] args)
  {
    junit.textui.TestRunner.run(TestLogon.class);
  }

  /**
   * Constructor
   */
  public TestLogon(String testName)
  {
    super(testName);
  }
  
  /**
   * The setUp method overrides the DSRAlertTestCase setUp to setup the
   * request path info and action form.
   * 
   * @throws Exception Any exceptions encountered during setup
   */
  public void setUp() throws Exception
  {
    super.setUp();

    setRequestPathInfo(getPath(Constants._ACTLOGON));
    setActionForm(new LogonForm());
  }

  /**
   * The testSuccessfulLogin method is the test for a valid login.
   */
  public void testSuccessfulLogin()
  {
    // Setup a valid logon request
    addRequestParameter("userid", _validUserid);
    addRequestParameter("pswd", _validPswd);

    // Then perform the action
    actionPerform();

    // Verify there were no errors and we got to the launcher page
    verifyForward(Constants._ACTLIST);
    verifyNoActionErrors();

    // Verify there is a user bean in the session
    assertNotNull(getSession().getAttribute(AlertBean._SESSIONNAME));
  }

  /**
   * The testInvalidLogin method is the test for an invalid login.
   */
  public void testInvalidLogin()
  {
    // Setup an invalid logon request
    addRequestParameter("userid", _invalidUserid);
    addRequestParameter("pswd", _invalidPswd);

    // Then perform the action
    actionPerform();

    // Verify there was an invalid password error and we got to the logon page
    verifyActionErrors(new String[] { "DB.1017" });
  }

  /**
   * The testBlankLogin method is the test for a blank login.
   */
  public void testBlankLogin()
  {
    // Setup an invalid logon request
    addRequestParameter("userid", "");
    addRequestParameter("pswd", "");

    // Then perform the action
    actionPerform();

    // Verify there was an blank password error and we got to the logon page
    verifyActionErrors(new String[] { "error.logon.blankuser" });
  }
}