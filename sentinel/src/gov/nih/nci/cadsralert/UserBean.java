// Copyright (c) 2002 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsralert/UserBean.java,v 1.1 2006-01-24 16:54:17 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsralert;

// *********************************************
// WARNING: This class was copied from package com.scenpro.NCICuration to allow
// the Curation Tool a means to launch the Sentinel Tool for caCORE 3.0.
// *********************************************

import java.io.Serializable;

/**
 * <b>Not currently used by the Sentinel Tool but planned for inter-tool
 * communication with the single login feature. </b>
 * <p>
 * The UserBean encapsulates the user information and will be stored in the
 * session after the user has logged on.
 * <P>
 * 
 * @author Michael Holck
 * @version 2.1
 */

/*
 * The CDE Curation Tool Software License, Version 1.0 Copyright 2001-2003
 * ScenPro, Inc. This software was developed in conjunction with the National
 * Cancer Institute, and so to the extent government employees are co-authors,
 * any rights in such works shall be subject to Title 17 of the United States
 * Code, section 105. Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met: 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the disclaimer of Article 3, below.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. 2. The end-user
 * documentation included with the redistribution, if any, must include the
 * following acknowledgment: "This product includes software developed by
 * ScenPro, Inc. and the National Cancer Institute." If no such end-user
 * documentation is to be included, this acknowledgment shall appear in the
 * software itself, wherever such third-party acknowledgments normally appear.
 * 3. The names "The National Cancer Institute", "NCI" and "ScenPro" must not be
 * used to endorse or promote products derived from this software. 4. This
 * license does not authorize the incorporation of this software into any
 * proprietary programs. This license does not authorize the recipient to use
 * any trademarks owned by either the National Cancer Institute or ScenPro, Inc.
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, SCENPRO, OR THEIR AFFILIATES BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

public final class UserBean implements Serializable
{
    // Attributes
    private String  m_Password;

    private String  m_username;

    private String  m_DBAppContext;

    private boolean m_superuser;

    /**
     * Constructor
     */
    public UserBean()
    {
    }

    /**
     * The getPassword method returns the Password for this bean.
     * 
     * @return String The Password
     */
    public String getPassword()
    {
        return m_Password;
    }

    /**
     * The setPassword method sets the Password for this bean.
     * 
     * @param Password
     *        The Password to set
     */
    public void setPassword(String Password)
    {
        m_Password = Password;
    }

    /**
     * The getUsername method returns the username for this bean.
     * 
     * @return String The username
     */
    public String getUsername()
    {
        return m_username;
    }

    /**
     * The setUsername method sets the username for this bean.
     * 
     * @param username
     *        The username to set
     */
    public void setUsername(String username)
    {
        m_username = username;
    }

    /**
     * The getDBAppContext method returns the DBAppContext for this bean.
     * 
     * @return String The DBAppContext
     */
    public String getDBAppContext()
    {
        return m_DBAppContext;
    }

    /**
     * The setDBAppContext method sets the DBAppContext for this bean.
     * 
     * @param sDBAppContext
     *        The username to set
     */
    public void setDBAppContext(String sDBAppContext)
    {
        m_DBAppContext = sDBAppContext;
    }

    /**
     * The isSuperuser method returns the superuser status for this bean.
     * 
     * @return boolean Whether this user is a superuser or not
     */
    public boolean isSuperuser()
    {
        return m_superuser;
    }

    /**
     * The setSuperuser method sets the superuser status for this bean.
     * 
     * @param isSuperuser
     *        The superuser status to set
     */
    public void setSuperuser(boolean isSuperuser)
    {
        m_superuser = isSuperuser;
    }

    private static final long serialVersionUID = 8123556646776080906L;
}
