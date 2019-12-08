package com.github.artemzip.processor;

import com.github.artemzip.annotation.JpaRepository;
import com.github.artemzip.annotation.RestCrudController;
import com.github.artemzip.helper.HelpersFactory;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MainProcessor extends AbstractProcessor {
    private HelpersFactory helpersFactory;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        helpersFactory = new HelpersFactory(
                processingEnv.getFiler(),
                processingEnv.getElementUtils(),
                processingEnv.getTypeUtils(),
                processingEnv.getMessager()
        );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        helpersFactory.process(roundEnv);
        return true;
    }
}
