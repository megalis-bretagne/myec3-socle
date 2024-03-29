package org.myec3.socle.core.service.impl;

import au.com.bytecode.opencsv.CSVWriter;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("exportAgentCSVGeneratorService")
public class ExportAgentCSVGeneratorServiceImpl implements ExportAgentCSVGeneratorService {

	private static final Logger logger = LoggerFactory.getLogger(ExportAgentCSVGeneratorServiceImpl.class);

	@Autowired
	@Qualifier("agentProfileService")
	private AgentProfileService agentProfileService;


	@Autowired
	@Qualifier("profileService")
	private ProfileService profileService;


	@Autowired
	@Qualifier("userService")
	private UserService userService;

	@Autowired
	@Qualifier("roleService")
	private RoleService roleService;

	@Override
	public void writeCsv(List<Organism> organismsList, List<Application> applicationsList, CSVWriter csvWriter) throws IOException {
		String[] header = writeHeader(applicationsList, csvWriter);
		for (Organism organism : organismsList) {
			writeAllUserOfOrganismInfo(organism, csvWriter, header);
		}
		csvWriter.close();
	}

	private String[] writeHeader(List<Application> applicationsList, CSVWriter csvWriter) {
		String[] header = generateHeader(applicationsList);
		csvWriter.writeNext(header);
		return header;
	}

	private void writeAllUserOfOrganismInfo(Organism organism, CSVWriter writer, String[] header) {

		List<AgentProfile> agentProfileList = agentProfileService.findAllAgentProfilesByOrganism(organism);

		for (AgentProfile ap : agentProfileList) {
			try {

				writer.writeNext(retrieveUserInfo(ap, header, organism));

			} catch (NoResultException nre) {
				logger.error("NoResult Exception has occured : ", nre);
			} catch (NullPointerException npe) {
				logger.error("NullPointer Exception has occured : ", npe);
			}
		}

	}

	private String[] retrieveUserInfo(AgentProfile ap, String[] header, Organism organism) {
		HashMap<String, String> csvDataMap = new HashMap<String, String>();

		// Retrival of information from database
		Profile profile = profileService.findOne(ap.getId());

		User user = userService.findOne(profile.getUser().getId());

		ConnectionInfos connectionInfos = null;

		// FIXME: these data are outdated since the connection is handled by Keycloak.
		if (user.getConnectionInfos() != null && user.getConnectionInfos().getId() != null) {
			connectionInfos = user.getConnectionInfos();
		}

		List<Role> roleList = roleService.findAllRoleByProfile(profile);

		// set information to the hashmap
		setAgentProfilData(csvDataMap, ap);
		setRoleData(csvDataMap, roleList);
		setConnectionInfosData(csvDataMap, connectionInfos);
		setUserData(csvDataMap, user);
		setProfileData(csvDataMap, profile);
		setOrganismData(csvDataMap, organism);

		// generation and writting of the line corresponding to this
		// user
		return generateDataString(csvDataMap, header);

	}

	/**
	 * add the String value of the element or "" if the element is null
	 *
	 * @param csvDataMap
	 * @param key
	 * @param element    the element
	 */
	private <T> void putElement(HashMap<String, String> csvDataMap, String key, T element) {
		if (element == null) {
			csvDataMap.put(key, "");
		} else {
			csvDataMap.put(key, "" + element);
		}
	}

	/**
	 * fill information retrieved from ConnectionInfos in the hashmap
	 *
	 * @param csvDataMap
	 * @param connectionInfos
	 */
	private void setConnectionInfosData(HashMap<String, String> csvDataMap, ConnectionInfos connectionInfos) {
		if (connectionInfos != null) {
			putElement(csvDataMap, "user_last_connection_date", connectionInfos.getLastConnectionDate());
			putElement(csvDataMap, "user_mean_time_betweenTwoConnections",
					connectionInfos.getMeanTimeBetweenTwoConnections());
			putElement(csvDataMap, "user_nbConnections", connectionInfos.getNbConnections());
		} else {
			csvDataMap.put("user_last_connection_date", "");
			csvDataMap.put("user_mean_time_betweenTwoConnections", "");
			csvDataMap.put("user_nbConnections", "");
		}

	}

