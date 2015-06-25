//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.06.24 um 05:10:02 PM CEST 
//


package de.uniulm.ki.panda3.parser.xml.problem;

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
