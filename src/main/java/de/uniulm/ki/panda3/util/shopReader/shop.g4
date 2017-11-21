grammar shop;

domain :
'(defdomain' NAME '('
(operator | method)*
')' ')';

method : '(:method' task ifThen+ ')';

ifThen : formulaList taskList;
taskList : '(' task* ')';
task : '(' ':task'? taskName param+')';
taskName : opName | NAME;

operator : '(:operator' task formulaList formulaList formulaList ')';

formulaList : '(' formula* ')';
formula : posFormula | negFormula;
posFormula : '(' NAME param+ ')';
negFormula : '(' 'not' posFormula ')';

problem : '(defproblem' NAME NAME formulaList '(:unordered' task* ')' ')';

opName : '!'+ NAME;
param : VAR_NAME | NAME;
VAR_NAME : '?' NAME;

NAME : [a-zA-Z][a-zA-Z0-9\-_]* ;
COMMENT : (';' ~[\r\n]* ('\r'|'\n') ('\r'|'\n')? ) -> skip ;
WS : [ \t\r\n]+ -> skip ;
NUMBER : [0-9][0-9]* ;
