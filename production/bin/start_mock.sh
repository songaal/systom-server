#!/bin/sh

VERSION=1.2.12

pid=`ps -ef|grep java|grep coincloud-server|grep -v grep|awk '{print $2}'`
if [ ! -z "$pid" ] ; then
    echo "kill $pid"
    kill $pid || kill -9 $pid
    sleep 2
fi
sleep 1
# prevent killed by Hup, ctrl-c
APP_HOME=/home/ec2-user/systom

cd $APP_HOME

trap '' 1
JVM_OPT="-Xms4g -Xmx4g -Duser.language=ko -XX:+HeapDumpOnOutOfMemoryError -XX:+UseParallelOldGC -d64 -server"
SPRING_OPT="--spring.config.location=file:$APP_HOME/conf/application.yml --logging.pattern.file=$APP_HOME/conf/logback-spring.xml "

java $JVM_OPT -jar $APP_HOME/coincloud-server-$VERSION.jar $SPRING_OPT > $APP_HOME/output.log 2>&1 &
sleep 1
tail -f $APP_HOME/logs/system.log