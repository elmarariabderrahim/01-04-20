import groovy.sql.Sql
import java.sql.DriverManager


class Tools {
	def greet(String user,String pass,String folderName) {
	long now = System.currentTimeMillis();
	//----------------------
	//Setting up environment
	//----------------------
	def d_tables = []
	def d_views = []
	def d_procedures = []
	def d_functions = []
	def TRIGGER_NAME = []
	def finalScript = ''
	def String stmt 
	File file = new File("out.txt")
	file.write "/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */; /*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */; /*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */; /*!50503 SET NAMES utf8mb4 */; /*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */; /*!40103 SET TIME_ZONE='+00:00' */; /*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */; /*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */; /*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */; /*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;\n"
		//-----------------
		// Getting DB names
		//-----------------
		def tableSchemas = []
		def sql = Sql.newInstance('jdbc:mysql://localhost:3306/', user, pass,'com.mysql.jdbc.Driver');  
		sql.eachRow('show databases'){
			row-> row[0]
			tableSchemas.add(row[0])
		}
		//---------------
		//Get SQL scripts
		//---------------
		def sqlFolder = new File(folderName)
		def listOfScripts = []
		def text
		sqlFolder.eachFile{
			text = it.getText('UTF-8')
			listOfScripts.add(text.toLowerCase())
		}
		//----------------------
		//Cleaning those scripts
		//----------------------
		for(def int i=0 ; i<listOfScripts.size() ; i++){
			listOfScripts[i] = listOfScripts[i].replaceAll("[^a-zA-Z0-9_]", " ")
			listOfScripts[i] = listOfScripts[i].replaceAll(" +", " ")
			listOfScripts[i] = listOfScripts[i].split(" ")
		}
		//------------------
		//seach for used DBs
		//------------------
		def foundDbs = []
		for(def int i=0 ; i<tableSchemas.size() ; i++)
			for(def int j=0 ; j<listOfScripts.size() ; j++)
				for(def int k=0 ; k<listOfScripts[j].size() ; k++)
					if(listOfScripts[j][k].equals(tableSchemas[i]))
						foundDbs.add(tableSchemas[i])
		//-----------------------------------------------------
		//This list is what databases are used in those scripts
		//-----------------------------------------------------
		foundDbs = foundDbs.unique()
		//-----------------------------
		//Extract table names from databases
		//-----------------------------
		def tableQuery;
		def tableNames = []
		def foundDbTables = []
		foundDbs.each{
			//tableQuery = 'show tables from ' + it
			tableQuery = "select TABLE_NAME from information_schema.tables where TABLE_SCHEMA= $it"
			
			sql.eachRow(tableQuery){
				row-> row[0]
				tableNames.add(row[0])
				//foundDbTables.add(it+'.'+row[0])
			}
		}
		//---------------------------------------------
		//Next we need to find out what tables are used
		//---------------------------------------------
		def usedTables = []
		for(def int i=0 ; i<tableNames.size() ; i++)
			for(def int j=0 ; j<listOfScripts.size() ; j++)
				for(def int k=0 ; k<listOfScripts[j].size() ; k++)
					if(listOfScripts[j][k].equals(tableNames[i]))
						usedTables.add(tableNames[i])
		usedTables = usedTables.unique()
		//----------------------------------
		//get all table names from databases
		//----------------------------------
		
		//------------------------------------------
		//look for the existing names in the scripts
		//------------------------------------------
		//------------------------------------------------------------------
		//for each database get tables and see if there is equality in names
		//------------------------------------------------------------------
		tableNames = []
		def finalTables = []
		foundDbs.each{
			tableQuery = 'show tables from ' + it
			sql.eachRow(tableQuery){
				row-> row[0]
				tableNames.add(row[0])
				//foundDbTables.add(it+'.'+row[0])
			}
			for(def int i=0 ; i<usedTables.size() ; i++)
				for(def int j=0 ; j<tableNames.size() ; j++)
					if(usedTables[i].equals(tableNames[j]))
						finalTables.add(it+'.'+usedTables[i])
			finalTables = finalTables.unique()
						
			tableNames = []
		}
		//---------------------
		//get constraint tables
		//---------------------
		def constTables = []
		def currentTable = []
		finalTables.each{
			currentTable = it.split('\\.')
			stmt = "SELECT REFERENCED_TABLE_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE CONSTRAINT_SCHEMA = '${currentTable[0]}' AND TABLE_NAME = '${currentTable[1]}' AND REFERENCED_TABLE_NAME != 'null'"
			sql.eachRow(stmt){
				row-> constTables.add(currentTable[0]+'.'+row[0])
			}
			
		}
		constTables.each{
			finalTables.add(it)
		}
		finalTables = finalTables.unique()
		//---------------------------------------
		//if true add it with full name db.tables
		//---------------------------------------
		//----------------------
		//Create those databases
		//----------------------
		foundDbs.each{
			stmt = 'SHOW CREATE DATABASE ' + it + ';\n'
			sql.eachRow(stmt){
				row-> finalScript <<= row[1]+';\n'
			}
		}
		//-------------
		//Create tables
		//-------------
		finalTables.each{
			stmt = 'SHOW CREATE TABLE ' + it + ';\n'
			sql.eachRow(stmt){
				row-> finalScript <<= 'use ' + it.split('\\.')[0] + ';\n' + row[1] + ';\n'
			}
			
		}
		file << finalScript
		
		
		println finalTables
		System.out.println( (System.currentTimeMillis() - now) + " ms");

		
	}
}	
