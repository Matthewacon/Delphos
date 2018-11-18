package io.github.matthewacon.delphos.api.annotations.structural;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(SubtypeCase.SubtypeCases.class)
public @interface SubtypeCase {
 String variable();
 String verifier();
 Class<?>[] subtypes();

 @Target(ElementType.TYPE)
 @Retention(RetentionPolicy.RUNTIME)
 @interface SubtypeCases {
  SubtypeCase[] value();
 }
}
