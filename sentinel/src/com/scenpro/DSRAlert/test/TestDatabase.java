/*
 * Copyright (c) Feb 17, 2005 ScenPro, Inc.
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.scenpro.DSRAlert.test;

import com.scenpro.DSRAlert.AlertBean;
import com.scenpro.DSRAlert.DBAlert;

/**
 * @author Larry Hebel
 */

/**
 * @author lhebel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestDatabase extends DSRAlertTestCase
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(TestCreate.class);
    }

    private AlertBean _alertBean;

    /**
     * Constructor
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
