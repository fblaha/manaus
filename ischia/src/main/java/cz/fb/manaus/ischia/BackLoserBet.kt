package cz.fb.manaus.ischia

import org.springframework.beans.factory.annotation.Qualifier

@Qualifier
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.FIELD, AnnotationTarget.CONSTRUCTOR)
@Retention
annotation class BackLoserBet
