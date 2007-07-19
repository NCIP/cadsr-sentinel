<!-- Copyright ScenPro, Inc. 2005
     $Header: /share/content/gforge/sentinel/sentinel/WebRoot/jsp/run.jsp,v 1.1 2007-07-19 15:26:46 hebell Exp $
     $Name: not supported by cvs2svn $
-->
<%@ page contentType="text/html;charset=WINDOWS-1252" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/dsralert" prefix="dtags" %>
<%@ page import="java.util.*" %>

<html>
    <head>
        <title><bean:message key="run.title" /></title>
        <html:base />
        <meta http-equiv="Content-Language" content="en-us">
        <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=WINDOWS-1252">
        <LINK href="../css/sentinel.css" rel="stylesheet" type="text/css">
    </head>

<body onload="loaded();">

    <script language="javascript">
        <dtags:run section="script" />
    </script>
    <script language="javascript" src="../js/run.js"></script>

    <html:form method="post" action="/run" focus="startDate">
    <html:hidden property="nextScreen" />

    <table class="secttable"><colgroup></colgroup><tbody class="secttbody" />
    <tr><td align="center">

        <dtags:head key="run.title" />
        <table class="table3">
        <colgroup><col style="text-align: left" /><col style="text-align: right" /></colgroup><tbody class="secttbody" /><tr>
            <td>
                <html:button styleClass="but1" property="save1" onclick="cmdSubmit();"><bean:message key="all.submit" /></html:button>
                <html:button styleClass="but2" property="back1" onclick="cmdCancel();"><bean:message key="all.back" /></html:button>
            </td><td>
                <html:button styleClass="but1" property="butList1" onclick="cmdLogout();"><bean:message key="all.logout" /></html:button>
                <html:button styleClass="but2" property="help1" onclick="cmdHelp();"><bean:message key="all.help" /></html:button>
            </td>
        </tr></table>

        <p class="std12" style="margin-left: 0.5in; text-align: left"><bean:message key="run.note" /></p>
        <table style="margin-left: 0.5in; text-align: left">
            <colgroup><col/><col/></colgroup><tbody/>
			<tr><td valign="middle"><html:button styleClass="but2" property="setToday" onclick="setDates(0);"><bean:message key="run.setDef" /></html:button>
			</td><td align="left"><bean:message key="run.default" />
			</td></tr>
			<tr><td valign="middle"><html:button styleClass="but2" property="setToday" onclick="setDates(1);"><bean:message key="run.setToday" /></html:button>
			</td><td align="left"><bean:message key="run.today" />
			</td></tr>
			<tr><td valign="middle"><html:button styleClass="but2" property="setToday" onclick="setDates(2);"><bean:message key="run.setYest" /></html:button>
			</td><td align="left"><bean:message key="run.yesterday" />
			</td></tr>
		</table><br/>
        <table style="margin-left: 0.5in; text-align: left">
            <colgroup><col/><col/></colgroup><tbody/>
            <tr><td valign="middle"><bean:message key="run.start" /></td>
                <td valign="middle"><html:text styleClass="std" style="text-align: center" size="10" property="startDate" /></td></tr>
            <tr><td valign="middle"><bean:message key="run.end" /></td>
                <td valign="middle"><html:text styleClass="std" style="text-align: center" size="10" property="endDate" /></td></tr>
            <tr><td valign="middle" colspan="2"><p class="bstd12"><html:radio property="recipients" value="C" /><bean:message key="run.creator" /></p></td></tr>
            <tr><td valign="middle" colspan="2"><p class="bstd"><html:radio property="recipients" value="A" /><bean:message key="run.recipients" /></p></td></tr>
        </table>
        <br/>

        <table class="table3">
        <colgroup><col style="text-align: left" /><col style="text-align: right" /></colgroup><tbody class="secttbody" /><tr>
            <td>
                <html:button styleClass="but1" property="save2" onclick="cmdSubmit();"><bean:message key="all.submit" /></html:button>
                <html:button styleClass="but2" property="back2" onclick="cmdCancel();"><bean:message key="all.back" /></html:button>
            </td><td>
                <html:button styleClass="but1" property="butList2" onclick="cmdLogout();"><bean:message key="all.logout" /></html:button>
                <html:button styleClass="but2" property="help2" onclick="cmdHelp();"><bean:message key="all.help" /></html:button>
            </td>
        </tr></table>
        <dtags:foot />
    </td></tr></table>
    </html:form>
</body>
</html>
