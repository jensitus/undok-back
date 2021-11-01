package at.undok.it.cucumber.auth;

import com.icegreen.greenmail.spring.GreenMailBean;
import org.jsoup.Jsoup;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class EmailVerifications {

    private final GreenMailBean greenMail;

    public EmailVerifications(GreenMailBean greenMail) {
        this.greenMail = greenMail;
    }

    public MimeMessage assertEmailSentTo(String recipientEmail, EmailType emailType) {
        var messages = greenMail.getReceivedMessages();
        assertThat(messages.length)
                .isNotZero();

        var filterMessages = Arrays.stream(messages)
                .filter(message -> hasRecipient(message, recipientEmail))
                .filter(message -> isOfMatchingMediaType(message, emailType))
                .filter(message -> isOfMatchingInternalType(message, emailType))
                .collect(Collectors.toList());

        assertThat(filterMessages)
                .hasSize(1);

        var singleMessage = filterMessages.get(0);

        var content = getEmailContent(filterMessages.get(0));

        return singleMessage;

    }

    public String getEmailContent(MimeMessage message) {
        try {
            var content = (String) message.getContent();
            assertThat(content).isNotBlank();
            return content;
        } catch (IOException | MessagingException e) {
            throw new RuntimeException("Exception while retrieving content from mail", e);
        }

    }

    private boolean isOfMatchingMediaType(MimeMessage message, EmailType emailType) {
        try {
            return message.getContentType().equals(emailType.getMediaType());
        } catch (MessagingException e) {
            throw new RuntimeException("Exception while determining content type from mail", e);
        }
    }

    private boolean isOfMatchingInternalType(MimeMessage message, EmailType emailType) {
        try {
            return message.getSubject().equals(emailType.getSubject());
        } catch (MessagingException e) {
            throw new RuntimeException("Exception while extracting subject from mail", e);
        }
    }

    private boolean hasRecipient(MimeMessage message, String recipientEmail) {
        try {
            return Arrays.stream(message.getAllRecipients()).anyMatch(address -> address.toString().equals(recipientEmail));
        } catch (MessagingException e) {
            throw new RuntimeException("Exception while extracting email recipients from mail", e);
        }
    }

    public String parseConfirmationLink(String content) {
        var confirmationLinkElement = Jsoup.parse(content).select("a.confirmation");
        assertThat(confirmationLinkElement).isNotNull();

        var confirmationLink = confirmationLinkElement.attr("href");
        assertThat(confirmationLink).isNotBlank();
        return confirmationLink;
    }
}
