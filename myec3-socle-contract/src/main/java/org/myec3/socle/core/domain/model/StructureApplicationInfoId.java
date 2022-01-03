package org.myec3.socle.core.domain.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite Key of {@link StructureApplicationInfo}
 */
@Embeddable
public class StructureApplicationInfoId implements Serializable {


    private Long structuresId;


    private Long applicationsId;

    public StructureApplicationInfoId() {
    }

    public StructureApplicationInfoId(Long structuresId, Long applicationsId) {
        this.structuresId = structuresId;
        this.applicationsId = applicationsId;
    }

    @Column(name = "structures_id", insertable = false, updatable = false)
    public Long getStructuresId() {
        return structuresId;
    }

    public void setStructuresId(Long structuresId) {
        this.structuresId = structuresId;
    }

    @Column(name = "applications_id", insertable = false, updatable = false)
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
        if (!(o instanceof StructureApplicationInfoId)) {
            return false;
        }
        StructureApplicationInfoId that = (StructureApplicationInfoId) o;
        return structuresId.equals(that.structuresId) && applicationsId.equals(that.applicationsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(structuresId, applicationsId);
    }
}
