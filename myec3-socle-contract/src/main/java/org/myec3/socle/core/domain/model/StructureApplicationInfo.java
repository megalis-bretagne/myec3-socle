package org.myec3.socle.core.domain.model;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;


/**
 * Information about the link between Structure & application
 */
@Entity
@Table(name = "Structure_Application_Info")
@Audited
public class StructureApplicationInfo implements PE, Serializable {

    private StructureApplicationInfoId structureApplicationInfoId;

    private Structure structure;

    private Application application;

    private Long nbMaxLicenses;

    public StructureApplicationInfo() {
    }

    public StructureApplicationInfo(Structure structure, Application application, Long nbMaxLicenses) {
        this.structureApplicationInfoId = new StructureApplicationInfoId(structure.getId(), application.getId());
        this.structure = structure;
        this.application = application;
        this.nbMaxLicenses = nbMaxLicenses;
    }

    @EmbeddedId
    public StructureApplicationInfoId getStructureApplicationInfoId() {
        return structureApplicationInfoId;
    }

    public void setStructureApplicationInfoId(StructureApplicationInfoId structureApplicationInfoId) {
        this.structureApplicationInfoId = structureApplicationInfoId;
    }

    /**
     * Nb max licence of the application for the structure
     * @return nbMaxLicenses
     */
    public Long getNbMaxLicenses() {
        return nbMaxLicenses;
    }

    public void setNbMaxLicenses(Long nbMaxLicenses) {
        this.nbMaxLicenses = nbMaxLicenses;
    }

    @ManyToOne
    @MapsId("structuresId")
    @JoinColumn(name = "structures_id")
    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    @ManyToOne
    @MapsId("applicationsId")
    @JoinColumn(name = "applications_id")
    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}
