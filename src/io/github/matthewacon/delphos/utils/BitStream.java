package io.github.matthewacon.delphos.utils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.Arrays;
import java.util.stream.Stream;

import static io.github.matthewacon.delphos.utils.Arrays.*;
import static io.github.matthewacon.delphos.utils.PrimitiveUtils.*;

//TODO Constructor overloads for all primitives
//TODO Consumer functions for all primitives
//TODO Change to operation stack model to combine multiple operations and add parallel operators
//TODO implement Stream
//TODO use longs internally instead of bytes
//public final class BitStream implements Stream, Closeable {
public final class BitStream {
 public interface BitTransformationFunction {
  byte transform(final byte data, final byte transform, final int bitIndex);
 }

 private byte[] data;
 private long bitLength;

 public BitStream(final byte[] data) {
  this(data, data.length * 8L);
 }

 public BitStream(final byte[] data, final long bitLength) {
  this.data = data;
  this.bitLength = bitLength;
 }

 public BitStream transform(final byte[] tData, final long tBitLength, final BitTransformationFunction btf) {
  if (tData.length > 0 && tBitLength > 0) {
   int
    bitIndex = 0,
    byteIndex = 0,
    transformIndex = 0;
   for (; byteIndex < data.length && (byteIndex * 8L + bitIndex) < bitLength; bitIndex++) {
    if (bitIndex == 8) {
     bitIndex = 0;
     byteIndex += 1;
     if (transformIndex * 8L < tBitLength) {
      transformIndex += 1;
     } else {
      transformIndex = 0;
     }
    }
    data[byteIndex] = btf.transform(data[byteIndex], tData[transformIndex], bitIndex);
   }
  } else {
   //TODO merge into main loop
   //TODO bitLength check
   for (int byteIndex = 0; byteIndex < data.length; byteIndex++) {
    data[byteIndex] = btf.transform(data[byteIndex], (byte)0, 0);
   }
  }
  return this;
 }

