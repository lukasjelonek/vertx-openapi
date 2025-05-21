package io.vertx.openapi.validation.mediatypes;

import io.vertx.core.buffer.Buffer;
import io.vertx.openapi.validation.Parameter;

public interface MediaTypeValidator {
  public Parameter validate(String contentType, Buffer buf);
}
