package io.github.matthewacon.delphos.api;

import io.github.matthewacon.delphos.Parsed;

public interface IParser<T> {
 <C, P extends IParser<C>> Parsed<P, C> parse(final Parsed pt, final byte[] data);
 Class<T> getType();
}
