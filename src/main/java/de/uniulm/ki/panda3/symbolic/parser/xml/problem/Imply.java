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
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "atomicOrNotOrAndOrOrOrImplyOrForallOrExists"
})
@XmlRootElement(name = "imply")
public class Imply {

    @XmlElements({
            @XmlElement(name = "atomic", type = Atomic.class),
            @XmlElement(name = "not", type = Not.class),
            @XmlElement(name = "and", type = And.class),
            @XmlElement(name = "or", type = Or.class),
            @XmlElement(name = "imply", type = Imply.class),
            @XmlElement(name = "forall", type = Forall.class),
            @XmlElement(name = "exists", type = Exists.class)
    })
    protected List<Object> atomicOrNotOrAndOrOrOrImplyOrForallOrExists;

    /**
     * Gets the value of the atomicOrNotOrAndOrOrOrImplyOrForallOrExists property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the atomicOrNotOrAndOrOrOrImplyOrForallOrExists property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAtomicOrNotOrAndOrOrOrImplyOrForallOrExists().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Atomic }
     * {@link Not }
     * {@link And }
     * {@link Or }
     * {@link Imply }
     * {@link Forall }
     * {@link Exists }
     */
    public List<Object> getAtomicOrNotOrAndOrOrOrImplyOrForallOrExists() {
        if (atomicOrNotOrAndOrOrOrImplyOrForallOrExists == null) {
            atomicOrNotOrAndOrOrOrImplyOrForallOrExists = new ArrayList<Object>();
        }
        return this.atomicOrNotOrAndOrOrOrImplyOrForallOrExists;
    }

}
