package org.myec3.socle.ws.client.impl.mps.response;

import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.json.ApiGouvEtablissement;
import org.myec3.socle.core.domain.model.json.ApiGouvLinks;
import org.myec3.socle.core.domain.model.json.ApiGouvMeta;

/* Java Class used for the Etablissements WS response */
public class ResponseEtablissement {

	private ApiGouvEtablissement data;

	private ApiGouvLinks links;

	private ApiGouvMeta meta;

	public ApiGouvEtablissement getData() {
		return data;
	}

	public void setData(ApiGouvEtablissement data) {
		this.data = data;
	}

	public ApiGouvLinks getLinks() {
		return links;
	}

	public void setLinks(ApiGouvLinks links) {
		this.links = links;
	}

	public ApiGouvMeta getMeta() {
		return meta;
	}

	public void setMeta(ApiGouvMeta meta) {
		this.meta = meta;
	}

	@Override
	public String toString() {
		return "ResponseEtablissements{" +
				"data=" + data +
				", links=" + links +
				", meta=" + meta +'}';
	}

}
