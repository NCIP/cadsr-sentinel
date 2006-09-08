// Copyright (c) 2004 ScenPro, Inc.

// $Header: /share/content/gforge/sentinel/sentinel/src/gov/nih/nci/cadsr/sentinel/tool/AlertRec.java,v 1.11 2006-09-08 22:32:54 hebell Exp $
// $Name: not supported by cvs2svn $

package gov.nih.nci.cadsr.sentinel.tool;

import gov.nih.nci.cadsr.sentinel.database.DBAlert;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * The Alert definition specifics. This contains all the information for a
 * specific alert while working in memory. The methods store and render using
 * standard Java primitive types.
 * 
 * @author Larry Hebel
 */
public class AlertRec
{
    /**
     * Constructor with a known creator.
     * 
     * @param id_
     *        The user id creating this Alert.
     * @param creator_
     *        The user name corresponding to the id.
     */
    public AlertRec(String id_, String creator_)
    {
        setRec(id_);
        setCreatorName(creator_);
    }

    /**
     * Constructor for a default record and unknown creator.
     */
    public AlertRec()
    {
        setRec(null);
    }

    /**
     * Copy a string array and create a new identical one. This guarantees all
     * memory references are new.
     * 
     * @param v_
     *        The array to copy.
     * @return The new copy.
     */
    private String[] copy(String v_[])
    {
        if (v_ == null)
            return null;

        String temp[] = new String[v_.length];
        for (int ndx = 0; ndx < temp.length; ++ndx)
            temp[ndx] = new String(v_[ndx]);
        return temp;
    }

    /**
     * Copy a string to new memory.
     * 
     * @param v_
     *        The string to copy.
     * @return The new string.
     */
    private String copy(String v_)
    {
        if (v_ == null)
            return null;
        return new String(v_);
    }

    /**
     * Copy a time stamp. This guarantees the time is preset so the copy matches
     * the original value.
     * 
     * @param v_
     *        The Timestamp to copy.
     * @return The new Timestamp.
     */
    private Timestamp copy(Timestamp v_)
    {
        if (v_ == null)
            return null;
        return new Timestamp(v_.getTime());
    }

    /**
     * Create an exact duplicate (copy) of this instance. This is used to submit
     * a manual run of an Alert as the user is not required to Save changes to
     * an Alert and we want to run using the on screen settings. It also allows
     * the user the flexibility to test "what if" situations before saving.
     * 
     * @param rec_ The object to duplicate.
     */
    public AlertRec(AlertRec rec_)
    {
        // If any new data elements are added to the object they MUST be added
        // to this
        // method to be copied!

        _alertRecNum = copy(rec_._alertRecNum);
        _reportRecNum = copy(rec_._reportRecNum);
        _searchIn = copy(rec_._searchIn);
        _name = copy(rec_._name);
        _summary = copy(rec_._summary);
        _term = copy(rec_._term);
        _creator = copy(rec_._creator);
        _creatorName = copy(rec_._creatorName);
        _inactiveReason = copy(rec_._inactiveReason);
        _infoVerNum = copy(rec_._infoVerNum);
        _actVerNum = copy(rec_._actVerNum);
        _intro = copy(rec_._intro);
        _cdate = copy(rec_._cdate);
        _mdate = copy(rec_._mdate);
        _adate = copy(rec_._adate);
        _rdate = copy(rec_._rdate);
        _start = copy(rec_._start);
        _end = copy(rec_._end);
        _day = rec_._day;
        _active = rec_._active;
        _freq = rec_._freq;
        _infoAssocLvl = rec_._infoAssocLvl;
        _incPropSect = rec_._incPropSect;
        _reportStyle = rec_._reportStyle;
        _reportEmpty = rec_._reportEmpty;
        _reportAck = rec_._reportAck;
        _vdte = rec_._vdte;
        _vdtn = rec_._vdtn;
        _iuse = rec_._iuse;
        _ause = rec_._ause;
        _iVersion = rec_._iVersion;
        _aVersion = rec_._aVersion;
        _related = rec_._related;
        _adminChg = rec_._adminChg;
        _adminNew = rec_._adminNew;
        _adminCopy = rec_._adminCopy;
        _adminDel = rec_._adminDel;
        _avdt = rec_._avdt;
        _attrs = copy(rec_._attrs);
        _recipients = copy(rec_._recipients);
        _aWorkflow = copy(rec_._aWorkflow);
        _cWorkflow = copy(rec_._cWorkflow);
        _aRegis = copy(rec_._aRegis);
        _searchAC = copy(rec_._searchAC);
        _forms = copy(rec_._forms);
        _schemes = copy(rec_._schemes);
        _protocols = copy(rec_._protocols);
        _schemeItems = copy(rec_._schemeItems);
        _domains = copy(rec_._domains);
        _creators = copy(rec_._creators);
        _modifiers = copy(rec_._modifiers);
        _contexts = copy(rec_._contexts);
        _cRegStatus = copy(rec_._cRegStatus);
        _ACTypes = copy(rec_._ACTypes);
        _dateFilter = rec_._dateFilter;
    }

    /**
     * Common initialization of class data elements.
     * 
     * @param creator_
     *        The identifier for the creator of this alert.
     */
    private void setRec(String creator_)
    {
        setRecNumNull();
        setName("New");
        setFreq('D');
        setCdate((Timestamp) null);
        setMdate((Timestamp) null);
        setAdate((Timestamp) null);
        setActive('A');
        setDay(0);
        setCreator(creator_);
        setInactiveReason(null);
        setInfoVerNum("");
        setIAssocLvl(0);
        setIntro(null, false);
        setIncPropSect(false);
        setReportStyle(null);
        setReportEmpty(null);
        setReportAck(null);
        setVDTE(true);
        setVDTN(true);
        setIUse(null);
        setAUse(null);
        setTerm(null);
        setIVersion(null);
        setRelated(true);
        setAdminChg(true);
        setAdminNew(true);
        setAdminCopy(true);
        setAdminDel(true);
        setStart((Timestamp) null);
        setEnd((Timestamp) null);
        setAVDT(null);
        setSearchIn(null);
        setAttrs(null);

        String temp[] = new String[1];
        temp[0] = creator_;
        setRecipients(temp);

        setSearchAC(null);
        setDomains(null);
        clearQuery();
    }

    /**
     * Clear the data elements which hold the query related information.
     */
    public void clearQuery()
    {
        setSummary("");
        setContexts(null);
        setCRegStatus(null);
        setACTypes(null);
        setProtocols(null);
        setForms(null);
        setSchemes(null);
        setSchemeItems(null);
        setCreators(null);
        setModifiers(null);
        setAWorkflow(null);
        setCWorkflow(null);
        setARegis(null);
        setAVersion(null);
        setActVerNum("");
        setDateFilter(DBAlert._DATECM);
    }

    /**
     * Reset data members based on dependancies with other data
     * members.  This will resolve any data interrelationships after
     * all updates are made to the Alert Definition.
     *
     */
    public void setDependancies()
    {
        if (_freq == 'D')
            _day = 0;
    }

    /**
     * Format an integer to a String and guarantee at least 2 digits by
     * prefixing a "0" if necessary.
     * 
     * @param x_
     * @return integer in String format
     */
    static private String formatInt(int x_)
    {
        if (x_ < 10)
            return "0" + String.valueOf(x_);
        return String.valueOf(x_);
    }

