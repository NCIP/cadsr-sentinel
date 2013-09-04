/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

// Copyright (c) 2006 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/database/DBAlertOracleMap2.java,v 1.2 2007-07-19 15:26:45 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.database;

/**
 * @author lhebel
 *
 */
public class DBAlertOracleMap2 extends DBAlertOracleMap1
{
    /**
     * 
     * @param key_
     * @param val_
     * @param col_
     * @param xtra_ 
     * @param subs_ 
     */
    public DBAlertOracleMap2(String key_, String val_, String col_, String xtra_, String subs_)
    {
        super(key_, val_);
        _xtra = xtra_;
        _col = col_;
        _subs = subs_;
    }

    /**
     * 
     */
    public String _xtra;

    /**
     * 
     */
    public String _col;

    /**
     * 
     */
    public String _subs;
}
