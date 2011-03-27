#!/bin/bash

if [ -z "$PROGRAM_DIR" ]; then
  echo "PROGRAM_DIR is undefined"
  exit 1
fi

. "$PROGRAM_DIR/lib/arrays.sh"

write_month_stats () {
  if [ "$current_month" != "" ]; then
    echo "$current_month" >> "$OUTPUT_FILE"

    local i
    local count=${#plugins[@]}
    for (( i=0; i<count; i++ )); do
      echo "${plugins[$i]} ${plugins_count[$i]}" >> "$OUTPUT_FILE"
    done
  fi
}

DETAILS=$1
OUTPUT_FILE=$2
if [ -z "$OUTPUT_FILE" ]; then
  OUTPUT_FILE=compiled-stats.out
fi

if [ -f "$OUTPUT_FILE" ]; then
  cp "$OUTPUT_FILE" "$OUTPUT_FILE.bak"
fi

echo "Compiling stats in '$DETAILS'..."

current_month=""

while read l; do
  mon=`echo $l | sed 's/^\([0-9]\{4\}-[0-9]\{2\}\)-[0-9]\{2\}$/\1/'`
  if [ "$mon" != "$l" ]; then
    if [ "$mon" != "$current_month" ]; then
      if [ "$current_month" != "" ]; then
        write_month_stats

        unset plugins
        unset plugins_count
      fi

      echo "Compiling stats for month: $mon"
      current_month=$mon
      declare -a plugins
      declare -a plugins_count
    fi
  else
    plugin=`echo $l | sed 's/^\(.*\) [0-9]*$/\1/'`
    plugin_count=`echo $l | sed 's/^.* \([0-9]*\)$/\1/'`

    # stringifies the array to pass it as an argument
    plugins_as_arg=`echo ${plugins[@]}`

    ARRAYS_contains "$plugins_as_arg" "$plugin"
    if [ "$?" -eq 0 ]; then
      plugin_idx=${#plugins[@]}
      plugins[$plugin_idx]=$plugin
      plugins_count[$plugin_idx]=$plugin_count
    else
      plugin_idx=$ARRAYS_index_of_item
      plugins_count[$plugin_idx]=$((${plugins_count[$plugin_idx]} + $plugin_count))
    fi
  fi
done < "$DETAILS"

write_month_stats

exit 0

