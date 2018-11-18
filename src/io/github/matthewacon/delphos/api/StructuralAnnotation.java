package io.github.matthewacon.delphos.api;

import io.github.matthewacon.delphos.Parsed;
import io.github.matthewacon.delphos.Parser;
import io.github.matthewacon.delphos.api.annotations.structural.EncapsulatedBy;

import java.lang.annotation.Annotation;

import static io.github.matthewacon.pal.util.LambdaUtils.*;

public interface StructuralAnnotation<T> {
 long getBitLength(final Parser<T> parser, final Parsed<Parser<T>, T> parsed, final byte[] data);

 static <T extends Annotation> StructuralAnnotation generateStructure(final T annotation) {
  final StructuralAnnotation[] structure = new StructuralAnnotation[1];
  cswitch(annotation,
   ccase(
    EncapsulatedBy.class,
    encapsulatedBy -> structure[0] = (parser, parsed, data) -> {
     String sData = new String(data);
     int
      index = sData.indexOf(encapsulatedBy.prefix()),
      open = 1;
     if (index == -1) {
      //TODO
      throw new RuntimeException();
     }
     for (; index < sData.length(); index = sData.indexOf(encapsulatedBy.prefix())) {
      sData = sData.substring(index);
     }

     return 0L;
    }
   )
  );

  return structure[0];
 }
}
