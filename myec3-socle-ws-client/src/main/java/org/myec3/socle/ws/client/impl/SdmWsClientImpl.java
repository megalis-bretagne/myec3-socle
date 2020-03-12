package org.myec3.socle.ws.client.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.sdm.model.SdmResource;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.*;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.ws.client.ResourceWsClient;
import org.myec3.socle.ws.client.constants.WsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;


/**
 * Implementation of {@link ResourceWsClient} to contact third-arty application
 * throught HTTPS
 */
@Component("sdmWsClientImpl")
public class SdmWsClientImpl implements ResourceWsClient {

	private static final Logger logger = LoggerFactory.getLogger(SdmWsClientImpl.class);

	private static final String SERVER_UNVAILABLE_ERROR_LABEL = "Server unavailable";
	private static final String SERVER_UNVAILABLE_ERROR_MESSAGE = "Impossible to contact distant server";
	private static final String CONNECTION_EXCEPTION = "java.net.ConnectException: Connection refused";
	private static final String CLIENT_EXCEPTION_ERROR_LABEL = "Client WS Exception";

	private Client clientWs = null;

	/**
	 * Creates and returns JerseyClient
	 *
	 * @return WS client
	 */
	private Client getClientWs() {
		if (this.clientWs == null) {
			this.clientWs = JerseyClientBuilder.newClient();
		}
		return this.clientWs;
	}

	/**
	 * Prepare Header before sending the request
	 *
	 * @param builder  : the builder used to create the request
	 */
	private void prepareHeaderAtexo(Invocation.Builder builder) {


		WebTarget webResource = getClientWs().target(WsConstants.SDM_TOKEN_URL);
		Invocation.Builder builderToken = webResource.request().accept(MediaType.APPLICATION_JSON);
		builderToken.header("externalid", 1122);
		builderToken.header("usertype", "AGENT");

		Response response = builderToken.get();
		String responseToString = response.readEntity(String.class);
		String[] tab= StringUtils.split(responseToString,"<ticket>");
		String[] tab2 =StringUtils.split(tab[1],"</ticket>");

		builder.header("Authorization", "Bearer " + tab2[0]);
		builder.header("externalid", 1122);
		builder.header("usertype", "AGENT");

	}

	/**
	 * Return the name of a resource
	 *
	 * @param resource : the resource to send
	 * @return the resource's name
	 */
	private String getResourceName(Resource resource) {
		logger.debug("Processing method getResourceName...");
		String resourceName = null;
		resourceName = resource.getClass().getSimpleName();
		return resourceName.substring(0, 1).toLowerCase() + resourceName.substring(1);
	}

	/**
	 * This method allows to retrieve the {@link HttpStatus} through the Client
	 * Response
	 *
	 * @param response : the client response received
	 * @return the correct {@link HttpStatus} corresponding at the client response
	 *         status received
	 */
	private HttpStatus retrieveHttpStatus(Response response) {
		logger.debug("Processing method retrieveHttpStatus...");
		HttpStatus[] HttpStatusList = HttpStatus.values();
		for (HttpStatus httpStatus : HttpStatusList) {
			if (response.getStatus() == httpStatus.getValue()) {
				logger.debug("HttpStatus contained into client response : {}", httpStatus);
				return httpStatus;
			}
		}
		return null;
	}

	/**
	 * Build the response message before sending it for error checking
	 *
	 * @param response   : the response returned by the ws client
	 * @param methodType : the {@link MethodType} used to perform the request
	 * @return a {@link ResponseMessage} used by the synchronization module in order
	 *         to log the request and perform error handling if necessary
	 * @throws Exception
	 */
	private ResponseMessage buildResponseMessage(Response response, MethodType methodType) throws Exception {
		ResponseMessage responseMsg = new ResponseMessage();
		// Set HttpStatus depending on client response value
		responseMsg.setHttpStatus(this.retrieveHttpStatus(response));

		if (!(responseMsg.getHttpStatus() == null)) {
			if (responseMsg.getHttpStatus().getValue() > HttpStatus.NO_CONTENT.getValue()) {
				logger.debug("[buildResponseMessage] HttpStatus returned by webService Client: {}",
						responseMsg.getHttpStatus().getValue());

				String responseToString = response.readEntity(String.class);
				// Display response content
				logResponseContent(responseToString);

				// Set entity error contained into client response to response
				// message
				// Jersey has difficulties to convert the response to Error natively. We are
				// doing it now by ourselves

				logger.debug("Content of response in string : " + responseToString);

				ObjectMapper mapper = new XmlMapper();
				mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				mapper.registerModule(new JaxbAnnotationModule());
				Error error = mapper.readValue(responseToString, Error.class);

				responseMsg.setError(error);

				// Fill the method used during the request
				responseMsg.getError().setMethodType(methodType);
			} else {
				// No error occurred during the request
				responseMsg.setError(null);
			}
		} else {
			throw new Exception("The HTTP status returned is not managed : " + response.getStatus());
		}
		return responseMsg;
	}

