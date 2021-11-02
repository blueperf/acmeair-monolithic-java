#!/bin/bash

parse_list () {
  for value in ${list}
  do 
    if [[ ! " ${jdeps_list[*]} " =~ " ${value} " ]] && [[ ! " ${exclude_list[*]} " =~ " ${value} " ]] 
    then
      echo "Adding $value"
      jdeps_list+=($value)
    fi
  done
}

dir=${1}
jdeps_list=()

if [[ -f java_modules_exclude.txt ]]
then
  excludeString=$(cat java_modules_exclude.txt)
  IFS=', ' read -r -a exclude_list <<< "$excludeString"
else
  exlude_list=()
fi

if [[ "${2}" == "--jars" ]] 
then
  jars=$(ls -l ${dir} | awk '{print $9}')
  for jar in ${jars} 
  do
    list=$(jdeps --list-deps --multi-release 11 --ignore-missing-deps -q ${dir}/${jar})
    parse_list
  done
else
  list=$(jdeps --list-deps --multi-release 11 --ignore-missing-deps -q ${dir})
  parse_list
fi

formatted_list=$(printf '%s,' "${jdeps_list[@]}")
formatted_list=${formatted_list%?} 

if [[ -f java_modules.txt ]]
then
  echo -n ",${formatted_list}" >> java_modules.txt
else
  echo -n ${formatted_list} > java_modules.txt
fi 
