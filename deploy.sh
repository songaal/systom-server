
echo "deploy version"

read version
rsync --progress ~/Projects/coincloud/systom-server/build/libs/systom-server-$version.jar ec2-user@13.125.73.226:/home/ec2-user/ -e "ssh -i ~/aws-pem/gncloud-io.pem"

echo "step 1. old package backup"
ssh ec2-user@13.125.73.226 -i ~/aws-pem/gncloud-io.pem "mv ~/systom/systom-* ~/systom/backup/"

echo "step 2. new package moving"
ssh ec2-user@13.125.73.226 -i ~/aws-pem/gncloud-io.pem "mv ~/systom-* ~/systom/"

echo "successful."
echo "version up and server restart Go"
