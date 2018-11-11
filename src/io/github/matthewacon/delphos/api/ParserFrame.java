package io.github.matthewacon.delphos.api;

import java.util.LinkedList;

public final class ParserFrame<T> {
 public final ParsableType<T> pt;
 public final LinkedList<AnnotationCondition<?>> conditions;

 public ParserFrame(final ParsableType<T> pt, final LinkedList<AnnotationCondition<?>> conditions) {
  this.pt = pt;
  this.conditions = conditions;
 }
}