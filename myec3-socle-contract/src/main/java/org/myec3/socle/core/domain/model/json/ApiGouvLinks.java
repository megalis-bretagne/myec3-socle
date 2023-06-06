package org.myec3.socle.core.domain.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class ApiGouvLinks {

        @JsonProperty("unite_legale")
        public String uniteLegale;

        public String getUniteLegale() {
                return uniteLegale;
        }

        public void setUniteLegale(String uniteLegale) {
                this.uniteLegale = uniteLegale;
        }

        @Override
        public String toString() {
                return "ApiGouvLinks{" +
                        "uniteLegale='" + uniteLegale + '\'' +
                        '}';
        }
}
