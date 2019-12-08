package com.github.artemzip.annotation;

import com.github.artemzip.annotation.structure.Method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class annotated by this will be provided by own spring JpaRepository
 * @value is array of methods which should include repository
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface JpaRepository {
    Method[] value() default {};
}
