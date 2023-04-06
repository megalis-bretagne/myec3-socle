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
import java.util.List;

import javax.inject.Named;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismDepartment;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Component class used to display a tree with all levels of organism
 * departments {@link OrganismDepartment}<br>
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/components/Tree.tml
 * 
 * @author Denis Cucchietti <denis.cucchietti@atos.net>
 * 
 */
public class Tree extends AbstractPage {

	@Inject
	private ComponentResources componentResources;

	@Inject
	private Messages messages;

	/**
	 * Business Service providing methods and specifics operations to synchronize
	 * {@link Resource} objects to external applications
	 */
	@Inject
	@Named("synchronizationNotificationService")
	private SynchronizationNotificationService synchronizationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link OrganismDepartment} objects
	 */
	@Inject
	@Named("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	@Parameter(required = true)
	@Property
	private Organism currentOrganism;

	@Property
	private OrganismDepartment rootDepartment;

	@Inject
	@Property
	@Path("context:static/images/departement_ajouter.png")
	private Asset create_department;

	@Inject
	@Property
	@Path("context:static/images/departement_ajouter_grise.png")
	private Asset not_create_department;

	@Inject
	@Property
	@Path("context:static/images/departement_modifier.png")
	private Asset modify_department;

	@Inject
	@Property
	@Path("context:static/images/departement_modifier_grise.png")
	private Asset not_modify_department;

	@Inject
	@Property
	@Path("context:static/images/departement_supprimer.png")
	private Asset delete_department;

	@Inject
	@Property
	@Path("context:static/images/departement_supprimer_grise.png")
	private Asset not_delete_department;

	@Inject
	@Property
	@Path("context:static/images/agent_ajouter.png")
	private Asset add_agent;

	@Inject
	@Property
	@Path("context:static/images/agent_voir.png")
	private Asset view_agents_level;

	@SetupRender
	boolean setupRender(MarkupWriter writer) {
		if (currentOrganism == null)
			return Boolean.FALSE;

		writer.element("div", "class", "almostCenter");
		this.initializeTree(currentOrganism, writer);
		writer.end();

		return Boolean.FALSE;
	}

	/**
	 * Initialize the tree
	 * 
	 * @param organismTree
	 * @param writer
	 */
	public void initializeTree(Organism organismTree, MarkupWriter writer) {
		rootDepartment = this.organismDepartmentService.findRootOrganismDepartment(this.currentOrganism);
		List<OrganismDepartment> allDepartmentsList = this.currentOrganism.getDepartments();
		allDepartmentsList.remove(rootDepartment);
		List<OrganismDepartment> subDepartments = new ArrayList<OrganismDepartment>();

		writer.element("ul", "class", "note_children");

		// Write RootDepartment
		this.writeRootLevel(rootDepartment, writer);

		// FIRST LEVEL
		for (OrganismDepartment department : allDepartmentsList) {
			if ((null != department.getParentDepartment())
					&& (department.getParentDepartment().getId() == rootDepartment.getId())) {
				subDepartments.add(department);
			}
		}

		writer.element("li", "style", "list-style:none outside none;");
		writer.element("ul", "class", "notes");

		for (OrganismDepartment parent : subDepartments) {
			this.writeChildLevel(parent, writer, 0);
			if (this.organismDepartmentService.hasSubDepartments(parent)) {
				this.fillChildrenOfDepartment(parent, writer, 0);
			}
		}

		writer.end();
		writer.end();
		writer.end();
	}

	/**
	 * Write all children of an department
	 * 
	 * @param department
	 * @param writer
	 * @param currentLevel
	 */
	public void fillChildrenOfDepartment(OrganismDepartment department, MarkupWriter writer, int currentLevel) {
		currentLevel++;
		List<OrganismDepartment> childrenList = this.organismDepartmentService.findAllChildrenDepartment(department);
		if ((childrenList != null) && (!childrenList.isEmpty())
				&& (currentLevel < GuWebAppConstants.getSocleGuMaxSubDepartments())) {
			writer.element("li", "class", "note_children");
			writer.element("ul", "class", "notes");
			for (OrganismDepartment child : childrenList) {
				if (child.getParentDepartment() == department) {
					this.writeChildLevel(child, writer, currentLevel);
					this.fillChildrenOfDepartment(child, writer, currentLevel);
				}
			}
			writer.end();
			writer.end();
		}
	}

	/**
	 * Write the root level of tree
	 * 
	 * @param rootLevel
	 * @param writer
	 */
	public void writeRootLevel(OrganismDepartment rootLevel, MarkupWriter writer) {
		writer.element("li", "class", "rootNoPoint");

		writer.element("p");
		writer.element("strong");
		writer.write(rootLevel.getLabel());
		writer.end();

		this.writeRootMenu(rootLevel, writer);

		writer.end();
		writer.end();
	}

	/**
	 * Write children of tree
	 * 
	 * @param childLevel
	 * @param writer
	 * @param currentLevel
	 */
	public void writeChildLevel(OrganismDepartment childLevel, MarkupWriter writer, int currentLevel) {
		writer.element("li", "class", "note");

		writer.element("a", "href", "../detail/" + childLevel.getId());
		writer.write(childLevel.getLabel());
		writer.end();

		// CHILD MENU
		this.writeChildMenu(childLevel, writer, currentLevel);
		writer.end();
	}

	/**
	 * Write menu of root level
	 * 
	 * @param rootLevel
	 * @param writer
	 */
	public void writeRootMenu(OrganismDepartment rootLevel, MarkupWriter writer) {
		writer.element("a", "href", "../create/" + rootLevel.getOrganism().getId() + "/" + rootLevel.getId());
		writer.element("img", "src", create_department, "alt", this.messages.get("add-level-label"), "title",
				this.messages.get("add-level-label"));
		writer.end();
		writer.end();

		writer.element("img", "src", not_modify_department, "alt", this.messages.get("modify-level-label"), "title",
				this.messages.get("modify-level-label"));
		writer.end();

		writer.element("img", "src", not_delete_department, "alt", this.messages.get("delete-level-label"), "title",
				this.messages.get("delete-level-label"));
		writer.end();

		writer.element("a", "href", "../../agent/create/" + rootLevel.getOrganism().getId() + "/" + rootLevel.getId());
		writer.element("img", "src", add_agent, "alt", this.messages.get("add-agents-label"), "title",
				this.messages.get("add-agents-label"));
		writer.end();
		writer.end();

		writer.element("a", "href",
				"../../agent/listagents/" + rootLevel.getOrganism().getId() + "/" + rootLevel.getId());
		writer.element("img", "src", view_agents_level, "alt", this.messages.get("view-agents-label"), "title",
				this.messages.get("view-agents-label"));
		writer.end();
		writer.end();
	}

	/**
	 * Write menu of child level
	 * 
	 * @param childLevel
	 * @param writer
	 * @param currentLevel
	 */
	public void writeChildMenu(OrganismDepartment childLevel, MarkupWriter writer, int currentLevel) {
		writer.element("div", "class", "note_controls");

		// CREATE LEVEL
		if (currentLevel < (GuWebAppConstants.getSocleGuMaxSubDepartments() - 1)) {
			writer.element("a", "href", "../create/" + childLevel.getOrganism().getId() + "/" + childLevel.getId());
			writer.element("img", "src", create_department, "alt", this.messages.get("add-level-label"), "title",
					this.messages.get("add-level-label"));
			writer.end();
			writer.end();
		} else {
			writer.element("img", "src", not_create_department, "alt", this.messages.get("add-level-label"), "title",
					this.messages.get("add-level-label"));
			writer.end();
		}

		// MODIFY LEVEL
		writer.element("a", "href", "../modify/" + childLevel.getId());
		writer.element("img", "src", modify_department, "alt", this.messages.get("modify-level-label"), "title",
				this.messages.get("modify-level-label"));
		writer.end();
		writer.end();

		// DELETE DEPARTMENT
		if (childLevel.getIsEmpty()) {
			writer.element("a", "id", "delete", "href",
					componentResources.createEventLink("delete", childLevel.getId()));
			writer.element("img", "src", delete_department, "alt", this.messages.get("delete-level-label"), "title",
					this.messages.get("delete-level-label"));
			writer.end();
			writer.end();
		} else {
			writer.element("img", "src", not_delete_department, "alt", this.messages.get("delete-level-label"), "title",
					this.messages.get("delete-level-label"));
			writer.end();
		}

		// CREATE AGENT IN LEVEL
		writer.element("a", "href",
				"../../agent/create/" + childLevel.getOrganism().getId() + "/" + childLevel.getId());
		writer.element("img", "src", add_agent, "alt", this.messages.get("add-agents-label"), "title",
				this.messages.get("add-agents-label"));
		writer.end();
		writer.end();

		// VIEW ALL AGENTS IN LEVEL
		writer.element("a", "href",
				"../../agent/listagents/" + childLevel.getOrganism().getId() + "/" + childLevel.getId());
		writer.element("img", "src", view_agents_level, "alt", this.messages.get("view-agents-label"), "title",
				this.messages.get("view-agents-label"));
		writer.end();
		writer.end();

		writer.end();
		writer.element("br", "clear", "all");
		writer.end();
	}

	/**
	 * Event triggered when an user click on delete image.
	 * 
	 * @param organismDepartmentId
	 */
	public void onDelete(Long organismDepartmentId) {
		try {
			OrganismDepartment departmentToDelete = this.organismDepartmentService.findOne(organismDepartmentId);
			if (departmentToDelete != null) {
				this.organismDepartmentService.deleteById(organismDepartmentId);
				this.synchronizationService.notifyDeletion(departmentToDelete);
			}
		} catch (Exception e) {
			this.componentResources.triggerEvent("delete_error", null, null);
		}
		this.componentResources.triggerEvent("delete_success", null, null);
	}
}
