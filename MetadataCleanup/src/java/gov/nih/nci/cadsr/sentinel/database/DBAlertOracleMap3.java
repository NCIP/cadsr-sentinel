/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

// Copyright (c) 2006 ScenPro, Inc.
// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/database/DBAlertOracleMap3.java,v 1.2 2007-07-19 15:26:45 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.database;

/**
 * @author lhebel
 * 
 */
public class DBAlertOracleMap3 extends DBAlertOracleMap1
{
    /**
     * 
     * @param key_
     * @param val_
     * @param table_
     * @param col_
     */
    public DBAlertOracleMap3(String key_, String val_, String table_, String col_)
    {
        super(key_, val_);
        _table = table_;
        _col = col_;
    }

    /**
     * 
     */
    public String _table;

    /**
     * 
     */
    public String _col;
}
