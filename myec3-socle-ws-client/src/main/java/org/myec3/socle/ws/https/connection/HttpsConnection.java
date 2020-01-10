package org.myec3.socle.ws.https.connection;

import java.security.cert.CertificateException;

import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Interface used to do https connection
 */
public interface HttpsConnection {

	RestTemplate getRestTemplate(SynchronizationSubscription synchronizationSubscription) throws CertificateException;

	HttpEntity getRequestEntity(MultiValueMap<String, Object> parameters);
}
