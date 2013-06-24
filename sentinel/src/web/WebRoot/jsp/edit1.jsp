<!-- Copyright ScenPro, Inc. 2005
     $Header: /share/content/gforge/sentinel/sentinel/WebRoot/jsp/edit1.jsp,v 1.3 2009-04-08 17:56:18 hebell Exp $
     $Name: not supported by cvs2svn $
-->
<%@ page contentType="text/html;charset=WINDOWS-1252"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/dsralert" prefix="dtags" %>
<%@ page import="java.util.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title><bean:message key="edit.title" /></title>
        
        <div style="position:absolute;">
 			<a href="#skip">
  			<img src="/cadsrsentinel/images/skipnav.gif" border="0" height="1" width="1" alt="Skip Navigation" title="Skip Navigation" />
	 		</a>
		</div>
        
        <html:base />
        <meta http-equiv="Content-Language" content="en-us">
        <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=WINDOWS-1252">
        <LINK href="/cadsrsentinel/css/sentinel.css" rel="stylesheet" type="text/css">
    </head>

<body onload="loaded()">
<%
    String color = "#999999";
%>
    <dtags:edit section="init1" />

    <SCRIPT LANGUAGE="JavaScript" src="/cadsrsentinel/js/edit1.js"></script>
    <SCRIPT LANGUAGE="JavaScript">
        <dtags:edit section="script" />
    </script>

	<a name="skip" id="skip"></a>

    <html:form method="post" action="/edit1">
    <html:hidden property="nextScreen" />
    <dtags:edit section="field" />

    <table class="secttable"><colgroup></colgroup><tbody class="secttbody" />
    <tr><td align="center">

        <dtags:head key="edit.title" />
        <table class="table3">
        <colgroup><col style="text-align: left" /><col style="text-align: right" /></colgroup><tbody class="secttbody" /><tr>
            <td>
                <html:button styleClass="but1" property="save1" onclick="cmdSave();"><bean:message key="all.save" /></html:button>
                <html:button styleClass="but1" property="run1" onclick="cmdRun();"><bean:message key="all.run" /></html:button>
                <html:button styleClass="but1" property="clear1" onclick="cmdClear();"><bean:message key="all.clear" /></html:button>
                <html:button styleClass="but2" property="back1" onclick="cmdBack();"><bean:message key="all.back" /></html:button>
            </td>
            <td>
                <html:button property="logout1" styleClass="but1" onclick="cmdLogout();"><bean:message key="all.logout" /></html:button>
                <html:button property="help1" styleClass="but2" onclick="cmdHelp();"><bean:message key="all.help" /></html:button>
            </td>
        </tr></table><br/>

        <table class="tabtable"><colgroup><col /><col /><col /><col /><col /></colgroup><tbody class="tabtbody" /><tr>
            <td width="15%" class="tabtd"><span class="tab0" id="tabMain1" onmouseover="tabMouseOver(this);" onmouseout="tabMouseOut(this);"
                onclick="selectTab1(this);" title=<bean:message key="edit.propertytitle" />><bean:message key="edit.properties" /></span></td>
            <td width="15%" class="tabtd"><span class="tab1" id="tabMain2" onmouseover="tabMouseOver(this);" onmouseout="tabMouseOut(this);"
                onclick="selectTab1(this);" title=<bean:message key="edit.reporttitle" />><bean:message key="edit.reports" /></span></td>
            <td width="15%" class="tabtd"><span class="tab1" id="tabMain3" onmouseover="tabMouseOver(this);" onmouseout="tabMouseOut(this);"
                onclick="selectTab1(this);" title=<bean:message key="edit.criteriatitle" />><bean:message key="edit.search" /></span></td>
            <td width="15%" class="tabtd"><span class="tab1" id="tabMain4" onmouseover="tabMouseOver(this);" onmouseout="tabMouseOut(this);"
                onclick="selectTab1(this);" title=<bean:message key="edit.monitortitle" />><bean:message key="edit.monitors" /></span></td>
            <td width="40%" class="tabtd"><span class="tabend">&nbsp;</span></td>
        </tr></table>

        <table style="border: 1px solid black; border-top: 0px solid black; width: 100%; border-spacing: 0px; border-collapse: collapse">
        <colgroup></colgroup><tbody class="secttbody" /><tr><td>

        <div id="tabProp"><table class="table1" cellpadding="7"><tr><td>
            <p class="bstd6"><span class="rstd">&#119;</span><bean:message key="edit.name" /><br><html:text styleClass="std"
                property="propName" size="70" maxlength="30" onchange="nameChanged(this.value);" /></p>
            <p class="bstd6" style="color: #888888"><bean:message key="edit.summary" /><br><html:textarea styleClass="sstd100"
                property="propDesc" rows="8" style="color: #888888" readonly="true"></html:textarea></p>

            <p class="bstd6"><bean:message key="edit.runoptions" /></p>
            <p class="bstd12" style="margin-left: 0.2in"><bean:message key="edit.freq" /></p>
            <table style="margin-left: 0.2in">
                <colgroup><col/><col/><col/></colgroup><tbody/><tr>
                <td class="td1" align="left" width="33%"><p><html:radio property="freqUnit" value="D" onclick="setFreq(this.value);"/><span class="std" title=<bean:message key="edit.fudtitle" />><bean:message key="edit.daily" /></span></p></td>
                <td class="td1" align="center" width="34%"><p><html:radio property="freqUnit" value="W" onclick="setFreq(this.value);" /><span class="std" title=<bean:message key="edit.fuwtitle" />><bean:message key="edit.weekly" /></span>&nbsp;
                    <html:select property="freqWeekly" styleClass="std" disabled="true" size="1">
                        <html:option value="1" key="edit.wsun"></html:option>
                        <html:option value="2" key="edit.wmon"></html:option>
                        <html:option value="3" key="edit.wtue"></html:option>
                        <html:option value="4" key="edit.wwed"></html:option>
                        <html:option value="5" key="edit.wthu"></html:option>
                        <html:option value="6" key="edit.wfri"></html:option>
                        <html:option value="7" key="edit.wsat"></html:option>
                    </html:select></p>
                </td>
                <td class="td1" align="right" width="33%"><p><html:radio property="freqUnit" value="M" onclick="setFreq(this.value);" /><span class="std" title=<bean:message key="edit.fumtitle" />><bean:message key="edit.monthly" /></span>&nbsp;
                    <html:select property="freqMonthly" styleClass="std" disabled="true" size="1">
                    <html:option value="1">1st</html:option>
                    <html:option value="2">2nd</html:option>
                    <html:option value="3">3rd</html:option>
                    <html:option value="4">4th</html:option>
                    <html:option value="5">5th</html:option>
                    <html:option value="6">6th</html:option>
                    <html:option value="7">7th</html:option>
                    <html:option value="8">8th</html:option>
                    <html:option value="9">9th</html:option>
                    <html:option value="10">10th</html:option>
                    <html:option value="11">11th</html:option>
                    <html:option value="12">12th</html:option>
                    <html:option value="13">13th</html:option>
                    <html:option value="14">14th</html:option>
                    <html:option value="15">15th</html:option>
                    <html:option value="16">16th</html:option>
                    <html:option value="17">17th</html:option>
                    <html:option value="18">18th</html:option>
                    <html:option value="19">19th</html:option>
                    <html:option value="20">20th</html:option>
                    <html:option value="21">21st</html:option>
                    <html:option value="22">22nd</html:option>
                    <html:option value="23">23rd</html:option>
                    <html:option value="24">24th</html:option>
                    <html:option value="25">25th</html:option>
                    <html:option value="26">26th</html:option>
                    <html:option value="27">27th</html:option>
                    <html:option value="28">28th</html:option>
                    <html:option value="29">29th</html:option>
                    <html:option value="30">30th</html:option>
                    <html:option value="31">31st</html:option>
                    </html:select></p>
                </td>
            </tr></table><br/>
            <p class="bstd12" style="margin-left: 0.2in"><bean:message key="edit.status" /></p>
            <p class="std0" style="margin-left: 0.2in"><html:radio property="propStatus" value="A" /><bean:message key="edit.statusa" /></p>
            <p class="std0" style="margin-left: 0.2in"><html:radio property="propStatus" value="F" /><bean:message key="edit.statusf" /></p>
            <p class="std0" style="margin-left: 0.2in"><html:radio property="propStatus" value="D" /><bean:message key="edit.statusd1" />
                <html:text style="text-align: center" size="10" property="propBeginDate" /><bean:message key="edit.statusd2" />
                <html:text style="text-align: center" size="10" property="propEndDate" /><span style="color: <%=color%>"><bean:message key="edit.statusd3" /></span></p>
            <p class="std0" style="margin-left: 0.2in"><html:radio property="propStatus" value="I" onclick="highlightReason();" /><bean:message key="edit.statusi1" />
                <html:text styleClass="std" size="50" maxlength="2000" property="propStatusReason" onchange="checkReason(value);" /></p>

            <hr class="hrs"><table cellspacing="0" cellpadding="0" width="100%"><tr>
                <td width="50%"><p class="bstd12" style="color: <%=color%>"><bean:message key="edit.creator" /><br><html:text styleClass="std"
                    property="propCreator" disabled="true" size="30" /></p></td>
                <td><p class="bstd12" style="color: <%=color%>"><bean:message key="edit.created" /><br><html:text styleClass="std" style="text-align: center"
                    property="propCreateDate" disabled="true" size="25" /></p></td>
                </tr><tr>
                <td><p class="bstd12" style="color: <%=color%>"><bean:message key="edit.lastrun" /><br><html:text styleClass="std" style="text-align: center"
                    property="propLastRunDate" disabled="true" size="25" /></p></td>
                <td><p class="bstd12" style="color: <%=color%>"><bean:message key="edit.modified" /><br><html:text styleClass="std" style="text-align: center"
                    property="propModifyDate" disabled="true" size="25" /></p></td>
            </tr></table>
        </td></tr></table></div>

        </td></tr></table>

        <div id="btmButs">
        <table class="table3">
        <colgroup><col style="text-align: left" /><col style="text-align: right" /></colgroup><tbody class="secttbody" /><tr>
                <td>
                    <html:button styleClass="but1" property="save2" onclick="cmdSave();"><bean:message key="all.save" /></html:button>
                    <html:button styleClass="but1" property="run2" onclick="cmdRun();"><bean:message key="all.run" /></html:button>
                    <html:button styleClass="but1" property="clear2" onclick="cmdClear();"><bean:message key="all.clear" /></html:button>
                    <html:button styleClass="but2" property="back2" onclick="cmdBack();"><bean:message key="all.back" /></html:button>
                </td>
                <td>
                    <html:button property="logout2" styleClass="but1" onclick="cmdLogout();"><bean:message key="all.logout" /></html:button>
                    <html:button property="help2" styleClass="but2" onclick="cmdHelp();"><bean:message key="all.help" /></html:button>
                </td>
        </table>
        </div>
        <dtags:foot />
    </td></tr></table>
    </html:form>
</body>
</html>
