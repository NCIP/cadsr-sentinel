/*
 * Created on Jan 25, 2005
 *
 * Copyright (c) 2004 ScenPro, Inc
 */
package com.scenpro.DSRAlert.test;

import com.scenpro.DSRAlert.AlertBean;
import com.scenpro.DSRAlert.AlertRec;
import com.scenpro.DSRAlert.Constants;
import com.scenpro.DSRAlert.DBAlert;
import com.scenpro.DSRAlert.ListForm;

/**
 * @author James McAndrew
 *
 */
public class TestList extends DSRAlertTestCase
{
  public static void main(String[] args)
  {
    junit.textui.TestRunner.run(TestList.class);
  }

  /**
   * Constructor
   */
  public TestList(String testName)
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

    setUpLoginSession();

    setRequestPathInfo(getPath(Constants._ACTLIST));
    setActionForm(new ListForm());
  }

  /**
   * The testShowFilter method is the test for selecting the show all option
   * and show private option.
   */
  public void testShowFilter()
  {
    // Setup a valid show all request
    addRequestParameter("listShow", String.valueOf(AlertBean._SHOWALL));
    addRequestParameter("rowCount", "0");
    addRequestParameter("nextScreen", Constants._ACTLIST);

    // Then perform the action
    actionPerform();

    // Verify there were no errors
    verifyForward(Constants._ACTLIST);
    verifyNoActionErrors();

    // Setup a valid show private request
    addRequestParameter("listShow", String.valueOf(AlertBean._SHOWPRIV));
    addRequestParameter("rowCount", "0");
    addRequestParameter("nextScreen", Constants._ACTLIST);

    // Then perform the action
    actionPerform();

    // Verify there were no errors
    verifyForward(Constants._ACTLIST);
    verifyNoActionErrors();
  }

  /**
   * The testNavigation method is the test for choosing a navigation option.
   */
  public void testNavigation()
  {
    // Setup a valid create request
    addRequestParameter("rowCount", "0");
    addRequestParameter("nextScreen", Constants._ACTCREATE);

    // Then perform the action
    actionPerform();

    // Verify there were no errors
    verifyForward(Constants._ACTCREATE);
    verifyNoActionErrors();

    // Setup a valid edit request
    addRequestParameter("nextScreen", Constants._ACTEDIT);

    // Then perform the action
    actionPerform();

    // Verify there were no errors
    verifyForward(Constants._ACTEDIT);
    verifyNoActionErrors();

    // Setup a valid edit request
    addRequestParameter("nextScreen", Constants._ACTNEWFROM);

    // Then perform the action
    actionPerform();

    // Verify there were no errors
    verifyForward(Constants._ACTNEWFROM);
    verifyNoActionErrors();

    // Setup a valid edit request
    addRequestParameter("nextScreen", Constants._ACTDELETE);

    // Then perform the action
    actionPerform();

    // Verify there were no errors
    verifyForward(Constants._ACTLIST);
    verifyNoActionErrors();

    // Setup a valid edit request
    addRequestParameter("nextScreen", Constants._ACTRUN);

    // Then perform the action
    actionPerform();

    // Verify there were no errors
    verifyForward(Constants._ACTRUN);
    verifyNoActionErrors();
  }

  /**
   * The testDelete method is the test for choosing the delete option.
   */
  public void testDelete()
  {
    // Create the alert to delete
    AlertBean alertBean = (AlertBean)getSession().getAttribute(
        AlertBean._SESSIONNAME);
    AlertRec alertRec = new AlertRec(alertBean.getUser(), alertBean
        .getUserName());
    alertRec.setName("struts_test_temp_alert");
    DBAlert dbAlert = new DBAlert();
    assertEquals(dbAlert.open(getSession().getServletContext(), alertBean
        .getUser(), alertBean.getPswd()), 0);
    assertEquals(dbAlert.insertAlert(alertRec), 0);
    assertEquals(dbAlert.close(), 0);

    // Setup a valid delete request
    addRequestParameter("rowCount", "1");
    addRequestParameter("nextScreen", Constants._ACTDELETE);
    addRequestParameter("cb0", alertRec.getAlertRecNum());

    // Then perform the action
    actionPerform();

    // Verify there were no errors
    verifyForward(Constants._ACTLIST);
    verifyNoActionErrors();
  }

  /**
   * The testLogout method is the test for choosing the logout option.
   */
  public void testLogout()
  {
    // Setup a valid logout request
    setRequestPathInfo(getPath(Constants._ACTLOGOUT));

    // Then perform the action
    actionPerform();

    // Verify there were no errors
    verifyForward(Constants._ACTLOGON);
    verifyNoActionErrors();
  }
}