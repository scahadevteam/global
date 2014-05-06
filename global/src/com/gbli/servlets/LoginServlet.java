package com.gbli.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gbli.busobj.GbliConstants;
import com.gbli.busobj.Profile;
import com.gbli.context.ContextManager;


public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		HttpSession session = req.getSession();

		String account = req.getParameter("uname");
		String password = req.getParameter("pwd");

		int iFailedAttempts = 0;
		if (session.getAttribute(GbliConstants.LOGIN_FAILED_ATTEMPTS) != null) {
			iFailedAttempts =  (int)session.getAttribute(GbliConstants.LOGIN_FAILED_ATTEMPTS);
		}
		
		//
		// Lets validate the user.. and get back a profile..
		
		Profile pro = validate(account, password);
		
		LOGGER.info("PERFORMING LOGIN: attempt #" + iFailedAttempts);
		
		if (pro != null) {

			// Valid login. Make a note in the session object. And Reset Counters
			session.setAttribute(GbliConstants.PROFILE, pro);
			session.setAttribute(GbliConstants.PROFILE_NICKNAME, pro.getNickName());
			session.setAttribute(GbliConstants.LOGIN_FAILED_ATTEMPTS,0);
			
			// Try redirecting the client to the page he first tried to access
			try {
				String starget = (String) session.getAttribute(GbliConstants.LOGIN_TARGET);
				
				//
				// remove any target information.. now that login was successfull
				//
				session.removeAttribute(GbliConstants.LOGIN_TARGET);
				
				LOGGER.info("Redirecting to:" + starget);
				if (starget != null) {
					res.sendRedirect(starget);
					return;
				}
			} catch (Exception ignored) {
			}

			// Couldn't redirect to the target. Redirect to the site's home
			// page.
			res.sendRedirect("/the-rinks/sec/Welcome.jsp");

		} else {
			
			//
			// User failed to Login!  place him back to login
			//
			res.sendRedirect("/the-rinks/sec/Welcome.jsp");
			session.setAttribute(GbliConstants.LOGIN_FAILED_ATTEMPTS,iFailedAttempts+1);
			
		}
	}

	// Generate a profile
	protected Profile validate(String _sUser, String _sPwd) {
		return Profile.verify(_sUser, _sPwd);
	}
}
