package cz.fb.manaus.manila

import org.springframework.beans.factory.annotation.Qualifier

@Qualifier
@Target(
        AnnotationTarget.CLASS,
        AnnotationTarget.FILE,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.FIELD,
        AnnotationTarget.CONSTRUCTOR)
@Retention
annotation class ManilaBet
