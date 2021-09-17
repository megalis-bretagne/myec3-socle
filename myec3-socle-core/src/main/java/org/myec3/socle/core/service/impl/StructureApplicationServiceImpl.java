package org.myec3.socle.core.service.impl;

import org.myec3.socle.core.domain.dao.StructureApplicationDao;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.StructureApplication;
import org.myec3.socle.core.service.StructureApplicationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("structureApplicationService")
public class StructureApplicationServiceImpl extends AbstractGenericServiceImpl<StructureApplication, StructureApplicationDao> implements StructureApplicationService {
    @Override
    public List<StructureApplication> findAllByApplication(Application application) {
        return this.dao.findAllByApplication(application);
    }

    @Override
    public List<StructureApplication> findAllByStructure(Structure structure) {
        return this.dao.findAllByStructure(structure);
    }

    @Override
    public StructureApplication findByStructureAndApplication(Structure structure, Application application) {
        return this.dao.findByStructureAndApplication(structure,application);
    }
}
