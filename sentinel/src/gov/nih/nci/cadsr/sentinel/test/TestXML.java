
package gov.nih.nci.cadsr.sentinel.test;

import gov.nih.nci.cadsr.sentinel.database.DBAlertOracle;
import gov.nih.nci.cadsr.sentinel.database.DBAlertOracleMap1;
import gov.nih.nci.cadsr.sentinel.database.DBAlertUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Created by IntelliJ IDEA. Date: Sep 3, 2007 Time: 2:56:36 PM To change this template use File | Settings | File Templates.
 */
public class TestXML extends TestCase
{

    String URL = "";

    String[] recipients = new String[3];

    /**
     * Main entry
     * 
     * @param args
     */
    public static void main(String args[])
    {

        System.out.println("\n\nTest JDOM  - output\n\n");

        writeXMLWithJDOM();

    }

    /**
     * Test changing the URL
     */
    public void testURLChange()
    {

        String temp = "";
        System.out.println("Original URL : " + URL);
        if (URL.toLowerCase().startsWith("http://") || URL.toLowerCase().startsWith("https://"))
        {
            System.out.println("This is an URL");
        }
        temp = URL.toLowerCase();
        System.out.println("Lower CASe URL : " + temp);
        if (temp.endsWith("/"))
        {
            System.out.println("Has slash at the end");
            temp = temp.substring(0, temp.lastIndexOf("/"));
        }
        System.out.println("Final URL to be Saved : " + temp);

    }

    public void setUp()
    {
        URL = "https://locaLHOST:8080/TEST";
        recipients[0] = "https://localhost:9999/struts-blank/processalert";
        // recipients[0] = "developer1971@gmail.com";
        // recipients[1] = "http://localhost:9999/struts-blank/processalert";
        // recipients[1] = "aseiba@omnicomm.com";
        // recipients[2] = "aseiba@omnicomm.com";
        recipients[1] = "https://localhost:9999/struts-blank/processalert/";
        recipients[2] = "https://localhost:9999/struts-blank/processalert";

    }

    /**
     * Test lookup for a Process recipient.
     *
     */
    public void testHasProcessRecipient()
    {
        assertTrue(hasProcessRecipient(recipients));
        // assertFalse(hasProcessReceipient(recipients));
    }

    // Method to check if a receipient is a process
    private static boolean hasProcessRecipient(String[] receipients)
    {
        boolean hasProcess = false;
        for (int i = 0; i < receipients.length; i++)
        {
            if (receipients[i].startsWith("http://") || (receipients[i].startsWith("https://")))
            {
                hasProcess = true;
            }
        }
        return hasProcess;
    }

    /**
     * Test the lookup for an email recipient
     *
     */
    public void testHasEmailReceipient()
    {
        // assertTrue(hasEmailReceipient(recipients));
        assertFalse(hasEmailReceipient(recipients));
    }

