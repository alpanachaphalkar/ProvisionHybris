#!/bin/bash

HYBRIS_VERSION=$1
HYBRIS_PACKAGE=$2
ACCELERATOR_TYPE=$3
PACKAGES_DIR="/opt/packages/"
SCRIPTS_DIR="/opt/scripts/"
#HYBRIS_PACKAGE="hybris-commerce-suite-6.3.0.5.zip"
#ACCELERATOR_TYPE="b2c_acc"
#HYBRIS_DIR="/opt/Hybris-6.3.0"
HYBRIS_DIR="/opt/$HYBRIS_VERSION"
HYBRIS_HOME="$HYBRIS_DIR/hybris"
HYBIS_PACKAGES_URI="hybris-packages/"
REPO_SERVER="http://54.210.0.102/"
HYBRIS_PACKAGE_URL="$REPO_SERVER$HYBIS_PACKAGES_URI$HYBRIS_PACKAGE"
HYBRIS_PACKAGE_PATH="$PACKAGES_DIR$HYBRIS_PACKAGE"
HYBRIS_PLATFORM_PATH="$HYBRIS_HOME/bin/platform/"
HYBRIS_INSTALLER_RECIPE_SCRIPT_FOLDER="$HYBRIS_DIR/installer/"
HYBRIS_INSTALLER_RECIPE_SCRIPT="install.sh"
HYBRIS_INSTALLER_RECIPE_SCRIPT_PATH="$HYBRIS_INSTALLER_RECIPE_SCRIPT_FOLDER$HYBRIS_INSTALLER_RECIPE_SCRIPT"
HYBRIS_SERVER_START_SCRIPT="hybrisserver.sh"
HYBRIS_SERVER_START_SCRIPT_PATH="$HYBRIS_PLATFORM_PATH$HYBRIS_SERVER_START_SCRIPT"

BASH_PROFILE_HYBRIS_HOME_SCRIPT="/etc/profile.d/hybris.sh"
ENVIRONMENT_FILE_PATH="/etc/environment"
BASHRC_FILE_PATH="/etc/bash.bashrc"
BASH_PROFILE_FILE_PATH="/etc/profile"
HYBRIS_SERVICE_FILE_PATH="/lib/systemd/system/hybris.service"

USERNAME="hybris"
USERGROUP="hybris"

# Setting HYBRIS_HOME environment variable
echo "HYBRIS_HOME=${HYBRIS_HOME}" >>$BASH_PROFILE_HYBRIS_HOME_SCRIPT
echo "export HYBRIS_HOME" >>$BASH_PROFILE_HYBRIS_HOME_SCRIPT
chmod -R 775 $BASH_PROFILE_HYBRIS_HOME_SCRIPT
chown -R root:root $BASH_PROFILE_HYBRIS_HOME_SCRIPT
echo "export HYBRIS_HOME=${HYBRIS_HOME}" >>$ENVIRONMENT_FILE_PATH
echo "export HYBRIS_HOME=${HYBRIS_HOME}" >>$BASHRC_FILE_PATH
echo "export HYBRIS_HOME=${HYBRIS_HOME}" >>$BASH_PROFILE_FILE_PATH
source $BASH_PROFILE_HYBRIS_HOME_SCRIPT

# Create hybris.service
echo "[Unit]" > $HYBRIS_SERVICE_FILE_PATH
echo "Description=hybris" >> $HYBRIS_SERVICE_FILE_PATH
echo "After=network.target" >> $HYBRIS_SERVICE_FILE_PATH
echo "" >> $HYBRIS_SERVICE_FILE_PATH
echo "[Service]" >> $HYBRIS_SERVICE_FILE_PATH
echo "WorkingDirectory=${HYBRIS_HOME}/bin/platform/" >> $HYBRIS_SERVICE_FILE_PATH
echo "Type=oneshot" >> $HYBRIS_SERVICE_FILE_PATH
echo "ExecStart=${HYBRIS_HOME}/bin/platform/hybrisserver.sh start" >> $HYBRIS_SERVICE_FILE_PATH
echo "ExecStop=${HYBRIS_HOME}/bin/platform/hybrisserver.sh stop" >> $HYBRIS_SERVICE_FILE_PATH
echo "RemainAfterExit=yes" >> $HYBRIS_SERVICE_FILE_PATH
echo "User=$USERNAME" >> $HYBRIS_SERVICE_FILE_PATH
echo "Group=$USERGROUP" >> $HYBRIS_SERVICE_FILE_PATH
echo "" >> $HYBRIS_SERVICE_FILE_PATH
echo "[Install]" >> $HYBRIS_SERVICE_FILE_PATH
echo "WantedBy=multi-user.target" >> $HYBRIS_SERVICE_FILE_PATH
systemctl daemon-reload
systemctl enable $HYBRIS_SERVICE_FILE_PATH

# Download Hybris Package
#wget "http://54.210.0.102/hybris-packages/hybris-commerce-suite-6.2.0.4.zip" -P /opt/packages
wget $HYBRIS_PACKAGE_URL -P $PACKAGES_DIR
chmod -R 775 $PACKAGES_DIR
chown -R root:root $PACKAGES_DIR
mkdir -p $HYBRIS_DIR
apt-get install unzip
unzip $HYBRIS_PACKAGE_PATH -d $HYBRIS_DIR

rm -r $PACKAGES_DIR
chmod -R 775 $HYBRIS_DIR
chown -R $USERNAME:$USERGROUP $HYBRIS_DIR
sudo su $USERNAME
echo "###################################### Download Hybris completed! ######################################"

# Install and Setup Hybris Reciepe
cd $HYBRIS_INSTALLER_RECIPE_SCRIPT_FOLDER
sudo $HYBRIS_INSTALLER_RECIPE_SCRIPT_PATH -r $ACCELERATOR_TYPE
chmod -R 775 $HYBRIS_DIR
chown -R $USERNAME:$USERGROUP $HYBRIS_DIR
sudo su $USERNAME
echo "###################################### Installation of Reciepe ${ACCELERATOR_TYPE} completed! ######################################"

# Build Hybris eCommerce
cd $HYBRIS_PLATFORM_PATH
. ./setantenv.sh
ant clean all

echo "###################################### Hybris Build completed! ######################################"
ant initialize
echo "###################################### Hybris Initialize completed! ######################################"

#$HYBRIS_SERVER_START_SCRIPT_PATH