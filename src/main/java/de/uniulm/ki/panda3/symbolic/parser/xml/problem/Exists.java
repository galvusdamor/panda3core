//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.06.24 um 05:10:02 PM CEST 
//


package de.uniulm.ki.panda3.symbolic.parser.xml.problem;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "variableDeclaration",
        "atomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExists"
})
@XmlRootElement(name = "exists")
public class Exists {

    @XmlElement(required = true)
    protected VariableDeclaration variableDeclaration;
    @XmlElements({
            @XmlElement(name = "atomic", required = true, type = Atomic.class),
            @XmlElement(name = "fact", required = true, type = Fact.class),
            @XmlElement(name = "not", required = true, type = Not.class),
            @XmlElement(name = "and", required = true, type = And.class),
            @XmlElement(name = "or", required = true, type = Or.class),
            @XmlElement(name = "imply", required = true, type = Imply.class),
            @XmlElement(name = "forall", required = true, type = Forall.class),
            @XmlElement(name = "exists", required = true, type = Exists.class)
    })
    protected List<Object> atomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExists;

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
     * Gets the value of the atomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExists property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the atomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExists property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAtomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExists().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Atomic }
     * {@link Fact }
     * {@link Not }
     * {@link And }
     * {@link Or }
     * {@link Imply }
     * {@link Forall }
     * {@link Exists }
     */
    public List<Object> getAtomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExists() {
        if (atomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExists == null) {
            atomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExists = new ArrayList<Object>();
        }
        return this.atomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExists;
    }

}
