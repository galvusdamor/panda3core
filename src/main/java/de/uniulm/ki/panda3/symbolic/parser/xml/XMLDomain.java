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
 *         &lt;element ref="{}sortDeclaration" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}constantDeclaration" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}relationDeclaration" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}decompositionAxiom" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}taskSchemaDeclaration" maxOccurs="unbounded"/>
 *         &lt;element ref="{}methodDeclaration" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="non-hierarchical"/>
 *             &lt;enumeration value="pure-hierarchical"/>
 *             &lt;enumeration value="hybrid"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "documentation",
        "sortDeclaration",
        "constantDeclaration",
        "relationDeclaration",
        "decompositionAxiom",
        "taskSchemaDeclaration",
        "methodDeclaration"
})
@XmlRootElement(name = "domain")
public class XMLDomain {

    protected String documentation;
    protected List<SortDeclaration> sortDeclaration;
    protected List<ConstantDeclaration> constantDeclaration;
    protected List<RelationDeclaration> relationDeclaration;
    protected List<DecompositionAxiom> decompositionAxiom;
    @XmlElement(required = true)
    protected List<TaskSchemaDeclaration> taskSchemaDeclaration;
    protected List<MethodDeclaration> methodDeclaration;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String name;
    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;

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
     * Gets the value of the sortDeclaration property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sortDeclaration property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSortDeclaration().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SortDeclaration }
     */
    public List<SortDeclaration> getSortDeclaration() {
        if (sortDeclaration == null) {
            sortDeclaration = new ArrayList<SortDeclaration>();
        }
        return this.sortDeclaration;
    }

    /**
     * Gets the value of the constantDeclaration property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the constantDeclaration property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConstantDeclaration().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConstantDeclaration }
     */
    public List<ConstantDeclaration> getConstantDeclaration() {
        if (constantDeclaration == null) {
            constantDeclaration = new ArrayList<ConstantDeclaration>();
        }
        return this.constantDeclaration;
    }

    /**
     * Gets the value of the relationDeclaration property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relationDeclaration property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelationDeclaration().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RelationDeclaration }
     */
    public List<RelationDeclaration> getRelationDeclaration() {
        if (relationDeclaration == null) {
            relationDeclaration = new ArrayList<RelationDeclaration>();
        }
        return this.relationDeclaration;
    }

    /**
     * Gets the value of the decompositionAxiom property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the decompositionAxiom property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDecompositionAxiom().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DecompositionAxiom }
     */
    public List<DecompositionAxiom> getDecompositionAxiom() {
        if (decompositionAxiom == null) {
            decompositionAxiom = new ArrayList<DecompositionAxiom>();
        }
        return this.decompositionAxiom;
    }

    /**
     * Gets the value of the taskSchemaDeclaration property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the taskSchemaDeclaration property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTaskSchemaDeclaration().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TaskSchemaDeclaration }
     */
    public List<TaskSchemaDeclaration> getTaskSchemaDeclaration() {
        if (taskSchemaDeclaration == null) {
            taskSchemaDeclaration = new ArrayList<TaskSchemaDeclaration>();
        }
        return this.taskSchemaDeclaration;
    }

    /**
     * Gets the value of the methodDeclaration property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the methodDeclaration property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMethodDeclaration().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MethodDeclaration }
     */
    public List<MethodDeclaration> getMethodDeclaration() {
        if (methodDeclaration == null) {
            methodDeclaration = new ArrayList<MethodDeclaration>();
        }
        return this.methodDeclaration;
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

}
