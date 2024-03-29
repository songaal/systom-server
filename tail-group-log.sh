#!/bin/bash

group_name='/ecs/coincloud/tradebot'
start_seconds_ago=300

start_time=$(( ( $(date -u +"%s") - $start_seconds_ago ) * 1000 ))
while [[ -n "$start_time" ]]; do
  loglines=$( aws --output text logs filter-log-events --log-group-name "$group_name" --interleaved --startDate-time $start_time )
  [ $? -ne 0 ] && break
  next_start_time=$( sed -nE 's/^EVENTS.([^[:blank:]]+).([[:digit:]]+).+$/\2/ p' <<< "$loglines" | tail -n1 )
  [ -n "$next_start_time" ] && start_time=$(( $next_start_time + 1 ))
  echo "$loglines"
  sleep 2
done
