package cz.fb.manaus.manila

import org.springframework.beans.factory.annotation.Qualifier
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Qualifier
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.FIELD, AnnotationTarget.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
annotation class ManilaBet
