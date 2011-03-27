#!/bin/bash

if [ -z "$PROGRAM_DIR" ]; then
  echo "PROGRAM_DIR is undefined"
  exit 1
fi

. "$PROGRAM_DIR/lib/arrays.sh"

collect_dates () {
  local log_dates
  local dat
  local j=0
  local f

  for f in `ls "$1"/`; do
    dat=`echo $f | sed 's/^\([0-9]\{4\}-[0-9]\{2\}-[0-9]\{2\}\)-.*$/\1/'`

    # stringifies the array to pass it as an argument
    local log_dates_as_arg=`echo ${log_dates[@]}`

    ARRAYS_contains "$log_dates_as_arg" $dat
    if [ "$?" -eq 0 ]; then
      log_dates[j++]=$dat
    fi
  done

  # echoes the array, so that it can be retrieved from outside this function
  echo "${log_dates[@]}"
}

WORKING_DIR=$1
OUTPUT_FILE=$2
if [ -z "$OUTPUT_FILE" ]; then
  OUTPUT_FILE=stats.out
fi

if [ -f "$OUTPUT_FILE" ]; then
  cp "$OUTPUT_FILE" "$OUTPUT_FILE.bak"
fi

echo "Reading stats from logs in '$WORKING_DIR'..."

log_dates=( `collect_dates "$WORKING_DIR"` )

echo "Available dates: ${log_dates[@]}"

for d in ${log_dates[@]}; do
  echo "Computing stats for $d..."

  declare -a plugins
  declare -a plugins_count

  echo $d >> "$OUTPUT_FILE"

  for l in `grep "GET http://moreunit\.sourceforge\.net/update-site/plugins/org\.moreunit.*\.jar" "$WORKING_DIR/$d"*`; do
    if [ $(echo $l | sed 's/^http://') != $l -a $(echo $l | sed 's/\.source_//') = $l ]; then
      plugin=`echo $l | sed 's/^.*\/\([^\/]*\)$/\1/'`

      # stringifies the array to pass it as an argument
      plugins_as_arg=`echo ${plugins[@]}`

      ARRAYS_contains "$plugins_as_arg" "$plugin"
      if [ "$?" -eq 0 ]; then
        plugin_idx=${#plugins[@]}
        plugins[$plugin_idx]=$plugin
        plugins_count[$plugin_idx]=1
      else
        plugin_idx=$ARRAYS_index_of_item
        plugins_count[$plugin_idx]=$((${plugins_count[$plugin_idx]} + 1))
      fi
    fi
  done

  count=${#plugins[@]}
  for (( i=0; i<count; i++)); do
    echo "${plugins[$i]} ${plugins_count[$i]}" >> "$OUTPUT_FILE"
  done

  unset plugins
  unset plugins_count
done

exit 0

