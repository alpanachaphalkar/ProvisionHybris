#!/bin/bash

STRIP=${1%.*}
SOLR_PACKAGE=$1
PACKAGES_DIR="/opt/packages/"
SOLR_PACKAGE_URL="http://54.210.0.102/solr-packages/$SOLR_PACKAGE"
SOLR_PACKAGE_PATH="$PACKAGES_DIR$SOLR_PACKAGE"
SOLR_FOLDER=${STRIP%.tgz}
SOLR_CURRENT_DIR="/opt/solr/"
SOLR_SYMLINK="/opt/solr"
SOLR_DESIRED_DIR="/opt/$SOLR_FOLDER/"
SOLR_SERVICE_SCRIPT_PATH="/opt/$SOLR_FOLDER/bin/install_solr_service.sh"

SUDOERS="/etc/sudoers"
USERNAME="solr"
USERGROUP="solr"

export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
export PATH=$(dirname $(dirname $(readlink -f $(which java))))/bin:$PATH

## Download solr Package
wget $SOLR_PACKAGE_URL -P $PACKAGES_DIR
chmod -R 775 $PACKAGES_DIR; chown -R root:root $PACKAGES_DIR
cd $PACKAGES_DIR; tar -xvzf $SOLR_PACKAGE_PATH -C /opt/
mv -T $SOLR_CURRENT_DIR $SOLR_DESIRED_DIR
chmod -R 775 $SOLR_DESIRED_DIR; $SOLR_SERVICE_SCRIPT_PATH $SOLR_PACKAGE_PATH
echo "${USERNAME}   ALL=(ALL) NOPASSWD:ALL" >> $SUDOERS; echo "%${USERGROUP}   ALL=(ALL) NOPASSWD:ALL" >> $SUDOERS
chmod -R 775 $SOLR_DESIRED_DIR; chown -R $USERNAME:$USERGROUP $SOLR_DESIRED_DIR
chown -R $USERNAME:$USERGROUP $SOLR_SYMLINK; rm -r $PACKAGES_DIR
