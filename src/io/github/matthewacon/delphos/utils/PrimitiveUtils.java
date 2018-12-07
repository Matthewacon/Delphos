package io.github.matthewacon.delphos.utils;

import io.github.matthewacon.delphos.api.ParsablePrimitives;

import static io.github.matthewacon.delphos.utils.Arrays.*;

import static io.github.matthewacon.pal.util.LambdaUtils.rcase;
import static io.github.matthewacon.pal.util.LambdaUtils.rswitch;

public final class PrimitiveUtils {
 private PrimitiveUtils() {}

 public static byte[] getBytes(final Object o) {
  final Class<?> clazz = o.getClass();
  if (!ParsablePrimitives.contains(clazz)) {
   throw new IllegalArgumentException(
    "The class '" +
     clazz.getCanonicalName() +
     "' is not a primitive or boxed equivalent!"
   );
  }
  return rswitch(o,
   rcase(Boolean.class, b -> getBytes((boolean)o)),
   rcase(Byte.class, b -> getBytes((byte)b)),
   rcase(Character.class, c -> getBytes((char)c)),
   rcase(Short.class, s -> getBytes((short)s)),
   rcase(Integer.class, i -> getBytes((int)i)),
   rcase(Float.class, f -> getBytes((float)f)),
   rcase(Long.class, l -> getBytes((long)l)),
   rcase(Double.class, d -> getBytes((double)d))
  );
 }

 public static byte[] getBytes(final boolean b) {
  return new byte[] { (byte)(b ? 1 : 0) };
 }

 public static byte[] getBytes(final byte b) {
  return new byte[] { b };
 }

 public static byte[] getBytes(final char c) {
  return new byte[] {
   (byte)(c >> 8),
   (byte)c
  };
 }

 public static byte[] getBytes(final short s) {
  return new byte[] {
   (byte)(s >> 8),
   (byte)s
  };
 }

 public static byte[] getBytes(final int i) {
  return new byte[] {
   (byte)(i >> 24),
   (byte)(i >> 16),
   (byte)(i >> 8),
   (byte)i
  };
 }

 public static byte[] getBytes(final float f) {
  return getBytes(Float.floatToRawIntBits(f));
 }

 public static byte[] getBytes(final long l) {
  return new byte[] {
   (byte)(l >> 56),
   (byte)(l >> 48),
   (byte)(l >> 40),
   (byte)(l >> 32),
   (byte)(l >> 24),
   (byte)(l >> 16),
   (byte)(l >> 8),
   (byte)l
  };
 }

 public static byte[] getBytes(final double d) {
  return getBytes(Double.doubleToRawLongBits(d));
 }

 public static boolean[] parseBooleans(final int count, final byte[] booleans) {
  ensureArraySize(0, count, booleans);
  final boolean[] parsed = new boolean[count];
  for (int bitIndex = 0; bitIndex < count; bitIndex++) {
   parsed[bitIndex] = (booleans[bitIndex / 8] << (int)(bitIndex % 8F)) == 0;
  }
  return parsed;
 }

 public static byte[] parseBytes(final int count, byte[] bytes) {
  ensureArraySize(0, count, bytes);
  if (count != bytes.length) {
   final byte[] parsed = new byte[count];
   for (int i = 0; i < count; i++) {
    parsed[i] = bytes[i];
   }
   bytes = parsed;
  }
  return bytes;
 }

 public static char[] parseChars(final int count, final byte[] chars) {
  ensureArraySize(0, count * 2, chars);
  final char[] parsed = new char[count];
  for (int byteIndex = 0; byteIndex < count * 2; byteIndex++) {
   parsed[byteIndex / 2] |= (chars[byteIndex] << (8 * (byteIndex % 2)));
  }
  return parsed;
 }

 public static short[] parseShorts(final int count, final byte[] shorts) {
  ensureArraySize(0, count * 2, shorts);
  final short[] parsed = new short[count];
  for (int byteIndex = 0; byteIndex < count * 2; byteIndex++) {
   parsed[byteIndex / 2] |= (shorts[byteIndex] << (8 * (byteIndex % 2))) & 0xFF;
  }
  return parsed;
 }

 public static int[] parseInts(final int count, final byte[] ints) {
  ensureArraySize(0, count * 4, ints);
  final int[] parsed = new int[count];
  for (int byteIndex = 0; byteIndex < count * 4; byteIndex++) {
   parsed[byteIndex / 4] |= (ints[byteIndex] << (8 * (byteIndex % 4))) & 0xFFFF;
  }
  return parsed;
 }

 public static float[] parseFloats(final int count, final byte[] floats) {
  final int[] iParsed = parseInts(count, floats);
  final float[] parsed = new float[count];
  for (int i = 0; i < count; i++) {
   parsed[i] = Float.intBitsToFloat(iParsed[i]);
  }
  return parsed;
 }

 public static long[] parseLongs(final int count, final byte[] longs) {
  ensureArraySize(0, count * 8, longs);
  final long[] parsed = new long[count];
  for (int byteIndex = 0; byteIndex < count * 8; byteIndex++) {
   parsed[byteIndex / 8] |= (((long)longs[byteIndex]) << (8 * (byteIndex % 8)));
  }
  return parsed;
 }

 public static double[] parseDoubles(final int count, final byte[] doubles) {
  final long[] lParsed = parseLongs(count, doubles);
  final double[] parsed = new double[count];
  for (int i = 0; i < count; i++) {
   parsed[i] = Double.longBitsToDouble(lParsed[i]);
  }
  return parsed;
 }
}
