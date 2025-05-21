package io.vertx.openapi.validation.mediatypes.impl;

import static io.vertx.openapi.validation.ValidationContext.REQUEST;

import io.vertx.core.buffer.Buffer;
import io.vertx.openapi.contract.MediaType;
import io.vertx.openapi.contract.OpenAPIContract;
import io.vertx.openapi.validation.analyser.ContentAnalyser;
import io.vertx.openapi.validation.analyser.MultipartFormAnalyser;
import io.vertx.openapi.validation.mediatypes.MediaTypeRegistration;
import io.vertx.openapi.validation.mediatypes.MediaTypeValidator;

public class MultipartMediaTypeRegistration implements MediaTypeRegistration {

  @Override
  public boolean canHandle(MediaType mediaType) {
    return MediaType.MULTIPART_FORM_DATA.equals(mediaType.getIdentifier());
  }

  @Override
  public MediaTypeValidator getValidator(OpenAPIContract contract, MediaType mediaType) {
    return new MultipartSchemaValidator(contract, mediaType);
  }

  private static final class MultipartSchemaValidator extends MediaTypeSchemaValidator {
    private MultipartSchemaValidator(OpenAPIContract contract, MediaType mediaType) {
      super(contract, mediaType);
    }

    @Override
    protected ContentAnalyser createAnalyser(String contentType, Buffer rawContent) {
      return new MultipartFormAnalyser(contentType, rawContent, REQUEST);
    }
  }
}
