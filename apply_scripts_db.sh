#!/bin/bash

 IFS=':'
for f in sql_scripts/*; do
input="./$f"
varrr=""
while IFS= read -r line
do
varrr="${varrr}$line"
done < "$input"
mysql -uroot -pHGKO$.xu1234 -Bse "$varrr"
done
