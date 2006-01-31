// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tool/ACDataFindRange.java,v 1.1.2.1 2006-01-31 18:33:45 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tool;

/**
 * This class tracks the range of indices matching a successful ACData list
 * search.
 * 
 * @author Larry Hebel
 * @version 1.0
 */

public class ACDataFindRange
{
    /**
     * Constructor.
     * 
     * @param min_
     *        The minimum index.
     * @param max_
     *        The maximum index.
     */
    public ACDataFindRange(int min_, int max_)
    {
        _min = min_;
        _max = max_;
    }

    /**
     * The starting index within an array of ACData types where a match was
     * found. If _min equals _max no match was found. And example control loop:
     * for (int ndx = _min; ndx < _max; ++ndx)...
     */
    public int _min;

    /**
     * The ending index within an array of ACData types after the match. If _min
     * equals _max no match was found. And example control loop: for (int ndx =
     * _min; ndx < _max; ++ndx)...
     */
    public int _max;
}