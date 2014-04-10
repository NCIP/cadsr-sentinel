/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

// Copyright (c) 2007 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tool/ACDataXML.java,v 1.1 2007-12-06 20:52:10 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tool;

/**
 * @author lhebel
 *
 */
public class ACDataXML
{
    /** **/
    public String _type;
    /** **/
    public String _name;
    /** **/
    public String _id;
    /** **/
    public String _publicId;
    /** **/
    public String _version;
    /** **/
    public String _modifiedByUser;
    /** **/
    public String _modifiedByName;
    /** **/
    public String _modifiedTime;
    /** **/
    public String _createdByUser;
    /** **/
    public String _createdByName;
    /** **/
    public String _createdTime;
    /** **/
    public String _changeNote;
    /** **/
    public String _relatedId;
    /** **/
    public ACDataChangesXML[] _changes;

    /**
     * 
     */
    public ACDataXML()
    {
    }

}
