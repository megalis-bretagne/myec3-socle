package org.myec3.socle.ws.client.impl.mps.response;

import org.myec3.socle.core.domain.model.json.ApiGouvLinks;
import org.myec3.socle.core.domain.model.json.ApiGouvMeta;
import org.myec3.socle.core.domain.model.json.ApiGouvUniteLegale;

public class ResponseUniteLegale {

	private ApiGouvUniteLegale data;

	private ApiGouvLinks links;

	private ApiGouvMeta meta;

	public ApiGouvUniteLegale getData() {
		return data;
	}

	public void setData(ApiGouvUniteLegale data) {
		this.data = data;
	}

	public ApiGouvMeta getMeta() {
		return meta;
	}

	public void setMeta(ApiGouvMeta meta) {
		this.meta = meta;
	}


	@Override
	public String toString() {
		return "ResponseEntreprises{" +
				"data=" + data +
				", links=" + links +
				", meta=" + meta +
				'}';
	}


}
