//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.04.09 um 10:15:46 AM CEST 
//


package de.uniulm.ki.panda3.symbolic.parser.xml;

import javax.xml.bind.annotation.*;
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
 *         &lt;choice>
 *           &lt;element ref="{}atomic"/>
 *           &lt;element ref="{}not"/>
 *           &lt;element ref="{}and"/>
 *           &lt;element ref="{}or"/>
 *           &lt;element ref="{}imply"/>
 *           &lt;element ref="{}forall"/>
 *           &lt;element ref="{}exists"/>
 *         &lt;/choice>
 *         &lt;choice>
 *           &lt;element ref="{}atomic"/>
 *           &lt;element ref="{}not"/>
 *           &lt;element ref="{}and"/>
 *           &lt;element ref="{}or"/>
 *           &lt;element ref="{}imply"/>
 *           &lt;element ref="{}forall"/>
 *           &lt;element ref="{}exists"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "content"
})
@XmlRootElement(name = "imply")
public class Imply {

    @XmlElementRefs({
            @XmlElementRef(name = "atomic", type = Atomic.class, required = false),
            @XmlElementRef(name = "not", type = Not.class, required = false),
            @XmlElementRef(name = "exists", type = Exists.class, required = false),
            @XmlElementRef(name = "imply", type = Imply.class, required = false),
            @XmlElementRef(name = "and", type = And.class, required = false),
            @XmlElementRef(name = "forall", type = Forall.class, required = false),
            @XmlElementRef(name = "or", type = Or.class, required = false)
    })
    protected List<Object> content;

    /**
     * Ruft das restliche Contentmodell ab.
     * <p>
     * <p>
     * Sie rufen diese "catch-all"-Eigenschaft aus folgendem Grund ab:
     * Der Feldname "Atomic" wird von zwei verschiedenen Teilen eines Schemas verwendet. Siehe:
     * Zeile 146 von file:/home/gregor/Workspace/Panda2/domain-2.0.xsd
     * Zeile 137 von file:/home/gregor/Workspace/Panda2/domain-2.0.xsd
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
     * {@link Exists }
     * {@link Imply }
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

}