	
#!/bin/bash
export username=$1
export password=$2	
results=( $( mysql --batch mysql -u $username -p$password -N -e "use db5; select script_name from scripts where script_state='succes' and script_validation='null' or script_state='succes' and script_validation='invalid' ;"  ) )
results_succes=( $( mysql --batch mysql -u $username -p$password -N -e "use db5; select script_name from scripts where  script_state='succes' and script_validation='valid' ;"  ) )


 IFS=':'
for f in sql_scripts/*; do
	input="./$f"
	script_name=$(echo $f| cut -d'/' -f 2)
	if [[   ${results_succes[*]} =~ "$script_name" ]] 
	then 
	echo "le $script_name script est deja test dans la base et dans docker"
	else
	if [[ !  ${results[*]} =~ "$script_name" ]]
	then 
			varrr=""
			while IFS= read -r line
			do
				varrr="${varrr}$line"
			done < "$input"
	    	mysql -u$username -p$password -Bse "$varrr"
		    if [ "$?" -eq 0 ]; then 
		    	echo " le script $script_name est passer avec succes"
				mysql -u$username -p$password -Bse "use db5;update scripts set script_validation ='valid' where script_name='$script_name';"
				
			else
				echo " le script ${script_name} a échoué"
												 
				mysql -u$username -p$password -Bse "use db5;update scripts set script_validation ='invalid' where script_name='$script_name';"
			fi
	else
		echo "il n'exixt pas de nv script ou le script n'est valider dans l'image docker "
	fi
	fi
done









