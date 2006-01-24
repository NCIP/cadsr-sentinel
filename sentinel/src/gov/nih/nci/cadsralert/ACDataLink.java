// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsralert/ACDataLink.java,v 1.1 2006-01-24 16:54:17 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsralert;

import java.util.Stack;

/**
 * This class provides a wrapper for the ACData lists. The lists are chained
 * together into specific configurations which define the possible associations
 * between each object.
 * 
 * @author Larry Hebel
 * @version 1.0
 */

public class ACDataLink
{
    /**
     * Constructor.
     * 
     * @param list_
     *        The ACData list.
     * @param leaf_
     *        A flag indicating if this list represents a leaf or successful end
     *        node.
     */
    public ACDataLink(ACData list_[], boolean leaf_)
    {
        _leaf = leaf_;
        setLink(list_);
    }

    /**
     * Constructor.
     * 
     * @param list_
     *        The ACData list.
     */
    public ACDataLink(ACData list_[])
    {
        _leaf = false;
        setLink(list_);
    }

    /**
     * Set the internal list pointer and always guarantee a non-null value.
     * 
     * @param list_
     *        The ACData list.
     */
    private void setLink(ACData list_[])
    {
        if (list_ == null)
            _list = new ACData[0];
        else
            _list = list_;
        _links = new ACDataLink[0];
    }

    /**
     * Add another link in this chain. The chain is built in atomic increments.
     * Any one link only refers to links which it can immediately reach. For
     * example, to get from a VD to a Context, a chain of links must be created
     * in the order VD, DE, Context.
     * 
     * @param related_
     *        The next link in the chain.
     */
    public void add(ACDataLink related_)
    {
        for (int ndx = 0; ndx < _links.length; ++ndx)
        {
            if (_links[ndx] == related_)
                return;
        }

        ACDataLink temp[] = new ACDataLink[_links.length + 1];
        int ndx;
        for (ndx = 0; ndx < _links.length; ++ndx)
            temp[ndx] = _links[ndx];
        temp[ndx] = related_;
        _links = temp;
    }

    /**
     * Get the maximum range for the list controlled by this link. This always
     * equates to "list.length".
     * 
     * @return The range object with min set to zero (0) and max set to
     *         list.length.
     */
    public ACDataFindRange getRange()
    {
        return new ACDataFindRange(0, _list.length);
    }

    /**
     * Check the link being processed and if it is a leaf node dump the
     * appropriate list records to the stack.
     * 
     * @param results_
     *        The output stack.
     * @param indent_
     *        The indentation level.
     * @param fr_
     *        The range of list elements to dump.
     * @return true if the method pushed records on the stack, otherwise false.
     */
    private boolean checkLeaf(Stack<RepRows> results_, int indent_, ACDataFindRange fr_)
    {
        if (_leaf)
        {
            for (int ndx = fr_._min; ndx < fr_._max; ++ndx)
                results_.push(new RepRows(indent_, _list[ndx]));
            return true;
        }
        return false;
    }

    /**
     * This is one of a pair of methods to recursively traverse the link chains.
     * 
     * @param id_
     *        The idseq being searched for in the related links.
     * @param results_
     *        The output stack.
     * @param indent_
     *        The indentation level.
     * @return true if the method pushed records on the stack, otherwise false.
     */
    private boolean follow2(String id_, Stack<RepRows> results_, int indent_)
    {
        boolean rc = false;
        ACDataFindRange fr;
        for (int ndx = 0; ndx < _links.length; ++ndx)
        {
            fr = ACData.findRelated(id_, _links[ndx]._list);
            if (fr._min < fr._max)
            {
                if (_links[ndx].checkLeaf(results_, indent_ + 1, fr))
                    rc = true;
                else if (_links[ndx].follow(results_, indent_ + 1, fr))
                    rc = true;
            }
        }
        return rc;
    }

    /**
     * This is one of a pair of methods to recursively traverse the link chains.
     * 
     * @param results_
     *        The output stack.
     * @param indent_
     *        The indentation level.
     * @param fr_
     *        The range of elements in the list to be used for the search.
     * @return true if the method pushed records on the stack, otherwise false.
     */
    public boolean follow(Stack<RepRows> results_, int indent_, ACDataFindRange fr_)
    {
        boolean rc = false;
        for (int ndx = fr_._min; ndx < fr_._max; ++ndx)
        {
            if (follow2(_list[ndx].getIDseq(), results_, indent_))
            {
                rc = true;
                results_.push(new RepRows(indent_, _list[ndx]));
            }
        }
        return rc;
    }

    // Class data elements.
    private ACData     _list[];

    private boolean    _leaf;

    private ACDataLink _links[];
}