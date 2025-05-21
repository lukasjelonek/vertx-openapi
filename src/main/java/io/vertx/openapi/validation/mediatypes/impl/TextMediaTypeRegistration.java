package io.vertx.openapi.validation.mediatypes.impl;

import io.vertx.core.buffer.Buffer;
import io.vertx.openapi.contract.MediaType;
import io.vertx.openapi.contract.OpenAPIContract;
import io.vertx.openapi.validation.Parameter;
import io.vertx.openapi.validation.impl.RequestParameterImpl;
import io.vertx.openapi.validation.mediatypes.MediaTypeRegistration;
import io.vertx.openapi.validation.mediatypes.MediaTypeValidator;

public class TextMediaTypeRegistration implements MediaTypeRegistration {

  @Override
  public boolean canHandle(MediaType mediaType) {
    return mediaType.getIdentifier().startsWith("text/plain");
  }

  @Override
  public MediaTypeValidator getValidator(OpenAPIContract contract, MediaType type) {
    return new MediaTypeValidator() {

      @Override
      public Parameter validate(String contentType, Buffer rawContent) {
        return new RequestParameterImpl(rawContent);
      }

    };
  }

}
