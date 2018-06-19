// PANDA 3 -- a domain-independent planner for classical and hierarchical planning
// Copyright (C) 2014-2018 the original author or authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package de.uniulm.ki.panda3.symbolic.parser.xml.problem;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the de.uniulm.ki.panda3.parser.xml.problem package.
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.uniulm.ki.panda3.parser.xml.problem
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link InitialState }
     */
    public InitialState createInitialState() {
        return new InitialState();
    }

    /**
     * Create an instance of {@link Fact }
     */
    public Fact createFact() {
        return new Fact();
    }

    /**
     * Create an instance of {@link Constant }
     */
    public Constant createConstant() {
        return new Constant();
    }

    /**
     * Create an instance of {@link Or }
     */
    public Or createOr() {
        return new Or();
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
     * Create an instance of {@link CausalLink }
     */
    public CausalLink createCausalLink() {
        return new CausalLink();
    }

    /**
     * Create an instance of {@link VariableDeclaration }
     */
    public VariableDeclaration createVariableDeclaration() {
        return new VariableDeclaration();
    }

    /**
     * Create an instance of {@link Preference }
     */
    public Preference createPreference() {
        return new Preference();
    }

    /**
     * Create an instance of {@link TaskNode }
     */
    public TaskNode createTaskNode() {
        return new TaskNode();
    }

    /**
     * Create an instance of {@link InitialTaskNetwork }
     */
    public InitialTaskNetwork createInitialTaskNetwork() {
        return new InitialTaskNetwork();
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
     * Create an instance of {@link SortRestriction }
     */
    public SortRestriction createSortRestriction() {
        return new SortRestriction();
    }

    /**
     * Create an instance of {@link Problem }
     */
    public Problem createProblem() {
        return new Problem();
    }

    /**
     * Create an instance of {@link ConstantDeclaration }
     */
    public ConstantDeclaration createConstantDeclaration() {
        return new ConstantDeclaration();
    }

    /**
     * Create an instance of {@link Goals }
     */
    public Goals createGoals() {
        return new Goals();
    }

    /**
     * Create an instance of {@link Variable }
     */
    public Variable createVariable() {
        return new Variable();
    }

}