    /**
     * Return the date as a string in the form mm/dd/yyyy. The time is included
     * only if the caller requests it. Time is returned in AM/PM format.
     * 
     * @param var_
     *        The desired date.
     * @param flag_
     *        true for the date and time, false for the date only.
     * @return The date as a string.
     */
    static public String dateToString(Timestamp var_, boolean flag_)
    {
        return dateToString(var_, flag_, false);
    }

    /**
     * Return the date as a string in the form mm/dd/yyyy. The time is
     * optionally included. Time is returned in AM/PM format. The output can be
     * a simple string or formated with HTML tags.
     * 
     * @param var_
     *        The time stamp to format.
     * @param flag_
     *        true for the date and time, false for the date only.
     * @param html_
     *        true to include HTML tags, false for a simple string.
     * @return String date in String format
     */
    static public String dateToString(Timestamp var_, boolean flag_,
        boolean html_)
    {
        // Be sure we have an object.
        if (var_ == null)
            return (html_) ? "<i>&lt;null&gt;</i>" : "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(var_);

        // Always render in Month/Day/Year format.
        String date = (formatInt(cal.get(Calendar.MONTH) + 1)) + "/"
            + formatInt(cal.get(Calendar.DATE)) + "/"
            + formatInt(cal.get(Calendar.YEAR));

        // Do we include time?
        if (flag_)
        {
            date = date + ((html_) ? "&nbsp;" : " ");
            if (cal.get(Calendar.HOUR) == 0)
                date = date + "12";
            else
                date = date + cal.get(Calendar.HOUR);

            // Put is all together.
            date = date + ":" + formatInt(cal.get(Calendar.MINUTE)) + ":"
                + formatInt(cal.get(Calendar.SECOND))
                + ((html_) ? "&nbsp;" : " ")
                + ((cal.get(Calendar.AM_PM) == 0) ? "AM" : "PM");
        }
        return date;
    }

    /**
     * Return the End Date of the Active date range. An empty string
     * (String.length() == 0) indicates no end.
     * 
     * @return The date is in the form m/d/yyyy.
     */
    public String getEDate()
    {
        return dateToString(_end, false);
    }

    /**
     * Return the Start Date of the Active date range. An empty string
     * (String.length() == 0) indicates the alert is active to the end date.
     * 
     * @return The date is in the form m/d/yyyy.
     */
    public String getSDate()
    {
        return dateToString(_start, false);
    }

    /**
     * Return the Creation Date of the alert.
     * 
     * @return The date is in the form m/d/yyyy.
     */
    public String getCDate()
    {
        return dateToString(_cdate, true);
    }

    /**
     * Return the Last Auto Run Date of the alert.
     * 
     * @return The date is in the form m/d/yyyy.
     */
    public String getADate()
    {
        return dateToString(_adate, true);
    }

    /**
     * Return the Last Manual Run Date of the alert.
     * 
     * @return The date is in the form m/d/yyyy.
     */
    public String getRDate()
    {
        return dateToString(_rdate, true);
    }

    /**
     * Return the Last Modification Date of the alert.
     * 
     * @return The date is in the form m/d/yyyy.
     */
    public String getMDate()
    {
        return dateToString(_mdate, true);
    }

    /**
     * Render a string using <br>
     * tags in HTML in place of new lines ('\n').
     * 
     * @param text_
     *        The string to convert.
     * @return An HTML compatible string.
     */
    static public String getHTMLString(String text_)
    {
        // Playing with the escape character to get past the compiler.
        String temp;
        temp = text_;
        temp = temp.replaceAll("[&]", "&amp;");
        temp = temp.replaceAll("[<]", "&lt;");
        temp = temp.replaceAll("[>]", "&gt;");
        temp = temp.replaceAll("\\n", "<br>");
        return temp;
    }

    /**
     * Return a string in a form that can be assigned in Javascript code.
     * 
     * @param val_
     *        The original string.
     * @return The translated string.
     */
    private String forCode(String val_)
    {
        if (val_ == null || val_.length() == 0)
            return "";
        String temp = val_.replaceAll("\\n", "\\\\n");
        return temp.replaceAll("\"", "\\\\\"");
    }

    /**
     * Return the Summary description of the Alert in one of two forms.
     * 
     * @param flag_
     *        True will return the string with new line characters translated to
     *        text for use in code assignments. False will return the string in
     *        raw form, untranslated, for use in println() or embedded in a JSP
     *        Textarea field.
     * @return The summary text as indicated by flag_.
     */
    public String getSummary(boolean flag_)
    {
        // It's all about how the string is going to be used. Sometimes a new
        // line has to be converted
        // to text. Talk about playing with escapes...
        if (flag_)
            return forCode(_summary);
        return _summary;
    }

    /**
     * Return the Report Introduction description of the Alert.
     * 
     * @param flag_
     *        true will return the string with new line characters translated to
     *        text for use in code assignments. False will return the string in
     *        raw form, untranslated, for use in println() or embedded in a JSP
     *        Textarea field.
     * @return The summary text as indicated by flag_.
     */
    public String getIntro(boolean flag_)
    {
        // It's all about how the string is going to be used. Sometimes a new
        // line has to be converted
        // to text.
        if (_intro == null || _intro.length() == 0)
            return null;
        if (flag_)
            return forCode(_intro);
        return _intro;
    }

    /**
     * Return the Alert Frequency value in an HTML string in the form
     * {unit}[&lt;br&gt;{value}]. Where unit and value may be: <br>
     * <table cellpadding="3">
     * <tr>
     * <th align="left">UNIT</th>
     * <th>&nbsp;</th>
     * <th align="left">VALUE</th>
     * </tr>
     * <tr>
     * <td>Daily</td>
     * <td>&nbsp;</td>
     * <td>&lt;empty&gt;</td>
     * </tr>
     * <tr>
     * <td>Weekly</td>
     * <td>&nbsp;</td>
     * <td>a 3 letter abbreviation for the day of the week</td>
     * </tr>
     * <tr>
     * <td>Monthly</td>
     * <td>&nbsp;</td>
     * <td>a number in the range 1-31</td>
     * </tr>
     * </table>
     * 
     * @param flag_ true for HTML and false for non-HTML
     * @return The auto run frequency in HTML compatible format.
     */
    public String getFreq(boolean flag_)
    {
        String temp;
        String sep = (flag_) ? "<br>" : " ";
        if (_freq == 'D')
            temp = "Daily";
        else if (_freq == 'W')
            temp = "Weekly" + sep + weekdays[_day - 1];
        else
            temp = "Monthly" + sep + Integer.toString(_day);
        return temp;
    }

    /**
     * Set the alert name. A null is valid for this method, however it can not
     * be saved to the database with a blank or null name.
     * 
     * @param val_
     *        The name.
     */
    public void setName(String val_)
    {
        _name = (val_ == null) ? "" : val_.replaceAll("[\"\\r\\n]", "");
        if (_name.length() > DBAlert._MAXNAMELEN)
            _name = _name.substring(0, DBAlert._MAXNAMELEN);
        if (_name != null)
            _name = _name.trim();
    }

    /**
     * Set the summary description.
     * 
     * @param val_
     *        The descriptive text.
     * @param flag_
     *        True if the text should be searched and stripped of '\r'
     *        characters. False if the text should be used unaltered.
     */
    public void setSummary(String val_, boolean flag_)
    {
        if (val_ == null)
            val_ = "";
        if (flag_)
            _summary = val_.replaceAll("\r", "");
        else
            _summary = val_;
    }

