package com.nexblocks.authguard.external.email.jmail;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HandlebarsTemplateResolverTest {

    @Test
    void resolve() {
        final HandlebarsTemplateResolver handlebarsTemplateResolver = new HandlebarsTemplateResolver();

        final String template = "Hello, {{name}}!";
        final Map<String, Object> parameters = Collections.singletonMap("name", "Handlebars");

        final String expected = "Hello, Handlebars!";
        final String actual = handlebarsTemplateResolver.resolve(template, parameters).get();

        assertThat(actual).isEqualTo(expected);
    }
}