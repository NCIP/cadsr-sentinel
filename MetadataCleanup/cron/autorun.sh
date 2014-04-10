#!/bin/bash
###################################### THIS IS NOT THE FILE USED ON THE SERVER. IT IS JUST A TEST SCRIPT! ######################################

echo "Executing Auto Run for Sentinel Tool"
echo "\$Header: /share/content/gforge/sentinel/sentinel/scripts/autorun.sh,v 1.10 2009-07-24 15:36:50 davet Exp $"
echo "\$Name: not supported by cvs2svn $"

DATE='date +%Y%m%d'
#JAVA_HOME=/usr/jdk1.6.0_33
#BASE_DIR=/local/content/cadsrsentinel/bin
BASE_DIR=/Users/ag/demo/sentinel.orig
LIB_DIR=$BASE_DIR/lib
BIN_DIR=/Users/ag/demo/sentinel.orig/deployment-artifacts/bin

export JAVA_BIN=java
#export JAVA_HOME BASE_DIR
export BASE_DIR

#ORACLE_HOME=/app/oracle/product/dbhome/9.2.0
PATH=$ORACLE_HOME/bin:$PATH
LD_LIBRARY_PATH=$ORACLE_HOME/lib:$LD_LIBRARY_PATH
TNS_ADMIN=$ORACLE_HOME/network/admin
JAVA_PARMS='-Xms512m -Xmx512m -XX:PermSize=64m'

#export JAVA_PARMS ORACLE_HOME TNS_ADMIN PATH LD_LIBRARY_PATH
export JAVA_PARMS

echo "Executing job as 'id'"
echo "Executing on 'date'"

mkdir -p $BASE_DIR/../reports
find $BASE_DIR/../reports -mtime +20 -exec rm {} \;

for x in $LIB_DIR/*.jar
do

CP=$CP:$x

done

export CP

#echo $JAVA_BIN $JAVA_PARMS -classpath $BIN_DIR:$LIB_DIR:$CP gov.nih.nci.cadsr.sentinel.daily.SimpleSQL $LIB_DIR/log4j.xml $LIB_DIR/SimpleSQL.xml
#$JAVA_BIN $JAVA_PARMS -classpath $BIN_DIR:$LIB_DIR:$CP gov.nih.nci.cadsr.sentinel.daily.SimpleSQL $LIB_DIR/log4j.xml $LIB_DIR/SimpleSQL.xml

#echo $JAVA_BIN $JAVA_PARMS -classpath $BIN_DIR:$LIB_DIR:$CP gov.nih.nci.cadsr.sentinel.daily.CleanStrings $LIB_DIR/log4j.xml $LIB_DIR/CleanStrings.xml
#$JAVA_BIN $JAVA_PARMS -classpath $BIN_DIR:$LIB_DIR:$CP gov.nih.nci.cadsr.sentinel.daily.CleanStrings $LIB_DIR/log4j.xml $LIB_DIR/CleanStrings.xml

#echo $JAVA_BIN $JAVA_PARMS -classpath $BIN_DIR:$LIB_DIR:$CP gov.nih.nci.cadsr.sentinel.daily.CleanOCR $LIB_DIR/log4j.xml $LIB_DIR/CleanOCR.xml
#$JAVA_BIN $JAVA_PARMS -classpath $BIN_DIR:$LIB_DIR:$CP gov.nih.nci.cadsr.sentinel.daily.CleanOCR $LIB_DIR/log4j.xml $LIB_DIR/CleanOCR.xml

#echo $JAVA_BIN $JAVA_PARMS -classpath $BASE_DIR/cron:$BIN_DIR:$LIB_DIR:$BIN_DIR/cadsrsentinel.jar:$CP gov.nih.nci.cadsr.sentinel.tool.AutoProcessAlerts $LIB_DIR/log4j.xml true $LIB_DIR/cadsrsentinel.xml
$JAVA_BIN $JAVA_PARMS -classpath $BASE_DIR/cron:$BIN_DIR:$LIB_DIR:$BIN_DIR/cadsrsentinel.jar:$CP gov.nih.nci.cadsr.sentinel.tool.AutoProcessAlerts $LIB_DIR/log4j.xml true $LIB_DIR/cadsrsentinel.xml
