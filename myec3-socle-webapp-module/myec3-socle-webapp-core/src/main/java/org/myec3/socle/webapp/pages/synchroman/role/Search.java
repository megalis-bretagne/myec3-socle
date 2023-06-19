package org.myec3.socle.webapp.pages.synchroman.role;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.Role;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.service.ProfileTypeService;
import org.myec3.socle.core.service.RoleService;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Page used to search an role{@link Role}<br />
 * Redirect to SearchResult page if at least one role has been found<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/synchroman/role/Search.tml<br
 * />
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Alice Millour
 */

@SuppressWarnings("unused")
public class Search extends AbstractPage {

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String infoMessage;

	/**
	 * Business Service providing methods and specifics operations on {@link Role}
	 * objects
	 */
	@Inject
	@Service("roleService")
	private RoleService roleService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Application} objects
	 */
	@Inject
	@Service("applicationService")
	private ApplicationService applicationService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Profile} objects
	 */
	@Inject
	@Service("profileService")
	private ProfileService profileService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link ProfileType} objects
	 */
	@Inject
	@Service("profileTypeService")
	private ProfileTypeService profileTypeService;

	@Component(id = "role_search_form")
	private Form roleSearchForm;

	@Inject
	private Messages messages;

	@Property
	private String searchName;

	@Property
	private String searchProfileName;

	@Property
	private ProfileType searchProfileType;

	@Property
	private String searchLabel;

	@Property
	private Application applicationSelected;

	@Property
	private ProfileTypeValue profileTypeValueSelected;

	private List<Application> applicationList;

	private GenericListEncoder<Application> applicationEncoder;

	@Property
	private SelectModel applicationModel;

	@Property
	private List<Role> roleResults;

	@Property
	private Role roleResult;

	@InjectPage
	private SearchResult searchResultPage;

	// Activation n Passivation
	@OnEvent(EventConstants.ACTIVATE)
	public boolean onActivate() {
		this.applicationList = this.applicationService.findAll();
		Collections.sort(this.applicationList, new Comparator<Application>() {
			@Override
			public int compare(final Application object1, final Application object2) {
				return object1.getName().compareTo(object2.getName());
			}
		});
		this.applicationEncoder = new GenericListEncoder<Application>(this.applicationList);
		return Boolean.TRUE;
	}

	// Form
	@OnEvent(component = "role_search_form", value = EventConstants.SUCCESS)
	public Object onSuccess() {

		if (searchName != null || searchLabel != null || applicationSelected != null) {
			this.roleResults = this.roleService.findAllByCriteria(searchLabel, searchName, applicationSelected);
		} else {
			this.roleResults = this.roleService.findAll();
		}

		if (this.roleResults.isEmpty()) {
			this.infoMessage = this.getMessages().get("empty-result-message");
			return null;
		}

		this.searchResultPage.setRolesResult(roleResults);
		return SearchResult.class;
	}

	/**
	 * list for select Application
	 */

	public Map<Application, String> getApplicationsList() {
		Map<Application, String> mapApplication = new HashMap<Application, String>();
		Collections.sort(this.applicationList, new Comparator<Application>() {
			@Override
			public int compare(final Application object1, final Application object2) {
				return object1.getName().compareTo(object2.getName());
			}
		});
		for (Application applicationItem : this.applicationList) {
			mapApplication.put(applicationItem, applicationItem.getName());
		}
		return mapApplication;
	}

	public GenericListEncoder<Application> getApplicationEncoder() {
		return this.applicationEncoder;
	}
}
