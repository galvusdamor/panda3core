grammar antlrHDDL;

//
// Version Log:
//
// 15-04-12 DH :
//   - basic version with typing, propositions, conditional effects, hierarchy
//   - pure PDDL rules are taken from Fox and Long's PDDL 2.1 definition
//
// MISSING FEATURES:
//   - decomposition axioms

hddl_file : domain | problem;

//
// general structure of a domain definition
//
domain : '(' 'define' '(' 'domain' domain_symbol ')'
             require_def?
             type_def?
             const_def?
             predicates_def?
             funtions_def?
             comp_task_def*
             method_def*
             action_def* ')';

domain_symbol : NAME;

//
// requirement statement
//
require_def : '(' ':requirements' require_defs ')';
require_defs : REQUIRE_NAME+;

//
// type-definition
//
type_def : '(' ':types' one_def+ ')';
one_def : new_types ('-' var_type)?;
new_types : NAME+;

//
// domain constant definition
//
const_def : '(' ':constants' typed_obj_list ')';

//
// predicate definition
//
funtions_def : '(' ':functions' ( atomic_formula_skeleton ('-' 'number' | var_type )?)+')';
predicates_def : '(' ':predicates' atomic_formula_skeleton+ ')';
atomic_formula_skeleton : '(' predicate typed_var_list ')';

//
// task definition
//
// - not present in PDDL
// - please be aware that prec and effects are both optional
//
// TODO: define EBNF for TR
//
comp_task_def :
   '(' ':task' task_def;

task_def : task_symbol
      ':parameters' '(' typed_var_list ')'
      (':precondition' gd)?
      (':effect' effect_body)? ')';

task_symbol : NAME;

//
// method definition
//
// - not present in PDDL
// - please be aware that subtasks and especially prec and effects are optional
//
// TODO: define EBNF for TR
//
method_def :
   '(' ':method' method_symbol
      ':parameters' '(' typed_var_list ')'
      ':task' '(' task_symbol var_or_const* ')'
      (':precondition' gd)?
      tasknetwork_def;

tasknetwork_def :
      ((':subtasks' | ':tasks' | ':ordered-subtasks'| ':ordered-tasks') subtask_defs)?
      ((':ordering' | ':order') ordering_defs)?
      (':constraints' constraint_defs)?
      ((':causal-links' | ':causallinks') causallink_defs)?
      ')';

method_symbol : NAME;

//
// subtasks
//
// - start with an id that is used in ordering definition:
//   (t1 (tname ?v1 ?v2))
//
// TODO: define EBNF for TR
//
subtask_defs : '(' ')' | subtask_def | '(' 'and' subtask_def+ ')';
subtask_def : ('(' task_symbol var_or_const* ')' | '(' subtask_id '(' task_symbol var_or_const* ')' ')');
subtask_id : NAME;

//
// ordering
//
// - refers the ids given in subtask definition:
//   (t1 < t2)
//
// TODO: define EBNF for TR
//
ordering_defs : '(' ')' | ordering_def | '(' 'and' ordering_def+ ')';
ordering_def : '(' subtask_id '<' subtask_id ')';

//
// variable constraits
//
// - enables to codesignate or non-codesignate variables
//   (= ?v1 ?v2))
//   (not (= ?v3 ?v4)))
//
// TODO: define EBNF for TR
//
constraint_defs : '(' ')' | constraint_def | '(' 'and' constraint_def+ ')';
constraint_def : '(' ')' | '(' 'not' equallity var_or_const var_or_const')' ')' | equallity var_or_const var_or_const ')';

causallink_defs : '(' ')' | causallink_def | '(' 'and' causallink_def+ ')';
causallink_def : '(' subtask_id literal subtask_id ')';

//
// action definition
//
action_def :
   '(' ':action' task_def;

