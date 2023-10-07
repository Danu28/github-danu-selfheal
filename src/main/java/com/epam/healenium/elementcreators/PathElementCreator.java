package com.epam.healenium.elementcreators;

import com.epam.healenium.treecomparing.Node;
import java.util.ArrayDeque;

/**
 * Creates a selector string representing the path from the root to a given Node.
 */
public class PathElementCreator implements ElementCreator {
    private final PositionElementCreator positionCreator = new PositionElementCreator();

    /**
     * Constructs a PathElementCreator.
     */
    public PathElementCreator() {
    }

    /**
     * Creates a selector string representing the path from the root to a given Node.
     *
     * @param node The Node for which to create the path selector.
     * @return A selector string representing the path to the element.
     */
    @Override
    public String create(Node node) {
        Node current = node;

        ArrayDeque<String> path = new ArrayDeque<>();
        while (current != null) {
            String item = current.getTag();
            if (hasSimilarNeighbours(current)) {
                item = item + positionCreator.create(current);
            }

            path.addFirst(item);
            current = current.getParent();
        }

        return String.join(" > ", path);
    }

    /**
     * Checks if a Node has similar neighboring elements with the same tag.
     *
     * @param current The Node to check.
     * @return true if there are similar neighboring elements, false otherwise.
     */
    private boolean hasSimilarNeighbours(Node current) {
        Node parent = current.getParent();
        if (parent == null) {
            return false;
        } else {
            return parent.getChildren().stream()
                    .map(Node::getTag)
                    .filter(tag -> tag.equals(current.getTag()))
                    .count() > 1L;
        }
    }
}
