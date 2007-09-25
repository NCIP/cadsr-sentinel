<!-- Copyright ScenPro, Inc. 2005
     $Header: /share/content/gforge/sentinel/sentinel/WebRoot/jsp/list.jsp,v 1.2 2007-09-25 14:26:46 hebell Exp $
     $Name: not supported by cvs2svn $
-->
<%@ page contentType="text/html;charset=WINDOWS-1252"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/dsralert" prefix="dtags" %>

<html>
    <head>
        <title><bean:message key="list.title" /></title>
        <html:base />
        <meta http-equiv="Content-Language" content="en-us">
        <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=WINDOWS-1252">
        <LINK href="/cadsrsentinel/css/sentinel.css" rel="stylesheet" type="text/css">
    </head>

<body onload="loaded();">
    <dtags:list section="init" />

    <script language="javascript" src="/cadsrsentinel/js/list.js"></script>
    <script language="javascript">
        <dtags:list section="script" />
    </script>

    <html:form method="post" action="/list">
    <html:hidden property="nextScreen" />
    <dtags:list section="field"/>

    <table class="secttable"><colgroup></colgroup><tbody class="secttbody" />
    <tr><td align="center">

        <dtags:head key="list.title" />
        <table class="table3" style="margin-bottom: 0.1in">
        <colgroup><col style="text-align: left" /><col style="text-align: right" /></colgroup><tbody class="secttbody" /><tr>
            <td id="cmdButsTop">
                    <html:button styleClass="but1" property="c1" onclick="cmdCreate();"><bean:message key="all.create" /></html:button>
                    <html:button styleClass="but1" property="e1" onclick="cmdEdit();"><bean:message key="all.edit" /></html:button>
                    <html:button styleClass="but1a" property="u1" onclick="cmdNewFrom();"><bean:message key="all.createusing" /></html:button>
                    <html:button styleClass="but1" property="d1" onclick="cmdDelete();"><bean:message key="all.delete" /></html:button>
                    <html:button styleClass="but2" property="r1" onclick="cmdRun();"><bean:message key="all.run" /></html:button>
            </td><td>
                <dtags:list section="button" />
                <html:button property="logout1" styleClass="but1" onclick="cmdLogout();"><bean:message key="all.logout" /></html:button>
                <html:button property="help1" styleClass="but2" onclick="cmdHelp();"><bean:message key="all.help" /></html:button>
            </td>
        </tr></table>

        <dtags:list section="table" />
        <dtags:list section="info" />

        <table class="table3">
        <colgroup><col style="text-align: left" /><col style="text-align: right" /></colgroup><tbody class="secttbody" /><tr>
            <tr>
                <td id="cmdButsBtm">
                    <html:button styleClass="but1" property="c2" onclick="cmdCreate();"><bean:message key="all.create" /></html:button>
                    <html:button styleClass="but1" property="e2" onclick="cmdEdit();"><bean:message key="all.edit" /></html:button>
                    <html:button styleClass="but1a" property="u2" onclick="cmdNewFrom();"><bean:message key="all.createusing" /></html:button>
                    <html:button styleClass="but1" property="d2" onclick="cmdDelete();"><bean:message key="all.delete" /></html:button>
                    <html:button styleClass="but2" property="r2" onclick="cmdRun();"><bean:message key="all.run" /></html:button>
                </td><td>
                    <dtags:list section="button" />
                    <html:button property="logout2" styleClass="but1" onclick="cmdLogout();"><bean:message key="all.logout" /></html:button>
                    <html:button property="help2" styleClass="but2" onclick="cmdHelp();"><bean:message key="all.help" /></html:button>
                </td>
            </tr>
        </table>
        <dtags:foot />
    </td></tr></table>
    </html:form>
</body>
</html>
