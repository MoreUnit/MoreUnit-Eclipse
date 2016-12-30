#!/bin/bash

if [ $# -ne 1 ]; then
  echo "Usage: extract-stats.sh CONFIGURATION_FILE"
  exit 1
fi

load_configuration () {
  local CONFIGURATION=$1

  if [ ! -e $CONFIGURATION ]; then
    echo "File does not exist: $CONFIGURATION"
    exit 1
  fi

  if [ ! -f $CONFIGURATION -o ! -r $CONFIGURATION -o ! -x $CONFIGURATION ]; then
    echo "File is not readable: $CONFIGURATION"
    exit 1
  fi

  . $CONFIGURATION
}

copy_logs () {
  rm -rf "$WORKING_DIR"
  mkdir "$WORKING_DIR"
  cp "$LOGS_DIR"/* "$WORKING_DIR/"

  gunzip "$WORKING_DIR"/*.gz
}

read_stats () {
  ./"$SCRIPT_DIR/read-stats.sh" "$WORKING_DIR" "$OUTPUT_DETAILS"
}

compile_stats () {
  ./"$SCRIPT_DIR/compile-stats.sh" "$OUTPUT_DETAILS" "$OUTPUT"
}


load_configuration $1

export PROGRAM_DIR=`dirname $0`
SCRIPT_DIR="$PROGRAM_DIR/script"
WORKING_DIR="$PROGRAM_DIR/workingdir"
RESULT_DIR="$PROGRAM_DIR/results"
LAST_STATS="$RESULT_DIR/last-stats.txt"

echo "Starting stats extraction from logs at $LOGS_DIR"

copy_logs

OUTPUT_PREFIX=`date +"%Y-%m-%d-%H-%M-%S"`
OUTPUT_PREFIX="$RESULT_DIR/$OUTPUT_PREFIX"
OUTPUT_DETAILS="$OUTPUT_PREFIX.details.txt"
OUTPUT="$OUTPUT_PREFIX.txt"

mkdir -p "$RESULT_DIR"

read_stats
compile_stats

ln -f "$OUTPUT" "$LAST_STATS"

echo "Extraction is over. Output file: $OUTPUT. Last statistics are always reachable at $LAST_STATS"

exit 0

