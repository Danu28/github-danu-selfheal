/**
 * The SelectorComponent enum represents different components used to create selectors for elements on a web page.
 */
package com.epam.healenium;

import com.epam.healenium.elementcreators.*;
import com.epam.healenium.treecomparing.Node;

/**
 * An enumeration of SelectorComponents, each associated with an ElementCreator to generate element selectors.
 */
public enum SelectorComponent {
    /**
     * Represents a path element creator.
     */
    PATH(new PathElementCreator()),

    /**
     * Represents a parent element creator.
     */
    PARENT(new ParentElementCreator()),

    /**
     * Represents a tag element creator.
     */
    TAG(new TagElementCreator()),

    /**
     * Represents an ID element creator.
     */
    ID(new IdElementCreator()),

    /**
     * Represents a class element creator.
     */
    CLASS(new ClassElementCreator()),

    /**
     * Represents a position element creator.
     */
    POSITION(new PositionElementCreator()),

    /**
     * Represents an attributes element creator.
     */
    ATTRIBUTES(new AttributesElementCreator());

    private final ElementCreator elementCreator;

    /**
     * Creates a SelectorComponent with the associated ElementCreator.
     *
     * @param elementCreator The ElementCreator for this SelectorComponent.
     */
    private SelectorComponent(ElementCreator elementCreator) {
        this.elementCreator = elementCreator;
    }

    /**
     * Creates a component of an element selector based on the provided Node using the associated ElementCreator.
     *
     * @param node The Node representing an element.
     * @return A string component of the element selector.
     */
    public String createComponent(Node node) {
        return this.elementCreator.create(node);
    }
}
