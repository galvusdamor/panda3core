package de.uniulm.ki.panda3.symbolic.search;

/**
 * @author Gregor Behnke (gregor.behnke@uni-ulm.de)
 */
public enum SearchState {
    SOLUTION,DEADEND_UNRESOLVABLEFLAW,DEADEND_HEURISTIC,DEADEND_CSP,UNSOLVABLE, EXPLORED,INSEARCH;
}
