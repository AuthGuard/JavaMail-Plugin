package com.authguard.external.email.jmail;

import com.authguard.config.ConfigContext;
import com.authguard.external.email.EmailProvider;
import com.authguard.external.email.ImmutableEmail;
import com.authguard.external.email.jmail.config.ImmutableJavaMailProviderConfig;
import com.authguard.external.email.jmail.config.ImmutableTemplateConfig;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * An email provider implementation using JavaMail. It
 * can support any protocol JavaMail supports. Its
 * configuration is split into two parts:
 * 1. javaMail: A list of key-value pairs which will be
 *    mapped to a Properties object and will be passed
 *    to a JavaMail session.
 * 2. provider: The configuration of the provider which
 *    includes template file and subjects mapping, from
 *    address ..etc.
 *
 * It only support Handlebars templates for now.
 */
public class JavaMailProvider implements EmailProvider {
    private static final Logger LOG = LoggerFactory.getLogger(JavaMailProvider.class);

    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

    private final TemplateResolver templateResolver;
    private final TemplatesLoader templatesLoader;
    private final Properties javaMailConfig;
    private final ImmutableJavaMailProviderConfig providerConfig;

    private Session mailSession;

    @Inject
    public JavaMailProvider(final @Named("mail") ConfigContext config) {
        this.templateResolver = new HandlebarsTemplateResolver();
        this.templatesLoader = new TemplatesLoader();

        this.javaMailConfig = config.getSubContext("javaMail").asProperties();
        this.providerConfig = config.getAsConfigBean("provider", ImmutableJavaMailProviderConfig.class);
    }

    public JavaMailProvider(final ImmutableJavaMailProviderConfig providerConfig, final Properties javaMailConfig) {
        this.templateResolver = new HandlebarsTemplateResolver();
        this.templatesLoader = new TemplatesLoader();

        this.javaMailConfig = javaMailConfig;
        this.providerConfig = providerConfig;
    }

    @Override
    public void send(final ImmutableEmail immutableEmail) {
        final ImmutableTemplateConfig templateConfig = providerConfig.getTemplates().get(immutableEmail.getTemplate());
        final String templateFile = templateConfig.getFile();

        LOG.debug("Template {} was mapped to file {}", immutableEmail.getTemplate(), templateFile);

        final Try<String> contentTry = templatesLoader.get(templateFile, providerConfig.enableFileCache())
                .flatMap(template -> templateResolver.resolve(template, immutableEmail.getParameters()));

        if (contentTry.isFailure()) {
            LOG.error("Failed to process template ({}, {})", immutableEmail.getTemplate(),
                    templateFile, contentTry.getCause());
        } else {
            doSend(immutableEmail, templateConfig.getSubject(), contentTry.get());
        }
    }

    private Session getSession() {
        if (mailSession == null) {
            mailSession = Session.getInstance(javaMailConfig);
        }

        return mailSession;
    }

    private void doSend(final ImmutableEmail immutableEmail, final String subject, final String content) {
        try {
            final MimeMessage msg = new MimeMessage(getSession());

            msg.setFrom(new InternetAddress(providerConfig.getFromAddress(), providerConfig.getFromName()));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(immutableEmail.getTo()));
            msg.setSubject(subject);
            msg.setContent(content, CONTENT_TYPE);

            Transport.send(msg);
        } catch (final Exception e) {
            LOG.error("Failed to send email", e);
        }
    }
}
