package com.epam.healenium.elementcreators;

import com.epam.healenium.treecomparing.Node;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class IdElementCreator implements ElementCreator {
    public IdElementCreator() {
    }

    @Override
    public String create(Node node) {
        return Optional.ofNullable(node.getId())
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map("#"::concat)
                .orElse("");
    }
}
