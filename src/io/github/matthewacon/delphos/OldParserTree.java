//package io.github.matthewacon.delphos;
//
//import io.github.matthewacon.delphos.api.*;
//import io.github.matthewacon.pal.util.ExampleLinkedTreeMap;
//import io.github.matthewacon.pal.util.IOUtils;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Field;
//import java.lang.reflect.Modifier;
//import java.util.*;
//import java.util.stream.Collectors;
//
////Generalized LinkedTreeMap specifying the structure of the object being parsed
//public final class OldParserTree<T> extends ExampleLinkedTreeMap<OldParserTree<T>> implements IParser<T> {
// /**Contains a map of half-linked ParserTree instances for a given class. Every ParserTree instance contains references
//  * to the respective children in the tree, descending from the given class, however, does not contain references to
//  * the parent of a class. Parent hierarchy is circumstantial and thus constructed on a per-Parser basis.
//  *
//  * Note: Array types are not cached
//  */
// private static final LinkedHashMap<Class<?>, IParser<?>> PARSER_CACHE;
//
// static {
//  PARSER_CACHE = new LinkedHashMap<>();
//  //Add all primitives to the cache
//  for (final ParsablePrimitives pp : ParsablePrimitives.values()) {
//   PARSER_CACHE.put(pp.getType(), pp);
//  }
// }
//
// private final Class<T> target;
// private final LinkedList<ConditionalAnnotation> conditions;
// private final LinkedList<StructuralAnnotation<?>> structures;
// /**Primitive types and arrays do not contain fields (arrays technically contain the 'length' field, but this does not
//  * need to be set by the parser)
//  *
//  * The 'field' property is initialized by the {@link OldParserTree#construct(Class)} function
//  */
// private Field field;
//
// private OldParserTree(
//  final Class<T> target,
//  final LinkedList<ConditionalAnnotation> conditions,
//  final LinkedList<StructuralAnnotation<?>> structures
// ) {
//  this.target = target;
//  this.conditions = conditions;
//  this.structures = structures;
// }
//
// //TODO multithread field type processing
// //TODO caching functionality
// //TODO remove recursive branching
// //TODO superclass parser construction
// public static <T> OldParserTree<T> construct(final Class<T> clazz) {
//  assertTypeCompliance(clazz);
//  final LinkedList<ConditionalAnnotation> conditions = new LinkedList<>();
//  final LinkedList<StructuralAnnotation<?>> structures = new LinkedList<>();
//  OldParserTree<T> parserTree;
////  ParserTree<T> parserTree = new ParserTree<>(clazz, conditions, structures);
//  if (clazz.isArray()) {
//   parserTree = new OldParserTree<>(clazz, conditions, structures);
//   Class<?> currentDimension = clazz;
//   while (currentDimension.isArray()) {
//    //TODO Process multidimensional array lengths
//    currentDimension = currentDimension.getComponentType();
//    final LinkedList<ConditionalAnnotation> arrayConditions = new LinkedList<>();
//    final LinkedList<StructuralAnnotation<?>> arrayStructures = new LinkedList<>();
//    parserTree.addChild(new OldParserTree(currentDimension, arrayConditions, arrayStructures));
//    //TODO Process array dimension annotations
//    //Each dimension must either have a pre-defined length specifier (@ArrayLength) or have some beginning (and / or)
//    //end specifier (@EncapsulatedBy) to determine the length of each array
//   }
//  } else {
//   final boolean isPrimitive = ParsablePrimitives.contains(clazz);
//   //Check for default constructor
//   if (!isPrimitive) {
//    final long viableConstructors = Arrays
//     .stream(clazz.getConstructors())
//     .filter(constructor -> {
////      final int mods = constructor.getModifiers();
////      return (mods | Modifier.PUBLIC) == mods && constructor.getParameterCount() == 0;
//      return (constructor.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC && constructor.getParameterCount() == 0;
//     })
//     .count();
//    if (viableConstructors < 1) {
//     throw new IllegalArgumentException(
//      "Error: The class '" +
//       clazz.getCanonicalName() +
//       "' does not contain a default constructor!"
//     );
//    }
//   }
//   //Filter out ineligible fields
//   final List<Field> fields = Arrays
//    .asList(clazz.getDeclaredFields())
//    .stream()
//    .filter(field -> {
////     final int mods = field.getModifiers();
////     if ((mods | Modifier.STATIC) != mods) {
//     if ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC) {
//      return true;
//     } else {
//      System.err.println(
//       "Excluded field: '" +
//       field.getName() +
//       "' of type '" +
//       field.getType().getCanonicalName() +
//       "'!"
//      );
//      return false;
//     }
//    })
//    .collect(Collectors.toList());
//   //Build parser tree
//   for (final Field field : fields) {
//    //TODO check cache for field types
//    field.setAccessible(true);
//    //TODO Pal support for generic parameter forwarding -- Class<(H)> => LinkedList<ConditionalAnnotation<(H)>>
//    //Process field annotations
//    final LinkedList<ConditionalAnnotation> fieldConditions = new LinkedList<>();
//    final LinkedList<StructuralAnnotation<?>> fieldStructures = new LinkedList<>();
//    for (final Annotation annotation : field.getAnnotations()) {
//     final ConditionalAnnotation condition = ConditionalAnnotation.generateCondition(annotation);
//     final StructuralAnnotation<?> structure = StructuralAnnotation.generateStructure(annotation);
//     boolean isValid = false;
//     if (condition != null) {
//      isValid = true;
//      fieldConditions.add(condition);
//     }
//     if (structure != null) {
//      isValid = true;
//      fieldStructures.add(structure);
//     }
//     if (!isValid) {
//      System.err.println(
//       "Warning: The annotation '" +
//        annotation.getClass().getCanonicalName() +
//        "' is not a valid delphos annotation!"
//      );
//     }
//    }
//    final OldParserTree ptField = new OldParserTree(field.getType(), fieldConditions, fieldStructures);
//    parserTree.addChild(ptField);
//    parserTree = ptField;
//   }
//  }
//  return parserTree;
// }
//
// //what is this name
// private static void assertFieldCompliance(final Field field, final Class<?> targetTree, final Class<?> clazz) {
//  if (!AbstractSyntaxTree.class.isAssignableFrom(clazz)) {
//   throw new IllegalArgumentException(
//    "Field '" +
//     field.getName() +
//     "' in '" +
//     targetTree.getCanonicalName() +
//     "' is not primitive and does not extend '" +
//     AbstractSyntaxTree.class.getCanonicalName() +
//     "'!"
//   );
//  }
// }
//
// private static void assertTypeCompliance(final Class<?> clazz) {
//  if (!clazz.isArray()) {
//   if (!(clazz.isPrimitive() || AbstractSyntaxTree.class.isAssignableFrom(clazz))) {
//    throw new IllegalArgumentException(
//     "The class '" +
//      clazz.getCanonicalName() +
//      "' is not primitive and does not extend '" +
//      AbstractSyntaxTree.class.getCanonicalName() +
//      "'!"
//    );
//   }
//  } else {
//   Class<?> baseType = clazz;
//   while ((baseType = baseType.getComponentType()).isArray());
//   assertTypeCompliance(clazz);
//  }
// }
//
// @Override
// public OldParserTree<T> clone() {
//  return new OldParserTree<>(target, new LinkedHashMap<>(conditions), new LinkedHashMap<>(structures));
// }
//
// @Override
// public boolean equals(Object obj) {
//  if (obj instanceof OldParserTree) {
//   final OldParserTree<T> parserTree = (OldParserTree<T>)obj;
//   if (parserTree.target.equals(target)) {
//    return true;
//   }
//  }
//  return false;
// }
//
// @Override
//// public <C, P extends IParser<C>> Parsed<P, C> parse(Parsed pt, byte[] data) {
// public Parsed<OldParserTree<T>, T> parse(Parsed parsed, byte[] data) {
//  final ParsablePrimitives pp = ParsablePrimitives.resolve(target);
//  //Process primitives
//  if (pp != null) {
//   return pp.parse(parsed, data);
//  } else {
//   if (parsed == null) {
//    final long[] bitLength = { 0L };
//    final T inst;
//    try {
//     inst = target.newInstance();
//    } catch (final Throwable t) {
//     throw new RuntimeException("Could not instantiate '" + target.getCanonicalName() + "'!", t);
//    }
//    //TODO firstly iterate through structure determining annotations, then verify data with verification annotations
//    for (final Map.Entry<Field, LinkedList<StructuralAnnotation<?>>> entry : structures.entrySet()) {
//     final Field field = entry.getKey();
//     for (final StructuralAnnotation<?> sa : entry.getValue()) {
////      sa.parse()
//     }
//    }
//    return new Parsed<>(this, inst, bitLength[0]);
//   } else {
//    //If the given tree is not the root
//   }
//  }
////  //Start at the top-level superclass (excluding java.lang.Object)
////  ParserTree<T> parent = this;
////  while (getParent() != null) {
////   parent = getParent();
////  }
////  final long index[] = { 0L };
////  parent.traverseTree((TreeTraversalFunction<ParserTree<T>, LinkedList<ParserTree<T>>>)(root, child) -> {
////   if (!root.equals(child)) {
////
////   }
////   return child;
////  });
////  return new Parsed<>(this, inst, 0L);
//  return null;
// }
//
// public static <T> T parse(final OldParserTree<T> parser, final byte[] data) {
//  final Parsed[] parsed = new Parsed[1];
//  parser.traverseTree((TreeTraversalFunction<OldParserTree<T>, LinkedList<OldParserTree<T>>>)(root, child) -> {
//   parsed[0] = child.parse(parsed[0], data);
//   return child;
//  });
//  return null;
// }
//
// @Override
// public Class<T> getType() {
//  return target;
// }
//
// public T parse(final InputStream is) throws IOException {
//  return OldParserTree.parse((OldParserTree<T>)null, IOUtils.readStream(is));
// }
//}
