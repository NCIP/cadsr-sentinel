<%--L
  Copyright ScenPro Inc, SAIC-F

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/cadsr-sentinal/LICENSE.txt for details.
L--%>

<!--
    Copyright 2007, ScenPro, Inc
    
    $Header: /share/content/gforge/sentinel/sentinel/WebRoot/jsp/index.jsp,v 1.2 2009-04-08 17:56:18 hebell Exp $
    $Name: not supported by cvs2svn $
-->
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@ page contentType="text/html" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:forward page="/do/logon" />

