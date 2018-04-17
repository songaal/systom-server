
echo "deploy version"

read version
rsync --progress ~/Projects/coincloud/coincloud-server/build/libs/coincloud-server-$version.jar ec2-user@api.gncloud.io:/home/ec2-user/ -e "ssh -i ~/aws-pem/gncloud-io.pem"
