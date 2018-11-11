package io.github.matthewacon.delphos.api;

public enum ParsablePrimitives implements ParsableType {
 BOOLEAN(boolean.class, 1l),
 BYTE(byte.class, 8l),
 SHORT(short.class, 16l),
 INTEGER(int.class, 32l),
 FLOAT(float.class, 32l),
 LONG(long.class, 64l),
 DOUBLE(double.class, 64l);

 private final Class<?> type;
 private final long bitLength;

 ParsablePrimitives(final Class<?> type, final long bitLength) {
  this.type = type;
  this.bitLength = bitLength;
 }

 @Override
 public long getBitLength() {
  return bitLength;
 }

 @Override
 public Class getType() {
  return type;
 }

 public static ParsablePrimitives resolve(final Class<?> clazz) {
  for (final ParsablePrimitives pp : ParsablePrimitives.values()) {
   if (pp.type.equals(clazz)) {
    return pp;
   }
  }
  //TODO return null instead?
  throw new IllegalArgumentException(
   "'" +
   clazz.getCanonicalName() +
   "' does not match any parsable primitives!"
  );
 }
}
