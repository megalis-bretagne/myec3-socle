package org.myec3.socle.core.service;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.StructureApplication;

import java.util.List;

public interface StructureApplicationService extends IGenericService<StructureApplication> {

    List<StructureApplication> findAllByApplication(Application application);

    List<StructureApplication> findAllByStructure(Structure structure);

    StructureApplication findByStructureAndApplication(Structure structure, Application application);
}
