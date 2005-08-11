// Copyright (c) 2004 ScenPro, Inc.

package com.scenpro.DSRAlert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * The struts form bean for edit.jsp.
 * 
 * @author Larry Hebel
 */

public class EditForm extends ActionForm
{
    /**
     * Constructor.
     */
    public EditForm()
    {
        _nextScreen = Constants._ACTEDIT;
    }

    /**
     * Set the Date Filter.
     * 
     * @param val_
     *        The date filter value.
     */
    public void setInfoDateFilter(String val_)
    {
        _infoDateFilter = val_;
    }
    
    /**
     * Get the Date Filter.
     * 
     * @return The date filter value.
     */
    public String getInfoDateFilter()
    {
        return _infoDateFilter;
    }
    
    /**
     * Set the Context selections for the Criteria tab.
     * 
     * @param val_
     *        The Context IDSEQ values.
     */
    public void setInfoContext(String val_[])
    {
        _infoContext = val_;
    }

    /**
     * Get the Context selections.
     * 
     * @return The Context IDSEQ values.
     */
    public String[] getInfoContext()
    {
        return _infoContext;
    }

    /**
     * Set the Admin Components selections.
     * 
     * @param val_ The AC values.
     */
    public void setInfoACTypes(String val_[])
    {
        _infoACTypes = val_;
    }

    /**
     * Get the Admin Component selections.
     * 
     * @return The AC values.
     */
    public String[] getInfoACTypes()
    {
        return _infoACTypes;
    }
    
    /**
     * Set the Possible Users for the Report Recipients.
     * 
     * @param val_
     *        The user id list.
     */
    public void setPropUsers(String val_[])
    {
        _propUsers = val_;
    }

    /**
     * Get the Users.
     * 
     * @return The user id list.
     */
    public String[] getPropUsers()
    {
        return _propUsers;
    }

