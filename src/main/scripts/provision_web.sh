#!/bin/bash

TEMPLATES_DIR="/opt/templates/"
APACHE_DIR="/etc/apache2/"
APP_HOST_NAME=$1
APP_HOST_IP=$2
DOMAIN_NAME=$3
SITE_CONF="${DOMAIN_NAME}.conf"
HOSTS_FILE_PATH="/etc/hosts"
DOMAIN_TEMPLATE_URL="http://54.210.0.102/templates/domain_name.conf"
DOMAIN_TEMPLATE_CURRENT_PATH="/opt/templates/domain_name.conf"
DOMAIN_TEMPLATE_DESIRED_PATH="/etc/apache2/sites-available/${SITE_CONF}"


mkdir $TEMPLATES_DIR; echo "${APP_HOST_IP} ${APP_HOST_NAME}" >>$HOSTS_FILE_PATH

apt-get update
apt-get --assume-yes install apache2
service apache2 stop
a2enmod proxy proxy_ajp proxy_http setenvif mime socache_shmcb ssl

wget $DOMAIN_TEMPLATE_URL -P $TEMPLATES_DIR
sed -i -e "s/domain_name/$DOMAIN_NAME/g" -e "s/app_host/$APP_HOST_NAME/g" $DOMAIN_TEMPLATE_CURRENT_PATH
mv $DOMAIN_TEMPLATE_CURRENT_PATH $DOMAIN_TEMPLATE_DESIRED_PATH
a2ensite $SITE_CONF

service apache2 start