    /**
     * Set the auto run frequency.
     * 
     * @param val_
     *        The frequency, "Daily", "Weekly", "Monthly".
     */
    public void setFreq(String val_)
    {
        _freq = (val_ == null || val_.length() == 0) ? 'D' : val_.charAt(0);
    }

    /**
     * Set the auto run frequency.
     * 
     * @param val_
     *        The frequency, 'D', 'W', 'M'
     */
    public void setFreq(char val_)
    {
        _freq = (val_ == '\0') ? 'D' : val_;
    }

    /**
     * Set the auto run frequency.
     * 
     * @param val_
     *        The frequency, "Daily", "Weekly", "Monthly".
     * @param week_
     *        For "Weekly", the day of the week, 1 = Sunday, 7 = Saturday.
     * @param month_
     *        For "Montly", the day of the month, 1-31.
     */
    public void setFreq(String val_, String week_, String month_)
    {
        _freq = val_.charAt(0);
        if (_freq == 'D')
            _day = 0;
        else if (_freq == 'W')
            _day = Integer.parseInt(week_);
        else
            _day = Integer.parseInt(month_);
    }

    /**
     * Set the Creation date.
     * 
     * @param val_
     *        If null, today's day and time are used. If not null, must match
     *        the form documented for java.sql.Timestamp.valueOf().
     */
    public void setCdate(String val_)
    {
        if (val_ == null)
        {
            Date today = new Date();
            _cdate = new Timestamp(today.getTime());
        }
        else
            _cdate = Timestamp.valueOf(val_);
    }

    /**
     * Set the Creation date.
     * 
     * @param val_
     *        If null, today's day and time are used, otherwise the desired
     *        timestamp.
     */
    public void setCdate(Timestamp val_)
    {
        if (val_ == null)
        {
            Date today = new Date();
            _cdate = new Timestamp(today.getTime());
        }
        else
            _cdate = val_;
    }

    /**
     * Set the Modification date.
     * 
     * @param val_
     *        If null and the creation date is null, today's day and time are
     *        used. If null and the creation date is set, the creation date is
     *        used to set the modification date. If not null, must match the
     *        form documented for java.sql.Timestamp.valueOf().
     */
    public void setMdate(String val_)
    {
        if (val_ == null)
        {
            if (_cdate == null)
            {
                Date today = new Date();
                _mdate = new Timestamp(today.getTime());
            }
            else
            {
                _mdate = _cdate;
            }
        }
        else
        {
            _mdate = Timestamp.valueOf(val_);
        }
    }

    /**
     * Set the Modification date.
     * 
     * @param val_
     *        If null and the creation date is null, today's day and time are
     *        used. If null and the creation date is set, the creation date is
     *        used to set the modification date. If not null, the desired
     *        timestamp.
     */
    public void setMdate(Timestamp val_)
    {
        if (val_ == null)
        {
            if (_cdate == null)
            {
                Date today = new Date();
                _mdate = new Timestamp(today.getTime());
            }
            else
            {
                _mdate = _cdate;
            }
        }
        else
        {
            _mdate = val_;
        }
    }

    /**
     * Set the last auto run date.
     * 
     * @param val_
     *        If null the date is set to null. Otherwise it must match the form
     *        documented for java.sql.Timestamp.valueOf().
     */
    public void setAdate(String val_)
    {
        _adate = (val_ == null) ? null : Timestamp.valueOf(val_);
    }

    /**
     * Set the last auto run date.
     * 
     * @param val_
     *        The desired timestamp. Null is valid.
     */
    public void setAdate(Timestamp val_)
    {
        _adate = val_;
    }

    /**
     * Set the last manual run date.
     * 
     * @param val_
     *        The desired timestamp. Null is valid.
     */
    public void setRdate(Timestamp val_)
    {
        _rdate = val_;
    }

    /**
     * Set the alert active flag. If attempting to set the value to "Dates" and
     * using null for the start and end dates, the value is automatically
     * changed to "Active". If start and end are not null they are ordered to
     * guarantee the start date is less than or equal to the end date.
     * 
     * @param val_
     *        One of "Active", "First", "Dates", "Inactive".
     * @param start_
     *        The start date if "Dates" is used. Null is valid.
     * @param end_
     *        The end date if "Dates" is used. Null is valid.
     */
    public void setActive(String val_, String start_, String end_)
    {
        // Set the dates first.
        setStart(start_);
        setEnd(end_);

        // Fix the dates based on the status value.
        _active = val_.charAt(0);
        if (_active == 'D' && _start == null && _end == null)
            _active = 'A';

        // Be sure the dates are in the proper order.
        if (_start != null && _end != null && _start.after(_end) == true)
        {
            Timestamp tdate;
            tdate = _start;
            _start = _end;
            _end = tdate;
        }
    }

    /**
     * Set the alert active flag.
     * 
     * @param val_
     *        One of "Active", "First", "Dates", "Inactive". If null, "Active"
     *        is assumed.
     */
    public void setActive(String val_)
    {
        _active = (val_ == null) ? 'A' : val_.charAt(0);
    }

    /**
     * Set the alert active flag.
     * 
     * @param val_
     *        One of 'A', 'F', 'D', 'I'.
     */
    public void setActive(char val_)
    {
        _active = (val_ == '\0') ? 'A' : val_;
    }

    /**
     * Set the creator id. This will also clear out the current recipients and
     * reset the list to the creator only.
     * 
     * @param val_
     *        The creator id.
     */
    public void setCreator(String val_)
    {
        _creator = (val_ == null) ? "" : val_;
        _recipients = new String[1];
        _recipients[0] = _creator;
    }

    /**
     * Set the creator name.
     * 
     * @param val_
     *        The creator id.
     */
    public void setCreatorName(String val_)
    {
        _creatorName = (val_ == null) ? "" : val_;
    }

    /**
     * Set the auto run frequency day.
     * 
     * @param val_
     *        For weekly runs, 1 = sunday, 7 = saturday. For monthly runs, 1-31.
     *        For daily this value is ignored.
     */
    public void setDay(String val_)
    {
        if (val_ == null)
            _day = 0;
        else
            _day = Integer.parseInt(val_);
    }

    /**
     * Set the auto run frequency day.
     * 
     * @param val_
     *        For weekly runs, 1 = sunday, 7 = saturday. For monthly runs, 1-31.
     *        For daily this value is ignored.
     */
    public void setDay(int val_)
    {
        _day = val_;
    }

