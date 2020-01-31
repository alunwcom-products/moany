package com.alunw.moany.web;

import java.security.Principal;

import javax.annotation.PostConstruct;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Default controller for handling (Freemarker) web pages for this app.
 */
@Controller
public class DefaultController implements ErrorController {
	
	private static Logger logger = LoggerFactory.getLogger(DefaultController.class);
	
	@PostConstruct
	public void init() {
		logger.info("init()");
	}
	
	/**
	 * Application home/index page.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(path = "/", method = RequestMethod.GET)
	public ModelAndView home(Principal principal, HttpServletRequest request, HttpServletResponse response) {
		logger.info("status() [user={}, remote={}]", getPrincipalName(principal), request.getRemoteAddr());
		return new ModelAndView("index");
	}
	
	@RequestMapping("/error")
	public String handleError(HttpServletRequest request, HttpServletResponse response, Model model) {
		
		Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR; // Default to internal server error
		
		if (statusCode != null && statusCode instanceof Integer) {
			try {
				int value = Integer.valueOf(statusCode.toString());
				httpStatus = HttpStatus.resolve(value);
				if (httpStatus == null) {
					logger.error("Unable to resolve HTTP status code: '{}'", value);
				}
			} catch (NumberFormatException e) {
				logger.error("Cannot parse HTTP status code: '{}'", statusCode);
			}
		} else {
			logger.error("ERROR_STATUS_CODE is null or not Integer: {}", statusCode);
		}
		
		logger.info("handleError() [remote={}, uri = {}, statusCode = {})", request.getRemoteAddr(), request.getRequestURI(), statusCode);
		
		if (httpStatus == HttpStatus.UNAUTHORIZED) {
			// pass authentication header back to client
			String headerName = "WWW-Authenticate";
			String headerValue = response.getHeader(headerName);
			logger.info("unauthorized header [{}: {}]", headerName, headerValue);
			response.setHeader(headerName, headerValue);
		}
		
		model.addAttribute("value", httpStatus.value());
		model.addAttribute("reason", httpStatus.getReasonPhrase());
		return "error";
	}
	
	@Override
	public String getErrorPath() {
		return "/error";
	}
	
	private String getPrincipalName(Principal principal) {
		if (principal == null) {
			return null;
		} else {
			return principal.getName();
		}
	}
}
