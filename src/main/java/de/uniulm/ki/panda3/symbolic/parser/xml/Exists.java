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


/**
 * <p>Java-Klasse für anonymous complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}variableDeclaration"/>
 *         &lt;choice>
 *           &lt;element ref="{}atomic"/>
 *           &lt;element ref="{}not"/>
 *           &lt;element ref="{}and"/>
 *           &lt;element ref="{}or"/>
 *           &lt;element ref="{}imply"/>
 *           &lt;element ref="{}forall"/>
 *           &lt;element ref="{}exists"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "variableDeclaration",
        "atomic",
        "not",
        "and",
        "or",
        "imply",
        "forall",
        "exists"
})
@XmlRootElement(name = "exists")
public class Exists {

    @XmlElement(required = true)
    protected VariableDeclaration variableDeclaration;
    protected Atomic atomic;
    protected Not not;
    protected And and;
    protected Or or;
    protected Imply imply;
    protected Forall forall;
    protected Exists exists;

    /**
     * Ruft den Wert der variableDeclaration-Eigenschaft ab.
     *
     * @return possible object is
     * {@link VariableDeclaration }
     */
    public VariableDeclaration getVariableDeclaration() {
        return variableDeclaration;
    }

    /**
     * Legt den Wert der variableDeclaration-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link VariableDeclaration }
     */
    public void setVariableDeclaration(VariableDeclaration value) {
        this.variableDeclaration = value;
    }

    /**
     * Ruft den Wert der atomic-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Atomic }
     */
    public Atomic getAtomic() {
        return atomic;
    }

    /**
     * Legt den Wert der atomic-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Atomic }
     */
    public void setAtomic(Atomic value) {
        this.atomic = value;
    }

    /**
     * Ruft den Wert der not-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Not }
     */
    public Not getNot() {
        return not;
    }

    /**
     * Legt den Wert der not-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Not }
     */
    public void setNot(Not value) {
        this.not = value;
    }

    /**
     * Ruft den Wert der and-Eigenschaft ab.
     *
     * @return possible object is
     * {@link And }
     */
    public And getAnd() {
        return and;
    }

    /**
     * Legt den Wert der and-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link And }
     */
    public void setAnd(And value) {
        this.and = value;
    }

    /**
     * Ruft den Wert der or-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Or }
     */
    public Or getOr() {
        return or;
    }

    /**
     * Legt den Wert der or-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Or }
     */
    public void setOr(Or value) {
        this.or = value;
    }

    /**
     * Ruft den Wert der imply-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Imply }
     */
    public Imply getImply() {
        return imply;
    }

    /**
     * Legt den Wert der imply-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Imply }
     */
    public void setImply(Imply value) {
        this.imply = value;
    }

    /**
     * Ruft den Wert der forall-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Forall }
     */
    public Forall getForall() {
        return forall;
    }

    /**
     * Legt den Wert der forall-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Forall }
     */
    public void setForall(Forall value) {
        this.forall = value;
    }

    /**
     * Ruft den Wert der exists-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Exists }
     */
    public Exists getExists() {
        return exists;
    }

    /**
     * Legt den Wert der exists-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Exists }
     */
    public void setExists(Exists value) {
        this.exists = value;
    }

}
