#!/bin/bash

REPO_SERVER="54.210.0.102"
SCRIPTS_DIR="/opt/scripts/"
INSTALL_JAVA_SCRIPT_URL="http://$REPO_SERVER/scripts/install_java.sh"
INSTALL_HYBRIS_SCRIPT_URL="http://$REPO_SERVER/scripts/install_hybris.sh"
HYBRIS_SCRIPT_URL="http://$REPO_SERVER/scripts/hybris.sh"
HYBRIS_SCRIPT_CURRENT_PATH="/opt/scripts/hybris.sh"
HYBRIS_SCRIPT_DESIRED_PATH="/etc/init.d/hybris"

SUDOERS="/etc/sudoers"
USERNAME="hybris"
PASSWORD="hybris"
USERGROUP="hybris"

## Adding hybris user and usergroup
apt-get install whois
addgroup $USERGROUP
useradd -p `mkpasswd ${PASSWORD}` -d /home/$USERGROUP -m -g $USERGROUP -s /bin/bash $USERGROUP
usermod -aG sudo $USERNAME
echo "${USERNAME}   ALL=(ALL:ALL) ALL" >> $SUDOERS
echo "%${USERGROUP}   ALL=(ALL:ALL) ALL" >> $SUDOERS
echo "###################################### User ${USERNAME} and Usergroup ${USERGROUP} is added! ######################################"

wget $INSTALL_JAVA_SCRIPT_URL -P $SCRIPTS_DIR
wget $INSTALL_HYBRIS_SCRIPT_URL -P $SCRIPTS_DIR
wget $HYBRIS_SCRIPT_URL -P $SCRIPTS_DIR
chmod -R 775 $SCRIPTS_DIR
chown -R root:root $SCRIPTS_DIR
mv $HYBRIS_SCRIPT_CURRENT_PATH $HYBRIS_SCRIPT_DESIRED_PATH
chmod +x $HYBRIS_SCRIPT_DESIRED_PATH
chown -R root:root $HYBRIS_SCRIPT_DESIRED_PATH