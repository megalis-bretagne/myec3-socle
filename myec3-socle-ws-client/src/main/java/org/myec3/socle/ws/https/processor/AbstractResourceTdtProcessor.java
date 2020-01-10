package org.myec3.socle.ws.https.processor;

import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.ErrorCodeType;
import org.myec3.socle.core.sync.api.HttpStatus;
import org.myec3.socle.core.sync.api.MethodType;
import org.myec3.socle.core.sync.api.ResponseMessage;
import org.myec3.socle.ws.dto.tdt.ResponseTdTDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public abstract class AbstractResourceTdtProcessor<T extends Resource> implements ResourceProcessor<T> {

	private static final String OK = "ok";

	@Override
	public ResponseMessage process(HttpEntity requestEntity, HttpMethod method,
			RestTemplate rest, String uri, Long resourceId) {
		ResponseEntity<ResponseTdTDto> responseEntity = rest.exchange(uri,
				HttpMethod.POST,
				requestEntity,
				ResponseTdTDto.class);
		return createResponseMessage(responseEntity.getBody(), MethodType.POST, resourceId);
	}

	private ResponseMessage createResponseMessage(ResponseTdTDto responseTdTDto, MethodType methodType,
			Long resourceId) {
		ResponseMessage responseMessage;
		if (OK.equals(responseTdTDto.getStatus())) {
			responseMessage = new ResponseMessage(HttpStatus.OK);
		} else {
			Error error = new Error(ErrorCodeType.INTERNAL_SERVER_ERROR, responseTdTDto.getErrorMessage(),
					responseTdTDto.getErrorMessage(), resourceId, methodType);
			responseMessage = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, error);
		}

		return responseMessage;
	}
}
