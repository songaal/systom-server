#!/bin/bash

group_name='/ecs/coincloud/tradebot'
stream_name="$1"
echo "group_name: ", $group_name
echo "stream_name: ", $stream_name
start_seconds_ago=300

start_time=$(( ( $(date -u +"%s") - $start_seconds_ago ) * 1000 ))
while [[ -n "$start_time" ]]; do
  loglines=$( aws --output text logs get-log-events --log-group-name "$group_name" --log-stream-name "$stream_name" --startDate-time $start_time )
  [ $? -ne 0 ] && break
  next_start_time=$( sed -nE 's/^EVENTS.([[:digit:]]+).+$/\1/ p' <<< "$loglines" | tail -n1 )
  [ -n "$next_start_time" ] && start_time=$(( $next_start_time + 1 ))
  echo "$loglines"
  sleep 2
done
