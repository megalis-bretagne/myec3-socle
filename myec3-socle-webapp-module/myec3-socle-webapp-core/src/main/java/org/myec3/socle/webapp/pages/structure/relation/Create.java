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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
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

/**
 * Page used to create a relation between two structures{@link Structure}
 * :<br />
 * <ul>
 * <li>Appartient à</li>
 * <li>Regroupe</li>
 * </ul>
 * 
 * In this step your must choose a relation type and fill a structure name or
 * siren.<br />
 * The structure name field use autocompletion.<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/structure/relation/Create.tml
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class Create extends AbstractPage {

	// Constants used in this page
	private static final String CHILD_RELATION_TYPE = "Regroupe";
	private static final String COMPANY_STRUCTURE_TYPE = "Entreprise";
	private static final String ORGANISM_STRUCTURE_TYPE = "Organisme";

	// Used services
	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Structure} objects
	 */
	@Inject
	@Named("structureService")
	private StructureService structureService;

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

	// Other page
	@InjectPage
	private ListRelations listRelationsPage;

	// Template attributes
	@Property
	private Structure structure;

	@Property
	private Structure searchStructure;

	@Persist(PersistenceConstants.FLASH)
	@Property
	private Map<String, Structure> mapStructures;

	// Don't remove this unused field
	@SuppressWarnings("unused")
	@Property
	private String labelOrSiren;

	@Property
	private String selectedStructure;

	@Property
	private String selectedRelationType;

	@Component(id = "relation_form")
	private Form form;

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return Index.class;
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.structure != null) ? this.structure.getId() : null;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		// Retrieve the structure with its id
		this.structure = this.structureService.findOne(id);

		// If the structure is null we redirect the user to the index
		if (null == this.structure) {
			return Index.class;
		}

		// Check if loggedUser can access to this page
		return this.hasRights(this.structure);
	}

	// Template properties
	@OnEvent(value = EventConstants.VALIDATE, component = "relation_form")
	public Object onValidate() {
		try {
			// We check if the selected structure exists into the list suggested
			if (!this.mapStructures.containsKey(selectedStructure)) {
				form.recordError(this.getMessages().get("selected-error-message"));
			}

			// We get the structure selected
			this.searchStructure = mapStructures.get(selectedStructure);

			// We check id the relation doesn't already exists
			if (this.structureService.relationAlreadyExists(this.structure, this.searchStructure)) {
				form.recordError(this.getMessages().get("relation-already-exists-error-message"));
			}

			return Boolean.TRUE;

		} catch (Exception e) {
			form.recordError(this.getMessages().get("validation-error-message"));
			return null;
		}
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		try {
			List<Resource> addedStructure = new ArrayList<Resource>();
			Structure structureToSynchronize = null;

			// In case of we add a child structure
			if (selectedRelationType.equals(CHILD_RELATION_TYPE)) {
				this.structure.addChildStructure(this.searchStructure);

				structureToSynchronize = this.structure;
				addedStructure.add(this.searchStructure);

				if (this.structure instanceof Organism) {
					organismService.update((Organism) this.structure);
				} else {
					companyService.update((Company) this.structure);
				}
			}
			// In case of we add a parent structure
			else {
				this.searchStructure.setChildStructures(
						this.structureService.findAllChildStructuresByStructure(this.searchStructure));
				this.searchStructure.addChildStructure(this.structure);

				structureToSynchronize = this.searchStructure;
				addedStructure.add(this.structure);

				if (this.searchStructure instanceof Organism) {
					organismService.update((Organism) this.searchStructure);
				} else {
					companyService.update((Company) this.searchStructure);
				}
			}

			// Notify external applications
			this.synchronizationService.notifyCollectionUpdate(structureToSynchronize,
					SynchronizationRelationsName.CHILD_STRUCTURES, null, addedStructure, null);

		} catch (Exception e) {
			form.recordError(this.getMessages().get("recording-error-message"));
			return null;
		}

		// Return to the list of relations of the structure
		this.listRelationsPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
		this.listRelationsPage.setStructure(this.structure);
		return this.listRelationsPage;
	}

	/**
	 * This method allows to fill the autocomplete field
	 * 
	 * @param searchValue : the string to search
	 * @return a string list of structures which corresponding at the search
	 */
	public List<String> onProvideCompletionsFromLabelOrSiren(String searchValue) {
		List<Structure> foundListStructures = new ArrayList<Structure>();
		List<String> foundListStructuresToString = new ArrayList<String>();
		// We get the list of stuctures corresponding at the field labelOrSiren
		foundListStructures = this.structureService.findAllByLabelOrSiren(searchValue);

		// Create a map in order to get the structure depending on its
		// value
		mapStructures = new HashMap<String, Structure>();

		for (Structure structure : foundListStructures) {
			String structureType = null;
			String labelToDisplay = null;

			if (structure instanceof Company) {
				structureType = COMPANY_STRUCTURE_TYPE;
			} else {
				structureType = ORGANISM_STRUCTURE_TYPE;
			}
			labelToDisplay = structure.getLabel().toUpperCase() + " - " + structureType + " - " + structure.getSiren();

			foundListStructuresToString.add(labelToDisplay);
			mapStructures.put(labelToDisplay, structure);
		}

		// Sort the list before return the result
		Collections.sort(foundListStructuresToString);

		// Return the list of structures found converted in string
		return foundListStructuresToString;
	}
}