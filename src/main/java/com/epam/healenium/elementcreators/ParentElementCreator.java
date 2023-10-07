package com.epam.healenium.elementcreators;

import com.epam.healenium.treecomparing.Node;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Creates selector strings representing the parent element of a given Node.
 */
public class ParentElementCreator implements ElementCreator {
    private final TagElementCreator tagCreator = new TagElementCreator();
    private final IdElementCreator idCreator = new IdElementCreator();
    private final ClassElementCreator classCreator = new ClassElementCreator();

    /**
     * Constructs a ParentElementCreator.
     */
    public ParentElementCreator() {
    }

    /**
     * Creates a selector string representing the parent element of a given Node.
     *
     * @param node The Node for which to create the parent selector.
     * @return A selector string representing the parent element.
     */
    @Override
    public String create(Node node) {
        Node parent = node.getParent();
        return parent == null ? "" : Stream.of(tagCreator, idCreator, classCreator)
                .map(creator -> creator.create(parent))
                .collect(Collectors.joining())
                .concat(" > ");
    }
}
