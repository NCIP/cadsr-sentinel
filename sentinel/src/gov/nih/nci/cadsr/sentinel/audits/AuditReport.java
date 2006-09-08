// Copyright (c) 2006 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/audits/AuditReport.java,v 1.1 2006-09-08 22:32:55 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.audits;

import gov.nih.nci.cadsr.sentinel.database.DBAlert;

/**
 * @author lhebel
 *
 */
public abstract class AuditReport
{
    /**
     * Constructor
     * 
     * @param db_ the database connection to the caDSR
     */
    public AuditReport(DBAlert db_)
    {
        _db = db_;
    }

    /**
     * Constructor
     *
     */
    public AuditReport()
    {
    }
    
    /**
     * Set the database connection object.
     * 
     * @param db_ the database connection.
     */
    public void setDB(DBAlert db_)
    {
        _db = db_;
    }
    
    /**
     * Return the title for the report.
     * 
     * @return the report title.
     */
    abstract public String getTitle();
    
    /**
     * Get the report details. Each array entry is a row in the formatted HTML table. Columns
     * for each row are separated by a double colon, "::", e.g. "Data Element::Heart::12345"
     * represents three columns. All array entries in the report must have the same number
     * of columns.
     * 
     * @return the report details formatted as described above.
     */
    abstract public String[] getReportRows();
    
    /**
     * Should the row count be displayed?
     * 
     * @return true to display the row count.
     */
    abstract public boolean okToDisplayCount();
    
    /**
     * Tell the report writer to right justify the last column.
     * 
     * @return true to right justify the last column of the report rows.
     */
    abstract public boolean rightJustifyLastColumn();
    
    /**
     * The database connection to the caDSR.
     */
    protected DBAlert _db;
}
