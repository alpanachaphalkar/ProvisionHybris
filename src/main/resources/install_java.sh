#!/bin/bash

PACKAGES_DIR="/opt/packages/"
JVM_DIR="/usr/lib/jvm/java-8-oracle/"
JAVA_PACKAGES_URI="java-packages/"
#$1="http://54.210.0.102/"
#$2="jdk-8u131-linux-x64.tar.gz"
#$3="hybris-commerce-suite-6.2.0.4.zip"
#$4="b2c_acc"
REPO_SERVER="http://54.210.0.102/"
JAVA_PACKAGE="jdk-8u131-linux-x64.tar.gz"
JAVA_PACKAGE_URL="$REPO_SERVER$JAVA_PACKAGES_URI$JAVA_PACKAGE"
JAVA_PACKAGE_PATH="$PACKAGES_DIR$JAVA_PACKAGE"
JAVA_PATH="${JVM_DIR}bin/java"
JAVAC_PATH="${JVM_DIR}bin/javac"
DEFAULT_JAVA_PATH="/usr/bin/java"
DEFAULT_JAVAC_PATH="/usr/bin/javac"
ENVIRONMENT_FILE_PATH="/etc/environment"
BASHRC_FILE_PATH="/root/.bashrc"
HOSTS_FILE_PATH="/etc/hosts"

echo "127.0.0.1 ${HOSTNAME}" >>$HOSTS_FILE_PATH
mkdir -p $PACKAGES_DIR
cd $PACKAGES_DIR

# Download Java
# wget "http://54.210.0.102/java-packages/jdk-8u131-linux-x64.tar.gz" -P /opt/packages
wget $JAVA_PACKAGE_URL -P $PACKAGES_DIR
chmod -R 775 $PACKAGES_DIR
chown -R root:root $PACKAGES_DIR
mkdir -p $JVM_DIR
tar -zxf $JAVA_PACKAGE_PATH -C $JVM_DIR --strip-components 1
chmod -R 775 $JVM_DIR
chown -R root:root $JVM_DIR
echo "###################################### Download JAVA completed! ######################################"
#############################################################################################################

# Setting Oracle JDK as the default JVM
update-alternatives --install $DEFAULT_JAVA_PATH java $JAVA_PATH 100
update-alternatives --install $DEFAULT_JAVAC_PATH javac $JAVAC_PATH 100

#############################################################################################################

# Check Java Installation
java -version

# Setting JAVA_HOME environment variable
echo "JAVA_HOME=${JVM_DIR}" >$ENVIRONMENT_FILE_PATH
echo "PATH=${JVM_DIR}bin:${PATH}" >>$ENVIRONMENT_FILE_PATH
echo "source ${ENVIRONMENT_FILE_PATH}" >>$BASHRC_FILE_PATH
echo "export JAVA_HOME=${JVM_DIR}" >>$BASHRC_FILE_PATH
echo "export PATH=${JVM_DIR}bin:${PATH}" >>$BASHRC_FILE_PATH
source $BASHRC_FILE_PATH