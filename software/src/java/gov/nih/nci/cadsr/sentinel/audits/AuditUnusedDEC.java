/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

// Copyright (c) 2006 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/audits/AuditUnusedDEC.java,v 1.2 2007-07-19 15:26:44 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.audits;


/**
 * @author lhebel
 *
 */
public class AuditUnusedDEC extends AuditReport
{
    /**
     * Constructor
     *
     */
    public AuditUnusedDEC()
    {
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cadsr.sentinel.tool.AuditReport#getTitle()
     */
    @Override
    public String getTitle()
    {
        return "Unused Data Element Concepts";
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cadsr.sentinel.tool.AuditReport#getReportRows()
     */
    @Override
    public String[] getReportRows()
    {
        return _db.reportUnusedDEC();
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
