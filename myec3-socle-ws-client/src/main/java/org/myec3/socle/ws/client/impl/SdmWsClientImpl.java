package org.myec3.socle.ws.client.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.BooleanUtils;
import org.glassfish.jersey.client.ClientProperties;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.sdm.model.SdmResource;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.*;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.core.service.TraiterReponseSDMService;
import org.myec3.socle.ws.client.ResourceWsClient;
import org.myec3.socle.ws.client.constants.WsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


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

    @Autowired
    @Qualifier("traiterReponseSDMService")
    private TraiterReponseSDMService traiterReponseSDMService;

    @Value("${myec3.synchro.sdm.token.uri:false}")
    private Boolean loadTokenInUri;

    /**
     * Creates and returns JerseyClient
     *
     * @return WS client
     */
    private Client getClientWs() {
        if (this.clientWs == null) {
            this.clientWs = ClientBuilder.newClient();
            this.clientWs.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
        }
        return this.clientWs;
    }

    /**
     * Prepare Header before sending the request
     *
     * @param builder : the builder used to create the request
     */
    private void prepareHeaderAtexo(Invocation.Builder builder) {
        if (BooleanUtils.isFalse(loadTokenInUri)) {
            builder.header("Authorization", "Bearer " + getTokenSdm());
        }
    }

    /**
     * Get Token for SDM
     * @return  the token
     */
    private String getTokenSdm() {
        WebTarget webResource = getClientWs().target(WsConstants.SDM_TOKEN_URL);
        Invocation.Builder builderToken = webResource.request().accept(MediaType.APPLICATION_JSON);
        try {
            Response response = builderToken.get();
            String responseToString = response.readEntity(String.class);
            String[] tab = StringUtils.split(responseToString, "<ticket>");
            String[] token = StringUtils.split(tab != null ? tab[1] : null, "</ticket>");
            return token != null ? token[0] : null;
        } catch (Exception e) {
            logger.error("[TOKEN] Failed retrieving SDM token", e);
            return null;
        }
    }

    private String getUri(String uri) {
        if (BooleanUtils.isTrue(loadTokenInUri)) {
            return uri+"?ticket="+getTokenSdm();
        } else {
            return uri;
        }
    }


    /**
     * This method allows to retrieve the {@link HttpStatus} through the Client
     * Response
     *
     * @param response : the client response received
     * @return the correct {@link HttpStatus} corresponding at the client response
     * status received
     */
    private HttpStatus retrieveHttpStatus(Response response) {
        logger.debug("Processing method retrieveHttpStatus...");
        HttpStatus[] httpStatusList = HttpStatus.values();
        for (HttpStatus httpStatus : httpStatusList) {
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
     * @param resource
     * @return a {@link ResponseMessage} used by the synchronization module in order
     * to log the request and perform error handling if necessary
     * @throws Exception
     */
    private ResponseMessage buildResponseMessage(Response response, MethodType methodType, Resource resource) throws Exception {
        ResponseMessage responseMsg = new ResponseMessage();
        // Set HttpStatus depending on client response value
        responseMsg.setHttpStatus(this.retrieveHttpStatus(response));

        if (responseMsg.getHttpStatus() != null) {
            if (responseMsg.getHttpStatus().getValue() > HttpStatus.NO_CONTENT.getValue()) {
                logger.debug("[buildResponseMessage] HttpStatus returned by webService Client: {}",
                        responseMsg.getHttpStatus().getValue());
                String responseToString = response.readEntity(String.class);
                logger.info("RESPONSE: {}", responseToString);
                try {
                    traiterReponseSDMService.traiterReponseKo(responseToString, responseMsg, resource);
                } catch (Exception e) {
                    Error error = new Error();
                    responseMsg.setError(error);
                    error.setErrorMessage("Erreur lors du parsing de la réponse de la SDM");
                    error.setErrorLabel("Erreur lors du parsing de la réponse de la SDM");
                    //mapping à faire
                    error.setErrorCode(ErrorCodeType.INTERNAL_CLIENT_ERROR);
                }
                // Fill the method used during the request
                responseMsg.getError().setMethodType(methodType);
            } else {
                // No error occurred during the request
                String responseToString = response.readEntity(String.class);
                // Display response content
                logger.info("RESPONSE: {}", responseToString);
                //traiter la reponse de la SDM
                try {
                    traiterReponseSDMService.traiterReponseOk(resource, responseToString);
                } catch (Exception e) {
                    logger.error("Error when parsing the OK response from the SDM", e);
                    throw e;
                }

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

    public ResponseMessage post(Resource resource, SdmResource resourceSDM, SynchronizationSubscription synchronizationSubscription) {
        WebTarget webResource = getClientWs().target(getUri(synchronizationSubscription.getUri()));

        Invocation.Builder builder = webResource.request().accept(MediaType.APPLICATION_JSON);
        prepareHeaderAtexo(builder);
        logger.info("[POST] on URI: {}", synchronizationSubscription.getUri());
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(resourceSDM);
            logger.info("[POST] REQUEST: {}", json);
        } catch (Exception e) {
            logger.warn("[POST] Problems encountered when processing JSON content", e);
        }
        try {

            Response response = builder.post(Entity.json(resourceSDM));
            return buildResponseMessage(response, MethodType.POST, resource);
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

    public ResponseMessage put(Resource resource, SdmResource resourceSDM, SynchronizationSubscription synchronizationSubscription) {
        WebTarget webResource = getClientWs().target(getUri(synchronizationSubscription.getUri()));

        Invocation.Builder builder = webResource.request().accept(MediaType.APPLICATION_JSON);
        prepareHeaderAtexo(builder);

        logger.info("[PUT] on URI: {}", synchronizationSubscription.getUri());
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(resourceSDM);
            logger.info("[PUT] REQUEST: {}", json);
        } catch (Exception e) {
            logger.warn("[PUT] Problems encountered when processing JSON content", e);
        }

        try {

            Response response = builder.put(Entity.json(resourceSDM));
            return buildResponseMessage(response, MethodType.PUT, resource);
        } catch (ClientErrorException ex) {
            if (ex.getMessage().contains(CONNECTION_EXCEPTION)) {
                logger.error("[PUT][ConnectException] Server Unavailable HTTP status 503", ex);
                return this.buildServerErrorMessage(resource, HttpStatus.SERVER_UNAVAILABLE, null, MethodType.PUT,
                        SERVER_UNVAILABLE_ERROR_LABEL, SERVER_UNVAILABLE_ERROR_MESSAGE);
            } else {
                logger.error("[PUT] Exception in sync", ex);
                return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST,
                        ErrorCodeType.INTERNAL_CLIENT_ERROR, MethodType.PUT, CLIENT_EXCEPTION_ERROR_LABEL,
                        ex.getMessage());
            }
        } catch (Exception ex) {
            logger.error("[PUT] Exception in sync", ex);
            return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST,
                    ErrorCodeType.INTERNAL_CLIENT_ERROR,
                    MethodType.PUT, CLIENT_EXCEPTION_ERROR_LABEL, ex.getMessage());
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Response get(Resource resource, SynchronizationSubscription synchronizationSubscription) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response get(Long id, SynchronizationSubscription synchronizationSubscription) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage post(Resource resource, SynchronizationSubscription synchronizationSubscription) {

        throw new UnsupportedOperationException("Not implemented.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage put(Resource resource, SynchronizationSubscription synchronizationSubscription) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public ResponseMessage putComplete(Resource resource, SynchronizationSubscription synchronizationSubscription) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public ResponseMessage delete(Resource resource, SynchronizationSubscription synchronizationSubscription) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    public ResponseMessage delete(Resource resource, SdmResource resourceSDM, SynchronizationSubscription synchronizationSubscription) {
        WebTarget webResource = getClientWs().target(getUri(synchronizationSubscription.getUri()));

        Invocation.Builder builder = webResource.request().accept(MediaType.APPLICATION_JSON);
        prepareHeaderAtexo(builder);

        logger.info("[DELETE] on URI: {}", synchronizationSubscription.getUri());
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(resourceSDM);
            logger.info("[DELETE] REQUEST: {}", json);
        } catch (JsonProcessingException e) {
            logger.warn("[DELETE] Problems encountered when processing JSON content", e);
        }

        try {
            Response response = builder.build(MethodType.DELETE.name(), Entity.json(resourceSDM)).invoke();
            return buildResponseMessage(response, MethodType.DELETE, resource);
        } catch (ClientErrorException ex) {
            if (ex.getMessage().contains(CONNECTION_EXCEPTION)) {
                logger.error("[DELETE][ConnectException] Server Unavailable HTTP status 503", ex);
                return this.buildServerErrorMessage(resource, HttpStatus.SERVER_UNAVAILABLE, null, MethodType.DELETE,
                        SERVER_UNVAILABLE_ERROR_LABEL, SERVER_UNVAILABLE_ERROR_MESSAGE);
            } else {
                logger.error("[DELETE] Exception in sync", ex);
                return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST,
                        ErrorCodeType.INTERNAL_CLIENT_ERROR, MethodType.DELETE, CLIENT_EXCEPTION_ERROR_LABEL,
                        ex.getMessage());
            }
        } catch (Exception ex) {
            logger.error("[DELETE] Exception in sync", ex);
            return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST,
                    ErrorCodeType.INTERNAL_CLIENT_ERROR,
                    MethodType.DELETE, CLIENT_EXCEPTION_ERROR_LABEL, ex.getMessage());
        }
    }

}
