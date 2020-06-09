package org.myec3.socle.core.domain.sdm.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@XmlRootElement
public class SdmEntreprise extends SdmResource  {

    @XmlElement(required = true)
    private long id;

    @XmlElement(required = false)
    private String siren;

    @XmlElement(required = false)
    private String sirenEtranger;

    @XmlElement(required = false)
    private String paysenregistrement;

    @XmlElement(required = false)
    private String effectif;

    @XmlElement(required = false)
    private SdmAdresse adresse;

    @XmlElement(required = false)
    private String formeJuridique;

    @XmlElement(required = false)
    private String codeAPE;

    @XmlElement(required = false)
    private String capitalSocial;

    @XmlElement(required = false)
    private String raisonSociale;

    @XmlElement(required = true)
    private String email;

    @XmlElement(required = false)
    private List<SdmEtablissement> etablisssements;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren =  Objects.toString(siren,"");
    }

    public String getSirenEtranger() {
        return sirenEtranger;
    }

    public void setSirenEtranger(String sirenEtranger) {
        this.sirenEtranger =  Objects.toString(sirenEtranger,"");
    }

    public String getPaysenregistrement() {
        return paysenregistrement;
    }
    public void setPaysenregistrement(String paysenregistrement) {
        this.paysenregistrement =  Objects.toString(paysenregistrement,"");
    }

    public String getEffectif() {
        return effectif;
    }

    public void setEffectif(String effectif) {
        this.effectif =  Objects.toString(effectif,"");;
    }

    public SdmAdresse getAdresse() {
        return adresse;
    }

    public void setAdresse(SdmAdresse adresse) {
        this.adresse = adresse;
    }

    public String getFormeJuridique() {
        return formeJuridique;
    }

    public void setFormeJuridique(String formeJuridique) {
        this.formeJuridique = Objects.toString(formeJuridique,"");
    }

    public String getCodeAPE() {
        return codeAPE;
    }

    public void setCodeAPE(String codeAPE) {
        this.codeAPE = Objects.toString(codeAPE,"");
    }

    public String getCapitalSocial() {
        return capitalSocial;
    }

    public void setCapitalSocial(String capitalSocial) {
        this.capitalSocial = Objects.toString(capitalSocial,"");
    }

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = Objects.toString(raisonSociale,"");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Objects.toString(email,"");
    }

    public List<SdmEtablissement> getEtablisssements() {
        return etablisssements;
    }

    public void setEtablisssements(List<SdmEtablissement> etablisssements) {
        this.etablisssements = etablisssements;
    }
}
