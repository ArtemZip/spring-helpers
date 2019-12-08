package com.github.artemzip.utils;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TypeNameUtils {
    private TypeNameUtils() {}

    public static TypeName getTypeByString(String clazzName, Element element) {
        clazzName = clazzName.replace(".class", "");
        try {
            Class clazz = Class.forName(clazzName);
            return (clazz.getTypeParameters().length > 0) ? getParameterizedType(clazz, element)
                                                          : ClassName.get(clazz);
        } catch (ClassNotFoundException e) {
            return ClassName.get("", clazzName);
        }
    }

    public static ParameterizedTypeName getParameterizedType(Class<?> clazz, Element element, TypeName ...typeNames) {
        List<TypeName> classNames = Stream.of(typeNames).collect(Collectors.toList());
        classNames.add(0, TypeName.get(element.asType()));
        return getParameterizedType(clazz, classNames);
    }

    public static ParameterizedTypeName getParameterizedType(Class<?> clazz, Element element, List<TypeName> typeNames) {
        typeNames.add(0, TypeName.get(element.asType()));
        return getParameterizedType(clazz, typeNames);
    }

    public static ParameterizedTypeName getParameterizedType(Class<?> clazz, List<TypeName> typeNames) {
        final TypeName[] names = typeNames.toArray(new TypeName[0]);
        return ParameterizedTypeName.get(ClassName.get(clazz), names);
    }

    public static List<TypeName> getTypeOfId(TypeElement element) {
        return element.getEnclosedElements().stream().filter(e -> {
            boolean isField = e.getKind().isField();
            boolean isId = e.getAnnotation(org.springframework.data.annotation.Id.class) != null
                           || e.getAnnotation(javax.persistence.Id.class) != null;

            return isField && isId;
        }).map(e -> TypeName.get(e.asType())).collect(Collectors.toList());
    }
}
