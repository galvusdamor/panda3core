#!/bin/bash

git clone https://mirkwood.informatik.uni-ulm.de/gitbucket/git/gbehnke/panda3core.git
wget http://repo1.maven.org/maven2/com/madgag/bfg/1.12.13/bfg-1.12.13.jar


java -jar bfg-1.12.13.jar --no-blob-protection --delete-files "*.pddl" panda3core
java -jar bfg-1.12.13.jar --no-blob-protection --delete-files "*.PDDL" panda3core
java -jar bfg-1.12.13.jar --no-blob-protection --delete-files "*.xml" panda3core
java -jar bfg-1.12.13.jar --no-blob-protection --delete-files "*.hddp" panda3core
java -jar bfg-1.12.13.jar --no-blob-protection --delete-files "*.in" panda3core
java -jar bfg-1.12.13.jar --no-blob-protection --delete-files "*.ans" panda3core
java -jar bfg-1.12.13.jar --no-blob-protection --delete-files "*.lisp" panda3core
java -jar bfg-1.12.13.jar --no-blob-protection --delete-files "*.tex" panda3core
java -jar bfg-1.12.13.jar --no-blob-protection --delete-files "*.hpddl" panda3core
java -jar bfg-1.12.13.jar --no-blob-protection --delete-files "*antlr*Parser.java" panda3core
java -jar bfg-1.12.13.jar --no-blob-protection --delete-files "*antlr*Lexer.java" panda3core
java -jar bfg-1.12.13.jar --no-blob-protection --delete-files "*hddlParser.java" panda3core
java -jar bfg-1.12.13.jar --no-blob-protection --delete-files "*hddlLexer.java" panda3core
java -jar bfg-1.12.13.jar --no-blob-protection --delete-files "*.tokens" panda3core
java -jar bfg-1.12.13.jar --no-blob-protection --delete-files "pfile*" panda3core
java -jar bfg-1.12.13.jar --no-blob-protection --delete-files "Domain.java" panda3core


find . -path "*/java/*/xml/*java" -type f -exec sh -c 'echo $(basename {})' \; | sort  | uniq | while read f
do
	java -jar bfg-1.12.13.jar --no-blob-protection --delete-files $f panda3core
done

cd panda3core
git reflog expire --expire=now --all && git gc --prune=now --aggressive
cd ..

# now execute your gitstats command (should be fixed for your needs)
gitstats panda3core stats

rm -rf panda3core bfg-1.12.13.jar

# reset all the changes filter-branch have done
#git reset --hard origin/master