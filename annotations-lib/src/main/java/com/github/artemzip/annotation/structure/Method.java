package com.github.artemzip.annotation.structure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method which should be included to JpaRepository
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Method {
    /**
     * Method's name
     */
    String name();

    /**
     * Type of returning object
     * If type is generic your class will be pasted as type parameter automatically
     */
    Class<?> returnType();

    /**
     * Types of method's arguments, should have correct order as expected
     */
    Class<?>[] args();

    /**
     * If your method is not in spring standard, you can write your own JPQL query
     */
    String query() default "";
}
