def user = 'root'
def pass = 'pixid123'
def folderName = 'sql_scripts'
def tools = new GroovyScriptEngine( '.' ).with {
loadScriptByName( 'class_tools.groovy' )
}
this.metaClass.mixin tools
greet(user, pass, folderName)
