// Copyright (c) 2004 ScenPro, Inc
// $Header: /CVSNT/sentinel/src/gov/nih/nci/cadsr/sentinel/test/DSRAlertTestCase.java,v 1.4 2006/05/26 18:04:00 lhebel
// Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.test;

import gov.nih.nci.cadsr.sentinel.database.DBAlert;
import gov.nih.nci.cadsr.sentinel.database.DBAlertUtil;
import gov.nih.nci.cadsr.sentinel.tool.Constants;
import gov.nih.nci.cadsr.sentinel.ui.AlertBean;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.servlet.ServletContext;
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

    protected String _dsurl;

    protected String _dsuser;

    protected String _dspswd;

    /**
     * Constructor.
     * 
     * @param testName
     *            The name of the class for the test.
     */
    public DSRAlertTestCase(String testName)
    {
        super(testName);
    }

    /**
     * The setUp method overrides the MockStrutsTestCase setUp to setup the datasource.
     * 
     * @throws Exception
     *             Any exceptions encountered during setup
     */
    public void setUp() throws Exception
    {
        super.setUp();
        // Grab the useris/password combos from the properties file
        ResourceBundle testProperties = PropertyResourceBundle
                        .getBundle("gov.nih.nci.cadsr.sentinel.test.DSRAlertTest");
        _validUserid = testProperties.getString("valid.userid");
        _validPswd = testProperties.getString("valid.pswd");
        _invalidUserid = testProperties.getString("invalid.userid");
        _invalidPswd = testProperties.getString("invalid.pswd");
        // Setup the MessageResources object since we are only in a mock environment
        ServletContext sc = getSession().getServletContext();
        sc.setAttribute(Constants._RESOURCES, new PropertyMessageResources(MessageResourcesFactory.createFactory(),
                        "gov.nih.nci.cadsr.sentinel.DSRAlert"));
        try
        {
            String propfile = System.getProperty("property_xml");
            FileInputStream in = new FileInputStream(propfile);
            Properties prop = new Properties();
            prop.loadFromXML(in);
            in.close();
            DSproperties pp = new DSproperties();
            pp._dspassword = prop.getProperty(Constants._DSPSWD);
            pp._dsurl = prop.getProperty(Constants._DSURL);
            pp._dsusername = prop.getProperty(Constants._DSUSER);
            sc.setAttribute(Constants._DSTESTPROP, pp);
        }
        catch (FileNotFoundException ex)
        {
            System.out.println(ex.toString());
        }
        catch (InvalidPropertiesFormatException ex)
        {
            System.out.println(ex.toString());
        }
        catch (IOException ex)
        {
            System.out.println(ex.toString());
        }
    }

    /**
     * The setUpLoginSession method sets up the datasource that is normally created by the LogonForm and adds the alert
     * bean to the session.
     */
    protected void setUpLoginSession()
    {
        // Get the default information needed to connect to the database.
        // This requires an entry in the TNSNAMES.ORA file. If problems
        // occur, first verify the database is accessible using the same
        // information through SQL Plus.
        ServletContext sc = getSession().getServletContext();
        DSproperties pp = (DSproperties) sc.getAttribute(Constants._DSTESTPROP);
        // Setup the database pool.
        DBAlert db = DBAlertUtil.factory();
        db.setupPool(getSession(), pp._dsurl, pp._dsusername, pp._dspassword);
        getSession().setAttribute(AlertBean._SESSIONNAME, new AlertBean(_validUserid, "", _validPswd));
    }

    protected String getPath(String page)
    {
        return ("/" + page);
    }
}
