#!/bin/bash

TEMPLATES_DIR="/opt/templates/"
HYBRIS_DB_SQL_SCRIPT_URL="http://54.210.0.102/templates/hybrisdb.sql"
HYBRIS_DB_SQL_SCRIPT_PATH="/opt/templates/hybrisdb.sql"
MYSQL_CONF_FILE="/etc/mysql/mysql.conf.d/mysqld.cnf"

wget $HYBRIS_DB_SQL_SCRIPT_URL -P $TEMPLATES_DIR

## Installing mysql
apt-get update
export DEBIAN_FRONTEND="noninteractive"
debconf-set-selections <<< "mysql-server mysql-server/root_password password root"
debconf-set-selections <<< "mysql-server mysql-server/root_password_again password root"
apt-get -y install mysql-server-5.7

## Creating hybris database
mysql -u root -proot < $HYBRIS_DB_SQL_SCRIPT_PATH
sed -i 's/^\(bind-address\s*=\s*\).*$/\10\.0\.0\.0/' $MYSQL_CONF_FILE

service mysql restart