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
import java.util.*;

import javax.imageio.ImageIO;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.AuthorizedMimeType;
import org.myec3.socle.core.domain.model.enums.OrganismMemberStatus;
import org.myec3.socle.core.domain.model.enums.OrganismNafCode;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.StructureService;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Page used to modify the organism{@link Organism}<br />
 *
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/organism/Modify.tml<br />
 *
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 *
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 */
@Import(library = { "context:/static/js/custom_datepicker.js" })
public class Modify extends AbstractPage {

	private static final Logger logger = LogManager.getLogger(Modify.class);

  private static final String EBOU = "Ebou";

	@Property
	private String errorMessage;

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
	 * {@link Structure} objects
	 */
	@Inject
	@Named("structureService")
	private StructureService structureService;

	@InjectPage
	private DetailOrganism detailOrganismPage;

	@Property
	private Organism organism;

	@Property
	@Persist
	private List<OrganismStatus> organismStatusList;

	@Persist
	private Long idControl;

	// Only used in Tapestry
	@Property
	private OrganismStatus organismStatusLoop;

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

	@Component(id = "modification_form")
	private Form form;

	@Inject
	private Block superAdminBlock;

	@Inject
	private Block userBlock;

	@Property
	private UploadedFile logo;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	@Property
	private SelectModel plateformSelectModel;

	@Inject
	private SelectModelFactory selectModelFactory;

  @AfterRender
  public void afterRender() {
    javaScriptSupport.importJavaScriptLibrary(this.getWebContext()
      + "/static/js/custom_datepicker.js");
  }

	// Page Activation n Passivation
	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	/**
	 * event activate
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return detailOrganismPage;
	}

	/**
	 * @param id : organism id
	 * @return : Object
	 */
	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		this.organism = this.organismService.findOne(id);

		if (null == this.organism) {
			return Boolean.FALSE;
		}

		if (null == this.organismStatusList || !(id.equals(this.idControl))) {
			this.organismStatusList = new ArrayList<OrganismStatus>(this.organism.getOrganismStatus());
			Collections.sort(organismStatusList, new Comparator<OrganismStatus>() {
				public int compare(OrganismStatus o1, OrganismStatus o2) {
					return o1.getDate().compareTo(o2.getDate());
				}
			});
			this.idControl = id;
		}

		// FIXME : use encoder
		if (null == this.organism.getAddress()) {
			this.organism.setAddress(new Address());
		}
		if (null != this.organism.getBudget()) {
			this.budget = String.valueOf(this.organism.getBudget());
		}
		if (null != this.organism.getWorkforce()) {
			this.workforce = String.valueOf(this.organism.getWorkforce());
		}
		if (null != this.organism.getOfficialPopulation()) {
			this.officialPopulation = String.valueOf(this.organism.getOfficialPopulation());
		}
		if (null != this.organism.getContributionAmount()) {
			this.contributionAmount = String.valueOf(this.organism.getContributionAmount());
		}

