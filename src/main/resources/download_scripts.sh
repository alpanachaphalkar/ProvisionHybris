#!/bin/bash

SCRIPTS_DIR="/opt/scripts"
JAVA_SCRIPT_URL="http://54.210.0.102/scripts/install_java.sh"
HYBRIS_SCRIPT_URL="http://54.210.0.102/scripts/install_hybris.sh"

wget $JAVA_SCRIPT_URL -P $SCRIPTS_DIR
wget $HYBRIS_SCRIPT_URL -P $SCRIPTS_DIR
chmod -R 775 $SCRIPTS_DIR
chown -R root:root $SCRIPTS_DIR