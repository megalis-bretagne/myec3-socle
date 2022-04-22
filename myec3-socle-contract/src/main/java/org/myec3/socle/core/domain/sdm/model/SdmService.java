package org.myec3.socle.core.domain.sdm.model;


import com.fasterxml.jackson.annotation.JsonRootName;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement
public class SdmService extends SdmResource  {

    @XmlElement(required = true)
    private Long id;
    private Long idParent;
    private Long idExterneParent;
    private String libelle;
    private String acronymeOrganisme;
    private String sigle;
    private String siren;
    private String complement;
    private String email;
    private String formeJuridique;
    private String formeJuridiqueCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdParent() {
        return idParent;
    }

    public void setIdParent(Long idParent) {
        this.idParent = idParent;
    }

    public Long getIdExterneParent() {
        return idExterneParent;
    }

    public void setIdExterneParent(Long idExterneParent) {
        this.idExterneParent = idExterneParent;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle =  Objects.toString(libelle,"");
    }

    public String getAcronymeOrganisme() {
        return acronymeOrganisme;
    }

    public void setAcronymeOrganisme(String acronymeOrganisme) {
        this.acronymeOrganisme =  Objects.toString(acronymeOrganisme,"");
    }

    public String getSigle() {
        return sigle;
    }

    public void setSigle(String sigle) {
        this.sigle =  Objects.toString(sigle,"");
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren =  Objects.toString(siren,"");
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement =  Objects.toString(complement,"");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email =  Objects.toString(email,"");
    }

    public String getFormeJuridique() {
        return formeJuridique;
    }

    public void setFormeJuridique(String formeJuridique) {
        this.formeJuridique =  Objects.toString(formeJuridique,"");
    }

    public String getFormeJuridiqueCode() {
        return formeJuridiqueCode;
    }

    public void setFormeJuridiqueCode(String formeJuridiqueCode) {
        this.formeJuridiqueCode =  Objects.toString(formeJuridiqueCode,"");
    }
}
