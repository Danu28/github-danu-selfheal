package com.epam.healenium.elementcreators;

import com.epam.healenium.treecomparing.Node;

/**
 * Creates a tag-based selector for a given Node.
 */
public class TagElementCreator implements ElementCreator {

    /**
     * Constructs a TagElementCreator.
     */
    public TagElementCreator() {
    }

    /**
     * Creates a tag-based selector for a given Node.
     *
     * @param node The Node for which to create the tag-based selector.
     * @return A tag-based selector for the element.
     */
    @Override
    public String create(Node node) {
        return node.getTag();
    }
}
