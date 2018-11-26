package io.github.matthewacon.delphos;

public final class ParserConfigurationException extends RuntimeException {
 public ParserConfigurationException(final String msg) {
  super(msg);
 }

 public ParserConfigurationException(final String msg, final Throwable t) {
  super(msg, t);
 }
}
