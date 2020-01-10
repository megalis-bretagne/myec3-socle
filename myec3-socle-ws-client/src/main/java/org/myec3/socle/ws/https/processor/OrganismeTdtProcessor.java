package org.myec3.socle.ws.https.processor;

import java.text.MessageFormat;
import java.util.Arrays;

import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.ws.dto.tdt.OrganismeTdtDto;
import org.myec3.socle.ws.dto.tdt.ResponseTdTDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class OrganismeTdtProcessor extends AbstractResourceTdtProcessor<Organism> {

	private static final int POSTAL_CODE_CORSE_SOUTH_MIN = 20_000;

	private static final int POSTAL_CODE_CORSE_MIDDLE = 20_200;

	private static final int POSTAL_CODE_CORSE_NORTH_MAX = 20_700;

	private static final String CODE_CORSE_NORTH = "2B";

	private static final String CODE_CORSE_SOUTH = "2A";

	@Value("${tdt.organism.list.url}")
	private String organismListUrl;

	@Value("${tdt.organism.detail.url}")
	private String organismDetailUrl;

	@Value("${tdt.siren.list.url}")
	private String sirenListUrl;

	@Value("${tdt.siren.add.url}")
	private String sirenAddUrl;

	@Value("${tdt.authority.group.id}")
	private int authorityGroupId;

	@Value("${tdt.authority.type.id}")
	private String authorityTypeId;

	@Value("${tdt.district.id}")
	private String dsitrictId;

	@Override
	public MultiValueMap<String, Object> map(Organism resource, HttpMethod method, RestTemplate rest) {
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
		parameters.add("api", 1);

		parameters.add("name", resource.getLabel());
		parameters.add("siren", resource.getSiren());
		parameters.add("authority_groupe_id", authorityGroupId);
		parameters.add("authority_type_id", authorityTypeId);
		parameters.add("department", getDepartment(resource.getAddress().getPostalCode()));
		parameters.add("district", dsitrictId);
		// Actes
		parameters.add("perm_1", "1");
		// Helios
		parameters.add("perm_2", "1");
		// DIA
		parameters.add("perm_3", "0");
		// Mail
		parameters.add("perm_4", "0");
		parameters.add("address", resource.getAddress().getPostalAddress());
		parameters.add("postal_code", resource.getAddress().getPostalCode());
		parameters.add("city", resource.getAddress().getCity());

		if (method.equals(HttpMethod.PUT)) {
			retrieveTdtId(resource, rest, parameters);
		}

		if (method.equals(HttpMethod.DELETE)) {
			parameters.add("status", 0);
		} else {
			parameters.add("status", resource.getEnabled() ? "1" : "0");
		}

		addSiren(resource.getSiren(), rest);

		return parameters;
	}

	private void addSiren(String siren, RestTemplate rest) {
		ResponseEntity<String[]> responseEntity = rest.getForEntity(
				MessageFormat.format(sirenListUrl, authorityGroupId),
				String[].class);

		if (!Arrays.asList(responseEntity.getBody()).contains(siren)) {
			rest.getForEntity(MessageFormat.format(sirenAddUrl, authorityGroupId, siren),
					ResponseTdTDto.class);
		}
	}

	private void retrieveTdtId(Organism resource, RestTemplate rest, MultiValueMap<String, Object> parameters) {
		String siren = resource.getSiren();
		ResponseEntity<OrganismeTdtDto[]> responseOrganismeEntity = rest.getForEntity(
				MessageFormat.format(organismListUrl, siren),
				OrganismeTdtDto[].class);
		OrganismeTdtDto[] organismes = responseOrganismeEntity.getBody();

		for (OrganismeTdtDto organisme : organismes) {
			ResponseEntity<OrganismeTdtDto> responseOneOrgansimeEntity = rest.getForEntity(
					MessageFormat.format(organismDetailUrl, organisme.getId()),
					OrganismeTdtDto.class);
			if (responseOneOrgansimeEntity.getBody().getSiren().equals(resource.getSiren())) {
				parameters.add("id", organisme.getId());
				break;
			}
		}
	}

	private String getDepartment(String codePostal) {
		int codePostalInt = Integer.parseInt(codePostal);
		String code;
		if (codePostalInt >= POSTAL_CODE_CORSE_SOUTH_MIN && codePostalInt < POSTAL_CODE_CORSE_MIDDLE) {
			code = CODE_CORSE_SOUTH;
		} else if (codePostalInt >= POSTAL_CODE_CORSE_MIDDLE && codePostalInt < POSTAL_CODE_CORSE_NORTH_MAX) {
			code = CODE_CORSE_NORTH;
		} else {
			code = codePostal.substring(0, 2);
		}

		// add leading zero to string
		return String.format("%1$3s", code).replace(" ", "0");
	}
}
