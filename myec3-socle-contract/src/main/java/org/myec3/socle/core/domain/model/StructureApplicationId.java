package org.myec3.socle.core.domain.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class StructureApplicationId implements Serializable {

    @Column(name="structures_id")
    private Long structuresId;

    @Column(name="applications_id")
    private Long applicationsId;

    public Long getStructuresId() {
        return structuresId;
    }

    public void setStructuresId(Long structuresId) {
        this.structuresId = structuresId;
    }

    public Long getApplicationsId() {
        return applicationsId;
    }

    public void setApplicationsId(Long applicationsId) {
        this.applicationsId = applicationsId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StructureApplicationId)) {
            return false;
        }
        StructureApplicationId that = (StructureApplicationId) o;
        return structuresId.equals(that.structuresId) && applicationsId.equals(that.applicationsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(structuresId, applicationsId);
    }
}
