// Copyright (c) 2007 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tool/ACXMLData.java,v 1.2 2007-12-07 21:52:53 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tool;

import gov.nih.nci.cadsr.sentinel.database.DBAlert;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.*;


import org.apache.log4j.Logger;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * This is the working class for the collection, assimilation and processing of caDSR data by the Sentinel Alert Auto Process server and generating an XML file.
 * Jdom parser is used to build the tree structure. Date: Sep 8, 2007 Time: 7:37:14 PM
 */
public class ACXMLData
{
    /** **/
    public static final String _elementCadsr = "cadsr";
    /** **/
    public static final String _attrDocType = "cadsrsentinel-modified-1.0.dtd";
    /** **/
    public static final String _elementAlertReport = "alertReport";
    /** **/
    public static final String _attrSoftwareName = "softwareName";
    /** **/
    public static final String _attrSoftwareVersion =  "softwareVersion";
    /** **/
    public static final String _attrVersion =  "version";
    /** **/
    public static final String _elementDatabase = "database";
    /** **/
    public static final String _attrServer = "server";
    /** **/
    public static final String _attrName = "name";
    /** **/
    public static final String _attrRAI = "rai";
    /** **/
    public static final String _elementDefinition = "definition";
    /** **/
    public static final String _elementName = "name";
    /** **/
    public static final String _attrValue = "value";
    /** **/
    public static final String _elementId = "id";
    /** **/
    public static final String _elementIntro = "intro";
    /** **/
    public static final String _elementCreatedBy = "createdBy";
    /** **/
    public static final String _attrUser = "user";
    /** **/
    public static final String _attrEmail = "email";
    /** **/
    public static final String _elementRecipient = "recipient";
    /** **/
    public static final String _attrUrl = "url";
    /** **/
    public static final String _elementSummary = "summary";
    /** **/
    public static final String _elementLastAutoRun = "lastAutoRun";
    /** **/
    public static final String _attrTime = "time";
    /** **/
    public static final String _elementFrequency = "frequency";
    /** **/
    public static final String _attrUnit = "unit";
    /** **/
    public static final String _elementStatus = "status";
    /** **/
    public static final String _attrCode = "code";
    /** **/
    public static final String _attrBeginDate = "beginDate";
    /** **/
    public static final String _attrEndDate = "endDate";
    /** **/
    public static final String _elementLevel = "level";
    /** **/
    public static final String _attrDepth = "depth";
    /** **/
    public static final String _elementStart = "start";
    /** **/
    public static final String _elementEnd = "end";
    /** **/
    public static final String _elementCreatedOn = "createdOn";
    /** **/
    public static final String _attrDate = "date";
    /** **/
    public static final String _elementGroup = "group";
    /** **/
    public static final String _elementAssociateItem = "associateItem";
    /** **/
    public static final String _elementChangedItem = "changedItem";
    /** **/
    public static final String _elementChange = "change";

    private OutputStream _out;

    private AlertRec _rec;

    private DBAlert _db;

    private Stack<RepRows> _save;

    private String _dbName;

    private String _cemail;

    private String _version;

    private Timestamp _startDate;

    private Timestamp _endDate;

    private static final Logger _logger = Logger.getLogger(ACXMLData.class.getName());

    /**
     * Constructor
     *
     * @param out_
     * @param rec_
     * @param db_
     * @param save_
     * @param dbName_
     * @param cemail_
     * @param version_
     * @param start_
     * @param end_
     */
    public ACXMLData(OutputStream out_, AlertRec rec_, DBAlert db_, Stack<RepRows> save_, String dbName_, String cemail_, String version_, Timestamp start_,
        Timestamp end_)
    {
        _out = out_;
        _rec = rec_;
        _db = db_;
        _save = save_;
        _dbName = dbName_;
        _cemail = cemail_;
        _version = version_;
        _startDate = start_;
        _endDate = end_;
    }