	/**
	 * Set {@link Error} into {@link ResponseMessage} if the http status returned
	 * equals 500 or 503
	 *
	 * @param resource   : the {@link Resource} concerned by the request
	 * @param httpStatus : the httpStatus returned by the client
	 * @param methodType : the type of REST request used
	 * @return a ResponseMessage
	 */
	private ResponseMessage buildServerErrorMessage(Resource resource, HttpStatus httpStatus, ErrorCodeType errorCode,
													MethodType methodType, String errorLabel, String errorMessage) {
		ResponseMessage responseMsg = new ResponseMessage();
		// Create error
		Error error = new Error();
		error.setErrorLabel(errorLabel);
		error.setErrorMessage(errorMessage);
		error.setResourceId(resource.getId());
		error.setMethodType(methodType);
		error.setErrorCode(errorCode);

		// Create responseMessage
		responseMsg.setHttpStatus(httpStatus);
		responseMsg.setError(error);

		return responseMsg;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response get(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		WebTarget webResource = getClientWs()
				.target(synchronizationSubscription.getUri() + resource.getId().toString());
		Invocation.Builder builder = webResource.request().accept(MediaType.APPLICATION_XML);
		try {
			return builder.get();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			Response.ResponseBuilder errorResponse = Response.status(Response.Status.SERVICE_UNAVAILABLE);
			return errorResponse.build();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response get(Long id, SynchronizationSubscription synchronizationSubscription) {
		WebTarget webResource = getClientWs().target(synchronizationSubscription.getUri() + id.toString());
		Invocation.Builder builder = webResource.request().accept(MediaType.APPLICATION_XML);
		try {
			return builder.get();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			Response.ResponseBuilder errorResponse = Response.status(Response.Status.SERVICE_UNAVAILABLE);
			return errorResponse.build();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage post(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		WebTarget webResource = getClientWs().target(synchronizationSubscription.getUri());

		throw new UnsupportedOperationException("Not implemented.");
	}

	public ResponseMessage post(Resource resource, SdmResource resourceSDM,SynchronizationSubscription synchronizationSubscription) {
		WebTarget webResource = getClientWs().target(synchronizationSubscription.getUri());

		Invocation.Builder builder = webResource.request().accept(MediaType.APPLICATION_JSON);
		//patch pour ajouter les trucs dans le header
		//Authorization, externalId et usertype
		prepareHeaderAtexo(builder);
		try {
			logger.debug("[POST] on URI: {}", synchronizationSubscription.getUri());
			Response response = builder.post(Entity.json(resourceSDM));

			return buildResponseMessage(response, MethodType.POST);
		} catch (ClientErrorException ex) {
			if (ex.getMessage().contains(CONNECTION_EXCEPTION)) {
				logger.error("[POST][ConnectException] Server Unavailable HTTP status 503", ex);
				return this.buildServerErrorMessage(resource, HttpStatus.SERVER_UNAVAILABLE, null, MethodType.POST,
						SERVER_UNVAILABLE_ERROR_LABEL, SERVER_UNVAILABLE_ERROR_MESSAGE);
			} else {
				logger.error("[POST] Exception in sync", ex);
				return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST,
						ErrorCodeType.INTERNAL_CLIENT_ERROR, MethodType.POST, CLIENT_EXCEPTION_ERROR_LABEL,
						ex.getMessage());
			}
		} catch (Exception ex) {
			logger.error("[POST] Exception in sync", ex);
			return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST,
					ErrorCodeType.INTERNAL_CLIENT_ERROR,
					MethodType.POST, CLIENT_EXCEPTION_ERROR_LABEL, ex.getMessage());
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage put(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		WebTarget webResource = getClientWs().target(synchronizationSubscription.getUri() + resource.getId());

		Invocation.Builder builder = webResource.request().accept(MediaType.APPLICATION_XML);
		if (synchronizationSubscription.getApplication().getId().equals(3l))
			prepareHeaderAtexo(builder);

		try {
			logger.debug("[PUT] on URI : {}", synchronizationSubscription.getUri());

			Response response = builder.put(Entity.json(resource));
			return buildResponseMessage(response, MethodType.PUT);
		} catch (ClientErrorException ex) {
			if (ex.getMessage().contains(CONNECTION_EXCEPTION)) {
				logger.error("[PUT][ConnectException] Server Unavailable HTTP status 503", ex);
				return this.buildServerErrorMessage(resource, HttpStatus.SERVER_UNAVAILABLE, null, MethodType.PUT,
						SERVER_UNVAILABLE_ERROR_LABEL, SERVER_UNVAILABLE_ERROR_MESSAGE);
			} else {
				logger.error("[PUT] Syncmerc error:", ex);
				return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST,
						ErrorCodeType.INTERNAL_CLIENT_ERROR, MethodType.PUT, CLIENT_EXCEPTION_ERROR_LABEL,
						ex.getMessage());
			}
		} catch (Exception ex) {
			logger.error("[PUT] Sync error:", ex);
			return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST, ErrorCodeType.INTERNAL_CLIENT_ERROR,
					MethodType.PUT, CLIENT_EXCEPTION_ERROR_LABEL, ex.getMessage());
		}
	}

	@Override
	public ResponseMessage putComplete(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public ResponseMessage delete(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		throw new UnsupportedOperationException("Not implemented.");
	}


	/**
	 * This method allows to log the content of the client response received
	 *
	 * @param response : the Response body received as a string
	 */
	private void logResponseContent(String response) {
		try {
			// We display the content of inputStream
			OutputStream outStream = System.out;
			try (Writer w = new OutputStreamWriter(outStream, "UTF-8")) {
				w.write(response);
			}
		} catch (Exception e) {
			logger.error("Failed to write response content : ", e);
		}
	}

}
