package com.github.artemzip.helper;

import com.github.artemzip.utils.SupportedType;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.EnumMap;

public class HelpersFactory {
    private static final EnumMap<SupportedType, AbstractProcessorHelper> HELPERS = new EnumMap<>(SupportedType.class);

    private Filer filer;
    private Elements elementUtils;
    private Types typeUtils;
    private Messager messager;

    public HelpersFactory(Filer filer, Elements elementUtils, Types typeUtils, Messager messager) {
        this.filer = filer;
        this.elementUtils = elementUtils;
        this.typeUtils = typeUtils;
        this.messager = messager;
        init();
    }

    private void init() {
        HELPERS.put(SupportedType.JPA_REPOSITORY, new JpaRepositoryProcessorHelper(filer, elementUtils, typeUtils, messager));
        HELPERS.put(SupportedType.REST_CRUD_CONTROLLER, new RestCrudControllerProcessorHelper(filer, elementUtils, typeUtils, messager));
    }

    public void process(RoundEnvironment roundEnvironment) {
        HELPERS.forEach( (key,helper) -> helper.process(roundEnvironment));
    }

}
