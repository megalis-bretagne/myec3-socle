package org.myec3.socle.core.domain.sdm.model;

import java.util.Date;
import java.util.List;

public class SdmOrganisme extends SdmResource  {

    private long id;
    private String acronyme;
    private String categorieInsee;
    private String sigle;
    private String siren;
    private String denomination;
    private String nic;
    private SdmAdresse adresse;
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAcronyme() {
        return acronyme;
    }

    public void setAcronyme(String acronyme) {
        this.acronyme = acronyme;
    }

    public String getCategorieInsee() {
        return categorieInsee;
    }

    public void setCategorieInsee(String categorieInsee) {
        this.categorieInsee = categorieInsee;
    }

    public String getSigle() {
        return sigle;
    }

    public void setSigle(String sigle) {
        this.sigle = sigle;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = denomination;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public SdmAdresse getAdresse() {
        return adresse;
    }

    public void setAdresse(SdmAdresse adresse) {
        this.adresse = adresse;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
