/**
 * Copyright (c) 2011 Atos Bourgogne
 * 
 * This file is part of MyEc3.
 * 
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * 
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.ws.client.impl;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.jaxb.XmlJaxbAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.*;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.ws.client.ResourceWsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

/**
 * Concrete implementation of REST methods used to contact third-party
 * applications
 * 
 * @see ResourceWsClient
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("resourceWsClient")
public class ResourceWsClientImpl implements ResourceWsClient {

	private static Logger logger = LoggerFactory.getLogger(ResourceWsClientImpl.class);

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
			System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
			System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
			System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
			System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
		}
		return this.clientWs;
	}

	/**
	 * Prepare Header before sending the request
	 * 
	 * @param builder  : the builder used to create the request
	 * @param resource : the resource to send
	 */
	private void prepareHeader(Builder builder, Resource resource) {

		if (resource.getExternalId().equals(0L)) {
			builder.header(getResourceName(resource) + "ExternalId", resource.getId());
		} else {
			builder.header(getResourceName(resource) + "ExternalId", resource.getExternalId());
		}

		if (AgentProfile.class.equals(resource.getClass())) {
			if (((AgentProfile) resource).getOrganismDepartment().getOrganism().getExternalId().equals(0L)) {
				builder.header("organismExternalId",
						((AgentProfile) resource).getOrganismDepartment().getOrganism().getId());
			} else {
				builder.header("organismExternalId",
						((AgentProfile) resource).getOrganismDepartment().getOrganism().getExternalId());
			}
		}

		if (OrganismDepartment.class.equals(resource.getClass())) {
			if (((OrganismDepartment) resource).getOrganism().getExternalId().equals(0L)) {
				builder.header("organismExternalId", ((OrganismDepartment) resource).getOrganism().getId());
			} else {
				builder.header("organismExternalId", ((OrganismDepartment) resource).getOrganism().getExternalId());
			}
		}

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
				logger.info("REPONSE: {}",responseToString);
				// Display response content
				//logResponseContent(responseToString);

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
				String responseToString = response.readEntity(String.class);
				logger.info("REPONSE: {}",responseToString);
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
				.target(synchronizationSubscription.getUri() +"/" + resource.getId().toString());
		Builder builder = webResource.request().accept(MediaType.APPLICATION_XML);
		try {
			return builder.get();
		} catch (Exception ex) {
			logger.error(synchronizationSubscription.getApplication().getName() + " - " + ex.getMessage(), ex);
			ResponseBuilder errorResponse = Response.status(Status.SERVICE_UNAVAILABLE);
			return errorResponse.build();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response get(Long id, SynchronizationSubscription synchronizationSubscription) {
		WebTarget webResource = getClientWs().target(synchronizationSubscription.getUri() +"/" + id.toString());
		Builder builder = webResource.request().accept(MediaType.APPLICATION_XML);
		try {
			return builder.get();
		} catch (Exception ex) {
			logger.error(synchronizationSubscription.getApplication().getName() + " - " + ex.getMessage(), ex);
			ResponseBuilder errorResponse = Response.status(Status.SERVICE_UNAVAILABLE);
			return errorResponse.build();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage post(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		WebTarget webResource = getClientWs().target(synchronizationSubscription.getUri());

		Builder builder = webResource.request().accept(MediaType.APPLICATION_XML);

		try {
			logger.debug(synchronizationSubscription.getApplication().getName() + " - [POST] on URI: {}", synchronizationSubscription.getUri());
			Response response = builder.post(Entity.xml(resource));
			return buildResponseMessage(response, MethodType.POST);
		} catch (ClientErrorException ex) {
			if (ex.getMessage().contains(CONNECTION_EXCEPTION)) {
				logger.error(synchronizationSubscription.getApplication().getName() + " - [POST][ConnectException] Server Unavailable HTTP status 503", ex);
				return this.buildServerErrorMessage(resource, HttpStatus.SERVER_UNAVAILABLE, null, MethodType.POST,
						SERVER_UNVAILABLE_ERROR_LABEL, SERVER_UNVAILABLE_ERROR_MESSAGE);
			} else {
				logger.error(synchronizationSubscription.getApplication().getName() + " - [POST] Exception in sync", ex);
				return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST,
						ErrorCodeType.INTERNAL_CLIENT_ERROR, MethodType.POST, CLIENT_EXCEPTION_ERROR_LABEL,
						ex.getMessage());
			}
		} catch (Exception ex) {
			logger.error(synchronizationSubscription.getApplication().getName() + " - [POST] Exception in sync", ex);
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
		WebTarget webResource = getClientWs().target(synchronizationSubscription.getUri() +"/" + resource.getId());
		Builder builder = webResource.request().accept(MediaType.APPLICATION_XML);

		try {
			logger.debug(synchronizationSubscription.getApplication().getName() + " - [PUT] on URI : {}", synchronizationSubscription.getUri());
			Response response = builder.put(Entity.xml(resource));
			return buildResponseMessage(response, MethodType.PUT);
		} catch (ClientErrorException ex) {
			if (ex.getMessage().contains(CONNECTION_EXCEPTION)) {
				logger.error(synchronizationSubscription.getApplication().getName() + " - [PUT][ConnectException] Server Unavailable HTTP status 503", ex);
				return this.buildServerErrorMessage(resource, HttpStatus.SERVER_UNAVAILABLE, null, MethodType.PUT,
						SERVER_UNVAILABLE_ERROR_LABEL, SERVER_UNVAILABLE_ERROR_MESSAGE);
			} else {
				logger.error(synchronizationSubscription.getApplication().getName() + " - [PUT] Sync error:", ex);
				return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST,
						ErrorCodeType.INTERNAL_CLIENT_ERROR, MethodType.PUT, CLIENT_EXCEPTION_ERROR_LABEL,
						ex.getMessage());
			}
		} catch (Exception ex) {
			logger.error(synchronizationSubscription.getApplication().getName() + " - [PUT] Sync error:", ex);
			return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST, ErrorCodeType.INTERNAL_CLIENT_ERROR,
					MethodType.PUT, CLIENT_EXCEPTION_ERROR_LABEL, ex.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage putComplete(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		WebTarget webResource = getClientWs().target(synchronizationSubscription.getUri() +"/" + resource.getId());
		Builder builder = webResource.request().accept(MediaType.APPLICATION_XML);

		prepareHeader(builder, resource);
		try {
			logger.debug(synchronizationSubscription.getApplication().getName() + " - [PUT COMPLETE] on URI: {}", synchronizationSubscription.getUri());
			Response response = builder.put(Entity.xml(resource));
			return buildResponseMessage(response, MethodType.PUT);
		} catch (ClientErrorException ex) {
			if (ex.getMessage().contains(CONNECTION_EXCEPTION)) {
				logger.error(synchronizationSubscription.getApplication().getName() +
						" - [PUT COMPLETE][ConnectException] Server Unavailable HTTP status 503", ex);
				return this.buildServerErrorMessage(resource, HttpStatus.SERVER_UNAVAILABLE, null, MethodType.PUT,
						SERVER_UNVAILABLE_ERROR_LABEL, SERVER_UNVAILABLE_ERROR_MESSAGE);
			} else {
				logger.error(synchronizationSubscription.getApplication().getName() + " - [PUT COMPLETE] sync error:", ex);
				return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST,
						ErrorCodeType.INTERNAL_CLIENT_ERROR, MethodType.PUT, CLIENT_EXCEPTION_ERROR_LABEL,
						ex.getMessage());
			}
		} catch (Exception ex) {
			logger.error(synchronizationSubscription.getApplication().getName() + " - [PUT COMPLETE] sync error:", ex);
			return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST, ErrorCodeType.INTERNAL_CLIENT_ERROR,
					MethodType.PUT, CLIENT_EXCEPTION_ERROR_LABEL, ex.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage delete(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		WebTarget webResource = getClientWs().target(synchronizationSubscription.getUri() +"/" + resource.getId());
		Builder builder = webResource.request().accept(MediaType.APPLICATION_XML);

		prepareHeader(builder, resource);
		try {
			logger.debug(synchronizationSubscription.getApplication().getName() + " - [DELETE] on URI: {}", synchronizationSubscription.getUri());
			Response response = builder.delete();

			return buildResponseMessage(response, MethodType.DELETE);

		} catch (ClientErrorException ex) {
			if (ex.getMessage().contains(CONNECTION_EXCEPTION)) {
				logger.error(synchronizationSubscription.getApplication().getName() + " - [DELETE][ConnectException] Server Unavailable HTTP status 503", ex);
				return this.buildServerErrorMessage(resource, HttpStatus.SERVER_UNAVAILABLE, null, MethodType.DELETE,
						SERVER_UNVAILABLE_ERROR_LABEL, SERVER_UNVAILABLE_ERROR_MESSAGE);
			} else {
				logger.error(synchronizationSubscription.getApplication().getName() + " - [DELETE]", ex);
				return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST,
						ErrorCodeType.INTERNAL_CLIENT_ERROR, MethodType.DELETE, CLIENT_EXCEPTION_ERROR_LABEL,
						ex.getMessage());
			}
		} catch (Exception ex) {
			logger.error(synchronizationSubscription.getApplication().getName() + " - [DELETE]", ex);
			return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST, ErrorCodeType.INTERNAL_CLIENT_ERROR,
					MethodType.DELETE, CLIENT_EXCEPTION_ERROR_LABEL, ex.getMessage());
		}
	}

}
