package org.myec3.socle.core.domain.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class ApiGouvMeta {

        @JsonProperty("date_derniere_mise_a_jour")
        public Long dateDerniereMiseAjour;

        @JsonProperty("redirect_from_siret")
        public String redirectFromSiret;

        public Date getDateDerniereMiseAjourAsDate(){
            return new Date(dateDerniereMiseAjour);
        }

        public String getRedirectFromSiret() {
                return redirectFromSiret;
        }

        public void setRedirectFromSiret(String redirectFromSiret) {
                this.redirectFromSiret = redirectFromSiret;
        }

        public Long getDateDerniereMiseAjour() {
                return dateDerniereMiseAjour;
        }

        public void setDateDerniereMiseAjour(Long dateDerniereMiseAjour) {
                this.dateDerniereMiseAjour = dateDerniereMiseAjour;
        }

        @Override
        public String toString() {
                return "ApiGouvMeta{" +
                        "dateDerniereMiseAjour=" + dateDerniereMiseAjour +
                        ", redirectFromSiret='" + redirectFromSiret + '\'' +
                        '}';
        }
}
