<!-- Copyright ScenPro, Inc. 2005
     $Header: /share/content/gforge/sentinel/sentinel/WebRoot/jsp/create.jsp,v 1.3 2008-11-25 18:26:26 hebell Exp $
     $Name: not supported by cvs2svn $
-->
<%@ page contentType="text/html;charset=WINDOWS-1252"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/dsralert" prefix="dtags" %>
<%@ page import="java.util.*" %>

<html>
    <head>
        <title><bean:message key="create.title" /></title>
        <html:base />
        <meta http-equiv="Content-Language" content="en-us">
        <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=WINDOWS-1252">
        <meta HTTP-EQUIV="Pragma" CONTENT="no-cache">
        <meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
        <LINK href="/cadsrsentinel/css/sentinel.css" rel="stylesheet" type="text/css">
    </head>

<body onload="loaded();">

    <script language="javascript">
        <dtags:create section="script" />
    </script>
    <script language="javascript" src="/cadsrsentinel/js/create.js"></script>

    <html:form method="post" action="/create" focus="propName">
    <dtags:create section="field" />
    <html:hidden property="nextScreen" />

    <table class="secttable"><colgroup></colgroup><tbody class="secttbody" />
    <tr><td align="center">

        <dtags:head key="create.title" />
        <table class="table3">
        <colgroup><col style="text-align: left" /><col style="text-align: right" /></colgroup><tbody class="secttbody" /><tr>
            <td>
                <html:submit styleClass="but1" property="save1" onclick="cmdSave();"><bean:message key="all.save" /></html:submit>
                <html:button styleClass="but1" property="edit1" onclick="cmdEdit();"><bean:message key="all.edit" /></html:button>
                <html:button styleClass="but2" property="back1" onclick="cmdCancel();"><bean:message key="all.back" /></html:button>
            </td><td>
                <html:button styleClass="but1" property="butList1" onclick="cmdLogout();"><bean:message key="all.logout" /></html:button>
                <html:button styleClass="but2" property="help1" onclick="cmdHelp();"><bean:message key="all.help" /></html:button>
            </td>
        </tr></table>

        <p class="std12" align="left"><bean:message key="create.line1" /></p>
        <p class="bstd" style="text-align: left; margin-left: 0.5in"><bean:message key="create.line2" /><br><html:text property="propName" styleClass="std" size="70" maxlength="30" /></p>
        <p class="std12" align="left"><br><bean:message key="create.line3" /></p>
        <p class="std05"><html:radio property="initial" value="1" onclick="setBlank(this.value)" />&nbsp;<bean:message key="create.line4" /></p>
<!--        <p class="std05"><html:radio property="initial" value="2" onclick="setBlank(this.value)" />&nbsp;<bean:message key="create.line5" /></p>
        <p class="std05"><html:radio property="initial" value="3" onclick="setBlank(this.value)" />&nbsp;<bean:message key="create.line6" /></p>
        <p class="std05"><html:radio property="initial" value="4" onclick="setBlank(this.value)" />&nbsp;<bean:message key="create.line7" /></p>
        <p class="std05"><html:radio property="initial" value="5" onclick="setBlank(this.value)" />&nbsp;<bean:message key="create.line8" /></p>
-->        <p class="std05"><html:radio property="initial" value="6" onclick="setBlank(this.value)" />&nbsp;<bean:message key="create.line9" /></p>
        <p class="std05"><br><html:radio property="initial" value="0" onclick="setBlank(this.value)" />&nbsp;<bean:message key="create.line10" /></p>

        <p class="bstd" style="text-align: left; margin-left: 0.5in; color: #888888"
            >Summary:<br><html:textarea styleClass="std" property="propDesc" cols="90" rows="5" style="color: #888888" readonly="true"></html:textarea></p>

        <table class="table3">
        <colgroup><col style="text-align: left" /><col style="text-align: right" /></colgroup><tbody class="secttbody" /><tr>
            <td>
                <html:submit styleClass="but1" property="save2" onclick="cmdSave();"><bean:message key="all.save" /></html:submit>
                <html:button styleClass="but1" property="edit2" onclick="cmdEdit();"><bean:message key="all.edit" /></html:button>
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
    <head>
        <meta HTTP-EQUIV="Pragma" CONTENT="no-cache">
        <meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
    </head>
</html>
