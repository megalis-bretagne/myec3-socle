/**
 * Copyright (c) 2011 Atos Bourgogne
 * <p>
 * This file is part of MyEc3.
 * <p>
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * <p>
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.webapp.pages.organism;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.myec3.socle.core.domain.model.AdminProfile;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismStatus;
import org.myec3.socle.core.domain.model.enums.AuthorizedMimeType;
import org.myec3.socle.core.domain.model.enums.OrganismMemberStatus;
import org.myec3.socle.core.domain.model.enums.OrganismNafCode;
import org.myec3.socle.core.service.CustomerService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.service.exceptions.AllAcronymsUsedException;
import org.myec3.socle.core.service.exceptions.OrganismCreationException;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.encoder.GenericListEncoder;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

import javax.imageio.ImageIO;
import javax.inject.Named;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;

/**
 * Page used during organism{@link Organism} creation process.<br />
 *
 * It's the first step to create an organism. In this step your must fill<br />
 * organism attributes.<br />
 *
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/organism/Create.tml
 *
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <anthony.j.colas@atosorigin.com>
 */
public class Create extends AbstractPage {

  private static final Log logger = LogFactory.getLog(Create.class);

  private static final String EBOU = "Ebou";

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
  public void activation() {
    super.initUser();
  }

  @Getter
  @Setter
  @Persist(PersistenceConstants.FLASH)
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

  // Page Activation n Passivation

  /**
   * event activate
   */
  @OnEvent(EventConstants.ACTIVATE)
  public Object onActivate() {
    if (this.organism != null ) {
      organismStatus = new OrganismStatus();
      return Boolean.TRUE;
    } else {
      return Boolean.FALSE;
    }
  }

  @SuppressWarnings("squid:S4165")
  @OnEvent(EventConstants.PASSIVATE)
  public void onPassivate() {
    // NECESSAIRE POUR VALIDER LE FORMULAIRE => sinon NULL POINTER
    this.organism = organism;
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
//    todo: voir avec Megalis
    this.organismStatus.setDate(new Date());

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
        String fileExtension = mimeType.toString().toLowerCase();

        String finNomFic = "structure_id_" + this.organism.getId() + "." + fileExtension;
        String nomFicLogoFull = "logo_full_" + finNomFic;
        String nomFicLogo = "logo_" + finNomFic;
        String nomFicIcon = "icon_" + finNomFic;

        File copiedLogo = new File(GuWebAppConstants.FILER_LOGO_PATH + nomFicLogoFull);
        logo.write(copiedLogo);

        /* Resize LOGO for 200x200 pixel */
        FileInputStream fis = new FileInputStream(copiedLogo);
        BufferedImage originalImg = ImageIO.read(fis);
        BufferedImage resizeBufferLogo = resize(originalImg);
        File resizedLogo = new File(GuWebAppConstants.FILER_LOGO_PATH + nomFicLogo);

        // write logo.jpeg
        ImageIO.write(resizeBufferLogo, fileExtension, resizedLogo);

        this.organism.setLogoUrl(GuWebAppConstants.FILER_LOGO_URL + nomFicLogo);

        File copiedIcon = new File(GuWebAppConstants.FILER_LOGO_PATH + nomFicIcon);
        this.organism.setIconUrl(GuWebAppConstants.FILER_LOGO_URL + nomFicIcon);

        // write icon.jpeg
        ImageIO.write(resizeBufferLogo, fileExtension, copiedIcon);

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
    Map<OrganismNafCode, String> availablesNafCodes = new LinkedHashMap<>();
    OrganismNafCode[] nafCodeList = OrganismNafCode.values();
    for (OrganismNafCode organismNafCode : nafCodeList) {
      availablesNafCodes
        .put(organismNafCode,
          organismNafCode
            + " - "
            + this.getMessages().get(
            organismNafCode.name()));
    }

    return availablesNafCodes;
  }

  public ValueEncoder<OrganismNafCode> getNafCodeEncoder() {
    return new GenericListEncoder<>(Arrays.asList(OrganismNafCode.values()));
  }

  /**
   * list for select Customers
   */
  public Map<Customer, String> getCustomersList() {
    List<Customer> customersList = this.customerService.findAll();
    Map<Customer, String> customersMap = new LinkedHashMap<>();
    for (Customer customer : customersList) {
      customersMap.put(customer, customer.getLabel());
    }
    return customersMap;
  }

  public ValueEncoder<Customer> getCustomerEncoder() {
    return new GenericListEncoder<>(this.customerService.findAll());

  }

  /**
   * Setup organism status according to the plateform
   *
   */
  public void setupRender() {

    OrganismMemberStatus[] availablesOrganismStatus = OrganismMemberStatus.values();
    List<OrganismMemberStatus> organismMemberStatuses = new ArrayList<>();
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
