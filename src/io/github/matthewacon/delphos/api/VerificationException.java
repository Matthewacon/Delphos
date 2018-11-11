package io.github.matthewacon.delphos.api;

public final class VerificationException extends RuntimeException {
 public VerificationException(final String msg) {
  super(msg);
 }

 public VerificationException(final String msg, final Throwable cause) {
  super(msg, cause);
 }
}
