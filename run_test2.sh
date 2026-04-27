#!/bin/bash
xvfb-run mvn -f org.moreunit.build/pom.xml test -pl org.moreunit.plugins:org.moreunit.mock.test
