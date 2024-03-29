package org.myec3.socle.ws.client.impl.mps.response;

import org.myec3.socle.core.domain.model.json.ApiGouvMandataireSocial;
import org.myec3.socle.core.domain.model.json.ApiGouvMandataireSocialData;
import org.myec3.socle.core.domain.model.json.ApiGouvMeta;

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

	public ApiGouvMeta getMeta() {
		return meta;
	}

	public void setMeta(ApiGouvMeta meta) {
		this.meta = meta;
	}

	@Override
	public String toString() {
		return "ResponseEntreprises{" +
				"data=" + data != null ? data.toString() : "" +
				", meta=" + meta != null ? meta.toString() : "" +
				'}';
	}


}
