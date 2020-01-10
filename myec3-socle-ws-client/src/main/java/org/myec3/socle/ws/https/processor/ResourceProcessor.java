package org.myec3.socle.ws.https.processor;

import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.sync.api.ResponseMessage;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Interface to map Socle Resources to External Resources
 * 
 * @param <T> Type to map to external API
 */
public interface ResourceProcessor<T extends Resource> {

	MultiValueMap<String, Object> map(T resource, HttpMethod method, RestTemplate rest);

	ResponseMessage process(HttpEntity requestEntity, HttpMethod method, RestTemplate rest, String uri,
			Long resourceId);
}
