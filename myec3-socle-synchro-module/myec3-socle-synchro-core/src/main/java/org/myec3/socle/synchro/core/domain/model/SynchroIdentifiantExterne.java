package org.myec3.socle.synchro.core.domain.model;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.PE;
import org.myec3.socle.core.domain.model.enums.ResourceType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class SynchroIdentifiantExterne implements Serializable, PE {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Application application;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType typeRessource;

    @Column(nullable = true)
    private Long idSocle;

    @Column(nullable = true)
    private Long idAppliExterne;

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public ResourceType getTypeRessource() {
        return typeRessource;
    }

    public void setTypeRessource(ResourceType typeRessource) {
        this.typeRessource = typeRessource;
    }

    public Long getIdSocle() {
        return idSocle;
    }

    public void setIdSocle(Long idSocle) {
        this.idSocle = idSocle;
    }

    public Long getIdAppliExterne() {
        return idAppliExterne;
    }

    public void setIdAppliExterne(Long idAppliExterne) {
        this.idAppliExterne = idAppliExterne;
    }
}
