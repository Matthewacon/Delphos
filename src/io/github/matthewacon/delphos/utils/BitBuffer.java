package io.github.matthewacon.delphos.utils;

import java.util.Arrays;

//TODO Constructor overloads for all primitives
//TODO Consumer functions for all primitives
//TODO Change to operation stack model to combine multiple operations and add parallel operators
public final class BitBuffer {
 public interface BitTransformationFunction {
  byte transform(final byte data, final byte transform, final int bitIndex);
 }

 private byte[] data;
 private long bitLength;

 public BitBuffer(final byte[] data) {
  this(data, data.length * 8L);
 }

 public BitBuffer(final byte[] data, final long bitLength) {
  this.data = data;
  this.bitLength = bitLength;
 }

 public BitBuffer transform(
  final byte[] transformationData,
  final long transformationBitLength,
  final BitTransformationFunction btf
 ) {
  if (transformationData.length > 0 && transformationBitLength > 0) {
   int
    bitIndex = 0,
    byteIndex = 0,
    transformIndex = 0;
   for (; byteIndex < data.length && (byteIndex * 8L + bitIndex) < bitLength; bitIndex++) {
    if (bitIndex == 8) {
     bitIndex = 0;
     byteIndex += 1;
     if (transformIndex * 8L < transformationBitLength) {
      transformIndex += 1;
     } else {
      transformIndex = 0;
     }
    }
//    data[byteIndex] |= (transformationData[orIndex] << bitIndex);
    data[byteIndex] = btf.transform(data[byteIndex], transformationData[transformIndex], bitIndex);
   }
  } else {
   //TODO merge into main loop
   //TODO bitLength check
   for (int byteIndex = 0; byteIndex < data.length; byteIndex++) {
    data[byteIndex] = btf.transform(data[byteIndex], (byte)0, 0);
   }
  }
//  if (bitLength > 0) {
//   final TransformationResult result = btf.transform(data);
//   this.data = result.data;
//   this.bitLength = result.bitLength;
//  } else {
//   throw new IllegalArgumentException("Stream is empty!");
//  }
  return this;
 }

 public BitBuffer shiftLeft(final long shift, final boolean pad) {
  if (shift != 0L) {
   if (shift > 0) {
    //Shift left
    double dShift = shift / 8D;
    final long
     byteShift = (long)Math.floor(dShift),
     bitShift = ((long)(dShift - byteShift) * 8L);

    if (pad) {

    } else {

    }
   } else {
    //Shift right
    if (pad) {

    } else {

    }
   }
  }
  return this;
 }

 public BitBuffer shiftLeft(final long shift) {
  return shiftLeft(shift, false);
 }

 public BitBuffer shiftRight(final long shift, final boolean pad) {
  return shiftLeft(-shift, pad);
 }

 public BitBuffer shiftRight(final long shift) {
  return shiftRight(shift, false);
 }

 public BitBuffer or(final long orBitLength, final byte... or) {
  return transform(
   or,
   orBitLength,
   (data, transform, bitIndex) -> (byte)((data >> bitIndex) | (transform >> bitIndex))
  );
//  if (or.length > 0 && orBitLength > 0) {
//   for (int bitIndex = 0, byteIndex = 0, orIndex = 0; byteIndex < data.length; bitIndex++) {
//    if (bitIndex == 8) {
//     bitIndex = 0;
//     byteIndex += 1;
//     if (orIndex * 8L < orBitLength) {
//      orIndex += 1;
//     } else {
//      orIndex = 0;
//     }
//    }
//    data[byteIndex] |= (or[orIndex] << bitIndex);
//   }
//  }
 }

 public BitBuffer and(final long andBitLength, final byte... and) {
  return transform(
   and,
   andBitLength,
   (data, transform, bitIndex) -> (byte)((data >> bitIndex) & (transform >> bitIndex))
  );
//  if (and.length > 0 && andBitLength > 0) {
//   for (int bitIndex = 0, byteIndex = 0, andIndex = 0; byteIndex < data.length; bitIndex++) {
//    if (bitIndex == 8) {
//     bitIndex = 0;
//     byteIndex += 1;
//     andIndex += 1;
//     if (andIndex < and.length) {
//      andIndex = 0;
//     }
//    }
//    data[byteIndex] &= (and[andIndex] << bitIndex);
//   }
//  }
 }

 public BitBuffer xor(final long xorBitLength, final byte... xor) {
  return transform(
   xor,
   xorBitLength,
   (data, transform, bitIndex) -> (byte)((data >> bitIndex) ^ (transform >> bitIndex))
  );
 }

 public BitBuffer not() {
  return transform(
   new byte[0],
   0L,
   (data, transform, bitIndex) -> (byte)~data
  );
//  return transform(data -> {
//   //Even byte not
//   for (int i = 0; i < byteLength; i++) {
//    data[i] = (byte)(~data[i]);
//   }
//   //Bit not
//   if (bitRemainder > 0) {
//    for (int i = 0; i < bitRemainder; i++) {
//     data[data.length - 1] |= (byte)~(data[data.length - 1] >> i);
////     data[data.length - 1] >>= i;
//    }
//   }
//   return new TransformationResult(bitLength, data);
//  });
 }

 private void appendBits(final long bitLength, final byte... data) {
  this.bitLength += bitLength;
  final int
   oldLength = this.data.length,
   newLength = (int)Math.ceil(this.bitLength / 8F);
  final byte[] newData = Arrays.copyOf(this.data, newLength);
  this.data = newData;
  for (int i = oldLength; i < newLength; i++) {
   newData[i] = data[i - oldLength];
  }
 }

 public BitBuffer appendBoolean(final int count, final boolean... b) {
  final byte[] data = new byte[(int)Math.ceil(count / 8F)];
  for (int bIndex = 0, bitIndex = 0, byteIndex = 0; bIndex < count; bIndex++, bitIndex++) {
   if (bitIndex == 8) {
    byteIndex += 1;
    bitIndex = 0;
   }
   data[byteIndex] |= (byte)(getBytes(b[bIndex])[0] >> bitIndex);
  }
  appendBits(count, data);
  return this;
 }

 public BitBuffer consumeBoolean(final boolean[] bs, final int count) {

  return this;
 }

 public BitBuffer appendByte(final int count, final byte[] bytes) {
  //TODO
  return this;
 }

 public BitBuffer consumeByte(final byte[] data, final int count) {
  //TODO
  return this;
 }

 //Static primitive utilities
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
}