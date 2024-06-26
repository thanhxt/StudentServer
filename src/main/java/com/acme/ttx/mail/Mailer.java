package com.acme.ttx.mail;


import com.acme.ttx.MailProps;
import com.acme.ttx.entity.Student;
import jakarta.mail.internet.InternetAddress;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import static jakarta.mail.Message.RecipientType.TO;

/**
 * Mail-Client.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("ClassNamePrefixedWithPackageName")
public class Mailer {
    @SuppressWarnings("BooleanVariableAlwaysNegated")
    private static final boolean SMTP_ACTIVATED = Objects.equals(System.getenv("SMTP_ACTIVATED"), "true") ||
        Objects.equals(System.getProperty("smtp-activated"), "true");

    private final JavaMailSender mailSender;
    private final MailProps props;

    @Value("${spring.mail.host}")
    private String mailhost;

    @Async
    @SuppressWarnings({"CatchParameterName", "IllegalIdentifierName", "LocalFinalVariableName"})
    public void send (final Student neuerStudent) {

        if (!SMTP_ACTIVATED) {
            log.warn("SMTP ist deaktiviert.");
        }
        final MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setFrom(new InternetAddress(props.from()));
            mimeMessage.setRecipient(TO, new InternetAddress(props.sales()));
            mimeMessage.setSubject("Neuer Student " + neuerStudent.getId());
            final var body = "<strong>Neuer Student:</strong> <em>" + neuerStudent.getNachname() + "</em>";
            log.trace("send: Mailserver={}, Thread-ID={}, body={}", mailhost, Thread.currentThread().threadId(), body);
            mimeMessage.setText(body);
            mimeMessage.setHeader("Content-Type", "text/html");
        };

        try {
            mailSender.send(preparator);
        } catch (final MailSendException | MailAuthenticationException _) {
            // TODO Wiederholung, um die Email zu senden
            log.warn("Email nicht gesendet: Ist der Mailserver {} erreichbar?", mailhost);
        }
    }
}
