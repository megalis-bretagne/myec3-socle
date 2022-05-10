package org.myec3.socle.core.domain.sdm.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SdmOrganisme extends SdmResource  {

    private Long id;
    private String acronyme;
    private String categorieInsee;
    private String sigle;
    private String siren;
    private String denomination;
    private String nic;
    private SdmAdresse adresse;
    private String description;
    private String url;
    private String email;
    private String tel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAcronyme() {
        return acronyme;
    }

    public void setAcronyme(String acronyme) {
        this.acronyme = Objects.toString(acronyme,"");
    }

    public String getCategorieInsee() {
        return categorieInsee;
    }

    public void setCategorieInsee(String categorieInsee) {
        this.categorieInsee = Objects.toString(categorieInsee,"");
    }

    public String getSigle() {
        return sigle;
    }

    public void setSigle(String sigle) {
        this.sigle = Objects.toString(sigle,"");
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = Objects.toString(siren,"");
    }

    public String getDenomination() {
        return denomination;
    }

    public void setDenomination(String denomination) {
        this.denomination = Objects.toString(denomination,"");
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = Objects.toString(nic,"");
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
        this.description = Objects.toString(description,"");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = Objects.toString(url,"");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Objects.toString(email,"");
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = Objects.toString(tel,"");
    }
}
