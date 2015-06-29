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
 *         &lt;element ref="{}variableDeclaration" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}taskNode" maxOccurs="unbounded"/>
 *         &lt;element ref="{}orderingConstraint" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{}valueRestriction"/>
 *           &lt;element ref="{}sortRestriction"/>
 *         &lt;/choice>
 *         &lt;element ref="{}causalLink" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="taskSchema" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "documentation",
        "variableDeclaration",
        "taskNode",
        "orderingConstraint",
        "valueRestrictionOrSortRestriction",
        "causalLink"
})
@XmlRootElement(name = "methodDeclaration")
public class MethodDeclaration {

    protected String documentation;
    protected List<VariableDeclaration> variableDeclaration;
    @XmlElement(required = true)
    protected List<TaskNode> taskNode;
    protected List<OrderingConstraint> orderingConstraint;
    @XmlElements({
            @XmlElement(name = "valueRestriction", type = ValueRestriction.class),
            @XmlElement(name = "sortRestriction", type = SortRestriction.class)
    })
    protected List<Object> valueRestrictionOrSortRestriction;
    protected List<CausalLink> causalLink;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String name;
    @XmlAttribute(name = "taskSchema", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object taskSchema;

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
     * Gets the value of the taskNode property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the taskNode property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTaskNode().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TaskNode }
     */
    public List<TaskNode> getTaskNode() {
        if (taskNode == null) {
            taskNode = new ArrayList<TaskNode>();
        }
        return this.taskNode;
    }

    /**
     * Gets the value of the orderingConstraint property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the orderingConstraint property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrderingConstraint().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrderingConstraint }
     */
    public List<OrderingConstraint> getOrderingConstraint() {
        if (orderingConstraint == null) {
            orderingConstraint = new ArrayList<OrderingConstraint>();
        }
        return this.orderingConstraint;
    }

    /**
     * Gets the value of the valueRestrictionOrSortRestriction property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the valueRestrictionOrSortRestriction property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValueRestrictionOrSortRestriction().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValueRestriction }
     * {@link SortRestriction }
     */
    public List<Object> getValueRestrictionOrSortRestriction() {
        if (valueRestrictionOrSortRestriction == null) {
            valueRestrictionOrSortRestriction = new ArrayList<Object>();
        }
        return this.valueRestrictionOrSortRestriction;
    }

    /**
     * Gets the value of the causalLink property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the causalLink property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCausalLink().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CausalLink }
     */
    public List<CausalLink> getCausalLink() {
        if (causalLink == null) {
            causalLink = new ArrayList<CausalLink>();
        }
        return this.causalLink;
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
     * Ruft den Wert der taskSchema-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Object }
     */
    public Object getTaskSchema() {
        return taskSchema;
    }

    /**
     * Legt den Wert der taskSchema-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setTaskSchema(Object value) {
        this.taskSchema = value;
    }

}