 public BitStream shiftLeft(final long shift, final boolean pad) {
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

 public BitStream shiftLeft(final long shift) {
  return shiftLeft(shift, false);
 }

 public BitStream shiftRight(final long shift, final boolean pad) {
  return shiftLeft(-shift, pad);
 }

 public BitStream shiftRight(final long shift) {
  return shiftRight(shift, false);
 }

 public BitStream or(final long orBitLength, final byte... or) {
  return transform(
   or,
   orBitLength,
   (data, transform, bitIndex) -> (byte)(data | ((data & (1 << bitIndex)) | (transform & (1 << bitIndex))))
  );
 }

 public BitStream and(final long andBitLength, final byte... and) {
  return transform(
   and,
   andBitLength,
   (data, transform, bitIndex) -> (byte)(data | (data & (1 << bitIndex)) & (transform & (1 << bitIndex)))
  );
 }

 public BitStream xor(final long xorBitLength, final byte... xor) {
  return transform(
   xor,
   xorBitLength,
   (data, transform, bitIndex) -> (byte)(data | ((data & (1 << bitIndex)) ^ (transform & (1 << bitIndex))))
  );
 }

 public BitStream not() {
  return transform(
   new byte[0],
   0L,
   (data, transform, bitIndex) -> (byte)~data
  );
 }

 private void ensureCapacity(final long bitLength) {
  if (bitLength < 0) {
   throw new BufferUnderflowException();
  } else if (bitLength > this.bitLength) {
   throw new BufferOverflowException();
  }
 }

 public BitStream appendData(final long dataBitLength, final int start, final int end, final Object... objs) {
  ensureArraySize(start, end, objs);
  final int prevDataLength = data.length;
  final byte[] newData = new byte[prevDataLength + (int)Math.ceil((dataBitLength * (end - start)) / 8F)];
  //Copy existing data into new array
  for (int i = 0; i < data.length; i++) {
   newData[i] = data[i];
  }
  data = newData;
  int
   dataIndex = prevDataLength,
   dataBitIndex = (int)(bitLength % 8),
   objIndex = 0,
   objBitIndex = 0,
   objByteIndex = 0;
  byte[] objData = getBytes(objs[objByteIndex]);
  //TODO check condition validity
  for (; objIndex < objs.length; objBitIndex++, dataBitIndex++) {
   if (objBitIndex == 8) {
    objBitIndex = 0;
    objByteIndex += 1;
    if (objByteIndex == objData.length) {
     objByteIndex = 0;
     objIndex += 1;
     objData = getBytes(objs[objIndex]);
    }
   }
   if (dataBitIndex == 8) {
    dataBitIndex = 0;
    dataIndex += 1;
   }
   data[dataIndex] |= ((objData[objByteIndex] & (1 << objBitIndex)) << dataBitIndex);
  }
  return this;
 }

 public BitStream consumeData(final long typeBitLength, final int start, final int end, final byte[] out) {
  //TODO
  ensureArraySize(start, end, out);
  final long outBitLength = (end - start) * typeBitLength;
  ensureCapacity(outBitLength);
  final int newCapacity = data.length - (int)Math.ceil((bitLength - outBitLength) % 8F);
  int
   dataByteIndex = newCapacity,
   dataBitIndex = (int)(bitLength % 8F),
   outByteIndex = 0,
   outBitIndex = (int)(outBitLength % 8F);
  for (; outBitIndex * typeBitLength + outBitIndex < outBitLength; outBitIndex++, dataBitIndex++) {
   if (outBitIndex == 8) {
    outBitIndex = 0;
    outByteIndex += 1;
   }
   if (dataBitIndex == 8) {
    dataBitIndex = 0;
    dataByteIndex += 1;
   }
   out[outByteIndex] |= (data[dataByteIndex] & (1 << dataBitIndex)) << outBitIndex;
  }
  bitLength -= outBitLength;
  final byte[] newData = new byte[newCapacity];
  for (int i = 0; i < newCapacity; i++) {
   newData[i] = data[i];
  }
  data = newData;
  return this;
 }

 public BitStream appendBooleans(final boolean... booleans) {
  appendData(1, 0, booleans.length, booleans);
  return this;
 }

 public BitStream consumeBooleans(final boolean[] booleans, final int start, final int end) {
  ensureArraySize(start, end, booleans);
  final int size = end - start;
  ensureCapacity(size);
  final byte[] data = new byte[(int)Math.ceil(size / 8F)];
  consumeData(1, start, end, data);
  final boolean[] parsed = parseBooleans(size, data);
  for (int i = 0; i < size; i++) {
   booleans[start + i] = parsed[i];
  }
  return this;
 }

 public BitStream appendBytes(final byte... bytes) {
  appendData(8, 0, bytes.length, bytes);
  return this;
 }

 public BitStream consumeBytes(final byte[] bytes, final int start, final int end) {
  ensureArraySize(start, end, bytes);
  ensureCapacity(end - start);
  consumeData(8, start, end, bytes);
  return this;
 }

 public BitStream appendChars(final char... chars) {
  appendData(16, 0, chars.length, chars);
  return this;
 }

 public BitStream consumeChars(final char[] chars, final int start, final int end) {
  ensureArraySize(start, end, chars);

  return this;
 }

 public BitStream appendShorts(final short... shorts) {
  appendData(16, 0, shorts.length, shorts);
  return this;
 }

 public BitStream consumeShorts(final short[] shorts, final int start, final int end) {
  ensureArraySize(start, end, shorts);
  return this;
 }

 public BitStream appendInteger(final int... ints) {
  appendData(32, 0, ints.length, ints);
  return this;
 }

 public BitStream consumeInteger(final int[] ints, final int start, final int end) {
  ensureArraySize(start, end, ints);
  return this;
 }

 public BitStream appendFloats(final float... floats) {
  appendData(32, 0, floats.length, floats);
  return this;
 }

 public BitStream consumeFloats(final float[] floats, final int start, final int end) {
  ensureArraySize(start, end, floats);
  return this;
 }

 public BitStream appendLongs(final long... longs) {
  appendData(64, 0, longs.length, longs);
  return this;
 }

 public BitStream consumeLongs(final long[] longs, final int start, final int end) {
  ensureArraySize(start, end, longs);
  return this;
 }

 public BitStream appendDoubles(final double... doubles) {
  appendData(64, 0, doubles.length, doubles);
  return this;
 }

 public BitStream consumeDouble(final double[] doubles, final int start, final int end) {
  ensureArraySize(start, end, doubles);
  return this;
 }
}