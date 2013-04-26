package com.stratosim.server.admin;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import com.stratosim.server.ConfigFactory;
import com.stratosim.server.HashUtils;
import com.stratosim.server.mail.Mailer;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.shared.PersistenceException;
import com.stratosim.shared.filemodel.LowercaseEmailAddress;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class WhitelistHelper {

  private static final Logger logger = Logger.getLogger(WhitelistHelper.class.getCanonicalName());

  // These should be synced with MainServletLoginForm.html's javascript validation of the email.
  // Must be sorted.
  private final static String[] JANRAIN_DOMAINS = {"gmail.com", "hotmail.com", "stratosim.com",
      "yahoo.com"};

  static String addToWhitelist(ServletContext context, List<LowercaseEmailAddress> users) {
    String returnString = "";

    // Add users and create a StratoSim Account if necessary.
    // Users already whitelisted will have their StratoSim account password reset.
    for (LowercaseEmailAddress user : users) {
      Map<String, String> root = new HashMap<String, String>();

      try {
        returnString += user.getEmail();
        PersistenceLayerFactory.createWhitelistLayer().putWhitelisted(user);

      } catch (IllegalArgumentException ex) {
        returnString += "\tEXISTING";
        // Don't continue since the password may still be reset.

      } catch (PersistenceException ex) {
        returnString += "\tFAILED\n";
        logger.log(Level.SEVERE, "Error", ex);
        continue;

      }

      String emailDomain = user.getDomain();
      root.put("emailDomain", emailDomain);

      try {
        // Create necessary StratoSim accounts (or change password).
        if (Arrays.binarySearch(JANRAIN_DOMAINS, emailDomain) < 0) {
          root.put("username", user.getEmail());
          // Use shorter password tokens because long things get flagged as phone numbers by gmail.
          String password = "" + (HashUtils.secureRandom().nextInt(9000) + 1000);
          root.put("password", password);
          String hash = HashUtils.sha256String(user.getEmail() + password);
          PersistenceLayerFactory.createAccountsLayer(user).putAccount(hash);
          returnString += "\t" + password;
        }

      } catch (PersistenceException ex) {
        returnString += "\tFAILED\n";
        logger.log(Level.SEVERE, "Error", ex);
        continue;

      }

      returnString += "\n";

      try {
        // Send email.
        Configuration config = ConfigFactory.get(context);
        Template htmlTemplate = config.getTemplate("WhitelistEmail.html");
        Template textTemplate = config.getTemplate("WhitelistEmail.txt");

        StringWriter htmlWriter = new StringWriter();
        htmlTemplate.process(root, htmlWriter);

        StringWriter textWriter = new StringWriter();
        textTemplate.process(root, textWriter);

        Mailer.sendMailFromContact(user.getEmail(), "Welcome to StratoSim!", htmlWriter.toString(),
            textWriter.toString());

      } catch (Exception ex) {
        logger.log(Level.SEVERE, "Failed to send email to: " + user.getEmail(), ex);

      }
    }

    logger.log(Level.INFO, returnString);
    return returnString;
  }
}
