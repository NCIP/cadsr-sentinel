/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

// Copyright (c) 2004 ScenPro, Inc

// $Header: /CVSNT/sentinel/src/gov/nih/nci/cadsr/sentinel/test/TestEdit.java,v
// 1.1 2006/01/31 17:05:52 lhebel Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.test;

import gov.nih.nci.cadsr.sentinel.database.DBAlert;
import gov.nih.nci.cadsr.sentinel.database.DBAlertUtil;
import gov.nih.nci.cadsr.sentinel.tool.AlertRec;
import gov.nih.nci.cadsr.sentinel.tool.Constants;
import gov.nih.nci.cadsr.sentinel.ui.AlertBean;
import gov.nih.nci.cadsr.sentinel.ui.EditForm;

/**
 * Test the Alert Edit logic.
 * 
 * @author James McAndrew
 */
public class TestEdit extends DSRAlertTestCase
{
    /**
     * The main entry to run the test case.
     * 
     * @param args
     *        Command line arguments - none at this time.
     */
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(TestEdit.class);
    }

    private AlertBean _alertBean;

    /**
     * Constructor
     * 
     * @param testName
     *        The name of the class to test.
     */
    public TestEdit(String testName)
    {
        super(testName);
    }

    /**
     * The setUp method overrides the DSRAlertTestCase setUp to setup the
     * request path info and action form.
     * 
     * @throws Exception
     *         Any exceptions encountered during setup
     */
    public void setUp() throws Exception
    {
        super.setUp();

        setUpLoginSession();

        setRequestPathInfo(getPath(Constants._ACTEDIT));
        setActionForm(new EditForm());
        _alertBean = (AlertBean) getSession().getAttribute(
            AlertBean._SESSIONNAME);
        _alertBean.setWorking(new AlertRec(_alertBean.getUser(), _alertBean
            .getUserName()));
    }

    /**
     * The testSave method is the test for selecting the save option.
     */
    public void testSave()
    {
        // Create the alert to edit
        AlertRec alertRec = _alertBean.getWorking();
        alertRec.setName("struts_test_temp_alert");
        DBAlert dbAlert = DBAlertUtil.factory();
        assertEquals(dbAlert.open(getSession().getServletContext(), _alertBean
            .getUser()), 0);
        assertEquals(dbAlert.insertAlert(alertRec), 0);
        assertEquals(dbAlert.close(), 0);

        // Setup a valid save request
        EditForm _editForm = (EditForm) getActionForm();
        _editForm.setNextScreen(Constants._ACTSAVE);

        _editForm.setPropName("struts_test_temp_alert_edit");
        _editForm.setPropDesc(alertRec.getSummary(true));
        _editForm.setFreqUnit("D");
        _editForm.setFreqWeekly("1");
        _editForm.setFreqMonthly("1");
        _editForm.setPropStatus("I");
        _editForm.setPropBeginDate("01/01/2005");
        _editForm.setPropEndDate("01/01/2005");
        _editForm.setPropStatusReason("Testing");
        _editForm.setInfoVerNum("");
        _editForm.setActVerNum("");
        _editForm.setPropRecipients(alertRec.getRecipients());
        _editForm.setInfoSearchIn("");
        _editForm.setInfoAssocLvl("0");
        _editForm.setInfoDateFilter("2");

        // Then perform the action
        actionPerform();

        assertEquals(dbAlert.open(getSession().getServletContext(), _alertBean
            .getUser()), 0);
        // Verify that the alert got changed in the database
        assertEquals(dbAlert.selectAlert(alertRec.getAlertRecNum()).getName(),
            "struts_test_temp_alert_edit");

        // Delete the record (clean up)
        assertEquals(dbAlert.deleteAlert(alertRec.getAlertRecNum()), 0);

        assertEquals(dbAlert.close(), 0);

        // Verify there were no errors
        verifyForward(Constants._ACTLIST);
        verifyNoActionErrors();
    }

    /**
     * The testClear method is the test for selecting the clear option.
     */
    public void testClear()
    {
        // Set the next screen to be the list page
        ((EditForm) getActionForm()).setNextScreen(Constants._ACTEDIT);

        // Then perform the action
        actionPerform();

        // Verify there were no errors
        verifyForward(Constants._ACTEDIT);
        verifyNoActionErrors();
    }

    /**
     * The testBack method is the test for selecting the back option.
     */
    public void testBack()
    {
        // Set the next screen to be the list page
        ((EditForm) getActionForm()).setNextScreen(Constants._ACTLIST);

        // Then perform the action
        actionPerform();

        // Verify there were no errors and the alert is not in the database
        verifyForward(Constants._ACTLIST);
        verifyNoActionErrors();
    }
}
