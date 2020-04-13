	
#!/bin/bash
export username=$1
export password=$2
echo "$password"
results=( $( mysql --batch mysql -u $username -p$password -N -e "use db5; select script_name from scripts where script_state='succes';"  ) )
results_of_failed_scripts=( $( mysql --batch mysql -u $username -p$password -N -e "use db5; select script_name from scripts where script_state='failed';"  ) )

str=$(docker port test-mysql)
IFS=':'
read -ra ADDR <<< "$str"
docker_mysql_port=${ADDR[1]}
echo ${docker_mysql_port}
# acces to docker image 'test-mysql'
mysql -P $docker_mysql_port --protocol=tcp -u $username -p$password 

path=$(pwd)

# import database schema 
input="$path/output.sql"
var=""
while IFS= read -r line
do
var="${var}$line"
done < "$input"
mysql -P $docker_mysql_port --protocol=tcp -u$username -p$password -Bse "$var"

flag=""
for f in sql_scripts/*; do
	
			script_name=$(echo $f| cut -d'/' -f 2)
			
			  
				

				if [[ ! ${results[*]} =~ "$script_name" ]] 
				then

					flag="0"
					# echo "$script_name n'est pas encore teste"
					 
				else 
					flag="1"
					# echo "$script_name est deja teste"
					
				fi
				

							if [[ $flag -eq 0 ]]; then	
					                input="./$f"
									varrr=""	 
									while IFS= read -r line
									do
									    varrr="${varrr}$line"
									done < "$input" 

									
									mysql -P $docker_mysql_port --protocol=tcp -u$username -p$password -Bse "$varrr" 


									if [ "$?" -eq 0 ]; then
											if [[ ${results_of_failed_scripts[*]} =~ "$script_name" ]] 
											then
												mysql -u$username -p$password -Bse "use db5;update scripts set  script_state = 'succes' where script_name='$script_name';"
												echo " le script $script_name est passer avec succes"
											else
												echo " le script $script_name est passer avec succes"
												mysql -u$username -p$password -Bse "use db5;insert into scripts (script_name,script_state) values('$script_name','succes');"
											fi
									else
											if [[ ${results_of_failed_scripts[*]} =~ "$script_name" ]] 
											then
											echo " vous n'avais pas corriger ce fichier"
											else
											echo " le script ${script_name} a échoué"
											 
											mysql -u$username -p$password -Bse "use db5;insert into scripts (script_name,script_state) values('$script_name','failed');"
											fi
									fi 
							else
									echo "le script $script_name est deja tester avec succes "
							fi
done
