package at.undok.common.mailer.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class UndokMailer {

  private static final String STYLE = "<style>.container{max-width: 500px;border: 0px solid #999;border-radius: 5px;margin: 0px auto;} .general {padding: 11px;text-align:left;line-height:1.3em;font-family:verdana, arial, helvetica, sans-serif;font-size: 0.9em;}a{text-decoration: none;color: #0055ff;}</style>";

  @Value("${at.undok.app.undokFromSender}")
  private String fromSender;

  private static final String DIV_CLASS_GENERAL = "<div class='general'>";

  @Autowired
  private JavaMailSender mailSender;

  public void getTheMailDetails(String to, String subject, String text, String salutation, String url) {

    String first_the_salutation = DIV_CLASS_GENERAL + "<h4>Dear " + salutation + "</h4></div>";
    String second_the_text = DIV_CLASS_GENERAL + text + "</div>";
    String third_the_url = DIV_CLASS_GENERAL + "<a href='" + url + "'>" + url + "</a></div>";
    String the_container = "<div class='container'>" + first_the_salutation + second_the_text + third_the_url + "</div>";
    String the_html_head_and_body = "<html><head>" + STYLE + "</head><body>" + the_container + "</body></html>";

    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
    try {
      helper.setText(the_html_head_and_body, true);
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

}
