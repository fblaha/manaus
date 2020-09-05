package cz.fb.manaus.ischia

import org.springframework.beans.factory.annotation.Qualifier

@Qualifier
@Target(
        AnnotationTarget.CLASS,
        AnnotationTarget.FILE,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.FIELD,
        AnnotationTarget.CONSTRUCTOR
)
@Retention(AnnotationRetention.RUNTIME)
annotation class LayUniverse
