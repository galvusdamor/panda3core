//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.04.09 um 10:15:46 AM CEST 
//


package de.uniulm.ki.panda3.symbolic.parser.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the generated package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Documentation_QNAME = new QName("", "documentation");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MethodDeclaration }
     */
    public MethodDeclaration createMethodDeclaration() {
        return new MethodDeclaration();
    }

    /**
     * Create an instance of {@link VariableDeclaration }
     */
    public VariableDeclaration createVariableDeclaration() {
        return new VariableDeclaration();
    }

    /**
     * Create an instance of {@link TaskNode }
     */
    public TaskNode createTaskNode() {
        return new TaskNode();
    }

    /**
     * Create an instance of {@link OrderingConstraint }
     */
    public OrderingConstraint createOrderingConstraint() {
        return new OrderingConstraint();
    }

    /**
     * Create an instance of {@link ValueRestriction }
     */
    public ValueRestriction createValueRestriction() {
        return new ValueRestriction();
    }

    /**
     * Create an instance of {@link Variable }
     */
    public Variable createVariable() {
        return new Variable();
    }

    /**
     * Create an instance of {@link Constant }
     */
    public Constant createConstant() {
        return new Constant();
    }

    /**
     * Create an instance of {@link SortRestriction }
     */
    public SortRestriction createSortRestriction() {
        return new SortRestriction();
    }

    /**
     * Create an instance of {@link CausalLink }
     */
    public CausalLink createCausalLink() {
        return new CausalLink();
    }

    /**
     * Create an instance of {@link Atomic }
     */
    public Atomic createAtomic() {
        return new Atomic();
    }

    /**
     * Create an instance of {@link Not }
     */
    public Not createNot() {
        return new Not();
    }

    /**
     * Create an instance of {@link And }
     */
    public And createAnd() {
        return new And();
    }

    /**
     * Create an instance of {@link Or }
     */
    public Or createOr() {
        return new Or();
    }

    /**
     * Create an instance of {@link Imply }
     */
    public Imply createImply() {
        return new Imply();
    }

    /**
     * Create an instance of {@link Forall }
     */
    public Forall createForall() {
        return new Forall();
    }

    /**
     * Create an instance of {@link Exists }
     */
    public Exists createExists() {
        return new Exists();
    }

    /**
     * Create an instance of {@link DecompositionAxiom }
     */
    public DecompositionAxiom createDecompositionAxiom() {
        return new DecompositionAxiom();
    }

    /**
     * Create an instance of {@link LeftHandSide }
     */
    public LeftHandSide createLeftHandSide() {
        return new LeftHandSide();
    }

    /**
     * Create an instance of {@link RightHandSide }
     */
    public RightHandSide createRightHandSide() {
        return new RightHandSide();
    }

    /**
     * Create an instance of {@link TaskSchemaDeclaration }
     */
    public TaskSchemaDeclaration createTaskSchemaDeclaration() {
        return new TaskSchemaDeclaration();
    }

    /**
     * Create an instance of {@link True }
     */
    public True createTrue() {
        return new True();
    }

    /**
     * Create an instance of {@link ArgumentSort }
     */
    public ArgumentSort createArgumentSort() {
        return new ArgumentSort();
    }

    /**
     * Create an instance of {@link ConstantDeclaration }
     */
    public ConstantDeclaration createConstantDeclaration() {
        return new ConstantDeclaration();
    }

    /**
     * Create an instance of {@link SubSort }
     */
    public SubSort createSubSort() {
        return new SubSort();
    }

    /**
     * Create an instance of {@link SortDeclaration }
     */
    public SortDeclaration createSortDeclaration() {
        return new SortDeclaration();
    }

    /**
     * Create an instance of {@link XMLDomain }
     */
    public XMLDomain createDomain() {
        return new XMLDomain();
    }

    /**
     * Create an instance of {@link RelationDeclaration }
     */
    public RelationDeclaration createRelationDeclaration() {
        return new RelationDeclaration();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "", name = "documentation")
    public JAXBElement<String> createDocumentation(String value) {
        return new JAXBElement<String>(_Documentation_QNAME, String.class, null, value);
    }

}
