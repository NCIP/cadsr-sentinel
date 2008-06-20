<!-- Copyright ScenPro, Inc. 2005
     $Header: /share/content/gforge/sentinel/sentinel/WebRoot/jsp/logon.jsp,v 1.5 2008-06-20 20:12:51 hebell Exp $
     $Name: not supported by cvs2svn $
-->
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/dsralert" prefix="dtags" %>

<%@ page import="gov.nih.nci.cadsr.sentinel.ui.AlertPlugIn" %>

<html>
    <head>
        <title><bean:message key="logon.title" /></title>
        <html:base />
        <meta http-equiv="Content-Language" content="en-us">
        <meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=WINDOWS-1252">
        <meta HTTP-EQUIV="Pragma" CONTENT="no-cache">
        <LINK href="/cadsrsentinel/css/sentinel.css" rel="stylesheet" type="text/css">
    </head>

<body>

    <script language="javascript">
        function cmdLogon()
        {
            msg.innerText = <bean:message key="logon.msg" />;
        }
        function cmdHelp()
        {
        <%
            AlertPlugIn api = pageContext.getServletContext().getAttribute(DBAlert._DATASOURCE);
        %>
            window.open("<%=api.getHelpUrl()%>", "_blank");
        }
    </script>

    <html:form method="post" action="/logon" focus="userid">

    <table class="secttable"><colgroup></colgroup><tbody class="secttbody" /><tr><td align="center">

        <dtags:head key="logon.title" />
        <bean:message key="logon.back" />
        <table summary=<bean:message key="logon.credits" />>
            <tr>
                <td valign="middle"><bean:message key="logon.userid" /></td>
                <td valign="middle"><html:text property="userid" styleClass="std" style="width: 3.75in" /></td>
            </tr><tr>
            <tr>
                <td valign="middle"><bean:message key="logon.pswd" /></td>
                <td valign="middle"><html:password property="pswd" styleClass="std" style="width: 3.75in" /></td>
            </tr><tr>
                <td colspan="2" valign="middle"><html:errors /></td>
            </tr><tr>
                <td valign="bottom"><html:submit property="logon" styleClass="but2" onclick="cmdLogon();"><bean:message key="logon.logon" /></html:submit></td>
                <td valign="bottom" align="right"><html:button styleClass="but2" property="help" onclick="cmdHelp();"><bean:message key="all.help" /></html:button></td>
            </tr>
        </table>
        
        <dtags:foot />
    </td></tr></table>
    </html:form>
</body>
    <head>
        <meta HTTP-EQUIV="Pragma" CONTENT="no-cache">
    </head>
</html>
