/**
 * This annotation is used to mark fields or types as Page-Aware FindBy, allowing for custom handling
 * of element location strategies and associating them with a specific page.
 *
 * <p>By using this annotation, you can specify a custom page name for the elements and control the
 * strategy for locating them. It helps in maintaining a structured and organized test automation framework
 * by associating elements with their respective pages.
 *
 * <p>Usage example:
 * <pre>
 * {@code
 *   // Applying PageAwareFindBy to a field
 *   @PageAwareFindBy(findBy = @FindBy(id = "exampleElement"), page = "LoginPage")
 *   private WebElement loginButton;
 *
 *   // Applying PageAwareFindBy to a class/type
 *   @PageAwareFindBy(findBy = @FindBy(className = "menu"), page = "HomePage")
 *   public class HomePageElements {
 *       // ...
 *   }
 * }
 * </pre>
 *
 * @see com.epam.healenium.PageAwareBy
 */
package com.epam.healenium.annotation;

import com.epam.healenium.PageAwareBy;
import org.openqa.selenium.By;
import org.openqa.selenium.support.AbstractFindByBuilder;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactoryFinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@PageFactoryFinder(PageAwareFindBy.FindByBuilder.class)
public @interface PageAwareFindBy {
    /**
     * The FindBy annotation that specifies the strategy and value for locating the element.
     *
     * @return The FindBy annotation.
     */
    FindBy findBy();

    /**
     * The name of the page to which this element belongs. Defaults to "DEFAULT_PAGE".
     *
     * @return The name of the associated page.
     */
    String page() default "DEFAULT_PAGE";

    /**
     * Builder class for PageAwareFindBy.
     */
    class FindByBuilder extends AbstractFindByBuilder {
        /**
         * Builds the By object based on the PageAwareFindBy annotation.
         *
         * @param annotation The PageAwareFindBy annotation.
         * @param field      The field associated with the annotation.
         * @return The By object for the annotated element.
         */
        public By buildIt(Object annotation, Field field) {
            PageAwareFindBy pageAwareFindBy = (PageAwareFindBy) annotation;
            FindBy findBy = pageAwareFindBy.findBy();
            assertValidFindBy(findBy);
            By ans = buildByFromShortFindBy(findBy);
            if (ans == null) {
                ans = buildByFromLongFindBy(findBy);
            }
            String actualPageName = pageAwareFindBy.page();
            if ("DEFAULT_PAGE".equals(actualPageName)) {
                actualPageName = field.getDeclaringClass().getSimpleName();
            }
            return PageAwareBy.by(actualPageName, ans);
        }
    }
}
