package io.github.matthewacon.delphos.utils;

//TODO endianness detection
//TODO utility parsing for all primitive types
//TODO stream equivalents for parsing primitives out of the BitStream
public final class BitStream {
 public interface ByteTransformationFunction {
  byte transform(final byte b);
 }

 private byte[] data;

 public BitStream(final byte[] data) {
  this.data = data;
 }

 public BitStream transform(final ByteTransformationFunction btf) {
  data = BitStream.transform(data, btf);
  return this;
 }

 public BitStream shiftLeft(final long shift, final boolean pad) {
  data = BitStream.shiftLeft(data, shift, pad);
  return this;
 }

 public BitStream shiftLeft(final long shift) {
  return shiftLeft(shift, false);
 }

 public BitStream shiftRight(final long shift, final boolean pad) {
  data = BitStream.shiftRight(data, shift, pad);
  return this;
 }

 public BitStream shiftRight(final long shift) {
  return shiftRight(shift, false);
 }

 public BitStream or(final byte or) {
  data = BitStream.or(data, or);
  return this;
 }

 public BitStream and(final byte and) {
  data = BitStream.and(data, and);
  return this;
 }

 public BitStream xor(final byte xor) {
  data = BitStream.xor(data, xor);
  return this;
 }

 public BitStream not() {
  data = BitStream.not(data);
  return this;
 }

 //TODO Finish
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

 //TODO Binary operators on entire bit sets (accept byte[] and bit length as arguments)
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
