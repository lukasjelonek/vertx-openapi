package io.vertx.openapi.validation.mediatypes;

import java.util.ArrayList;
import java.util.List;

import io.vertx.openapi.contract.MediaType;
import io.vertx.openapi.contract.OpenAPIContract;
import io.vertx.openapi.validation.mediatypes.impl.JsonMediaTypeRegistration;
import io.vertx.openapi.validation.mediatypes.impl.MultipartMediaTypeRegistration;
import io.vertx.openapi.validation.mediatypes.impl.TextMediaTypeRegistration;

/**
 * By default the openapi validation uses a whitelist of mediatypes that can be processed. Unknown media types are
 * rejected. The MediaTypeRegistry allows you to register new media types and provide validation logic for each
 * registered media type.
 */
public class MediaTypeRegistry {
  private final List<MediaTypeRegistration> registrations = new ArrayList<>();

  /**
   * Registers a new MediaTypeHandler
   *
   * @param registration
   * @return
   */
  public MediaTypeRegistry register(MediaTypeRegistration registration) {
    this.registrations.add(registration);
    return this;
  }

  /**
   * Checks if the given mediatype can be handled by any of the registered mediatype registrations.
   *
   * @param type
   * @return
   */
  public boolean isSupported(MediaType type) {
    return this.registrations.stream().anyMatch(x -> x.canHandle(type));
  }

  /**
   * Retrieves a validator for the given media type. If the validators are stateless, they may be reused. Otherwise a
   * new validator is created each time.
   *
   * @param contract The contract to use for validation.
   * @param type     The mediatype.
   * @return A validator
   */
  public MediaTypeValidator getValidator(OpenAPIContract contract, MediaType type) {
    var reg = this.registrations.stream().filter(x -> x.canHandle(type)).findFirst();
    if (reg.isEmpty()) {
      // TODO replace with appropriate exception type
      throw new RuntimeException("Unsupported media type " + type.getIdentifier());
    }
    return reg.get().getValidator(contract, type);
  }

  /**
   * Creates a default registry with application/json, application/multipart and text/plain mediatypes registered.
   *
   * @return A registry with default options.
   */
  public static MediaTypeRegistry createDefault() {
    return new MediaTypeRegistry()
        .register(new TextMediaTypeRegistration())
        .register(new MultipartMediaTypeRegistration())
        .register(new JsonMediaTypeRegistration());
  }

  /**
   * Creates an empty registry.
   *
   * @return A empty registry.
   */
  public static MediaTypeRegistry createEmpty() {
    return new MediaTypeRegistry();
  }
}
