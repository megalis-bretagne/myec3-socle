package org.myec3.socle.ws.https;

import java.security.cert.CertificateException;

import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.sync.api.ResponseMessage;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.ws.https.connection.HttpsConnection;
import org.myec3.socle.ws.https.processor.ResourceProcessor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Class used to request external API through HTTPS
 */
public class ConnectionContext {

	private HttpsConnection connection;

	private ResourceProcessor processor;

	public ConnectionContext(ResourceProcessor processor, HttpsConnection connection) {
		this.connection = connection;
		this.processor = processor;
	}

	public RestTemplate getRestTemplate(SynchronizationSubscription synchronizationSubscription)
			throws CertificateException {
		return connection.getRestTemplate(synchronizationSubscription);
	}

	public HttpEntity getRequestEntity(MultiValueMap<String, Object> parameters) {
		return connection.getRequestEntity(parameters);
	}

	public MultiValueMap<String, Object> map(Resource resource, HttpMethod method, RestTemplate rest) {
		return processor.map(resource, method, rest);
	}

	public ResponseMessage process(HttpEntity requestEntity, HttpMethod method, RestTemplate rest, String uri,
			Long resourceId) {
		return processor.process(requestEntity, method, rest, uri, resourceId);
	}

}
