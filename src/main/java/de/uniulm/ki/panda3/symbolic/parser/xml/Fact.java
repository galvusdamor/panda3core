//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.06.23 um 06:51:34 PM CEST 
//


package de.uniulm.ki.panda3.symbolic.parser.xml;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "constant"
})
@XmlRootElement(name = "fact")
public class Fact {

    @XmlAttribute(name = "relation", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String relation;
    protected List<Constant> constant;

    /**
     * Ruft den Wert der relation-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRelation() {
        return relation;
    }

    /**
     * Legt den Wert der relation-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRelation(String value) {
        this.relation = value;
    }

    /**
     * Gets the value of the constant property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the constant property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConstant().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Constant }
     */
    public List<Constant> getConstant() {
        if (constant == null) {
            constant = new ArrayList<Constant>();
        }
        return this.constant;
    }

}
