
echo "deploy version"

read version
rsync --progress ~/Projects/coincloud/systom-server/build/libs/systom-server-$version.jar ec2-user@api.gncloud.io:/home/ec2-user/ -e "ssh -i ~/aws-pem/gncloud-io.pem"
