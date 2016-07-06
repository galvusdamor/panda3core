#include <list>
#include <stack>
#include <queue>
#include <set>
#include <map>
#include <algorithm>
#include <iostream>
#include <sstream>
#include <cstdio>
#include <cstdlib>
#include <cmath>
#include <cstring>
#include <cfloat>
#include <climits>
#include <numeric>
#include <iomanip>

using namespace std;

const int oo = 0x3f3f3f3f;
const double eps = 1e-9;
const double PI = 2.0 * acos(0.0);


typedef long long ll;
typedef unsigned long long ull;
typedef pair<int, int> pii;
typedef vector<int> vi;
typedef vector<string> vs;

#define sz(c) int((c).size())
#define all(c) (c).begin(), (c).end()
#define FOR(i,a,b) for (int i = (a); i < (b); i++)
#define FORS(i,a,b,s) for (int i = (a); i < (b); i=i+(s))
#define FORD(i,a,b) for (int i = int(b)-1; i >= (a); i--)
#define FORIT(i,c) for (__typeof__((c).begin()) i = (c).begin(); i != (c).end(); i++)

struct state { set<string> pos,neg; };
struct action {
	string name;
	state pre,eff;
};

bool operator<(const action& lhs, const action& rhs) {return lhs.name < rhs.name;}

vector<action> actions;
state init;
state goal;


vector<string> split(string s, char separator){
	vector<string> list;
	size_t pos = 0;
	while ((pos = s.find(separator)) != string::npos) {
		list.push_back(s.substr(0, pos));
		s.erase(0, pos + 1);
	}
	list.push_back(s);
	
	//cout << "LIST" << endl;
	//FOR(i,0,sz(list)) cout << list[i] << endl;
	return list;
}


set<string> stringToLiterals(string s){
	vector<string> lits = split(s,',');
	set<string> ret;
	ret.insert(lits.begin(),lits.end());
	ret.erase("");
	return ret;
}

state stringToState(string s){
	vector<string> parts = split(s,';');
	state ret;
	ret.pos = stringToLiterals(parts[0]);
	ret.neg = stringToLiterals(parts[1]);
	return ret;
}

action stringToAction(string s){
	vector<string> parts = split(s,';');
	action ret;
	ret.name = parts[0];
	ret.pre.pos = stringToLiterals(parts[1]);
	ret.pre.neg = stringToLiterals(parts[2]);
	ret.eff.pos = stringToLiterals(parts[3]);
	ret.eff.neg = stringToLiterals(parts[4]);
	return ret;	
}


void readDomain(){
	string _line;
	// initial state
	getline(cin,_line);
	init = stringToState(_line);
	// goal state
	getline(cin,_line);
	goal = stringToState(_line);

	// actions
	while (1){
		getline(cin,_line);
		if (!sz(_line)) return;
		actions.push_back(stringToAction(_line));
	}
}


bool stateAchieved(state g, state s){
	FORIT(i,g.pos) if (!s.pos.count(*i)) return false;
	FORIT(i,g.neg) if (!s.neg.count(*i)) return false;
	return true;
}


bool applicable(action a, state s) {
	return stateAchieved(a.pre,s);
}



// planning graph
vector<state> sLayer;
vector<vector<action> > aLayer;

