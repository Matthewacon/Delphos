package io.github.matthewacon.delphos.utils;

public final class Arrays {
 public static void ensureArraySize(final int start, final int end, final Object... arr) {
  if (start < 0 || end > arr.length) {
   throw new ArrayIndexOutOfBoundsException("Array length: " + arr.length + " Start: " + start + " End: " + end);
  }
 }
}
