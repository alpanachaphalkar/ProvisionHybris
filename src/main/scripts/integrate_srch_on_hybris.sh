#!/bin/bash

HOSTS_FILE_PATH="/etc/hosts"
SRCH_HOST_IP=$2
SRCH_HOST_NAME=$1
DEFAULT_SHOP_NAME=$3
IMPEX_SCRIPT_URL="http://54.210.0.102/scripts/standaloneSolrServerConfig.impex"
IMPEX_SCRIPT_PATH="/opt/scripts/standaloneSolrServerConfig.impex"
SCRIPTS_DIR="/opt/scripts/"
HYBRIS_PLATFORM="/opt/hybris/hybris/bin/platform"
HYBRIS_USER="hybris"

wget $IMPEX_SCRIPT_URL -P $SCRIPTS_DIR
chown -R root:root $SCRIPTS_DIR; chmod -R 775 $SCRIPTS_DIR
echo "${SRCH_HOST_IP} ${SRCH_HOST_NAME}" >>$HOSTS_FILE_PATH
sed -i -e "s/srch_server/${SRCH_HOST_NAME}/g" -e "s/default_shop/${DEFAULT_SHOP_NAME}/g" $IMPEX_SCRIPT_PATH
cd $HYBRIS_PLATFORM; sudo su $HYBRIS_USER
. ./setantenv.sh
ant importImpex -Dresource=$IMPEX_SCRIPT_PATH
nohup sudo service hybris start