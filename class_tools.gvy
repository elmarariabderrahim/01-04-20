import groovy.sql.Sql
import java.sql.DriverManager
class Tools {
def greet() {
		def sql = Sql.newInstance('jdbc:mysql://localhost:3306/db5', 'root', 'pixid123', 'com.mysql.jdbc.Driver')
		
		def ArrayList tablesName = new ArrayList<>()
		def ArrayList showCreateTables = new ArrayList<>()
		def String[] currentCreateTable
		def Map<String, ArrayList<String>> tablesConstraints=new HashMap<String, ArrayList<String>>();
		def ArrayList<String[]> showCreateTables_min = new ArrayList<String[]>()
		def ArrayList<String> currentConstraints = new ArrayList<String>()
		def Iterator itConstraints;
		
		
		
		
		sql.eachRow('show tables'){
			row->   row[0]
			tablesName.add(row[0])
		}
		
		def ArrayList arryOflines = new ArrayList<>()
		for (def int j =0 ; j<tablesName.size ; j++) {
			def String stmt= "show create table ${tablesName[j]}"
			sql.eachRow(stmt){
				row-> row[1]
				showCreateTables.add(row[1]+";")
			}
		}

		
		
		
		for(def int i=0 ; i<showCreateTables.size ; i++) {
			def flag=false
			currentConstraints.clear()
			currentCreateTable = showCreateTables[i].split("\n")
			showCreateTables_min.add(currentCreateTable)
				for(def int j=0 ; j<currentCreateTable.length ; j++) {
					if (currentCreateTable[j].contains("CONSTRAINT")) {
						if(!flag) {
							showCreateTables_min[i][j-1]=showCreateTables_min[i][j-1].substring(0, currentCreateTable[j-1].length() - 1)
							flag=true
						}
						if (currentCreateTable[j].substring(currentCreateTable[j].length()-1, currentCreateTable[j].length())==',') {
							if(j>0)
currentConstraints.add(currentCreateTable[j].substring(0, currentCreateTable[j].length() - 1) + ", ADD")							else
								currentConstraints.add(currentCreateTable[j].substring(0, currentCreateTable[j].length() - 1))
							
						}
						else
							if(j!=0)
								currentConstraints.add(currentCreateTable[j] )
							else
								currentConstraints.add(currentCreateTable[j])
						
						
						showCreateTables_min[i][j]=""
						
					}
				}
				
				/*for(def int k=0 ; k<showCreateTables_min.size ; k++) {
					for(def int s=0 ; s<showCreateTables_min[k].length ; s++) {
						println showCreateTables_min[k][s]
					}
				}*/
				if(!currentConstraints.isEmpty()) {
					currentConstraints.add(";")
					tablesConstraints.put(tablesName[i], currentConstraints.toArray())
				}
				
				//showCreateTables_min[i][showCreateTables_min[i].length-1]=showCreateTables_min[i][showCreateTables_min[i].length-1]+";"
		}
		println "use db5;"
		for(def int k=0 ; k<showCreateTables_min.size ; k++) {
		 for(def int s=0 ; s<showCreateTables_min[k].length ; s++) {
			 println showCreateTables_min[k][s]
		 }
	 } 

	 def Iterator<Map.Entry<String, ArrayList<String>>> itr = tablesConstraints.entrySet().iterator();
	 
	 while(itr.hasNext()) 
        { 
             Map.Entry<String, String> entry = itr.next(); 
             println( "ALTER TABLE "+ entry.getKey() +
                                 " ADD "   );
							 for(String s : entry.getValue()) {
								 println s 
							 }
							  
        } 
}
}

