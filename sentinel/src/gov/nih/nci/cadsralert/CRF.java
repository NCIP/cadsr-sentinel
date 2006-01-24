/*
 * Copyright (c) 2005 ScenPro, Inc.
 */

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsralert/CRF.java,v 1.1 2006-01-24 16:54:17 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsralert;

import java.io.PrintWriter;
import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * A Struts Action class implementation that supports requests made via the DSRAlertAPI.createAlert() method.
 * 
 * @author Larry Hebel Oct 11, 2005
 */

public class CRF extends Action
{
    /**
     * Constructor
     */
    public CRF()
    {
    }

    /**
     * Get the user name for the proxy.
     * 
     * @param request_ HTTP request.
     * @param proxy_ The proxy account
     * @param userid_ The user id for the database connection.
     * @param pswd_ The password for the user id.
     * @return The name of the proxy.
     */
    private String getUserName(HttpServletRequest request_, String proxy_, String userid_, String pswd_)
    {
        String name = null;
        DBAlert db = new DBAlert();
        int msgnum = db.open(request_, userid_, pswd_);
        if (msgnum == 0)
        {
            name = db.selectUserName(proxy_);
            db.close();
        }
        return name;
    }
    
    /**
     * Check for the existence of a processing bean and create it if needed.
     * 
     * @param form_ The form.
     * @param request_ The HTTP request.
     * @return The processing bean.
     */
    private AlertBean checkBean(CRFForm form_, HttpServletRequest request_)
    {
        String proxy = form_.getUser();
        String idseq = form_.getIdseq();
        if (proxy == null || idseq == null)
        {
            return null;
        }
        proxy = proxy.toUpperCase();

        HttpSession session = request_.getSession();
        AlertBean ub = (AlertBean) session.getAttribute(AlertBean._SESSIONNAME);
        int msgnum = 0;
        if (ub == null)
        {
            // Perform a special logon process without the user password.
            if (proxy.length() > 0)
            {
                // Use the Sentinel admin account.
                String accnt[] = new String[2];
                msgnum = LogonForm.initialize(proxy, request_, accnt);
                if (msgnum == 0)
                {
                    // Remember the admin account.
                    String userid = accnt[0];
                    String pswd = accnt[1];
                    
                    // Get the name of the proxy.
                    String userName = getUserName(request_, proxy, userid, pswd);
                    form_.setUserName(userName);
                    
                    // Provided we validated the proxy, create the processing bean.
                    if (userName != null && userName.length() != 0)
                    {       
                        // Create the bean used by all the logic.
                        ub = Logon.createBean(request_, userid, userName, pswd);
                        ub.setProxy(proxy);
                    }
                }
            }
        }
        else
        {
            // Use the existing processing bean to validate the proxy.
            String userName = getUserName(request_, proxy, ub.getUser(), ub.getPswd());
            form_.setUserName(userName);
            
            // Provided we validated the proxy, reset the processing bean. 
            if (userName == null || userName.length() == 0)
                ub = null;
            else
            {
                // Create the bean used by all the logic.
                ub.setProxy(proxy);
                ub.setUserName(userName);
            }
        }
        
        // Return a process bean if we have it.
        return ub;
    }
    
