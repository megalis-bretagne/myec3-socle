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
package org.myec3.socle.webapp.pages.organism;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.AuthorizedMimeType;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.domain.model.enums.OrganismNafCode;
import org.myec3.socle.core.service.CustomerService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.exceptions.AllAcronymsUsedException;
import org.myec3.socle.core.service.exceptions.OrganismCreationException;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.model.CollegeCategorieSelectModel;
import org.myec3.socle.webapp.model.OrganismMemberStatusSelectModel;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

/**
 * Page used during organism{@link Organism} creation process.<br />
 * 
 * It's the first step to create an organism. In this step your must fill<br />
 * organism attributes.<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/organism/Create.tml
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <anthony.j.colas@atosorigin.com>
 */
public class Create extends AbstractPage {

	private static final Log logger = LogFactory.getLog(Create.class);

	// Template properties
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Organism} objects
	 */
	@Inject
	@Named("organismService")
	private OrganismService organismService;

	/**
	 * Business Service providing methods and specifics operations on
	 * {@link Customer} objects
	 */
	@Inject
	@Named("customerService")
	private CustomerService customerService;

	@InjectPage
	private CreateSubscriptions createSubscriptions;

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	@Property
	private Organism organism;

	@Component(id = "modification_form")
	private Form form;

	@Property
	private UploadedFile logo;

	@Property
	private Customer selectedCustomer;

	// FIXME: Je passe les integer en String pour que tapestry n'autorise pas
	// les ","
	@Property
	private String budget;

	@Property
	private String workforce;

	@Property
	private String officialPopulation;

	@Property
	private String contributionAmount;

	@Property
	private SelectModel plateformSelectModel;

	@Inject
	private SelectModelFactory selectModelFactory;

	@Property
	private OrganismStatus organismStatus = new OrganismStatus();

	@Property
	private SelectModel listCollegeModel;

	@Property
	private SelectModel listOrganismMemberStatusModel;

	// Page Activation n Passivation
	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		organism = new Organism();
		organismStatus = new OrganismStatus();
		organism.setAddress(new Address());
		organism.getAddress().setCity("");
		organism.getAddress().setPostalAddress("");
		organism.getAddress().setPostalCode("");
		organism.getAddress().setCountry(Country.FR);
		return Boolean.TRUE;
	}

	// Form events
	@OnEvent(value = EventConstants.VALIDATE, component = "modification_form")
	public void onValidate() {
		// FIXME : use encoder
		if (null != this.budget) {
			this.organism.setBudget(Integer.parseInt(this.budget));
		}
		if (null != this.workforce) {
			this.organism.setWorkforce(Integer.parseInt(this.workforce));
		}
		if (null != this.officialPopulation) {
			this.organism.setOfficialPopulation(Integer
					.parseInt(this.officialPopulation));
		}
		if (null != this.contributionAmount) {
			this.organism.setContributionAmount(Integer
					.parseInt(this.contributionAmount));
		}
	}

	@OnEvent(value = EventConstants.VALIDATE, component = "logo")
	public void validateLogo(UploadedFile logo) {
		if (null != logo) {
			if (logo.getSize() > GuWebAppConstants.LOGO_MAX_SIZE) {
				this.form.recordError(this.getMessages().get(
						"invalid-file-size-error"));
			}

			if (!AuthorizedMimeType.getValuesList().contains(
					logo.getContentType())) {
				this.form.recordError(this.getMessages().get(
						"invalid-file-type-error"));
			}
		}
	}

	@OnEvent(value = EventConstants.VALIDATE, component = "siren")
	public void validateSiren(String siren) {
		if (null != siren) {
			if (!this.organismService.isSirenValid(siren)) {
				this.form.recordError(this.getMessages().get(
						"invalid-siren-error"));
			}

			// We check if an organism with this siren not already exists into
			// the database
			if (null != this.organismService.findBySiren(siren)) {
				this.form.recordError(this.getMessages().get(
						"organism-exists-error"));
			}
		}
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {
		this.organism.setName(organism.getLabel());
		this.organism.setMember(Boolean.TRUE);

		// In case of user connected hasn't SUPER_ADMIN role
		if (this.selectedCustomer == null) {
			this.selectedCustomer = this.customerService
					.findByAdminProfile((AdminProfile) this.getLoggedProfile());
		}
		this.organism.setCustomer(this.selectedCustomer);

		this.organismStatus.setOrganism(this.organism);
		this.organism = this.organism.addOrganismStatus(this.organismStatus);

		try {
			this.organismService.create(organism);
			
			//Generate apiKey
			String apiPassword = RandomStringUtils.randomAlphanumeric(12);
			String apiKey = new String(Base64.encodeBase64((organism.getId().toString() + ':' + apiPassword).getBytes()), "UTF-8");
			this.organism.setApiKey(apiKey);
			this.organismService.update(organism);

			if (null != this.logo) {
				// FIXME can put that part in the organismService because
				// eb-core does'nt import tapestry
				AuthorizedMimeType mimeType = AuthorizedMimeType
						.getTypeByLabel(this.logo.getContentType());
				String file_extension = mimeType.toString().toLowerCase();

				File targetDirectory = new File(GuWebAppConstants.FILER_LOGO_PATH
						+ this.organism.getAcronym() + GuWebAppConstants.IMAGE);
				targetDirectory.mkdirs();

				File copiedLogo = new File(GuWebAppConstants.FILER_LOGO_PATH
						+ this.organism.getAcronym() + GuWebAppConstants.IMAGE
						+ "logo_full." + file_extension);

				logo.write(copiedLogo);

				/* Resize LOGO for 200x200 pixel */
				FileInputStream fis = new FileInputStream(copiedLogo);
				BufferedImage originalImg = ImageIO.read(fis);
				BufferedImage resizeBufferLogo = resize(originalImg);
				File resizedLogo = new File(GuWebAppConstants.FILER_LOGO_PATH
						+ this.organism.getAcronym() + GuWebAppConstants.IMAGE
						+ "logo." + file_extension);

				// write logo.jpeg
				ImageIO.write(resizeBufferLogo, file_extension, resizedLogo);
				this.organism.setLogoUrl(GuWebAppConstants.FILER_LOGO_URL
						+ this.organism.getAcronym() + GuWebAppConstants.IMAGE
						+ "logo." + file_extension);

				File copiedIcon = new File(GuWebAppConstants.FILER_LOGO_PATH
						+ this.organism.getAcronym() + GuWebAppConstants.IMAGE
						+ "icon." + file_extension);
				this.organism.setIconUrl(GuWebAppConstants.FILER_LOGO_URL
						+ this.organism.getAcronym() + GuWebAppConstants.IMAGE
						+ "icon." + file_extension);

				// write icon.jpeg
				ImageIO.write(resizeBufferLogo, file_extension, copiedIcon);
				
				this.organismService.update(organism);
			}
		} catch (AllAcronymsUsedException e) {
			this.errorMessage = this.getMessages().get("no-free-acronym-error");
			logger.error(e);
			return null;
		} catch (OrganismCreationException e) {
			this.errorMessage = this.getMessages().get(
					"organism-creation-error");
			logger.error(e);
			return null;
		} catch (FileNotFoundException e) {
			this.errorMessage = this.getMessages().get("file-copying-error");
			logger.error(e);
			return null;
		} catch (IOException e) {
			this.errorMessage = this.getMessages().get("file-copying-error");
			logger.error(e);
			return null;
		}

		this.createSubscriptions.setSuccessMessage(this.getMessages().get(
				"recording-success-message"));
		this.createSubscriptions.setOrganism(organism);
		return this.createSubscriptions;
	}

	@OnEvent(EventConstants.CANCELED)
	public Object onFormCancel() {
		return Index.class;
	}

	/**
	 * Resize the logos to 200x200px
	 */
	private BufferedImage resize(BufferedImage originalImg) {
		BufferedImage dimg = new BufferedImage(GuWebAppConstants.LOGO_WIDTH,
				GuWebAppConstants.LOGO_HEIGHT, originalImg.getType());
		Graphics2D g = dimg.createGraphics();
		// make quality close to the original
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(originalImg, 0, 0, GuWebAppConstants.LOGO_WIDTH,
				GuWebAppConstants.LOGO_HEIGHT, null);
		g.dispose();
		return dimg;
	}

	/**
	 * list for select NafCode
	 */
	public Map<OrganismNafCode, String> getListOfOrganismNafCode() {
		Map<OrganismNafCode, String> availablesNafCodes = new LinkedHashMap<OrganismNafCode, String>();
		OrganismNafCode[] nafCodeList = OrganismNafCode.values();
		for (OrganismNafCode organismNafCode : nafCodeList) {
			availablesNafCodes
					.put(organismNafCode,
							organismNafCode
									+ " - "
									+ this.getMessages().get(
											organismNafCode.name().toString()));
		}

		return availablesNafCodes;
	}

	public ValueEncoder<OrganismNafCode> getNafCodeEncoder() {
		OrganismNafCode[] availablesNafCodes = OrganismNafCode.values();
		List<OrganismNafCode> nafCodeList = new ArrayList<OrganismNafCode>();
		for (OrganismNafCode organismNafCode : availablesNafCodes) {
			nafCodeList.add(organismNafCode);
		}
		return new GenericListEncoder<OrganismNafCode>(nafCodeList);
	}

	/**
	 * list for select Customers
	 */
	public Map<Customer, String> getCustomersList() {
		List<Customer> customersList = this.customerService.findAll();
		Map<Customer, String> customersMap = new LinkedHashMap<Customer, String>();
		for (Customer customer : customersList) {
			customersMap.put(customer, customer.getLabel());
		}
		return customersMap;
	}

	public ValueEncoder<Customer> getCustomerEncoder() {
		return new GenericListEncoder<Customer>(this.customerService.findAll());

	}

	/**
	 * Setup organism status according to the plateform
	 *
	 */
	public void setupRender() {

		List<OrganismMemberStatus> organismMemberStatuses = new ArrayList<>();
		for (String key : this.getMessages().getKeys()) {
			if (key.contains("OrganismMemberStatus")) {
				String keyValue = key.split("\\.")[1];
				OrganismMemberStatus organismMemberStatus = new OrganismMemberStatus(keyValue, this.getMessages().get(key));
				organismMemberStatuses.add(organismMemberStatus);
			}
		}
		listOrganismMemberStatusModel = new OrganismMemberStatusSelectModel(organismMemberStatuses);
		
		List<CollegeCategorie> colleges = new ArrayList<>();
		
		for (String key : this.getMessages().getKeys()) {
			if (key.contains("OrganismCollegeCat")) {
				String keyValue = key.split("\\.")[1];
				CollegeCategorie college = new CollegeCategorie(keyValue, this.getMessages().get(key));
				colleges.add(college);
			}
		}

        Collections.sort(colleges, new Comparator<CollegeCategorie>() {
            @Override
            public int compare(CollegeCategorie o1, CollegeCategorie o2) {
                try {
                    int indiceCC1 = Integer.parseInt(o1.getName().split("-")[0].replace(" ", ""));
                    int indiceCC2 = Integer.parseInt(o2.getName().split("-")[0].replace(" ", ""));
                    if (indiceCC1 == indiceCC2) {
                        return 0;
                    } else if (indiceCC1 < indiceCC2) {
                        return -1;
                    }
                } catch (Exception e) {
                    logger.error("Problème lors du tri des colleges categories : " + e.getMessage());
                }
                return 1;
            }
        });
		
		listCollegeModel = new CollegeCategorieSelectModel(colleges);
		
	}

}
