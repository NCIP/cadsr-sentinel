/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

// Copyright (c) 2007 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/audits/AuditUsedObjectClasses.java,v 1.2 2007-07-19 15:26:44 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.audits;

import gov.nih.nci.cadsr.sentinel.database.DBAlert;
import gov.nih.nci.cadsr.common.Constants;

/**
 * @author lhebel
 *
 */
public class AuditUsedObjectClasses extends AuditReport
{
    /**
     * @param db_
     */
    public AuditUsedObjectClasses(DBAlert db_)
    {
        super(db_);
    }

    /**
     * 
     */
    public AuditUsedObjectClasses()
    {
        super();
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cadsr.sentinel.audits.AuditReport#getTitle()
     */
    @Override
    public String getTitle()
    {
        return "Object Classes not owned by " + Constants.DEFAULT_CONTEXT;
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cadsr.sentinel.audits.AuditReport#getReportRows()
     */
    @Override
    public String[] getReportRows()
    {
        return _db.reportUsedObjectClasses();
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cadsr.sentinel.audits.AuditReport#okToDisplayCount()
     */
    @Override
    public boolean okToDisplayCount()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cadsr.sentinel.audits.AuditReport#rightJustifyLastColumn()
     */
    @Override
    public boolean rightJustifyLastColumn()
    {
        return false;
    }
}
