//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.06.24 um 05:10:02 PM CEST 
//


package de.uniulm.ki.panda3.symbolic.parser.xml.problem;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "variableOrConstant"
})
@XmlRootElement(name = "valueRestriction")
public class ValueRestriction {

    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;
    @XmlAttribute(name = "variable", required = true)
    @XmlIDREF
    protected Object variable;
    @XmlElements({
            @XmlElement(name = "variable", required = true, type = Variable.class),
            @XmlElement(name = "constant", required = true, type = Constant.class)
    })
    protected List<Object> variableOrConstant;

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
     * Ruft den Wert der variable-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Object }
     */
    public Object getVariable() {
        return variable;
    }

    /**
     * Legt den Wert der variable-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setVariable(Object value) {
        this.variable = value;
    }

    /**
     * Gets the value of the variableOrConstant property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the variableOrConstant property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVariableOrConstant().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Variable }
     * {@link Constant }
     */
    public List<Object> getVariableOrConstant() {
        if (variableOrConstant == null) {
            variableOrConstant = new ArrayList<Object>();
        }
        return this.variableOrConstant;
    }

}