//
// goal description
// - gd ^= goal description and is used in goals and preconditions
//
gd : gd_empty | atomic_formula | gd_negation | gd_conjuction | gd_disjuction | gd_existential | gd_univeral | gd_equality_constraint;

gd_empty : '(' ')';
gd_conjuction : '(' 'and' gd+ ')';
gd_disjuction : '(' 'or' gd+ ')';
gd_negation : '(' 'not' gd ')';
gd_existential : '(exists' '(' typed_var_list ')' gd ')';
gd_univeral : '(forall' '(' typed_var_list ')' gd ')';
gd_equality_constraint : equallity var_or_const var_or_const ')';
//
// effects
//
// - enables forall and when statements
//
effect_body : eff_empty | c_effect | eff_conjuntion;

eff_conjuntion : '(' 'and' c_effect+ ')';
eff_empty : '(' ')';
c_effect : p_effect | literal | forall_effect | conditional_effect;

forall_effect : '(' 'forall' '(' var_or_const* ')' effect_body ')';
conditional_effect : '(' 'when' gd cond_effect ')';

literal : neg_atomic_formula | atomic_formula;
neg_atomic_formula : '(' 'not' atomic_formula ')';
cond_effect : literal | '(' 'and' literal+ ')';

p_effect : '(' assign_op f_head f_exp ')';

assign_op : 'assign' | 'scale-down' | 'scale-up' | 'increase' | 'decrease';

f_head : func_symbol | '(' func_symbol term* ')';

f_exp : NUMBER | '(' bin_op f_exp f_exp ')' | '(' multi_op f_exp f_exp+ ')' | '(' '-' f_exp ')' | f_head;

bin_op : multi_op | '-' | '/';

multi_op : '+' | '*';

//
// basic definitions
//

// predicates
atomic_formula : '('predicate var_or_const*')';
predicate : NAME;

// special "predicate" for equallity
equallity : '(' '=' | '(=';

// list of typed variables and objects
typed_var_list : typed_vars*;
typed_obj_list : typed_objs*;

// one or more variable names, followed by a type
typed_vars : VAR_NAME+ '-' var_type;
typed_objs : new_consts+ '-' var_type;
new_consts : NAME;
var_type : NAME;

// "require"-statements start with a ":"-symbol
REQUIRE_NAME : ':' NAME;

// names of variables with a "?"
var_or_const : NAME | VAR_NAME;
VAR_NAME : '?'NAME;

// basic name definition
term : NAME | VAR_NAME | functionterm;
functionterm : '(' func_symbol term* ')';
func_symbol : NAME;
NAME : [a-zA-Z][a-zA-Z0-9\-_]* ;
COMMENT : (';' ~[\r\n]* ('\r'|'\n') ('\r'|'\n')? ) -> skip ;
WS : [ \t\r\n]+ -> skip ;
NUMBER : [0-9][0-9]* ;

//
//*********************************************************************************
//
// Problem Definition
//
// TODO: define EBNF for TR

problem : '(' 'define' '(' 'problem' NAME ')'
              '(' ':domain' NAME ')'
              require_def?
              p_object_declaration?
              p_htn?
              p_init
              p_goal?
              metric_spec?
              ')';

p_object_declaration : '(' ':objects' typed_obj_list')';
p_init : '(' ':init' init_el*')';
init_el : literal | num_init;
num_init : equallity f_head NUMBER ')';
p_goal : '(' ':goal' gd ')';

p_htn : '(' (':htn'|':htnti')
        tasknetwork_def;
//      (':tasks' subtask_defs)?
//      (':ordering' ordering_defs)?
//      (':constraints' constraint_defs)? ')';
metric_spec : '(' ':metric' optimization ground_f_exp')';
optimization : 'minimize' | 'maximize';
ground_f_exp : '(' bin_op ground_f_exp ground_f_exp ')'
             | ('(' '-' | '(-') ground_f_exp ')'
             | NUMBER
             | '(' func_symbol NAME* ')'
             | 'total-time'
             | func_symbol;