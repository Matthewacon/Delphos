package io.github.matthewacon.delphos.api.annotations.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface TypeAlias {
 Class<?> aliasType();
 String aliasTarget();
}
