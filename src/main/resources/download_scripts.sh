#!/bin/bash

REPO_SERVER="54.210.0.102"
SCRIPTS_DIR="/opt/scripts/"
INSTALL_JAVA_SCRIPT_URL="http://$REPO_SERVER/scripts/install_java.sh"
INSTALL_HYBRIS_SCRIPT_URL="http://$REPO_SERVER/scripts/install_hybris.sh"
HYBRIS_SCRIPT_URL="http://$REPO_SERVER/scripts/hybris.sh"

HOSTS_FILE_PATH="/etc/hosts"


wget $INSTALL_JAVA_SCRIPT_URL -P $SCRIPTS_DIR
wget $INSTALL_HYBRIS_SCRIPT_URL -P $SCRIPTS_DIR
wget $HYBRIS_SCRIPT_URL -P $SCRIPTS_DIR
echo "127.0.0.1 `hostname`" >>$HOSTS_FILE_PATH
chmod -R 775 $SCRIPTS_DIR
chown -R root:root $SCRIPTS_DIR