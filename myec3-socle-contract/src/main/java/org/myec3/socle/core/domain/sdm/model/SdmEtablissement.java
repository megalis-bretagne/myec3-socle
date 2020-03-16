package org.myec3.socle.core.domain.sdm.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class SdmEtablissement extends SdmResource  {

    @XmlElement(required = true)
    private long id;

    private String siege;

    @XmlElement(required = true)
    private String siret;
    private Date dateCreation;

    @XmlElement(required = false)
    private Date dateModification;

    @XmlElement(required = false)
    private SdmAdresse adresse;

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
        this.siege = siege;
    }

    public String getSiret() {
        return siret;
    }

    public void setSiret(String siret) {
        this.siret = siret;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Date getDateModification() {
        return dateModification;
    }

    public void setDateModification(Date dateModification) {
        this.dateModification = dateModification;
    }

    public SdmAdresse getAdresse() {
        return adresse;
    }

    public void setAdresse(SdmAdresse adresse) {
        this.adresse = adresse;
    }
}

