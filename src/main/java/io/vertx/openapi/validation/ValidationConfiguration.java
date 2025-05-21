package io.vertx.openapi.validation;

import io.vertx.openapi.validation.mediatypes.MediaTypeRegistry;

public class ValidationConfiguration {

  private final MediaTypeRegistry registry;

  private ValidationConfiguration(MediaTypeRegistry registry) {
    this.registry = registry;
  }

  public MediaTypeRegistry getRegistry() {
    return registry;
  }

  public static ValidationConfiguration create() {
    return new ValidationConfiguration(MediaTypeRegistry.createDefault());
  }

  public static ValidationConfiguration create(MediaTypeRegistry registry) {
    return new ValidationConfiguration(registry);
  }

}
