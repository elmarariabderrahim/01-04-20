#!/bin/bash

str=$(docker port test-mysql)
IFS=':'
read -ra ADDR <<< "$str"
docker_mysql_port=${ADDR[1]}
echo ${docker_mysql_port}
mysql -P $docker_mysql_port --protocol=tcp -u root -ppixid123 -Bse "drop database if exists db5;create database db5;use db5;"


input="/var/lib/jenkins/workspace/03-11-V1/logfile.txt"
varrr=""
while IFS= read -r line
do
varrr="${varrr}$line"
done < "$input"
mysql -P $docker_mysql_port --protocol=tcp -uroot -pHGKO$.xu1234 -Bse "$varrr"

IFS=':'
for f in sql_scripts/*; do
input="./$f"
varrr=""
while IFS= read -r line
do
varrr="${varrr}$line"
done < "$input"
mysql -P $docker_mysql_port --protocol=tcp -uroot -ppixid123 -Bse "$varrr"
done

