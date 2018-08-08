#!/bin/sh
pid=`ps -ef|grep java|grep systom-server|grep -v grep|awk '{print $2}'`
if [ ! -z "$pid" ] ; then
    echo "kill $pid"
    kill $pid || kill -9 $pid
    sleep 2
fi