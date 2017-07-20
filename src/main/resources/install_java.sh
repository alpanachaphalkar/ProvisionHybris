#!/bin/bash

PACKAGES_DIR="/opt/packages/"
JAVA_FOLDER="/usr/lib/jvm/java-8-oracle/"
JAVA_HOME="/usr/lib/jvm/java-8-oracle/bin/java"
JAVAC_HOME="/usr/lib/jvm/java-8-oracle/bin/javac"
JAVA_PACKAGES_URI="java-packages/"
#$1="http://54.210.0.102/"
#$2="jdk-8u131-linux-x64.tar.gz"
#$3="hybris-commerce-suite-6.2.0.4.zip"
#$4="b2c_acc"
REPO_SERVER="http://54.210.0.102/"
JAVA_PACKAGE="jdk-8u131-linux-x64.tar.gz"
JAVA_PACKAGE_URL="$REPO_SERVER$JAVA_PACKAGES_URI$JAVA_PACKAGE"
JAVA_PACKAGE_PATH="$PACKAGES_DIR$JAVA_PACKAGE"
DEFAULT_JAVA_PATH="/usr/bin/java"
DEFAULT_JAVAC_PATH="/usr/bin/javac"
ENVIRONMENT_FILE_PATH="/etc/environment"
BASHRC_FILE_PATH="/etc/bash.bashrc"
BASH_PROFILE_FILE_PATH="/etc/profile"
BASH_PROFILE_JAVA_HOME_SCRIPT="/etc/profile.d/java.sh"
HOSTS_FILE_PATH="/etc/hosts"

echo "127.0.0.1 ${HOSTNAME}" >>$HOSTS_FILE_PATH
mkdir -p $PACKAGES_DIR
cd $PACKAGES_DIR

# Download Java
# wget "http://54.210.0.102/java-packages/jdk-8u131-linux-x64.tar.gz" -P /opt/packages
wget $JAVA_PACKAGE_URL -P $PACKAGES_DIR
chmod -R 775 $PACKAGES_DIR
chown -R root:root $PACKAGES_DIR
mkdir -p $JAVA_FOLDER
tar -zxf $JAVA_PACKAGE_PATH -C $JAVA_FOLDER --strip-components 1
chmod -R 775 $JAVA_FOLDER
chown -R root:root $JAVA_FOLDER
echo "###################################### Download JAVA completed! ######################################"
#############################################################################################################

# Setting Oracle JDK as the default JVM
update-alternatives --install $DEFAULT_JAVA_PATH java $JAVA_HOME 100
update-alternatives --install $DEFAULT_JAVAC_PATH javac $JAVAC_HOME 100

#############################################################################################################

# Check Java Installation
java -version

# Setting JAVA_HOME environment variable
echo "JAVA_HOME=${JAVA_FOLDER}" >$BASH_PROFILE_JAVA_HOME_SCRIPT
echo "export JAVA_HOME" >>$BASH_PROFILE_JAVA_HOME_SCRIPT
echo "PATH=${JAVA_FOLDER}bin:${PATH}" >>$BASH_PROFILE_JAVA_HOME_SCRIPT
echo "export PATH" >>$BASH_PROFILE_JAVA_HOME_SCRIPT
chmod -R 775 $BASH_PROFILE_JAVA_HOME_SCRIPT
chown -R root:root $BASH_PROFILE_JAVA_HOME_SCRIPT

echo "export JAVA_HOME=${JAVA_FOLDER}" >$ENVIRONMENT_FILE_PATH
echo "export PATH=${JAVA_FOLDER}bin:${PATH}" >>$ENVIRONMENT_FILE_PATH

echo "source ${ENVIRONMENT_FILE_PATH}" >>$BASHRC_FILE_PATH
echo "export JAVA_HOME=${JAVA_FOLDER}" >>$BASHRC_FILE_PATH
echo "export PATH=${JAVA_FOLDER}bin:${PATH}" >>$BASHRC_FILE_PATH

echo "source ${ENVIRONMENT_FILE_PATH}" >>$BASH_PROFILE_FILE_PATH
echo "export JAVA_HOME=${JAVA_FOLDER}" >>$BASH_PROFILE_FILE_PATH
echo "export PATH=${JAVA_FOLDER}bin:${PATH}" >>$BASH_PROFILE_FILE_PATH
source $BASH_PROFILE_JAVA_HOME_SCRIPT