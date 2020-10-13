package org.myec3.socle.synchro.core.service.impl;

import au.com.bytecode.opencsv.CSVWriter;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.EtatExport;
import org.myec3.socle.core.service.*;
import org.myec3.socle.synchro.core.service.ExportAgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service("exportAgentService")
public class ExportAgentServiceImpl implements ExportAgentService {

    private static final char SEPARATOR = ';';

    private static Logger logger = LoggerFactory.getLogger(ExportAgentServiceImpl.class);

    @Autowired
    @Qualifier("organismService")
    private OrganismService organismService;

    @Autowired
    @Qualifier("applicationService")
    private ApplicationService applicationService;


    @Autowired
    @Qualifier("organismDepartmentService")
    private OrganismDepartmentService organismDepartmentService;


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
    @Qualifier("connectionInfosService")
    private ConnectionInfosService connectionInfosService;

    @Autowired
    @Qualifier("roleService")
    private RoleService roleService;


    @Autowired
    @Qualifier("exportCSVService")
    private ExportCSVService exportCSVService;


    @Override
    public void exportAgent() {

        //On sélectionne toutes les demande d'export
        List<ExportCSV> exportCSVList =  exportCSVService.findExportCSVByEtat(EtatExport.AF);

        if ( exportCSVList !=null && exportCSVList.size()> 0){
            logger.info("Il y a un export d'agent à faire");

            try {
                StringWriter sw = new StringWriter();
                CSVWriter writer = new CSVWriter(sw, SEPARATOR);

                List<Application> applicationsList = applicationService.findAll();

                // Write header
                String[] header = generateHeader(applicationsList);
                writer.writeNext(header);

                List<Organism> organismsList = organismService.findAll();

                for (Organism organism : organismsList) {
                    writeAllUserOfOrganismInfo(organism, writer, header);
                }
                writer.close();
                Date dateExport =new Date(System.currentTimeMillis());
                boolean first= true;


                for(ExportCSV exportCSV : exportCSVList) {
                    if (first){
                        exportCSV.setEtat(EtatExport.OK);
                        exportCSV.setDateExport(dateExport);
                        exportCSV.setContent(sw.toString());
                        exportCSVService.update(exportCSV);
                    }else{
                        exportCSV.setEtat(EtatExport.AN);
                        exportCSV.setDateExport(dateExport);
                        exportCSVService.update(exportCSV);
                    }
                }

            } catch (IllegalArgumentException e) {
                logger.debug(e.getMessage());
                Date dateExport =new Date(System.currentTimeMillis());
                for(ExportCSV exportCSV : exportCSVList) {
                    exportCSV.setEtat(EtatExport.KO);
                    exportCSV.setDateExport(dateExport);
                    //exportCSV.setContent(sw.toString());
                    exportCSVService.update(exportCSV);
                }
            } catch (Exception e) {
                logger.error("An unexpected error has occured during the validation of the file to upload {} ",
                        e.getMessage());
                Date dateExport =new Date(System.currentTimeMillis());
                for(ExportCSV exportCSV : exportCSVList) {
                    exportCSV.setEtat(EtatExport.KO);
                    exportCSV.setDateExport(dateExport);
                    //exportCSV.setContent(sw.toString());
                    exportCSVService.update(exportCSV);
                }
            }
        }


    }

    public void writeAllUserOfOrganismInfo(Organism organism, CSVWriter writer, String[] header) {

        List<AgentProfile> agentProfileList = agentProfileService.findAllAgentProfilesByOrganism(organism);

        for (AgentProfile ap : agentProfileList) {
            try {

                writer.writeNext(retrieveUserInfo(ap, header, organism));

            } catch (javax.persistence.NoResultException nre) {
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
     *
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
     *
     * fill information retrieved from AgentProfile in the hashmap
     *
     * @param csvDataMap
     * @param ap
     */
    private void setAgentProfilData(HashMap<String, String> csvDataMap, AgentProfile ap) {
        if (ap != null) {
            putElement(csvDataMap, "agent_profil_elected", ap.getElected());
            putElement(csvDataMap, "agent_profil_executive", ap.getExecutive());
            putElement(csvDataMap, "agent_profil_representative", ap.getRepresentative());
            putElement(csvDataMap, "agent_profil_substitute", ap.getSubstitute());
            putElement(csvDataMap, "organism_department_id", ap.getOrganismDepartment().getId());
            putElement(csvDataMap, "organism_department_label", ap.getOrganismDepartment().getLabel());
        } else {
            csvDataMap.put("agent_profil_elected", "");
            csvDataMap.put("agent_profil_executive", "");
            csvDataMap.put("agent_profil_representative", "");
            csvDataMap.put("agent_profil_substitute", "");
            csvDataMap.put("organism_department_id", "");
            csvDataMap.put("organism_department_label", "");
        }

    }

    /**
     *
     * fill information retrieved from User in the hashmap
     *
     * @param csvDataMap
     * @param user
     */
    private void setUserData(HashMap<String, String> csvDataMap, User user) {
        if (user != null) {
            putElement(csvDataMap, "user_id", user.getId());
            if (user.getCivility() != null) {
                putElement(csvDataMap, "user_civility", user.getCivility().getLabel());
            } else {
                putElement(csvDataMap, "user_civility", "");
            }

            putElement(csvDataMap, "user_firstname", user.getFirstname());
            putElement(csvDataMap, "user_lastname", user.getLastname());
            putElement(csvDataMap, "user_username", user.getUsername());
            putElement(csvDataMap, "user_connectionAttempts", user.getConnectionAttempts());
            putElement(csvDataMap, "user_expirationDatePassword", user.getExpirationDatePassword());
            putElement(csvDataMap, "user_modifDatePassword", user.getModifDatePassword());
        } else {
            csvDataMap.put("user_id", "");
            csvDataMap.put("user_civility", "");
            csvDataMap.put("user_firstname", "");
            csvDataMap.put("user_lastname", "");
            csvDataMap.put("user_username", "");
            csvDataMap.put("user_connectionAttempts", "");
            csvDataMap.put("user_expirationDatePassword", "");
            csvDataMap.put("user_modifDatePassword", "");
        }

    }

    private void setOrganismData(HashMap<String, String> csvDataMap, Organism organism) {
        putElement(csvDataMap, "organism_id", organism.getId());
        putElement(csvDataMap, "organism_label", organism.getLabel());
        putElement(csvDataMap, "organism_siren", organism.getSiren());
        if (organism.getNic() != null) {
            putElement(csvDataMap, "organism_nic", organism.getNic());
        } else {
            putElement(csvDataMap, "organism_nic", "");
        }
    }

    /**
     *
     * fill information retrieved from Profile in the hashmap
     *
     * @param csvDataMap
     * @param profile
     */
    private void setProfileData(HashMap<String, String> csvDataMap, Profile profile) {
        if (profile != null) {
            putElement(csvDataMap, "profil_id", profile.getId());
            if (profile.getTechnicalIdentifier() != null) {
                putElement(csvDataMap, "profil_technicalIdentifier", profile.getTechnicalIdentifier());
            } else {
                putElement(csvDataMap, "profil_technicalIdentifier", "");
            }
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
     *
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
        header.add("user_expirationDatePassword");
        header.add("user_modifDatePassword");
        header.add("profil_id");
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
     *
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
