#!/bin/sh
mvn package exec:java -Dexec.mainClass=FromTxt -Dexec.args="$1"
