package com.github.artemzip.helper;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;

public abstract class AbstractProcessorHelper {
    protected Filer filer;
    protected Elements elementUtils;
    protected Types typeUtils;
    protected Messager messager;

    public AbstractProcessorHelper(Filer filer, Elements elementUtils, Types typeUtils, Messager messager) {
        this.filer = filer;
        this.elementUtils = elementUtils;
        this.typeUtils = typeUtils;
        this.messager = messager;
    }

    public abstract boolean process(final RoundEnvironment roundEnvironment);

    protected void writeType (TypeSpec typeSpec, String packageName) {
        try {
            JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Cannot write file");
        }
    }
    protected String getPackageName(TypeElement element) {
        return ClassName.get(element).packageName();
    }
}
