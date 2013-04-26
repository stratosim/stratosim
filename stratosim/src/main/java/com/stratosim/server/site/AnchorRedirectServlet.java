package com.stratosim.server.site;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AnchorRedirectServlet extends HttpServlet {

	private static final long serialVersionUID = 3696874694365288265L;

	/**
	 * Redirects to whatever comes after /. Static pages will be processed first
	 * in web.xml and so never actually reach this page.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String path = req.getPathInfo();
		resp.sendRedirect("#" + path.substring(1));
	}

}