    /**
     * Action process for the user Login.
     * 
     * @param mapping_
     *        The action map defined in struts-config.xml.
     * @param form_
     *        The form bean for this jsp.
     * @param request_
     *        The servlet request object.
     * @param response_
     *        The servlet response object.
     * @return The action to forward to continue processing.
     */
    public ActionForward execute(ActionMapping mapping_, ActionForm form_,
        HttpServletRequest request_, HttpServletResponse response_)
    {
        // Check the requested output form. No reason to proceed if it's
        // an unsupported client.
        CRFForm form = (CRFForm) form_;
        int version = form.getVer(); 
        if (version < 1 || 1 < version)
        {
            response_.setStatus(HttpURLConnection.HTTP_NOT_IMPLEMENTED);
            return null;
        }

        // Get the database id.
        String list[] = new String[1];
        list[0] = form.getIdseq();

        int rc = -1;
        AlertBean ub = null;
        String alertName = "";
        while (true)
        {
            // Get a processing bean.
            ub = checkBean(form, request_);
            if (ub == null)
                break;
            
            // See if there is an Alert for this user which already watches the entity id.
            rc = 1;
            DBAlert db = new DBAlert();
            if (db.open(request_, ub.getUser(), ub.getPswd()) == 0)
            {
                while (true)
                {
                    // See if the Alert already exists.
                    rc = 2;
                    if ((alertName = db.checkQuery(list[0], ub.getProxy())) != null)
                        break;
    
                    // Check the entity id.
                    rc = 3;
                    String data[] = db.getACtype(list[0]);
                    if (data[0] == null)
                        break;
    
                    // Create the Alert because we couldn't find one. We set() and
                    // get() the name because the set() method will correct the
                    // name for length and content so the get() will report the
                    // correct name.
                    rc = 4;
                    ub.setWorking(new AlertRec(ub.getProxy(), ub.getUserName()));
                    alertName = db.selectAlertNameFormat();
                    if (alertName == null)
                        alertName = "Monitor " + data[0].toUpperCase() + " " + data[1];
                    else
                    {
                        alertName = alertName.replace("$ua_name$", ub.getProxy());
                        alertName = alertName.replace("$ac_type$", data[0].toUpperCase());
                        alertName = alertName.replace("$ac_name$", data[1]);
                    }
                    ub.getWorking().setName(alertName);
                    alertName = ub.getWorking().getName();
                    
                    // The id must be a certain type.
                    rc = 5;
                    if (data[0].compareTo("conte") == 0)
                    {
                        ub.getWorking().setContexts(list);
                    }
                    else if (data[0].compareTo("cs") == 0)
                    {
                        ub.getWorking().setSchemes(list);
                    }
                    else if (data[0].compareTo("csi") == 0)
                    {
                        ub.getWorking().setSchemeItems(list);
                        
                        // CSI has a special use for holding arbitrary AC's to monitor.
                        // If this is a reserved CSI, meaning it is owned by the RSVD.CS.CS_IDSEQ
                        // configured in the Tool Options for the Sentinel, then add the CS
                        // to the Alert Definition also.
                        String cs[] = new String[1];
                        cs[0] = db.selectCSfromReservedCSI(list[0]);
                        if (cs[0] != null)
                        {
                            ub.getWorking().setSchemes(cs);
                        }
                    }
                    else if (data[0].compareTo("qc") == 0)
                    {
                        ub.getWorking().setForms(list);
                    }
                    else if (data[0].compareTo("proto") == 0)
                    {
                        ub.getWorking().setProtocols(list);
                    }
                    else
                    {
                        break;
                    }
                    
                    // Be sure the owner is the proxy and not our generic id.
                    rc = 6;
                    if (db.insertAlert(ub.getWorking()) != 0)
                        break;
    
                    // Set the owner of the alert to the proxy.
                    rc = 7;
                    db.setOwner(ub.getWorking());
                    rc = 0;
                    break;
                }
                db.close();
            }
            break;
        }
        
        try
        {
            // Give a response the caller can understand.
            if (version == 1)
            {
                // Send back the name of the Alert.
                if (rc == 0 )
                {
                    response_.setStatus(HttpURLConnection.HTTP_CREATED);
                    PrintWriter out = response_.getWriter();
                    out.println(alertName);
                    out.close();
                }
                else if (rc == 2 )
                {
                    response_.setStatus(HttpURLConnection.HTTP_OK);
                    PrintWriter out = response_.getWriter();
                    out.println(alertName);
                    out.close();
                }
                
                // Something isn't kosher.
                else
                {
                    String txt;
                    if (ub == null)
                    {
                        txt = form.getUser() + " / " + list[0];
                    }
                    else
                    {
                        txt = ub.getProxy() + " / "
                        + ub.getUserName() + " / "
                        + ub.getUser() + " / "
                        + list[0];
                    }
                    response_.sendError(HttpURLConnection.HTTP_FORBIDDEN,
                        "Results: " + txt + " / " + rc);
                }
            }
        }
        catch (java.io.IOException ex)
        {
            System.err.println("DSRAlert CRF: " + ex.toString());
        }
        
        return null;
    }
}
