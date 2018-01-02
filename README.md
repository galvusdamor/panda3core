# PANDA3 Core

PANDA3 is a domain-independent planning system. It is capable of handling both classical and hierarchical planning problems.

## License

Copyright (C) 2014-2017 Gregor Behnke (gregor.behnke@uni-ulm.de)  
Copyright (C) 2014 Thomas Geier  
Copyright (C) 2015 Kadir Dede  
Copyright (C) 2015-2017 Daniel Höller (daniel.hoeller@uni-ulm.de)  
Copyright (C) 2016 Kristof Mickeleit  
Copyright (C) 2016 Matthias Englert  


it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.


You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

## Building panda
To obtain an executable jar of PANDA, you need an installation of the simple build tool (version 0.13.9 or higher).
If you have, please run

    sbt main/assembly

in a command line.
The second-last line of the commands output will tell you where sbt has put the jar file.
