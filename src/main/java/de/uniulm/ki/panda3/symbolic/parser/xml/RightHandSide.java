//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.04.09 um 10:15:46 AM CEST 
//


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
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{}atomic"/>
 *         &lt;element ref="{}not"/>
 *         &lt;element ref="{}and"/>
 *         &lt;element ref="{}or"/>
 *         &lt;element ref="{}imply"/>
 *         &lt;element ref="{}forall"/>
 *         &lt;element ref="{}exists"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "atomicOrNotOrAnd"
})
@XmlRootElement(name = "rightHandSide")
public class RightHandSide {

    @XmlElements({
            @XmlElement(name = "atomic", type = Atomic.class),
            @XmlElement(name = "not", type = Not.class),
            @XmlElement(name = "and", type = And.class),
            @XmlElement(name = "or", type = Or.class),
            @XmlElement(name = "imply", type = Imply.class),
            @XmlElement(name = "forall", type = Forall.class),
            @XmlElement(name = "exists", type = Exists.class)
    })
    protected List<Object> atomicOrNotOrAnd;

    /**
     * Gets the value of the atomicOrNotOrAnd property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the atomicOrNotOrAnd property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAtomicOrNotOrAnd().add(newItem);
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
    public List<Object> getAtomicOrNotOrAnd() {
        if (atomicOrNotOrAnd == null) {
            atomicOrNotOrAnd = new ArrayList<Object>();
        }
        return this.atomicOrNotOrAnd;
    }

}
