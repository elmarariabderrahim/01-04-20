pipeline {
    agent any 
	 environment {
    		PATH = "C:\\Program Files\\Git\\usr\\bin;C:\\Program Files\\Git\\bin;${env.PATH}"
		 }
    stages {
        stage('generate_DDL') {
            steps {
		    
        	     bat 'sh -c ./exp_script.sh '
		    
		    
            }
        }
        stage('Import_schema_apply_scripts') {
            steps {
		   	withCredentials([
					usernamePassword(
						credentialsId: '0467c09c-9a30-4e9f-bdc9-6126fd2482d4', 
						usernameVariable: 'USERNAME',
						passwordVariable: 'PASSWORD'
						
						
					)
			]){
        	     bat "sh  ./add_to_container.sh ${USERNAME}  ${PASSWORD}"
		    }
		    
        	      
		    
		   
        	              }
        }
        stage('Apply_to_db') {
            steps {
		    withCredentials([
					usernamePassword(
						credentialsId: '0467c09c-9a30-4e9f-bdc9-6126fd2482d4', 
						usernameVariable: 'USERNAME',
						passwordVariable: 'PASSWORD'
						
						
					)
			]){
        	    bat "sh  ./apply_scripts_db.sh ${USERNAME}  ${PASSWORD}" 
		    }
		    
        	     
            }
        }
    }
}
