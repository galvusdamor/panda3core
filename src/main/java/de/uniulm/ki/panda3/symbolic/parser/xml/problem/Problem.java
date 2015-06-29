//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.06.24 um 05:10:02 PM CEST 
//


package de.uniulm.ki.panda3.symbolic.parser.xml.problem;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "documentation",
        "constantDeclaration",
        "initialState",
        "goals",
        "initialTaskNetwork"
})
@XmlRootElement(name = "problem")
public class Problem {

    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected String name;
    @XmlAttribute(name = "domain", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String domain;
    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;
    protected String documentation;
    protected List<ConstantDeclaration> constantDeclaration;
    @XmlElement(required = true)
    protected InitialState initialState;
    protected Goals goals;
    protected InitialTaskNetwork initialTaskNetwork;

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Ruft den Wert der domain-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Legt den Wert der domain-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomain(String value) {
        this.domain = value;
    }

    /**
     * Ruft den Wert der type-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Ruft den Wert der documentation-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentation() {
        return documentation;
    }

    /**
     * Legt den Wert der documentation-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentation(String value) {
        this.documentation = value;
    }

    /**
     * Gets the value of the constantDeclaration property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the constantDeclaration property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConstantDeclaration().add(newItem);
     * </pre>
     *
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConstantDeclaration }
     *
     * 
     */
    public List<ConstantDeclaration> getConstantDeclaration() {
        if (constantDeclaration == null) {
            constantDeclaration = new ArrayList<ConstantDeclaration>();
        }
        return this.constantDeclaration;
    }

    /**
     * Ruft den Wert der initialState-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link InitialState }
     *     
     */
    public InitialState getInitialState() {
        return initialState;
    }

    /**
     * Legt den Wert der initialState-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link InitialState }
     *     
     */
    public void setInitialState(InitialState value) {
        this.initialState = value;
    }

    /**
     * Ruft den Wert der goals-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Goals }
     *     
     */
    public Goals getGoals() {
        return goals;
    }

    /**
     * Legt den Wert der goals-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Goals }
     *     
     */
    public void setGoals(Goals value) {
        this.goals = value;
    }

    /**
     * Ruft den Wert der initialTaskNetwork-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link InitialTaskNetwork }
     *     
     */
    public InitialTaskNetwork getInitialTaskNetwork() {
        return initialTaskNetwork;
    }

    /**
     * Legt den Wert der initialTaskNetwork-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link InitialTaskNetwork }
     *     
     */
    public void setInitialTaskNetwork(InitialTaskNetwork value) {
        this.initialTaskNetwork = value;
    }

}
