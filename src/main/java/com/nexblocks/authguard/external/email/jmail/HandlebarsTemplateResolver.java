package com.nexblocks.authguard.external.email.jmail;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.vavr.control.Try;

import java.util.Map;

public class HandlebarsTemplateResolver implements TemplateResolver {
    private final Handlebars handlebars;

    public HandlebarsTemplateResolver() {
        handlebars = new Handlebars();
    }

    @Override
    public Try<String> resolve(final String template, final Map<String, Object> parameters) {
        try {
            final Template compiledTemplate = handlebars.compileInline(template);
            final Context context = Context.newContext(parameters);

            return Try.success(compiledTemplate.apply(context));
        } catch (final Exception e) {
            return Try.failure(e);
        }
    }
}
