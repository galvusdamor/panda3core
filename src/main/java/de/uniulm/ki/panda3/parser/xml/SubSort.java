//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.04.09 um 10:15:46 AM CEST 
//


package de.uniulm.ki.panda3.parser.xml;

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
 *       &lt;attribute name="sort" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "subSort")
public class SubSort {

    @XmlAttribute(name = "sort", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object sort;

    /**
     * Ruft den Wert der sort-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Object }
     */
    public Object getSort() {
        return sort;
    }

    /**
     * Legt den Wert der sort-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setSort(Object value) {
        this.sort = value;
    }

}
