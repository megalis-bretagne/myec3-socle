package org.myec3.socle.ws.https.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;

import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.ws.dto.tdt.UserTdtDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class UserTdtProcessor extends AbstractResourceTdtProcessor<AgentProfile> {

	private static final Logger logger = LoggerFactory.getLogger(UserTdtProcessor.class);

	private static final String LOGIN_SUFFIX = "@maximilien.fr";

	@Value("${tdt.user.list.url}")
	private String userListUrl;

	@Value("${tdt.user.detail.url}")
	private String userDetailUrl;

	@Value("${tdt.authority.id}")
	private int authorityId;

	@Value("${tdt.authority.name}")
	private String authorityName;

	@Value("${certificate.temp.directory}")
	private String tempDirectory;

	@Override
	public MultiValueMap<String, Object> map(AgentProfile resource, HttpMethod method, RestTemplate rest) {
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
		parameters.add("api", "1");
		parameters.add("name", resource.getUser().getLastname());
		parameters.add("givenname", resource.getUser().getFirstname());
		// Create a login for the tdt
		// Can not use User.username since it changed when we update the email address
		parameters.add("login", getTdtLogin(resource));
		parameters.add("telephone", "");
		parameters.add("email", resource.getEmail());
		parameters.add("authority_id", authorityId);
		parameters.add("role", "USER");
		parameters.add("auth_method", 1);

		parameters.add("authority_name", authorityName);
		parameters.add("authority_group_id", "");

		addCertificate(parameters, resource.getUser().getCertificate());

		if (method.equals(HttpMethod.PUT) || method.equals(HttpMethod.DELETE)) {
			retrieveTdtId(resource, rest, parameters);
		}

		if (method.equals(HttpMethod.DELETE)) {
			parameters.add("status", 0);
		} else {
			parameters.add("status", 1);
		}

		return parameters;
	}

	private void retrieveTdtId(AgentProfile resource, RestTemplate rest, MultiValueMap<String, Object> parameters) {
		String name = resource.getUser().getLastname();
		ResponseEntity<UserTdtDto[]> responseUserEntity = rest.getForEntity(MessageFormat.format(userListUrl, name),
				UserTdtDto[].class);
		UserTdtDto[] users = responseUserEntity.getBody();

		for (UserTdtDto user : users) {
			ResponseEntity<UserTdtDto> responseOneUserEntity = rest.getForEntity(
					MessageFormat.format(userDetailUrl, user.getId()),
					UserTdtDto.class);
			if (responseOneUserEntity.getBody().getLogin()
					.equals(getTdtLogin(resource))) {
				parameters.add("id", user.getId());
				break;
			}
		}
	}

	private void addCertificate(MultiValueMap<String, Object> parameters, String certificate) {
		try {
			// create a temp file
			File tempDir = new File(tempDirectory);

			File temp = File.createTempFile("certificate", ".tmp", tempDir);
			// write it
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
				bw.write(certificate);
				Resource resource = new FileSystemResource(temp);
				parameters.add("certificate", resource);
			}
		} catch (IOException e) {
			logger.debug("Cannot add certificate to parameters ", e);
		}
	}

	private String getTdtLogin(AgentProfile resource) {
		return resource.getId() + LOGIN_SUFFIX;
	}
}
