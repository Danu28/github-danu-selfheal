package com.epam.healenium.elementcreators;

import com.epam.healenium.treecomparing.Node;

import java.util.Optional;

/**
 * Creates a positional selector for a given Node.
 */
public class PositionElementCreator implements ElementCreator {

    /**
     * Constructs a PositionElementCreator.
     */
    public PositionElementCreator() {
    }

    /**
     * Creates a positional selector for a given Node.
     *
     * @param node The Node for which to create the positional selector.
     * @return A positional selector for the element.
     */
    @Override
    public String create(Node node) {
        Node parent = node.getParent();
        return parent == null ? "" : Optional.ofNullable(node.getIndex()).map(i -> {
            return String.format(":nth-child(%d)", i + 1);
        }).orElse("");
    }
}
