#!/bin/bash

# This script will prepare the init mechanism and generate ssh keys and set up
# temporary directories for the worker.

# Create tmp directory
mkdir tmp
cd ~

# Create SSL certificate
mkdir cert
cd cert
openssl genrsa -des3 -passout pass:willberemoved -out pass.key 1024
openssl rsa -passin pass:willberemoved -in pass.key -out server.key
echo -e "US\n\n\n\n\nworker.stratosim.com\n\n" | openssl req -new -key server.key -x509 -out server.crt -days 999
rm pass.key
cd ~

# Add worker startup to crontab
echo "@reboot sh /home/ec2-user/worker/start_prod_worker" > worker.cron
sudo crontab worker.cron
rm worker.cron
cd ~

rm configure_worker.sh