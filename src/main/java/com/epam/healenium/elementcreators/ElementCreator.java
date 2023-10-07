package com.epam.healenium.elementcreators;

import com.epam.healenium.treecomparing.Node;

public interface ElementCreator {
    /**
     * Create a selector string based on the provided Node.
     *
     * @param node The Node for which to create a selector string.
     * @return The selector string created based on the Node.
     */
    String create(Node node);
}
