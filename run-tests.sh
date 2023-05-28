#!/bin/bash
#
# E.g. run the following in your jamm directory to kick off testing (for more info on EXECUTION_ID check the README toolchains section)
# sudo EXECUTION_ID=test ./run-tests.sh
# 
# Prerequisites
[ "x$EXECUTION_ID" != "x" ] || { echo "Variable EXECUTION_ID must be defined";  exit 1; }
mvn ${EXECUTION_ID}

