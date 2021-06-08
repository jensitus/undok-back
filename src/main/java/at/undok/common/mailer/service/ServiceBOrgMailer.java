package at.undok.common.mailer.service;

public interface ServiceBOrgMailer {

  void getTheMailDetails(String to, String subject, String text, String salutation, String url);

}
