package org.myec3.socle.webapp.controller;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.myec3.socle.core.constants.MyEc3EmailConstants;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.Civility;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.domain.model.enums.PrefComMedia;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.service.*;
import org.myec3.socle.synchro.api.SynchronizationNotificationService;
import org.myec3.socle.webapp.entities.MessageEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@RestController
public class PydioInitController {

    @Autowired
    @Qualifier("applicationService")
    private ApplicationService applicationService;

    @Autowired
    @Qualifier("roleService")
    private RoleService roleService;

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @Autowired
    @Qualifier("agentProfileService")
    private AgentProfileService agentProfileService;

    @Autowired
    @Qualifier("synchronizationNotificationService")
    private SynchronizationNotificationService synchronizationService;

    @Autowired
    @Qualifier("profileService")
    private ProfileService profileService;

    @Autowired
    @Qualifier("organismService")
    private OrganismService organismService;

    @Autowired
    @Qualifier("organismDepartmentService")
    private OrganismDepartmentService organismDepartmentService;

    @Autowired
    @Qualifier("profileTypeService")
    private ProfileTypeService profileTypeService;

    @Autowired
    @Qualifier("emailService")
    private EmailService emailService;


    @GetMapping("pydioInit/createAgent")
    @Transactional
    public void initAgentByCSV() throws IOException {
        File file = new File("/root/result.csv");
        FileWriter outputfile = new FileWriter(file);
        try (CSVWriter writer = new CSVWriter(outputfile,';', CSVWriter.NO_QUOTE_CHARACTER)) {
            List<String[]> data = new ArrayList<>();
            try (CSVReader reader = new CSVReader(new FileReader("/root/import.csv"))) {
                String[] lineInArray;
                while ((lineInArray = reader.readNext()) != null) {
                    Profile profile = create(lineInArray[0], lineInArray[1], lineInArray[2],lineInArray[3], new Long(lineInArray[4]));
                    if (profile != null) {
                        data.add(new String[]{String.valueOf(profile.getId()), lineInArray[2]});
                    } else {
                        data.add(new String[]{"ERROR", lineInArray[2]});
                    }
                }
            }
            writer.writeAll(data);
        }

    }

    private Profile create(String firstName, String lastName, String email,String civ, Long idOrganisme) {
        AgentProfile agentToCreate = new AgentProfile();
        User userToCreate = new User();
        Address addressToCreate = new Address();

        agentToCreate.setName(
                lastName + " " + firstName);
        agentToCreate.setLabel(agentToCreate.getName());

        agentToCreate.setProfileType(this.profileTypeService.findByValue(ProfileTypeValue.AGENT));
        agentToCreate.setUser(userToCreate);
        agentToCreate.setAddress(addressToCreate);
        agentToCreate.setPrefComMedia(PrefComMedia.EMAIL);

        Organism organism = this.organismService.findOne(idOrganisme);
        OrganismDepartment rootDepartment = this.organismDepartmentService.findRootOrganismDepartment(organism);
        userToCreate.setFirstname(firstName);
        userToCreate.setLastname(lastName);
        userToCreate.setCivility(Civility.valueOf(civ));
        agentToCreate.setEmail(email);
        agentToCreate.setOrganismDepartment(rootDepartment);

        // AGENT PROFILE USER
        agentToCreate.getUser().setName(
                agentToCreate.getUser().getLastname() + " " + agentToCreate.getUser().getFirstname());
        agentToCreate.getUser().setLabel(agentToCreate.getUser().getName());
        agentToCreate.getUser().setUsername(agentToCreate.getEmail());

        // SET AGENT ADDRESS
        this.setAddressOfAgentToCreate(agentToCreate);

        this.setBasicRoles(agentToCreate);

        // password for mail
        String password = this.userService.generatePassword();
        agentToCreate.getUser().setExpirationDatePassword(this.userService.generateExpirationDatePassword());
        agentToCreate.getUser().setPassword(this.userService.generateHashPassword(password));

        if (!this.isOkay(agentToCreate)) {
            return null;
        }

        this.userService.create(agentToCreate.getUser());
        User user = this.userService.findByName(agentToCreate.getUser().getName());
        agentToCreate.setUser(user);

        // Create agentProfile
        this.agentProfileService.create(agentToCreate);

        this.synchronizationService.notifyCreation(agentToCreate);

        this.sendMail(password, agentToCreate);

        return this.profileService.findByUsername(agentToCreate.getUsername());

    }

