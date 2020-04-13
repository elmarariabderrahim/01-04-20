	
#!/bin/bash
	
results=( $( mysql --batch mysql -u root -ppixid123 -N -e "use db5; select script_name from scripts where script_state='succes';"  ) )


str=$(docker port test-mysql)
IFS=':'
read -ra ADDR <<< "$str"
docker_mysql_port=${ADDR[1]}
echo ${docker_mysql_port}
# acces to docker image 'test-mysql'
mysql -P $docker_mysql_port --protocol=tcp -u root -ppixid123 

path=$(pwd)

# import database schema 
input="$path/out.txt"
var=""
while IFS= read -r line
do
var="${var}$line"
done < "$input"
mysql -P $docker_mysql_port --protocol=tcp -uroot -ppixid123 -Bse "$var"

flag=""
for f in sql_scripts/*; do
	
			script_name=$(echo $f| cut -d'/' -f 2)
			
			  
				

				if [[ ! ${results[*]} =~ "$script_name" ]] 
				then

					flag="0"
					echo "$script_name n'est pas encore teste"
					 
				else 
					flag="1"
					echo "$script_name est deja teste"
					
				fi
				

							if [[ $flag -eq 0 ]]; then	
					                input="./$f"
									varrr=""	 
									while IFS= read -r line
									do
									    varrr="${varrr}$line"
									done < "$input" 

									
									mysql -P $docker_mysql_port --protocol=tcp -uroot -ppixid123 -Bse "$varrr" 


									if [ "$?" -eq 0 ]; then 
											echo " le script $script_name est passer avec succes"
											mysql -uroot -ppixid123 -Bse "use db5;insert into scripts (script_name,script_state) values('$script_name','succes');"
									else
											echo " le script ${script_name} a échoué"
											 
											mysql -uroot -ppixid123 -Bse "use db5;insert into scripts (script_name,script_state) values('$script_name','failed');"
									fi 
							else
									echo "le script $script_name est deja tester avec succes "
							fi
done










