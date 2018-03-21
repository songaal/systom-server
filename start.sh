trap '' 1
java -jar ~/coincloud-server-1.0.0.jar -Dspring.config.location=file:./application.yml >> ~/output.log 2>&1 &
sleep 1
tail -f logs/system.log