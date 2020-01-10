package org.myec3.socle.core.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * This class represents a customer of the platform.
 * 
 * This class is synchronized.<br />
 * 
 * This class extend generic {@link Resource} class.<br/>
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Entity
@XmlRootElement
@Synchronized
public class Customer extends Resource {

	private static final long serialVersionUID = 8697177726875845950L;

	private String email;
	private Boolean authorizedToManageCompanies;
	private String logoUrl;
	private String portalUrl;
	private String documentationUrl;
	private String assistanceUrl;
	private String hotlinePhone;

	private List<AdminProfile> adminProfiles;
	private List<Application> applications = new ArrayList<Application>();

	/**
	 * Default constructor. Do nothing.
	 */
	public Customer() {
		super();
	}

	/**
	 * Contructor. Initialize the customer name
	 * 
	 * @param name : name of the customer
	 */
	public Customer(String name) {
		super(name);
	}

	/**
	 * @return contact email of customer
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Set at false by default.
	 * 
	 * @return true if the customer is authorized to manage companies in GU. False
	 *         otherwise.
	 */
	@NotNull
	@Column(nullable = false)
	@XmlTransient
	@JsonIgnore
	public Boolean isAuthorizedToManageCompanies() {
		if (authorizedToManageCompanies == null) {
			this.authorizedToManageCompanies = Boolean.FALSE;
		}
		return authorizedToManageCompanies;
	}

	public void setAuthorizedToManageCompanies(
			Boolean authorizedToManageCompanies) {
		this.authorizedToManageCompanies = authorizedToManageCompanies;
	}

	/**
	 * LogoUrl of the customer. i.e. the url where the logo associated to the
	 * customer can be found. No particular format.
	 * 
	 * @return the url of the logo for this structure
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	/**
	 * Portal url of the customer. i.e. the url where users can acess at their
	 * account. No particular format.
	 * 
	 * @return the url of the customer's portal
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getPortalUrl() {
		return portalUrl;
	}

	public void setPortalUrl(String portalUrl) {
		this.portalUrl = portalUrl;
	}

	/**
	 * Url where customer's users can access at the documentation of services.
	 * 
	 * @return the url of the documentation of services provided by the customer
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getDocumentationUrl() {
		return documentationUrl;
	}

	public void setDocumentationUrl(String documentationUrl) {
		this.documentationUrl = documentationUrl;
	}

	/**
	 * Assistance URL of the customer.
	 * 
	 * @return the assistance URL of the customer.
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getAssistanceUrl() {
		return assistanceUrl;
	}

	public void setAssistanceUrl(String assistanceUrl) {
		this.assistanceUrl = assistanceUrl;
	}

	/**
	 * Hotline phone number of the customer.
	 * 
	 * @return the hotline phone number of the customer.
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getHotlinePhone() {
		return hotlinePhone;
	}

	public void setHotlinePhone(String hotlinePhone) {
		this.hotlinePhone = hotlinePhone;
	}

	/**
	 * @return the list of {@link AdminProfile} of the customer.
	 */
	@OneToMany(mappedBy = "customer", cascade = { CascadeType.ALL })
	@XmlTransient
	@JsonIgnore
	public List<AdminProfile> getAdminProfiles() {
		if (this.adminProfiles == null) {
			this.adminProfiles = new ArrayList<AdminProfile>();
		}
		return adminProfiles;
	}

	public void setAdminProfiles(List<AdminProfile> adminProfiles) {
		this.adminProfiles = adminProfiles;
	}

	/**
	 * @return the list of {@link Application} available for the customer.
	 */
	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@XmlTransient
	@JsonIgnore
	public List<Application> getApplications() {
		return applications;
	}

	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}

	/**
	 * Add a new {@link Application} to this customer.
	 * 
	 * @param application : the new application to add
	 * @return the modified customer
	 */
	public Customer addApplication(Application application) {
		if (!this.applications.contains(application)) {
			this.applications.add(application);
		}
		return this;
	}

	/**
	 * Remove the given {@link Application} from the customer applications list. If
	 * the application does not exist, nothing is done
	 * 
	 * @param application : application to remove
	 * @return the modified customer
	 */
	public Customer removeApplication(Application application) {
		if ((this.applications != null)
				&& (this.applications.contains(application))) {
			this.applications.remove(application);
		}
		return this;
	}

}
