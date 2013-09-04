/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

// Copyright (c) 2006 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tool/ConceptItem.java,v 1.2 2007-07-19 15:26:45 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tool;

/**
 * @author lhebel
 *
 */
public class ConceptItem
{
    /**
     * Constructor
     *
     */
    public ConceptItem()
    {
    }
    
    /**
     * 
     */
    public String _idseq;  //con_idseq same as ac_idseq in administrative component

    /**
     * 
     */
    public String _conteidseq;  //conte_idseq is context id same in administrative component

    /**
     * 
     */
    public String _publicID;  //con_id is the public id

    /**
     * 
     */
    public String _version;
    
    /**
     * 
     */
    public String _evsSource;
    
    /**
     * 
     */
    public String _preferredName;
    
    /**
     * 
     */
    public String _longName;
    
    /**
     * 
     */
    public String _definitionSource;
    
    /**
     * 
     */
    public String _preferredDefinition;
    
    /**
     * 
     */
    public String _origin;
    
    /**
     * 
     */
    public String _workflow_status;
}
