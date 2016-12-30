#!/bin/bash

ARRAYS_index_of_item="-1"

ARRAYS_contains () {
  ARRAYS_index_of_item="-1"
  local array=( `echo "$1"` )
  local array_size=${#array[@]}
  local k

  for (( k=0; k<array_size; k++ )); do
    if [ "${array[$k]}" == "$2" ]; then
      ARRAYS_index_of_item=$k
      return 1
    fi
  done

  return 0
}

