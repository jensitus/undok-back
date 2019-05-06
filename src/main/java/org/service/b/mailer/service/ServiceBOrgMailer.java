package org.service.b.mailer.service;

public interface ServiceBOrgMailer {

  void getTheMailDetails(String to, String subject, String text, String salutation, String url);

}
