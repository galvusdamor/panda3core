#!/bin/bash

git pull

cat << 'EOF' > /tmp/cleancommit
#!/bin/bash

find . -name "*.pddl" -type f -delete
find . -name "*.xml" -type f -delete
find . -name "*.hddp" -type f -delete
find . -name "*.in" -type f -delete
find . -name "*.ans" -type f -delete
find . -name "*.lisp" -type f -delete
find . -name "*.tex" -type f -delete
find . -name "*.hpddl" -type f -delete
find . -name "*antlr*Parser.java" -type f -delete
find . -name "*antlr*Lexer.java" -type f -delete
find . -name "*hddlParser.java" -type f -delete
find . -name "*hddlLexer.java" -type f -delete
find . -name "*.tokens" -type f -delete
find . -path "*/java/*/xml/*" -type f -delete

EOF

chmod +x /tmp/cleancommit

# remove all the files from previously created list in current repository
git filter-branch -f --tree-filter '/tmp/cleancommit' HEAD

rm /tmp/cleancommit

# now execute your gitstats command (should be fixed for your needs)
gitstats .git stats

# reset all the changes filter-branch have done
git reset --hard refs/original/heads/master
