/*L
 * Copyright ScenPro Inc, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
 */

// Copyright (c) 2006 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/audits/AuditUnusedConcepts.java,v 1.2 2007-07-19 15:26:44 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.audits;

import java.util.Vector;

/**
 * @author lhebel
 *
 */
public class AuditUnusedConcepts extends AuditReport
{
    /**
     * Constructor
     *
     */
    public AuditUnusedConcepts()
    {
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cadsr.sentinel.audits.AuditReport#getTitle()
     */
    @Override
    public String getTitle()
    {
        return "Unused Concepts";
    }

    /* (non-Javadoc)
     * @see gov.nih.nci.cadsr.sentinel.audits.AuditReport#getReportRows()
     */
    @Override
    public String[] getReportRows()
    {
        String[] used = _db.selectUsedConcepts();
        String[] concepts = _db.selectAllConcepts();
        
        int uNdx = 0;
        int cNdx = 0;
        Vector<String> unused = new Vector<String>();
        while (uNdx < used.length && cNdx < concepts.length)
        {
            if (used[uNdx].equals(concepts[cNdx]))
            {
                ++uNdx;
                ++cNdx;
            }
            else
            {
                unused.add(concepts[cNdx]);
                ++cNdx;
            }
        }
        used = null;
        for (; cNdx < concepts.length; ++cNdx)
        {
            unused.add(concepts[cNdx]);
        }
        
        String[] ids = new String[unused.size()];
        for (int i = 0; i < ids.length; ++i)
        {
            ids[i] = unused.get(i);
        }
        unused = null;

        return _db.reportUnusedConcepts(ids);
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