    private void setBasicRoles(AgentProfile agentProfile) {

        List<Application> defaultApplications = this.applicationService
                .findAllDefaultApplicationsByStructureType(agentProfile.getOrganismDepartment().getOrganism().getStructureType());


        List<Role> listRoles = new ArrayList<>();

        // Retrieve roles of default applications
        for (Application application : defaultApplications) {
            Role role = this.roleService
                    .findBasicRoleByProfileTypeAndApplication(agentProfile.getProfileType(), application);
            if ((role != null) && (!listRoles.contains(role))) {
                listRoles.add(role);
            }
        }

        agentProfile.setRoles(listRoles);

    }

    private void setAddressOfAgentToCreate(AgentProfile agentToCreate) {

        Organism organism = agentToCreate.getOrganismDepartment().getOrganism();

        String agentDepartmentPostalAddress = agentToCreate.getOrganismDepartment().getAddress().getPostalAddress();
        String agentDepartmentPostalCode = agentToCreate.getOrganismDepartment().getAddress().getPostalCode();
        String agentDepartmentCity = agentToCreate.getOrganismDepartment().getAddress().getCity();

        if (!agentDepartmentPostalAddress.isEmpty()) {
            agentToCreate.getAddress().setPostalAddress(agentDepartmentPostalAddress);
        } else {
            // We set the organism address
            agentToCreate.getAddress().setPostalAddress(organism.getAddress().getPostalAddress());
        }

        // In this case we retrieve the department postal code
        if (!agentDepartmentPostalCode.isEmpty()) {
            agentToCreate.getAddress().setPostalCode(agentDepartmentPostalCode);
        } else {
            // We set the organism postal code
            agentToCreate.getAddress().setPostalCode(organism.getAddress().getPostalCode());
        }

        // In this case we retrieve the department city
        if (!agentDepartmentCity.isEmpty()) {
            agentToCreate.getAddress().setCity(agentDepartmentCity);
        } else {
            // We set the organism city
            agentToCreate.getAddress().setCity(organism.getAddress().getCity());
        }


        // SET COUNTRY
        agentToCreate.getAddress().setCountry(Country.FR);
    }

    private boolean isOkay(AgentProfile agentToCreate) {
        // Check that username not already exists
        return !this.profileService.usernameAlreadyExists(agentToCreate.getEmail(), agentToCreate);

    }

    public void sendMail(String password, AgentProfile agentProfile) {
        // message mail
        StringBuilder message = new StringBuilder();
        message.append("Votre login:");
        message.append(agentProfile.getEmail());
        message.append("\n");
        message.append("Votre password:");
        message.append(password);
        message.append("\n\n");
        message.append("Cordialement,\\nEquipe your company");

        MessageEmail messageEmail = new MessageEmail(ProfileTypeValue.AGENT,
                MessageEmail.EmailContext.AGENT_ORGANISME_CREATE, agentProfile.getUser().getUsername(), password,
                agentProfile.getOrganismDepartment().getOrganism().getLabel());

        Properties properties = new Properties();
        properties.put("telephone-content-email", "02 23 48 04 54");
        properties.put("hello-message", "Bonjour,");
        properties.put("welcome-user-organism", "Votre compte utilisateur pour l'organisme");
        properties.put("content-email-user", " vient d’être créé avec succès par un administrateur de cet organisme sur la plate-forme.");
        properties.put("content-email-user-info", "Vous pouvez dès maintenant accéder  à votre espace et aux différents services de la plate-forme  pour lesquels des droits vous ont été attribués, avec les informations suivantes :");
        properties.put("identifiant-label", " Identifiant:");
        properties.put("password-label", "Mot de passe:");
        properties.put("content-email-info1", "Conservez-les précieusement ! Ils vous seront demandés à chaque nouvelle connexion.");
        properties.put("content-email-info2", "(Vous aurez toutefois la possibilité de modifier votre mot de passe dès votre prochaine connexion).");
        properties.put("content-email-footer2", "Ce mail est généré automatiquement, merci de ne pas y répondre.");

        String[] recipients = new String[1];
        recipients[0] = agentProfile.getEmail();
        this.emailService.silentSendMail(MyEc3EmailConstants.getSender(), MyEc3EmailConstants.getFrom(), recipients,
                "[MEGALIS BRETAGNE] Votre login et votre mot de passe.",
                messageEmail.generateContent(properties, agentProfile.getOrganismDepartment().getOrganism().getCustomer()));
    }
}
