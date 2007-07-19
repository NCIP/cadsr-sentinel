// Copyright (c) 2004 ScenPro, Inc

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/test/TestDatabase.java,v 1.13 2007-07-19 15:26:45 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.test;

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
    }
    
    /**
     * Test the database connection and table dependencies.
     */
    public void testDB()
    {
//        DBAlert dbAlert = DBAlertUtil.factory();
//        assertEquals(dbAlert.open(getSession().getServletContext(), _alertBean
//            .getUser()), 0);
//        String txt = dbAlert.testDBdependancies();
//        assertNull(txt, txt);
//        dbAlert.close();
    }
}
