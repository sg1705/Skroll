#!/bin/bash
sudo add-apt-repository ppa:openjdk-r/ppa
sudo apt-get update
sudo apt-get install openjdk-8-jdk
sudo apt-get install nodejs
sudo apt-get install npm
sudo ln -s /usr/bin/nodejs /usr/bin/node
sudo apt-get install nginx
sudo service nginx restart
curl -sSO https://dl.google.com/cloudagents/install-logging-agent.sh
sha256sum install-logging-agent.sh
install -> sudo bash install-logging-agent.sh
#sudo vi /etc/google-fluentd/config.d/skroll.conf
#<source>  type tail  # Parse the timestamp, but still collect the entire line as 'message'  format none  path /home/skrollioteamsep2015/logs/skroll.log  pos_file /var/lib/google-fluentd/pos/skroll.pos  read_from_head true  tag skroll</source>
sudo service google-fluentd restart