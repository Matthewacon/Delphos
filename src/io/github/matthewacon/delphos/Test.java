package io.github.matthewacon.delphos;

//import io.github.matthewacon.delphos.class_parser.ClassFile;
import io.github.matthewacon.pal.util.AbstractTreeMap;
import io.github.matthewacon.pal.util.ExampleLinkedTreeMap;

import java.util.LinkedList;

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
   child1 = new IntegerTree(7),
   child2 = new IntegerTree(100);
  child1.addChild(new IntegerTree(8));
  child1.addChild(new IntegerTree(9));
  child1.addChild(new IntegerTree(10));
  child1.addChild(new IntegerTree(11));
  child2.addChild(new IntegerTree(101));
  child2.addChild(new IntegerTree(102));
  child2.addChild(new IntegerTree(103));
  child2.addChild(new IntegerTree(104));
  root.addChild(child1);
  root.addChild(child2);
  root.traverseTree((AbstractTreeMap.TreeTraversalFunction<IntegerTree, LinkedList<IntegerTree>>)(parent, elem) -> {
   System.out.println(elem);
   return elem;
  });
  System.out.println("Duration: " + (System.currentTimeMillis() - start));
  System.out.println();
 }
}