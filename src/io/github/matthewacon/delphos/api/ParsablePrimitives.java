package io.github.matthewacon.delphos.api;

import io.github.matthewacon.delphos.Parsed;

//Specific parser implementations for every primitive, and respective boxed, types in Java
public enum ParsablePrimitives implements IParser {
 BOOLEAN(boolean.class, Boolean.class) {
  @Override
  public Parsed<IParser<Boolean>, Boolean> parse(final Parsed pt, final byte[] data) {
   return new Parsed<>(BOOLEAN, (data[0] & 1) == data[0], 1L);
  }
 },
 BYTE(byte.class, Byte.class) {
  @Override
  public Parsed<IParser<Byte>, Byte> parse(final Parsed pt, final byte[] data) {
   return new Parsed<>(BYTE, data[0], 8L);
  }
 },
 //TODO write unit tests to ensure the endianness matches correctly
 CHAR(char.class, Character.class) {
  @Override
  public Parsed<IParser<Character>, Character> parse(Parsed pt, byte[] data) {
   return new Parsed<>(
    CHAR,
    (char)((data[0] & 0xFF) << 8 | data[1]),
   16L
   );
  }
 },
 SHORT(short.class, Short.class) {
  @Override
  public Parsed<IParser<Short>, Short> parse(final Parsed pt, final byte[] data) {
   return new Parsed<>(
    SHORT,
    (short)(data[0] | (data[1] & 0xFF) << 8),
    16L
   );
  }
 },
 INTEGER(int.class, Integer.class) {
  @Override
  public Parsed<IParser<Integer>, Integer> parse(final Parsed pt, final byte[] data) {
   return new Parsed<>(
    INTEGER,
    data[0] << 24 | (data[1] & 0xFF) << 16 | (data[2] & 0xFF) << 8 | (data[3] & 0xFF),
    32L
   );
  }
 },
 FLOAT(float.class, Float.class) {
  @Override
  public Parsed<IParser<Float>, Float> parse(final Parsed pt, final byte[] data) {
   return new Parsed<>(
    FLOAT,
    Float.intBitsToFloat(data[0] << 24 | (data[1] & 0xFF) << 16 | (data[2] & 0xFF) << 8 | (data[3] & 0xFF)),
    32L
   );
  }
 },
 LONG(long.class, Long.class) {
  @Override
  public Parsed<IParser<Long>, Long> parse(final Parsed pt, final byte[] data) {
   //TODO
   return new Parsed<>(LONG, null, 64L);
  }
 },
 DOUBLE(double.class, Double.class) {
  @Override
  public Parsed<IParser<Double>, Double> parse(final Parsed pt, final byte[] data) {
   //TODO
   return new Parsed<>(DOUBLE, null, 64L);
  }
 };

 private final Class<?>[] types;

 ParsablePrimitives(final Class<?>... types) {
  this.types = types;
 }

 @Override
 public Class getType() {
  return types[0];
 }

 public Class[] equivalents() {
  return types;
 }

 //Returns null if the supplied class is not a primitive or boxed equivalent
 public static ParsablePrimitives resolve(final Class<?> clazz) {
  for (final ParsablePrimitives pp : ParsablePrimitives.values()) {
   for (final Class<?> type : pp.types) {
    if (type.equals(clazz)) {
     return pp;
    }
   }
  }
  return null;
 }

 public static boolean contains(final Class<?> clazz) {
  return resolve(clazz) != null;
 }
}
