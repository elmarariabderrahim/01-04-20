export CLASSPATH=./lib/mysql-connector-java-5.1.48-bin.jar
queries="$(groovy class_script.gvy)"
echo "${queries}" | tee logfile.txt

