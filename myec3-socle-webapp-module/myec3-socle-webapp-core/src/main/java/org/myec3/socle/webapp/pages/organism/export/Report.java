package org.myec3.socle.webapp.pages.organism.export;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Address;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.enums.Article;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.domain.model.enums.OrganismINSEECat;
import org.myec3.socle.core.domain.model.enums.OrganismNafCode;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.tools.UnaccentLetter;
import org.myec3.socle.core.util.CsvHelper;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/**
 * Page used during prospect organism{@link Organism} import process.<br />
 *
 * It's an entry point for uploading csv file of prospect organisms<br />
 * and adding them into organisms database.<br />
 *
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 *
 *      Corresponding tapestry template file is :
 *      src/main/resources/org/myec3/socle/webapp/pages/organism/ImportProspects.tml
 *
 * @author Ludovic Lavigne <ludovic.lavigne@atos.net>
 */
public class Report extends AbstractPage {

	private static final Logger logger = LoggerFactory.getLogger(Report.class);

	private static final String PROSPECTS_FILENAME = "prospects";

	@Autowired
	private Environment environment;

	@Inject
	private Messages messages;

	// Template properties
	@SuppressWarnings("unused")
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@Inject
	private ComponentResources componentResources;

	@Inject
	@Service("organismService")
	private OrganismService organismService;

	@Persist
	private File uploadedFile;

	@Persist
	private List<Organism> organismsInImport;

	@Persist
	private Map<Long, List<String>> mapFieldErrors;

	@Persist
	private List<Map<String, String>> mappedCsvData;

//	@Inject
//	private Block errorBlock;
//
//	@Inject
//	private Block successBlock;
//
//	@Property
//	private Long errorLineLoop;

