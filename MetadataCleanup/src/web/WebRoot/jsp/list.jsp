<%--L
  Copyright ScenPro Inc, SAIC-F

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
L--%>

<!-- Copyright ScenPro, Inc. 2005
     $Header: /share/content/gforge/sentinel/sentinel/WebRoot/jsp/list.jsp,v 1.5 2009-04-08 17:56:18 hebell Exp $
     $Name: not supported by cvs2svn $
-->
<%@ page contentType="text/html;charset=WINDOWS-1252"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/dsralert" prefix="dtags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title><bean:message key="list.title" /></title>
        
        <div style="position:absolute;">
 			<a href="#skip">
  			<img src="/cadsrsentinel/images/skipnav.gif" border="0" height="1" width="1" alt="Skip Navigation" title="Skip Navigation" />
	 		</a>
		</div>
        
        <html:base />
        <meta http-equiv="Content-Language" content="en-us">
        <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=WINDOWS-1252">
        <meta HTTP-EQUIV="Pragma" CONTENT="no-cache">
        <meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
        <LINK href="/cadsrsentinel/css/sentinel.css" rel="stylesheet" type="text/css">
    </head>

<body onload="loaded();">
    <dtags:list section="init" />

    <script language="javascript" src="/cadsrsentinel/js/list.js"></script>
    <script language="javascript">
        <dtags:list section="script" />
    </script>

	<a name="skip" id="skip"></a>

    <html:form method="post" action="/list">
    <html:hidden property="nextScreen" />
    <dtags:list section="field"/>

    <table class="secttable"><colgroup></colgroup><tbody class="secttbody" />
    <tr><td align="center">

        <dtags:head key="list.title" />
        <table class="table3" style="margin-bottom: 0.1in">
        <colgroup><col style="text-align: left" /><col style="text-align: right" /></colgroup><tbody class="secttbody" /><tr>
            <td id="cmdButsTop">
                    <input type="button" class="but1" name="c1" onclick="cmdCreate();" value="<bean:message key='all.create'/>" />
                    <input type="button" class="but1" name="e1" onclick="cmdEdit();" cstTestSingle="true" value="<bean:message key='all.edit'/>" />
                    <input type="button" class="but1a" name="u1" onclick="cmdNewFrom();" cstTestSingle="true" value="<bean:message key='all.createusing'/>" />
                    <input type="button" class="but1" name="d1" onclick="cmdDelete();" cstTestMulti="true" value="<bean:message key='all.delete'/>" />
                    <input type="button" class="but2" name="r1" onclick="cmdRun();" cstTestSingle="true" value="<bean:message key='all.run'/>" />
            </td><td>
                <dtags:list section="button" />
                <html:button property="logout1" styleClass="but1" onclick="cmdLogout();"><bean:message key="all.logout" /></html:button>
                <html:button property="help1" styleClass="but2" onclick="cmdHelp();"><bean:message key="all.help" /></html:button>
            </td>
        </tr></table>

        <div id="debugText" style="display: none"></div>
        <dtags:list section="table" />
        <dtags:list section="info" />

        <table class="table3">
        <colgroup><col style="text-align: left" /><col style="text-align: right" /></colgroup><tbody class="secttbody" /><tr>
            <tr>
                <td id="cmdButsBtm">
                    <input type="button" class="but1" name="c2" onclick="cmdCreate();" value="<bean:message key='all.create'/>" />
                    <input type="button" class="but1" name="e2" onclick="cmdEdit();" cstTestSingle="true" value="<bean:message key='all.edit'/>" />
                    <input type="button" class="but1a" name="u2" onclick="cmdNewFrom();" cstTestSingle="true" value="<bean:message key='all.createusing'/>" />
                    <input type="button" class="but1" name="d2" onclick="cmdDelete();" cstTestMulti="true" value="<bean:message key='all.delete'/>" />
                    <input type="button" class="but2" name="r2" onclick="cmdRun();" cstTestSingle="true" value="<bean:message key='all.run'/>" />
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
    <head>
        <meta HTTP-EQUIV="Pragma" CONTENT="no-cache">
        <meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
    </head>
</html>
