// Copyright (c) 2006 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/audits/AuditUnusedObjectClasses.java,v 1.1 2006-09-08 22:32:55 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.audits;


/**
 * @author lhebel
 *
 */
public class AuditUnusedObjectClasses extends AuditReport
{
    /**
     * Constructor
     *
     */
    public AuditUnusedObjectClasses()
    {
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cadsr.sentinel.tool.AuditReport#getTitle()
     */
    @Override
    public String getTitle()
    {
        return "Unused Object Classes";
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cadsr.sentinel.tool.AuditReport#getReportRows()
     */
    @Override
    public String[] getReportRows()
    {
        return _db.reportUnusedObjectClasses();
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cadsr.sentinel.tool.AuditReport#okToDisplayCount()
     */
    @Override
    public boolean okToDisplayCount()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cadsr.sentinel.tool.AuditReport#rightJustifyLastColumn()
     */
    @Override
    public boolean rightJustifyLastColumn()
    {
        return false;
    }
}
