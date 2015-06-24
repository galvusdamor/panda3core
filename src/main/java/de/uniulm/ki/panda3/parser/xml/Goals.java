//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.06.23 um 06:51:34 PM CEST 
//


package de.uniulm.ki.panda3.parser.xml;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "documentation",
        "atomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExistsOrPreference"
})
@XmlRootElement(name = "goals")
public class Goals {

    protected String documentation;
    @XmlElements({
            @XmlElement(name = "atomic", type = Atomic.class),
            @XmlElement(name = "fact", type = Fact.class),
            @XmlElement(name = "not", type = Not.class),
            @XmlElement(name = "and", type = And.class),
            @XmlElement(name = "or", type = Or.class),
            @XmlElement(name = "imply", type = Imply.class),
            @XmlElement(name = "forall", type = Forall.class),
            @XmlElement(name = "exists", type = Exists.class),
            @XmlElement(name = "preference", type = Preference.class)
    })
    protected List<Object> atomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExistsOrPreference;

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
     * Gets the value of the atomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExistsOrPreference property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the atomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExistsOrPreference property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAtomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExistsOrPreference().add(newItem);
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
     * {@link Preference }
     */
    public List<Object> getAtomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExistsOrPreference() {
        if (atomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExistsOrPreference == null) {
            atomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExistsOrPreference = new ArrayList<Object>();
        }
        return this.atomicOrFactOrNotOrAndOrOrOrImplyOrForallOrExistsOrPreference;
    }

}
