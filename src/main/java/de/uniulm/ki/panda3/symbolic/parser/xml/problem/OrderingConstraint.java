// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.symbolic.parser.xml.problem;

import javax.xml.bind.annotation.*;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "orderingConstraint")
public class OrderingConstraint {

    @XmlAttribute(name = "predecessor", required = true)
    @XmlIDREF
    protected Object predecessor;
    @XmlAttribute(name = "successor", required = true)
    @XmlIDREF
    protected Object successor;

    /**
     * Ruft den Wert der predecessor-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Object }
     */
    public Object getPredecessor() {
        return predecessor;
    }

    /**
     * Legt den Wert der predecessor-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setPredecessor(Object value) {
        this.predecessor = value;
    }

    /**
     * Ruft den Wert der successor-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Object }
     */
    public Object getSuccessor() {
        return successor;
    }

    /**
     * Legt den Wert der successor-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setSuccessor(Object value) {
        this.successor = value;
    }

}
