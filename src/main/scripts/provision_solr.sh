#!/bin/bash

STRIP=${1%.*}
SOLR_PACKAGE=$1
PACKAGES_DIR="/opt/packages/"
TEMPLATES_DIR="/opt/templates/"
SOLR_PACKAGE_URL="http://54.210.0.102/solr-packages/$SOLR_PACKAGE"
SOLR_PACKAGE_PATH="$PACKAGES_DIR$SOLR_PACKAGE"
SOLR_FOLDER=${STRIP%.tgz}
SOLR_CURRENT_DIR="/opt/solr/"
SOLR_SYMLINK="/opt/solr"
SOLR_DESIRED_DIR="/opt/$SOLR_FOLDER/"
#SOLR_SERVICE_SCRIPT_PATH="/opt/$SOLR_FOLDER/bin/install_solr_service.sh"
SOLR_SERVICE_URL="http://54.210.0.102/templates/solr.service"
SOLR_SERVICE_CURRENT_PATH="/opt/templates/solr.service"
SOLR_SERVICE_DESIRED_PATH="/lib/systemd/system/solr.service"
SOLR_INIT_SYMLINK="/etc/init.d/solr"
SOLR_INIT_SCRIPT="${SOLR_DESIRED_DIR}bin/solr"
WorkingDirectory="\/opt\/$SOLR_FOLDER\/"
ExecStart="\/opt\/$SOLR_FOLDER\/bin\/solr start"
ExecStop="\/opt\/$SOLR_FOLDER\/bin\/solr stop"

SUDOERS="/etc/sudoers"
USERNAME="solr"
USERGROUP="solr"
PASSWORD="solr"

export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
export PATH=$(dirname $(dirname $(readlink -f $(which java))))/bin:$PATH

## Adding solr user and usergroup
apt-get install whois
addgroup $USERGROUP; useradd -p `mkpasswd ${PASSWORD}` -d /home/$USERNAME -m -g $USERGROUP -s /bin/bash $USERGROUP
echo "${USERNAME}   ALL=(ALL) NOPASSWD:ALL" >> $SUDOERS; echo "%${USERGROUP}   ALL=(ALL) NOPASSWD:ALL" >> $SUDOERS
echo "###################################### User ${USERNAME} and Usergroup ${USERGROUP} is added! ######################################"

## Download solr Package
wget $SOLR_PACKAGE_URL -P $PACKAGES_DIR
chmod -R 775 $PACKAGES_DIR; chown -R root:root $PACKAGES_DIR
cd $PACKAGES_DIR; tar -xvzf $SOLR_PACKAGE_PATH -C /opt/
mv -T $SOLR_CURRENT_DIR $SOLR_DESIRED_DIR
chmod -R 775 $SOLR_DESIRED_DIR; chown -R $USERNAME:$USERGROUP $SOLR_DESIRED_DIR
ln -s $SOLR_DESIRED_DIR $SOLR_SYMLINK; chown -R $USERNAME:$USERGROUP $SOLR_SYMLINK

## Creating solr init script
ln -s $SOLR_INIT_SCRIPT $SOLR_INIT_SYMLINK

## Download solr.service
mkdir -p $TEMPLATES_DIR; wget $SOLR_SERVICE_URL -P $TEMPLATES_DIR
sed -i -e "s/working_directory/$WorkingDirectory/g" -e "s/exec_start/$ExecStart/g" -e "s/exec_stop/$ExecStop/g" $SOLR_SERVICE_CURRENT_PATH
mv $SOLR_SERVICE_CURRENT_PATH $SOLR_SERVICE_DESIRED_PATH
systemctl daemon-reload; systemctl enable $SOLR_SERVICE_DESIRED_PATH
rm -r $PACKAGES_DIR