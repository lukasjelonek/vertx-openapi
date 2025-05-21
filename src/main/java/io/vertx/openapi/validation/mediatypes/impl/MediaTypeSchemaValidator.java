package io.vertx.openapi.validation.mediatypes.impl;

import static io.vertx.openapi.validation.SchemaValidationException.createInvalidValueBody;
import static io.vertx.openapi.validation.ValidationContext.REQUEST;
import static io.vertx.openapi.validation.ValidatorErrorType.UNSUPPORTED_VALUE_FORMAT;

import io.vertx.core.buffer.Buffer;
import io.vertx.json.schema.JsonSchemaValidationException;
import io.vertx.json.schema.OutputUnit;
import io.vertx.openapi.contract.MediaType;
import io.vertx.openapi.contract.OpenAPIContract;
import io.vertx.openapi.validation.ValidationContext;
import io.vertx.openapi.validation.ValidatorException;
import io.vertx.openapi.validation.analyser.ContentAnalyser;
import io.vertx.openapi.validation.impl.RequestParameterImpl;
import io.vertx.openapi.validation.mediatypes.MediaTypeValidator;

abstract class MediaTypeSchemaValidator implements MediaTypeValidator {

  private final OpenAPIContract contract;
  private final MediaType       mediaType;

  protected MediaTypeSchemaValidator(OpenAPIContract contract, MediaType mediaType) {
    this.contract  = contract;
    this.mediaType = mediaType;

  }

  protected abstract ContentAnalyser createAnalyser(String contentType, Buffer rawContent);

  @Override
  public io.vertx.openapi.validation.Parameter validate(String contentType, Buffer rawContent) {
    ValidationContext requestOrResponse = REQUEST; // TODO move the scope to the request or response object if
                                                   // possible, otherwise keep it here and add it as a parameter to
                                                   // the method
    if (mediaType == null) {
      throw new ValidatorException("The format of the " + requestOrResponse + " body is not supported",
          UNSUPPORTED_VALUE_FORMAT);
    }
    ContentAnalyser contentAnalyser = createAnalyser(contentType, rawContent);

    // Throws an exception if the content is not syntactically correct
    contentAnalyser.checkSyntacticalCorrectness();

    if (isSchemaValidationRequired(mediaType)) {
      Object     transformedValue = contentAnalyser.transform();
      OutputUnit result           = contract.getSchemaRepository().validator(mediaType.getSchema()).validate(
          transformedValue);
      try {
        result.checkValidity();
        return new RequestParameterImpl(transformedValue);
      } catch (JsonSchemaValidationException e) {
        throw createInvalidValueBody(result, requestOrResponse, e);
      }
    }
    return new RequestParameterImpl(rawContent);
  }

  protected boolean isSchemaValidationRequired(MediaType mediaType) {
    if (mediaType.getSchema() == null) {
      // content should be treated as binary, because no media model is defined (OpenAPI 3.1)
      return false;
    } else {
      String type   = mediaType.getSchema().get("type");
      String format = mediaType.getSchema().get("format");

      // Also a binary string could have length restrictions, therefore we need to preclude further properties.
      boolean noFurtherProperties = mediaType.getSchema().fieldNames().size() == 2;

      if ("string".equalsIgnoreCase(type) && "binary".equalsIgnoreCase(format) && noFurtherProperties) {
        return false;
      }
      return true;
    }
  }

}
