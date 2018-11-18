package io.github.matthewacon.delphos;

import io.github.matthewacon.delphos.api.IParser;

public final class Parsed<T extends IParser<C>, C> {
 public final T parser;
 public final C parsed;
 public final long bitLength;

 public Parsed(final T parser, final C parsed, final long bitLength) {
  this.parser = parser;
  this.parsed = parsed;
  this.bitLength = bitLength;
 }
}
