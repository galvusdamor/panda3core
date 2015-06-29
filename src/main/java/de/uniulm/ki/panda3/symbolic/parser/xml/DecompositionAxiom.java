//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.04.09 um 10:15:46 AM CEST 
//


package de.uniulm.ki.panda3.symbolic.parser.xml;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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
 *       &lt;sequence>
 *         &lt;element ref="{}documentation" minOccurs="0"/>
 *         &lt;element ref="{}variableDeclaration" maxOccurs="unbounded"/>
 *         &lt;element ref="{}leftHandSide"/>
 *         &lt;element ref="{}rightHandSide"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "documentation",
        "variableDeclaration",
        "leftHandSide",
        "rightHandSide"
})
@XmlRootElement(name = "decompositionAxiom")
public class DecompositionAxiom {

    protected String documentation;
    @XmlElement(required = true)
    protected List<VariableDeclaration> variableDeclaration;
    @XmlElement(required = true)
    protected LeftHandSide leftHandSide;
    @XmlElement(required = true)
    protected RightHandSide rightHandSide;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String name;

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
     * Gets the value of the variableDeclaration property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the variableDeclaration property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVariableDeclaration().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VariableDeclaration }
     */
    public List<VariableDeclaration> getVariableDeclaration() {
        if (variableDeclaration == null) {
            variableDeclaration = new ArrayList<VariableDeclaration>();
        }
        return this.variableDeclaration;
    }

    /**
     * Ruft den Wert der leftHandSide-Eigenschaft ab.
     *
     * @return possible object is
     * {@link LeftHandSide }
     */
    public LeftHandSide getLeftHandSide() {
        return leftHandSide;
    }

    /**
     * Legt den Wert der leftHandSide-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link LeftHandSide }
     */
    public void setLeftHandSide(LeftHandSide value) {
        this.leftHandSide = value;
    }

    /**
     * Ruft den Wert der rightHandSide-Eigenschaft ab.
     *
     * @return possible object is
     * {@link RightHandSide }
     */
    public RightHandSide getRightHandSide() {
        return rightHandSide;
    }

    /**
     * Legt den Wert der rightHandSide-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link RightHandSide }
     */
    public void setRightHandSide(RightHandSide value) {
        this.rightHandSide = value;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

}
