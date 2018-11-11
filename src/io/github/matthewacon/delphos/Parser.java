package io.github.matthewacon.delphos;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.matthewacon.delphos.api.AbstractSyntaxTree;
import io.github.matthewacon.delphos.api.AnnotationCondition;
import io.github.matthewacon.delphos.api.ParsablePrimitives;
import io.github.matthewacon.delphos.api.ParserFrame;
import io.github.matthewacon.delphos.class_parser.FieldModifiers;
import io.github.matthewacon.pal.util.ClassUtils;
import io.github.matthewacon.pal.util.IOUtils;

public final class Parser<T extends AbstractSyntaxTree> {
 //Type annotation cache (not applicable to encapsulated property annotations)
// private static final HashMap<Class<?>, LinkedTreeMap<ParserFrame<?>>> TYPE_CACHE;
 private static final HashMap<Class<?>, LinkedList<ParserFrame<?>>> TYPE_CACHE;

 static {
  TYPE_CACHE = new HashMap<>();
 }

 private final Class<T> targetTree;
// private final LinkedTreeMap<ParsableType<?>> apt;
 private final LinkedList<ParserFrame<?>> apt;

 public Parser(final Class<T> targetTree) {
  if (!AbstractSyntaxTree.class.isAssignableFrom(targetTree)) {
   throw new IllegalArgumentException(
    "'" +
    targetTree.getCanonicalName() +
    "' does not extend '" +
    AbstractSyntaxTree.class.getCanonicalName() +
    "'!"
   );
  }
  this.targetTree = targetTree;
  LinkedList<ParserFrame<?>> apt = TYPE_CACHE.get(targetTree);
  if (apt == null) {
   apt = new LinkedList<>();
   //Build class hierarchy
   final Class<? super T>[] superClasses = ClassUtils.resolveSuperClasses(targetTree);
   //Filter out ineligible fields
   final List<Field> fields = Arrays
    .asList(targetTree.getDeclaredFields())
    .stream()
    .filter(field -> {
     final int mods = field.getModifiers();
     return (mods | FieldModifiers.ACC_STATIC.value) != mods;
    })
    .collect(Collectors.toList());
//  final Field[] fields = clazz.getDeclaredFields();
   //Abstract Parser Tree
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
       "The annotation '" +
        annotation.getClass().getCanonicalName() +
        "' is not a valid delphos annotation!"
      );
//     throw new IllegalArgumentException();
     }
    }
    final Class<?> fieldType = field.getType();
    if (fieldType.isArray()) {
     //TODO process array dim annotations
     //Each dimension must either have a pre-defined length specifier (@ArrayLength) or have some beginning (and / or)
     //end specifier (@EncapsulatedBy) to determine the length of each array
     Class<?> baseFieldType = field.getType();
     while ((baseFieldType = baseFieldType.getComponentType()).isArray());
     assertFieldCompliance(field, targetTree, baseFieldType);

    }
    //Construct APT for primitive or class
    if (fieldType.isPrimitive()) {
     //Process primitives
     apt.add(new ParserFrame<>(
      ParsablePrimitives.resolve(fieldType),
      conditions
     ));
    } else {
     //Process classes
     assertFieldCompliance(field, targetTree, fieldType);
    }
   }
  }
  this.apt = apt;
  //TODO Post-parsing verification
 }

 //what is this name
 private static void assertFieldCompliance(
  final Field field,
  final Class<? extends AbstractSyntaxTree> targetTree,
  final Class<?> clazz
 ) {
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

// public static <T extends AbstractSyntaxTree> LinkedTreeMap<...> buildParserTree(final Class<T> clazz) {
 public static <T extends AbstractSyntaxTree> Parser<T> buildParserTree(final Class<T> clazz) {
  return null;
 }

 public T parse(final InputStream is) throws IOException {
  final byte[] data = IOUtils.readStream(is);
  int index = 0;
  return null;
 }
}
