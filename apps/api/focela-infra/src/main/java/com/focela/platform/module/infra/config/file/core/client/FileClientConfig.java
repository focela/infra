package com.focela.platform.module.infra.config.file.core.client;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * File client config
 * Different implementations of clients require different configs, defined via subclasses
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
// Purpose of the @JsonTypeInfo annotation, Jackson polymorphism
// 1. When serializing to the database, adds a @class property.
// 2. When deserializing into an in-memory object, the @class property enables creating the correct type
public interface FileClientConfig {
}
