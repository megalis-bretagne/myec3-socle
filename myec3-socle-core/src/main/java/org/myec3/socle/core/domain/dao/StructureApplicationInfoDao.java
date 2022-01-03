package org.myec3.socle.core.domain.dao;

import org.myec3.socle.core.domain.model.*;

import java.util.List;


public interface StructureApplicationInfoDao extends NoResourceGenericDao<StructureApplicationInfo> {

	List<StructureApplicationInfo> findAllByApplication(Application application);

	List<StructureApplicationInfo> findAllByStructure(Structure structure);

	StructureApplicationInfo findByStructureAndApplication(Structure structure, Application application);

}
