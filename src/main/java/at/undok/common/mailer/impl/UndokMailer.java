package at.undok.common.mailer.impl;

import at.undok.auth.model.entity.User;
import at.undok.common.encryption.AttributeEncryptor;
import at.undok.common.message.Message;
import at.undok.common.util.EmailStuff;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@Slf4j
public class UndokMailer {

  private static final String STYLE = "<style>.container{max-width: 500px;border: 0px solid #999;border-radius: 5px;margin: 0px auto;} .general {padding: 11px;text-align:left;line-height:1.3em;font-family:verdana, arial, helvetica, sans-serif;font-size: 0.9em;}a{text-decoration: none;color: #0055ff;}</style>";

  @Value("${at.undok.app.undokFromSender}")
  private String fromSender;

  @Value("${service.b.org.app.baseUrl}")
  private String applicationBaseUrl;

  private static final String DIV_CLASS_GENERAL = "<div class='general'>";
  private static final String CLOSING_DIV = "</div>";

  private final JavaMailSender mailSender;

  private final AttributeEncryptor attributeEncryptor;

  public UndokMailer(JavaMailSender mailSender, AttributeEncryptor attributeEncryptor) {
    this.mailSender = mailSender;
    this.attributeEncryptor = attributeEncryptor;
  }

  public void getTheMailDetails(String to, String subject, String text, String salutation, String url) {

    String first_the_salutation = DIV_CLASS_GENERAL + "<h4>Dear " + salutation + "</h4></div>";
    String second_the_text = DIV_CLASS_GENERAL + text + "</div>";
    String third_the_url = DIV_CLASS_GENERAL + "<a class='confirmation' href='" + url + "'>" + url + "</a></div>";
    String the_container = "<div class='container'>" + first_the_salutation + second_the_text + third_the_url + "</div>";
    String the_html_head_and_body = "<html><head>" + STYLE + "</head><body>" + the_container + "</body></html>";

    setMimeMessage(to, the_html_head_and_body, subject);

  }

  private void setMimeMessage(String to, String text, String subject) {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
    try {
      helper.setText(text, true);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setFrom(fromSender);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
    andNowSendTheMail(mimeMessage);
  }

  private void andNowSendTheMail(MimeMessage mailMessage){
    mailSender.send(mailMessage);
  }

  public Message createConfirmationMail(User user, String confirmationToken) {
    String url = createConfirmationUrl(user.getEmail(), confirmationToken);
    String subject = EmailStuff.SUBJECT_PREFIX + " confirm account";
    String text = "click the link below within the next 2 hours, after this it will expire";
    log.info(url);
    getTheMailDetails(user.getEmail(), subject, text, user.getUsername(), url);
    return new Message("We've sent you a message confirming your", true);
  }

  public String createConfirmationUrl(String email, String confirmationToken) {
    String encryptedEmail = attributeEncryptor.encodeWithUrlEncoder(email);
    String encryptedToken = attributeEncryptor.encodeWithUrlEncoder(confirmationToken);
    String confUrl = applicationBaseUrl + "/auth/" + encryptedToken + "/confirm/" + encryptedEmail;
    log.info(confUrl);
    return confUrl;
  }

  public void send2FactorTokenToUser(String token, String email) {
    String text = DIV_CLASS_GENERAL + token + CLOSING_DIV;
    setMimeMessage(email, text, "2Factor");
  }

}
