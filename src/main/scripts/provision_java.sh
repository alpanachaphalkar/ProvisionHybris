#!/bin/bash

JAVA_PACKAGE=$1
JAVA_VERSION_FOLDER=$2
PACKAGES_DIR="/opt/packages/"
JAVA_HOME="/usr/lib/jvm/$JAVA_VERSION_FOLDER/"
JAVA_PATH="/usr/lib/jvm/$JAVA_VERSION_FOLDER/bin/java"
JAVAC_PATH="/usr/lib/jvm/$JAVA_VERSION_FOLDER/bin/javac"
JAVA_PACKAGE_URL="http://54.210.0.102/java-packages/$JAVA_PACKAGE"
JAVA_PACKAGE_PATH="$PACKAGES_DIR$JAVA_PACKAGE"
DEFAULT_JAVA_PATH="/usr/bin/java"
DEFAULT_JAVAC_PATH="/usr/bin/javac"
ENVIRONMENT_FILE_PATH="/etc/environment"
BASH_PROFILE_FILE_PATH="/etc/profile"
BASH_PROFILE_JAVA_HOME_SCRIPT="/etc/profile.d/java.sh"
SUDOERS="/etc/sudoers"

mkdir -p $PACKAGES_DIR; cd $PACKAGES_DIR

# Download Java
wget $JAVA_PACKAGE_URL -P $PACKAGES_DIR
chmod -R 775 $PACKAGES_DIR; chown -R root:root $PACKAGES_DIR
mkdir -p $JAVA_HOME; tar -zxf $JAVA_PACKAGE_PATH -C $JAVA_HOME --strip-components 1
chmod -R 775 $JAVA_HOME; chown -R root:root $JAVA_HOME
rm $JAVA_PACKAGE_PATH
echo "###################################### Download JAVA completed! ######################################"
#############################################################################################################

# Setting Oracle JDK as the default JVM
update-alternatives --install $DEFAULT_JAVA_PATH java $JAVA_PATH 100
update-alternatives --install $DEFAULT_JAVAC_PATH javac $JAVAC_PATH 100

# Check Java Installation
java -version

# Setting JAVA_HOME environment variable
echo "export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))" >> $ENVIRONMENT_FILE_PATH
echo "export PATH=$(dirname $(dirname $(readlink -f $(which java))))/bin:$PATH" >> $ENVIRONMENT_FILE_PATH
echo "export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))" >> $BASH_PROFILE_FILE_PATH
echo "export PATH=$(dirname $(dirname $(readlink -f $(which java))))/bin:$PATH" >> $BASH_PROFILE_FILE_PATH
echo "export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))" >> $BASH_PROFILE_JAVA_HOME_SCRIPT
echo "export PATH=$(dirname $(dirname $(readlink -f $(which java))))/bin:$PATH" >> $BASH_PROFILE_JAVA_HOME_SCRIPT
source $ENVIRONMENT_FILE_PATH
echo "Defaults   env_keep += \"JAVA_HOME\"" >>$SUDOERS
echo "Defaults   env_keep += \"PATH\"" >>$SUDOERS
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
export PATH=$(dirname $(dirname $(readlink -f $(which java))))/bin:$PATH