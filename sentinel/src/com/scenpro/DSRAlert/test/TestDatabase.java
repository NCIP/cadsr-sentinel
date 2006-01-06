// Copyright (c) 2004 ScenPro, Inc

// $Header: /share/content/gforge/sentinel/sentinel/src/com/scenpro/DSRAlert/test/TestDatabase.java,v 1.3 2006-01-06 16:14:26 hebell Exp $
// $Name: not supported by cvs2svn $

package com.scenpro.DSRAlert.test;

import com.scenpro.DSRAlert.AlertBean;
import com.scenpro.DSRAlert.DBAlert;

/**
 * Database verification test.
 * 
 * @author Larry Hebel
 */

public class TestDatabase extends DSRAlertTestCase
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
    public TestDatabase(String testName)
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
        _alertBean = (AlertBean)getSession().getAttribute(AlertBean._SESSIONNAME);
    }
    
    /**
     * Test the database connection and table dependencies.
     */
    public void testDB()
    {
        DBAlert dbAlert = new DBAlert();
        assertEquals(dbAlert.open(getSession().getServletContext(), _alertBean
            .getUser(), _alertBean.getPswd()), 0);
        String txt = dbAlert.testDBdependancies();
        assertNull(txt, txt);
        dbAlert.close();
    }
}
