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
package org.myec3.socle.webapp.components;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.internal.services.ComponentResultProcessorWrapper;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ComponentEventResultProcessor;
import org.apache.tapestry5.services.SelectModelFactory;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.view.OrganismDepartmentView;

/**
 * Component class used to manage organism departments (add or modify)
 * {@link OrganismDepartment}<br>
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp
 * /components/OrganismDepartmentForm.tml
 * 
 * @author Anthony Colas <anthony.colas@atos.net>
 * 
 */
public class OrganismDepartmentForm extends AbstractPage {

	private static Log logger = LogFactory.getLog(OrganismDepartmentForm.class);

	@SuppressWarnings("rawtypes")
	@Environmental
	private ComponentEventResultProcessor componentEventResultProcessor;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link OrganismDepartment} objects
	 */
	@Inject
	@Service("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	@Inject
	private ComponentResources componentResources;

	@Parameter(required = true)
	@Property
	private OrganismDepartment organismDepartment;

	@Property
	private OrganismDepartmentView selectedRootDepartment;

	@Property
	private OrganismDepartment rootDepartment;

	private int deeperLevel;

	@Parameter(required = true, defaultPrefix = "prop")
	@Property
	private Object cancelRedirect;

	@Property
	private SelectModel departments;

	@Inject
	private SelectModelFactory selectModelFactory;

	@Component(id = "modification_form")
	private Form form;

	// Form events
	@OnEvent(value = EventConstants.VALIDATE, component = "modification_form")
	public void onValidate() {
		try {
			// Validate during modification of an organismDepartment not during
			// creation
			if (!this.getNewOrganismDeparment()) {
				// Get level of current department
				int currentDepartmentLevel = this.organismDepartmentService
						.findCurrentLevelOfDepartment(this.organismDepartment);

				this.deeperLevel = currentDepartmentLevel;

				// Get number of level of department's subdepartments
				// (initialize deeperLevel value)
				this.countDeeperLevelOfDepartment(this.organismDepartment,
						currentDepartmentLevel);
				logger.debug("Depper level value = " + this.deeperLevel);

				// Check if department level + subdepartments not > at maximum
				// authorized number of level
				if (this.deeperLevel > GuWebAppConstants
						.getSocleGuMaxSubDepartments()) {
					this.form.recordError(this.getMessages().get(
							"max-subdepartment-error-message"));
				}
			}
		} catch (Exception e) {
			this.form.recordError(this.getMessages().get(
					"validation-error-message"));
		}
	}

	@OnEvent(EventConstants.PREPARE)
	void onPrepare() {
		this.rootDepartment = this.organismDepartmentService
				.findRootOrganismDepartment(this.organismDepartment.getOrganism());

		List<OrganismDepartmentView> departmentsList = getDepartementList();

		departments = selectModelFactory.create(departmentsList, "nameDisplay");
	}

	@OnEvent(EventConstants.SUCCESS)
	public void onSuccess() {
		this.organismDepartment.setParentDepartment(selectedRootDepartment.getOrganismDepartment());
		final ComponentResultProcessorWrapper callback = new ComponentResultProcessorWrapper(
				componentEventResultProcessor);
		this.componentResources.triggerEvent("participativeprocessformok",
				null, callback);
	}

	@OnEvent(EventConstants.CANCELED)
	public Object cancelRedirect() {
		return this.cancelRedirect;
	}

	// Getters
	public ValueEncoder<OrganismDepartmentView> getDepartmentEncoder() {
		return new GenericListEncoder(getDepartementList());
	}

	private Map<OrganismDepartment, String> constructDepartementMap(
			Map<OrganismDepartment, String> map, OrganismDepartment parent,
			int depth) {
		if (this.rootDepartment.equals(parent)
				|| (parent.getId() != this.organismDepartment.getId())) {
			StringBuffer indent = new StringBuffer("");
			if (depth < GuWebAppConstants.getSocleGuMaxSubDepartments()) {
				for (int i = 0; i < depth; i++) {
					indent.append("-- \\ ");
				}

				map.put(parent, indent.toString() + parent.getLabel());

				List<OrganismDepartment> departmentsList = this.organismDepartmentService
						.findAllChildrenDepartment(parent);
				depth++;
				for (OrganismDepartment organismDepartment : departmentsList) {
					map = constructDepartementMap(map, organismDepartment,
							depth);
				}
			}
		}
		return map;
	}

	/**
	 * This method allows to set deeperLevel value in order to know the deeper level
	 * of the current organismDepartment
	 */
	public void countDeeperLevelOfDepartment(
			OrganismDepartment parentDepartment, int level) {
		logger.debug("Enterring into countDeeperLevelOfDepartment");

		List<OrganismDepartment> childrenList = this.organismDepartmentService
				.findAllChildrenDepartment(parentDepartment);
		if ((childrenList != null) && (!childrenList.isEmpty())) {
			level++;
			for (OrganismDepartment child : childrenList) {
				this.countDeeperLevelOfDepartment(child, level);
				if (this.deeperLevel < level) {
					this.deeperLevel = level;
				}
			}
		}

		logger.debug("level = " + level);
	}

	/**
	 * Check if current department has sub departments
	 */
	public Boolean getHasSubDepartments() {
		return this.organismDepartmentService
				.hasSubDepartments(this.organismDepartment);
	}

	/**
	 * Check if it's a creation or a modification of an department
	 * 
	 * @return true if it's a creation of new organism department
	 */
	public Boolean getNewOrganismDeparment() {
		if (null == this.organismDepartment.getId()) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	private List<OrganismDepartmentView> getDepartementList() {
		List<OrganismDepartmentView> departmentsList = new ArrayList<>();

		OrganismDepartment rootDepartments = this.organismDepartmentService
				.findRootOrganismDepartment(this.organismDepartment.getOrganism());

		Map<OrganismDepartment, String> departmentsMap = new LinkedHashMap<OrganismDepartment, String>();
		departmentsMap = constructDepartementMap(departmentsMap, rootDepartments, 0);

		for (Entry<OrganismDepartment, String> entry : departmentsMap.entrySet()) {
			departmentsList.add(new OrganismDepartmentView(entry.getKey(), entry.getValue()));
		}
		return departmentsList;
	}
}
