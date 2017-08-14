#!/bin/bash
for f in *.txt; do
   java -jar panda3pcpProbGen.jar $f "./benchmarks/domains/d-${f/.txt/.hddl}" "./benchmarks/problems/p-${f/.txt/.hddl}"
done