    /**
     * TBD
     * 
     * @param val_
     */
    public void setInfoSearchFor(String val_[])
    {
        _infoSearchFor = val_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String[] getInfoSearchFor()
    {
        return _infoSearchFor;
    }

    /**
     * TBD
     * 
     * @param val_
     */
    public void setInfoConceptDomain(String val_[])
    {
        _infoConceptDomain = val_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String[] getInfoConceptDomain()
    {
        return _infoConceptDomain;
    }

    /**
     * Set the Classification Schemes selections on the Criteria tab.
     * 
     * @param val_
     *        The CS id list.
     */
    public void setInfoSchemes(String val_[])
    {
        _infoSchemes = val_;
    }

    /**
     * Set the Classification Scheme Items selections on the Criteria tab.
     * 
     * @param val_
     *        The CSI id list.
     */
    public void setInfoSchemeItems(String val_[])
    {
        _infoSchemeItems = val_;
    }

    /**
     * Get the Classification Schemes selections.
     * 
     * @return The CS id list.
     */
    public String[] getInfoSchemes()
    {
        return _infoSchemes;
    }

    /**
     * Get the Classification Scheme Items selections.
     * 
     * @return The CS id list.
     */
    public String[] getInfoSchemeItems()
    {
        return _infoSchemeItems;
    }

    /**
     * Set the Forms/Template selections on the Criteria tab.
     * 
     * @param val_
     *        The F/T id list.
     */
    public void setInfoForms(String val_[])
    {
        _infoForms = val_;
    }

    /**
     * Get the Forms/Template selections.
     * 
     * @return The F/T id list.
     */
    public String[] getInfoForms()
    {
        return _infoForms;
    }

    /**
     * Set the Modified By selections on the Criteria tab.
     * 
     * @param val_
     *        The Modifier id list.
     */
    public void setInfoModifier(String val_[])
    {
        _infoModifier = val_;
    }

    /**
     * Get the Modified By selections.
     * 
     * @return The Modifier id list.
     */
    public String[] getInfoModifier()
    {
        return _infoModifier;
    }

    /**
     * Set the Created By selections on the Criteria tab.
     * 
     * @param val_
     *        The Creator id list.
     */
    public void setInfoCreator(String val_[])
    {
        _infoCreator = val_;
    }

    /**
     * Get the Created By selections.
     * 
     * @return The Creator id list.
     */
    public String[] getInfoCreator()
    {
        return _infoCreator;
    }

    /**
     * TBD
     * 
     * @param val_
     */
    public void setInfoRegStatus(String val_[])
    {
        _infoRegStatus = val_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String[] getInfoRegStatus()
    {
        return _infoRegStatus;
    }

    /**
     * Set the Registration Status selections on the Monitors tab.
     * 
     * @param val_
     *        The RS id list.
     */
    public void setActRegStatus(String val_[])
    {
        _actRegStatus = val_;
    }

    /**
     * Get the Registration Status selections.
     * 
     * @return The RS id list.
     */
    public String[] getActRegStatus()
    {
        return _actRegStatus;
    }

    /**
     * TBD
     * 
     * @param val_
     */
    public void setInfoWorkflowStatus(String val_[])
    {
        _infoWorkflowStatus = val_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String[] getInfoWorkflowStatus()
    {
        return _infoWorkflowStatus;
    }

    /**
     * Set the Workflow Status selections on the Monitors tab.
     * 
     * @param val_
     *        The WFS id list.
     */
    public void setActWorkflowStatus(String val_[])
    {
        _actWorkflowStatus = val_;
    }

    /**
     * Get the Workflow Status selections.
     * 
     * @return The WFS id list.
     */
    public String[] getActWorkflowStatus()
    {
        return _actWorkflowStatus;
    }

    /**
     * TBD
     * 
     * @param val_
     */
    public void setInfoSearchIn(String val_)
    {
        _infoSearchIn = val_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String getInfoSearchIn()
    {
        return _infoSearchIn;
    }

    /**
     * TBD
     * 
     * @param value_
     */
    public void setRepAttributes(String value_[])
    {
        _repAttributes = value_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String[] getRepAttributes()
    {
        return _repAttributes;
    }

    /**
     * Set the Monthly Auto Run Frequency on the Properties tab.
     * 
     * @param value_
     *        A value 1 to 31.
     */
    public void setFreqMonthly(String value_)
    {
        _freqMonthly = value_;
    }

    /**
     * Get the Monthly Auto Run Frequency.
     * 
     * @return A value 1 to 31.
     */
    public String getFreqMonthly()
    {
        return _freqMonthly;
    }

    /**
     * Set the Weekly Auto Run Frequency on the Properties tab.
     * 
     * @param value_
     *        A value 1 to 7.
     */
    public void setFreqWeekly(String value_)
    {
        _freqWeekly = value_;
    }

    /**
     * Get the Weekly Auto Run Frequency.
     * 
     * @return A value 1 to 7.
     */
    public String getFreqWeekly()
    {
        return _freqWeekly;
    }

    /**
     * Set the Report Recipients on the Report Details tab.
     * 
     * @param value_
     *        The recipient id list.
     */
    public void setPropRecipients(String value_[])
    {
        _propRecipients = value_;
    }

    /**
     * Get the Report Recipients.
     * 
     * @return The recipient id list.
     */
    public String[] getPropRecipients()
    {
        return _propRecipients;
    }

    /**
     * TBD
     * 
     * @param value_
     */
    public void setInfoVerNum(String value_)
    {
        _infoVerNum = value_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String getInfoVerNum()
    {
        return _infoVerNum;
    }

    /**
     * TBD
     * 
     * @param value_
     */
    public void setInfoVersion(String value_)
    {
        _infoVersion = value_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String getInfoVersion()
    {
        return _infoVersion;
    }

    /**
     * TBD
     * 
     * @param value_
     */
    public void setInfoContextUse(String value_)
    {
        _infoContextUse = value_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String getInfoContextUse()
    {
        return _infoContextUse;
    }

    /**
     * TBD
     * 
     * @param value_
     */
    public void setActDependantChg(String value_)
    {
        _actDependantChg = value_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String getActDependantChg()
    {
        return _actDependantChg;
    }

    /**
     * TBD
     * 
     * @param value_
     */
    public void setActContextUse(String value_)
    {
        _actContextUse = value_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String getActContextUse()
    {
        return _actContextUse;
    }

    /**
     * Set the Status Reason Description on the Properties tab.
     * 
     * @param value_
     *        The description.
     */
    public void setPropStatusReason(String value_)
    {
        _propStatusReason = value_;
    }

    /**
     * Get the Status Reason Description.
     * 
     * @return The description.
     */
    public String getPropStatusReason()
    {
        return _propStatusReason;
    }

    /**
     * TBD
     * 
     * @param value_
     */
    public void setActVDT(String value_)
    {
        _actVDT = value_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String getActVDT()
    {
        return _actVDT;
    }

    /**
     * Set the Version Number on the Monitors tab.
     * 
     * @param value_
     *        The version number.
     */
    public void setActVerNum(String value_)
    {
        _actVerNum = value_;
    }

    /**
     * Get the Version Number value.
     * 
     * @return The version number.
     */
    public String getActVerNum()
    {
        return _actVerNum;
    }

    /**
     * Set the Version Option on the Monitors tab.
     * 
     * @param value_
     *        The version option.
     */
    public void setActVersion(String value_)
    {
        _actVersion = value_;
    }

    /**
     * Get the Version Option value.
     * 
     * @return The version option.
     */
    public String getActVersion()
    {
        return _actVersion;
    }

    /**
     * TBD
     * 
     * @param value_
     */
    public void setPropEmail(String value_)
    {
        _propEmail = value_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String getPropEmail()
    {
        return _propEmail;
    }

    /**
     * TBD
     * 
     * @param value_
     */
    public void setFreqAck(String value_)
    {
        _freqAck = value_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String getFreqAck()
    {
        return _freqAck;
    }

    /**
     * Set the Send Empty Reports on the Report Details tab.
     * 
     * @param value_
     *        The send flag.
     */
    public void setFreqEmpty(String value_)
    {
        _freqEmpty = value_;
    }

    /**
     * Get the Send Empty Reports flag.
     * 
     * @return The send flag.
     */
    public String getFreqEmpty()
    {
        return _freqEmpty;
    }

    /**
     * Set the Auto Run Active Status End Date on the Properties tab.
     * 
     * @param value_
     *        The end date.
     */
    public void setPropEndDate(String value_)
    {
        _propEndDate = value_;
    }

    /**
     * Get the Auto Run Active Status End Date value.
     * 
     * @return The end date.
     */
    public String getPropEndDate()
    {
        return _propEndDate;
    }

    /**
     * Set the Auto Run Active Status Begin Date on the Properties tab.
     * 
     * @param value_
     *        The begin date.
     */
    public void setPropBeginDate(String value_)
    {
        _propBeginDate = value_;
    }

    /**
     * Get the Auto Run Active Status Begin Date value.
     * 
     * @return The begin date.
     */
    public String getPropBeginDate()
    {
        return _propBeginDate;
    }

    /**
     * Set the Auto Run Status Flag on the Properties tab.
     * 
     * @param value_
     *        The status flag.
     */
    public void setPropStatus(String value_)
    {
        _propStatus = value_;
    }

    /**
     * Get the Auto Run Status Flag value.
     * 
     * @return The status flag.
     */
    public String getPropStatus()
    {
        return _propStatus;
    }

    /**
     * Set the Auto Run Day Frequency on the Properties tab.
     * 
     * @param value_
     *        The day frequency.
     */
    public void setFreqUnit(String value_)
    {
        _freqUnit = value_;
    }

    /**
     * Get the Auto Run Day Frequency value.
     * 
     * @return The day frequency.
     */
    public String getFreqUnit()
    {
        return _freqUnit;
    }

    /**
     * TBD
     * 
     * @param value_
     */
    public void setInfoVDTN(String value_)
    {
        _infoVDTN = value_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String getInfoVDTN()
    {
        return _infoVDTN;
    }

    /**
     * TBD
     * 
     * @param value_
     */
    public void setInfoVDTE(String value_)
    {
        _infoVDTE = value_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String getInfoVDTE()
    {
        return _infoVDTE;
    }

    /**
     * TBD
     * 
     * @param value_
     */
    public void setInfoSearchTerm(String value_)
    {
        _infoSearchTerm = value_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String getInfoSearchTerm()
    {
        return _infoSearchTerm;
    }

    /**
     * TBD
     * 
     * @param value_
     */
    public void setActAdminNew(String value_)
    {
        _actAdminNew = value_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String getActAdminNew()
    {
        return _actAdminNew;
    }

    /**
     * TBD
     * 
     * @param value_
     */
    public void setActAdminDel(String value_)
    {
        _actAdminDel = value_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String getActAdminDel()
    {
        return _actAdminDel;
    }

    /**
     * TBD
     * 
     * @param value_
     */
    public void setActAdminCopy(String value_)
    {
        _actAdminCopy = value_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String getActAdminCopy()
    {
        return _actAdminCopy;
    }

    /**
     * TBD
     * 
     * @param value_
     */
    public void setActAdminChg(String value_)
    {
        _actAdminChg = value_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String getActAdminChg()
    {
        return _actAdminChg;
    }

    /**
     * Set the Report Introduction Static Text on the Report Details tab.
     * 
     * @param value_
     *        The static intro.
     */
    public void setPropIntro(String value_)
    {
        _propIntro = value_;
    }

    /**
     * Get the Report Introduction Static Text.
     * 
     * @return The static intro.
     */
    public String getPropIntro()
    {
        return _propIntro;
    }

    /**
     * TBD
     * 
     * @param value_
     */
    public void setRepStyle(String value_)
    {
        _repStyle = value_;
    }

    /**
     * TBD
     * 
     * @return TBD
     */
    public String getRepStyle()
    {
        return _repStyle;
    }

    /**
     * Set the Include Properties flag on the Report Details tab.
     * 
     * @param value_
     *        The include flag.
     */
    public void setRepIncProp(String value_)
    {
        _repIncProp = value_;
    }

    /**
     * Get the Include Properties flag.
     * 
     * @return The include flag.
     */
    public String getRepIncProp()
    {
        return _repIncProp;
    }

    /**
     * Set the Alert Modified Date on the Properties tab.
     * 
     * @param value_
     *        The modified date.
     */
    public void setPropModifyDate(String value_)
    {
        _propModifyDate = value_;
    }

    /**
     * Get the Alert Modified Date.
     * 
     * @return The modified date.
     */
    public String getPropModifyDate()
    {
        return _propModifyDate;
    }

    /**
     * Set the Alert Last Auto Run Date on the Properties tab.
     * 
     * @param value_
     *        The date.
     */
    public void setPropLastRunDate(String value_)
    {
        _propLastRunDate = value_;
    }

    /**
     * Get the Alert Last Auto Run Date.
     * 
     * @return The date.
     */
    public String getPropLastRunDate()
    {
        return _propLastRunDate;
    }

    /**
     * Set the Alert Create Date on the Properties tab.
     * 
     * @param value_
     *        The date.
     */
    public void setPropCreateDate(String value_)
    {
        _propCreateDate = value_;
    }

    /**
     * Get the Alert Create Date.
     * 
     * @return The date.
     */
    public String getPropCreateDate()
    {
        return _propCreateDate;
    }

    /**
     * Set the Alert Created By User ID on the Properties tab.
     * 
     * @param value_
     *        The user id.
     */
    public void setPropCreator(String value_)
    {
        _propCreator = value_;
    }

    /**
     * Get the Alert Created By User ID.
     * 
     * @return The user id.
     */
    public String getPropCreator()
    {
        return _propCreator;
    }

    /**
     * Set the next action/screen name to transfer control.
     * 
     * @param value_
     *        The action/screen name.
     */
    public void setNextScreen(String value_)
    {
        _nextScreen = value_;
    }

    /**
     * Get the next action/screen name.
     * 
     * @return The action/screen name.
     */
    public String getNextScreen()
    {
        return _nextScreen;
    }

    /**
     * Set the Sentinel Name on the Properties tab.
     * 
     * @param value_
     *        The name text.
     */
    public void setPropName(String value_)
    {
        _propName = value_;
    }

    /**
     * Get the Sentinel Name.
     * 
     * @return The name text.
     */
    public String getPropName()
    {
        return _propName;
    }

    /**
     * Set the Summary Description on the Properties tab.
     * 
     * @param value_
     *        The description.
     */
    public void setPropDesc(String value_)
    {
        _propDesc = value_;
    }

    /**
     * Get the Summary Description.
     * 
     * @return The description.
     */
    public String getPropDesc()
    {
        return _propDesc;
    }

    /**
     * Validate the content of the Edit Screen.
     * 
     * @param mapping_
     *        The action map defined for Edit.
     * @param request_
     *        The servlet request object.
     * @return Any errors found.
     */
    public ActionErrors validate(ActionMapping mapping_,
        HttpServletRequest request_)
    {
        ActionErrors errors = new ActionErrors();

        HttpSession session = request_.getSession();
        AlertBean ub = (AlertBean) session.getAttribute(AlertBean._SESSIONNAME);
        if (ub == null)
        {
            // Can't do this without a bean.
            errors.add("bean", new ActionMessage("error.nobean"));
            return errors;
        }

        if (_nextScreen.equals(Constants._ACTSAVE))
        {
            // The form is self validating. Meaning we handle as much as
            // possible in the UI to avoid the
            // load on the server and the delay in the round trip. Also the
            // alert definitions are written
            // such that only specific permitted values are used, consequently
            // if something other than
            // our JSP interface creates the definition it will still process.
            // In the case of unexpected
            // values, the defaults are used. This makes for a very bullet proof
            // process. And as the
            // Alert only generates reports of activities and does not change
            // any caDSR content it is a
            // safe approach. Should the user not receive the desired output, it
            // is a simple matter of
            // tracing back into the permitted values in the alert tables and
            // then to the user interface.
        }

        return errors;
    }

    // Class data elements.
    private String           _nextScreen;

    private String           _propName;

    private String           _propDesc;

    private String           _propCreator;

    private String           _propCreateDate;

    private String           _propLastRunDate;

    private String           _propModifyDate;

    private String           _repIncProp;

    private String           _repStyle;

    private String           _propIntro;

    private String           _actAdminChg;

    private String           _actAdminNew;

    private String           _actAdminDel;

    private String           _actAdminCopy;

    private String           _infoSearchTerm;

    private String           _infoVDTE;

    private String           _infoVDTN;

    private String           _freqUnit;

    private String           _propStatus;

    private String           _propBeginDate;

    private String           _propEndDate;

    private String           _freqEmpty;

    private String           _freqAck;

    private String           _propEmail;

    private String           _actVersion;

    private String           _actVerNum;

    private String           _actVDT;

    private String           _propStatusReason;

    private String           _actContextUse;

    private String           _actDependantChg;

    private String           _infoContextUse;

    private String           _infoVersion;

    private String           _infoVerNum;

    private String           _propRecipients[];

    private String           _freqWeekly;

    private String           _freqMonthly;

    private String           _repAttributes[];

    private String           _infoSearchIn;

    private String           _actWorkflowStatus[];

    private String           _infoWorkflowStatus[];

    private String           _actRegStatus[];

    private String           _infoRegStatus[];

    private String           _infoCreator[];

    private String           _infoModifier[];

    private String           _infoSchemes[];

    private String           _infoSchemeItems[];

    private String           _infoForms[];

    private String           _infoConceptDomain[];

    private String           _infoSearchFor[];

    private String           _propUsers[];

    private String           _infoContext[];
    
    private String           _infoDateFilter;
    
    private String           _infoACTypes[];

    private static final long serialVersionUID = 1111591331986981868L;
}