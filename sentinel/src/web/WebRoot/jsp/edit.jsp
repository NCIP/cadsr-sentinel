<!-- Copyright ScenPro, Inc. 2005
     $Header: /share/content/gforge/sentinel/sentinel/WebRoot/jsp/edit.jsp,v 1.7 2009-08-31 19:12:18 davet Exp $
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
        <html:base />
        <meta http-equiv="Content-Language" content="en-us">
        <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=WINDOWS-1252">
        <meta HTTP-EQUIV="Pragma" CONTENT="no-cache">
        <meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
        <LINK href="/cadsrsentinel/css/sentinel.css" rel="stylesheet" type="text/css">
    </head>

<body onload="loaded()">
<%
    String color = "#999999";
%>
    <dtags:edit section="init" />

    <SCRIPT LANGUAGE="JavaScript" src="/cadsrsentinel/js/edit.js"></script>
    <SCRIPT LANGUAGE="JavaScript">
        <dtags:edit section="script" />
    </script>

    <html:form method="post" action="/edit">
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
            <td width="15%" class="tabtd"><p class="tab0" id="tabMain1" onmouseover="tabMouseOver(this);" onmouseout="tabMouseOut(this);"
                onclick="selectTab1(this);" title=<bean:message key="edit.propertytitle" />><bean:message key="edit.properties" /></p></td>
            <td width="15%" class="tabtd"><p class="tab1" id="tabMain2" onmouseover="tabMouseOver(this);" onmouseout="tabMouseOut(this);"
                onclick="selectTab1(this);" title=<bean:message key="edit.reporttitle" />><bean:message key="edit.reports" /></p></td>
            <td width="15%" class="tabtd"><p class="tab1" id="tabMain3" onmouseover="tabMouseOver(this);" onmouseout="tabMouseOut(this);"
                onclick="selectTab1(this);" title=<bean:message key="edit.criteriatitle" />><bean:message key="edit.search" /></p></td>
            <td width="15%" class="tabtd"><p class="tab1" id="tabMain4" onmouseover="tabMouseOver(this);" onmouseout="tabMouseOut(this);"
                onclick="selectTab1(this);" title=<bean:message key="edit.monitortitle" />><bean:message key="edit.monitors" /></p></td>
            <td width="40%" class="tabtd"><p class="tabend">&nbsp;</p></td>
        </tr></table>

        <table style="border: 1px solid black; border-top: 0px solid black; width: 100%; border-spacing: 0px; border-collapse: collapse">
        <colgroup></colgroup><tbody class="secttbody" /><tr><td>
        <div id="tabProp"><table class="table1" cellpadding="7"><tr><td>
            <p class="bstd6"><span class="rstd">*</span><bean:message key="edit.name" /><br><html:text styleClass="std"
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

        <div id="tabMon" style="visibility: hidden">
        <table class="table1">
            <colgroup>
                <col style="width: 33%; padding: 0.1in 0.1in 0.1in 0.1in" />
                <col style="width: 34%; padding: 0.1in 0.1in 0.1in 0.1in" />
                <col style="width: 33%; padding: 0.1in 0.1in 0.1in 0.1in" />
            </colgroup>
            <tbody />
            <tr>
                <td class="td1"><p class="bstd6"><bean:message key="edit.mwfs" /><br>
                    <html:select styleClass="sstd100" property="actWorkflowStatus" size="6" multiple="true" onchange="fixXStatus(this);">
                        <html:option value="0">xxx</html:option></html:select></p>
                </td><td class="td1"><p class="bstd6"><bean:message key="edit.mrs" /><br>
                    <html:select styleClass="sstd100" property="actRegStatus" size="6" multiple="true" onchange="fixXStatus(this);">
                        <html:option value="0">xxx</html:option></html:select></p>
                </td><td class="td1"><p class="bstd6"><bean:message key="edit.mver" /><br><span class="std">
                    <html:radio property="actVersion" value="C" onclick="setActVerNum(this.value);" /><bean:message key="edit.mvera" /><br>
                    <html:radio property="actVersion" value="M" onclick="setActVerNum(this.value);" /><bean:message key="edit.mverm" /><br>
                    <html:radio property="actVersion" value="I" onclick="setActVerNum(this.value);" /><bean:message key="edit.mveri" /><br>
                    <html:radio property="actVersion" value="S" onclick="setActVerNum(this.value);" /><bean:message key="edit.mvers" />
                    <html:text styleClass="std" property="actVerNum" disabled="true" size="6" />
                    </span></p>
                </td>
            </tr><tr>
                <td colspan="3"><p class="std12"><bean:message key="edit.actdoc0" /></p><ul class="std12">
                        <li class="std6"><bean:message key="edit.actdoc1" /></li>
                        <li class="std6"><bean:message key="edit.actdoc2" /></li>
                        <li class="std6"><bean:message key="edit.actdoc3" /></li>
                        <li class="std6"><bean:message key="edit.actdoc4" /></li>
                        <li class="std6"><bean:message key="edit.actdoc5" /></li>
                    </ul>
                </td>
            </tr>
        </table></div>

        <div id="tabReport" style="visibility: hidden">
        <table class="table1" cellpadding="7"><tr><td>
            <table class="secttable">
                <colgroup><col /></colgroup>
                <tbody class="secttbody" />
                <tr>
                    <td><p class="bstd6"><bean:message key="edit.content" /></p>
                    </td>
                </tr><tr>
                    <td class="td1"><p class="std"><html:checkbox property="repIncProp" value="Y" /><bean:message key="edit.incprop" />
                        <!--<br><br>
                            <html:radio property="repStyle" value="A" /><bean:message key="edit.stylea" /><br>
                            <html:radio property="repStyle" value="S" /><bean:message key="edit.styles" /> -->
                        </p>
                        <p class="std"><bean:message key="edit.incassoc" /><html:select styleClass="std" property="infoAssocLvl" size="1">
                                <html:option value="0">0 - Do not show Associated To</html:option>
                                <html:option value="1">1</html:option>
                                <html:option value="2">2</html:option>
                                <html:option value="3">3</html:option>
                                <html:option value="4">4</html:option>
                                <html:option value="5">5</html:option>
                                <html:option value="6">6</html:option>
                                <html:option value="7">7</html:option>
                                <html:option value="8">8</html:option>
                                <html:option value="9">9 - Show all Associated To</html:option>
                            </html:select>
                        </p>
                    </td>
                </tr><tr>
                    <td class="td1"><hr class="hrs"><p align="left" class="bstd6"><bean:message key="edit.distrib" /></p>
                    </td>
                </tr><tr>
                    <td class="td1"><p class="std"><html:radio property="freqEmpty" value="N" /><bean:message key="edit.distriba" /><br>
                        <html:radio property="freqEmpty" value="Y" /><bean:message key="edit.distribe" /></p>
