package org.myec3.socle.webapp.pages.organism.agent.export;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Address;
import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.domain.model.enums.Civility;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.service.AgentProfileService;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.core.tools.UnaccentLetter;
import org.myec3.socle.core.util.CsvHelper;
import org.myec3.socle.webapp.constants.GuWebAppConstants;
import org.myec3.socle.webapp.pages.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.inject.Named;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Page used to display the report of the file uploaded.<br />
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp
 * /pages/organism/agent/export/Report.tml
 * 
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class Report extends AbstractImport {

	private static final Logger logger = LoggerFactory.getLogger(Report.class);

	@Inject
	@Named("organismService")
	private OrganismService organismService;

	@Inject
	@Named("agentProfileService")
	private AgentProfileService agentProfileService;

	private Organism organism;

	@Persist
	private File uploadedFile;

	@Persist
	private Map<Long, List<String>> mapFieldErrors;

	@Persist
	private List<Map<String, String>> mappedCsvData;

	@Inject
	private Block errorBlock;

	@Inject
	private Block successBlock;

	@Persist
	private List<AgentProfile> agentsInSocle;

	@Persist
	private List<AgentProfile> agentsInImport;

	@Persist
	private List<AgentProfile> agentsToModify;

	@Persist
	private List<AgentProfile> agentsToCreate;

	@Persist
	private List<AgentProfile> agentsToDelete;

	@Property
	private Long errorLineLoop;

	@SuppressWarnings("unused")
	@Property
	private String errorLoop;

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate() {
		return Import.class;
	}

	@OnEvent(EventConstants.ACTIVATE)
	public Object onActivate(Long id) {
		this.organism = this.organismService.findOne(id);

		if ((this.organism == null) || (this.uploadedFile == null)) {
			return Import.class;
		}

		try {
			if (null == this.mappedCsvData) {
				this.mappedCsvData = CsvHelper.getMappedData(this.uploadedFile);

				// We check file content only the first time
				this.checkFileContent();
			}

			// If there are no errors in the file we retrieve the list of agents
			// from the database
			if (!this.getImportHasErrors() && (this.agentsInSocle == null)) {

				// Retrieve the list of agents in socle (for the current
				// organism only)
				this.agentsInSocle = this.agentProfileService.findAllAgentProfilesByOrganism(this.organism);

				// convert import to a list of agents
				this.agentsInImport = this.convertImportToListOfAgents();

				if ((this.agentsInImport != null) && (!this.agentsInImport.isEmpty())) {
					// Retrieve the list of agents in the socle and in the
					// imported
					// file
					this.agentsToModify = this.constructAgentsListToModify();

					// Retrieve the list of agents to create
					this.agentsToCreate = this.constructAgentsListToCreate();

					// Retrieve the list of agents to delete
					this.agentsToDelete = this.constructAgentsListToDelete();
				}

			}
		} catch (Exception ex) {
			logger.error("An exception has occured during onActivate : " + ex);
			this.clearPersistentParams();
			return Index.class;
		}
		// Check if loggedUser can access at this page
		return this.hasRights(this.organism);
	}

	@OnEvent(EventConstants.PASSIVATE)
	public Long onPassivate() {
		return (this.organism != null) ? this.organism.getId() : null;
	}

	/**
	 * @return the correct block to display on the page
	 */
	public Block getChooseBlock() {
		if (this.getImportHasErrors()) {
			return this.errorBlock;
		}
		return this.successBlock;
	}

	/**
	 * @return the last modified date of the uploaded file
	 */
	public String getFileDate() {
		if (this.uploadedFile != null && this.uploadedFile.exists()) {
			return new SimpleDateFormat(GuWebAppConstants.DEFAULT_DATE_FORMAT)
					.format(new Date(uploadedFile.lastModified()));
		}
		return this.getMessages().get("unknown-date");
	}

	/**
	 * @return the last modified time of the uploaded file
	 */
	public String getFileTime() {
		if (this.uploadedFile != null && this.uploadedFile.exists()) {
			return new SimpleDateFormat(GuWebAppConstants.DEFAULT_TIME_FORMAT)
					.format(new Date(uploadedFile.lastModified()));
		}
		return this.getMessages().get("unknown-time");
	}

	/**
	 * @return TRUE if all data are updated false otherwise.
	 */
	public Boolean getIsAllDataUpdated() {
		if ((this.agentsToModify.isEmpty()) && (this.agentsToCreate.isEmpty()) && (this.agentsToModify.isEmpty())) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * @return number of erros contained in file imported.
	 */
	public int getNbOfErrors() {
		int cpt = 0;
		if (this.mapFieldErrors != null) {
			for (Entry<Long, List<String>> entry : this.mapFieldErrors.entrySet()) {
				cpt += entry.getValue().size();
			}
		}
		return cpt;
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

	/**
	 * @return all errors of the current line (errorLineLoop)
	 */
	public List<String> getErrorsLine() {
		List<String> listOfErrors = new ArrayList<String>();
		List<String> errors = this.mapFieldErrors.get(this.errorLineLoop);
		for (int i = 0; i < errors.size(); i++) {
			listOfErrors.add(errors.get(i));
		}
		return listOfErrors;
	}

	/**
	 * Check datas contained into the file
	 */
	public void checkFileContent() throws Exception {
		int currentLine = 0;
		this.mapFieldErrors = new HashMap<Long, List<String>>();

		for (Map<String, String> mapLine : this.mappedCsvData) {
			// Increment line number
			currentLine++;

			// List of errors contained into the current line
			List<String> lineFieldErrors = new ArrayList<String>();

			for (int i = 0; i < mapLine.size(); i++) {
				// We retrieve the key
				String mapKey = UnaccentLetter.unaccentString(this.getMessages().get("user-import-header-column-" + i));

				// We retrieve the field value corresponding at the key
				String data = mapLine.get(mapKey.toLowerCase());

				// If the field is empty we must check if the field is
				// required
				if (data.isEmpty()) {
					if (this.isFieldRequired(mapKey)) {
						lineFieldErrors.add(this.getMessages().format("user-import-column-required-message", mapKey));
					}
				} else {
					// If the field is not empty we must check its content
					// by
					// using regex defined into propertie file
					if (!this.isFieldMatchRegex(mapKey, data)) {
						lineFieldErrors.add(this.getMessages().format("user-import-column-regex-message-" + mapKey));
					}
				}
			}

			// We put the list of errors corresponding at this line into
			// the
			// map
			if (lineFieldErrors.size() > 0) {
				this.mapFieldErrors.put(new Long(currentLine), lineFieldErrors);
			}
		}
	}

	/**
	 * @return the list of agents in the socle and in the imported file
	 */
	public List<AgentProfile> constructAgentsListToModify() {
		List<String> listOfEmails = new ArrayList<String>(this.agentsInImport.size());
		List<AgentProfile> listOfAgentsInDatabase = new ArrayList<AgentProfile>();
		List<AgentProfile> listOfAgentsToModify = new ArrayList<AgentProfile>();
		Map<String, AgentProfile> mapAgentsInImport = new HashMap<String, AgentProfile>();

		// Retrieve all Id from the map
		for (AgentProfile agent : this.agentsInImport) {
			listOfEmails.add(agent.getEmail());
			mapAgentsInImport.put(agent.getEmail(), agent);
		}

		// Retrieve all agentProfiles with this ID from the database
		listOfAgentsInDatabase = this.agentProfileService.findAllByListOfEmailsAndOrganism(listOfEmails, this.organism);

		for (AgentProfile agentInDb : listOfAgentsInDatabase) {
			if (mapAgentsInImport.containsKey(agentInDb.getEmail())) {

				AgentProfile agentInImport = mapAgentsInImport.get(agentInDb.getEmail());

				// Set new values

				// SET CIVILITY
				agentInDb.getUser().setCivility(agentInImport.getUser().getCivility());
				// SET FIRSTNAME
				agentInDb.getUser().setFirstname(agentInImport.getUser().getFirstname());
				// SET NAME
				agentInDb.getUser().setLastname(agentInImport.getUser().getLastname());
				// SET ELECTED
				agentInDb.setElected(agentInImport.getElected());
				// SET FUNCTION
				agentInDb.setFunction(agentInImport.getFunction());

				// SET ADDRESS
				this.setAddressOfAgentToModify(agentInDb, agentInImport);

				// SET CELL PHONE
				agentInDb.setCellPhone(agentInImport.getCellPhone());
				// SET PHONE
				agentInDb.setPhone(agentInImport.getPhone());
				// SET FAX
				agentInDb.setFax(agentInImport.getFax());

				listOfAgentsToModify.add(agentInDb);
			}
		}

		return listOfAgentsToModify;
	}

	/**
	 * Set address of agentToModify. If address is empty we use the adress of the
	 * organism.
	 * 
	 * @param agentToModify : the agentProfile to update
	 * @param agentInImport : the agentProfile contained in the file imported.
	 */
	public void setAddressOfAgentToModify(AgentProfile agentToModify, AgentProfile agentInImport) {

		String postalAddress = agentInImport.getAddress().getPostalAddress();
		String postalCode = agentInImport.getAddress().getPostalCode();
		String city = agentInImport.getAddress().getCity();
		Country country = agentInImport.getAddress().getCountry();

		// SET ADRESSE
		if ((postalAddress != null) && !(postalAddress.isEmpty())) {
			agentToModify.getAddress().setPostalAddress(postalAddress);
		} else {
			// retrieve postal address of the organism
			agentToModify.getAddress().setPostalAddress(this.organism.getAddress().getPostalAddress());
		}

		// SET POSTAL CODE
		if ((postalCode != null) && !(postalCode.isEmpty())) {
			agentToModify.getAddress().setPostalCode(postalCode);
		} else {
			agentToModify.getAddress().setPostalCode(this.organism.getAddress().getPostalCode());
		}

		// SET CITY
		if ((city != null) && !(city.isEmpty())) {
			agentToModify.getAddress().setCity(city);
		} else {
			agentToModify.getAddress().setCity(this.organism.getAddress().getCity());
		}

		// SET COUNTRY
		agentToModify.getAddress().setCountry(country);
	}

	/**
	 * @return the list of agents in the imported file but not in the socle database
	 */
	public List<AgentProfile> constructAgentsListToCreate() {
		List<AgentProfile> listOfAgentsToCreate = new ArrayList<AgentProfile>();
		Map<String, AgentProfile> mapAgentsToModify = new HashMap<String, AgentProfile>();

		for (AgentProfile agentToModify : this.agentsToModify) {
			mapAgentsToModify.put(agentToModify.getEmail().toLowerCase(), agentToModify);
		}

		for (AgentProfile agent : this.agentsInImport) {
			if (!mapAgentsToModify.containsKey(agent.getEmail().toLowerCase())) {
				listOfAgentsToCreate.add(agent);
			}
		}

		return listOfAgentsToCreate;
	}

	/**
	 * @return the list of agents that there are in the socle but not in the file
	 *         imported for the current organism
	 */
	public List<AgentProfile> constructAgentsListToDelete() {
		List<AgentProfile> listOfAgentsToDelete = new ArrayList<AgentProfile>();

		for (AgentProfile agentSocle : this.agentsInSocle) {
			if (!(this.agentsToModify.contains(agentSocle)) && !(this.getLoggedProfile().equals(agentSocle))) {
				listOfAgentsToDelete.add(agentSocle);
			}
		}

		return listOfAgentsToDelete;
	}

	/**
	 * This method allows to convert the mapped file into a list of agent profiles
	 * 
	 * @return a list of agent profiles
	 */
	public List<AgentProfile> convertImportToListOfAgents() {
		List<AgentProfile> listOfAgentProfileInImport = new ArrayList<>(this.mappedCsvData.size());

		for (Map<String, String> mapLine : this.mappedCsvData) {
			AgentProfile agent = new AgentProfile();
			Address address = new Address();
			User userAgent = new User();

			/**
			 * SET ADDRESS
			 */
			// SET ADDRESS
			address.setPostalAddress(mapLine.get(this.getMessages().get("user-import-column-adresse")));
			// SET POSTAL CODE
			address.setPostalCode(mapLine.get(this.getMessages().get("user-import-column-codepostal")));
			// SET CITY
			address.setCity(mapLine.get(this.getMessages().get("user-import-column-ville")));
			// SET COUNTRY
			address.setCountry(Country.FR);

			/**
			 * SET USER
			 */
			// SET CIVILITY
			String civility = mapLine.get("civilite");
			if ((civility != null) && (!civility.isEmpty())
					&& (!civility.equalsIgnoreCase(this.getMessages().get("NA-label")))) {
				userAgent.setCivility(Civility.valueOf(mapLine.get("civilite")));
			}

			// SET FIRSTNAME
			userAgent.setFirstname(mapLine.get("prenom"));
			// SET LASTNAME
			userAgent.setLastname(mapLine.get(this.getMessages().get("user-import-column-nom")));
			// SET USERNAME
			userAgent.setUsername(mapLine.get(this.getMessages().get("user-import-column-courriel")));

			/**
			 * SET AGENT
			 */

			// SET FUNCTION
			agent.setFunction(mapLine.get(this.getMessages().get("user-import-column-fonction")));
			// SET ELECTED
			if (mapLine.get(this.getMessages().get("user-import-column-elu"))
					.equalsIgnoreCase(this.getMessages().get("true-label"))) {
				agent.setElected(Boolean.TRUE);
			} else {
				agent.setElected(Boolean.FALSE);
			}
			// SET EMAIL
			agent.setEmail(userAgent.getUsername());
			// SET CELLPHONE
			agent.setCellPhone(mapLine.get("telephoneportable"));
			// SET PHONE
			agent.setPhone(mapLine.get("telephonefixe"));
			// SET FAX
			agent.setFax(mapLine.get(this.getMessages().get("user-import-column-fax")));

			// SET USER TO AGENT
			agent.setUser(userAgent);
			// SET ADDRESS TO AGENT
			agent.setAddress(address);
			agent.setCreatedUserId(this.getUserIdLogged());

			// ADD AGENTPROFILE INTO LIST
			listOfAgentProfileInImport.add(agent);
		}

		return listOfAgentProfileInImport;

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

		logger.debug("Checking field of column " + columnTitle + " with value = " + fieldValue);

		String regex = null;
		Matcher matcher = null;
		Pattern pattern = null;
		Boolean match = Boolean.FALSE;

		try {
			// We retrieve the regex corresponding at the field into the
			// propertie
			// file
			regex = this.getMessages().get("user-import-column-regex-" + columnTitle);
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
	 * @param mapKey
	 * @return true is the field to import is required
	 */
	public Boolean isFieldRequired(String mapKey) {
		Assert.notNull(mapKey, "mapKey can't be null");

		if (Integer.valueOf(this.getMessages().get("user-import-column-required-" + mapKey)) == 1) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/***********************/
	/** GETTER AND SETTER **/
	/***********************/
	public Organism getOrganism() {
		return organism;
	}

	public void setOrganism(Organism organism) {
		this.organism = organism;
	}

	public File getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(File uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	/**
	 * @return number of agents in file imported
	 */
	public int getNumberOfAgentsInImport() {
		if (this.agentsInImport != null) {
			return this.agentsInImport.size();
		}
		return new Integer(0);
	}

	/**
	 * @return number of agents in socle for the current organism
	 */
	public int getNumberOfAgentsInSocle() {
		if (this.agentsInImport != null) {
			return this.agentsInImport.size();
		}
		return new Integer(0);
	}

	/**
	 * @return number of agents to create
	 */
	public int getNumberOfAgentsToCreate() {
		if (this.agentsToCreate != null) {
			return this.agentsToCreate.size();
		}
		return new Integer(0);
	}

	/**
	 * @return number of agents to modify
	 */
	public int getNumberOfAgentsToModify() {
		if (this.agentsToModify != null) {
			return this.agentsToModify.size();
		}
		return new Integer(0);
	}

	/**
	 * @return number of agents to create
	 */
	public int getNumberOfAgentsToDelete() {
		if (this.agentsToDelete != null) {
			return this.agentsToDelete.size();
		}
		return new Integer(0);
	}

	/**
	 * @return TRUE if the application must display the CREATE menu on the page
	 */
	public Boolean getDisplayCreateMenu() {
		if (this.getImportHasErrors() || this.agentsToCreate == null || this.agentsToCreate.isEmpty()) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	/**
	 * @return TRUE if the application must display the MODIFY menu on the page
	 */
	public Boolean getDisplayModifyMenu() {
		if (this.getImportHasErrors() || this.agentsToModify == null || this.agentsToModify.isEmpty()) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	/**
	 * @return TRUE if the application must display the DELETE menu on the page
	 */
	public Boolean getDisplayDeleteMenu() {
		if (this.getImportHasErrors() || this.agentsToDelete == null || this.agentsToDelete.isEmpty()) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	/**
	 * @return the list of agents to create
	 */
	public List<AgentProfile> getAgentsToCreate() {
		return agentsToCreate;
	}

	public void setAgentsToCreate(List<AgentProfile> agentsToCreate) {
		this.agentsToCreate = agentsToCreate;
	}

	/**
	 * @return the list of agents to update
	 */
	public List<AgentProfile> getAgentsToModify() {
		return agentsToModify;
	}

	public void setAgentsToModify(List<AgentProfile> agentsToModify) {
		this.agentsToModify = agentsToModify;
	}

	/**
	 * @return the list of agents to delete
	 */
	public List<AgentProfile> getAgentsToDelete() {
		return agentsToDelete;
	}

	public void setAgentsToDelete(List<AgentProfile> agentsToDelete) {
		this.agentsToDelete = agentsToDelete;
	}
}
