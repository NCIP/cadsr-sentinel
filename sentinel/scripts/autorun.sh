#!/bin/bash

echo "Executing Auto Run for Sentinel Tool"
echo "\$Header: /share/content/gforge/sentinel/sentinel/scripts/autorun.sh,v 1.6 2008-04-23 18:17:10 hebell Exp $"
echo "\$Name: not supported by cvs2svn $"

DATE=`date +%Y%m%d`
JAVA_HOME=/usr/jdk1.5.0_10
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

CP=$BASE_DIR/acegi-security-1.0.4.jar
CP=$CP:$BASE_DIR/activation.jar
CP=$CP:$BASE_DIR/asm.jar
CP=$CP:$BASE_DIR/cadsrsentinel.jar
CP=$CP:$BASE_DIR/castor-1.0.2.jar
CP=$CP:$BASE_DIR/cglib-2.1.3.jar
CP=$CP:$BASE_DIR/commons-collections-3.2.jar
CP=$CP:$BASE_DIR/commons-logging-1.1.jar
CP=$CP:$BASE_DIR/commons-pool-1.3.jar
CP=$CP:$BASE_DIR/dlbadapter.jar
CP=$CP:$BASE_DIR/evsapi41-beans.jar
CP=$CP:$BASE_DIR/evsapi41-framework.jar
CP=$CP:$BASE_DIR/hibernate3.jar
CP=$CP:$BASE_DIR/jdom-1.0.jar
CP=$CP:$BASE_DIR/lbAdmin.jar
CP=$CP:$BASE_DIR/lbImpl.jar
CP=$CP:$BASE_DIR/lbInterfaces.jar
CP=$CP:$BASE_DIR/lbModel.jar
CP=$CP:$BASE_DIR/lgConverter.jar
CP=$CP:$BASE_DIR/lgIndexer.jar
CP=$CP:$BASE_DIR/lgModel.emf.jar
CP=$CP:$BASE_DIR/lgModel.jar
CP=$CP:$BASE_DIR/lgRDFConverter.jar
CP=$CP:$BASE_DIR/lgResourceReader.jar
CP=$CP:$BASE_DIR/lgUtility.jar
CP=$CP:$BASE_DIR/log4j-1.2.14.jar
CP=$CP:$BASE_DIR/lucene-core-2.0.0.jar
CP=$CP:$BASE_DIR/lucene-regex-10-9-06-nightly.jar
CP=$CP:$BASE_DIR/lucene-snowball-9-26-06-nightly.jar
CP=$CP:$BASE_DIR/mail.jar
CP=$CP:$BASE_DIR/ojdbc14.jar
CP=$CP:$BASE_DIR/sdk-client-framework.jar
CP=$CP:$BASE_DIR/spring.jar
CP=$CP:$BASE_DIR/stax-api-1.0.1.jar
CP=$CP:$BASE_DIR/xercesImpl.jar

export CP

echo $JAVA_HOME/bin/java -client $JAVA_PARMS -classpath $BASE_DIR:$CP gov.nih.nci.cadsr.sentinel.tool.AutoProcessAlerts $BASE_DIR/log4j.xml true $BASE_DIR/cadsrsentinel.xml

$JAVA_HOME/bin/java -client $JAVA_PARMS -classpath $BASE_DIR:$CP gov.nih.nci.cadsr.sentinel.tool.AutoProcessAlerts $BASE_DIR/log4j.xml true $BASE_DIR/cadsrsentinel.xml
