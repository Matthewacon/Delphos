package io.github.matthewacon.delphos;

public final class ParserException extends RuntimeException {
 public ParserException(final String msg) {
  super(msg);
 }

 public ParserException(final String msg, final Throwable t) {
  super(msg, t);
 }
}
