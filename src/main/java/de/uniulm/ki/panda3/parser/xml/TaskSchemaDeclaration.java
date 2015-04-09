//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.04.09 um 10:15:46 AM CEST 
//


package de.uniulm.ki.panda3.parser.xml;

import javax.xml.bind.JAXBElement;
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
 *         &lt;choice>
 *           &lt;element ref="{}atomic"/>
 *           &lt;element ref="{}not"/>
 *           &lt;element ref="{}and"/>
 *           &lt;element ref="{}or"/>
 *           &lt;element ref="{}imply"/>
 *           &lt;element ref="{}forall"/>
 *           &lt;element ref="{}exists"/>
 *           &lt;element ref="{}true"/>
 *         &lt;/choice>
 *         &lt;choice>
 *           &lt;element ref="{}atomic"/>
 *           &lt;element ref="{}not"/>
 *           &lt;element ref="{}and"/>
 *           &lt;element ref="{}or"/>
 *           &lt;element ref="{}imply"/>
 *           &lt;element ref="{}forall"/>
 *           &lt;element ref="{}exists"/>
 *           &lt;element ref="{}true"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="primitive"/>
 *             &lt;enumeration value="complex"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="cost" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" default="1" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "content"
})
@XmlRootElement(name = "taskSchemaDeclaration")
public class TaskSchemaDeclaration {

    @XmlElementRefs({
            @XmlElementRef(name = "atomic", type = Atomic.class, required = false),
            @XmlElementRef(name = "not", type = Not.class, required = false),
            @XmlElementRef(name = "variableDeclaration", type = VariableDeclaration.class, required = false),
            @XmlElementRef(name = "documentation", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "exists", type = Exists.class, required = false),
            @XmlElementRef(name = "imply", type = Imply.class, required = false),
            @XmlElementRef(name = "true", type = True.class, required = false),
            @XmlElementRef(name = "and", type = And.class, required = false),
            @XmlElementRef(name = "forall", type = Forall.class, required = false),
            @XmlElementRef(name = "or", type = Or.class, required = false)
    })
    protected List<Object> content;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String name;
    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;
    @XmlAttribute(name = "cost")
    @XmlSchemaType(name = "anySimpleType")
    protected String cost;

    /**
     * Ruft das restliche Contentmodell ab.
     * <p>
     * <p>
     * Sie rufen diese "catch-all"-Eigenschaft aus folgendem Grund ab:
     * Der Feldname "Atomic" wird von zwei verschiedenen Teilen eines Schemas verwendet. Siehe:
     * Zeile 268 von file:/home/gregor/Workspace/Panda2/domain-2.0.xsd
     * Zeile 258 von file:/home/gregor/Workspace/Panda2/domain-2.0.xsd
     * <p>
     * Um diese Eigenschaft zu entfernen, wenden Sie eine Eigenschaftenanpassung für eine
     * der beiden folgenden Deklarationen an, um deren Namen zu ändern:
     * Gets the value of the content property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Atomic }
     * {@link Not }
     * {@link VariableDeclaration }
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link Exists }
     * {@link Imply }
     * {@link True }
     * {@link And }
     * {@link Forall }
     * {@link Or }
     */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();
        }
        return this.content;
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

    /**
     * Ruft den Wert der cost-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCost() {
        if (cost == null) {
            return "1";
        } else {
            return cost;
        }
    }

    /**
     * Legt den Wert der cost-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCost(String value) {
        this.cost = value;
    }

}
