#!/bin/bash


HOME_DIR=$(cd `dirname $0; pwd`)
HOME_DIR=${HOME_DIR%/bin}

PID_PATH=$HOME_DIR/bin/pid

if [ -f $PID_PATH ]
then
  pid=`cat $PID_PATH`
  kill -QUIT $pid
  rm $PID_PATH
  echo "Kill -QUIT pid: $pid"
else
  echo "pid file not exist, path: $PID_PATH"
fi