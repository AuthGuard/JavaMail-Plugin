package com.authguard.external.email.jmail.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
@Value.Style(
        get = { "get*", "enable*" },
        validationMethod = Value.Style.ValidationMethod.NONE,
        jdkOnly = true
)
@JsonDeserialize(as = ImmutableJavaMailProviderConfig.class)
public interface JavaMailProviderConfig {
    Map<String, ImmutableTemplateConfig> getTemplates();

    Map<String, String> getSubjects();

    String getFromAddress();
    String getFromName();

    @Value.Default
    default Boolean enableFileCache() {
        return true;
    }
}
