/**
 * A utility class for working with stack traces and annotations.
 */
package com.epam.healenium.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A utility class for working with stack traces and annotations.
 */
public final class StackUtils {

    private static final Logger log = LoggerFactory.getLogger(StackUtils.class);

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private StackUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Check if a specific annotation is present in the current stack trace.
     *
     * @param aClass The class of the annotation to check for.
     * @return `true` if the annotation is present, `false` otherwise.
     */
    public static boolean isAnnotationPresent(Class<? extends Annotation> aClass) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        return findAnnotatedInTrace(trace, aClass).isPresent();
    }

    /**
     * Get a stack trace element by searching for a specific class name in the current stack trace.
     *
     * @param elements    The array of stack trace elements.
     * @param targetClass The name of the target class to search for.
     * @return An optional containing the stack trace element if found, or an empty optional if not found.
     */
    public static Optional<StackTraceElement> getElementByClass(StackTraceElement[] elements, String targetClass) {
        return Arrays.stream(elements)
                .filter(redundantPackages())
                .filter(element -> {
                    String className = element.getClassName();
                    String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
                    return simpleClassName.equals(targetClass);
                })
                .findFirst();
    }

    private static Optional<StackTraceElement> findAnnotatedInTrace(StackTraceElement[] elements, Class<? extends Annotation> clazz) {
        return Arrays.stream(elements)
                .filter(redundantPackages())
                .filter(it -> {
                    try {
                        Class<?> aClass = Class.forName(it.getClassName());
                        String methodName = it.getMethodName();
                        return Arrays.stream(aClass.getMethods())
                                .filter(m -> m.getName().equals(methodName))
                                .anyMatch(m -> Arrays.stream(m.getDeclaredAnnotations())
                                        .anyMatch(annotation -> clazz.isInstance(annotation))
                                );
                    } catch (ClassNotFoundException e) {
                        log.warn("Failed to check class: {}", it.getClassName());
                        return false;
                    }
                })
                .findFirst();
    }

    private static Predicate<StackTraceElement> redundantPackages() {
        return value -> {
            Stream<String> skippingPackageStream = Stream.of(
                    "sun.reflect", "java.lang", "org.gradle", "org.junit", "java.util", "com.sun", "com.google"
            );
            return skippingPackageStream.noneMatch(s -> value.getClassName().startsWith(s));
        };
    }
}
