package com.github.artemzip.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface RestCrudController {
    /**
     * controller's mapping value
     */
    String value() default "";

    /**
     * whether create method should be created Mappings for this method: "/create", "/save", "/update"
     */
    boolean save() default true;

    /**
     * whether create method should be created Mappings for this method: "/all", "/{id}"
     */
    boolean read() default true;

    /**
     * whether create method should be created Mapping for this method: "/delete" (with request body), "/delete/{id}"
     */
    boolean delete() default true;

}
