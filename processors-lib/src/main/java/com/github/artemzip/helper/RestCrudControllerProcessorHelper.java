package com.github.artemzip.helper;

import com.github.artemzip.annotation.RestCrudController;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.validation.Valid;
import java.util.Optional;

import static com.github.artemzip.utils.AnnotationUtils.getParameterOfAnnotationOnElement;
import static com.github.artemzip.utils.TypeNameUtils.getParameterizedType;
import static com.github.artemzip.utils.TypeNameUtils.getTypeOfId;

public class RestCrudControllerProcessorHelper extends AbstractProcessorHelper {
    private static final String CLASS_SUFFIX = "RestController";
    private static final String PACKAGE_SUFFIX = ".controller";
    private static final String REPOSITORY_CLASS_SUFFIX = "Repository";
    private static final String REPOSITORY_PACKAGE_SUFFIX = ".repository";

    RestCrudControllerProcessorHelper(Filer filer, Elements elementUtils, Types typeUtils, Messager messager) {
        super(filer, elementUtils, typeUtils, messager);
    }

    @Override
    public boolean process(RoundEnvironment roundEnvironment) {
        roundEnvironment.getElementsAnnotatedWith(RestCrudController.class).forEach(element -> {
            final String typePackageName = getPackageName((TypeElement) element);
            final String elementName = element.getSimpleName().toString();

            String mapping = (String) getParameterOfAnnotationOnElement(element, RestCrudController.class, "value");
            mapping = mapping == null ? elementName.toLowerCase() : mapping;

            final AnnotationSpec path = AnnotationSpec.builder(RequestMapping.class)
                                                      .addMember("value", "$S", mapping)
                                                      .build();

            final TypeSpec.Builder restController = TypeSpec.classBuilder(elementName + CLASS_SUFFIX)
                                                            .addAnnotation(RestController.class)
                                                            .addAnnotation(path)
                                                            .addModifiers(Modifier.PUBLIC)
                                                            .addField(FieldSpec.builder(
                                                                    ClassName.bestGuess(getRepositoryClassName(elementName, typePackageName)),
                                                                    "repository",
                                                                    Modifier.PRIVATE).addAnnotation(Autowired.class).build());

            Boolean save = (Boolean) getParameterOfAnnotationOnElement(element, RestCrudController.class, "save");
            Boolean read = (Boolean) getParameterOfAnnotationOnElement(element, RestCrudController.class, "read");
            Boolean delete = (Boolean) getParameterOfAnnotationOnElement(element, RestCrudController.class, "delete");

            if (save == null || save) {
                restController.addMethod(save((TypeElement) element));
            }

            if (read == null || read) {
                restController.addMethod(findAllMethod((TypeElement) element)).addMethod(findById((TypeElement) element));
            }

            if (delete == null || delete) {
                restController.addMethod(deleteById((TypeElement) element)).addMethod(delete((TypeElement) element));
            }

            writeType(restController.build(), typePackageName + PACKAGE_SUFFIX);
        });
        return false;
    }

    private  MethodSpec save(TypeElement element) {
        final AnnotationSpec mapping = AnnotationSpec.builder(PostMapping.class)
                                                     .addMember("value", "{$S, $S, $S}", "/create", "/save", "/update")
                                                     .build();

        final ParameterSpec obj = ParameterSpec.builder(TypeName.get(element.asType()), "entity")
                                               .addAnnotation(Valid.class)
                                               .addAnnotation(RequestBody.class)
                                               .build();

        return MethodSpec.methodBuilder("save")
                         .addModifiers(Modifier.PUBLIC)
                         .addAnnotation(mapping)
                         .returns(TypeName.get(element.asType()))
                         .addParameter(obj)
                         .addStatement("if(entity == null) return null")
                         .addStatement("return repository.save(entity)")
                         .build();
    }

    private MethodSpec findById(TypeElement element){
        final AnnotationSpec mapping = AnnotationSpec.builder(GetMapping.class)
                                                   .addMember("value", "$S", "/{id}")
                                                   .build();

        final ParameterSpec id = ParameterSpec.builder(getTypeOfId(element).get(0), "id", Modifier.FINAL)
                                              .addAnnotation(PathVariable.class)
                                              .build();

        return MethodSpec.methodBuilder("findById")
                         .addModifiers(Modifier.PUBLIC)
                         .addAnnotation(mapping)
                         .returns(getParameterizedType(Optional.class, element))
                         .addParameter(id)
                         .addStatement("if(id == null) return null")
                         .addStatement("return repository.findById(id)")
                         .build();
    }

    private MethodSpec findAllMethod(TypeElement element){
        final AnnotationSpec mapping = AnnotationSpec.builder(GetMapping.class)
                                                     .addMember("value", "$S", "/all")
                                                     .build();

        return MethodSpec.methodBuilder("findAll")
                         .addModifiers(Modifier.PUBLIC)
                         .addAnnotation(mapping)
                         .returns(getParameterizedType(Iterable.class, element))
                         .addStatement("return repository.findAll()")
                         .build();
    }

    private MethodSpec deleteById(TypeElement element) {
        final AnnotationSpec mapping = AnnotationSpec.builder(DeleteMapping.class)
                                                     .addMember("value", "$S", "/delete/{id}")
                                                     .build();

        final ParameterSpec id = ParameterSpec.builder(getTypeOfId(element).get(0), "id", Modifier.FINAL)
                                              .addAnnotation(PathVariable.class)
                                              .build();

        return MethodSpec.methodBuilder("deleteById")
                         .addModifiers(Modifier.PUBLIC)
                         .addAnnotation(mapping)
                         .addParameter(id)
                         .addStatement("if(id == null) return")
                         .addStatement("repository.deleteById(id)")
                         .build();
    }

    private MethodSpec delete(TypeElement element) {
        final AnnotationSpec mapping = AnnotationSpec.builder(DeleteMapping.class)
                                                     .addMember("value", "$S", "/delete")
                                                     .build();

        final ParameterSpec id = ParameterSpec.builder(TypeName.get(element.asType()), "entity", Modifier.FINAL)
                                              .addAnnotation(RequestBody.class)
                                              .build();

        return MethodSpec.methodBuilder("delete")
                         .addModifiers(Modifier.PUBLIC)
                         .addAnnotation(mapping)
                         .addParameter(id)
                         .addStatement("if(entity == null) return")
                         .addStatement("repository.delete(entity)")
                         .build();
    }

    private String getRepositoryClassName(String typeName, String packageName) {
        return packageName + REPOSITORY_PACKAGE_SUFFIX + "." + typeName + REPOSITORY_CLASS_SUFFIX;
    }
}