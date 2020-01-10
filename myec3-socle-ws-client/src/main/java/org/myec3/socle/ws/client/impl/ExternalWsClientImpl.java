package org.myec3.socle.ws.client.impl;

import java.security.InvalidParameterException;
import java.security.cert.CertificateException;
import java.text.MessageFormat;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.HttpStatus;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.ResponseMessage;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.ws.client.ResourceWsClient;
import org.myec3.socle.ws.https.ConnectionContext;
import org.myec3.socle.ws.https.connection.HttpsConnection;
import org.myec3.socle.ws.https.connection.HttpsConnectionImpl;
import org.myec3.socle.ws.https.processor.OrganismeTdtProcessor;
import org.myec3.socle.ws.https.processor.UserTdtProcessor;
import org.myec3.socle.ws.utils.ApplicationName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


/**
 * Implementation of {@link ResourceWsClient} to contact third-arty application
 * throught HTTPS
 */
@Component("externalWsClient")
public class ExternalWsClientImpl implements ResourceWsClient {

	private static final Logger logger = LoggerFactory.getLogger(ExternalWsClientImpl.class);

	private ConnectionContext connection;

	@Value("${http.proxy.url}")
	private String httpProxy;

	@Value("${http.proxy.port}")
	private int httpProxyPort;

	@Autowired
	private UserTdtProcessor userTdtProcessor;

	@Autowired
	private OrganismeTdtProcessor organismeTdtProcessor;

	private HttpsConnection tdtConnection;

	@Override
	public Response get(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Response get(Long id, SynchronizationSubscription synchronizationSubscription) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public ResponseMessage post(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		try {
			logger.info("[POST] on URI : {}", synchronizationSubscription.getUri());
			getConnection(resource, synchronizationSubscription);
			RestTemplate rest = connection.getRestTemplate(synchronizationSubscription);
			MultiValueMap<String, Object> parameters = connection.map(resource, HttpMethod.POST, rest);
			HttpEntity<Map> requestEntity = connection.getRequestEntity(parameters);
			return connection.process(requestEntity, HttpMethod.POST, rest, synchronizationSubscription.getUri(),
					resource.getId());
		} catch (CertificateException e) {
			logger.error("[POST] Error handling certificate ", e);
			Error error = new Error(ErrorCodeType.INTERNAL_CLIENT_ERROR, "Internal Error",
					"[POST] Error handling certificate", resource.getId(), MethodType.POST);
			return new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, error);
		}
	}

	@Override
	public ResponseMessage put(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		try {
			logger.debug("[PUT] on URI : {}", synchronizationSubscription.getUri());
			getConnection(resource, synchronizationSubscription);
			RestTemplate rest = connection.getRestTemplate(synchronizationSubscription);
			MultiValueMap<String, Object> parameters = connection.map(resource, HttpMethod.PUT, rest);
			HttpEntity<Map> requestEntity = connection.getRequestEntity(parameters);
			return connection.process(requestEntity, HttpMethod.PUT, rest, synchronizationSubscription.getUri(),
					resource.getId());
		} catch (CertificateException e) {
			logger.error("[PUT] Error handling certificate ", e);
			Error error = new Error(ErrorCodeType.INTERNAL_CLIENT_ERROR, "Internal Error",
					"[PUT] Error handling certificate", resource.getId(), MethodType.PUT);
			return new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, error);
		}
	}

	@Override
	public ResponseMessage putComplete(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public ResponseMessage delete(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		try {
			logger.debug("[DELETE] on URI : {}", synchronizationSubscription.getUri());
			getConnection(resource, synchronizationSubscription);
			RestTemplate rest = connection.getRestTemplate(synchronizationSubscription);
			MultiValueMap<String, Object> parameters = connection.map(resource, HttpMethod.DELETE, rest);
			HttpEntity<Map> requestEntity = connection.getRequestEntity(parameters);
			return connection.process(requestEntity, HttpMethod.DELETE, rest, synchronizationSubscription.getUri(),
					resource.getId());
		} catch (CertificateException e) {
			logger.error("[DELETE] Error handling certificate ", e);
			Error error = new Error(ErrorCodeType.INTERNAL_CLIENT_ERROR, "Internal Error",
					"[DELETE] Error handling certificate", resource.getId(), MethodType.DELETE);
			return new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, error);
		}
	}

	private void getConnection(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		if (this.tdtConnection == null) {
			this.tdtConnection = new HttpsConnectionImpl().withProxy(httpProxy, httpProxyPort);
		}
		if (resource instanceof AgentProfile
				&& synchronizationSubscription.getApplication().getName().equals(ApplicationName.TDT.getName())) {
			connection = new ConnectionContext(userTdtProcessor, tdtConnection);
		} else if (resource instanceof Organism
				&& synchronizationSubscription.getApplication().getName().equals(ApplicationName.TDT.getName())) {
			connection = new ConnectionContext(organismeTdtProcessor, tdtConnection);
		} else {
			throw new InvalidParameterException(MessageFormat.format(
					"No mapper could be found for application {} and resource {}",
					synchronizationSubscription.getApplication().getName(), resource.getClass().toGenericString()));
		}
	}
}
