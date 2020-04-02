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
	    boolean scripts_succes = true
        stage('Import_schema_apply_scripts') {
            
			   try{
				 bat 'sh -c ./add_to_container.sh'
			   }catch (Exception e){
				scripts_succes = false
			   }
        	  
        }
        stage('Apply_to_db') {
            steps {
		    if(scripts_succes){
        	    bat 'sh -c ./apply_scripts_db.sh'  
		    }
            }
        }
    }
}
