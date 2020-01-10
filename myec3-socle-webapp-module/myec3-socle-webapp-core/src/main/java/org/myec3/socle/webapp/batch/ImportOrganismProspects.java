package org.myec3.socle.webapp.batch;

import org.myec3.socle.core.domain.model.Address;
import org.myec3.socle.core.domain.model.Customer;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.enums.*;
import org.myec3.socle.core.service.CustomerService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.tools.UnaccentLetter;
import org.myec3.socle.core.util.CsvHelper;
import org.myec3.socle.synchro.api.constants.SynchronizationJobType;
import org.myec3.socle.synchro.core.domain.model.SynchronizationQueue;
import org.myec3.socle.synchro.core.service.SynchronizationQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used to import a list of {@link Organism} from a csv file.<br />
 *
 * @author Matthieu GASPARD <matthieu.gaspard@worldline.com>
 */
public class ImportOrganismProspects {

	private static OrganismService organismService;
	private static CustomerService customerService;
	private static SynchronizationQueueService synchronizationQueueService;

	private static final int HEADER_LENGTH = 20;
	private static final int MIN_NB_LINES = 2;

	private final String[] fileHeaders = new String[HEADER_LENGTH];
	private final int[] fieldRequirement = new int[HEADER_LENGTH];
	private final String[] fieldRegex = new String[HEADER_LENGTH];

	private List<Map<String, String>> mappedCsvData;
	private Map<Long, List<String>> mapFieldErrors;

	@Autowired
	private Environment environment;

	/**
	 * Logger for this class
	 */
	private static Logger logger = LoggerFactory.getLogger(ImportOrganismProspects.class);

	private MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

	private int numberOfLinesToImport;
	private static int firstLine = 0;
	private static int lastLine = 0;

