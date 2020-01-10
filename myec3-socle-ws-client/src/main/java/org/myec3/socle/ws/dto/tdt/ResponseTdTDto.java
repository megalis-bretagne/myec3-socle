package org.myec3.socle.ws.dto.tdt;

import com.fasterxml.jackson.annotation.JsonSetter;

public class ResponseTdTDto {

	private String status;

	private String message;

	@JsonSetter("error-message")
	private String errorMessage;

	private String id;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
