#!/bin/bash

PACKAGES_DIR="/opt/packages/"
HYBRIS_DIR="/opt/Hybris-6.2.0"
HYBIS_PACKAGES_URI="hybris-packages/"
#$1="http://54.210.0.102/"
#$2="jdk-8u131-linux-x64.tar.gz"
#$3="hybris-commerce-suite-6.2.0.4.zip"
#$4="b2c_acc"
REPO_SERVER="http://54.210.0.102/"
HYBRIS_PACKAGE="hybris-commerce-suite-6.2.0.4.zip"
ACCELERATOR_TYPE="b2c_acc"
HYBRIS_PACKAGE_URL="$REPO_SERVER$HYBIS_PACKAGES_URI$HYBRIS_PACKAGE"
HYBRIS_PACKAGE_PATH="$PACKAGES_DIR$HYBRIS_PACKAGE"
HYBRIS_PLATFORM_PATH="/opt/Hybris-6.2.0/hybris/bin/platform/"
HYBRIS_INSTALLER_RECIPE_SCRIPT_FOLDER="/opt/Hybris-6.2.0/installer/"
HYBRIS_INSTALLER_RECIPE_SCRIPT="install.sh"
HYBRIS_INSTALLER_RECIPE_SCRIPT_PATH="$HYBRIS_INSTALLER_RECIPE_SCRIPT_FOLDER$HYBRIS_INSTALLER_RECIPE_SCRIPT"
HYBRIS_SERVER_START_SCRIPT="hybrisserver.sh"
HYBRIS_SERVER_START_SCRIPT_PATH="$HYBRIS_PLATFORM_PATH$HYBRIS_SERVER_START_SCRIPT"
HOSTS_FILE_PATH="/etc/hosts"


# Download Hybris Package
#wget "http://54.210.0.102/hybris-packages/hybris-commerce-suite-6.2.0.4.zip" -P /opt/packages
wget $HYBRIS_PACKAGE_URL -P $PACKAGES_DIR
chmod -R 775 $PACKAGES_DIR
chown -R root:root $PACKAGES_DIR
mkdir -p $HYBRIS_DIR
apt-get install unzip
unzip $HYBRIS_PACKAGE_PATH -d $HYBRIS_DIR
chmod -R 775 $HYBRIS_DIR
chown -R root:root $HYBRIS_DIR
echo "###################################### Download Hybris completed! ######################################"

# Install and Setup Hybris Reciepe
cd $HYBRIS_INSTALLER_RECIPE_SCRIPT_FOLDER
$HYBRIS_INSTALLER_RECIPE_SCRIPT_PATH -r $ACCELERATOR_TYPE
echo "###################################### Installation of Reciepe ${ACCELERATOR_TYPE} completed! ######################################"

# Build Hybris eCommerce
cd $HYBRIS_PLATFORM_PATH
. ./setantenv.sh
ant clean all
#echo "127.0.0.1 " `hostname` >>/etc/hosts
echo "###################################### Hybris Build completed! ######################################"
ant initialize
echo "###################################### Hybris Initialize completed! ######################################"
$HYBRIS_SERVER_START_SCRIPT_PATH