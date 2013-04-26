package com.stratosim.server.site;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.stratosim.server.ConfigFactory;
import com.stratosim.server.mail.Mailer;
import com.stratosim.shared.filemodel.EmailAddress;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class RequestInviteServlet extends HttpServlet {
	private static final long serialVersionUID = -4278985504180972646L;

	private static final Logger logger = Logger
			.getLogger(RequestInviteServlet.class.getCanonicalName());

	private static final boolean ENABLE_EMAIL_MEMCACHE_DEDUPING = true;

	// Limit the number of entries to 20 entries / minute / IP.
	private static final int RESET_TIME = 60; // 1 Minute
	private static final int MAX_COUNT = 20; // 4 Entries

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/plain");

		Cache cache = null;
		Map<String, Integer> props = new HashMap<String, Integer>();
		props.put(GCacheFactory.EXPIRATION_DELTA, RESET_TIME);
		try {
			CacheFactory cacheFactory = CacheManager.getInstance()
					.getCacheFactory();
			cache = cacheFactory.createCache(props);
		} catch (CacheException e) {
			throw new IllegalStateException();
		}

		Integer count = (Integer) cache.get(req.getRemoteAddr());
		if (count == null) {
			count = 0;
		}

		if (count < MAX_COUNT) {
			// We don't want to increment the count in the event of MAX_COUNT.
			// Otherwise a massive attack could cause overflow. (Though, this
			// can't happen in practice without taking down our instance hours).
			count++;
			// This can easily skip values, if we concurrently overwrite, but
			// we don't care, it'll just take longer to detect.
			cache.put(req.getRemoteAddr(), count);

			String emailString = req.getParameter("email");
			if (emailString != null && EmailAddress.isValid(emailString)) {
				DatastoreService datastore = DatastoreServiceFactory
						.getDatastoreService();

				Entity requestEntity = new Entity("request");
				Email email = new Email(emailString);
				requestEntity.setUnindexedProperty("email", email);
				requestEntity.setProperty("approved", false);
				requestEntity.setProperty("date", new Date());
				requestEntity.setUnindexedProperty("ipAddress", req.getRemoteAddr());

				String tracking = req.getParameter("tracking");
				if (tracking != null) {
					requestEntity.setProperty("tracking", tracking);
				}

				if (!ENABLE_EMAIL_MEMCACHE_DEDUPING
						|| !cache.containsKey(email.getEmail())) {
					cache.put(email.getEmail(), System.currentTimeMillis());
					datastore.put(requestEntity);

					// Send email.
					Configuration config = ConfigFactory
							.get(getServletContext());

					Template htmlTemplate = config
							.getTemplate("RequestInviteEmail.html");
					StringWriter htmlWriter = new StringWriter();
					htmlTemplate.dump(htmlWriter);

					Template textTemplate = config
							.getTemplate("RequestInviteEmail.txt");
					StringWriter textWriter = new StringWriter();
					textTemplate.dump(textWriter);

					try {
						Mailer.sendMailFromContact(email.getEmail(),
								"Thanks for signing up for StratoSim!",
								htmlWriter.toString(), textWriter.toString());
					} catch (MessagingException ex) {
						logger.log(Level.SEVERE, "Failed to send email to: "
								+ email.getEmail(), ex);
					}
				}

				resp.getWriter().write("true");
				
			} else {
				logger.log(
						Level.WARNING,
						"Email validation failed for: "
								+ req.getRemoteAddr() + " " + emailString);
				resp.getWriter().write("false");
			}

		} else {
			logger.log(Level.WARNING,
					"High request volume from: " + req.getRemoteAddr());
			resp.getWriter().write("captcha");
		}
	}
}
