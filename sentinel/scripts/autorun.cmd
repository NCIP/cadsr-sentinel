rem #!/bin/bash

echo "Executing Auto Run for Sentinel Tool"
echo "\$Header: /share/content/gforge/sentinel/sentinel/scripts/autorun.cmd,v 1.2 2008-04-23 18:17:10 hebell Exp $"
echo "\$Name: not supported by cvs2svn $"

setlocal

rem DATE=`date +%Y%m%d`
set JAVA_HOME=d:/Java/jdk1.5.0_10
set BASE_DIR=.

rem export JAVA_HOME BASE_DIR

rem ORACLE_HOME=/app/oracle/product/dbhome/9.2.0
rem PATH=$ORACLE_HOME/bin:$PATH
rem LD_LIBRARY_PATH=$ORACLE_HOME/lib:$LD_LIBRARY_PATH
rem TNS_ADMIN=$ORACLE_HOME/network/admin
set JAVA_PARMS='-Xms512m -Xmx512m -XX:PermSize=64m'

rem export JAVA_PARMS ORACLE_HOME TNS_ADMIN PATH LD_LIBRARY_PATH

echo "Executing job as `id`"
echo "Executing on `date`"

rem find %BASE_DIR%/../reports -mtime +20 -exec rm {} \;

%JAVA_HOME%/bin/java -client %JAVA_PARMS% -classpath %BASE_DIR%:%BASE_DIR%/commons-logging-1.1.jar:%BASE_DIR%/log4j-1.2.14.jar:%BASE_DIR%/jdom-1.0.jar:%BASE_DIR%/stax-api-1.0.1.jar:%BASE_DIR%/mail.jar:%BASE_DIR%/activation.jar:%BASE_DIR%/ojdbc14.jar:%BASE_DIR%/evsapi41-beans.jar:%BASE_DIR%/evsapi41-framework.jar:%BASE_DIR%/acegi-security-1.0.4.jar:%BASE_DIR%/sdk-client-framework.jar:%BASE_DIR%/hibernate3.jar:%BASE_DIR%/spring.jar:%BASE_DIR%/cglib-2.1.3.jar:%BASE_DIR%/asm.jar:%BASE_DIR%/cadsrsentinel.jar gov.nih.nci.cadsr.sentinel.tool.AutoProcessAlerts %BASE_DIR%/log4j.xml true %BASE_DIR%/cadsrsentinel.xml

endlocal
