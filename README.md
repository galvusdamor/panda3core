# PANDA Planning and Acting in a Network Decomposition Architecture

PANDA is a planning architecture consisting of various components for planning
and acting in a hierarchical planning framework.

It contains different planning systems (capable of solving different kinds of 
planning problems, including both hierarchical and non-hierarchical problems),
components used for the verification of plans (is the given plan a solution to
the given planning problem?), as well as more practice-oriented components, such
as for plan explanations (answering questions like "why should I perform this action?"),
or plan repair (finding new solutions if execution errors arise).

At the moment, not all these components are delivered, yet. More precisely, our 
components for plan explanation, plan repair, and our SAT-based HTN planner PANDA-totSAT
are not yet delivered -- but they will be soon.

Call PANDA.jar with -help to get detailed information and instructions.


## License

Copyright (C) 2014-2018 Gregor Behnke (gregor.behnke@uni-ulm.de)  
Copyright (C) 2014 Thomas Geier  
Copyright (C) 2015-2018 Daniel Höller (daniel.hoeller@uni-ulm.de)  
Copyright (C) 2015 Kadir Dede  
Copyright (C) 2016-2018 Kristof Mickeleit  
Copyright (C) 2016 Matthias Englert  
Copyright (C) 2017-2018 Pascal Bercher (pascal.bercher@uni-ulm.de)


it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.


You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.


## Building PANDA

To obtain an executable jar of PANDA, you need an installation of the 
simple build tool (version 0.13.9 or higher). If you have, please run

    sbt main/assembly

in a command line.

The second-last line of the commands output will tell you where sbt has put the jar file.
