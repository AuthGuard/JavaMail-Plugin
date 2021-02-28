package com.nexblocks.authguard.external.email.jmail;

import io.vavr.control.Try;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class TemplatesLoader {
    private final Map<String, String> cache;

    public TemplatesLoader() {
        cache = new HashMap<>();
    }

    public Try<String> get(final String fileName, final boolean keepInCache) {
        try {
            final String template = getUnsafe(fileName, keepInCache);
            return Try.success(template);
        } catch (final Exception e) {
            return Try.failure(e);
        }
    }

    private String getUnsafe(final String fileName, final boolean keepInCache) throws IOException {
        if (keepInCache) {
            if (!cache.containsKey(fileName)) {
                final String content = Files.readString(new File(fileName).toPath());

                cache.put(fileName, content);
            }

            return cache.get(fileName);
        } else {
            return Files.readString(new File(fileName).toPath());
        }
    }
}
