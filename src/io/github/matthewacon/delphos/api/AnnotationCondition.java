package io.github.matthewacon.delphos.api;

import java.lang.annotation.Annotation;

import io.github.matthewacon.delphos.api.annotations.field.Constants;

import static io.github.matthewacon.pal.util.LambdaUtils.*;

public interface AnnotationCondition<T extends Annotation> {
 //TODO Doc - Will be provided with an instance of the annotation 'T' and non-zero length byte[]
 boolean isValid(final T annotation, final byte[] data);

 //TODO Doc - returns null if annotation type is not matched
 static <T extends Annotation> AnnotationCondition<T> generateCondition(final T annotation) {
  final AnnotationCondition[] condition = new AnnotationCondition[1];
  cswitch(annotation,
   ccase(Constants.ConstantBoolean.class,
    cInt -> {
     condition[0] = (AnnotationCondition<Constants.ConstantBoolean>)(ann, data) -> (data[0] & 1) == (ann.value() ? 1 : 0);
    }
   ),
   ccase(Constants.ConstantByte.class,
    cByte -> {
     condition[0] = (AnnotationCondition<Constants.ConstantByte>)(ann, data) -> data[0] == ann.value();
    }
   ),
   ccase(Constants.ConstantShort.class,
    cShort -> {
     condition[0] = (AnnotationCondition<Constants.ConstantShort>)(ann, data) -> ((short)(data[1] << 8 | data[0])) == ann.value();
    }
   ),
   ccase(Constants.ConstantInt.class,
    cInt -> {
     //TODO
     condition[0] = (AnnotationCondition<Constants.ConstantInt>)(ann, data) -> false;
    }
   ),
   ccase(Constants.ConstantFloat.class,
    cFloat -> {
     //TODO
     condition[0] = (AnnotationCondition<Constants.ConstantFloat>)(ann, data) -> false;
    }
   ),
   ccase(Constants.ConstantLong.class,
    cLong -> {
     //TODO
     condition[0] = (AnnotationCondition<Constants.ConstantLong>)(ann, data) -> false;
    }
   ),
   ccase(Constants.ConstantDouble.class,
    cDouble -> {
     //TODO
     condition[0] = (AnnotationCondition<Constants.ConstantDouble>)(ann, data) -> false;
    }
   ),
   ccase(Constants.ConstantString.class,
    cString -> {
     condition[0] = (AnnotationCondition<Constants.ConstantString>)(ann, data) -> ann.value().equals(new String(data));
    }
   )
   //Default case - condition[0] will be null
   //Not necessarily fatal, consider simply returning null
//   ccase(null,
//    cDefault -> {
//     throw new IllegalArgumentException("The annotation '" + cDefault.getClass() + "' is not a ");
//    }
//   )
  );
  return condition[0];
 }
}
