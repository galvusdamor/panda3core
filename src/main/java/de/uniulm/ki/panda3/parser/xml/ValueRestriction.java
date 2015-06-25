//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.04.09 um 10:15:46 AM CEST 
//


package de.uniulm.ki.panda3.parser.xml;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für anonymous complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{}variable"/>
 *         &lt;element ref="{}constant"/>
 *       &lt;/choice>
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="eq"/>
 *             &lt;enumeration value="neq"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="variableN" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "variable",
        "constant"
})
@XmlRootElement(name = "valueRestriction")
public class ValueRestriction {

    protected Variable variable;
    protected Constant constant;
    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;
    @XmlAttribute(name = "variable", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object variableN;

    /**
     * Ruft den Wert der variable-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Variable }
     */
    public Variable getVariable() {
        return variable;
    }

    /**
     * Legt den Wert der variable-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Variable }
     */
    public void setVariable(Variable value) {
        this.variable = value;
    }

    /**
     * Ruft den Wert der constant-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Constant }
     */
    public Constant getConstant() {
        return constant;
    }

    /**
     * Legt den Wert der constant-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Constant }
     */
    public void setConstant(Constant value) {
        this.constant = value;
    }

    /**
     * Ruft den Wert der type-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getType() {
        return type;
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Ruft den Wert der variableN-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Object }
     */
    public Object getVariableN() {
        return variableN;
    }

    /**
     * Legt den Wert der variableN-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setVariableN(Object value) {
        this.variableN = value;
    }

}
