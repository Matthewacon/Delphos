package io.github.matthewacon.delphos.utils;

public final class BitUtils {
 public interface ByteTransformationFunction {
  byte transform(final byte b);
 }

 //TODO
 public static byte[] shiftLeft(final byte[] data, final long shift, final boolean pad) {
  if (shift == 0) {
   return data;
  } else if (shift < 0) {

  } else {
   final long bitLength = 8L * data.length;
   float f = shift % 8;
   final long remainingBitShift = bitLength - (long)Math.floor(f);
   if (pad) {

   } else {

   }
  }
  return null;
 }

 public static byte[] shiftLeft(final byte[] data, final long shift) {
  return shiftLeft(data, shift, false);
 }

 public static byte[] shiftRight(final byte[] data, final long shift, final boolean pad) {
  return shiftLeft(data, -shift, pad);
 }

 public static byte[] shiftRight(final byte[] data, final long shift) {
  return shiftRight(data, shift, false);
 }

 public static byte[] transform(final byte[] data, final ByteTransformationFunction btf) {
  for (int i = 0; i < data.length; i++, data[i] = btf.transform(data[i]));
  return data;
 }

 public static byte[] or(final byte[] data, final byte or) {
  return transform(data, b -> (byte)(b | or));
 }

 public static byte[] and(final byte[] data, final byte and) {
  return transform(data, b -> (byte)(b & and));
 }

 public static byte[] xor(final byte[] data, final byte xor) {
  return transform(data, b -> (byte)(b ^ xor));
 }

 public static byte[] not(final byte[] data) {
  return transform(data, b -> (byte)(~b));
 }
}
