package com.alunw.moany.rest;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO A very basic controller to output application status. Currently only
 * outputs status = "OK" (as JSON) - to indicate that the application is running.
 *
 */
@RestController
@RequestMapping("/status")
public class RestStatusController {

	private static Logger logger = LoggerFactory.getLogger(RestStatusController.class);

	/**
	 * Returns the current authenticated user name as a JSON object
	 * 
	 * @param auth
	 * @return 
	 */
	@RequestMapping(method = RequestMethod.GET)
	@CrossOrigin("*")
	public Map<String, Object> user(Authentication auth) {
		if (auth != null) {
			logger.debug("Current user is {} [{}]", auth.getName(), auth.getDetails());
		} else {
			logger.warn("No authentication token found.");
		}
		
		Map<String, Object> result = new HashMap<>();
		result.put("status", "OK");
		return result;
	}
}
