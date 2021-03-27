#!/bin/bash

ENV_PATH=$(dirname $0)/../env/env.properties
if [ -f $ENV_PATH ]; then
  source $ENV_PATH
fi

EXEC_JAR=""
HOME_DIR=$(cd `dirname $0`; pwd)
HOME_DIR=${HOME_DIR%/bin}
JVM_PARAM=$JVM_PARAM
JAVA_PARAM=$JAVA_PARAM

LOG_DIR=$HOME_DIR/logs
if [ ! -d "$LOG_DIR" ]; then
  mkdir -p $LOG_DIR
fi

jars=`ls $HOME_DIR | grep "*.jar"`
if [ ${#jars} -gt 1 ]; then
  echo "Not only one jar."
else
  EXEC_JAR=`ls $HOME_DIR/lib/ | grep ".jar"`
fi
EXEC_JAR_PATH=$HOME_DIR/lib/$EXEC_JAR
EXEC_JAR_NAME=${EXEC_JAR%\.jar}

echo "nohup java $JVM_PARAM -jar $EXEC_JAR_PATH $JAVA_PARAM > $LOG_DIR/$EXEC_JAR_NAME-console.log 2>&1 &"

nohup java $JVM_PARAM -jar $EXEC_JAR_PATH $JAVA_PARAM > $LOG_DIR/$EXEC_JAR_NAME"-console.log" 2>&1 &
pid=$!
sleep 5s

if [ -d /proc/$pid ]; then
  echo $pid > $HOME_DIR/bin/pid
  echo "launcher $EXEC_JAR_PATH success, pid is $pid"
else
  echo "launcher $EXEC_JAR_PATH failure."
fi