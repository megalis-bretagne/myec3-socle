package org.myec3.socle.core.domain.sdm.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Objects;

@XmlRootElement
public class SdmEtablissement extends SdmResource  {

    @XmlElement(required = true)
    private long id;

    private String siege;

    @XmlElement(required = true)
    private String siret;

    @XmlElement(required = false)
    private SdmAdresse adresse;

    @XmlElement(required = true)
    private String idEntreprise;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String codeEtablissement;

    public String getIdEntreprise() {
        return idEntreprise;
    }

    public void setIdEntreprise(String idEntreprise) {
        this.idEntreprise = idEntreprise;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSiege() {
        return siege;
    }

    public void setSiege(String siege) {
        this.siege = Objects.toString(siege,"");
    }

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = Objects.toString(siret,"");
    }

    public String getCodeEtablissement() {
        return codeEtablissement;
    }

    public void setCodeEtablissement(String codeEtablissement) {
        this.codeEtablissement = Objects.toString(codeEtablissement,"");
    }

    public SdmAdresse getAdresse() {
        return adresse;
    }

    public void setAdresse(SdmAdresse adresse) {
        this.adresse = adresse;
    }
}

