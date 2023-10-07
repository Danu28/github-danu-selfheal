/**
 * A custom By class that includes the name of the web page it belongs to.
 */
package com.epam.healenium;

import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * A custom By class that includes the name of the web page it belongs to.
 */
@Getter
public class PageAwareBy extends By {
    private final String pageName;
    private final By by;

    /**
     * Creates a PageAwareBy instance with the given page name and base By.
     *
     * @param pageName The name of the web page.
     * @param by       The base By to locate elements.
     */
    public static PageAwareBy by(String pageName, By by) {
        return new PageAwareBy(pageName, by);
    }

    /**
     * Finds all elements within the given search context based on the base By.
     *
     * @param searchContext The search context to use for locating elements.
     * @return A list of WebElements that match the base By within the search context.
     */
    public List<WebElement> findElements(SearchContext searchContext) {
        return this.by.findElements(searchContext);
    }

    /**
     * Returns a string representation of this PageAwareBy.
     *
     * @return A string in the format "[base By] on page [page name]".
     */
    public String toString() {
        return String.format("[%s] on page %s", this.by.toString(), this.pageName);
    }

    /**
     * Constructs a PageAwareBy instance with the given page name and base By.
     *
     * @param pageName The name of the web page.
     * @param by       The base By to locate elements.
     */
    public PageAwareBy(String pageName, By by) {
        this.pageName = pageName;
        this.by = by;
    }

    /**
     * Checks if this PageAwareBy is equal to another object.
     *
     * @param o The object to compare with.
     * @return True if equal, false otherwise.
     */
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PageAwareBy)) {
            return false;
        } else {
            PageAwareBy other = (PageAwareBy) o;
            if (!other.canEqual(this)) {
                return false;
            } else if (!super.equals(o)) {
                return false;
            } else {
                Object this$pageName = this.getPageName();
                Object other$pageName = other.getPageName();
                if (this$pageName == null) {
                    if (other$pageName != null) {
                        return false;
                    }
                } else if (!this$pageName.equals(other$pageName)) {
                    return false;
                }

                Object this$by = this.getBy();
                Object other$by = other.getBy();
                if (this$by == null) {
                    if (other$by != null) {
                        return false;
                    }
                } else if (!this$by.equals(other$by)) {
                    return false;
                }

                return true;
            }
        }
    }

    /**
     * Checks if this PageAwareBy can be equal to another object.
     *
     * @param other The object to compare with.
     * @return True if this PageAwareBy can be equal to the other object, false otherwise.
     */
    protected boolean canEqual(Object other) {
        return other instanceof PageAwareBy;
    }

    /**
     * Computes the hash code of this PageAwareBy.
     *
     * @return The hash code value.
     */
    public int hashCode() {
        int result = super.hashCode();
        Object $pageName = this.getPageName();
        result = result * 59 + ($pageName == null ? 43 : $pageName.hashCode());
        Object $by = this.getBy();
        result = result * 59 + ($by == null ? 43 : $by.hashCode());
        return result;
    }
}
