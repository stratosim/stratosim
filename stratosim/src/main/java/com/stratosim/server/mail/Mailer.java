package com.stratosim.server.mail;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Mailer {

  private static final Logger logger = Logger.getLogger(Mailer.class.getCanonicalName());
  
  private static final InternetAddress CONTACT_ADDRESS;
  private static final InternetAddress ARCHIVE_ADDRESS;
  private static final InternetAddress NOREPLY_ADDRESS;

  static {
    InternetAddress address;

    address = null;
    try {
      address = new InternetAddress("contact@stratosim.com", "StratoSim Team");
    } catch (UnsupportedEncodingException ex) {
      logger.log(Level.SEVERE, "Couldn't create internet address to send", ex);
    }
    CONTACT_ADDRESS = address;
    
    address = null;
    try {
        address = new InternetAddress("archive@stratosim.com",
                "StratoSim Archiver");
    } catch (UnsupportedEncodingException ex) {
        logger.log(Level.SEVERE,
                "Couldn't create internet address to archive", ex);
    }
    ARCHIVE_ADDRESS = address;

    address = null;
    try {
      // TODO(tpondich): Create No Reply Email.
      address = new InternetAddress("contact@stratosim.com", "StratoSim");
    } catch (UnsupportedEncodingException ex) {
      logger.log(Level.SEVERE, "Couldn't create internet address to send", ex);
    }
    NOREPLY_ADDRESS = address;
  }

  public static void sendMailFromContact(String recipient, String subject, String htmlBody,
      String textBody) throws MessagingException {
    sendMail(recipient, subject, htmlBody, textBody, CONTACT_ADDRESS);
  }

  public static void sendMailFromNoReply(String recipient, String subject, String htmlBody,
      String textBody) throws MessagingException {
    sendMail(recipient, subject, htmlBody, textBody, NOREPLY_ADDRESS);
  }

  public static void sendMail(String recipient, String subject, String htmlBody, String textBody,
      InternetAddress from) throws MessagingException {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props);

    Multipart mp = new MimeMultipart();

    MimeBodyPart htmlPart = new MimeBodyPart();
    htmlPart.setContent(htmlBody, "text/html");
    mp.addBodyPart(htmlPart);

    MimeBodyPart textPart = new MimeBodyPart();
    textPart.setContent(textBody, "text/plain");
    mp.addBodyPart(textPart);

    MimeMessage message = new MimeMessage(session);

    message.setFrom(from);
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
    message.addRecipient(Message.RecipientType.BCC, ARCHIVE_ADDRESS);

    message.setSubject(subject);

    message.setContent(mp);

    Transport.send(message);
  }

}
