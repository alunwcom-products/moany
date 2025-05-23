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

@RestController
@RequestMapping("/rest/user/v2/")
public class RestUserController {

	private static Logger logger = LoggerFactory.getLogger(RestUserController.class);

	/**
	 * Returns the current authenticated user name as a JSON object
	 *
	 * @param auth
	 * @return
	 */
	@RequestMapping(value={"/"}, method = RequestMethod.GET)
	@CrossOrigin("*")
	public Map<String, Object> user(Authentication auth) {
		if (auth != null) {
			logger.debug("Current user is {} [{}]", auth.getName(), auth.getDetails());
		} else {
			logger.warn("No authentication token found.");
		}

		Map<String, Object> result = new HashMap<>();
		result.put("name", auth.getName());
		return result;
	}
}
