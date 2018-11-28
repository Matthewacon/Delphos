package io.github.matthewacon.delphos;

//import io.github.matthewacon.delphos.class_parser.ClassFile;
import io.github.matthewacon.delphos.api.IParser;
import io.github.matthewacon.pal.util.AbstractTreeMap;
import io.github.matthewacon.pal.util.ExampleLinkedTreeMap;

import java.util.Arrays;
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
  public IntegerTree shallowClone() {
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

  //Parser Tests
  start = System.currentTimeMillis();
  final IParser<Byte> parser = ParserTree.construct(byte.class);
  long end = System.currentTimeMillis();
  System.out.println("Parser construction time (ms): " + (end - start));
  start = System.currentTimeMillis();
  final int i = (int)ParserTree.parse(parser, (byte)0x0F);
  end = System.currentTimeMillis();
  System.out.println("Result: " + i + " :: Duration (ms): " + (end - start));
  start = System.currentTimeMillis();
  final IParser<Float[]> parser1 = ParserTree.construct(Float[].class);
  end = System.currentTimeMillis();
  System.out.println("Parser construction time (ms): " + (end - start));
  start = System.currentTimeMillis();
//  int j = Float.floatToRawIntBits(3.1415926535897932384626433826795f);
  int
   j = Float.floatToRawIntBits(3.14159265358f),
   k = Float.floatToRawIntBits(1.41421356237f);
  final byte[] bytes = {
   (byte)(j >> 24),
   (byte)(j >> 16),
   (byte)(j >> 8),
   (byte)j,
   (byte)(k >> 24),
   (byte)(k >> 16),
   (byte)(k >> 8),
   (byte)k
  };
  final Object[] ia = ParserTree.parse(parser1, bytes);
  end = System.currentTimeMillis();
  System.out.println("Result: " + Arrays.toString(ia) + " :: Duration (ms): " + (end - start));
  final char c = 'X';
  System.out.println(c);
  final byte[] cBytes = {
   (byte)(c >> 8),
   (byte)c
  };
  final IParser<Character> parser2 = ParserTree.construct(char.class);
  char d = ParserTree.parse(parser2, cBytes);
  System.out.println(d);
 }
}