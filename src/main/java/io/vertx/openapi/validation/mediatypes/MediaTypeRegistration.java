package io.vertx.openapi.validation.mediatypes;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import io.vertx.core.buffer.Buffer;
import io.vertx.openapi.contract.MediaType;
import io.vertx.openapi.contract.OpenAPIContract;
import io.vertx.openapi.validation.Parameter;
import io.vertx.openapi.validation.impl.RequestParameterImpl;

/**
 * A MediaTypeRegistration is used to register mediatypes to the openapi mediatype registry.
 *
 */
public interface MediaTypeRegistration {
  /**
   * Checks if this registration can handle the given media type. This method is intended to be used by the
   * MediaTypeRegistry.
   *
   * @param mediaType
   * @return
   */
  boolean canHandle(MediaType mediaType);

  /**
   * Creates a new validator or reuses an existing one (if the validators are stateless) for the mediatype.
   *
   * @param contract The OpenAPIContract to use for validation.
   * @param type     The mediatype of the request.
   * @return A validator.
   */
  MediaTypeValidator getValidator(OpenAPIContract contract, MediaType type);

  /**
   * Creates a new registration created from the provided functions.
   *
   * @param canHandleMediaType
   * @param validatorFactory
   * @return
   */
  public static MediaTypeRegistration create(
      Predicate<MediaType> canHandleMediaType,
      BiFunction<OpenAPIContract, MediaType, MediaTypeValidator> validatorFactory) {
    return new MediaTypeRegistration() {

      @Override
      public boolean canHandle(MediaType mediaType) {
        return canHandleMediaType.test(mediaType);
      }

      @Override
      public MediaTypeValidator getValidator(OpenAPIContract contract, MediaType type) {
        return validatorFactory.apply(contract, type);
      }

    };
  }

  /**
   * Creates a registration that does simply returns the input without any validation. Can be used to register
   * mediatypes that do not require validation.
   *
   * @param canHandleMediaType
   * @return
   */
  public static MediaTypeRegistration alwaysValid(Predicate<MediaType> canHandleMediaType) {
    return new MediaTypeRegistration() {

      @Override
      public boolean canHandle(MediaType mediaType) {
        return canHandleMediaType.test(mediaType);
      }

      @Override
      public MediaTypeValidator getValidator(OpenAPIContract contract, MediaType type) {
        return new MediaTypeValidator() {

          @Override
          public Parameter validate(String contentType, Buffer buf) {
            return new RequestParameterImpl(buf);
          }

        };
      }

    };
  }
}