<!--                    </td><td>&nbsp;
                    </td><td class="td1"><p class="std"><html:radio property="freqAck" value="N" /><bean:message key="edit.distribo" /><br>
                        <html:radio property="freqAck" value="Y" /><bean:message key="edit.distribr" /></p>
-->                    </td>
                </tr>
            </table>

            <br><table class="secttable">
                <colgroup>
                    <col style="width: 39%" />
                    <col style="width: 1%" />
                    <col style="width: 60%" />
                </colgroup>
                <tbody class="secttbody" />
				<tr>
					<td colspan="3"><bean:message key="edit.emailo1" /><bean:message key="edit.emailo2" /><bean:message key="edit.emailo3" /></td>
                </tr><tr>
                    <td><table class="tabtable" style="margin-top: 4pt">
                        <colgroup><col /><col /><col /></colgroup><tbody class="tabtbody" /><tr>
                        <td class="tabtd" width="10"><p id="propUList" class="tab0" onmouseover="tabMouseOver(this);" onmouseout="tabMouseOut(this);"
                            onclick="selectTab0(this);" title=<bean:message key="edit.userstitle" />><bean:message key="edit.users" /></p></td>
                        <td class="tabtd" width="10"><p id="propGList" class="tab1" onmouseover="tabMouseOver(this);" onmouseout="tabMouseOut(this);"
                            onclick="selectTab0(this);" title=<bean:message key="edit.groupstitle" />><bean:message key="edit.groups" /></p></td>
                        <td class="tabtd"><p class="tabend">&nbsp;</p></td></tr></table>
                    </td>
                    <td align="center">&nbsp;</td>
                    <td><p class="bstd6"><bean:message key="edit.recipients" /></p></td>
                </tr><tr>
                    <td class="std">
                        <html:select styleClass="sstd100" property="propUsers" size="6" multiple="true"><html:option value="0">xxxx</html:option></html:select>
                    </td><td align="center"><button class="but3" type="button" onclick="addToEmail();"><bean:message key="edit.emaila" />&nbsp;<img src="/cadsrsentinel/images/arrow_16_right.gif"></button><br>
                        <br><button class="but3" type="button" onclick="removeFromEmail();"><img src="/cadsrsentinel/images/arrow_16_left.gif">&nbsp;<bean:message key="edit.emailr" /></button>
                    </td><td class="td1"><html:select styleClass="std" property="propRecipients" size="6" multiple="true"><html:option value="0">xxx</html:option></html:select></td>
                </tr><tr>
                    <td colspan="3"><p class="bstd12"><bean:message key="edit.emailz" /><br><html:text property="propEmail" size="50" maxlength="255" styleClass="std" />
                        <button type="button" class="but3" value="Add" onclick="addFEmail();"><bean:message key="edit.emaila" /></button></p>
                    </td>
                </tr><tr>
                    <td colspan="3"><bean:message key="edit.emailo1" /><span id="exemptlist">&nbsp;</span><bean:message key="edit.emailo3" /></td>
                </tr>
            </table>

            <table class="secttable"><colgroup><col style="padding-right: 0.1in" /><col /></colgroup><tbody style="padding-top: 0.1in" />
				<tr>
                    <td class="td1"><p class="bstd"><bean:message key="edit.emailt" /></p></td>
                    <td class="td1"><p class="estd"><span id="sampleAddr">XXX</span></p></td>
                </tr><tr>
                    <td class="td1"><p class="bstd"><bean:message key="edit.emails" /></p></td>
                    <td class="td1"><p class="std"><dtags:edit section="subject" /></p></td>
                </tr><tr>
                    <td class="td1"><p class="bstd">&nbsp;</p></td>
                    <td class="td1"><html:textarea styleClass="std" property="propIntro" rows="6" cols="80"></html:textarea></td>
                </tr><tr>
                    <td>&nbsp;</td>
                    <td class="td1"><p><a target="_blank" href="/cadsrsentinel/html/samples.html"><bean:message key="edit.emailx" /></a></p></td>
                </tr>
            </table>
        </td></tr></table></div>

        <div id="tabQual" style="visibility: hidden">
        <table class="table1">
            <colgroup>
                <col style="width: 50%; padding: 0.1in 0.1in 0.1in 0.1in" />
                <col style="width: 50%; padding: 0.1in 0.1in 0.1in 0.1in" />
            </colgroup>
            <tbody />
            <tr>
                <td colspan="2" class="td1"><p class="bstd6"><bean:message key="edit.scon" /><br>
                    <html:select styleClass="sstd100" property="infoContext" size="5" multiple="true" onchange="fixListAll(this);">
                        <html:option value="0">xxx</html:option></html:select></p>
                </td>
            </tr><tr>
                <td><p class="bstd6"><bean:message key="edit.sproto" /><br>
                    <html:select styleClass="sstd100" property="infoProtos" size="5" multiple="true" onchange="fixListAll(this);">
                        <html:option value="0">xxx</html:option></html:select></p>
                </td><td><p class="bstd6"><bean:message key="edit.sform" /><br>
                    <html:select styleClass="sstd100" property="infoForms" size="5" multiple="true" onchange="fixListAll(this);">
                        <html:option value="0">xxx</html:option></html:select></p>
                </td>
            </tr><tr>
                <td><p class="bstd6"><bean:message key="edit.scs" /><br>
                    <html:select styleClass="sstd100" property="infoSchemes" size="5" multiple="true" onchange="fixListAll(this);">
                        <html:option value="0">xxx</html:option></html:select></p>
                </td><td><p class="bstd6"><bean:message key="edit.scsi" /><br>
                    <html:select styleClass="sstd100" property="infoSchemeItems" size="5" multiple="true" onchange="fixListAll(this);">
                        <html:option value="0">xxx</html:option></html:select></p>
                </td>
            </tr><tr>
                <td class="td1"><p class="bstd6"><bean:message key="edit.actypes" /><br>
                    <html:select styleClass="sstd100" property="infoACTypes" size="5" multiple="true" onchange="fixListAll(this);">
                        <html:option value="0">xxx</html:option></html:select></p>
                </td><td class="td1"><p class="bstd6"><bean:message key="edit.dfi" /><br>
                    <html:select styleClass="sstd100" property="infoDateFilter">
                        <html:option value="2"><bean:message key="edit.dfboth" /></html:option>
                        <html:option value="1"><bean:message key="edit.dfmod" /></html:option>
                        <html:option value="0"><bean:message key="edit.dfcre" /></html:option>
                    </html:select></p>
                </td>
            </tr><tr>
                <td class="td1"><p class="bstd6"><bean:message key="edit.wflows" /><br>
                    <html:select styleClass="sstd100" property="infoWorkflow" size="5" multiple="true" onchange="fixListAll(this);">
                        <html:option value="0">xxx</html:option></html:select></p>
                </td><td class="td1"><p class="bstd6"><bean:message key="edit.regs" /><br>
                    <html:select styleClass="sstd100" property="infoRegStatus" size="5" multiple="true" onchange="fixListAll(this);">
                        <html:option value="0">xxx</html:option></html:select></p>
                </td>
            </tr><tr>
                <td class="td1"><p class="bstd6"><bean:message key="edit.sc" /><br>
                    <html:select styleClass="sstd100" property="infoCreator" size="5" multiple="true" onchange="fixListAll(this);">
                        <html:option value="0">xxx</html:option></html:select></p>
                </td><td class="td1"><p class="bstd6"><bean:message key="edit.sm" /><br>
                    <html:select styleClass="sstd100" property="infoModifier" size="5" multiple="true" onchange="fixListAll(this);">
                        <html:option value="0">xxx</html:option></html:select></p>
                </td>
            </tr>
        </table></div>

		<div id="blankSpaces" style="visibility: hidden">
			<table><tr><td>
				<p><br/>&nbsp;&nbsp;<br/></p>
				<p><br/>&nbsp;&nbsp;<br/></p>
				<p><br/>&nbsp;&nbsp;<br/></p>				
			</td></tr></table>
		</div>

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
    <head>
        <meta HTTP-EQUIV="Pragma" CONTENT="no-cache">
        <meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
    </head>
</html>
