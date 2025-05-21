package io.vertx.openapi.validation.mediatypes.impl;

import static io.vertx.openapi.validation.ValidationContext.REQUEST;

import io.vertx.core.buffer.Buffer;
import io.vertx.openapi.contract.MediaType;
import io.vertx.openapi.contract.OpenAPIContract;
import io.vertx.openapi.validation.analyser.ApplicationJsonAnalyser;
import io.vertx.openapi.validation.analyser.ContentAnalyser;
import io.vertx.openapi.validation.mediatypes.MediaTypeRegistration;
import io.vertx.openapi.validation.mediatypes.MediaTypeValidator;

public class JsonMediaTypeRegistration implements MediaTypeRegistration {

  @Override
  public boolean canHandle(MediaType mediaType) {
    var id = mediaType.getIdentifier();
    return id.startsWith("application/json") || id.endsWith("+json");
  }

  @Override
  public MediaTypeValidator getValidator(OpenAPIContract contract, MediaType mediaType) {
    return new JsonSchemaValidator(contract, mediaType);
  }

  private final static class JsonSchemaValidator extends MediaTypeSchemaValidator {
    private JsonSchemaValidator(OpenAPIContract contract, MediaType mediaType) {
      super(contract, mediaType);
    }

    @Override
    protected ContentAnalyser createAnalyser(String contentType, Buffer rawContent) {
      return new ApplicationJsonAnalyser(contentType, rawContent, REQUEST);
    }
  }
}
