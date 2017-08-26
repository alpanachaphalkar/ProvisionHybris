create database hybris CHARACTER SET utf8 COLLATE utf8_bin; 
create user 'hybris'@'%' identified by 'hybris';
GRANT ALL PRIVILEGES ON hybris.* to 'hybris'@'%';