package io.github.matthewacon.delphos.api;

import java.util.LinkedList;

public interface ParsableType<T> {
 long getBitLength();
 Class<T> getType();

 default LinkedList<AnnotationCondition<?>> conditions() {
  return new LinkedList<>();
 }
}
