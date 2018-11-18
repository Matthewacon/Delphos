package io.github.matthewacon.delphos;

import io.github.matthewacon.delphos.api.*;
import io.github.matthewacon.pal.util.ExampleLinkedTreeMap;
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

public final class Parser<T> extends ExampleLinkedTreeMap<Parser<T>> implements IParser<T> {
 /**Contains a map of half-linked ParserTree instances for a given class. Every ParserTree instance contains references
  * to the respective children in the tree, descending from the given class, however, does not contain references to
  * the parent of a class. Parent hierarchy is circumstantial and thus constructed on a per-Parser basis.
  */
 private static final LinkedHashMap<Class<?>, Parser<?>> PARSER_CACHE;

 static {
  PARSER_CACHE = new LinkedHashMap<>();
 }

 private final Class<T> target;
 private final LinkedHashMap<Field, LinkedList<ConditionalAnnotation>> conditions;

 private Parser(final Class<T> target, final LinkedHashMap<Field, LinkedList<ConditionalAnnotation>> conditions) {
  this.target = target;
  this.conditions = conditions;
 }

 //TODO multithread field type processing
 //TODO caching functionality
 //TODO remove recursive branching
 //TODO superclass parser construction
 public static <T> Parser<T> construct(final Class<T> clazz) {
  final boolean isPrimitive = ParsablePrimitives.contains(clazz);
  if (!(AbstractSyntaxTree.class.isAssignableFrom(clazz) || isPrimitive)) {
   throw new IllegalArgumentException(
    "The class '" +
    clazz.getCanonicalName() +
    "' is not primitive and does not extend '" +
    AbstractSyntaxTree.class.getCanonicalName() +
    "'!"
   );
  }
  final LinkedHashMap<Field, LinkedList<ConditionalAnnotation>> conditions = new LinkedHashMap<>();
  Parser<T> parser = new Parser<>(clazz, conditions);
  //Check for default constructor
  if (!isPrimitive) {
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
   //TODO Pal support for generic parameter forwarding -- Class<(H)> => LinkedList<ConditionalAnnotation<(H)>>
   //Process field annotations
   final LinkedList<ConditionalAnnotation> fieldConditions = new LinkedList<>();
   for (final Annotation annotation : field.getAnnotations()) {
    final ConditionalAnnotation condition = ConditionalAnnotation.generateCondition(annotation);
    if (condition != null) {
     fieldConditions.add(condition);
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
   IParser pt = ParsablePrimitives.resolve(fieldType);
   if (pt == null) {
    //Process class
    assertFieldCompliance(field, clazz, fieldType);
//    pt = new Parser(clazz, field, fieldConditions);
   }

  }

  return parser;
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

 @Override
 public Parser<T> clone() {
  //TODO clone conditions
//  return new Parser<>(target);
  return null;
 }

 @Override
 public boolean equals(Object obj) {
  if (obj instanceof Parser) {
   final Parser<T> parser = (Parser<T>)obj;
   if (parser.target.equals(target)) {
    return true;
   }
  }
  return false;
 }

 @Override
// public <C, P extends IParser<C>> Parsed<P, C> parse(Parsed pt, byte[] data) {
 public Parsed<Parser<T>, T> parse(Parsed pt, byte[] data) {
  final T inst;
  try {
   inst = target.newInstance();
  } catch (final Throwable t) {
   throw new RuntimeException("Could not instantiate '" + target.getCanonicalName() + "'!", t);
  }
  //Start at the top-level superclass (excluding java.lang.Object)
  Parser<T> parent = this;
  while (getParent() != null) {
   parent = getParent();
  }
  final long index[] = { 0L };
  parent.traverseTree((TreeTraversalFunction<Parser<T>, LinkedList<Parser<T>>>)(root, child) -> {
   if (!root.equals(child)) {

   }
   return child;
  });
  return new Parsed<>(this, inst, 0L);
 }

 @Override
 public Class<T> getType() {
  return target;
 }

 public T parse(final InputStream is) throws IOException {
  return parse(null, IOUtils.readStream(is)).parsed;
 }
}
