import groovy.sql.Sql
import java.sql.DriverManager
class Tools {
		def sql
	public Services() {
		sql = Sql.newInstance('jdbc:mysql://localhost:3306/', 'root', 'pixid123', 'com.mysql.jdbc.Driver')
    }

    def getDatabaseName() {
		  def ArrayList databaseName = new ArrayList<>()
		sql.eachRow('show databases'){
			row->   row[0]
			databaseName.add(row[0])
		}
		return databaseName
	}


	def getTableNames(ArrayList databaseName) {
		def ArrayList arryOfTableName = new ArrayList<>()
		def Map<String, List<String>> db_tables=new HashMap<>()
		//show tables
		for(String db : databaseName) {
			
			if(!db.equals('information_schema') && !db.equals('mysql')&& !db.equals('performance_schema')&& !db.equals('sys') && !db.equals('snomedct')) {
		sql.eachRow('show tables from ' + db){
			 row->   row[0]
			 arryOfTableName.add(row[0])
		}
		
		db_tables.put(db,arryOfTableName.toArray())
		
		arryOfTableName.clear()
			}
		}
		
		return db_tables
	}


	def getQuery(ArrayList databaseName){								
		def arryOfTableName = getTableNames(databaseName)	
		def ArrayList arryOflines = new ArrayList<>()		
		def Map<String, List<String>> db_table_show=new HashMap<>()
		def Iterator<Map.Entry<String, ArrayList<String>>> itr = arryOfTableName.entrySet().iterator();
		while(itr.hasNext())
		   {
			   Map.Entry<String, String> entry = itr.next();
			   for(String sss : entry.getValue()) {
				   def String stmt = "show create table "+entry.getKey() +"."+sss
				   sql.eachRow(stmt){
					   row-> row[1]
					   arryOflines.add("use "+entry.getKey() +" ;\n "+row[1]+";")
				   }
			   }	
				db_table_show.put(entry.getKey(), arryOflines.toArray())
				arryOflines.clear()
								
	       }
		return db_table_show
	}


 	
 	def enleverFin(String str) {
		if ((str != null) && (str.length() > 0) && str.substring(str.length()-1, str.length())==',') {
			return str.substring(0, str.length() - 1)
			
		}
		 return str

			}
	
		def queryWithoutConstraints(ArrayList databaseName) {
		
		def String[] line
		def List lines=new ArrayList()
		def List lineWitoutConstraint=new ArrayList()
		  def Map<String, List<String>> querys=getQuery(databaseName)
		  def Map<String, List<String>> queryWithoutConstraint=new HashMap<>()
		for (key in querys) {
			  for (String k : key.value)	{
				  line= k.split("\n")
				  for(def int i =0;i<line.length;i++) {
					  if(!line[i].contains("CONSTRAINT")) {
						  lineWitoutConstraint.add(line[i])
					  }
					  
				  }
			  }
		}
		
		for(def int i =0;i<lineWitoutConstraint.size();i++) {
		   
			if (lineWitoutConstraint[i].charAt(0)==")"){
			   
				
			lineWitoutConstraint[i-1]= enleverFin(lineWitoutConstraint[i-1])
			}  
		}
		return lineWitoutConstraint
		   
		
	}


	//===================================
	def gcst(ArrayList databaseName){								// parametere is a liste of db names [db1, db2, db3, ...]
			def arryOfTableName = getTableNames(databaseName)				// map contains <db name, [create1, create2, create3,...]
			def ArrayList arryOflines = new ArrayList<>()
			def Map<String, List<String>> db_table_show=new HashMap<>()
			def Iterator<Map.Entry<String, ArrayList<String>>> itr = arryOfTableName.entrySet().iterator();
			
			while(itr.hasNext())
			   {    def table
				   Map.Entry<String, String> entry = itr.next();
				   for(String sss : entry.getValue()) {
					   def String stmt = "show create table "+entry.getKey() +"."+sss
					   table = sss
					   sql.eachRow(stmt){
						   row-> row[1]
						   arryOflines.add(table+" use "+entry.getKey() +" ;\n "+row[1]+";")
					   }
				   }
					db_table_show.put(entry.getKey(), arryOflines.toArray())
					arryOflines.clear()
									
			   }
			  
			return db_table_show
			
			
			
			
			
		}
		
		
		
		
		def getContraint(ArrayList databaseName) {
			def String stmt 
			def String[] line
			def String table
			def List lines=new ArrayList()
			def List contraint=new ArrayList()
			  def Map<String, List<String>> querys=gcst(databaseName)
			  def Map<String, List<String>> table_contraint=new HashMap<>()
			for (key in querys) {
				  for (String k : key.value)	{
					  line= k.split("\n")
						
					  for(def int i =0;i<line.length;i++) {
							if(line[i].contains("use")) {
								table= line[i].split(" ")[0]
							}
							
						  if(line[i].contains("CONSTRAINT")) {
							  
							  stmt="use "+key.key+";"+"\n"+"ALTER TABLE "+table+" ADD "+enleverFin(line[i])+";"
							  contraint.add(stmt)
							  
						  }
						  
						  
					  }

				  }
			}
			

			
			return contraint
			   
			
		}
	//===================================


	
	def greet() {
	    Services()
	    ArrayList databaseName = getDatabaseName()
		
		
	    def Map<String, List<String>> querywithoutContraints=getQuery(databaseName)
	 

		    for(String db:getDatabaseName()){
		    	if(!db.equals('information_schema') && !db.equals('mysql')&& !db.equals('performance_schema')&& !db.equals('sys') ) {
		  	      println "CREATE DATABASE IF NOT EXISTS "+db+";"
		  		}
		   }
		  for(String st:queryWithoutConstraints(databaseName)) {
			  println st
		  }

		  for(String c:getContraint(databaseName)){
		  	println c
		  }


		 
		  

		
		
	 }

}
