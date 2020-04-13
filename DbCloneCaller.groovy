


//------------------------------
def user = 'root'
def pass = 'pixid123'
def folderName = 'sql_scripts'
//------------------------------


def tools = new GroovyScriptEngine( '.' ).with {
loadScriptByName( 'DbClone.groovy' )
}
this.metaClass.mixin tools
getReady(user, pass, folderName)

//------------------------------
getMySqlViews(folderName)
getMySqlTables(folderName)
getMySqlTriggers()
getMySqlProcedures()
getMySqlFunctions()