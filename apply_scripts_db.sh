	
#!/bin/bash
# export username=$1
# export password=$2	
# results=( $( mysql --batch mysql -u $username -p$password -N -e "use db5; select script_name from scripts where script_state='succes' and script_validation='null' ;"  ) )


#  IFS=':'
# for f in sql_scripts/*; do
# 	input="./$f"
# 	script_name=$(echo $f| cut -d'/' -f 2)
	
# 	if [[ !  ${results[*]} =~ "$script_name" ]]
# 	then 
# 			varrr=""
# 			while IFS= read -r line
# 			do
# 				varrr="${varrr}$line"
# 			done < "$input"
# 	    	mysql -u$username -p$password -Bse "$varrr"
# 		    if [ "$?" -eq 0 ]; then 
# 		    	echo " le script $script_name est passer avec succes"
# 				mysql -u$username -p$password -Bse "use db5;update scripts set script_validation ='valid' where script_name='$script_name';"
				
# 			else
# 				echo " le script ${script_name} a échoué"
												 
# 				mysql -u$username -p$password -Bse "use db5;update scripts set script_validation ='invalid' where script_name='$script_name';"
# 			fi
# 	else
# 		echo "il n'exixt pas de nv script ou le script n'est valider dans l'image docker "
	
# 	fi
# done


results=( $( mysql --batch mysql -u root -ppixid123 -N -e "use db5; select script_name from scripts where script_state='succes' and script_validation='valid';"  ) )
# results_of_failed_scripts=( $( mysql --batch mysql -u root -ppixid123 -N -e "use db5; select script_name from scripts where script_state='succes' and script_validation='invalid';"  ) )
results_of_succes_scripts=( $( mysql --batch mysql -u root -ppixid123 -N -e "use db5; select script_name from scripts where script_state='succes' and script_validation='invalid';"  ) )



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

									
									mysql -uroot -ppixid123 -Bse "$varrr" 


									if [ "$?" -eq 0 ]; then
											if [[ ${results_of_succes_scripts[*]} =~ "$script_name" ]] 
											then
												mysql -u$username -p$password -Bse "use db5;update scripts set script_validation ='valid' where script_name='$script_name';"
												echo " le script $script_name est passer avec succes"
											else
												echo " le script $script_name est passer avec succes"
												mysql -u$username -p$password -Bse "use db5;update scripts set script_validation ='valid' where script_name='$script_name';"
											fi
									else
											if [[ ${results_of_succes_scripts[*]} =~ "$script_name" ]] 
											then
											echo " le  $script_name n'ai pas corriger "
											else
											echo " le script ${script_name} a échoué"
											 
											mysql -u$username -p$password -Bse "use db5;update scripts set script_validation ='invalid' where script_name='$script_name';"
											fi
									fi 
							else
									echo "le script $script_name est deja tester avec succes "
							fi


							
							
							
							
done









