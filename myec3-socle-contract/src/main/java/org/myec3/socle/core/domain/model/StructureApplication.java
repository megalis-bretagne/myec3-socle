package org.myec3.socle.core.domain.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Structure_Application")
public class StructureApplication implements PE {

    @EmbeddedId
    private StructureApplicationId structureApplicationId = new StructureApplicationId();

    private Long nbMaxLicenses;

    public StructureApplication() {
    }

    public StructureApplication(StructureApplicationId structureApplicationId, Long nbMaxLicenses) {
        this.structureApplicationId = structureApplicationId;
        this.nbMaxLicenses = nbMaxLicenses;
    }

    public StructureApplication(Long structuresId, Long applicationsId, Long nbMaxLicenses) {
        this.structureApplicationId = new StructureApplicationId(structuresId, applicationsId);
        this.nbMaxLicenses = nbMaxLicenses;
    }

    public StructureApplicationId getStructureApplicationId() {
        return structureApplicationId;
    }

    public void setStructureApplicationId(StructureApplicationId structureApplicationId) {
        this.structureApplicationId = structureApplicationId;
    }

    public Long getNbMaxLicenses() {
        return nbMaxLicenses;
    }

    public void setNbMaxLicenses(Long nbMaxLicenses) {
        this.nbMaxLicenses = nbMaxLicenses;
    }
}