    /**
     * Write the XML output file
     */
    public void writeXMLWithJDOM()
    {
        ResourceBundle props = PropertyResourceBundle.getBundle("gov.nih.nci.cadsr.sentinel.DSRAlert");
        String attrDocType = (props != null) ? props.getString("cadsr.sentinel.dtd") : "Error loading Property file.";
        String attrSoftwareName = (props != null) ? props.getString("cadsr.softwarename") : "Error loading Property file.";


        Element root = new Element(_elementCadsr);
        // replace the dtd with the appropriate dtd URL
        DocType type = new DocType(_elementCadsr, attrDocType);
        // replace
        Document doc = new Document(root, type);

        try
        {
            XMLOutputter serializer = new XMLOutputter();
            // serialize with two space indents and extra line breaks
            serializer.setFormat(Format.getPrettyFormat());

            // create element alertreport
            // <!ELEMENT alertReport (database | definition | changedItem* | associateItem* | group*)>
            Element alertReport = new Element(_elementAlertReport);
            alertReport.setAttribute(_attrSoftwareName, attrSoftwareName);

            String[] parts = _version.split(";");
            alertReport.setAttribute(_attrSoftwareVersion, parts[parts.length - 1]);
            alertReport.setAttribute(_attrVersion, "1.0");

            // Add element database
            Element database = new Element(_elementDatabase);
            // add attributes to the database element

            database.setAttribute(_attrServer, "");
            database.setAttribute(_attrName, _dbName);
            database.setAttribute(_attrRAI, _db.getDatabaseRAI());
            alertReport.addContent(database);

            // Add element definition
            // <!ELEMENT definition (name | id | intro? | createdBy | recipient* | summary? | criteria+ |
            // monitor+ | lastAutoRun? | frequency | status | level | start | end | createdOn)>
            Element definition = new Element(_elementDefinition);
            // add elements to the definition element
            // add name to definition
            Element name = new Element(_elementName);
            name.setAttribute(_attrValue, _rec.getName());
            definition.addContent(name);

            // add id to definition
            Element id = new Element(_elementId);
            id.addContent(_rec.getAlertRecNum());
            definition.addContent(id);

            // add intro to definition
            Element intro = new Element(_elementIntro);
            intro.addContent(_rec.getIntro(false));
            definition.addContent(intro);

            // Add element createdBy to definition
            Element createdBy = new Element(_elementCreatedBy);
            // add attributes to the _createdBy element
            createdBy.setAttribute(_attrUser, _rec.getCreator());
            createdBy.setAttribute(_attrName, _rec.getCreatorName());
            createdBy.setAttribute(_attrEmail, _cemail);
            definition.addContent(createdBy);

            // Add recipient list
            // Add element recipient to definition, should be atleast 1 or more
            // String recipientNames= _db.selectRecipientNames(_rec.getRecipients());

            parts = _rec.getRecipients();
            for (int rc = 0; rc < parts.length; rc++)
            {
                Element recipient = new Element(_elementRecipient);

                // A process URL recipient
                if (parts[rc].startsWith("http://") || (parts[rc].startsWith("https://")))
                {
                    recipient.setAttribute(_attrUrl, parts[rc]);
                    definition.addContent(recipient);
                }

                // An email address recipient
                else if (parts[rc].indexOf("@") != -1)
                {
                    recipient.setAttribute(_attrEmail, parts[rc]);
                    definition.addContent(recipient);
                }

                // A Context Curator Group
                else if (parts[rc].indexOf('/') == 0)
                {
                    String[] temp = _db.selectEmailsFromConte(parts[rc]);
                    if(temp!=null && temp.length>0)
                    {
                        for(int cc=0; cc<temp.length;cc++){
                            if(temp[cc]!=null){
                                recipient = new Element(_elementRecipient);
                                recipient.setAttribute(_attrEmail, temp[cc]);
                                definition.addContent(recipient);
                            }
                        }
                    }
                }

                // A User
                else
                {
                    String temp[] = new String[1];
                    temp[0] = parts[rc];
                    recipient.setAttribute(_attrUser, parts[rc]);
                    recipient.setAttribute(_attrName, ACData.convertNullString(_db.selectRecipientNames(temp)));
                    recipient.setAttribute(_attrEmail, _db.selectEmailFromUser(parts[rc]));
                    definition.addContent(recipient);
                    String txt = _db.getError();
                    if (txt != null)
                        _logger.error(txt);
                }
            }

            // add summary to definition can be 0 or 1
            if (_rec.getSummary(true) != null)
            {
                Element summary = new Element(_elementSummary);
                summary.addContent(_rec.getSummary(true));
                definition.addContent(summary);
            }

            /*
             * Use this piece of code to display the criteria and monitor //Add criteria to definition, should be atleast 1 or more Element _criteria = new
             * Element("criteria"); //add attributes to criteria _criteria.setAttribute("type", ""); _criteria.setAttribute("value", ""); //add element value to
             * _criteria can be 0 or more Element _criteriaValue = new Element("value"); _criteriaValue.addContent(""); _criteria.addContent(_criteriaValue);
             * _definition.addContent(_criteria); //Add monitor to definition, should be atleast 1 or more Element _monitor = new Element("monitor"); //add
             * attributes to _monitor _monitor.setAttribute("type", ""); _monitor.setAttribute("value", ""); _definition.addContent(_monitor);
             */

            // Add _lastAutoRun to definition, can be 0 or 1
            Element lastAutoRun = new Element(_elementLastAutoRun);
            // add attributes to _lastAutoRun
            if (_rec.getAdate() != null)
                lastAutoRun.setAttribute(_attrTime, _rec.getAdate().toString());
            else
                lastAutoRun.setAttribute(_attrTime, "");

            definition.addContent(lastAutoRun);

            // Add frequency to definition, should be there once must be present

            Element frequency = new Element(_elementFrequency);
            // add attributes to _frequency
            if (_rec.getFreqString().equals("D"))
                frequency.setAttribute(_attrUnit, "Day");
            else if (_rec.getFreqString().equals("W"))
            {
                frequency.setAttribute(_attrUnit, "Week");
                frequency.setAttribute(_attrValue, _rec.getFreq(false));
            }
            else
            {
                frequency.setAttribute(_attrUnit, "Month");

                // check this method for week & month
                frequency.setAttribute(_attrValue, _rec.getFreq(false));
            }
            definition.addContent(frequency);

            // Add status to definition, should be there once must be present

            Element status = new Element(_elementStatus);
            // add attributes to _status
            if (_rec.isActive())
                status.setAttribute(_attrCode, "Active");
            else if (_rec.isInactive())
                status.setAttribute(_attrCode, "Inactive");
            else if (_rec.isActiveOnce())
                status.setAttribute(_attrCode, "Once");
            else if (_rec.isActiveDates())
            {
                status.setAttribute(_attrCode, "Range");
                // add beginDate and endDate only if the code is Range

                status.setAttribute(_attrBeginDate, _rec.getStart().toString().substring(0, 10));
                status.setAttribute(_attrEndDate, _rec.getEnd().toString().substring(0, 10));
            }
            definition.addContent(status);

            // Add level to definition, should be there once, must be present

            Element level = new Element(_elementLevel);
            // add attributes to _level
            level.setAttribute(_attrDepth, new StringBuffer().append(_rec.getIAssocLvl()).toString());
            definition.addContent(level);

            // Add start to definition, should be there once, must be present

            Element start = new Element(_elementStart);
            // add attributes to _start
            start.setAttribute(_attrDate, _startDate.toString().substring(0, 10));
            definition.addContent(start);

            // Add end to definition, should be there once, must be present

            Element end = new Element(_elementEnd);
            // add attributes to _start
            end.setAttribute(_attrDate, _endDate.toString().substring(0, 10));
            definition.addContent(end);

            // Add createdOn to definition, should be there once, must be present

            Element createdOn = new Element(_elementCreatedOn);
            // add attributes to _start
            createdOn.setAttribute(_attrTime, new Timestamp(System.currentTimeMillis()).toString());
            definition.addContent(createdOn);

            // Add definition to the alert report
            alertReport.addContent(definition);

            // Work on changed item now, changedItem can be 0 or more, so check for changedItem
            // Dump the stack.

            List<Element> changeList = getChangedItems(_db, _save, _rec.getIAssocLvl());


            if(changeList!=null  && changeList.size()>0)
            {
            // add the changed items or associated items or groups to the report.
            // Add group to alertReport can be 0 or more, if there is a change then there is a group
            for (int j = 0; j < changeList.size(); j++)
            {

                {
                    if ((changeList.get(j)).getName().equalsIgnoreCase(_elementChangedItem))
                        alertReport.addContent(changeList.get(j));
                }
            }

            // Add associateItem to alertReport can be 0 or more
            Map<String, Element> associateItemMap = new HashMap<String, Element>();
            for (int j = 0; j < changeList.size(); j++)
            {

                Element aItem = changeList.get(j);
                if ((changeList.get(j)).getName().equalsIgnoreCase(_elementAssociateItem))
                {
                    // check to see if the associateItem already appears in the Jdom tree, ignore if already there
                    associateItemMap.put(aItem.getAttributeValue("id"), aItem);

                }
            }

            // All unique values are in the Map - now process the map
            if (associateItemMap != null && associateItemMap.size() > 0)
            {
                Iterator<Element> it = associateItemMap.values().iterator();
                while (it.hasNext())
                {
                    alertReport.addContent(it.next());
                }

            }

            // Add groups to alertReport can be 0 or more

            for (int j = 0; j < changeList.size(); j++)
            {
                if ((changeList.get(j)).getName().equalsIgnoreCase(_elementGroup))
                    alertReport.addContent(changeList.get(j));
            }
            }

            root.addContent(alertReport);

            serializer.output(doc, _out);
        }
        catch (IOException e)
        {
            _logger.error("Error writing to the xml file");
        }

    }