		// Check if loggedUser can access to this organism
		return this.hasRights(this.organism);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.organism != null) ? this.organism.getId() : null;
	}

	// Form events
	@OnEvent(value = EventConstants.VALIDATE, component = "modification_form")
	public void onValidate() {
		if (null != this.organism.getSiren()) {
			if (!this.structureService.isSirenValid(this.organism.getSiren())) {
				this.form.recordError(this.getMessages().get("invalid-siren-error"));
			}
		}

		// At least a status and a date need to be specified
		if (null != this.organismStatusList) {
			if (this.organismStatusList.isEmpty()) {
				this.form.recordError(this.getMessages().get("invalid-status-error"));
			}
		}

		if (this.getIsAdmin()) {
			// We must specify a date for each status
			if (this.organism.getOrganismStatus() == null) {
				this.form.recordError(this.getMessages().get("beginMemberShipDate-error"));
			}
		}

		// FIXME : use encoder
		if (null != this.budget) {
			this.organism.setBudget(Integer.parseInt(this.budget));
		} else {
			this.organism.setBudget(null);
		}
		if (null != this.workforce) {
			this.organism.setWorkforce(Integer.parseInt(this.workforce));
		} else {
			this.organism.setWorkforce(null);
		}
		if (null != this.officialPopulation) {
			this.organism.setOfficialPopulation(Integer.parseInt(this.officialPopulation));
		} else {
			this.organism.setOfficialPopulation(null);
		}
		if (null != this.contributionAmount) {
			this.organism.setContributionAmount(Integer.parseInt(this.contributionAmount));
		} else {
			this.organism.setContributionAmount(null);
		}

		// FIXME Duplicated from Organism.Create.java
		if (null != logo) {
			if (logo.getSize() > GuWebAppConstants.LOGO_MAX_SIZE) {
				this.form.recordError(this.getMessages().get("invalid-file-size-error"));
			}

			if (!AuthorizedMimeType.getValuesList().contains(this.logo.getContentType())) {
				this.form.recordError(this.getMessages().get("invalid-file-type-error"));
			}
		}
	}

	@OnEvent(EventConstants.SUCCESS)
	public Object onSuccess() {

		try {
			if (null != this.logo) {
				// FIXME duplicated from Organism.Create.java
				AuthorizedMimeType mimeType = AuthorizedMimeType.getTypeByLabel(this.logo.getContentType());
				String file_extension = mimeType.toString().toLowerCase();

				File targetDirectory = new File(
						GuWebAppConstants.FILER_LOGO_PATH + this.organism.getAcronym() + GuWebAppConstants.IMAGE);

				if (!targetDirectory.exists())
					targetDirectory.mkdirs();

				File copiedLogo = new File(GuWebAppConstants.FILER_LOGO_PATH + this.organism.getAcronym()
						+ GuWebAppConstants.IMAGE + "logo_full." + file_extension);

				logo.write(copiedLogo);

				BufferedImage originalImg = ImageIO.read(copiedLogo);
				BufferedImage resizeBufferLogo = resize(originalImg);
				File resizedLogo = new File(GuWebAppConstants.FILER_LOGO_PATH + this.organism.getAcronym()
						+ GuWebAppConstants.IMAGE + "logo." + file_extension);
				ImageIO.write(resizeBufferLogo, file_extension, resizedLogo);
				this.organism.setLogoUrl(GuWebAppConstants.FILER_LOGO_URL + this.organism.getAcronym()
						+ GuWebAppConstants.IMAGE + "logo." + file_extension);

				File copiedIcon = new File(GuWebAppConstants.FILER_LOGO_PATH + this.organism.getAcronym()
						+ GuWebAppConstants.IMAGE + "icon." + file_extension);
				this.organism.setIconUrl(GuWebAppConstants.FILER_LOGO_URL + this.organism.getAcronym()
						+ GuWebAppConstants.IMAGE + "icon." + file_extension);

				// write icon.jpeg
				ImageIO.write(resizeBufferLogo, file_extension, copiedIcon);

			}

			// Sort the list to print status according to their date in tapestry
			Collections.sort(organismStatusList, new Comparator<OrganismStatus>() {
				public int compare(OrganismStatus o1, OrganismStatus o2) {
					return o1.getDate().compareTo(o2.getDate());
				}
			});

			// We have to clear the list and re add elements instead of using a setter
			// This enable hibernate to keep an eye on the object instead of creating a new
			// one
			this.organism.clearOrganismStatus();
			this.organism.addAllOrganismStatus(organismStatusList);

			this.organism = this.organismService.update(this.organism);
			this.synchronizationService.notifyUpdate(this.organism);

		} catch (Exception e) {
			logger.error("error onSuccess " + e);
			this.errorMessage = this.getMessages().get("recording-error-message");
			return null;
		}

		this.detailOrganismPage.setSuccessMessage(this.getMessages().get("recording-success-message"));
		this.detailOrganismPage.setOrganism(this.organism);
		return this.detailOrganismPage;
	}

	@OnEvent(EventConstants.CANCELED)
	public Object onFormCancel() {
		this.detailOrganismPage.setOrganism(this.organism);
		return View.class;
	}

	/*
	 * Resize the logos to 200x200px
	 */
	private BufferedImage resize(BufferedImage originalImg) {
		BufferedImage dimg = new BufferedImage(GuWebAppConstants.LOGO_WIDTH, GuWebAppConstants.LOGO_HEIGHT,
				originalImg.getType());
		Graphics2D g = dimg.createGraphics();
		// make quality close to the original
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(originalImg, 0, 0, GuWebAppConstants.LOGO_WIDTH, GuWebAppConstants.LOGO_HEIGHT, null);
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
			availablesNafCodes.put(organismNafCode,
					organismNafCode + " - " + this.getMessages().get(organismNafCode.name().toString()));
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

	@SuppressWarnings("rawtypes")
	public ValueEncoder getEncoder() {
		return new ValueEncoder<OrganismStatus>() {

			@Override
			public String toClient(OrganismStatus value) {
				return value.getStatus().getLabel();
			}

			@Override
			public OrganismStatus toValue(String statusAsString) {
				for (OrganismStatus organismStatus : organismStatusList) {
					if (organismStatus.getStatus().equals(statusAsString)) {
						// The first object matching statusAsString will be modified
						// Removing and adding organismStatus put the object at the end of the list
						// So if there are objects with equal statuses, no one will be modified several
						// times
						organismStatusList.remove(organismStatus);
						organismStatusList.add(organismStatus);
						return organismStatus;
					}
				}
				throw new IllegalArgumentException("Received key \"" + statusAsString
						+ "\" which has no counterpart in this collection: " + organismStatusList);
			}
		};
	}

	/**
	 * Event when we add status in ajaxformloop
	 *
	 * @return status
	 */
	@OnEvent(value = EventConstants.ADD_ROW, component = "status_list")
	public OrganismStatus onAddRow() {
    OrganismStatus newOrganismStatus = new OrganismStatus(OrganismMemberStatus.ADHERENT, new Date(), this.organism);
    this.organismStatusList.add(newOrganismStatus);
    return newOrganismStatus;
	}

	/**
	 * Event when we re removing row in ajaxform loop
	 */
	@OnEvent(value = EventConstants.REMOVE_ROW, component = "status_list")
	public void removeStatus(OrganismStatus organismStatus) {
		this.organismStatusList.remove(organismStatus);
	}

	/**
	 * Display block depending user role
	 *
	 * @return Block
	 */
	public Block getChooseBlock() {
		if (this.getIsAdmin()) {
			return this.superAdminBlock;
		}
		return this.userBlock;
	}

	/**
	 * Setup organism status according to the plateform
	 *
	 */
	public void setupRender() {

    OrganismMemberStatus[] availablesOrganismStatus = OrganismMemberStatus.values();
    List<OrganismMemberStatus> organismMemberStatuses = new ArrayList<OrganismMemberStatus>();
    Collections.addAll(organismMemberStatuses, availablesOrganismStatus);
    if (EBOU.equals(this.getMessages().get("plateform-name"))) {
      plateformSelectModel = selectModelFactory.create(organismMemberStatuses, "label");
    } else {
      organismMemberStatuses.remove(OrganismMemberStatus.DISSOLUTION);
      organismMemberStatuses.remove(OrganismMemberStatus.TEST);
      plateformSelectModel = selectModelFactory.create(organismMemberStatuses, "label");
    }
		
	}

}
