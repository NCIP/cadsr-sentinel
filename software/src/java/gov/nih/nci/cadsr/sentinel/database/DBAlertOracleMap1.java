// Copyright (c) 2006 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/database/DBAlertOracleMap1.java,v 1.3 2007-12-06 20:52:09 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.database;

/**
 * @author lhebel
 *
 */
public class DBAlertOracleMap1 implements Comparable
{
    /**
     * 
     * @param key_
     * @param val_
     */
    public DBAlertOracleMap1(String key_, String val_)
    {
        _key = key_;
        _val = val_;
    }

    /**
     * 
     */
    public String _key;

    /**
     * 
     */
    public String _val;

    /**
     * Overrides the comparison function required by the parent class. Allows use of the
     * generic Collections.sort() later.
     * 
     * @param obj the other object to which to compare
     * @return how the objects compare, 0 = identical, >0 other is "less" than this object, <0 other is greater than this object
     */
    public int compareTo(Object obj)
    {
        DBAlertOracleMap1 anotherMap = (DBAlertOracleMap1) obj;
        int valComp = _val.compareTo(anotherMap._val);

        //TODO why do the _val.compareTo twice??
        return ((valComp == 0) ? _val.compareTo(anotherMap._val) : valComp);
    }
}