    /**
     * Populate the common changedItem and associateItem attributes.
     *
     * @param elm_ the XML Element
     * @param xml_ the XML data
     */
    private void itemHeader(Element elm_, ACDataXML xml_)
    {

        elm_.setAttribute("type", xml_._type);
        elm_.setAttribute("name", xml_._name);
        elm_.setAttribute("id", xml_._id);
        if (xml_._publicId != null)
            elm_.setAttribute("publicId", xml_._publicId);
         if (xml_._version != null)
        elm_.setAttribute("version", xml_._version);
         if (xml_._modifiedByUser != null)
        elm_.setAttribute("modifiedByUser", xml_._modifiedByUser);
         if (xml_._modifiedByName != null)
        elm_.setAttribute("modifiedByName", xml_._modifiedByName);
        if (xml_._modifiedTime != null)
            elm_.setAttribute("modifiedTime", xml_._modifiedTime);
        elm_.setAttribute("createdByUser", xml_._createdByUser);
        elm_.setAttribute("createdByName", xml_._createdByName);
        elm_.setAttribute("createdTime", xml_._createdTime);
        elm_.setAttribute("changeNote", xml_._changeNote);
    }

    /**
     * Dump the changed items  into the XML tree.
     *
     * @param db_
     *        A database object for an open connection to a caDSR.
     * @param save_
     *        A stack containing the report results. (Note this is a LIFO
     *        stack.)
     * @param depth depth of the associated items to traverse
     * @return  a List of changed items  (changedItem, associatedItem, group)
     */
    public List<Element> getChangedItems(DBAlert db_, Stack<RepRows> save_, int depth)
    {
        List<Element> changeList = new ArrayList<Element>();
        List<Element> groupList = new ArrayList<Element>();
        int count = -1;

        // Get only rows of interest dep
        Stack<RepRows> report = ACData.dumpTrim(save_, depth);
        while (!report.empty())
        {
            RepRows val = report.pop();
            val._rec.resolveNames(db_);
            ACDataXML xml = val._rec.getXML();
            if (val._rec.isPrimary())
            {
                Element changedItem = new Element("changedItem");
                // add attributes to changedItem

                itemHeader(changedItem, xml);

                // can have 0 or more details so add logic to see if required
                if (xml._changes != null)
                {
                    for (int i = 0; i < xml._changes.length; i++)
                    {
                        ACDataChangesXML chg = xml._changes[i];
                        Element details = new Element("details");
                        // Add attributes to _details element
                        details.setAttribute("modifiedByUser", chg._modifiedByUser);
                        details.setAttribute("modifiedByName", chg._modifiedByName);
                        details.setAttribute("time", chg._time);
                        // Conver the meaningful names in the changes to internal codes for xml
                        String[] changekeyNames = db_.getKeyNames(chg._attributes);
                        // should have atleast one or more change elements
                        for (int chgcnt = 0; chgcnt < changekeyNames.length; chgcnt++)
                        {
                            Element change = new Element(_elementChange);
                            change.setAttribute("attribute", changekeyNames[chgcnt]);
                            change.setAttribute("oldValue", chg._oldValues[chgcnt]);
                            change.setAttribute("newValue", chg._newValues[chgcnt]);
                            details.addContent(change);
                        }
                        changedItem.addContent(details);
                    }
                }
                changeList.add(changedItem);

                // Add group to alertReport can be 0 or more, if there is a change then there is a group
                // for each changedItem add a group
                Element group = new Element("group");
                // add attribute changedItemId to _group which is referencint the id of the changedItem
                group.setAttribute("changedItemId", xml._id);

                groupList.add(group);
                count++;
            }
            else
            {

                Element associateItem = new Element("associateItem");
                // add attributes to changedItem

                itemHeader(associateItem, xml);

                changeList.add(associateItem);
                // Associate this item to the group

                Element group = (Element) groupList.get(count);

                Element associate = new Element("associate");
                associate.setAttribute("childItemId", xml._id);
                associate.setAttribute("parentItemId", xml._relatedId);
                groupList.remove(count);
                group.addContent(associate);
                groupList.add(count, group);

            }
        }

        // add the grouplist to changeList
        for (int k = 0; k < groupList.size(); k++)
        {
            changeList.add(groupList.get(k));
        }
        return changeList;
    }
}
