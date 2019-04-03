package com.prism.springas.utils.http;

import com.prism.springas.utils.BasePage;

import javax.servlet.http.HttpServletRequest;

public class SessionUtil {

	private static final String CURRENT_USER = "loginUser";
	
	public static void setUser(HttpServletRequest request, BasePage user) {
		request.getSession().setAttribute(CURRENT_USER, user);
	}

	public static BasePage getUser(HttpServletRequest request) {
		return (BasePage) request.getSession().getAttribute(CURRENT_USER);
	}

}