int buildGraph(){
	sLayer.push_back(init);
	sLayer[0].neg.clear();
	map<pair<string,string>,bool> sMutex;
	map<pair<action,action>,bool> aMutex;
	
	sMutex.clear();
	aMutex.clear();

	int lastState = sz(init.pos);
	int lastStateMutex = 0;
	int lastAction = 0;
	int lastActionMutex = 0;

	while (true){
		//cout << "LAYER " << sz(sLayer) << endl;
		//cout << "======" << endl;
		state oldState = sLayer[sz(sLayer)-1];

		// abort check
		bool allgoal = stateAchieved(goal,oldState);
		FORIT(g1,goal.pos) FORIT(g2,goal.pos) if (sMutex.count(make_pair(*g1,*g2))) allgoal = false;
		//if (allgoal) return sz(sLayer)-1;
		//if (sz(sLayer) == 4) return 0;

		// 1 step build new action layer (ignore keep-actions)
		vector<action> appli;
		FOR(a,0,sz(actions)) if (applicable(actions[a], oldState)){
			bool mutex = false;
			FORIT(p1,actions[a].pre.pos) FORIT(p2,actions[a].pre.pos) if (sMutex.count(make_pair(*p1,*p2))) mutex=true;
			if (!mutex)	{
			    appli.push_back(actions[a]);
			    //cout << actions[a].name << " ";
			}
		}
		//cout << endl;
		aLayer.push_back(appli);
		
		// compute action mutexes
		//cout << "== AMUTEX " << endl;
		aMutex.clear();
		FOR(a1i,0,sz(appli)) FOR(a2i,0,a1i) {
		    action a1 = appli[a1i];
		    action a2 = appli[a2i];
			// if they have mutex precs
			bool mutex = false;
			FORIT(p1,a1.pre.pos) FORIT(p2,a2.pre.pos) if (sMutex.count(make_pair(*p1,*p2))) mutex=true;
			FORIT(d1,a1.eff.neg) if (a2.pre.pos.count(*d1) + a2.eff.pos.count(*d1)) mutex=true;
			FORIT(d2,a2.eff.neg) if (a1.pre.pos.count(*d2) + a1.eff.pos.count(*d2)) mutex=true;
			
			//cout << a1.name << " " << a2.name << " " << mutex << endl;
			if (mutex){
			    aMutex[make_pair(a1,a2)] = true, aMutex[make_pair(a2,a1)] = true;
			    //cout << "A-Mutex: " << a1.name << " " << a2.name << endl;
			}
		}
		//cout << "END AMUTEX" << endl;
		
		// 2 step state layer
		state st;
		FORIT (i,oldState.pos) st.pos.insert(*i);
		FOR (a,0,sz(appli)) FORIT(i,appli[a].eff.pos) st.pos.insert(*i);
		
		//cout << "======" << endl;
		//FORIT(l,st.pos) cout << *l << endl;
		
		// compute state mutexes
		map<pair<string,string>,bool> osm = sMutex;
		int noopMutexCount = 0;
		sMutex.clear();
		FORIT(l1,st.pos) FORIT(l2,st.pos){
			//cout << "Can " << *l1 << " " << *l2 << endl;
			// possible producers
			bool nonMutexProducer = false;
			FOR(p1,0,sz(appli)+1) FOR(p2,0,sz(appli)+1) {
				if (p1 == sz(appli) && !oldState.pos.count(*l1)) continue;
				if (p2 == sz(appli) && !oldState.pos.count(*l2)) continue;

				if (p1 == sz(appli) && p2 == sz(appli)) {
					if (!osm.count(make_pair(*l1,*l2))) nonMutexProducer = true;
					else {
					    noopMutexCount++;
					    //cout << "A-Mutex: noop-" << *l1 << " noop-" << *l2 << endl;
					}
					continue;
				}

				
				if (p1 == sz(appli) || p2 == sz(appli)){
					action ac = p1 == sz(appli) ? appli[p2] : appli[p1];
					string litEffect = p1 == sz(appli) ? *l2 : *l1;
				    //cout << "TEST " << ac.name << " with " << litEffect;
					// check whether the action produces the literal
					if (ac.eff.neg.count(litEffect) && l1 == l2) {
					    noopMutexCount++;
					    //cout << "A-Mutex1: " << ac.name << " noop-" << litEffect << " "  << *l1 << " " << *l2 << endl;
					    continue;
					}

					if (!(ac.eff.pos.count(litEffect))){
					    // can be a noop mutex
					    continue;
					}

					string litKeep = p1 == sz(appli) ? *l1 : *l2;
					if (ac.eff.neg.count(litKeep)) continue;
					bool counter = false;
					FORIT(prec,ac.pre.pos) if (osm.count(make_pair(litKeep,*prec))) counter = true;
					if (counter) {
					    // only count the first time
					    if (*ac.eff.pos.begin()== litEffect) {
					        noopMutexCount++;
					        //cout << "A-Mutex2: " << ac.name << " noop-" << litKeep << " "  << *l1 << " " << *l2 << endl;
					    }
					    continue;
					}
					
					//cout << "Keep " << ac.name << " " << litKeep;
					//FORIT(prec,ac.pre.pos) cout << *prec << ":" << osm.count(make_pair(litKeep,*prec)) << " ";
					//cout << endl;
					nonMutexProducer = true;
				} else {
					if (!appli[p1].eff.pos.count(*l1)) continue;
					if (!appli[p2].eff.pos.count(*l2)) continue;
				
					// two real actions
					if (!aMutex.count(make_pair(appli[p1],appli[p2]))){
						nonMutexProducer = true;
						//cout << "do " << appli[p1].name << " " << appli[p2].name << endl;
					}
				}
			}
			//cout << "MUTEX " << *l1 << " " << *l2 << " " << !nonMutexProducer << endl;
			if (!nonMutexProducer) {
				sMutex[make_pair(*l1,*l2)] = sMutex[make_pair(*l2,*l1)] = true;
				//if (*l1 < *l2) cout << "S-Mutex: " << *l1 << " " << *l2 << endl;
			}
		}

		if (lastState == sz(st.pos) && lastAction == sz(appli)+ sz(oldState.pos) && lastStateMutex == sz(sMutex) && lastActionMutex == sz(aMutex) + noopMutexCount) return sz(sLayer);
		lastState = sz(st.pos);
		lastAction = sz(appli) + sz(oldState.pos);
		lastStateMutex = sz(sMutex);
		lastActionMutex = sz(aMutex) + noopMutexCount;
		cout << lastAction  << " " << lastActionMutex/2 << " " << lastState << " " << lastStateMutex << endl;

		sLayer.push_back(st);
	}
}


int main(){
	readDomain();

	buildGraph();
}
