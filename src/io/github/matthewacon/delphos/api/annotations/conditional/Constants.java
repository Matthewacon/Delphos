package io.github.matthewacon.delphos.api.annotations.conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//TODO on pal rewrite, make generic annotation
public final class Constants {
 @Target(ElementType.FIELD)
 @Retention(RetentionPolicy.RUNTIME)
 public @interface ConstantBoolean {
  boolean value();
 }
 @Target(ElementType.FIELD)
 @Retention(RetentionPolicy.RUNTIME)
 public @interface ConstantByte {
  byte value();
 }
 @Target(ElementType.FIELD)
 @Retention(RetentionPolicy.RUNTIME)
 public @interface ConstantShort {
  short value();
 }
 @Target(ElementType.FIELD)
 @Retention(RetentionPolicy.RUNTIME)
 public @interface ConstantInt {
  int value();
 }
 @Target(ElementType.FIELD)
 @Retention(RetentionPolicy.RUNTIME)
 public @interface ConstantFloat {
  float value();
 }
 @Target(ElementType.FIELD)
 @Retention(RetentionPolicy.RUNTIME)
 public @interface ConstantLong {
  long value();
 }
 @Target(ElementType.FIELD)
 @Retention(RetentionPolicy.RUNTIME)
 public @interface ConstantDouble {
  double value();
 }
 @Target(ElementType.FIELD)
 @Retention(RetentionPolicy.RUNTIME)
 public @interface ConstantString {
  String value();
 }
}
