#!/bin/bash
export DISPLAY=:99
Xvfb :99 -screen 0 1024x768x24 > /dev/null 2>&1 &
XVFB_PID=$!
sleep 2

mvn -f org.moreunit.build/pom.xml test -pl :org.moreunit.test -Dtest=MissingClassTreeContentProviderTest
mvn -f org.moreunit.build/pom.xml install -pl :org.moreunit.report -Pcoverage

kill $XVFB_PID