	/**
	 * fill information retrieved from AgentProfile in the hashmap
	 *
	 * @param csvDataMap
	 * @param ap
	 */
	private void setAgentProfilData(HashMap<String, String> csvDataMap, AgentProfile ap) {
		if (ap != null) {
			putElement(csvDataMap, "agent_profil_creation_date", ap.getCreatedDate());
			putElement(csvDataMap, "agent_profil_elected", ap.getElected());
			putElement(csvDataMap, "agent_profil_executive", ap.getExecutive());
			putElement(csvDataMap, "agent_profil_representative", ap.getRepresentative());
			putElement(csvDataMap, "agent_profil_substitute", ap.getSubstitute());
			putElement(csvDataMap, "organism_department_id", ap.getOrganismDepartment().getId());
			putElement(csvDataMap, "organism_department_label", ap.getOrganismDepartment().getLabel());
		} else {
			csvDataMap.put("agent_profil_creation_date", "");
			csvDataMap.put("agent_profil_elected", "");
			csvDataMap.put("agent_profil_executive", "");
			csvDataMap.put("agent_profil_representative", "");
			csvDataMap.put("agent_profil_substitute", "");
			csvDataMap.put("organism_department_id", "");
			csvDataMap.put("organism_department_label", "");
		}

	}

	/**
	 * fill information retrieved from User in the hashmap
	 *
	 * @param csvDataMap
	 * @param user
	 */
	private void setUserData(HashMap<String, String> csvDataMap, User user) {
		if (user != null) {
			putElement(csvDataMap, "user_id", user.getId());
			putElement(csvDataMap, "user_civility",
					user.getCivility() != null ? user.getCivility().getLabel() : "");
			putElement(csvDataMap, "user_firstname", user.getFirstname());
			putElement(csvDataMap, "user_lastname", user.getLastname());
			putElement(csvDataMap, "user_username", user.getUsername());
			putElement(csvDataMap, "user_connectionAttempts", user.getConnectionAttempts());
		} else {
			csvDataMap.put("user_id", "");
			csvDataMap.put("user_civility", "");
			csvDataMap.put("user_firstname", "");
			csvDataMap.put("user_lastname", "");
			csvDataMap.put("user_username", "");
			csvDataMap.put("user_connectionAttempts", "");
		}

	}

	private void setOrganismData(HashMap<String, String> csvDataMap, Organism organism) {
		putElement(csvDataMap, "organism_id", organism.getId());
		putElement(csvDataMap, "organism_label", organism.getLabel());
		putElement(csvDataMap, "organism_siren", organism.getSiren());
		putElement(csvDataMap, "organism_nic", organism.getNic());
	}

