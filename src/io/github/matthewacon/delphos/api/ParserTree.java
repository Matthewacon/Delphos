package io.github.matthewacon.delphos.api;

import io.github.matthewacon.pal.util.ExampleLinkedTreeMap;

import java.util.LinkedList;

public final class ParserTree<T> extends ExampleLinkedTreeMap<ParserTree<T>> {
 public final ParsableType<T> pt;
 public final LinkedList<AnnotationCondition<?>> conditions;

 public ParserTree(final ParsableType<T> pt, final LinkedList<AnnotationCondition<?>> conditions) {
  this.pt = pt;
  this.conditions = conditions;
 }

 @Override
 public ParserTree<T> clone() {
  return new ParserTree<>(this.pt, this.conditions);
 }

 @Override
 public boolean equals(Object o) {
  if (o instanceof ParserTree) {
   final ParserTree<?> pf = (ParserTree)o;
   if (pf.pt.equals(this.pt) && pf.conditions.equals(this.conditions)) {
    return true;
   }
  }
  return false;
 }
}