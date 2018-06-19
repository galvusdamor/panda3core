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

package de.uniulm.ki.panda3.symbolic.parser.xml;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java-Klasse für anonymous complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{}variable"/>
 *         &lt;element ref="{}constant"/>
 *       &lt;/choice>
 *       &lt;attribute name="relation" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "variableOrConstant"
})
@XmlRootElement(name = "atomic")
public class Atomic {

    @XmlElements({
            @XmlElement(name = "variable", type = Variable.class),
            @XmlElement(name = "constant", type = Constant.class)
    })
    protected List<Object> variableOrConstant;
    @XmlAttribute(name = "relation", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object relation;

    /**
     * Gets the value of the variableOrConstant property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the variableOrConstant property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVariableOrConstant().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Variable }
     * {@link Constant }
     */
    public List<Object> getVariableOrConstant() {
        if (variableOrConstant == null) {
            variableOrConstant = new ArrayList<Object>();
        }
        return this.variableOrConstant;
    }

    /**
     * Ruft den Wert der relation-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Object }
     */
    public Object getRelation() {
        return relation;
    }

    /**
     * Legt den Wert der relation-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setRelation(Object value) {
        this.relation = value;
    }

}
