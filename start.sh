#!/bin/sh
pid=`ps -ef|grep coincloud-server|grep -v grep|awk '{print $2}'`
if [ ! -z "$pid" ] ; then
    echo "kill $pid"
    kill $pid || kill -9 $pid
    sleep 2
fi
# prevent killed by Hup, ctrl-c

export COIN_HOME=/home/ec2-user


trap '' 1
JVM_OPT="-Xms4g -Xmx4g -Duser.language=en -XX:+HeapDumpOnOutOfMemoryError -XX:+UseParallelOldGC -d64 -server"
SPRING_OPT="--spring.config.location=$COIN_HOME/conf/application.yml"

/usr/bin/java $JVM_OPT -jar $COIN_HOME/coincloud-server-1.0.0.jar $SPRING_OPT > $COIN_HOME/output.log 2>&1 &
sleep 1

tail -f logs/system.log