// Copyright (c) 2008 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tags/AlertRootTag.java,v 1.1 2008-11-07 14:11:10 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tags;

import gov.nih.nci.cadsr.sentinel.ui.AlertBean;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author lhebel
 *
 */
public class AlertRootTag extends TagSupport
{

    /** **/
    public String    _section;

    /** **/
    public AlertBean _ub;

    private static final long serialVersionUID = -2746276956700612707L;

    /**
     * 
     */
    public AlertRootTag()
    {
        super();
    }

    /**
     * Set section name.
     * 
     * @param section_
     *        Either "script" or "field".
     */
    public void setSection(String section_)
    {
        _section = section_;
    }

    /**
     * Process the end tag.
     * 
     * @return EVAL_PAGE to continue processing the JSP.
     */
    public int doEndTag()
    {
        HttpSession session = pageContext.getSession();
        _ub = (AlertBean) session.getAttribute(AlertBean._SESSIONNAME);
        if (_ub == null)
            return SKIP_PAGE;

        return doEnd();
    }

    /**
     * Process the end of the tag
     * 
     * @return what to do with the page from this point
     */
    public int doEnd()
    {
        return EVAL_PAGE;
    }
    
    /**
     * Standard tag release.
     */
    public void release()
    {
        _section = null;
        _ub = null;
        super.release();
    }
}