    // Method to check if there is a receipient as a email address (including the name)
    private static boolean hasEmailReceipient(String[] receipients)
    {
        boolean hasEmail = false;
        for (int i = 0; i < receipients.length; i++)
        {
            if (receipients[i].indexOf("@") != -1)
                hasEmail = true;

        }
        return hasEmail; // To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Test the creation of the XML using JDOM
     *
     */
    public static void writeXMLWithJDOM()
    {

        Element root = new Element("cadsr");
        DocType type = new DocType("cadsr", "cadsrsentinel-1.0.dtd");
        Document doc = new Document(root, type);
        // serialize with two space indents and extra line breaks
        try
        {
            XMLOutputter _serializer = new XMLOutputter();
            _serializer.setFormat(Format.getPrettyFormat());
            for (int i = 0; i < 2; i++)
            {
                // create element alertreport
                // <!ELEMENT alertReport (database | definition | changedItem* | associateItem* | group*)>
                Element _alertReport = new Element("alertReport");
                _alertReport.setAttribute("softwareName", "text");
                _alertReport.setAttribute("softwareVersion", "text");
                _alertReport.setAttribute("version", "1.0");

                // Add element database
                Element _database = new Element("database");
                // add attributes to the database element
                _database.setAttribute("server", "text");
                _database.setAttribute("name", "text");
                _database.setAttribute("rai", "text");
                _alertReport.addContent(_database);

                // Add element definition
                // <!ELEMENT definition (name | id | intro? | createdBy | recipient* | summary? | criteria+ |
                // monitor+ | lastAutoRun? | frequency | status | level | start | end | createdOn)>
                Element _definition = new Element("definition");
                // add elements to the definition element
                // add name to definition
                Element _name = new Element("name");
                _name.setAttribute("value", "test");
                _definition.addContent(_name);

                // add id to definition
                Element _id = new Element("id");
                _id.addContent("345312134");
                _definition.addContent(_id);

                // add intro to definition
                Element _intro = new Element("intro");
                _intro.addContent("345312134");
                _definition.addContent(_intro);

                // Add element createdBy to definition
                Element _createdBy = new Element("createdBy");
                // add attributes to the _createdBy element
                _createdBy.setAttribute("user", "aseiba");
                _createdBy.setAttribute("name", "Aaron Seib");
                _createdBy.setAttribute("email", "aseiba@omnicomm.com");
                _definition.addContent(_createdBy);

                // Add recipient list
                // Add element recipient to definition, should be atleast 1 or more
                Element _recipient = new Element("recipient");
                // add attributes to the _recipient element , can be a process too in the email field
                _recipient.setAttribute("user", "aseiba");
                _recipient.setAttribute("name", "Aaron Seib");
                _recipient.setAttribute("email", "aseiba@omnicomm.com");
                _definition.addContent(_recipient);

                // add summary to definition can be 0 or 1
                Element _summary = new Element("summary");
                _summary.addContent("345312134");
                _definition.addContent(_summary);

                // Add criteria to definition, should be atleast 1 or more

                Element _criteria = new Element("criteria");
                // add attributes to criteria
                _criteria.setAttribute("type", "COS");
                _criteria.setAttribute("value", "fsksdfskdfkl");

                // add element value to _criteria can be 0 or more

                Element _criteriaValue = new Element("value");
                _criteriaValue.addContent("fromdate=98833andtodate=w324323");
                _criteria.addContent(_criteriaValue);
                _definition.addContent(_criteria);

                // Add monitor to definition, should be atleast 1 or more

                Element _monitor = new Element("monitor");
                // add attributes to _monitor
                _monitor.setAttribute("type", "REG_STATUS");
                _monitor.setAttribute("value", "Aaron Seib");
                _definition.addContent(_monitor);

                // Add _lastAutoRun to definition, can be 0 or 1

                Element _lastAutoRun = new Element("lastAutoRun");
                // add attributes to _lastAutoRun
                _lastAutoRun.setAttribute("time", "09:30PM");
                _definition.addContent(_lastAutoRun);

                // Add frequency to definition, should be there once must be present

                Element _frequency = new Element("frequency");
                // add attributes to _frequency
                _frequency.setAttribute("unit", "DAY");
                _frequency.setAttribute("value", "Thursday");
                _definition.addContent(_frequency);

                // Add status to definition, should be there once must be present

                Element _status = new Element("status");
                // add attributes to _status
                _status.setAttribute("code", "Always");
                _status.setAttribute("beginDate", "07/20/2007");
                _status.setAttribute("endDate", "09/20/2007");
                _definition.addContent(_status);

                // Add level to definition, should be there once, must be present

                Element _level = new Element("level");
                // add attributes to _level
                _level.setAttribute("depth", "3");
                _definition.addContent(_level);

                // Add start to definition, should be there once, must be present

                Element _start = new Element("start");
                // add attributes to _start
                _start.setAttribute("date", "07/20/2007");
                _definition.addContent(_start);

                // Add end to definition, should be there once, must be present

                Element _end = new Element("end");
                // add attributes to _start
                _end.setAttribute("date", "09/20/2007");
                _definition.addContent(_end);

                // Add createdOn to definition, should be there once, must be present

                Element _createdOn = new Element("createdOn");
                // add attributes to _start
                _createdOn.setAttribute("time", "09/08/2007 09:30PM");
                _definition.addContent(_createdOn);

                // Add definition to the alert report
                _alertReport.addContent(_definition);

                // Work on changed item now, changedItem can be 0 or more, so check for changedItem

                for (int j = 0; j < 2; j++)
                {
                    Element _changedItem = new Element("changedItem");
                    // add attributes to changedItem

                    _changedItem.setAttribute("type", "cd");
                    _changedItem.setAttribute("name", "Joe Ben");
                    _changedItem.setAttribute("id", "53455235345");
                    _changedItem.setAttribute("publicId", "3453224");
                    _changedItem.setAttribute("version", "2.0");
                    _changedItem.setAttribute("modifiedByUser", "seiba");
                    _changedItem.setAttribute("modifiedByName", "Aaron Seib");
                    _changedItem.setAttribute("modifiedTime", "09/05/200709:30AM");
                    _changedItem.setAttribute("createdByUser", "Jhonson");
                    _changedItem.setAttribute("createdByName", "Ben Jhonson");
                    _changedItem.setAttribute("createdTime", "09/05/200709:30AM");
                    _changedItem.setAttribute("changeNote", "Changes to the permissible values");

                    // can have 0 or more details so add logic to see if required
                    for (int m = 0; m < 2; m++)
                    {
                        Element _details = new Element("details");
                        // Add attributes to _details element
                        _details.setAttribute("modifiedByUser", "seiba");
                        _details.setAttribute("modifiedByName", "Aaron Seib");
                        _details.setAttribute("time", "09/05/200709:30AM");

                        // should have atleast one or more change elements
                        for (int n = 0; n < 2; n++)
                        {
                            Element _change = new Element("change");
                            _change.setAttribute("attribute", "ASL_NAME");
                            _change.setAttribute("oldValue", "Gender" + n);
                            _change.setAttribute("newValue", "Patient_Gender" + n);
                            _details.addContent(_change);
                        }
                        _changedItem.addContent(_details);
                    }

                    _alertReport.addContent(_changedItem);
                }

                // Add associateItem to alertReport can be 0 or more

                for (int j = 0; j < 2; j++)
                {
                    Element _associateItem = new Element("associateItem");
                    // add attributes to changedItem

                    _associateItem.setAttribute("type", "cd");
                    _associateItem.setAttribute("name", "Joe Ben");
                    _associateItem.setAttribute("id", "53455235345");
                    _associateItem.setAttribute("publicId", "3453224");
                    _associateItem.setAttribute("version", "2.0");
                    _associateItem.setAttribute("modifiedByUser", "seiba");
                    _associateItem.setAttribute("modifiedByName", "Aaron Seib");
                    _associateItem.setAttribute("modifiedTime", "09/05/200709:30AM");
                    _associateItem.setAttribute("createdByUser", "Jhonson");
                    _associateItem.setAttribute("createdByName", "Ben Jhonson");
                    _associateItem.setAttribute("createdTime", "09/05/200709:30AM");
                    _associateItem.setAttribute("changeNote", "Changes to the permissible values");
                    _alertReport.addContent(_associateItem);
                }

                // Add group to alertReport can be 0 or more
                for (int j = 0; j < 2; j++)
                {
                    Element _group = new Element("group");
                    // add attributes to _group
                    _group.setAttribute("changedItemId", "432345");

                    // Add Element associate to group , can be 0 or more

                    for (int k = 0; k < 2; k++)
                    {
                        Element _associate = new Element("associate");
                        _associate.setAttribute("childItemId", "432345");
                        _associate.setAttribute("parentItemId", "66432345");
                        _group.addContent(_associate);
                    }
                    // Add group to _alertReport
                    _alertReport.addContent(_group);
                }

                // <!ELEMENT alertReport (database | definition | changedItem* | associateItem* | group*)>
                root.addContent(_alertReport);
            }
            _serializer.output(doc, System.out);
        }
        catch (IOException e)
        {
            System.err.println(e);
        }

    }

    /**
     * Test a URL
     */
    public void testURL()
    {
        URL pURL = null;
        BufferedReader in = null;
        try
        {
            pURL = new URL(
                "http://localhost:9999/struts-blank/processurl.jsp?alertreport=http://localhost:9999/struts-blank/pages/Alert_Test_For_xml_20070909221431609.xml");
            URLConnection Url_Connection = pURL.openConnection();
            Url_Connection.setConnectTimeout(60);
            Url_Connection.setReadTimeout(60);
            in = new BufferedReader(new InputStreamReader(pURL.openStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);

            in.close();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        }
        catch (IOException e)
        {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        }

    }

    /**
     * Test for the date format
     */
    public void testDateFormat()
    {
        java.util.Date date = new java.util.Date();
        java.sql.Timestamp timeStampDate = new Timestamp((date).getTime());

        assertEquals(getFormattedDate(timeStampDate, "yyyy-MM-dd hh:mm:ss"), "2007-09-12 4:30:03");
    }

    private String getFormattedDate(Timestamp date, String format)
    {

        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat(format);
        System.out.println("date format = " + formatter.format(date));
        return formatter.format(date);
    }

    /**
     * Test for the key values for the changes
     */
    public void testBinarySearchForValues()
    {
        // Convert to list
        // Convert to list
        List<DBAlertOracleMap1> list = new ArrayList<DBAlertOracleMap1>(Arrays.asList(_DBMAP1));

        // Ensure list sorted
        DBAlertOracle.sort(list);

        DBAlertOracleMap1[] tempMap = list.toArray(new DBAlertOracleMap1[list.size()]);

        int i = DBAlertUtil.binarySearchValues(tempMap, "Concept Class association");
        assertNotNull(tempMap[i]._key);
        assertEquals(tempMap[i]._key, "CON_IDSEQ");
    }

    private static final DBAlertOracleMap1[] _DBMAP1 =
    {
        new DBAlertOracleMap1("ASL_NAME", "Workflow Status"), new DBAlertOracleMap1("BEGIN_DATE", "Begin Date"), new DBAlertOracleMap1("CDE_ID", "Public ID"),
        new DBAlertOracleMap1("CDR_IDSEQ", "Complex DE association"), new DBAlertOracleMap1("CD_IDSEQ", "Conceptual Domain association"),
        new DBAlertOracleMap1("CHANGE_NOTE", "Change Note"), new DBAlertOracleMap1("CONDR_IDSEQ", "Concept Class association"),
        new DBAlertOracleMap1("CON_IDSEQ", "Concept Class association"), new DBAlertOracleMap1("CREATED_BY", "Created By"),
        new DBAlertOracleMap1("CSTL_NAME", "Category"), new DBAlertOracleMap1("CS_ID", "Public ID"),
        new DBAlertOracleMap1("C_DEC_IDSEQ", "Child DEC association"), new DBAlertOracleMap1("C_DE_IDSEQ", "Child DE association"),
        new DBAlertOracleMap1("C_VD_IDSEQ", "Child VD association"), new DBAlertOracleMap1("DATE_CREATED", "Created Date"),
        new DBAlertOracleMap1("DATE_MODIFIED", "Modified Date"), new DBAlertOracleMap1("DECIMAL_PLACE", "Number of Decimal Places"),
        new DBAlertOracleMap1("DEC_ID", "Public ID"), new DBAlertOracleMap1("DEC_IDSEQ", "Data Element Concept association"),
        new DBAlertOracleMap1("DEC_REC_IDSEQ", "DEC_REC_IDSEQ"), new DBAlertOracleMap1("DEFINITION_SOURCE", "Definition Source"),
        new DBAlertOracleMap1("DELETED_IND", "Deleted Indicator"), new DBAlertOracleMap1("DESCRIPTION", "Description"),
        new DBAlertOracleMap1("DESIG_IDSEQ", "Designation association"), new DBAlertOracleMap1("DE_IDSEQ", "Data Element association"),
        new DBAlertOracleMap1("DE_REC_IDSEQ", "DE_REC_IDSEQ"), new DBAlertOracleMap1("DISPLAY_ORDER", "Display Order"),
        new DBAlertOracleMap1("DTL_NAME", "Data Type"), new DBAlertOracleMap1("END_DATE", "End Date"), new DBAlertOracleMap1("FORML_NAME", "Data Format"),
        new DBAlertOracleMap1("HIGH_VALUE_NUM", "Maximum Value"), new DBAlertOracleMap1("LABEL_TYPE_FLAG", "Label Type"),
        new DBAlertOracleMap1("LATEST_VERSION_IND", "Latest Version Indicator"), new DBAlertOracleMap1("LONG_NAME", "Long Name"),
        new DBAlertOracleMap1("LOW_VALUE_NUM", "Minimum Value"), new DBAlertOracleMap1("MAX_LENGTH_NUM", "Maximum Length"),
        new DBAlertOracleMap1("METHODS", "Methods"), new DBAlertOracleMap1("MIN_LENGTH_NUM", "Minimum Length"),
        new DBAlertOracleMap1("MODIFIED_BY", "Modified By"), new DBAlertOracleMap1("OBJ_CLASS_QUALIFIER", "Object Class Qualifier"),
        new DBAlertOracleMap1("OCL_NAME", "Object Class Name"), new DBAlertOracleMap1("OC_ID", "Public ID"),
        new DBAlertOracleMap1("OC_IDSEQ", "Object Class association"), new DBAlertOracleMap1("ORIGIN", "Origin"),
        new DBAlertOracleMap1("PREFERRED_DEFINITION", "Preferred Definition"), new DBAlertOracleMap1("PREFERRED_NAME", "Preferred Name"),
        new DBAlertOracleMap1("PROPERTY_QUALIFIER", "Property Qualifier"), new DBAlertOracleMap1("PROPL_NAME", "Property Name"),
        new DBAlertOracleMap1("PROP_ID", "Public ID"), new DBAlertOracleMap1("PROP_IDSEQ", "Property"), new DBAlertOracleMap1("PV_IDSEQ", "Permissible Value"),
        new DBAlertOracleMap1("P_DEC_IDSEQ", "Parent DEC association"), new DBAlertOracleMap1("P_DE_IDSEQ", "Parent DE association"),
        new DBAlertOracleMap1("P_VD_IDSEQ", "Parent VD association"), new DBAlertOracleMap1("QUALIFIER_NAME", "Qualifier"),
        new DBAlertOracleMap1("QUESTION", "Question"), new DBAlertOracleMap1("RD_IDSEQ", "Reference Document association"),
        new DBAlertOracleMap1("REP_IDSEQ", "Representation association"), new DBAlertOracleMap1("RL_NAME", "Relationship Name"),
        new DBAlertOracleMap1("RULE", "Rule"), new DBAlertOracleMap1("SHORT_MEANING", "Meaning"), new DBAlertOracleMap1("UOML_NAME", "Unit Of Measure"),
        new DBAlertOracleMap1("URL", "URL"), new DBAlertOracleMap1("VALUE", "Value"), new DBAlertOracleMap1("VD_ID", "Public ID"),
        new DBAlertOracleMap1("VD_IDSEQ", "Value Domain association"), new DBAlertOracleMap1("VD_REC_IDSEQ", "VD_REC_IDSEQ"),
        new DBAlertOracleMap1("VD_TYPE_FLAG", "Enumerated/Non-enumerated"), new DBAlertOracleMap1("VERSION", "Version")
    };

}
