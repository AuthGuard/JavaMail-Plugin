package com.authguard.external.email.jmail.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTemplateConfig.class)
public interface TemplateConfig {
    String getSubject();
    String getFile();
}
