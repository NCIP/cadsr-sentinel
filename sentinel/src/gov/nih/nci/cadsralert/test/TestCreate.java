// Copyright (c) 2004 ScenPro, Inc

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsralert/test/TestCreate.java,v 1.3 2006-05-17 20:17:02 hardingr Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsralert.test;

import gov.nih.nci.cadsralert.AlertBean;
import gov.nih.nci.cadsralert.AlertRec;
import gov.nih.nci.cadsralert.Constants;
import gov.nih.nci.cadsralert.CreateForm;
import gov.nih.nci.cadsralert.DBAlert;

/**
 * Test the Alert Create logic.
 * 
 * @author James McAndrew
 */
public class TestCreate extends DSRAlertTestCase
{
  /**
   * The main entry to run the test case.
   * 
   * @param args Command line arguments - none at this time.
   */
  public static void main(String[] args)
  {
    junit.textui.TestRunner.run(TestCreate.class);
  }

  private AlertBean _alertBean;

  /**
   * Constructor
   * 
   * @param testName The name of the class to test.
   */
  public TestCreate(String testName)
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

    setRequestPathInfo(getPath(Constants._ACTCREATE));
    setActionForm(new CreateForm());
    _alertBean = (AlertBean)getSession().getAttribute(AlertBean._SESSIONNAME);
    _alertBean.setWorking(new AlertRec(_alertBean.getUser(), _alertBean
        .getUserName()));
  }

  /**
   * The testSave method is the test for selecting the save option.
   */
  public void testSave()
  {
    // Setup a valid save request
    addRequestParameter("propName", "struts_test_temp_alert");

    // Then perform the action
    actionPerform();

    // Verify there were no errors
    verifyForward(Constants._ACTLIST);
    verifyNoActionErrors();

    // Verify that the alert got added to the database
    DBAlert dbAlert = new DBAlert();
    assertEquals(dbAlert.open(getSession().getServletContext(), _alertBean
        .getUser(), _alertBean.getPswd()), 0);
    assertNotNull(dbAlert.selectAlert(_alertBean.getWorking().getAlertRecNum()));

    // Delete the record (clean up)
    assertEquals(dbAlert.deleteAlert(_alertBean.getWorking().getAlertRecNum()), 0);

    assertEquals(dbAlert.close(), 0);
  }

  /**
   * The testSave method is the test for selecting the save option.
   */
  public void testEdit()
  {
    // Set the next screen to be the edit page
    ((CreateForm)getActionForm()).setNextScreen(Constants._ACTEDIT);

    // Then perform the action
    actionPerform();

    // Verify there were no errors
    verifyForward(Constants._ACTEDIT);
    verifyNoActionErrors();

    //  Verify that the alert did not get added to the database
    assertNull(_alertBean.getWorking().getAlertRecNum());
  }

  /**
   * The testBack method is the test for selecting the back option.
   */
  public void testBack()
  {
    // Set the next screen to be the list page
    ((CreateForm)getActionForm()).setNextScreen(Constants._ACTLIST);

    // Then perform the action
    actionPerform();

    // Verify there were no errors and the alert is not in the database
    verifyForward(Constants._ACTLIST);
    verifyNoActionErrors();

    //  Verify that the alert did not get added to the database
    assertNull(_alertBean.getWorking().getAlertRecNum());
  }
}