package io.github.matthewacon.delphos;

import io.github.matthewacon.delphos.api.*;
import io.github.matthewacon.pal.util.ClassUtils;
import io.github.matthewacon.pal.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public final class Parser<T extends AbstractSyntaxTree> {
 /**Contains a map of half-linked ParserTree instances for a given class. Every ParserTree instance contains references
  * to the respective children in the tree, descending from the given class, however, does not contain references to
  * the parent of a class. Parent hierarchy is circumstantial and thus constructed on a per-Parser basis.
  */
 private static final LinkedHashMap<Class<?>, ParserTree<?>> PARSER_CACHE;

 static {
  PARSER_CACHE = new LinkedHashMap<>();
 }

 private final Class<T> target;
 private final ParserTree<? super T> apt;

 public Parser(final Class<T> clazz) {
  if (!AbstractSyntaxTree.class.isAssignableFrom(clazz)) {
   throw new IllegalArgumentException();
  }
  this.target = clazz;
  ParserTree apt = PARSER_CACHE.get(clazz);
  if (apt == null) {
   //TODO populate the parser cache
   apt = constructTree(clazz, true);
   //Construct the superclass parser hierarchy (child linking only)
   for (final Class<?> superClass : ClassUtils.resolveSuperClasses(clazz)) {
    if (AbstractSyntaxTree.class.isAssignableFrom(superClass)) {
     final ParserTree<?> superTree = constructTree(superClass, false);
     superTree.addChild(apt);
     apt = superTree;
    } else {
     if (!Object.class.equals(superClass)) {
      throw new IllegalArgumentException(
       "Warning: Class '" +
       superClass.getCanonicalName() +
       "' is a superclass of '" +
       clazz.getCanonicalName() +
       "' and does not extend '" +
       AbstractSyntaxTree.class.getCanonicalName() +
       "'!"
      );
     }
    }
   }
   //TODO Construct the parent hierarchy
   ParserTree parent = apt;
   LinkedList<ParserTree>
    toLink,
    nextRound = new LinkedList<>();
   nextRound.addAll(parent.getChildren());
   while (nextRound.size() > 0) {
    toLink = new LinkedList<>(nextRound);
    nextRound.clear();
    for (final ParserTree child : toLink) {
     child.setParent(parent);
    }
   }
  }
  this.apt = apt;
 }

 //Disregards superclasses
 //TODO multithread field type processing
 //TODO caching functionality
 private static <T> ParserTree<T> constructTree(final Class<T> clazz, final boolean checkConstructor) {
  final LinkedList<ParserTree<?>> parsableTypes = new LinkedList<>();
  //Check for default constructor
  if (checkConstructor) {
   final long viableConstructors = Arrays
    .stream(clazz.getConstructors())
    .filter(constructor -> {
     final int mods = constructor.getModifiers();
     return (mods | Modifier.PUBLIC) == mods && constructor.getParameterCount() == 0;
    })
    .count();
   if (viableConstructors < 1) {
    throw new IllegalArgumentException(
     "Error: The class '" +
      clazz.getCanonicalName() +
      "' does not contain a default constructor!"
    );
   }
  }
  //Filter out ineligible fields
  final List<Field> fields = Arrays
   .asList(clazz.getDeclaredFields())
   .stream()
   .filter(field -> {
    final int mods = field.getModifiers();
    return (mods | Modifier.STATIC) != mods;
   })
   .collect(Collectors.toList());
  //Build parser tree
  for (final Field field : fields) {
   field.setAccessible(true);
   //TODO Pal support for generic parameter forwarding -- Class<(H)> => LinkedList<AnnotationCondition<(H)>>
   //Process field annotations
   final LinkedList<AnnotationCondition<?>> conditions = new LinkedList<>();
   for (final Annotation annotation : field.getAnnotations()) {
    final AnnotationCondition<?> condition = AnnotationCondition.generateCondition(annotation);
    if (condition != null) {
     conditions.add(condition);
    } else {
     System.err.println(
      "Warning: The annotation '" +
      annotation.getClass().getCanonicalName() +
      "' is not a valid delphos annotation!"
     );
    }
   }
   final Class<?> fieldType = field.getType();
   if (fieldType.isArray()) {
    //TODO process array dim annotations
    //Each dimension must either have a pre-defined length specifier (@ArrayLength) or have some beginning (and / or)
    //end specifier (@EncapsulatedBy) to determine the length of each array
    Class<?> baseFieldType = field.getType();
    while ((baseFieldType = baseFieldType.getComponentType()).isArray());
    assertFieldCompliance(field, clazz, baseFieldType);
   }
   //Construct APT for primitive or class
   if (fieldType.isPrimitive()) {
    //Process primitives
    parsableTypes.add(new ParserTree(
     ParsablePrimitives.resolve(fieldType),
     conditions
    ));
   } else {
    //Process classes
    assertFieldCompliance(field, clazz, fieldType);

   }
  }
  //Calculate bit length

  return null;
 }

 //what is this name
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

 public T parse(final InputStream is) throws IOException {
  final byte[] data = IOUtils.readStream(is);
  final int[] index = new int[1];

  apt.traverseTree((root, child) -> {
   return child;
  });
  return null;
 }
}
