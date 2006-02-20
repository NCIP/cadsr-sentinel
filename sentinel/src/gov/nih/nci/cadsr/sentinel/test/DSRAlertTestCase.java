// Copyright (c) 2004 ScenPro, Inc

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/test/DSRAlertTestCase.java,v 1.9 2006-02-20 20:59:33 hardingr Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.test;

import gov.nih.nci.cadsr.sentinel.tool.AlertBean;
import gov.nih.nci.cadsr.sentinel.tool.Constants;
import gov.nih.nci.cadsr.sentinel.tool.DBAlert;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.struts.util.MessageResources;
import org.apache.struts.util.MessageResourcesFactory;
import org.apache.struts.util.PropertyMessageResources;


import servletunit.struts.MockStrutsTestCase;

/**
 * This is the base test class which extends the Mock Struts Test Case class.
 * 
 * @author James McAndrew
 */
public class DSRAlertTestCase extends MockStrutsTestCase
{
  protected String _validUserid;
  protected String _validPswd;
  protected String _invalidUserid;
  protected String _invalidPswd;

  /**
   * Constructor.
   * 
   * @param testName The name of the class for the test.
   */
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
    ResourceBundle testProperties = PropertyResourceBundle.getBundle("gov.nih.nci.cadsr.sentinel.test.DSRAlertTest");
    _validUserid = testProperties.getString("valid.userid");
    _validPswd = testProperties.getString("valid.pswd");
    _invalidUserid = testProperties.getString("invalid.userid");
    _invalidPswd = testProperties.getString("invalid.pswd");

    // Setup the MessageResources object since we are only in a mock environment
    getSession().getServletContext().setAttribute(
        Constants._RESOURCES,
        new PropertyMessageResources(MessageResourcesFactory.createFactory(),
            "gov.nih.nci.cadsr.sentinel.DSRAlert"));
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
    String tnsname = msgs.getMessage(Constants._DBTNSNAME);
    String username = msgs.getMessage(Constants._DBUSER);
    String password = msgs.getMessage(Constants._DBPSWD);

    // Setup the database pool.
    DBAlert.setupPool(getSession(), tnsname, username, password);
    getSession().setAttribute(AlertBean._SESSIONNAME,
        new AlertBean(_validUserid, "", _validPswd));
  }

  protected String getPath(String page)
  {
    return ("/" + page);
  }
}