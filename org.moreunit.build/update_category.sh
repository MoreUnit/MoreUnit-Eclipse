#!/bin/bash

#################################################################################################
#
# Author: Vera
#
# This script has several parameters on the commandline:
# $1: property main_number from build.xml
# $2: property major_number from build.xml
# $3: property minor_number from build.xml
# $4: path to category.xml in org.moreunit.feature
# $5: feature-id (org.moreunit|org.moreunit.mock)
#
#################################################################################################

VERSION_REGEX="$5_$1\.$2\."

# check, if this main_number-major_number combination is already in the category.xml
GREP_RESULT=`egrep -o "${VERSION_REGEX}" $4`

echo ${GREP_RESULT}

if [ ${#GREP_RESULT} == 0 ]; then
	echo "This version is not part of the category.xml and will be added"
	
	# String of what should be added to category.xml
	NEW_VERSION="<feature url=\"features/$5_$1.$2.$3.jar\" id=\"$5\" version=\"$1.$2.$3\">\\
		<category name=\"moreunit.org\"/>\\
	</feature>\\
	<!-- new release -->"
	
	# add new version to category.xml
	# because of replacement contains slash characters we use the #-character for the sed-command
	sed -i "" "s#<!-- new release -->#${NEW_VERSION}#g" $4
else
	echo "This version is already part of the category.xml and will be updated to the new minor_number"
	sed -i "" "s/$1\.$2\.[0-9]/$1.$2.$3/g" $4
fi