	@SuppressWarnings("unused")
	@Property
	private String errorLoop;

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return Boolean.TRUE;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Integer numberOfLinesToImport, Integer firstLine) {

		// check filer and existence of prospects.csv file (which was writen only if it
		// has no error)
		uploadedFile = new File(GuWebAppConstants.FILER_IMPORT_PATH
				+ PROSPECTS_FILENAME + CsvHelper.getExtension());

		if (uploadedFile.exists()) {
			try {
				// if (null == this.mappedCsvData) {
				this.mappedCsvData = CsvHelper.getMappedData(this.uploadedFile);

				// check nbLines
				// int nbLinesToList = this.mappedCsvData.size();
				// We check file content
				this.checkFileContent(numberOfLinesToImport, firstLine);
				// }

				// If there are no errors in the file we retrieve the list of organisms
				if (!this.getImportHasErrors()) {

					this.organismsInImport = this.convertImportToListOfOrganisms(numberOfLinesToImport, firstLine);

					// Creation
					for (Organism organism : organismsInImport) {
						this.organismService.create(organism);
						// Wait to avoid problems when propagating synchronization messages
						Thread.sleep(2000);
					}
				} else {
					logger.error("Prospects from " + firstLine + " to " + firstLine + numberOfLinesToImport
							+ " are not successfully imported");
				}
			} catch (Exception ex) {
				this.errorMessage = ex.getMessage();
				logger.error("An exception has occured during onActivate : " + ex);
				// this.clearPersistentParams();
				return null;
			}
		} else {
			return ImportProspects.class;
		}

		return Boolean.TRUE;
	}

	/**
	 * Check datas contained into the file
	 */
	public void checkFileContent(Integer numberOfLinesToImport, Integer firstLine) throws Exception {
		int currentLine = firstLine;
		this.mapFieldErrors = new HashMap<Long, List<String>>();

		logger.info(
				"CheckFileContent: FirstLine : " + firstLine + " and numberOfLinesToImport : " + numberOfLinesToImport);
		Map<String, String> mapLine;

		for (int lineNumber = firstLine; lineNumber < firstLine + numberOfLinesToImport; lineNumber++) {

			logger.debug("CheckFileContent: Retrieve lineNumber " + lineNumber);

			mapLine = this.mappedCsvData.get(lineNumber);
			// for (Map<String, String> mapLine : this.mappedCsvData) {
			// Increment line number
			currentLine++;

			// List of errors contained into the current line
			List<String> lineFieldErrors = new ArrayList<String>();

			for (int i = 0; i < mapLine.size(); i++) {
				// We retrieve the key
				String mapKey = UnaccentLetter.unaccentString(this
						.getMessages().get("organism-import-header-column-" + i));

				// We retrieve the field value corresponding at the key
				String data = mapLine.get(mapKey.toLowerCase());

				// If the field is empty we must check if the field is
				// required
				if (data.isEmpty()) {
					if (this.isFieldRequired(mapKey)) {
						lineFieldErrors.add(this.getMessages().format(
								"organism-import-column-required-message", mapKey));
					}
				} else {
					// If the field is not empty we must check its content
					// by
					// using regex defined into propertie file
					if (!this.isFieldMatchRegex(mapKey, data)) {
						lineFieldErrors.add(this.getMessages().format(
								"organism-import-column-message-" + mapKey));
					}

					// check siren not already exists & is valid
					if (mapKey.equals("siren")) {
						if (!this.organismService.isSirenValid(data)) {
							lineFieldErrors.add(this.getMessages().format(
									"organism-import-column-siren-invalid-message"));
						} else {
							if (null != this.organismService.findBySiren(data)) {
								lineFieldErrors.add(this.getMessages().format(
										"organism-import-column-siren-already-exists-message"));
							}
						}
					}
				}
			}

			// We put the list of errors corresponding at this line into
			// the
			// map
			if (lineFieldErrors.size() > 0) {
				this.mapFieldErrors.put(new Long(currentLine), lineFieldErrors);
				logger.error("Error at line " + currentLine + ": " + lineFieldErrors);
			}
		}
	}

	/**
	 * @param mapKey
	 * @return true is the field to import is required
	 */
	public Boolean isFieldRequired(String mapKey) {
		Assert.notNull(mapKey, "mapKey can't be null");

		if (Integer.valueOf(this.getMessages().get(
				"organism-import-column-required-" + mapKey)) == 1) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * This method check if value is correct
	 *
	 * @param columnTitle : the title of the current column
	 * @param fieldValue  : the value of the current field of the current line
	 * @return true if the value is correct, false otherwise
	 */
	public Boolean isFieldMatchRegex(String columnTitle, String fieldValue) {
		Assert.notNull(columnTitle, "columnTitle can't be null");
		Assert.notNull(fieldValue, "fieldValue can't be null");

		logger.debug("Checking field of column " + columnTitle
				+ " with value = " + fieldValue);

		String regex = null;
		Matcher matcher = null;
		Pattern pattern = null;
		Boolean match = Boolean.FALSE;

		try {
			// We retrieve the regex corresponding at the field into the
			// propertie
			// file
			regex = this.getMessages().get(
					"organism-import-column-regex-" + columnTitle);
			if (regex != null) {
				if (regex.startsWith("^")) {
					pattern = Pattern.compile(regex);
					matcher = pattern.matcher(fieldValue);
					return matcher.matches();
				} else {
					// In case of enum we split the string contained into the
					// propertie file
					String[] regexTab = regex.split("\\|");
					if (regexTab.length > 0) {
						for (int i = 0; i < regexTab.length; i++) {
							if (regexTab[i].trim().equalsIgnoreCase(fieldValue)) {
								match = Boolean.TRUE;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Une erreur s'est produite pendant la vérification des champs");
		}
		return match;
	}

	/**
	 * This method allows to convert the mapped file into a list of organism
	 *
	 *
	 * @return a list of organisms
	 */
	public List<Organism> convertImportToListOfOrganisms(Integer numberOfLinesToImport, Integer firstLine) {
		List<Organism> listOfOrganismInImport = new ArrayList<Organism>(numberOfLinesToImport);

		Messages messages = this.getMessages();

		String statusKey = messages.get("organism-import-column-statut");
		String articleKey = messages.get("organism-import-column-article");
		String organismNameKey = messages.get("organism-import-column-denomination");
		String sirenKey = messages.get("organism-import-column-siren");
		String collegeKey = messages.get("organism-import-column-college");
		String populationKey = messages.get("organism-import-column-populationofficielle");
		String apeCodeKey = messages.get("organism-import-column-codenaf");
		String legalCategoryKey = messages.get("organism-import-column-naturejuridique");
		String workForceKey = messages.get("organism-import-column-effectif");
		String budgetKey = messages.get("organism-import-column-budget");
		String contributionAmountKey = messages.get("organism-import-column-montantdecotisation");
		String descriptionKey = messages.get("organism-import-column-description");
		String cityKey = messages.get("organism-import-column-ville");
		String adressKey = messages.get("organism-import-column-adresse");
		String postalCodeKey = messages.get("organism-import-column-codepostal");
		String cantonKey = messages.get("organism-import-column-canton");
		String mailKey = messages.get("organism-import-column-courriel");
		String phoneKey = messages.get("organism-import-column-telephonefixe");
		String faxKey = messages.get("organism-import-column-fax");
		String websiteKey = messages.get("organism-import-column-siteinternet");

		Map<String, String> mapLine;
		for (int lineNumber = firstLine; lineNumber < firstLine + numberOfLinesToImport; lineNumber++) {
			// for (Map<String, String> mapLine : this.mappedCsvData) {
			mapLine = this.mappedCsvData.get(lineNumber);

			Organism organism = new Organism();

			String statut = mapLine.get(statusKey);
			// parse statut to integer
			int status = Integer.parseInt(statut);
			/*
			 * if (status == 0) { //prospect
			 * organism.setMemberStatus(OrganismMemberStatus.PROSPECT); } else if
			 * (status==2) { //refus adhesion
			 * organism.setMemberStatus(OrganismMemberStatus.REFUS_D_ADHERER); } else if
			 * (status==4) { //resilie
			 * organism.setMemberStatus(OrganismMemberStatus.RESILIE); } else { //prospect
			 * organism.setMemberStatus(OrganismMemberStatus.PROSPECT); }
			 * 
			 * logger.debug("status not prospect :" + organism.getMemberStatus());
			 */

			// deny access of services
			organism.setMember(Boolean.FALSE);

			// article
			String article = mapLine.get(articleKey);
			if (!article.isEmpty()) {
				if (article.equals((Article.LE.getLabel()))) {
					organism.setArticle(Article.LE);
				} else if (article.equals(Article.LA.getLabel())) {
					organism.setArticle(Article.LA);
				} else {
					organism.setArticle(Article.L);
				}
			}

			String organismName = mapLine.get(organismNameKey);
			organism.setLabel(organismName);
			organism.setName(organismName);
			organism.setSiren(mapLine.get(sirenKey));

			String legalCategory = mapLine.get(legalCategoryKey);
			if (!legalCategory.isEmpty()) {
				// break when matching value to avoid looping to every elements
				legalCategoryLoop: for (OrganismINSEECat currentlegalCategory : OrganismINSEECat.values()) {
					if (currentlegalCategory.getId().equals(legalCategory)) {
						organism.setLegalCategory(currentlegalCategory);
						break legalCategoryLoop;
					}
					if (null == organism.getLegalCategory()) {
						organism.setLegalCategory(OrganismINSEECat._1);
					}
				}
			} else {
				organism.setLegalCategory(OrganismINSEECat._1);
			}

			logger.debug("Organism Legal Cat : " + organism.getLegalCategory());

			String apeCode = mapLine.get(apeCodeKey);
			if (!apeCode.isEmpty()) {
				// break when matching value to avoid looping to every elements
				apeCodeLoop: for (OrganismNafCode currentApeCode : OrganismNafCode.values()) {
					if (currentApeCode.getApeCode().equals(apeCode)) {
						organism.setApeCode(currentApeCode);
						break apeCodeLoop;
					}
				}
			}

			organism.setCollege(getCollegeCatFromNumber(Integer.parseInt(mapLine.get(collegeKey))));

			String officialPopulation = mapLine.get(populationKey);
			if (!officialPopulation.isEmpty()) {
				organism.setOfficialPopulation(Integer.parseInt(officialPopulation));
			}

			String workForce = mapLine.get(workForceKey);
			if (!workForce.isEmpty()) {
				organism.setWorkforce(Integer.parseInt(workForce));
			}

			String budget = mapLine.get(budgetKey);
			if (!budget.isEmpty()) {
				organism.setBudget(Integer.parseInt(budget));
			}

			String contributionAmount = mapLine.get(contributionAmountKey);
			if (!contributionAmount.isEmpty()) {
				organism.setContributionAmount(Integer.parseInt(contributionAmount));
			}

			String description = mapLine.get(descriptionKey);
			if (!description.isEmpty()) {
				organism.setDescription(description);
			}

			organism.setAddress(new Address());
			organism.getAddress().setCity(mapLine.get(cityKey));
			organism.getAddress().setPostalAddress(mapLine.get(adressKey));
			organism.getAddress().setPostalCode(mapLine.get(postalCodeKey));
			organism.getAddress().setCountry(Country.FR);

			String canton = mapLine.get(cantonKey);
			if (!canton.isEmpty()) {
				organism.getAddress().setCanton(canton);
			}

			organism.setEmail(mapLine.get(mailKey));

			String phone = mapLine.get(phoneKey);
			if (!phone.isEmpty()) {
				organism.setPhone(phone);
			}

			String fax = mapLine.get(faxKey);
			if (!fax.isEmpty()) {
				organism.setFax(fax);
			}

			String website = mapLine.get(websiteKey);
			if (!website.isEmpty()) {
				organism.setWebsite(website);
			}

			logger.info("convertImportToListOfOrganisms: Following organism will be created: " + organism.getName());

			// ADD ORGANISM INTO LIST
			listOfOrganismInImport.add(organism);

		}

		return listOfOrganismInImport;

	}

	/**
	 *
	 * @param collegeNumber, id between 1 and 12
	 * @return collegeCat associated
	 */
	private String getCollegeCatFromNumber(int collegeNumber) {
		String orgaCollegeCat;
		switch (collegeNumber) {
			case 0:
				orgaCollegeCat = environment.getProperty("OrganismCollegeCat.A_DEFINIR");
				break;
			case 1:
				orgaCollegeCat = environment.getProperty("OrganismCollegeCat.MEMBRES_FONDATATEURS");
				break;
			case 2:
				orgaCollegeCat = environment.getProperty("OrganismCollegeCat.MEMBRES_ASSOCIES");
				break;
			case 3:
				orgaCollegeCat = environment.getProperty("OrganismCollegeCat.COMMUNES_MOINS_20_000_HABITANTS");
				break;
			case 4:
				orgaCollegeCat = environment.getProperty("OrganismCollegeCat.COMMUNES_ENTRE_20_000_ET_50_000_HABITANTS");
				break;
			case 5:
				orgaCollegeCat = environment.getProperty("OrganismCollegeCat.COMMUNES_PLUS_50_000_HABITANTS");
				break;
			case 6:
				orgaCollegeCat = environment.getProperty("OrganismCollegeCat.EPCI_A_FISCALITE_MOINS_50_000_HABITANTS");
				break;
			case 7:
				orgaCollegeCat = environment.getProperty("OrganismCollegeCat.EPCI_A_FISCALITE_PLUS_50_000_HABITANTS");
				break;
			case 8:
				orgaCollegeCat = environment.getProperty("OrganismCollegeCat.EPCI_SANS_FISCALITE");
				break;
			case 9:
				orgaCollegeCat = environment.getProperty("OrganismCollegeCat.BAILLEURS_SOCIAUX");
				break;
			case 10:
				orgaCollegeCat = environment.getProperty("OrganismCollegeCat.ETABLISSEMENTS_SANTE");
				break;
			case 11:
				orgaCollegeCat = environment.getProperty("OrganismCollegeCat.AUTRES");
				break;
			case 12:
				orgaCollegeCat = environment.getProperty("OrganismCollegeCat.ORGANISME_DIVERS");
				break;
			default:
				orgaCollegeCat = environment.getProperty("OrganismCollegeCat.A_DEFINIR");
				break;
		}
		return orgaCollegeCat;
	}

	public void setUploadedFile(File uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public File getUploadedFile() {
		return uploadedFile;
	}

	public Messages getMessages() {
		return messages;
	}

	/**
	 * @return true if file uploaded contains errors
	 */
	public Boolean getImportHasErrors() {
		if ((this.mapFieldErrors != null) && (this.mapFieldErrors.size() > 0)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * @return the list of lines witch contains error(s)
	 */
	public List<Long> getListOfErrorsLine() {
		List<Long> listOfErrors = new ArrayList<Long>();
		for (Entry<Long, List<String>> entry : this.mapFieldErrors.entrySet()) {
			listOfErrors.add(Long.valueOf(entry.getKey()));
		}
		Collections.sort(listOfErrors);
		return listOfErrors;
	}

//	/**
//	 * @return all errors of the current line (errorLineLoop)
//	 */
//	public List<String> getErrorsLine() {
//		List<String> listOfErrors = new ArrayList<String>();
//		List<String> errors = this.mapFieldErrors.get(this.errorLineLoop);
//		for (int i = 0; i < errors.size(); i++) {
//			listOfErrors.add(errors.get(i));
//		}
//		return listOfErrors;
//	}
//
//	/**
//	 * @return the correct block to display on the page
//	 */
//	public Block getChooseBlock() {
//		if (this.getImportHasErrors()) {
//			return this.errorBlock;
//		}
//		return this.successBlock;
//	}
}
