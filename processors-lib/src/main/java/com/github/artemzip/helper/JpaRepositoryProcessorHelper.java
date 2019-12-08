package com.github.artemzip.helper;

import com.github.artemzip.annotation.JpaRepository;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.springframework.data.jpa.repository.Query;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Set;

import static com.github.artemzip.utils.AnnotationUtils.getAnnotationMirrorOfElement;
import static com.github.artemzip.utils.TypeNameUtils.getParameterizedType;
import static com.github.artemzip.utils.TypeNameUtils.getTypeByString;
import static com.github.artemzip.utils.TypeNameUtils.getTypeOfId;

@SuppressWarnings("unchecked")
public class JpaRepositoryProcessorHelper extends AbstractProcessorHelper {
    private static final Class IMPLEMENTED_CLASS = org.springframework.data.jpa.repository.JpaRepository.class;
    private static final String CLASS_SUFFIX = "Repository";
    private static final String PACKAGE_SUFFIX = ".repository";

    JpaRepositoryProcessorHelper(Filer filer, Elements elementUtils, Types typeUtils, Messager messager) {
        super(filer, elementUtils, typeUtils, messager);
    }

    @Override
    public boolean process(RoundEnvironment roundEnvironment) {
        roundEnvironment.getElementsAnnotatedWith(JpaRepository.class).forEach(element -> {
            String packageName = getPackageName((TypeElement) element) + PACKAGE_SUFFIX;

            final TypeSpec.Builder repository = TypeSpec.interfaceBuilder(element.getSimpleName() + CLASS_SUFFIX)
                                                        .addModifiers(Modifier.PUBLIC)
                                                        .addSuperinterface(getParameterizedType(IMPLEMENTED_CLASS, element, getTypeOfId((TypeElement) element)));

            final AnnotationMirror annotationMirror = getAnnotationMirrorOfElement(element, JpaRepository.class);

            if (annotationMirror == null) return;
            annotationMirror.getElementValues().forEach((k,v) -> {
                for( AnnotationMirror annotation : ((List<AnnotationMirror>) v.getValue())) {
                    repository.addMethod(generateMethod(annotation, (TypeElement) element));
                }
            });

            writeType(repository.build(), packageName);
        });

        return false;
    }

    private MethodSpec generateMethod(AnnotationMirror annotation, TypeElement element) {
        Set<? extends ExecutableElement> keys = annotation.getElementValues().keySet();

        ExecutableElement returnType = findKeyByName(keys, "returnType");
        ExecutableElement methodName = findKeyByName(keys, "name");
        ExecutableElement argsArray = findKeyByName(keys, "args");
        ExecutableElement query = findKeyByName(keys, "query");

        final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder((String) annotation.getElementValues().get(methodName).getValue())
                          .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                          .returns(getTypeByString(annotation.getElementValues().get(returnType).toString(), element));

        if(annotation.getElementValues().containsKey(query)){
            methodBuilder.addAnnotation(
                    AnnotationSpec.builder(Query.class).addMember("value", annotation.getElementValues().get(query).toString()).build()
            );
        }

        final List args = (List) annotation.getElementValues().get(argsArray).getValue();
        for (int i = 0; i < args.size(); i++) {
            methodBuilder.addParameter(
                    ParameterSpec.builder(getTypeByString(args.get(i).toString(), null), "arg" + i).build()
            );
        }
        return methodBuilder.build();
    }

    private ExecutableElement findKeyByName(Set<? extends ExecutableElement> keys, String name) {
        return keys.stream()
                   .filter(e -> e.getSimpleName().toString().equals(name))
                   .findAny()
                   .orElse(null);
    }
}
