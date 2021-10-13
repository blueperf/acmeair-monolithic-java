#!/bin/bash

dir=${1}
master_list=()

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
    for value in $list
    do 
      if [[ ! " ${master_list[*]} " =~ " ${value} " ]] && [[ ! "${value}" == */* ]] && [[ ! " ${exclude_list[*]} " =~ " ${value} " ]]; then
        echo "Adding $value"
        master_list+=($value)
      fi
    done
  done

  list=$(printf '%s,' "${master_list[@]}")
  final_list=${list%?} 
else
  list=$(jdeps --list-deps --multi-release 11 --ignore-missing-deps -q ${dir})
  for value in $list
    do
     if [[ ! " ${master_list[*]} " =~ " ${value} " ]] && [[ ! "${value}" == */* ]] && [[ ! " ${exclude_list[*]} " =~ " ${value} " ]]; then
       echo "Adding $value"
       master_list+=($value)
     fi
   done
  list=$(printf '%s,' "${master_list[@]}")
  final_list=${list%?} 
fi

if [[ "${2}" == "--append" ]] || [[ "${3}" == "--append" ]]
then
  echo -n ",${final_list}" >> java_modules.txt
else
  echo -n ${final_list} > java_modules.txt
fi 


cat java_modules.txt
