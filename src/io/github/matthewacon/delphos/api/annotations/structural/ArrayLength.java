package io.github.matthewacon.delphos.api.annotations.structural;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ArrayLength {
// long value();
 String value();
 int offset() default 0;
}
