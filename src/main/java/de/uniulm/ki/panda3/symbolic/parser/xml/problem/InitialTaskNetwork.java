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
        "documentation",
        "taskNode",
        "orderingConstraint",
        "valueRestrictionOrSortRestriction",
        "causalLink"
})
@XmlRootElement(name = "initialTaskNetwork")
public class InitialTaskNetwork {

    protected String documentation;
    @XmlElement(required = true)
    protected List<TaskNode> taskNode;
    protected List<OrderingConstraint> orderingConstraint;
    @XmlElements({
            @XmlElement(name = "valueRestriction", type = ValueRestriction.class),
            @XmlElement(name = "sortRestriction", type = SortRestriction.class)
    })
    protected List<Object> valueRestrictionOrSortRestriction;
    protected List<CausalLink> causalLink;

    /**
     * Ruft den Wert der documentation-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDocumentation() {
        return documentation;
    }

    /**
     * Legt den Wert der documentation-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDocumentation(String value) {
        this.documentation = value;
    }

    /**
     * Gets the value of the taskNode property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the taskNode property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTaskNode().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TaskNode }
     */
    public List<TaskNode> getTaskNode() {
        if (taskNode == null) {
            taskNode = new ArrayList<TaskNode>();
        }
        return this.taskNode;
    }

    /**
     * Gets the value of the orderingConstraint property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the orderingConstraint property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrderingConstraint().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrderingConstraint }
     */
    public List<OrderingConstraint> getOrderingConstraint() {
        if (orderingConstraint == null) {
            orderingConstraint = new ArrayList<OrderingConstraint>();
        }
        return this.orderingConstraint;
    }

    /**
     * Gets the value of the valueRestrictionOrSortRestriction property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the valueRestrictionOrSortRestriction property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValueRestrictionOrSortRestriction().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValueRestriction }
     * {@link SortRestriction }
     */
    public List<Object> getValueRestrictionOrSortRestriction() {
        if (valueRestrictionOrSortRestriction == null) {
            valueRestrictionOrSortRestriction = new ArrayList<Object>();
        }
        return this.valueRestrictionOrSortRestriction;
    }

    /**
     * Gets the value of the causalLink property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the causalLink property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCausalLink().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CausalLink }
     */
    public List<CausalLink> getCausalLink() {
        if (causalLink == null) {
            causalLink = new ArrayList<CausalLink>();
        }
        return this.causalLink;
    }

}
