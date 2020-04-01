pipeline {
    agent any 
    stages {
        stage('generate_DDL') {
            steps {
		    
		    sh 'chmod 777 ./exp_script.sh'
        	    sh './exp_script.sh'
		   
		    
            }
        }
        stage('Import_schema_apply_scripts') {
            steps {
		    sh 'docker start test-mysql' 
		    sh 'chmod 777 ./add_to_container.sh'
        	    sh './add_to_container.sh'            }
        }
        stage('Apply_to_db') {
            steps {
		    
		    sh 'chmod 777 ./apply_scripts_db.sh'
        	    sh './apply_scripts_db.sh'  
            }
        }
    }
}
