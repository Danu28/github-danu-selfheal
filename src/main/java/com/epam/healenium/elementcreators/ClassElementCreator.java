package com.epam.healenium.elementcreators;

import com.epam.healenium.treecomparing.Node;
import java.util.stream.Collectors;

public class ClassElementCreator implements ElementCreator {
    public ClassElementCreator() {
        // Constructor for the ClassElementCreator class
    }

    /**
     * Create a selector string based on the classes of a DOM element Node.
     *
     * @param node The DOM element Node for which to create the selector.
     * @return The selector string based on the element's classes.
     */
    public String create(Node node) {
        // Use a stream to map each class to ".classname" and join them together
        return node.getClasses().stream().map(clazz -> "." + clazz).collect(Collectors.joining());
    }
}
