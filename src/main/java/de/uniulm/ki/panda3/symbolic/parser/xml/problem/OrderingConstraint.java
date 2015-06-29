//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.06.24 um 05:10:02 PM CEST 
//


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
