package com.github.artemzip.utils;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class AnnotationUtils {
    private AnnotationUtils() {}

    public static Object getParameterOfAnnotationOnElement(Element element, Class<?> annotation, String key) {
        final AnnotationMirror annotationMirror = getAnnotationMirrorOfElement(element, annotation);
        final AnnotationValue annotationValue = annotationMirror == null ? null : getAnnotationValue(annotationMirror, key);
        return  annotationValue == null ? null : annotationValue.getValue();
    }

    public static AnnotationMirror getAnnotationMirrorOfElement(Element element, Class<?> annotation){
        return element.getAnnotationMirrors()
                       .stream()
                       .filter(e -> e.getAnnotationType().toString().equals(annotation.getName()))
                       .findAny()
                       .orElse(null);
    }

    public static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
        final ExecutableElement objKey = annotationMirror.getElementValues()
                                                                    .keySet()
                                                                    .stream()
                                                                    .filter(e -> e.getSimpleName().toString().equals(key))
                                                                    .findAny()
                                                                    .orElse(null);

        return annotationMirror.getElementValues().getOrDefault(objKey, null);
    }
}