	
#!/bin/bash
	
results=( $( mysql --batch mysql -u root -ppixid123 -N -e "use db5; select script_name from scripts where script_state='succes' and script_validation='null';"  ) )


 IFS=':'
for f in sql_scripts/*; do
	input="./$f"
	script_name=$(echo $f| cut -d'/' -f 2)
	if [[ ! ${results[*]} =~ "$script_name" ]]; then 
			varrr=""
			while IFS= read -r line
			do
				varrr="${varrr}$line"
			done < "$input"
	    	mysql -uroot -ppixid123 -Bse "$varrr"
		    if [ "$?" -eq 0 ]; then 
		    	echo " le script $script_name est passer avec succes"
				mysql -uroot -ppixid123 -Bse "use db5;update scripts set script_validation ='valid' where script_name='$script_name';"
				
			else
				echo " le script ${script_name} a échoué"
												 
				mysql -uroot -ppixid123 -Bse "use db5;update scripts set script_validation ='invalid' where script_name='$script_name';"
			fi
	else
		echo "il n'exixt pas de nv script ou le script n'est valider dans l'image docker "
	fi
done









