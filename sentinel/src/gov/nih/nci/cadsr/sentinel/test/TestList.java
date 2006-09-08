// Copyright (c) 2004 ScenPro, Inc

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/test/TestList.java,v 1.11 2006-09-08 22:32:54 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.test;

import gov.nih.nci.cadsr.sentinel.database.DBAlert;
import gov.nih.nci.cadsr.sentinel.database.DBAlertUtil;
import gov.nih.nci.cadsr.sentinel.tool.AlertRec;
import gov.nih.nci.cadsr.sentinel.tool.Constants;
import gov.nih.nci.cadsr.sentinel.ui.AlertBean;
import gov.nih.nci.cadsr.sentinel.ui.ListForm;

/**
 * Test the Alert List logic.
 * 
 * @author James McAndrew
 */
public class TestList extends DSRAlertTestCase
{
    /**
     * The main entry to run the test case.
     * 
     * @param args Command line arguments - none at this time.
     */
  public static void main(String[] args)
  {
    junit.textui.TestRunner.run(TestList.class);
  }

  /**
   * Constructor
   * 
   * @param testName The name of the class to test.
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
    DBAlert dbAlert = DBAlertUtil.factory();
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