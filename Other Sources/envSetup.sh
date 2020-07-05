#!/bin/bash

echo "EPRender Server Setup Script"


### Update system
echo "Updating the system: "

sudo apt update -y
sudo apt upgrade -y
sudo apt autoremove -y

### Installing dependencies
echo "Installing dependencies: "

sudo apt install openjdk-8-jre-headless	# Java 8 LTS JRE
sudo apt install apt-transport-https # apt transport https

## Erlang and RabbitMQ
curl -fsSL https://github.com/rabbitmq/signing-keys/releases/download/2.0/rabbitmq-release-signing-key.asc | sudo apt-key add -

sudo tee /etc/apt/sources.list.d/bintray.rabbitmq.list << EOF
deb https://dl.bintray.com/rabbitmq-erlang/debian focal erlang-23.x
deb https://dl.bintray.com/rabbitmq/debian bionic main
EOF

sudo apt update -y
sudo apt upgrade -y

sudo apt install -y erlang-base \
	erlang-asn1 erlang-crypto erlang-eldap erlang-ftp erlang-inets \
	erlang-mnesia erlang-os-mon erlang-parsetools erlang-public-key \
	erlang-runtime-tools erlang-snmp erlang-ssl \
	erlang-syntax-tools erlang-tftp erlang-tools erlang-xmerl

sudo apt-get install rabbitmq-server -y --fix-missing