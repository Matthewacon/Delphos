package io.github.matthewacon.delphos;

import io.github.matthewacon.delphos.api.IParser;
import io.github.matthewacon.pal.util.ExampleLinkedTreeMap;

public final class Parsed<T extends IParser<C>, C> extends ExampleLinkedTreeMap<Parsed<T, C>> {
 public final T parser;
 public final C parsed;
 public final long bitLength;

 public Parsed(final T parser, final C parsed, final long bitLength) {
  this.parser = parser;
  this.parsed = parsed;
  this.bitLength = bitLength;
 }

 @Override
 public Parsed<T, C> clone() {
  return new Parsed<>(parser, parsed, bitLength);
 }

 @Override
 public boolean equals(Object obj) {
  if (obj instanceof Parsed) {
   final Parsed<T, C> casted = (Parsed<T, C>)obj;
   if (casted.parser.equals(parser) && casted.parsed.equals(parsed) && casted.bitLength == bitLength) {
    return true;
   }
  }
  return false;
 }
}
