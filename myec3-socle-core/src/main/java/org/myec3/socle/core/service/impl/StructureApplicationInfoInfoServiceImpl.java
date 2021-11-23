package org.myec3.socle.core.service.impl;

import org.myec3.socle.core.domain.dao.StructureApplicationInfoDao;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.domain.model.StructureApplicationInfo;
import org.myec3.socle.core.service.StructureApplicationInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("structureApplicationInfoService")
public class StructureApplicationInfoInfoServiceImpl extends AbstractGenericServiceImpl<StructureApplicationInfo, StructureApplicationInfoDao> implements StructureApplicationInfoService {
    @Override
    public List<StructureApplicationInfo> findAllByApplication(Application application) {
        return this.dao.findAllByApplication(application);
    }

    @Override
    public List<StructureApplicationInfo> findAllByStructure(Structure structure) {
        return this.dao.findAllByStructure(structure);
    }

    @Override
    public StructureApplicationInfo findByStructureAndApplication(Structure structure, Application application) {
        return this.dao.findByStructureAndApplication(structure,application);
    }
}
