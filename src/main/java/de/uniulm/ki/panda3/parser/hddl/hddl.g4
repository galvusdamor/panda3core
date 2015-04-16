grammar hddl;

//
// Version Log:
//
// 15-04-12 DH :
//   - basic version with typing, propositions, conditional effects, hierarchy
//   - pure PDDL rules are taken from Fox and Long's PDDL 2.1 definition
//

hddl_file : domain | problem;

//
// general structure of a domain definition
//
domain : '(' 'define' '(' 'domain' domain_symbol ')'
             require_def?
             type_def?
             predicates_def?
             task_def*
             method_def*
             action_def* ')';

domain_symbol : NAME;

//
// requirement statement
//
require_def : '(' ':requirements' alldefs=require_defs ')';
require_defs : REQUIRE_NAME+;

//
// type-definition
//
type_def : '(' ':types' (NAME+ '-' var_type)+ ')';

//
// predicate definition
//
predicates_def : '(' ':predicates' atomic_formular_skeleton+ ')';
atomic_formular_skeleton : '(' predicate typed_var_list ')';

//
// task definition
//
// - not present in PDDL
// - please be aware that prec and effects are both optional
//
// TODO: define EBNF for TR
//
task_def :
   '(' ':task' task_symbol
      ':parameters' '(' typed_var_list ')'
      (':precondition' gd)?
      (':effect' effect_body)? ')';

task_symbol : NAME;

//
// method definition
//
// - not present in PDDL
// - please be aware that many subtasks and especially prec and effects are optional
//
// TODO: define EBNF for TR
//
method_def :
   '(' ':method' method_symbol
      ':parameters' '(' typed_var_list ')'
      ':task' '(' task_symbol VAR_NAME* ')'
      (':precondition' gd)?
      (':subtasks' subtask_defs)?
      (':ordering' ordering_defs)?
      (':constraints' constraint_defs)? ')';

method_symbol : NAME;

//
// subtasks
//
// - start with an id that is used in ordering definition:
//   (t1 (tname ?v1 ?v2))
//
// TODO: define EBNF for TR
//
subtask_defs : subtask_def | '(' 'and' subtask_def+ ')';
subtask_def : '(' subtask_id '(' task_symbol VAR_NAME+ ')' ')';
subtask_id : NAME;

//
// ordering
//
// - refers the ids given in subtask definition:
//   (t1 < t2)
//
// TODO: define EBNF for TR
// TODO: add "totalorder"-tag as syntactic sugar
//
ordering_defs : ordering_def | '(' 'and' ordering_def+ ')';
ordering_def : '(' subtask_id '<' subtask_id ')';

//
// variable constraits
//
// - enables to codesignate or non-codesignate variables
//   (= ?v1 ?v2))
//   (not (= ?v3 ?v4)))
//
// TODO: define EBNF for TR
// TODO: prefix or infix?
//
constraint_defs : constraint_def | '(' 'and' constraint_def+ ')';
constraint_def : '(' 'not' '(' '=' VAR_NAME VAR_NAME')' ')' | '(' '=' VAR_NAME VAR_NAME ')';

//
// action definition
//
action_def :
   '(' ':action' action_symbol
      ':parameters' '(' typed_var_list ')'
      (':precondition' gd)?
      (':effect' effect_body)? ')';

action_symbol : NAME;

//
// goal description
// - gd ^= goal description and is used in goals and preconditions
//
gd : '(' ')' | atomic_formular | '(' ('and'|'or') gd+ ')'| '(' 'not' gd ')';

//
// effects
//
// - enables forall and when statements
//
effect_body : '(' ')' | c_effect | '(' 'and' c_effect+ ')';

c_effect : p_effect
           | '(' 'forall' '(' VAR_NAME* ')' effect_body ')'
           | '(' 'when' gd cond_effect ')';

p_effect : '(' 'not' atomic_formular ')' | atomic_formular;
cond_effect : p_effect | '(' 'and' p_effect+ ')';

//
// basic definitions
//

// predicates
atomic_formular : '('predicate VAR_NAME*')';
predicate : NAME;

// list of typed variables
typed_var_list : typed_vars*;

// one or more variable names, followed by a type
typed_vars : (VAR_NAME)+ '-' var_type;
var_type : NAME;

// "require"-statements start with a ":"-symbol
REQUIRE_NAME : ':'NAME;

// names of variables with a "?"
VAR_NAME : '?'NAME;

// basic name definition
NAME : [a-zA-Z][a-zA-Z0-9\-_]* ;
WS : [ \t\r\n]+ -> skip ;

//
//*********************************************************************************
//
// Problem Definition
//
problem: '';
// TODO: define problem