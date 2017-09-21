#!/usr/bin/env bash

java -jar tritandb-cli.jar --query --file="queries/shelburne/cols.sparql"
java -jar tritandb-cli.jar --query --file="queries/shelburne/row.sparql"
java -jar tritandb-cli.jar --query --file="queries/shelburne/aggr.sparql"