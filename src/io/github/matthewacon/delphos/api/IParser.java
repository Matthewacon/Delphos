package io.github.matthewacon.delphos.api;

import io.github.matthewacon.delphos.Parsed;

public interface IParser<T> {
 //TODO Swap generic parameters to conform the the rest of the library
 <C, P extends IParser<C>> Parsed<P, C> parse(final Parsed pt, final byte[] data);
 Class<T> getType();
}
