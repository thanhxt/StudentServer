package com.acme.ttx.controller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to put on Mapstruct mappers for generated classes to keep the annotation.
 * <i>https://github.com/mapstruct/mapstruct/issues/1528</i>
 * <i>https://github.com/mapstruct/mapstruct/issues/1574</i>
 */
@Retention(RetentionPolicy.CLASS)
public @interface ExcludeFromJacocoGeneratedReport {
}
