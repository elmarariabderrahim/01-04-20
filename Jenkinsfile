pipeline {
    agent any 
	 environment {
    		PATH = "C:\\Program Files\\Git\\usr\\bin;C:\\Program Files\\Git\\bin;${env.PATH}"
		 }
    stages {
        stage('generate_DDL') {
            steps {
		    
        	     bat 'sh -c ./exp_script.sh'
		   
		    
            }
        }
        stage('Import_schema_apply_scripts') {
            steps {
		   
        	   bat 'sh -c ./add_to_container.sh'            }
        }
        stage('Apply_to_db') {
            steps {
		    
        	    bat 'sh -c ./apply_scripts_db.sh'  
            }
        }
    }
}
