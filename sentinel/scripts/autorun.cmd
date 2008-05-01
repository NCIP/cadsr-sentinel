echo $Header: /share/content/gforge/sentinel/sentinel/scripts/autorun.cmd,v 1.1 2007-07-19 15:26:52 hebell Exp $
echo $Name: not supported by cvs2svn $
%JAVA_HOME%\bin\java -client -classpath %BASE_DIR%\mail.jar;%BASE_DIR%\activation.jar;%BASE_DIR%\ojdbc14.jar;%BASE_DIR%\DSRAlert.jar gov.nih.nci.cadsr.sentinel.tool.AutoProcessAlerts
