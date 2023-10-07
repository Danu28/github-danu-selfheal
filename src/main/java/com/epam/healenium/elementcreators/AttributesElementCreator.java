package com.epam.healenium.elementcreators;

import com.epam.healenium.treecomparing.Node;
import com.epam.healenium.utils.ResourceReader;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

public class AttributesElementCreator implements ElementCreator {
    private static final Set<String> SKIPPED_ATTRIBUTES;

    static {
        // Initialize SKIPPED_ATTRIBUTES from the resource file once
        SKIPPED_ATTRIBUTES = ResourceReader.readResource("HealConfig/skippedAttributes.txt", s -> s.collect(Collectors.toSet()));
    }

    public AttributesElementCreator() {
    }

    /**
     * Create a selector string based on attributes of a DOM element Node.
     *
     * @param node The DOM element Node for which to create the selector.
     * @return The selector string based on the element's attributes.
     */
    public String create(Node node) {
        StringBuilder selector = new StringBuilder();

        // Build the selector using a single stream operation
        node.getOtherAttributes().entrySet().stream()
                .filter(entry -> StringUtils.isNoneBlank(entry.getKey(), entry.getValue()))
                .filter(entry -> !SKIPPED_ATTRIBUTES.contains(entry.getKey()))
                .forEach(entry -> {
                    selector.append("[").append(entry.getKey().trim()).append("=\"").append(entry.getValue().trim()).append("\"]");
                });

        return selector.toString();
    }
}
