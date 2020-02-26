package org.myec3.socle.core.domain.sdm.model;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SdmService extends SdmResource  {

    private long id;
    private long idParent;
    private long idExterne;
    private long idExterneParent;
    private String libelle;
    private String acronymeOrganisme;
    private String sigle;
    private String siren;
    private String complement;
    private String email;
    private String formeJuridique;
    private String formeJuridiqueCode;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdParent() {
        return idParent;
    }

    public void setIdParent(long idParent) {
        this.idParent = idParent;
    }

    public long getIdExterne() {
        return idExterne;
    }

    public void setIdExterne(long idExterne) {
        this.idExterne = idExterne;
    }

    public long getIdExterneParent() {
        return idExterneParent;
    }

    public void setIdExterneParent(long idExterneParent) {
        this.idExterneParent = idExterneParent;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getAcronymeOrganisme() {
        return acronymeOrganisme;
    }

    public void setAcronymeOrganisme(String acronymeOrganisme) {
        this.acronymeOrganisme = acronymeOrganisme;
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

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFormeJuridique() {
        return formeJuridique;
    }

    public void setFormeJuridique(String formeJuridique) {
        this.formeJuridique = formeJuridique;
    }

    public String getFormeJuridiqueCode() {
        return formeJuridiqueCode;
    }

    public void setFormeJuridiqueCode(String formeJuridiqueCode) {
        this.formeJuridiqueCode = formeJuridiqueCode;
    }
}
