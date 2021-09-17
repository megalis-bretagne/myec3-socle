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
