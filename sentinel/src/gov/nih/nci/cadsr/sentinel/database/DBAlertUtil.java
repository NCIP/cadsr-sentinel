// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/database/DBAlertUtil.java,v 1.5 2007-12-06 20:52:09 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.database;

import java.sql.SQLException;


// import org.apache.log4j.Logger;

/**
 * 
 * @author lhebel
 *
 */
public class DBAlertUtil
{
    
    /**
     * Instantiate the desired implementation of the DBAlert interface class for the database
     * engine hosting the caDSR. Currently only Oracle is implemented. If MySQL or another
     * database is desired, following these implementation steps.
     * 
     * <ol><li>Copy the DBAlertOracle class to a new class. Use a name to match the database
     * implementation, e.g. DBAlertMySQL.</li>
     * <li>Review the content of the new class and the SQL making modifications as needed
     * for the database engine.</li>
     * <li>In the DBAlertUtil.factory() method, add appropriate logic to distinguish which
     * database engine should be used. Either replace the "new DBAlertOracle()" with a "new"
     * for another class or added configuration methods to control which database engine is
     * used.</li></ol>
     * 
     * No other changes need to be made to the Sentinel Tool. All logic is data driven and
     * provided the caDSR matches the expected schema release, references to the DBAlert
     * interface protect the logic from the engine.
     * 
     * @return An instantiation of a DBAlert interface class.
     */
    static public DBAlert factory()
    {
        return new DBAlertOracle();
    }

    /**
     * Perform a binary search on a sorted list of keys.
     * 
     * @param keys_
     *        The key array to be searched.
     * @param vals_
     *        The values which match the key entries.
     * @param key_
     *        The key to search for.
     * @return The value corresponding to the matching key if found or the
     *         original key (key_) if not found.
     */
    static public String binarySearch(String keys_[], String vals_[],
        String key_)
    {
        int ndx = binarySearch(keys_, key_);
        if (ndx == -1)
            return key_;
        return vals_[ndx];
    }

    /**
     * Perform a binary search on a sorted list of keys.
     * 
     * @param list_
     *        The key/value array to be searched.
     * @param key_
     *        The key to search for.
     * @return The value corresponding to the matching key if found or the
     *         original key (key_) if not found.
     */
    static public String binarySearchS(DBAlertOracleMap1[] list_, String key_)
    {
        int ndx = binarySearch(list_, key_);
        if (ndx == -1)
            return key_;
        return list_[ndx]._val;
    }

    /**
     * Perform a binary search on a sorted list of keys.
     * 
     * @param keys_
     *        The key array to be searched.
     * @param key_
     *        The key to search for.
     * @return The index into the keys_ array.
     */
    static public int binarySearch(String keys_[], String key_)
    {
        int min = 0;
        int max = keys_.length;
        while (true)
        {
            int ndx = (max + min) / 2;
            int compare = key_.compareTo(keys_[ndx]);
            if (compare == 0)
            {
                return ndx;
            }
            else if (compare > 0)
            {
                if (min == ndx)
                    return -1;
                min = ndx;
            }
            else
            {
                if (max == ndx)
                    return -1;
                max = ndx;
            }
        }
    }

    /**
     * Perform a binary search on a sorted list of keys.
     * 
     * @param list_
     *        The key/value array to be searched.
     * @param key_
     *        The key to search for.
     * @return The index into the keys_ array.
     */
    static public int binarySearch(DBAlertOracleMap1[] list_, String key_)
    {
        int min = 0;
        int max = list_.length;
        while (true)
        {
            int ndx = (max + min) / 2;
            int compare = key_.compareTo(list_[ndx]._key);
            if (compare == 0)
            {
                return ndx;
            }
            else if (compare > 0)
            {
                if (min == ndx)
                    return -1;
                min = ndx;
            }
            else
            {
                if (max == ndx)
                    return -1;
                max = ndx;
            }
        }
    }


    /**
     * Perform a binary search on a sorted list of values.
     *
     * @param list_
     *        The key/value array to be searched.
     * @param val_
     *        The value to search for.
     * @return The index into the values array.
     */
    static public int binarySearchValues(DBAlertOracleMap1[] list_, String val_)
    {
        int min = 0;
        int max = list_.length;
        while (true)
        {
            int ndx = (max + min) / 2;
            int compare = val_.compareTo(list_[ndx]._val);
            if (compare == 0)
            {
                return ndx;
            }
            else if (compare > 0)
            {
                if (min == ndx)
                    return -1;
                min = ndx;
            }
            else
            {
                if (max == ndx)
                    return -1;
                max = ndx;
            }
        }
    }
    
    /**
     * Sometimes the SQL error code isn't set and we have to pull it from the message.
     * 
     * @param ex_ the SQL exception
     * @return the correct error code.
     */
    static public int getSQLErrorCode(SQLException ex_)
    {
        int error = ex_.getErrorCode();
        if (error != 0)
            return error;
        
        String msg = ex_.toString();
        int pos = msg.indexOf("ORA-");
        if (pos < 0)
            return -9999;

        pos += 4;
        String[] temp = msg.substring(pos).split("[:, ]");
        if (temp.length < 2)
            return -9998;

        return Integer.valueOf(temp[0]);
    }

//    private static final Logger _logger = Logger.getLogger(DBAlert.class.getName());
}
