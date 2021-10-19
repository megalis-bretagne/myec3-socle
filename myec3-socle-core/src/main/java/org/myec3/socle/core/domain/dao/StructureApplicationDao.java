package org.myec3.socle.core.domain.dao;

import org.myec3.socle.core.domain.model.*;

import java.util.List;


public interface StructureApplicationDao extends NoResourceGenericDao<StructureApplication> {

	List<StructureApplication> findAllByApplication(Application application);

	List<StructureApplication> findAllByStructure(Structure structure);

	StructureApplication findByStructureAndApplication(Structure structure, Application application);

}
