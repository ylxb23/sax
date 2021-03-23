#!/usr/bin/bash


HOME_DIR=$(cd `dirname $0; pwd`)
HOME_DIR=${HOME_DIR%/bin}

pid=`cat $HOME_DIR/bin/pid`
kill -QUIT $pid
rm $HOME_DIR/bin/pid

echo "Kill pid: $pid"