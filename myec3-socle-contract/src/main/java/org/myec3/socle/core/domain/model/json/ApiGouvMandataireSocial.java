package org.myec3.socle.core.domain.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiGouvMandataireSocial {

    @JsonProperty("data")
    public ApiGouvMandataireSocialData data;

    public ApiGouvMandataireSocialData getData() {
        return data;
    }

    public void setData(ApiGouvMandataireSocialData data) {
        this.data = data;
    }
}
