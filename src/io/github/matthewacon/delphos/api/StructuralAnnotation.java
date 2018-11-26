package io.github.matthewacon.delphos.api;

import io.github.matthewacon.delphos.Parsed;
import io.github.matthewacon.delphos.ParserException;
import io.github.matthewacon.delphos.ParserTree;
import io.github.matthewacon.delphos.api.annotations.structural.EncapsulatedBy;

import java.lang.annotation.Annotation;

import static io.github.matthewacon.pal.util.LambdaUtils.*;

public interface StructuralAnnotation<T> {
 default T parse(final ParserTree<T> parserTree, final Parsed<ParserTree<T>, T> parsed, final byte[] data) {

  return null;
 }

 long getBitLength(final ParserTree<T> parserTree, final Parsed<ParserTree<T>, T> parsed, final byte[] data);

 static <T extends Annotation> StructuralAnnotation generateStructure(final T annotation) {
  final StructuralAnnotation[] structure = new StructuralAnnotation[1];
  cswitch(annotation,
   ccase(
    EncapsulatedBy.class,
    encapsulatedBy -> structure[0] = (parserTree, parsed, data) -> {
     String sData = new String(data);
     int
      openIndex = sData.indexOf(encapsulatedBy.prefix()),
      closeIndex = -1,
      lastCloseIndex,
      opened = 1;
     //The opening marker should be the first element in the string
     if (openIndex != 0) {
      //TODO
      throw new ParserException("Misaligned encapsulation prefix!");
     }
     do {
      lastCloseIndex = closeIndex;
      openIndex = sData.indexOf(encapsulatedBy.prefix());
      if (openIndex != -1) {
       opened += 1;
      } else {
       closeIndex = sData.indexOf(encapsulatedBy.postfix());
      }
      if (closeIndex != -1) {
       opened -= 1;
      }
      sData = sData.substring(openIndex != -1 ? openIndex : closeIndex);
     } while (openIndex < closeIndex && opened > 0);
     if (opened != 0) {
      throw new ParserException("Unmatched pair of encapsulation markers!");
     }
     //Calculate the length of the encapsulated body, in bits
     sData = new String(data).substring(lastCloseIndex - 1);
     return 8L * sData.getBytes().length;
    }
   )
  );

  return structure[0];
 }
}
