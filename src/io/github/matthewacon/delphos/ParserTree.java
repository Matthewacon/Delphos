package io.github.matthewacon.delphos;

import io.github.matthewacon.delphos.api.*;
import io.github.matthewacon.pal.util.ClassUtils;
import io.github.matthewacon.pal.util.ExampleLinkedTreeMap;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

//Generic ParserTree structure that encapsulates all extensions of IParser<T>, including primitives, arrays and complex
//types.
public final class ParserTree<T> extends ExampleLinkedTreeMap<ParserTree<T>> implements IParser<T> {
// private static final LinkedHashMap<Class<?>, IParser<?>> PARSER_CACHE;
//
// static {
//  PARSER_CACHE = new LinkedHashMap<>();
//  //Cache all primitive parsers
//  for (final ParsablePrimitives pp : ParsablePrimitives.values()) {
//   for (final Class<?> clazz : pp.equivalents()) {
//    PARSER_CACHE.put(clazz, pp);
//   }
//  }
// }

 private final Class<T> target;
 private final LinkedList<StructuralAnnotation<?>> structuralAnnotations;
 private final LinkedList<ConditionalAnnotation> conditionalAnnotations;
 /**Primitive types and arrays do not contain fields (arrays technically contain the 'length' field, but this does not
  * need to be set by the parser)
  *
  * The 'field' property is initialized by the {@link ParserTree#construct(Class)} function
  */
 private Field field;
 private IParser<T> parser = this;

 private ParserTree(
  final Class<T> target,
  final LinkedList<StructuralAnnotation<?>> structuralAnnotations,
  final LinkedList<ConditionalAnnotation> conditionalAnnotations
 ) {
  this.target = target;
  this.structuralAnnotations = structuralAnnotations;
  this.conditionalAnnotations = conditionalAnnotations;
 }

 public static final <T> IParser<T> construct(final Class<T> clazz) {
  assertTypeCompliance(clazz);
  //Search for primitive parser
  //If a primitive parser was found, then the type is unbound
  final IParser<T> parser = ParsablePrimitives.resolve(clazz);
  //Process array or complex type (if no primitive parser was found)
  if (parser == null) {
   ParserTree<?> parserTree = null;
   if (clazz.isArray()) {
    //Process unbound array type (an array type that does not pertain to an encapsulating class)
    if (ClassUtils.countDims(clazz) > 1) {
     throw new ParserConfigurationException(
      "Array types of greater than one dimension, that are not part of an encapsulating class and thus do not " +
      "possess any structure-defining annotations, cannot be parsed as the length of any array dimension 'n' is " +
      "indeterminable. One-dimensional arrays, however, may be parsed so long as the exact byte length of the " +
      "component type is determinable in some manner."
     );
    } else {
     //TODO process component type without recursive invocations
     final IParser<?> componentParser = construct(clazz.getComponentType());
     //TODO move to separate class
     return new IParser<T>() {
      public Parsed<IParser<T>, T> parse(Parsed pt, byte[] data) {
       final LinkedList<Object> elements = new LinkedList<>();
       final long originalLength = 8 * data.length;
       long bitLength = originalLength;
       //TODO BufferUnderflowException and BufferOverflowException
       while (bitLength > 0) {
//        data = BitStream.shiftLeft(data, originalLength - bitLength, false);
        final Parsed parsed = componentParser.parse(pt, data);
        if (pt == null) {
         pt = parsed;
        } else {
         pt.addChild(parsed);
        }
        bitLength -= parsed.bitLength;
        elements.add(parsed.parsed);
        //TODO convert over to {@link io.github.matthewacon.delphos.utils.BitStream#shiftLeft(byte[], long)}
        data = Arrays.copyOfRange(data, (int)(bitLength / 8), data.length);
       }
       return new Parsed<>(this, (T)elements.toArray(), originalLength);
      }

      public Class<T> getType() {
       return clazz;
      }
     };
    }
   } else {
    LinkedHashMap<ParserTree<?>, LinkedList<Class<?>>>
     lastRound = new LinkedHashMap<>(),
     nextRound = new LinkedHashMap<>();
//    LinkedList<Class<?>>
//     lastRound = new LinkedList<>(),
//     nextRound = new LinkedList<>();
    nextRound.put(parserTree, new LinkedList<>());
    nextRound.get(parserTree).add(clazz);
    while (nextRound.size() > 0) {
     lastRound.clear();
     lastRound = new LinkedHashMap<>(nextRound);
     nextRound.clear();
     for (final Entry<ParserTree<?>, LinkedList<Class<?>>> entry : lastRound.entrySet()) {
//      if (target.isArray()) {
//       //TODO process array dim annotations
//       //Each dimension must either have a predefined length, or be encapsulated
//      } else {
//       //Process complex type
//       final LinkedList<StructuralAnnotation<?>> structuralAnnotations = new LinkedList<>();
//       final LinkedList<ConditionalAnnotation> conditionalAnnotations = new LinkedList<>();
//       //TODO Process class annotations
//       //TODO Process fields and field annotations
//       parserTree = new ParserTree<>(clazz, structuralAnnotations, conditionalAnnotations);
//      }
     }
    }
   }
   return (IParser<T>)parserTree;
  }
  return parser;
 }

 public static <T> T parse(final IParser<T> parser, final byte... data) {
  return parser.<T, IParser<T>>parse(null, data).parsed;
 }

 @Override
 //TODO
 public Parsed<IParser<T>, T> parse(Parsed pt, byte[] data) {
  if (parser.equals(this)) {
   return new Parsed<>(this, null, 0L);
  } else {
   return parser.parse(pt, data);
  }
 }

 @Override
 public Class<T> getType() {
  return target;
 }

 @Override
 public ParserTree<T> shallowClone() {
  return new ParserTree<>(target, new LinkedList<>(structuralAnnotations), new LinkedList<>(conditionalAnnotations));
 }

 @Override
 //TODO Doc: disregards field data
 public boolean equals(Object o) {
  return false;
 }

 private static void assertFieldCompliance(final Field field, final Class<?> targetTree, final Class<?> clazz) {
  if (!AbstractSyntaxTree.class.isAssignableFrom(clazz)) {
   throw new IllegalArgumentException(
    "Field '" +
     field.getName() +
     "' in '" +
     targetTree.getCanonicalName() +
     "' is not primitive and does not extend '" +
     AbstractSyntaxTree.class.getCanonicalName() +
     "'!"
   );
  }
 }

 private static void assertTypeCompliance(Class<?> clazz) {
  if (!clazz.isArray()) {
   final ParsablePrimitives pp = ParsablePrimitives.resolve(clazz);
   if (!(pp != null || AbstractSyntaxTree.class.isAssignableFrom(clazz))) {
    throw new IllegalArgumentException(
     "The class '" +
      clazz.getCanonicalName() +
      "' is not primitive and does not extend '" +
      AbstractSyntaxTree.class.getCanonicalName() +
      "'!"
    );
   }
  } else {
   while ((clazz = clazz.getComponentType()).isArray());
   assertTypeCompliance(clazz);
  }
 }
}
