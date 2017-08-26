#!/bin/bash

HYBRIS_VERSION=$1
HYBRIS_PACKAGE=$2
ACCELERATOR_TYPE=$3
CLUSTER_ID=$4
DB_HOST_NAME=$5
DB_HOST_IP=$6
PACKAGES_DIR="/opt/packages/"
SCRIPTS_DIR="/opt/scripts/"
TEMPLATES_DIR="/opt/templates/"
HYBRIS_PACKAGE_PATH="$PACKAGES_DIR$HYBRIS_PACKAGE"
HYBRIS_PACKAGE_URL="http://54.210.0.102/hybris-packages/$HYBRIS_PACKAGE"
SERVER_XML_URL="http://54.210.0.102/templates/server.xml"
LOCAL_PROPERTIES_URL="http://54.210.0.102/templates/local.properties"
HYBRIS_SCRIPT_URL="http://54.210.0.102/scripts/hybris.sh"
HYBRIS_SERVICE_URL="http://54.210.0.102/templates/hybris.service"
DB_DRIVER_URL="http://54.210.0.102/database-drivers/mysql-connector-java-5.1.33-bin.jar"
HYBRIS_DIR="/opt/$HYBRIS_VERSION"
HYBRIS_HOME_ABSOLUTE="$HYBRIS_DIR/hybris"
HYBRIS_SYMLINK="/opt/hybris"
HYBRIS_HOME="$HYBRIS_SYMLINK/hybris"
HYBRIS_PLATFORM_PATH="$HYBRIS_HOME/bin/platform/"
HYBRIS_DB_DRIVER_DIR="$HYBRIS_HOME/bin/platform/lib/dbdriver/"
HYBRIS_INSTALLER_RECIPE_SCRIPT_FOLDER="$HYBRIS_SYMLINK/installer/"
HYBRIS_INSTALLER_RECIPE_SCRIPT="install.sh"
HYBRIS_INSTALLER_RECIPE_SCRIPT_PATH="$HYBRIS_INSTALLER_RECIPE_SCRIPT_FOLDER$HYBRIS_INSTALLER_RECIPE_SCRIPT"
DB_URL="jdbc:mysql:\/\/${DB_HOST_NAME}:3306\/hybris\?useConfigs=maxPerformance\&characterEncoding=utf8"
WorkingDirectory="\/opt\/$HYBRIS_VERSION\/hybris\/bin\/platform"
ExecStart="\/opt\/$HYBRIS_VERSION\/hybris\/bin\/platform\/hybrisserver.sh start"
ExecStop="\/opt\/$HYBRIS_VERSION\/hybris\/bin\/platform\/hybrisserver.sh stop"

LOCAL_PROPERTIES_CURRENT_PATH="/opt/templates/local.properties"
LOCAL_PROPERTIES_DESIRED_PATH="$HYBRIS_HOME/config/local.properties"
HYBRIS_SERVICE_DESIRED_PATH="/lib/systemd/system/hybris.service"
HYBRIS_SERVICE_CURRENT_PATH="/opt/templates/hybris.service"
SERVER_XML_CURRENT_PATH="/opt/templates/server.xml"
SERVER_XML_DESIRED_PATH="$HYBRIS_HOME/config/tomcat/conf/server.xml"
HYBRIS_SCRIPT_CURRENT_PATH="/opt/scripts/hybris.sh"
HYBRIS_SCRIPT_DESIRED_PATH="/etc/init.d/hybris"

HOSTS_FILE_PATH="/etc/hosts"
ENVIRONMENT_FILE_PATH="/etc/environment"
BASH_PROFILE_FILE_PATH="/etc/profile"
BASH_PROFILE_HYBRIS_HOME_SCRIPT="/etc/profile.d/hybris.sh"

SUDOERS="/etc/sudoers"
USERNAME="hybris"
PASSWORD="hybris"
USERGROUP="hybris"

## Setting HYBRIS_HOME environment variable
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
export PATH=$(dirname $(dirname $(readlink -f $(which java))))/bin:$PATH
export HYBRIS_HOME=$HYBRIS_HOME_ABSOLUTE
echo "Defaults   env_keep += \"HYBRIS_HOME\"" >>$SUDOERS
echo "export HYBRIS_HOME=${HYBRIS_HOME_ABSOLUTE}" >> $ENVIRONMENT_FILE_PATH
echo "export HYBRIS_HOME=${HYBRIS_HOME_ABSOLUTE}" >> $BASH_PROFILE_FILE_PATH
echo "export HYBRIS_HOME=${HYBRIS_HOME_ABSOLUTE}" >> $BASH_PROFILE_HYBRIS_HOME_SCRIPT
source $ENVIRONMENT_FILE_PATH

