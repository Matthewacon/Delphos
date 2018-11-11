package io.github.matthewacon.delphos;

import io.github.matthewacon.delphos.class_parser.ClassFile;
import io.github.matthewacon.pal.util.ExampleLinkedTreeMap;

public class Test {
 public static final class IntegerTree extends ExampleLinkedTreeMap<IntegerTree> {
  public final int i;

  public IntegerTree(final int i) {
   this.i = i;
  }

  public IntegerTree(final int i, final IntegerTree parent) {
   super(parent);
   this.i = i;
  }

  @Override
  public IntegerTree clone() {
   return new IntegerTree(i);
  }

  @Override
  public boolean equals(Object obj) {
   if (obj instanceof IntegerTree) {
    return ((IntegerTree)obj).i == this.i;
   }
   return false;
  }

  @Override
  public String toString() {
   return "" + i;
  }
 }

 public static void main(String[] args) {
//  final Parser<ClassFile> parser = new Parser<>(ClassFile.class);
//  System.out.println();
  long start = System.currentTimeMillis();
  final IntegerTree
   root = new IntegerTree(-1),
   child = new IntegerTree(7);
  child.addChild(new IntegerTree(8));
  child.addChild(new IntegerTree(9));
  root.addChild(child);
  root.traverseTree((parent, elem) -> {
   System.out.println(elem);
   return elem;
  });
  System.out.println("Duration: " + (System.currentTimeMillis() - start));
  System.out.println();
 }
}
