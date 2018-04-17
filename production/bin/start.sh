#!/bin/sh

VERSION=1.0.0

pid=`ps -ef|grep java|grep coincloud-server|grep -v grep|awk '{print $2}'`
if [ ! -z "$pid" ] ; then
    echo "kill $pid"
    kill $pid || kill -9 $pid
    sleep 2
fi
# prevent killed by Hup, ctrl-c

export APP_HOME=/home/ec2-user/coincloud

cd $APP_HOME

trap '' 1
JVM_OPT="-Xms4g -Xmx4g -Duser.language=ko -XX:+HeapDumpOnOutOfMemoryError -XX:+UseParallelOldGC -d64 -server"
SPRING_OPT="--spring.config.location=file:$APP_HOME/conf/application.yaml --logging.pattern.file=$APP_HOME/conf/logback-spring.xml "

$APP_HOME/jdk/bin/java $JVM_OPT -jar $APP_HOME/coincloud-server-$VERSION.jar $SPRING_OPT > $MGB_HOME/output.log 2>&1 &
sleep 1
tail -f logs/system.log