    /**
     * Set the inactive reason description. The alert does not have to be
     * inactive to set this text. Null is valid.
     * 
     * @param val_
     *        The descriptive text.
     */
    public void setInactiveReason(String val_)
    {
        _inactiveReason = val_;
        if (_inactiveReason != null && _inactiveReason.length() > DBAlert._MAXREASONLEN)
            _inactiveReason = _inactiveReason.substring(0, DBAlert._MAXREASONLEN);
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setInfoVerNum(String val_)
    {
        _infoVerNum = (val_ == null) ? "" : val_;
    }

    /**
     * Set the version number to monitor for changes. Null is valid.
     * 
     * @param val_
     *        The desired caDSR administered component version number.
     */
    public void setActVerNum(String val_)
    {
        _actVerNum = (val_ == null) ? "" : val_;
    }

    /**
     * Set the report introduction description text. Null is valid.
     * 
     * @param val_
     *        The descriptive text.
     * @param flag_
     *        True causes all '\r' characters to be stripped from the text.
     *        False uses the text unaltered.
     */
    public void setIntro(String val_, boolean flag_)
    {
        if (val_ == null)
            val_ = "";
        if (flag_)
            _intro = val_.replaceAll("\r", "");
        else
            _intro = val_;
        if (_intro.length() > DBAlert._MAXINTROLEN)
            _intro = _intro.substring(0, DBAlert._MAXINTROLEN);
    }

    /**
     * Include the property section in the report output.
     * 
     * @param val_
     *        Not null to include the property section, null to omit it.
     */
    public void setIncPropSect(String val_)
    {
        _incPropSect = (val_ != null);
    }

    /**
     * Include the property section in the report output.
     * 
     * @param val_
     *        True to include the property section, False to omit it.
     */
    public void setIncPropSect(boolean val_)
    {
        _incPropSect = val_;
    }

    /**
     * Set the report style.
     * 
     * @param val_
     *        Either "Audit" or "Summary". If null, "Audit" is assumed.
     */
    public void setReportStyle(String val_)
    {
        _reportStyle = (val_ == null) ? 'A' : val_.charAt(0);
    }

    /**
     * Set the report style.
     * 
     * @param val_
     *        Either 'A' or 'S'.
     */
    public void setReportStyle(char val_)
    {
        _reportStyle = val_;
    }

    /**
     * Send empty reports.
     * 
     * @param val_
     *        Either "Yes" or "No". If null, "No" is assumed.
     */
    public void setReportEmpty(String val_)
    {
        _reportEmpty = (val_ == null) ? 'N' : val_.charAt(0);
    }

    /**
     * Send empty reports.
     * 
     * @param val_
     *        Either 'Y' or 'N'.
     */
    public void setReportEmpty(char val_)
    {
        _reportEmpty = val_;
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setReportAck(String val_)
    {
        _reportAck = (val_ == null) ? 'N' : val_.charAt(0);
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setReportAck(char val_)
    {
        _reportAck = val_;
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setVDTE(String val_)
    {
        _vdte = (val_ != null);
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setVDTE(boolean val_)
    {
        _vdte = val_;
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setVDTN(String val_)
    {
        _vdtn = (val_ != null);
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setVDTN(boolean val_)
    {
        _vdtn = val_;
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setIUse(String val_)
    {
        _iuse = (val_ == null) ? 'B' : val_.charAt(0);
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setIUse(char val_)
    {
        _iuse = val_;
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setAUse(String val_)
    {
        _ause = (val_ == null) ? 'C' : val_.charAt(0);
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setAUse(char val_)
    {
        _ause = val_;
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setTerm(String val_)
    {
        _term = (val_ == null) ? "" : val_;
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setIVersion(String val_)
    {
        _iVersion = (val_ == null) ? 'A' : val_.charAt(0);
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setIVersion(char val_)
    {
        _iVersion = val_;
    }

    /**
     * Set the version flag to monitor.
     * 
     * @param val_
     *        One of "Changes", "Major", "Ignore", "Specific". If null,
     *        "Changes" is assumed.
     */
    public void setAVersion(String val_)
    {
        _aVersion = (val_ == null) ? DBAlert._VERANYCHG : val_.charAt(0);
    }

    /**
     * Set the version flag to monitor.
     * 
     * @param val_
     *        One of _VERANYCHG, _VERMAJCHG, _VERIGNCHG, _VERSPECHG.
     */
    public void setAVersion(char val_)
    {
        _aVersion = val_;
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setRelated(String val_)
    {
        _related = (val_ != null);
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setRelated(boolean val_)
    {
        _related = val_;
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setAdminChg(String val_)
    {
        _adminChg = (val_ != null);
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setAdminChg(boolean val_)
    {
        _adminChg = val_;
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setAdminNew(String val_)
    {
        _adminNew = (val_ != null);
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setAdminNew(boolean val_)
    {
        _adminNew = val_;
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setAdminCopy(String val_)
    {
        _adminCopy = (val_ != null);
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setAdminCopy(boolean val_)
    {
        _adminCopy = val_;
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setAdminDel(String val_)
    {
        _adminDel = (val_ != null);
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setAdminDel(boolean val_)
    {
        _adminDel = val_;
    }

    /**
     * Be sure days and months have 2 digits and years have 4 digits.
     * 
     * @param val_
     *        The original date part.
     * @param len_
     *        The desired length. For month and day this is 2 and year this is
     *        4.
     * @return The corrected date part.
     */
    static private String fixLength(String val_, int len_)
    {
        int vallen = val_.length();
        if (vallen == len_)
            return val_;

        String base = "2000";
        base = base.substring(0, base.length() - vallen) + val_;

        return base.substring(base.length() - len_);
    }

    /**
     * Be sure a date has a month, day and year.
     * 
     * @param val_
     *        The date broken into tokens.
     * @return The corrected SQL datetime string in the format "yyyy-mm-dd
     *         00:00:00.0".
     */
    static private String fixDate(String val_[])
    {
        String parts[] = new String[3];

        if (val_.length == 2)
        {
            parts[2] = "1";
            parts[1] = val_[0];
            parts[0] = val_[1];
        }
        else
        {
            parts[2] = val_[1];
            parts[1] = val_[0];
            parts[0] = val_[2];
        }

        parts[0] = fixLength(parts[0], 4);
        parts[1] = fixLength(parts[1], 2);
        parts[2] = fixLength(parts[2], 2);

        String tdate = parts[0] + "-" + parts[1] + "-" + parts[2]
            + " 00:00:00.0";

        return tdate;
    }

    /**
     * Convert date strings to the expected format. Either '/' or '-' separators
     * can be used.
     * 
     * @param val_
     *        The date string.
     * @return The timestamp object for the date given.
     */
    static public Timestamp parseDate(String val_)
    {
        if (val_ == null || val_.length() == 0)
            return null;

        Timestamp temp = null;
        String tval[];
        int pos = val_.indexOf('/');
        if (pos > -1)
        {
            tval = val_.split("/");
            temp = Timestamp.valueOf(fixDate(tval));
        }
        else
        {
            pos = val_.indexOf('-');
            if (pos > -1)
            {
                tval = val_.split("-");
                temp = Timestamp.valueOf(fixDate(tval));
            }
        }
        return temp;
    }

    /**
     * Set the active start date.
     * 
     * @param val_
     *        The desired date in the form month/day/year or month/year. The "/"
     *        or "-" are valid separator characters.
     */
    public void setStart(String val_)
    {
        _start = parseDate(val_);
    }

    /**
     * Set the active start date.
     * 
     * @param val_
     *        The desired date.
     */
    public void setStart(Timestamp val_)
    {
        _start = val_;
    }

    /**
     * Set the active end date.
     * 
     * @param val_
     *        The desired date in the form month/day/year or month/year. The "/"
     *        or "-" are valid separator characters.
     */
    public void setEnd(String val_)
    {
        _end = parseDate(val_);
    }

    /**
     * Set the active end date.
     * 
     * @param val_
     *        The desired date.
     */
    public void setEnd(Timestamp val_)
    {
        _end = val_;
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setAVDT(String val_)
    {
        _avdt = (val_ == null) ? 'C' : val_.charAt(0);
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setAVDT(char val_)
    {
        _avdt = val_;
    }

    /**
     * Set the database alert record number primary key.
     * 
     * @param val_
     *        The key.
     */
    public void setAlertRecNum(String val_)
    {
        _alertRecNum = val_;
    }

    /**
     * Set the database record numbers to null in preparation for a new alert.
     */
    public void setRecNumNull()
    {
        _alertRecNum = null;
        _reportRecNum = null;
    }

    /**
     * Set the database report record number primary key.
     * 
     * @param val_
     *        The database primary key for the report.
     */
    public void setReportRecNum(String val_)
    {
        _reportRecNum = val_;
    }

    /**
     * Set the database record numbers for an update.
     * 
     * @param alert_
     *        The database primary key for the alert.
     * @param report_
     *        The database primary key for the report.
     */
    public void setRecNums(String alert_, String report_)
    {
        _alertRecNum = alert_;
        _reportRecNum = report_;
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setAttrs(String val_[])
    {
        if (val_ == null)
        {
            _attrs = new String[1];
            _attrs[0] = Constants._STRALL;
        }
        else
            _attrs = val_;
    }

    /**
     * Set the list of recipients for the report.
     * 
     * @param val_
     *        The string ids of each recipient. May also include well formed
     *        email addresses which include the "@" character. May also include
     *        the context database key prefixed with a "/" character.
     */
    public void setRecipients(String val_[])
    {
        _recipients = val_;
        if (_recipients == null)
            return;
        for (int ndx = 0; ndx < _recipients.length; ++ndx)
        {
            if (_recipients[ndx] != null && _recipients[ndx].length() > DBAlert._MAXEMAILLEN)
                _recipients[ndx] = _recipients[ndx].substring(0, DBAlert._MAXEMAILLEN);
        }
    }
    
    /**
     * Set the recipient for the Alert Definition.
     * @param ndx_ The index into the recipient list.
     * @param val_ The new recipient value.
     */
    public void setRecipients(int ndx_, String val_)
    {
        if (_recipients != null && ndx_ < _recipients.length)
        {
            if (val_ != null && val_.length() > DBAlert._MAXEMAILLEN)
                _recipients[ndx_] = val_.substring(0, DBAlert._MAXEMAILLEN);
            else
                _recipients[ndx_] = val_;
        }
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setSearchIn(String val_)
    {
        _searchIn = (val_ == null) ? "0" : val_;
    }

    /**
     * Set the workflow monitor value(s).
     * 
     * @param val_
     *        The workflow status value(s) from the caDSR. If null is specified,
     *        all workflow status changes are monitored.
     */
    public void setAWorkflow(String val_[])
    {
        if (val_ == null)
        {
            _aWorkflow = new String[1];
            _aWorkflow[0] = Constants._STRANY;
        }
        else
        {
            _aWorkflow = val_;
        }
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setCWorkflow(String val_[])
    {
        if (val_ == null)
        {
            _cWorkflow = new String[1];
            _cWorkflow[0] = Constants._STRALL;
        }
        else
        {
            _cWorkflow = val_;
        }
    }

    /**
     * Set the registration monitor value(s).
     * 
     * @param val_
     *        The registration status value(s) from the caDSR. If null is
     *        specified, all registration status changes are monitored.
     */
    public void setARegis(String val_[])
    {
        if (val_ == null)
        {
            _aRegis = new String[1];
            _aRegis[0] = Constants._STRANY;
        }
        else
        {
            _aRegis = val_;
        }
    }

    /**
     * Set the creators criteria.
     * 
     * @param val_
     *        The list of creators to qualify the criteria when searching for
     *        changes. If null, then all creators are included.
     */
    public void setCreators(String val_[])
    {
        if (val_ == null)
        {
            _creators = new String[1];
            _creators[0] = Constants._STRALL;
        }
        else
        {
            _creators = val_;
        }
    }

    /**
     * Set the modifiers criteria.
     * 
     * @param val_
     *        The list of modifiers to qualify the criteria when searching for
     *        changes. If null, then all modifiers are included.
     */
    public void setModifiers(String val_[])
    {
        if (val_ == null)
        {
            _modifiers = new String[1];
            _modifiers[0] = Constants._STRALL;
        }
        else
        {
            _modifiers = val_;
        }
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setSearchAC(String val_[])
    {
        if (val_ == null)
        {
            _searchAC = new String[1];
            _searchAC[0] = Constants._STRALL;
        }
        else
        {
            _searchAC = val_;
        }
    }

    /**
     * Set the list of forms for the criteria.
     * 
     * @param val_
     *        The forms database keys to qualify the criteria when searching for
     *        changes. If null, then all forms are included.
     */
    public void setForms(String val_[])
    {
        if (val_ == null)
        {
            _forms = new String[1];
            _forms[0] = Constants._STRALL;
        }
        else
        {
            _forms = val_;
        }
    }

    /**
     * Set the list of protocols for the criteria.
     * 
     * @param val_
     *        The protocol database keys to qualify the criteria
     *        when searching for changes. If null, then all protocols are
     *        included.
     */
    public void setProtocols(String val_[])
    {
        if (val_ == null)
        {
            _protocols = new String[1];
            _protocols[0] = Constants._STRALL;
        }
        else
        {
            _protocols = val_;
        }
    }

    /**
     * Set the list of classification schemes for the criteria.
     * 
     * @param val_
     *        The classification scheme database keys to qualify the criteria
     *        when searching for changes. If null, then all schemes are
     *        included.
     */
    public void setSchemes(String val_[])
    {
        if (val_ == null)
        {
            _schemes = new String[1];
            _schemes[0] = Constants._STRALL;
        }
        else
        {
            _schemes = val_;
        }
    }

    /**
     * Set the list of classification scheme items for the criteria.
     * 
     * @param val_
     *        The classification scheme item database keys to qualify the
     *        criteria when searching for changes. If null, then all schemes are
     *        included.
     */
    public void setSchemeItems(String val_[])
    {
        if (val_ == null)
        {
            _schemeItems = new String[1];
            _schemeItems[0] = Constants._STRALL;
        }
        else
        {
            _schemeItems = val_;
        }
    }

    /**
     * Future enhancements.
     * 
     * @param val_
     */
    public void setDomains(String val_[])
    {
        if (val_ == null)
        {
            _domains = new String[1];
            _domains[0] = Constants._STRALL;
        }
        else
        {
            _domains = val_;
        }
    }

    /**
     * Set the list of contexts for the criteria.
     * 
     * @param val_
     *        The context database keys to qualify the criteria when searching
     *        for changes. If null, then all contexts are included.
     */
    public void setContexts(String val_[])
    {
        if (val_ == null)
        {
            _contexts = new String[1];
            _contexts[0] = Constants._STRALL;
        }
        else
        {
            _contexts = val_;
        }
    }

    /**
     * Set the list of registration statuses for the criteria.
     * 
     * @param val_
     *        The registration status database keys to qualify the criteria when searching
     *        for changes. If null, then all statuses are included.
     */
    public void setCRegStatus(String val_[])
    {
        if (val_ == null)
        {
            _cRegStatus = new String[1];
            _cRegStatus[0] = Constants._STRALL;
        }
        else
        {
            _cRegStatus = val_;
        }
    }

    /**
     * Set the list of record types for the criteria.
     * 
     * @param val_
     *        The record type keys to qualify the criteria when searching
     *        for changes. If null, then all record types are included.
     */
    public void setACTypes(String val_[])
    {
        if (val_ == null)
        {
            _ACTypes = new String[1];
            _ACTypes[0] = Constants._STRALL;
        }
        else
        {
            _ACTypes = val_;
        }
    }

    /**
     * Return the alert definition name.
     * 
     * @return The name.
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Set the summary description without alterations.
     * 
     * @param text_
     *        The descriptive text.
     */
    public void setSummary(String text_)
    {
        setSummary(text_, false);
    }

    /**
     * Return the auto run frequeny flag as a string value.
     * 
     * @return The frequency.
     */
    public String getFreqString()
    {
        return String.valueOf(_freq);
    }

    /**
     * Return true if the auto run frequency is Daily, otherwise false.
     * 
     * @return True if daily.
     */
    public boolean isFreqDay()
    {
        return (_freq == 'D');
    }

    /**
     * Return true if the auto run frequency is Weekly, otherwise false.
     * 
     * @return True if weekly.
     */
    public boolean isFreqWeek()
    {
        return (_freq == 'W');
    }

    /**
     * Return true if the auto run frequency is Monthly, otherwise false.
     * 
     * @return True if monthly.
     */
    public boolean isFreqMonth()
    {
        return (_freq == 'M');
    }

    /**
     * Return the creation date.
     * 
     * @return The date.
     */
    public Timestamp getCdate()
    {
        return _cdate;
    }

    /**
     * Return the modification date.
     * 
     * @return The date.
     */
    public Timestamp getMdate()
    {
        return _mdate;
    }

    /**
     * Return the last auto run date.
     * 
     * @return The date.
     */
    public Timestamp getAdate()
    {
        return _adate;
    }

    /**
     * Return the last manual run date.
     * 
     * @return The date.
     */
    public Timestamp getRdate()
    {
        return _rdate;
    }

    /**
     * Return true if the alert is inactive, otherwise false.
     * 
     * @return True if inactive.
     */
    public boolean isInactive()
    {
        return (_active == 'I');
    }

    /**
     * Return true if the alert is "Active", otherwise false.
     * 
     * @return True if active.
     */
    public boolean isActive()
    {
        return (_active == 'A');
    }

    /**
     * Return true if the alert is "Active", otherwise false.
     * 
     * @param target_ The time stamp to compare to the Active setting
     *      in the Alert Definition.
     * @return True if active.
     */
    public boolean isActive(Timestamp target_)
    {
        if (_active == 'A' || _active == 'F')
            return true;
        else if (_active == 'D')
        {
            if (_start == null || _start.compareTo(target_) <= 0)
            {
                if (_end == null || target_.compareTo(_end) < 0)
                    return true;
            }
        }
        return false;
    }

    /**
     * Return true if the alert is "First", otherwise false.
     * 
     * @return True if active.
     */
    public boolean isActiveOnce()
    {
        return (_active == 'F');
    }

    /**
     * Return true if the alert is "Dates", otherwise false.
     * 
     * @return True if active.
     */
    public boolean isActiveDates()
    {
        return (_active == 'D');
    }

    /**
     * Return the active flag as a string.
     * 
     * @return The active flag.
     */
    public String getActiveString()
    {
        return String.valueOf(_active);
    }

    /**
     * Return the creator id for the alert.
     * 
     * @return The creator id.
     */
    public String getCreator()
    {
        return _creator;
    }

    /**
     * Return the creator name for the alert.
     * 
     * @return The creator name.
     */
    public String getCreatorName()
    {
        return _creatorName;
    }

    /**
     * Return the auto run frequency day.
     * 
     * @return The frequency day.
     */
    public int getDay()
    {
        return _day;
    }

    /**
     * Return the inactive reason description.
     * 
     * @param flag_ true for a result compatible with Javascript,
     *      otherwise false.
     * @return The inactive reason.
     */
    public String getInactiveReason(boolean flag_)
    {
        if (flag_)
            return forCode(_inactiveReason);
        return _inactiveReason;
    }

    /**
     * Return the monitored version number.
     * 
     * @return The version number.
     */
    public String getActVerNum()
    {
        return _actVerNum;
    }

    /**
     * Return the include property section flag as a string, "Yes" or "No".
     * 
     * @return The report property flag.
     */
    public String getIncPropSectString()
    {
        return (_incPropSect) ? "Yes" : "No";
    }

    /**
     * Return the include property section flag.
     * 
     * @return The report property flag.
     */
    public boolean getIncPropSect()
    {
        return _incPropSect;
    }

    /**
     * Return the report style as a string.
     * 
     * @return The report style.
     */
    public String getReportStyleString()
    {
        return String.valueOf(_reportStyle);
    }

    /**
     * Return the send empty report flag as a string.
     * 
     * @return The send empty report flag.
     */
    public String getReportEmptyString()
    {
        return String.valueOf(_reportEmpty);
    }

    /**
     * Test if an empty report should be distributed.
     * 
     * @return true when empty reports should be sent, false when empty reports
     *         are not sent.
     */
    public boolean isSendEmptyReport()
    {
        return (_reportEmpty != 'N');
    }

    /**
     * Future enhancements.
     * 
     * @return TBD
     */
    public String getReportAckString()
    {
        return (_reportAck == 'Y') ? "Yes" : "No";
    }

    /**
     * Return the recipients list. If an '@' is present, it is a well formed
     * email address. If a '/' is present it is the context database id
     * representing all users with write access to that context. Otherwise it is
     * a specific user database id.
     * 
     * @return The list of the report recipients.
     */
    public String[] getRecipients()
    {
        return _recipients;
    }

    /**
     * Return a single recipient from the list. If an '@' is present, it is a
     * well formed email address. If a '/' is present it is the context database
     * id representing all users with write access to that context. Otherwise it
     * is a specific user database id.
     * 
     * @param ndx_ The index into the list.
     * @return A report recipient.
     */
    public String getRecipients(int ndx_)
    {
        return _recipients[ndx_];
    }

    /**
     * Return the workflow status(es) to monitor. Anyone beginning with a '(' is
     * a special value, otherwise it is the database id for the status.
     * 
     * @return The workflow status ids.
     */
    public String[] getAWorkflow()
    {
        return _aWorkflow;
    }

    /**
     * Return a single workflow status to monitor. Anyone beginning with a '('
     * is a special value, otherwise it is the database id for the status.
     * 
     * @param ndx_ The index into the list.
     * @return A workflow status id.
     */
    public String getAWorkflow(int ndx_)
    {
        return _aWorkflow[ndx_];
    }

    /**
     * Return the registration status(es) to monitor. Anyone beginning with a
     * '(' is a special value, otherwise it is the database id for the status.
     * 
     * @return The registration status ids.
     */
    public String[] getARegis()
    {
        return _aRegis;
    }

    /**
     * Return a single registration status to monitor. Anyone beginning with a
     * '(' is a special value, otherwise it is the database id for the status.
     * 
     * @param ndx_ The index into the list.
     * @return A registration status id.
     */
    public String getARegis(int ndx_)
    {
        return _aRegis[ndx_];
    }

    /**
     * Return the specific form database ids to include in the criteria. "(All)"
     * is a special value and will be the sole value if present.
     * 
     * @return The form ids.
     */
    public String[] getForms()
    {
        return _forms;
    }

    /**
     * Return the specific form database id to include in the criteria. "(All)"
     * is a special value and will be the sole value if present.
     * 
     * @param ndx_ The index into the list.
     * @return A form id.
     */
    public String getForms(int ndx_)
    {
        return _forms[ndx_];
    }

    /**
     * Return the specific protocol database ids to include in the
     * criteria. "(All)" is a special value and will be the sole value if
     * present.
     * 
     * @return The protocol ids.
     */
    public String[] getProtocols()
    {
        return _protocols;
    }

    /**
     * Return the specific protocol database id to include in the
     * criteria. "(All)" is a special value and will be the sole value if
     * present.
     * 
     * @param ndx_ The index into the list.
     * @return The protocol id.
     */
    public String getProtocols(int ndx_)
    {
        return _protocols[ndx_];
    }

    /**
     * Return the specific classification scheme database ids to include in the
     * criteria. "(All)" is a special value and will be the sole value if
     * present.
     * 
     * @return The classification scheme ids.
     */
    public String[] getSchemes()
    {
        return _schemes;
    }

    /**
     * Return the specific classification scheme database id to include in the
     * criteria. "(All)" is a special value and will be the sole value if
     * present.
     * 
     * @param ndx_ The index into the list.
     * @return A classification scheme id.
     */
    public String getSchemes(int ndx_)
    {
        return _schemes[ndx_];
    }

    /**
     * Return the specific classification scheme item database ids to include in
     * the criteria. "(All)" is a special value and will be the sole value if
     * present.
     * 
     * @return The classification scheme ids.
     */
    public String[] getSchemeItems()
    {
        return _schemeItems;
    }

    /**
     * Return the specific classification scheme item database id to include in
     * the criteria. "(All)" is a special value and will be the sole value if
     * present.
     * 
     * @param ndx_ The index into the list.
     * @return A classification scheme id.
     */
    public String getSchemeItems(int ndx_)
    {
        return _schemeItems[ndx_];
    }

    /**
     * Return the specific creator database ids to include in the criteria.
     * "(All)" is a special value and will be the sole value if present.
     * 
     * @return The creator ids.
     */
    public String[] getCreators()
    {
        return _creators;
    }

    /**
     * Return the specific creator database id to include in the criteria.
     * "(All)" is a special value and will be the sole value if present.
     * 
     * @param ndx_ The index into the list.
     * @return A creator id.
     */
    public String getCreators(int ndx_)
    {
        return _creators[ndx_];
    }

    /**
     * Return the specific modifier database ids to include in the criteria.
     * "(All)" is a special value and will be the sole value if present.
     * 
     * @return The modifier ids.
     */
    public String[] getModifiers()
    {
        return _modifiers;
    }

    /**
     * Return the specific modifier database id to include in the criteria.
     * "(All)" is a special value and will be the sole value if present.
     * 
     * @param ndx_ The index into the list.
     * @return A modifier id.
     */
    public String getModifiers(int ndx_)
    {
        return _modifiers[ndx_];
    }

    /**
     * Return the specific context database ids to include in the criteria.
     * "(All)" is a special value and will be the sole value if present.
     * 
     * @return The context ids.
     */
    public String[] getContexts()
    {
        return _contexts;
    }

    /**
     * Return the specific registration status database ids to include in the criteria.
     * "(All)" is a special value and will be the sole value if present.
     * 
     * @return The registration status ids.
     */
    public String[] getCRegStatus()
    {
        return _cRegStatus;
    }

    /**
     * Return the specific workflow status database ids to include in the criteria.
     * "(All)" is a special value and will be the sole value if present.
     * 
     * @return The workflow status ids.
     */
    public String[] getCWorkflow()
    {
        return _cWorkflow;
    }
    
    /**
     * Return the specific record types to include in the criteria.  "(All)"
     * is a special value and will be the sole value if present.
     * 
     * @return The record types.
     */
    public String[] getACTypes()
    {
        return _ACTypes;
    }

    /**
     * Return the specific context database id to include in the criteria.
     * "(All)" is a special value and will be the sole value if present.
     * 
     * @param ndx_ The index into the context list.
     * @return A context id.
     */
    public String getContexts(int ndx_)
    {
        return _contexts[ndx_];
    }

    /**
     * Return the specific registration status database id to include in the criteria.
     * "(All)" is a special value and will be the sole value if present.
     * 
     * @param ndx_ The index into the registration status list.
     * @return A context id.
     */
    public String getCRegStatus(int ndx_)
    {
        return _cRegStatus[ndx_];
    }

    /**
     * Return the specific workflow status database id to include in the criteria.
     * "(All)" is a special value and will be the sole value if present.
     * 
     * @param ndx_ The index into the workflow status list.
     * @return A context id.
     */
    public String getCWorkflow(int ndx_)
    {
        return _cWorkflow[ndx_];
    }

    /**
     * Return the specific record type to include in the criteria.  "(All)"
     * is a special value and will be the sole value if present.
     * 
     * @param ndx_ The index into the record type list.
     * @return The record type requested.
     */
    public String getACTypes(int ndx_)
    {
        return _ACTypes[ndx_];
    }
    
    /**
     * Return the version monitor flag: _VERANYCHG, _VERMAJCHG, _VERIGNCHG,
     * _VERSPECHG.
     * 
     * @return The flag.
     */
    public char getAVersion()
    {
        return _aVersion;
    }

    /**
     * Return the version monitor flag as a string.
     * 
     * @return The flag.
     */
    public String getAVersionString()
    {
        return String.valueOf(_aVersion);
    }

    /**
     * Return the active start date.
     * 
     * @return The date.
     */
    public Timestamp getStart()
    {
        return _start;
    }

    /**
     * Return the active end date.
     * 
     * @return The date.
     */
    public Timestamp getEnd()
    {
        return _end;
    }

    /**
     * Return the alert database primary key.
     * 
     * @return The alert id.
     */
    public String getAlertRecNum()
    {
        return _alertRecNum;
    }

    /**
     * Return the report database primary key.
     * 
     * @return The report id.
     */
    public String getReportRecNum()
    {
        return _reportRecNum;
    }

    /**
     * Check for the use of all Classification Scheme Items.
     * 
     * @return true if all should be used.
     */
    public boolean isCSIall()
    {
        return (_schemeItems == null || _schemeItems.length == 0 ||
            _schemeItems[0].charAt(0) == '(');
    }

    /**
     * Check for the use of all Classification Schemes.
     * 
     * @return true if all should be used.
     */
    public boolean isCSall()
    {
        return (_schemes == null || _schemes.length == 0 ||
            _schemes[0].charAt(0) == '(');
    }

    /**
     * Check for the use of all Protocols.
     * 
     * @return true if all should be used.
     */
    public boolean isPROTOall()
    {
        return (_protocols == null || _protocols.length == 0 ||
            _protocols[0].charAt(0) == '(');
    }

    /**
     * Check for the use of all Forms/Templates.
     * 
     * @return true if all should be used.
     */
    public boolean isFORMSall()
    {
        return (_forms == null || _forms.length == 0 || _forms[0].charAt(0) == '(');
    }

    /**
     * Check for the use of all Contexts.
     * 
     * @return true if all should be used.
     */
    public boolean isCONTall()
    {
        return (_contexts == null || _contexts.length == 0 || _contexts[0].charAt(0) == '(');
    }

    /**
     * Check for the use of all Registration Statuses.
     * 
     * @return true if all should be used.
     */
    public boolean isCWFSall()
    {
        return (_cWorkflow == null || _cWorkflow.length == 0 || _cWorkflow[0].charAt(0) == '(');
    }

    /**
     * Check for the use of all Registration Statuses.
     * 
     * @return true if all should be used.
     */
    public boolean isCRSall()
    {
        return (_cRegStatus == null || _cRegStatus.length == 0 ||
            (_cRegStatus[0].charAt(0) == '(' && _cRegStatus[0].charAt(1) == 'A'));
    }

    /**
     * Check for the use of no Registration Status.
     * 
     * @return true if all should be used.
     */
    public boolean isCRSnone()
    {
        if (_cRegStatus == null || _cRegStatus.length == 0)
            return false;
        
        for (int ndx = 0; ndx < _cRegStatus.length; ++ndx)
        {
            if (_cRegStatus[ndx].charAt(0) == '(' && _cRegStatus[ndx].charAt(1) == 'n')
                return true;
        }

        return false;
    }

    /**
     * Check for the use of all Record Types.
     * 
     * @return true if all should be used.
     */
    public boolean isACTYPEall()
    {
        return (_ACTypes == null || _ACTypes.length == 0 || _ACTypes[0].charAt(0) == '(');
    }

    /**
     * Test the AC type to see if it appears in the AC Type list.
     * 
     * @param db_ The database interface object.
     * @param val_ The index in the definition type list.
     * @return The numeric AC Type if it's used.
     */
    public int isACTypeUsed(DBAlert db_, int val_)
    {
        if (isACTYPEall())
            return val_;
        return db_.isACTypeUsed(_ACTypes[val_]);
    }
    
    /**
     * Describe the status of the Alert Definition.
     * 
     * @return A string detailing the Alert status settings.
     */
    public String getStatus()
    {
        if (isInactive())
            return "Inactive: " + _inactiveReason;
        if (isActive())
            return "Active, no end date.";
        if (isActiveOnce())
            return "Active until the first report containing activity then change status to Inactive.";
        if (isActiveDates())
            return "Active only from " + dateToString(_start, false) + " to "
                + dateToString(_end, false);
        return "unknown";
    }

    /**
     * Test the workflow status.
     * 
     * @return true if the 'All' or 'Any' workflow status is set.
     */
    public boolean isAWorkflowANY()
    {
        return (_aWorkflow == null || _aWorkflow[0].equals(Constants._STRANY));
    }

    /**
     * Test the workflow status.
     * 
     * @return true if the 'Ignore' workflow status is set.
     */
    public boolean isAWorkflowIGNORE()
    {
        return (_aWorkflow != null && _aWorkflow[0]
            .equals(Constants._STRIGNORE));
    }

    /**
     * Test the registration status.
     * 
     * @return true if the 'All' or 'Any' registration status is set.
     */
    public boolean isARegisANY()
    {
        return (_aRegis == null || _aRegis[0].equals(Constants._STRANY));
    }

    /**
     * Test the registration status.
     * 
     * @return true if the 'Ignore' registration status is set.
     */
    public boolean isARegisIGNORE()
    {
        return (_aRegis != null && _aRegis[0].equals(Constants._STRIGNORE));
    }

    /**
     * Test the version setting.
     * 
     * @return true if the 'All' or 'Any' version is set.
     */
    public boolean isAVersionANY()
    {
        return (_aVersion == DBAlert._VERANYCHG);
    }

    /**
     * Test the version setting.
     * 
     * @return true if the 'Major Change' version is set.
     */
    public boolean isAVersionMAJOR()
    {
        return (_aVersion == 'M');
    }

    /**
     * Test the version setting.
     * 
     * @return true if the 'Ignore' version is set.
     */
    public boolean isAVersionIGNORE()
    {
        return (_aVersion == 'I');
    }

    /**
     * Test the version setting.
     * 
     * @return true if the 'Specific Value' version is set.
     */
    public boolean isAVersionSPECIFIC()
    {
        return (_aVersion == 'S');
    }

    /**
     * Set the date filter for the criteria.
     * 
     * @param dates_ the filter DBAlert._DATECM, _DATECONLY, _DATEMONLY
     */
    public void setDateFilter(int dates_)
    {
        _dateFilter = dates_;
    }

    /**
     * Set the date filter for the criteria.
     * 
     * @param dates_ the filter DBAlert._DATECM, _DATECONLY, _DATEMONLY
     */
    public void setDateFilter(String dates_)
    {
        _dateFilter = Integer.parseInt(dates_);
    }
    
    /**
     * Return the date filter setting.
     * 
     * @return DBAlert._DATECM, _DATECONLY, _DATEMONLY
     */
    public int getDateFilter()
    {
        return _dateFilter;
    }
    
    /**
     * Set the Associated To reporting Level
     * 
     * @param val_ The level, 0 none, 9 all
     */
    public void setIAssocLvl(int val_)
    {
        _infoAssocLvl = (0 <= val_ && val_ <= 9) ? val_ : 9;
    }
    
    /**
     * Set the Associated To reporting Level
     * 
     * @param val_ The level, 0 none, 9 all
     */
    public void setIAssocLvl(String val_)
    {
        setIAssocLvl(Integer.parseInt(val_));
    }

    /**
     * Return the Associated To Reporting Level
     * 
     * @return The level number.
     */
    public int getIAssocLvl()
    {
        return _infoAssocLvl;
    }
    
    // Class data elements.
    private String              _name;

    private String              _summary;

    private char                _freq;

    private Timestamp           _cdate;

    private Timestamp           _mdate;

    private Timestamp           _adate;

    private Timestamp           _rdate;

    private char                _active;

    private String              _creator;

    private String              _creatorName;

    private int                 _day;

    private String              _inactiveReason;

    private String              _infoVerNum;

    private String              _actVerNum;

    private String              _intro;

    private boolean             _incPropSect;

    private char                _reportStyle;

    private char                _reportEmpty;

    private char                _reportAck;

    private boolean             _vdte;

    private boolean             _vdtn;

    private String              _attrs[];

    private String              _recipients[];

    private String              _aWorkflow[];

    private String              _cWorkflow[];

    private String              _aRegis[];

    private String              _searchAC[];

    private String              _searchIn;

    private String              _forms[];

    private String              _protocols[];

    private String              _schemes[];

    private String              _schemeItems[];

    private String              _domains[];

    private String              _creators[];

    private String              _modifiers[];

    private String              _contexts[];
    
    private String              _ACTypes[];
    
    private String              _cRegStatus[];

    private char                _iuse;

    private char                _ause;

    private String              _term;

    private char                _iVersion;

    private char                _aVersion;

    private boolean             _related;

    private boolean             _adminChg;

    private boolean             _adminNew;

    private boolean             _adminCopy;

    private boolean             _adminDel;

    private Timestamp           _start;

    private Timestamp           _end;

    private char                _avdt;

    private String              _alertRecNum;

    private String              _reportRecNum;
    
    private int                 _dateFilter;
    
    private int                 _infoAssocLvl;

    private static final String weekdays[] = { "Sun", "Mon", "Tue", "Wed",
        "Thu", "Fri", "Sat"               };
}