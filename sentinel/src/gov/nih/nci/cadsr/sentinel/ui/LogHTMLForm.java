/*
 * Copyright (c) 2006 ScenPro, Inc.
 */

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/ui/LogHTMLForm.java,v 1.1 2006-09-08 22:32:55 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.ui;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * The form data required by LogHTML.
 * 
 * @author lhebel
 *
 */
public class LogHTMLForm extends ActionForm
{
    /**
     * Constructor
     *
     */
    public LogHTMLForm()
    {
    }
    
    /**
     * Get the idseq name
     * 
     * @return The form name.
     */
    public String getIdseq()
    {
        return _idseq;
    }

    /**
     * Set the idseq name
     * 
     * @param val_
     *        The idseq name.
     */
    public void setIdseq(String val_)
    {
        _idseq = val_;
    }
    
    /**
     * Set the version of the expected return format. This is used
     * to ensure backward compatibility so one service can provide
     * results to any version of call. This version dictates the
     * form and values of the reply expected by the caller.
     * 
     * @param val_ The version number.
     */
    public void setVersion(String val_)
    {
        _version = val_;
        _ver = Integer.parseInt(val_);
    }
    
    /**
     * Return the Version number. This is an internal value and does
     * not equal the version or release of the Sentinel Tool.
     * 
     * @return The version number.
     */
    public String getVersion()
    {
        return _version;
    }
    
    /**
     * Return the Version number. This is an internal value and does
     * not equal the version or release of the Sentinel Tool.
     * 
     * @return The version number.
     */
    public int getVer()
    {
        return _ver;
    }

    /**
     * Validate the page contents.
     * 
     * @param mapping_
     *        The action mapping from the struts-config.xml.
     * @param request_
     *        The servlet request object.
     * @return ActionErrors if something isn't quite right.
     */
    public ActionErrors validate(ActionMapping mapping_,
        HttpServletRequest request_)
    {
        ActionErrors errors = new ActionErrors();

        return errors;
    }

    // Class data.
    private String _idseq;
    
    private String _version;
    
    private int _ver;

    private static final long serialVersionUID = -2142089340998653909L;
}
