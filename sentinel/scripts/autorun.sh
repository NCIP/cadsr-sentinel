#!/bin/bash

JAVA_HOME=/usr/j2sdk1.4.2_06
BASE_DIR=/local/content/jboss4/Sentinel

export JAVA_HOME BASE_DIR

ORACLE_HOME=/app/oracle/product/dbhome/9.2.0
PATH=$ORACLE_HOME/bin:$PATH
LD_LIBRARY_PATH=$ORACLE_HOME/lib:$LD_LIBRARY_PATH
TNS_ADMIN=$ORACLE_HOME/network/admin

export ORACLE_HOME TNS_ADMIN PATH LD_LIBRARY_PATH


$JAVA_HOME/bin/java -client -classpath $BASE_DIR/mail.jar:$BASE_DIR/activation.jar:$BASE_DIR/ojdbc14.jar:$BASE_DIR/DSRAlert.jar com.scenpro.DSRAlert.AutoProcessAlerts