## Move hybris shell script to desired location
wget $HYBRIS_SCRIPT_URL -P $SCRIPTS_DIR
mv $HYBRIS_SCRIPT_CURRENT_PATH $HYBRIS_SCRIPT_DESIRED_PATH
chmod +x $HYBRIS_SCRIPT_DESIRED_PATH
chown -R root:root $HYBRIS_SCRIPT_DESIRED_PATH

## Adding hybris user and usergroup
apt-get install whois
addgroup $USERGROUP; useradd -p `mkpasswd ${PASSWORD}` -d /home/$USERNAME -m -g $USERGROUP -s /bin/bash $USERGROUP
echo "${USERNAME}   ALL=(ALL) NOPASSWD:ALL" >> $SUDOERS; echo "%${USERGROUP}   ALL=(ALL) NOPASSWD:ALL" >> $SUDOERS
echo "###################################### User ${USERNAME} and Usergroup ${USERGROUP} is added! ######################################"

# Download hybris.service
mkdir -p $TEMPLATES_DIR; wget $HYBRIS_SERVICE_URL -P $TEMPLATES_DIR
sed -i -e "s/working_directory/$WorkingDirectory/g" -e "s/exec_start/$ExecStart/g" -e "s/exec_stop/$ExecStop/g" $HYBRIS_SERVICE_CURRENT_PATH
mv $HYBRIS_SERVICE_CURRENT_PATH $HYBRIS_SERVICE_DESIRED_PATH
systemctl daemon-reload; systemctl enable $HYBRIS_SERVICE_DESIRED_PATH

# Download Hybris Package
wget $HYBRIS_PACKAGE_URL -P $PACKAGES_DIR
chmod -R 775 $PACKAGES_DIR; chown -R root:root $PACKAGES_DIR
mkdir -p $HYBRIS_DIR; apt-get install unzip
unzip $HYBRIS_PACKAGE_PATH -d $HYBRIS_DIR; ln -s $HYBRIS_DIR $HYBRIS_SYMLINK
chown -R $USERNAME:$USERGROUP $HYBRIS_SYMLINK
rm -r $PACKAGES_DIR
chmod -R 775 $HYBRIS_DIR; chown -R $USERNAME:$USERGROUP $HYBRIS_DIR
sudo su $USERNAME
echo "###################################### Download Hybris completed! ######################################"

# Install and Setup Hybris Reciepe
cd $HYBRIS_INSTALLER_RECIPE_SCRIPT_FOLDER
sudo $HYBRIS_INSTALLER_RECIPE_SCRIPT_PATH -r $ACCELERATOR_TYPE; sudo su root
echo "###################################### Installation of Reciepe ${ACCELERATOR_TYPE} completed! ######################################"

# Changing configuration
echo "${DB_HOST_IP} ${DB_HOST_NAME}" >>$HOSTS_FILE_PATH
wget $SERVER_XML_URL -P $TEMPLATES_DIR; mv $SERVER_XML_CURRENT_PATH $SERVER_XML_DESIRED_PATH
chmod -R 775 $SERVER_XML_DESIRED_PATH; chown -R $USERNAME:$USERGROUP $SERVER_XML_DESIRED_PATH
wget $DB_DRIVER_URL -P $HYBRIS_DB_DRIVER_DIR
wget $LOCAL_PROPERTIES_URL -P $TEMPLATES_DIR; mv $LOCAL_PROPERTIES_CURRENT_PATH $LOCAL_PROPERTIES_DESIRED_PATH
sed -i "s/^\(yms\.hostname\s*=\s*\).*$/\1${HOSTNAME}/" $LOCAL_PROPERTIES_DESIRED_PATH
sed -i "s/^\(cluster\.id\s*=\s*\).*$/\1${CLUSTER_ID}/" $LOCAL_PROPERTIES_DESIRED_PATH
sed -i "s/^\(db\.url\s*=\s*\).*$/\1${DB_URL}/" $LOCAL_PROPERTIES_DESIRED_PATH
chmod -R 775 $LOCAL_PROPERTIES_DESIRED_PATH; chown -R $USERNAME:$USERGROUP $LOCAL_PROPERTIES_DESIRED_PATH
chmod -R 775 $HYBRIS_DIR; chown -R $USERNAME:$USERGROUP $HYBRIS_DIR
rm -r $TEMPLATES_DIR; sudo su $USERNAME

# Build Hybris eCommerce
sudo su $USERNAME
cd $HYBRIS_PLATFORM_PATH
. ./setantenv.sh
ant clean all
echo "###################################### Hybris Build with config changes completed! ######################################"

# DB Initialization
ant initialize
echo "###################################### Hybris Initialize completed! ######################################"
sudo su root
chmod -R 775 $HYBRIS_DIR; chown -R $USERNAME:$USERGROUP $HYBRIS_DIR
sudo su $USERNAME