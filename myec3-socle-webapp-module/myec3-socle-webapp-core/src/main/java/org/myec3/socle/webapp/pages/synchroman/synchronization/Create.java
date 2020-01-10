package org.myec3.socle.webapp.pages.synchroman.synchronization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.CompanyDepartmentService;
import org.myec3.socle.core.service.CompanyService;
import org.myec3.socle.core.service.EmployeeProfileService;
import org.myec3.socle.core.service.OrganismDepartmentService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.ResourceService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class Create extends AbstractPage {

	private static Logger logger = LogManager.getLogger(Create.class);

	@SuppressWarnings("unused")
	@Component(id = "creation_synchronization_form")
	private Form form;

	// Services n pages
	@Inject
	@Service("agentProfileService")
	private AgentProfileService agentProfileService;

	@Inject
	@Service("employeeProfileService")
	private EmployeeProfileService employeeProfileService;

	@Inject
	@Service("organismService")
	private OrganismService organismService;

	@Inject
	@Service("organismDepartmentService")
	private OrganismDepartmentService organismDepartmentService;

	@Inject
	@Service("companyService")
	private CompanyService companyService;

	@Inject
	@Service("companyDepartmentService")
	private CompanyDepartmentService companyDepartmentService;

	@Inject
	private Messages messages;

	// Template properties
	@SuppressWarnings("unused")
	@Property
	@Persist("Flash")
	private String errorMessage;

	@Property
	private Long resourceId;

	@Property
	private ResourceType resourceType;

	@InjectPage
	private ManageSubscriptions manageSubscriptionPage;

	private Resource resource;

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		// Get resource to synchronize from the database
		this.resource = (Resource) this.getResourceService(this.resourceType).findOne(this.resourceId);

		if (null == this.resource) {
			logger.info("Finding resource failed");
			this.errorMessage = this.messages.get("empty-result-message");
			return null;
		}

		// // We set the resource's collections
		// if (this.resource.getClass().equals(AgentProfile.class)) {
		// this.agentProfileService
		// .populateCollections((AgentProfile) this.resource);
		// }
		// if (this.resource.getClass().equals(EmployeeProfile.class)) {
		// this.employeeProfileService
		// .populateCollections((EmployeeProfile) this.resource);
		// }
		// if (this.resource.getClass().equals(Company.class)) {
		// this.companyService.populateCollections((Company) this.resource);
		// }
		// if (this.resource.getClass().equals(CompanyDepartment.class)) {
		// this.companyDepartmentService
		// .populateCollections((CompanyDepartment) this.resource);
		// }
		// if (this.resource.getClass().equals(Organism.class)) {
		// this.organismService.populateCollections((Organism) this.resource);
		// }
		// if (this.resource.getClass().equals(OrganismDepartment.class)) {
		// this.organismDepartmentService
		// .populateCollections((OrganismDepartment) this.resource);
		// }

		this.manageSubscriptionPage.setResource(this.resource);
		this.manageSubscriptionPage.setResourceType(this.resourceType);
		return this.manageSubscriptionPage;
	}

	@OnEvent(component = "cancel", value = EventConstants.ACTION)
	public Object onFormCancel() {
		return Index.class;
	}

	/**
	 * 
	 * @param resourceType
	 * @return ResourceService
	 */
	@SuppressWarnings("unchecked")
	public ResourceService getResourceService(ResourceType resourceType) {
		logger.debug("Enterring in method getResourceService with resourceType : " + resourceType);

		switch (resourceType) {
		case AGENT_PROFILE:
			return agentProfileService;
		case EMPLOYEE_PROFILE:
			return employeeProfileService;
		case ORGANISM:
			return organismService;
		case ORGANISM_DEPARTMENT:
			return organismDepartmentService;
		case COMPANY:
			return companyService;
		case COMPANY_DEPARTMENT:
			return companyDepartmentService;
		}

		logger.error("no resource service was found for this type of resource : " + resourceType);
		return null;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Map<ResourceType, String> getResourceTypeModel() {
		ResourceType[] availableResourceType = ResourceType.values();

		Map<ResourceType, String> mapResourceType = new HashMap<ResourceType, String>();
		for (ResourceType resourceType : availableResourceType) {
			mapResourceType.put(resourceType, resourceType.toString());
		}
		return mapResourceType;
	}

	public GenericListEncoder<ResourceType> getResourceTypeEncoder() {
		ResourceType[] availableResourceType = ResourceType.values();
		List<ResourceType> listResourceType = new ArrayList<ResourceType>();
		for (ResourceType resourceType : availableResourceType) {
			listResourceType.add(resourceType);
		}
		GenericListEncoder<ResourceType> encoder = new GenericListEncoder<ResourceType>(listResourceType);
		return encoder;
	}

}
