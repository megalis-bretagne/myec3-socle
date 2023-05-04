package org.myec3.socle.ws.client.impl.mps.response;

import org.myec3.socle.core.domain.model.json.ApiGouvLinks;
import org.myec3.socle.core.domain.model.json.ApiGouvMandataireSocial;
import org.myec3.socle.core.domain.model.json.ApiGouvMeta;
import org.myec3.socle.core.domain.model.json.ApiGouvUniteLegale;

import java.util.List;

public class ResponseMandataires {

	private List<ApiGouvMandataireSocial> data;

	private ApiGouvMeta meta;

	public List<ApiGouvMandataireSocial> getData() {
		return data;
	}

	public void setData(List<ApiGouvMandataireSocial> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ResponseEntreprises{" +
				"data=" + data +
				", meta=" + meta +
				'}';
	}


}
