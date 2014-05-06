package com.gbli.servlets.filters;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gbli.busobj.Profile;
import com.gbli.busobj.GbliConstants;

import com.gbli.context.ContextManager;

/**
 * Servlet Filter implementation class ProfileChecker
 */
@WebFilter("/ProfileChecker")
public class ProfileChecker implements Filter {
	
	 private final static Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	 
	 private static Vector<String> c_vSafeURLs = new Vector<String>();

	 static {
		 c_vSafeURLs.add("/the-rinks/Login");
		 c_vSafeURLs.add("/the-rinks/Welcome.jsp");
	 }

	 /**
     * Default constructor. 
     */
    public ProfileChecker() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		//
		// Ok.. Lets get all the session info here..
		// This filter essentially checks to make sure the profile that is in the session..
		// Has the permissions to go the the requested page URL.
		//
		HttpServletRequest sreq = (HttpServletRequest)request;
		HttpServletResponse sres = (HttpServletResponse)response;
		HttpSession sess = sreq.getSession();
		String sURL = sreq.getRequestURI();
		
		//Get the IP address of client machine.        
		String ipAddress = sreq.getRemoteAddr();                
		
		//
		// Lets not fire for any images..
		// 
		// We can add to this over time
		//
		//if (sURL.contains("images")) {
		//	LOGGER.info("Filter URL is:" + sURL + ". Bypassing any checks here");
		//	return;
			//chain.doFilter(request, response);
		//}

		Profile pro = (Profile)sess.getAttribute(GbliConstants.PROFILE);
		String starget = (String)sess.getAttribute(GbliConstants.LOGIN_TARGET);

		LOGGER.info("Profile is:" + pro);
		LOGGER.info("target is:" + starget);
		LOGGER.info("sURL is:" + sURL);
		

		//
		// if they are attempting to login.. don't do a thing.. let it pass
		//
		if (sURL.equals("/the-rinks/Login")) {
			
			LOGGER.info("Trying to log in now.. IP: "+ipAddress + ", Time " + new Date().toString());
			chain.doFilter(request, response);

		} else if (pro == null) {

			//
			// ok.. if there is no Profile.. they go directly to the Welcome Page
			//
			
			LOGGER.info("Redirecting to Welcome.jsp for IP: "+ipAddress + ", Time " + new Date().toString());
			
			//
			// only set it once..
			//
			if (sess.getAttribute(GbliConstants.LOGIN_TARGET) == null) {
				sess.setAttribute(GbliConstants.LOGIN_TARGET, sURL);
			}

			request.getRequestDispatcher("/sec/Welcome.jsp").forward(request, response);
			
		} else {
			//
			// They have already been authenticated
			//
			LOGGER.info("User already authenticated  "+ipAddress + ", Time " + new Date().toString());
			chain.doFilter(request, response);
		}
		
		
		
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		LOGGER.info("ProfileChecker Filter has been instansiated");
		
	}

}
