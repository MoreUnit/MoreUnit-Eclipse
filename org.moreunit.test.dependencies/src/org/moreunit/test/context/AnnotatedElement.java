package org.moreunit.test.context;

import java.lang.annotation.Annotation;

interface AnnotatedElement
{
    <T extends Annotation> T getAnnotation(Class<T> annotationClass);
}