	public static void main(String[] args) {
		String fileLocation = "";
		if (args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith("-h")) {
					System.out.println("usage: ImportOrganismProspects [-h -readonly]");
					System.out.println(" -h : affiche l'aide.");
					System.out.println(" -file <path> : chemin vers le fichier à importer");

					System.exit(0);
				}
				if (args[i].startsWith("-file")) {
					fileLocation = args[i + 1];
				}

				if (args[i].startsWith("-firstLine")) {
					firstLine = Integer.valueOf(args[i + 1]);
				}

				if (args[i].startsWith("-lastLine")) {
					lastLine = Integer.valueOf(args[i + 1]);
				}

				if ((firstLine >= lastLine) && firstLine != 0 && lastLine != 0) {
					System.out.println("La première ligne ne peut pas être après la dernière ligne ...");
				}
			}
		}
		new ImportOrganismProspects(fileLocation, firstLine, lastLine);
	}

	private ImportOrganismProspects(String fileLocation, int firstLine, int lastLine) {

		if (this.init()) {
			try {
				logger.info("----- Début de l'import -----");

				// We check if file path is not null
				if (null != fileLocation) {
					logger.info("Fichier : " + fileLocation);

					File csvFile = new File(fileLocation).getAbsoluteFile();

					// check the csv file
					if (this.verifyFile(csvFile).equals(Boolean.FALSE)) {
						this.checkFileHeaders(csvFile);
					} else {
						logger.error(("Erreur lors de la vérification du fichier."));
					}

					this.mappedCsvData = CsvHelper.getMappedData(csvFile);
					this.checkFileContent(this.numberOfLinesToImport, this.firstLine);

					// If there are no errors in the file we retrieve the list of organisms
					if (this.getImportHasErrors().equals(Boolean.FALSE)) {
						logger.info("La vérification du fichier s'est terminée avec succès.");
						List<Organism> organismsInImport = this.convertImportToListOfOrganisms(numberOfLinesToImport,
								firstLine);

						// Create organisms from the list
						logger.info("--- Début de la création des organismes en BDD ---");
						for (Organism organism : organismsInImport) {
							logger.info("L'organisme suivant va être créé : " + organism.getLabel());
							this.organismService.create(organism);
							logger.info("Organisme créé avec succès : " + organism.getLabel());

							if (organism.getId() != null) {
								SynchronizationQueue synchronizationQueue = new SynchronizationQueue();
								synchronizationQueue.setResourceId(organism.getId());
								synchronizationQueue.setResourceType(ResourceType.ORGANISM);
								synchronizationQueue.setSendingApplication("Socle GU");
								synchronizationQueue.setSynchronizationJobType(SynchronizationJobType.CREATE);
								this.synchronizationQueueService.create(synchronizationQueue);
								logger.info("Organisme ajouté dans la queue de synchro avec l'id " + organism.getId());
							} else {
								logger.error("Organism " + organism.getLabel() + " has no id !");
							}

							// this.synchronizationService.notifyCreation(organism);
							// Wait to avoid problems when propagating synchronization messages
							// Thread.sleep(2000);
						}
						logger.info("----- Fin du script d'import -----");
					} else {
						logger.error("Prospects from " + firstLine + " to " + firstLine + numberOfLinesToImport
								+ " are not successfully imported");
					}
				} else {
					logger.info("Le script nécessite le chemin du fichier à importer pour fonctionner.");
				}
			} catch (IllegalArgumentException e) {
				logger.debug(e.getMessage());

			} catch (IOException e) {
				logger.error("An error has occured during writing the file on filer : " + e.getMessage());
			} catch (Exception e) {
				logger.error(
						"An unexpected error has occured during the validation of the file to upload" + e.getMessage());
			}
		}

	}

	private Boolean verifyFile(File file) throws Exception {

		logger.info("--- Début de la vérification du fichier ---");
		String mimeType = mimeTypesMap.getContentType(file);
		if (!CsvHelper.getAuthorizedMimeType().contains(mimeType)) {
			logger.warn("Wrong MIME type detected: " + mimeType);
			logger.error("Seuls les fichier CSV peuvent être importés");
			return Boolean.TRUE;
		}

		// Check file extension
		String fileName = file.getName();
		String fileExtension = fileName.substring(fileName.lastIndexOf("."), fileName.length());
		if (!CsvHelper.isCsvExtension(fileExtension)) {
			logger.error("Le fichier doit comporter l'extension .csv");
			return Boolean.TRUE;
		}

		// Retrieve number of lines
		int nbLines = CsvHelper.countLines(file);

		// Check that file contains more than 1 line
		if (nbLines < MIN_NB_LINES) {
			logger.error("ERREUR : le fichier contient un nombre de ligne < 2");
			return Boolean.TRUE;
		}

		if (this.firstLine == 0 && this.lastLine == 0) {
			this.numberOfLinesToImport = nbLines;
		} else {
			this.numberOfLinesToImport = (this.lastLine - this.firstLine - 2);
		}
		logger.info("--- Fichier vérifié avec succès ! ---");
		return Boolean.FALSE;
	}

	private void checkFileHeaders(File csvFile) throws Exception {

		// initialize the header array
		this.generateModelHeader();

		// get the headers from csv file
		String[] headers = CsvHelper.getHeaders(csvFile);

		// check that the numbers of headers are equal
		if (headers.length != HEADER_LENGTH) {
			throw new IllegalArgumentException("Le fichier ne respecte pas le modèle imposé (nombre de colonnes)");
		}

		// check that the header names are the same
		for (int i = 0; i < headers.length; i++) {

			if (!headers[i].trim().equalsIgnoreCase(this.fileHeaders[i])) {
				throw new IllegalArgumentException("Le fichier ne respecte pas le modèle imposé (nombre de colonnes)");
			}
		}
		logger.info("Vérification des headers du fichier OK");
	}

	/**
	 * Check datas contained into the file
	 */
	private void checkFileContent(Integer numberOfLinesToImport, Integer firstLine) throws Exception {
		int currentLine = firstLine;
		this.mapFieldErrors = new HashMap<Long, List<String>>();
		this.generateFieldRequirement();
		this.generateFieldRegex();

		Map<String, String> mapLine;

		// we are going to check line after line
		for (int lineNumber = firstLine; lineNumber < firstLine + numberOfLinesToImport - 2; lineNumber++) {

			mapLine = this.mappedCsvData.get(lineNumber);

			// Increment line number
			currentLine++;

			// List of errors contained into the current line
			List<String> lineFieldErrors = new ArrayList<String>();

			for (int i = 0; i < mapLine.size(); i++) {
				// We retrieve the key
				String mapKey = UnaccentLetter.unaccentString(this.fileHeaders[i]);

				// We retrieve the field value corresponding at the key
				String data = mapLine.get(mapKey.toLowerCase());

				// remove all the whitespaces
				if (this.fileHeaders[i].equals("Téléphonefixe") || this.fileHeaders[i].equals("Fax")
						|| this.fileHeaders[i].equals("Courriel") || this.fileHeaders[i].equals("CodePostal")
						|| this.fileHeaders[i].equals("Article")) {
					data = data.replaceAll("\\s+", "");
				}

				// If the field is empty we must check if the field is
				// required
				if (data.isEmpty() || (data.trim().length() == 0)) {
					if (this.isFieldRequired(i)) {
						lineFieldErrors.add("Le champ " + fileHeaders[i] + " ne doit pas être vide.");
					}
				} else {
					// If the field is not empty we must check its content
					// by
					// using regex defined into propertie file
					if (!this.isFieldMatchRegex(i, data)) {
						lineFieldErrors
								.add(this.fileHeaders[i] + " (" + data + ") ne correspond pas à la regex associée !");
					}

					// check siren not already exists & is valid
					if (mapKey.equals("siren")) {
						if (!this.organismService.isSirenValid(data)) {
							lineFieldErrors.add("Ce numéro de siren est invalide" + data);
						} else {
							if (null != this.organismService.findBySiren(data)) {
								lineFieldErrors.add("Ce numéro de siren existe " + data);
							}
						}
					}
				}
			}

			// We put the list of errors corresponding at this line into
			// the
			// map
			if (lineFieldErrors.size() > 0) {
				this.mapFieldErrors.put((long) currentLine, lineFieldErrors);
				logger.error("Error at line " + currentLine + ": " + lineFieldErrors);
			}
		}
	}

	/**
	 * @param index
	 * @return true is the field to import is required
	 */
	private Boolean isFieldRequired(int index) {
		Assert.notNull(index, "mapKey can't be null");

		if (this.fieldRequirement[index] == 1) {
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
	private Boolean isFieldMatchRegex(int columnTitle, String fieldValue) {
		Assert.notNull(columnTitle, "columnTitle can't be null");
		Assert.notNull(fieldValue, "fieldValue can't be null");

		String regex = null;
		Matcher matcher = null;
		Pattern pattern = null;
		Boolean match = Boolean.FALSE;

		try {
			// We retrieve the regex corresponding at the field into the
			// propertie
			// file
			regex = this.fieldRegex[columnTitle];
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
			throw new IllegalArgumentException("Une erreur s'est produite pendant la vérification des champs");
		}
		return match;
	}

	/**
	 * @return true if file uploaded contains errors
	 */
	private Boolean getImportHasErrors() {
		if ((this.mapFieldErrors != null) && (this.mapFieldErrors.size() > 0)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * This method allows to convert the mapped file into a list of organism
	 *
	 *
	 * @return a list of organisms
	 */
	private List<Organism> convertImportToListOfOrganisms(Integer numberOfLinesToImport, Integer firstLine) {

		logger.info("Début de la conversion du fichier en liste d'organisme.");
		List<Organism> listOfOrganismInImport = new ArrayList<Organism>(numberOfLinesToImport);

		String statusKey = this.fileHeaders[0].toLowerCase();
		String articleKey = this.fileHeaders[1].toLowerCase();
		String organismNameKey = this.fileHeaders[2].toLowerCase();
		organismNameKey = stripAccents(organismNameKey);
		String sirenKey = this.fileHeaders[3].toLowerCase();
		String legalCategoryKey = this.fileHeaders[4].toLowerCase();
		String apeCodeKey = this.fileHeaders[5].toLowerCase();
		String collegeKey = this.fileHeaders[6].toLowerCase();
		collegeKey = stripAccents(collegeKey);
		String populationKey = this.fileHeaders[7].toLowerCase();
		String workForceKey = this.fileHeaders[8].toLowerCase();
		String budgetKey = this.fileHeaders[9].toLowerCase();
		String contributionAmountKey = this.fileHeaders[10].toLowerCase();
		String descriptionKey = this.fileHeaders[11].toLowerCase();
		String countryKey = this.fileHeaders[12].toLowerCase();
		String adressKey = this.fileHeaders[13].toLowerCase();
		String postalCodeKey = this.fileHeaders[14].toLowerCase();
		String cityKey = this.fileHeaders[15].toLowerCase();
		String mailKey = this.fileHeaders[16].toLowerCase();
		String phoneKey = this.fileHeaders[17].toLowerCase();
		phoneKey = stripAccents(phoneKey);
		String faxKey = this.fileHeaders[18].toLowerCase();
		String websiteKey = this.fileHeaders[19].toLowerCase();

		Map<String, String> mapLine;
		for (int lineNumber = firstLine; lineNumber < firstLine + numberOfLinesToImport - 1; lineNumber++) {
			// for (Map<String, String> mapLine : this.mappedCsvData) {
			mapLine = this.mappedCsvData.get(lineNumber);

			logger.info("Traitement de l'organisme " + mapLine.get(organismNameKey));

			if (organismService.findBySiren(mapLine.get(sirenKey)) != null) {
				logger.info("L'organisme " + mapLine.get(organismNameKey) + " a déjà été créé !");
				continue;
			}
			Organism organism = new Organism();

			String statut = mapLine.get(statusKey);

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
			// legalCategory = legalCategory.replaceAll("\\.", "");
			if (!legalCategory.isEmpty()) {
				// break when matching value to avoid looping to every elements

				legalCategoryLoop: for (OrganismINSEECat currentlegalCategory : OrganismINSEECat.values()) {
					// logger.info("Comparing legalcategory : " + legalCategory + " with " +
					// currentlegalCategory.getId());
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

			// logger.debug("Organism Legal Cat : " + organism.getLegalCategory());

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

			if (organism.getCustomer() == null) {
				Customer customer = this.customerService.findByName("EB");
				organism.setCustomer(customer);
			}

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

			organism.getAddress().setCountry(Country.FR);

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

			logger.info("Ajout de l'organisme suivant dans la liste à importer : " + organism.getName());

			// ADD ORGANISM INTO LIST
			listOfOrganismInImport.add(organism);

		}

		logger.info("Fin de la conversion du fichier en liste d'organisme");
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

	/* TODO : better way to manage this ? */
	private void generateModelHeader() {
		this.fileHeaders[0] = "Statut";
		this.fileHeaders[1] = "Article";
		this.fileHeaders[2] = "Dénomination";
		this.fileHeaders[3] = "SIREN";
		this.fileHeaders[4] = "Naturejuridique";
		this.fileHeaders[5] = "CodeNAF";
		this.fileHeaders[6] = "Collège";
		this.fileHeaders[7] = "PopulationOfficielle";
		this.fileHeaders[8] = "Effectif";
		this.fileHeaders[9] = "Budget";
		this.fileHeaders[10] = "Montantdecotisation";
		this.fileHeaders[11] = "Description";
		this.fileHeaders[12] = "Pays";
		this.fileHeaders[13] = "Adresse";
		this.fileHeaders[14] = "CodePostal";
		this.fileHeaders[15] = "Ville";
		this.fileHeaders[16] = "Courriel";
		this.fileHeaders[17] = "Téléphonefixe";
		this.fileHeaders[18] = "Fax";
		this.fileHeaders[19] = "SiteInternet";
	}

	/* TODO : better way to manage this ? */
	private void generateFieldRequirement() {
		this.fieldRequirement[0] = 1;
		this.fieldRequirement[1] = 0;
		this.fieldRequirement[2] = 1;
		this.fieldRequirement[3] = 1;
		this.fieldRequirement[4] = 0;
		this.fieldRequirement[5] = 0;
		this.fieldRequirement[6] = 1;
		this.fieldRequirement[7] = 0;
		this.fieldRequirement[8] = 0;
		this.fieldRequirement[9] = 0;
		this.fieldRequirement[10] = 0;
		this.fieldRequirement[11] = 0;
		this.fieldRequirement[12] = 1;
		this.fieldRequirement[13] = 1;
		this.fieldRequirement[14] = 1;
		this.fieldRequirement[15] = 1;
		this.fieldRequirement[16] = 1;
		this.fieldRequirement[17] = 0;
		this.fieldRequirement[18] = 0;
		this.fieldRequirement[19] = 0;
	}

	/* TODO : better way to manage this ? */
	private void generateFieldRegex() {
		this.fieldRegex[0] = "^.{1,255}";
		this.fieldRegex[1] = "Le|La|L'|";
		this.fieldRegex[2] = "^.{1,255}";
		this.fieldRegex[3] = "^[0-9]{9}$";
		this.fieldRegex[4] = "^.{1,255}";
		this.fieldRegex[5] = "^[0-9]{4}[A-Z]{1}$";
		this.fieldRegex[6] = "^[0-9]{1,2}$";
		this.fieldRegex[7] = "^[0-9]{1,9}$";
		this.fieldRegex[8] = "^[0-9]{1,9}$";
		this.fieldRegex[9] = "^[0-9]{1,9}$";
		this.fieldRegex[10] = "^[0-9]{1,9}$";
		this.fieldRegex[11] = "^.{1,255}";
		this.fieldRegex[12] = "^.{1,255}";
		this.fieldRegex[13] = "^.{1,255}";
		this.fieldRegex[14] = "^[0-9]{5}$";
		this.fieldRegex[15] = "^.{1,255}";
		this.fieldRegex[16] = "^[^.][a-zA-Z0-9._%+-]+@[a-zA-Z0-9-.]+\\.[a-zA-Z.]{2,5}$";
		this.fieldRegex[17] = "^[0-9]{10}$";
		this.fieldRegex[18] = "^[0-9]{10}$";
		this.fieldRegex[19] = "^.{1,255}";
	}

	private static String stripAccents(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		return s;
	}

	/**
	 * Initializes class
	 *
	 * @return true if initialization was successful, false otherwise
	 */
	public boolean init() {
		boolean result = true;
		try {
			logger.info("Chargement des services");
			ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
					new String[] { "classpath:coreMyec3Context.xml", "classpath*:coreMyec3Context.xml" });
			organismService = (OrganismService) applicationContext.getBean("organismService");
			customerService = (CustomerService) applicationContext.getBean("customerService");
			synchronizationQueueService = (SynchronizationQueueService) applicationContext
					.getBean("synchronizationQueueService");

		} catch (Exception e) {
			logger.error("An error occured when initializing business services");
			e.printStackTrace();
			result = false;
		}
		return result;
	}
}
