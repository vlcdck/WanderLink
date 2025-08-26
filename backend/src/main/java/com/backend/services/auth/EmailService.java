package com.backend.services.auth;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.Locale;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final MessageSource messageSource;

    @Value("${app.domain}")
    private String appDomain;

    @Value("${email.confirmation.path}")
    private String confirmationPath;

    @Value("${app.mail.from.address}")
    private String fromAddress;

    @Value("${app.mail.from.name}")
    private String fromName;

    @Async
    public void sendConfirmationEmail(String to, String token, Locale locale) {
        if (!EmailValidator.getInstance().isValid(to)) {
            log.warn("Incorrect email address: {}", maskEmail(to));
            return;
        }

        validateConfig();

        Locale effectiveLocale = (locale != null ? locale : Locale.ENGLISH);
        LocaleContextHolder.setLocale(effectiveLocale);

        String body = generateEmailBody(token, effectiveLocale);

        try {
            MimeMessage message = createMessage(to, body, effectiveLocale);
            mailSender.send(message);
            log.info("A confirmation email has been successfully sent to: {} ({})",
                    maskEmail(to), effectiveLocale);

        } catch (MessagingException e) {
            log.error("Error when generating email for {}", maskEmail(to), e);
        } catch (MailException e) {
            log.error("Error sending email to {}", maskEmail(to), e);
            throw e;
        }
    }

    private void validateConfig() {
        if (appDomain == null || appDomain.isBlank()) {
            throw new IllegalStateException("App domain is not configured");
        }
        if (confirmationPath == null || confirmationPath.isBlank()) {
            throw new IllegalStateException("Confirmation path is not configured");
        }
        if (fromAddress == null || fromAddress.isBlank()) {
            throw new IllegalStateException("Email sender address is not configured");
        }
    }

    private String generateEmailBody(String token, Locale locale) {
        String link = appDomain + confirmationPath + "?token=" + token;
        Context context = new Context(locale);
        context.setVariable("confirmationLink", link);
        return templateEngine.process("email/confirmation-email", context);
    }

    private MimeMessage createMessage(String to, String body, Locale locale) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        try {
            helper.setFrom(fromAddress, fromName);
        } catch (UnsupportedEncodingException e) {
            log.error("Sender configuration error, only address is used", e);
            helper.setFrom(fromAddress);
        }

        helper.setTo(to);

        String subject = messageSource.getMessage("email.confirmation.subject", null, locale);

        helper.setSubject(subject);
        helper.setText(body, true); // HTML

        return message;
    }

    private String maskEmail(String email) {
        int at = email.indexOf("@");
        if (at <= 1) return "***" + email.substring(at);
        return email.substring(0, 1) + "***" + email.substring(at);
    }
}
