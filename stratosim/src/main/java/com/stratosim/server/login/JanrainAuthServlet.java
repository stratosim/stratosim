package com.stratosim.server.login;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.stratosim.server.AppInfo;

public class JanrainAuthServlet extends AbstractAuthServlet {
  private static final long serialVersionUID = -5746678995368093751L;

  @Override
  protected String getEmail(HttpServletRequest request, HttpServletResponse response) {
    try {
      String token = request.getParameter("token");
      checkNotNull(token);

      URL url = new URL(AppInfo.JANRAIN_URL);
      String message = "apiKey=" + AppInfo.JANRAIN_API_KEY + "&token=" + URLEncoder.encode(token, "UTF-8");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");
      OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
      writer.write(message);
      writer.close();

      checkState(connection.getResponseCode() == HttpURLConnection.HTTP_OK);

      BufferedInputStream input = new BufferedInputStream(connection.getInputStream());
      byte[] inputBytes = new byte[connection.getContentLength()];
      input.read(inputBytes);
      String responseString = new String(inputBytes);

      JsonParser parser = new JsonParser();
      JsonElement responseJson = parser.parse(responseString);
      checkState(responseJson.getAsJsonObject().get("stat").getAsString().equals("ok"));

      JsonElement verifiedEmail =
          responseJson.getAsJsonObject().get("profile").getAsJsonObject().get("verifiedEmail");
      JsonElement email = 
          responseJson.getAsJsonObject().get("profile").getAsJsonObject().get("email");
      
      String emailString;
      if (verifiedEmail != null) {
        emailString = verifiedEmail.getAsString();
      } else {
        emailString = email.getAsString();
      }

      return emailString;

    } catch (MalformedURLException e) {
      // TODO(tpondich): Log this.
      return null;
    } catch (IOException e) {
      // TODO(tpondich): Log this.
      return null;
    }
  }
}