	/**
	 * fill information retrieved from Profile in the hashmap
	 *
	 * @param csvDataMap
	 * @param profile
	 */
	private void setProfileData(HashMap<String, String> csvDataMap, Profile profile) {
		if (profile != null) {
			putElement(csvDataMap, "profil_id", profile.getId());
			putElement(csvDataMap, "profil_externalId", profile.getExternalId());
			putElement(csvDataMap, "profil_technicalIdentifier", profile.getTechnicalIdentifier());
			putElement(csvDataMap, "profil_canton", profile.getAddress().getCanton());
			putElement(csvDataMap, "profil_city", profile.getAddress().getCity());
			putElement(csvDataMap, "profil_country", profile.getAddress().getCountry());
			putElement(csvDataMap, "profil_postalAddress", profile.getAddress().getPostalAddress());
			putElement(csvDataMap, "profil_postalCode", profile.getAddress().getPostalCode());
			putElement(csvDataMap, "profil_cellPhone", profile.getCellPhone());
			putElement(csvDataMap, "profil_email", profile.getEmail());
			putElement(csvDataMap, "profil_enabled", profile.getEnabled());
			putElement(csvDataMap, "profil_fax", profile.getFax());
			putElement(csvDataMap, "profil_function", profile.getFunction());
			putElement(csvDataMap, "profil_grade", profile.getGrade());
			putElement(csvDataMap, "profil_phone", profile.getPhone());
		} else {
			csvDataMap.put("profil_id", "");
			csvDataMap.put("profil_externalId", "");
			csvDataMap.put("profil_canton", "");
			csvDataMap.put("profil_city", "");
			csvDataMap.put("profil_country", "");
			csvDataMap.put("profil_postalAddress", "");
			csvDataMap.put("profil_postalCode", "");
			csvDataMap.put("profil_cellPhone", "");
			csvDataMap.put("profil_email", "");
			csvDataMap.put("profil_enabled", "");
			csvDataMap.put("profil_fax", "");
			csvDataMap.put("profil_function", "");
			csvDataMap.put("profil_grade", "");
			csvDataMap.put("profil_phone", "");
		}

	}

	/**
	 * Extract the value of the HashMap to generate the String array needed for the
	 * CsvWritter If an element has no value we display empty value
	 *
	 * @param csvDataMap
	 * @return
	 */
	private String[] generateDataString(HashMap<String, String> csvDataMap, String[] header) {
		String[] data = new String[header.length];

		for (int i = 0; i < header.length; i++) {
			if (csvDataMap.get(header[i]) != null) {
				data[i] = csvDataMap.get(header[i]);
			} else {
				data[i] = "";
			}

		}

		return data;
	}

	private String[] generateHeader(List<Application> applications) {
		ArrayList<String> header = new ArrayList<String>();

		// add of each element of the header
		header.add("user_id");
		header.add("user_last_connection_date");
		header.add("user_mean_time_betweenTwoConnections");
		header.add("user_nbConnections");
		header.add("user_civility");
		header.add("user_firstname");
		header.add("user_lastname");
		header.add("user_username");
		header.add("user_connectionAttempts");
		header.add("profil_id");
		header.add("profil_externalId");
		header.add("profil_technicalIdentifier");
		header.add("profil_canton");
		header.add("profil_city");
		header.add("profil_country");
		header.add("profil_postalAddress");
		header.add("profil_postalCode");
		header.add("profil_cellPhone");
		header.add("profil_email");
		header.add("profil_enabled");
		header.add("profil_fax");
		header.add("profil_function");
		header.add("profil_grade");
		header.add("profil_phone");
		header.add("agent_profil_creation_date");
		header.add("agent_profil_elected");
		header.add("agent_profil_executive");
		header.add("agent_profil_representative");
		header.add("agent_profil_substitute");

		// application role is dynamic
		for (Application app : applications) {
			header.add("agent_profil_role_" + app.getLabel());
		}

		header.add("organism_department_id");
		header.add("organism_department_label");
		header.add("organism_id");
		header.add("organism_label");
		header.add("organism_siren");
		header.add("organism_nic");


		return header.toArray(new String[0]);
	}

	/**
	 * fill information about roles of applications applications are keys of the
	 * hashmap and for each the value is all the role for the application
	 *
	 * @param csvDataMap
	 * @param roleList
	 */
	private void setRoleData(HashMap<String, String> csvDataMap, List<Role> roleList) {

		for (Role tmpRole : roleList) {

			if (csvDataMap.containsKey("agent_profil_role_" + tmpRole.getApplication().getLabel())) {
				csvDataMap.put("agent_profil_role_" + tmpRole.getApplication().getLabel(),
						csvDataMap.get("agent_profil_role_" + tmpRole.getApplication().getLabel()) + "/"
								+ tmpRole.getLabel());
			} else {
				csvDataMap.put("agent_profil_role_" + tmpRole.getApplication().getLabel(), tmpRole.getLabel());
			}
		}

	}
}