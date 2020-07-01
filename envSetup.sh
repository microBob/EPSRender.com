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


