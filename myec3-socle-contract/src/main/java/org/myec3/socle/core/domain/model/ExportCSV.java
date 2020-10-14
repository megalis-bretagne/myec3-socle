package org.myec3.socle.core.domain.model;


import org.myec3.socle.core.domain.model.enums.EtatExport;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Synchronized
public class ExportCSV  implements Serializable, Cloneable, PE{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDemande;

    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateExport;

    @Column(nullable = true,columnDefinition = "LONGBLOB")
    @Lob
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EtatExport etat;

    public ExportCSV() {
        super();
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Technical id of the resource
     *
     * @return the id for this resource
     */
    public Long getId() {
        return id;
    }

    public Date getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(Date dateDemande) {
        this.dateDemande = dateDemande;
    }

    public Date getDateExport() {
        return dateExport;
    }

    public void setDateExport(Date dateExport) {
        this.dateExport = dateExport;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public EtatExport getEtat() {
        return etat;
    }

    public void setEtat(EtatExport etat) {
        this.etat = etat;
    }
}
