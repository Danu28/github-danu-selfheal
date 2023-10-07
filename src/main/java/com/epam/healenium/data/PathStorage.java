////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by FernFlower decompiler)
////
//
//package com.epam.healenium.data;
//
//import com.epam.healenium.treecomparing.Node;
//import java.io.IOException;
//import java.util.List;
//import org.openqa.selenium.By;
//
//public interface PathStorage {
//    void persistLastValidPath(By var1, String var2, List<Node> var3);
//
//    List<Node> getLastValidPath(By var1, String var2);
//
//    void saveLocatorInfo(LocatorInfo var1) throws IOException;
//}
package com.epam.healenium.data;

import com.epam.healenium.treecomparing.Node;
import org.openqa.selenium.By;

import java.io.IOException;
import java.util.List;

public interface PathStorage {

    /**
     * Persists the last valid path for a given locator and context.
     *
     * @param locator The Selenium locator.
     * @param context The context or description.
     * @param nodes   The list of nodes representing the path.
     */
    void persistLastValidPath(By locator, String context, List<Node> nodes);

    /**
     * Retrieves the last valid path for a given locator and context.
     *
     * @param locator The Selenium locator.
     * @param context The context or description.
     * @return The list of nodes representing the path.
     */
    List<Node> getLastValidPath(By locator, String context);

    /**
     * Saves locator information to storage.
     *
     * @param locatorInfo The information about locators.
     * @throws IOException If an error occurs while saving the information.
     */
    void saveLocatorInfo(LocatorInfo locatorInfo) throws IOException;
}
