/**
 * Copyright (c) 2011 Atos Bourgogne
 * 
 * This file is part of MyEc3.
 * 
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * 
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.webapp.pages.structure.relation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.Structure;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.StructureService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.synchro.api.constants.SynchronizationRelationsName;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;
import org.myec3.socle.webapp.propertyConduit.RelationPropertyConduit;

/**
 * Page used to create display all relations of a given structure
 * {@link Structure} :<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp
 * /pages/structure/relation/ListRelations.tml
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class ListRelations extends AbstractPage {

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Inject
	@Named("organismService")
	private OrganismService organismService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Company} objects
	 */
	@Inject
	@Named("companyService")
	private CompanyService companyService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Structure} objects
	 */
	@Inject
	@Named("structureService")
	private StructureService structureService;

	@Inject
	private BeanModelSource beanModelSource;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	private RelationPropertyConduit relationPropertyConduit;

	private Structure structure;

	// TABLE
	@SuppressWarnings("unused")
	@Property
	private Structure structureRelationRow;

	@Component
	private Grid structureRelationsGrid;

	@Property
	private List<Structure> listOfParentStructures;

	@Property
	private List<Structure> listOfChildStructures;

	@SuppressWarnings("unused")
	@Property
	private Integer rowIndex;

	@SetupRender
	public void setupGrid() {
		structureRelationsGrid.getSortModel().clear();
		structureRelationsGrid.getSortModel().updateSort("label");
	}

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return Index.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		this.structure = this.structureService.findOne(id);

		if (null == this.structure) {
			return Index.class;
		}

		// Check if loggedUser can access at this page
		return this.hasRights(this.structure);
	}

	public BeanModel<Structure> getGridModel() {
		BeanModel<Structure> model = this.beanModelSource.createDisplayModel(Structure.class, this.getMessages());

		// Use custom property consuit in order to can sort "relation" column
		relationPropertyConduit = new RelationPropertyConduit();
		relationPropertyConduit.setListOfParentStructures(this.listOfParentStructures);

		model.add("relation", this.relationPropertyConduit).sortable(true);
		model.add("actions", null);
		model.include("relation", "label", "actions");

		return model;
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.structure != null) ? this.structure.getId() : null;
	}

	// IN CASE OF DELETION OF THE RELATION
	@OnEvent(value = "action", component = "delete")
	public Object removeRelation(Long id) {
		// We retrieve the structure to delete
		Structure structureToDelete = structureService.findOne(id);

		if (structureToDelete != null) {
			List<Resource> removedStructure = new ArrayList<Resource>();
			Structure structureToSynchronize = null;

			// If its a parent relation
			if (isParentRelation(structureToDelete)) {
				// We remove the parent structure from the list of the current
				// structure
				structureToDelete.removeChildStructure(this.structure);

				structureToSynchronize = structureToDelete;
				removedStructure.add(this.structure);

				// We update the old parent structure
				if (structureToDelete instanceof Organism) {
					organismService.update((Organism) structureToDelete);
				} else {
					companyService.update((Company) structureToDelete);
				}
			} else {
				// It's a child relation
				this.structure.removeChildStructure(structureToDelete);

				structureToSynchronize = this.structure;
				removedStructure.add(structureToDelete);

				if (this.structure instanceof Organism) {
					// We update the current structure
					this.organismService.update((Organism) this.structure);
				} else {
					this.companyService.update((Company) this.structure);
				}
			}

			// Notify external applications
			this.synchronizationService.notifyCollectionUpdate(structureToSynchronize,
					SynchronizationRelationsName.CHILD_STRUCTURES, null, null, removedStructure);
		}

		this.successMessage = this.getMessages().get("delete-success");
		return this;
	}

	// USED TO FILL THE GRID OF RELATIONS
	public List<Structure> getStructureRelationsResult() {
		if (this.structure != null) {
			List<Structure> listOfAllRelationsOfOrganism = new ArrayList<Structure>();

			this.listOfParentStructures = this.structureService.findAllParentStructuresByStructure(this.structure);
			this.listOfChildStructures = this.structureService.findAllChildStructuresByStructure(this.structure);

			listOfAllRelationsOfOrganism.addAll(listOfParentStructures);
			listOfAllRelationsOfOrganism.addAll(listOfChildStructures);

			return listOfAllRelationsOfOrganism;
		}
		return new ArrayList<Structure>();
	}

	public Boolean isParentRelation(Structure structure) {
		if (this.structure.getParentStructures().contains(structure)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	// Getters n Setters
	public String getSuccessMessage() {
		return this.successMessage;
	}

	public void setSuccessMessage(String message) {
		this.successMessage = message;
	}

	public Structure getStructure() {
		return structure;
	}

	public void setStructure(Structure structure) {
		this.structure = structure;
	}

	public Boolean getIsOrganism() {
		return this.structure.isOrganism();
	}

	public Boolean getIsCompany() {
		return this.structure.isCompany();
	}

	public Integer getResultsNumber() {
		if (null == this.getStructureRelationsResult())
			return 0;
		return this.getStructureRelationsResult().size();
	}
}
