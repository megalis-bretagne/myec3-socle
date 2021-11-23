package org.myec3.socle.core.service;

import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.StructureApplicationInfo;

import java.util.List;

public interface StructureApplicationInfoService extends IGenericService<StructureApplicationInfo> {

    List<StructureApplicationInfo> findAllByApplication(Application application);

    List<StructureApplicationInfo> findAllByStructure(Structure structure);

    StructureApplicationInfo findByStructureAndApplication(Structure structure, Application application);
}
