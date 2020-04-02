#!/bin/bash

str=$(docker port test-mysql)
IFS=':'
read -ra ADDR <<< "$str"
docker_mysql_port=${ADDR[1]}
echo ${docker_mysql_port}
mysql -P $docker_mysql_port --protocol=tcp -u root -ppixid123 


input="C:\Program Files (x86)\Jenkins\workspace\pipeline_v1"
var=""
while IFS= read -r line
do
var="${var}$line"
done < "$input"
mysql -P $docker_mysql_port --protocol=tcp -uroot -ppixid123 -Bse "$var"


IFS=':'
for f in sql_scripts/*; do
 input="./$f"
 varrr=""
   while IFS= read -r line
     do
     varrr="${varrr}$line"
   done < "$input"
 script_name=$(echo $f| cut -d'/' -f 2)
 mysql -P $docker_mysql_port --protocol=tcp -uroot -ppixid123 -Bse "$varrr"


 if [ "$?" -eq 0 ]; then 
 echo " le script $script_name est passer par succes"
	mysql -uroot -ppixid123 -Bse "use db5;insert into scripts (script_name,script_state) values('$script_name','succes');"
 else
 echo " le script ${script_name} a échoué"
	mysql -uroot -ppixid123 -Bse "use db5;insert into scripts (script_name,script_state) values('$script_name','failed');"
 fi
done
