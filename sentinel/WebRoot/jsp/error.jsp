<!-- Copyright ScenPro, Inc. 2005
     $Header: /share/content/gforge/sentinel/sentinel/WebRoot/jsp/error.jsp,v 1.1 2007-07-19 15:26:46 hebell Exp $
     $Name: not supported by cvs2svn $
-->
<%@ page contentType="text/html;charset=windows-1252"%>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<%
    String title = (String)request.getAttribute("errorTitle");
%>
    <title><%=title%></title>
        <LINK href="../css/sentinel.css" rel="stylesheet" type="text/css">
  </head>
  <body>
    <form name="theForm" method="get" action="../do/logon">
    <table cellspacing="0" cellpadding="0" width="100%" align="center"><tr><td align="center">
<table width="100%" border="0" cellspacing="0" cellpadding="0" bgcolor="#A90101">
<tr bgcolor="#A90101">
<td valign="middle" align="left"><a href="http://www.cancer.gov" target="_blank" alt="NCI Logo" title="National Cancer Institute">
<img src="brandtype.gif" border="0"></a></td>
<td valign="middle" align="right"><a href="http://www.cancer.gov" target="_blank" alt="NCI Logo" title="National Cancer Institute">
<img src="tagline_nologo.gif" border="0"></a></td></tr>
</table>
<table class="secttable"><colgroup><col /></colgroup><tbody class="secttbody" />
<tr><td><a target="_blank" href="http://ncicb.nci.nih.gov/NCICB/infrastructure/cacore_overview/cadsr"><img style="border: 0px solid black" title="NCICB caDSR" src="sentinel_banner.gif"></a></td></tr>
</table>
        <p class="std12" align="justify">The caDSR Sentinel Tool encountered a problem from which it can not
        recover.  Please note the message below and select the Logon button to restart your
        session.
        </p>
<%
            String msg = (String)request.getAttribute("errorMsg");
%>
        <p id="message" class="bstd12" align="left"><%=msg%></p>
        <p class="std12"><button type="submit">Logon</button></p>
    </table>
    </form>
  </body>
</html>
