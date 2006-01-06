// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/com/scenpro/DSRAlert/RepRows.java,v 1.2 2006-01-06 16:14:26 hebell Exp $
// $Name: not supported by cvs2svn $

package com.scenpro.DSRAlert;

/**
 * This class tracks the records to be output on the report.
 * 
 * @author Larry Hebel
 * @version 1.0
 */

public class RepRows
{
    /**
     * Constructor.
     * 
     * @param indent_
     *        The indentations for this record.
     * @param rec_
     *        The data record.
     */
    public RepRows(int indent_, ACData rec_)
    {
        _indent = indent_;
        _rec = rec_;
    }

    /**
     * The indentation level shown as the Group number on the report.
     */
    public int    _indent;

    /**
     * The data record to write to the report.
     */
    public ACData _rec;
}