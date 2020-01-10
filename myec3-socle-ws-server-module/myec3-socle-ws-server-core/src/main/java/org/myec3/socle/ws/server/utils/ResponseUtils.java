package org.myec3.socle.ws.server.utils;

import javax.ws.rs.WebApplicationException;

import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.MethodType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

public class ResponseUtils {

	/**
	 * Method which allows to log the webapplicationException catched and return an
	 * {@link Error} to the webservice client.
	 * 
	 * @param exception : the WebApplicationException catched
	 * @return an {@link Error} object to the webservice client
	 */
	public static ResponseEntity errorResponse(WebApplicationException exception, MethodType methodType,
			Logger logger) {
		Assert.notNull(exception, "WebApplicationException can't be null");
		if (exception.getResponse().getEntity().getClass().equals(Error.class)) {
			Error error = (Error) exception.getResponse().getEntity();
			logger.error("[STATUS] : " + exception.getResponse().getStatus());
			logger.error("[LABEL] : " + error.getErrorLabel());
			logger.error("[ERROR CODE] : " + error.getErrorCode());
			logger.error("[MESSAGE] : " + error.getErrorMessage());
			logger.error("[METHOD TYPE] : " + error.getMethodType());
			logger.error("[RESOURCE ID] : " + error.getResourceId());
			return ResponseEntity.status(exception.getResponse().getStatus()).contentType(MediaType.APPLICATION_XML)
					.body(error);
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_XML)
				.body(new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, "Internal Server Error",
						"An unexpected error has occured : " + exception.getCause() + " " + exception.getMessage(),
						new Long(0),
						methodType));
	}
}
