package org.myec3.socle.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganismLightDTO {

    private Long id;
    private String label;
    private String siren;
}
