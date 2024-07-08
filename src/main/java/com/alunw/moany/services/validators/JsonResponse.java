package com.alunw.moany.services.validators;

/**
 * JSON POJO for returning (JSON) result and user message for a RESTful request.
 *
 */
public class JsonResponse {

	/**
	 * User-friendly message.
	 */
	private String message = null;

	/**
	 * JSON result data
	 */
	private Object result = null;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}
