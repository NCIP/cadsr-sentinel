#!/bin/bash

echo "Executing Auto Run for Sentinel Tool"
echo "\$Header: /share/content/gforge/sentinel/sentinel/scripts/autorun.sh,v 1.3 2007-07-19 15:26:52 hebell Exp $"
echo "\$Name: not supported by cvs2svn $"

DATE=`date +%Y%m%d`
JAVA_HOME=/usr/jdk1.5.0_06
BASE_DIR=/local/content/cadsrsentinel/bin

export JAVA_HOME BASE_DIR

ORACLE_HOME=/app/oracle/product/dbhome/9.2.0
PATH=$ORACLE_HOME/bin:$PATH
LD_LIBRARY_PATH=$ORACLE_HOME/lib:$LD_LIBRARY_PATH
TNS_ADMIN=$ORACLE_HOME/network/admin
JAVA_PARMS='-Xms512m -Xmx512m -XX:PermSize=64m'

export JAVA_PARMS ORACLE_HOME TNS_ADMIN PATH LD_LIBRARY_PATH

echo "Executing job as `id`"
echo "Executing on `date`"

find $BASE_DIR/../reports -mtime +20 -exec rm {} \;

$JAVA_HOME/bin/java -client $JAVA_PARMS -classpath $BASE_DIR/commons-logging-1.1.jar:$BASE_DIR/log4j-1.2.13.jar:$BASE_DIR/mail.jar:$BASE_DIR/activation.jar:$BASE_DIR/ojdbc14.jar:$BASE_DIR/cacore32-client.jar:$BASE_DIR/hibernate3.jar:$BASE_DIR/spring.jar:$BASE_DIR/cglib-2.1.3.jar:$BASE_DIR/asm.jar:$BASE_DIR/cadsrsentinel.jar gov.nih.nci.cadsr.sentinel.tool.AutoProcessAlerts $BASE_DIR/log4j.xml true $BASE_DIR/cadsrsentinel.xml
