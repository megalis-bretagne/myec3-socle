package org.myec3.socle.webapp.batch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.myec3.socle.core.domain.model.AgentProfile;
import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.EmployeeProfile;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.domain.model.enums.ProfileTypeValue;
import org.myec3.socle.core.domain.model.meta.ProfileType;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.core.service.ProfileTypeService;
import org.myec3.socle.core.service.UserService;
import org.myec3.socle.core.tools.EbDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Class update the expiration date of the password of the user
 * 
 * @author Arnaud Ozeel <arnaud.ozeel@atosorigin.com>
 */
public class UpdateUserPasswordExpiration {

    /**
     * Date format
     */
    private static final SimpleDateFormat sdf = new SimpleDateFormat(
	    "dd/MM/yyyy");

    /**
     * Logger for this class
     */
    private static Logger logger = LoggerFactory
	    .getLogger(UpdateUserPasswordExpiration.class);

    /**
     * Date from which passwords can expire
     */
    private Date expirationStartDate = null;

    /**
     * End date of the scheduled expiration of passwords
     */
    private Date expirationEndDate = null;

    /**
     * List of possible expiry dates
     */
    List<Date> listExpirationDates = null;

    /**
     * Business Service providing methods and specifics operations on
     * {@link User} objects
     */
    private static UserService userService;
    /**
     * Business Service providing methods and specifics operations on
     * {@link Profil} objects
     */
    private static ProfileService profileService;
    /**
     * Business Service providing methods and specifics operations on
     * {@link ProfilType} objects
     */
    private static ProfileTypeService profileTypeService;

    /**
     * Main method
     * 
     * @param args
     *            -h : affiche l'aide -p<typeProfil> (champ obligatoire): mise à
     *            jour suivant le type de profil donné : AGENT ou EMPLOYEE.
     *            -dd<date1> -df<date2> : simule un traitement de <date1> à
     *            <date2> au format yyyyMMdd : dd<Date début expiration> ;
     *            df<Date fin expiration> (champ obligatoire).Si la date de
     *            début d\'expiration n'est pas renseignée, celle-ci est
     *            initialisée avec la date de demain -allExpDate : réalise le
     *            traitement sur tous les utilisateurs ayant une date
     *            d'expiration renseignée ou non -readonly : réalise le
     *            traitement sans modification Bdd
     */
    public static void main(String[] args) {

	String expirationStartDateParam = null;
	String expirationEndDateParam = null;
	String profilType = null;
	boolean isReadOnly = false;
	boolean updateUserExpDateNotNull = false;

	if (args.length != 0) {
	    for (int i = 0; i < args.length; i++) {
		if (args[i].startsWith("-h")) {
		    System.out
			    .println("usage: UpdateUserPasswordExpiration [-h -p<AGENT|EMPLOYEE> -dd<date1> -df<date2> -readonly]");

		    System.out.println(" -h : affiche l'aide.");
		    System.out
			    .println(" -p<typeProfil> (champ obligatoire): mise à jour suivant le type de profil donné : AGENT ou EMPLOYEE.");
		    System.out
			    .println(" -dd<date1> -df<date2> : simule un traitement de <date1> à <date2> au format yyyyMMdd :  dd<Date début expiration> ; df<Date fin expiration> (champ obligatoire).Si la date de début d\'expiration n'est pas renseignée, celle-ci est initialisée avec la date de demain");
		    System.out
			    .println(" -allExpDate : réalise le traitement sur tous les utilisateurs ayant une date d'expiration renseignée ou non");
		    System.out
			    .println(" -readonly : réalise le traitement sans modification Bdd");

		    System.exit(0);
		}
		if (args[i].startsWith("-dd")) {
		    expirationStartDateParam = args[i].substring(3);
		}
		if (args[i].startsWith("-df")) {
		    expirationEndDateParam = args[i].substring(3);
		}
		if (args[i].startsWith("-p")) {
		    profilType = args[i].substring(2);
		}
		if (args[i].startsWith("-allExpDate")) {
		    updateUserExpDateNotNull = true;
		}
		if (args[i].startsWith("-readonly")) {
		    isReadOnly = true;
		}
	    }
	}

	if (profilType == null
		&& !(ProfileTypeValue.AGENT.toString())
			.equalsIgnoreCase(profilType)
		&& !(ProfileTypeValue.EMPLOYEE.toString())
			.equalsIgnoreCase(profilType)) {
	    System.out
		    .println("Nombre incorrect d'arguments : Le type de profil n'est pas renseignée ou incorrect. usage: UpdateUserPasswordExpiration -h pour afficher l'aide");
	    System.exit(0);
	}

	if (expirationEndDateParam == null) {
	    System.out
		    .println("Nombre incorrect d'arguments : La date de fin d'expiration n'est pas renseignée. usage: UpdateUserPasswordExpiration -h pour afficher l'aide");
	    System.exit(0);
	}

	new UpdateUserPasswordExpiration(profilType, expirationStartDateParam,
		expirationEndDateParam, isReadOnly, updateUserExpDateNotNull);
    }

    /**
     * Default constructor
     * 
     * @param profilType
     * @param expirationStartDateParam
     * @param expirationEndDateParam
     * @param updateUserExpDateNotNull
     * @param isReadOnly
     */
    public UpdateUserPasswordExpiration(String profilType,
	    String expirationStartDateParam, String expirationEndDateParam,
	    boolean isReadOnly, boolean updateUserExpDateNotNull) {

	if (this.init()) {

	    logger.info("Début du traitement");

	    if (initExpirationDates(expirationStartDateParam,
		    expirationEndDateParam)) {

		if ((ProfileTypeValue.AGENT.toString())
			.equalsIgnoreCase(profilType)) {
		    ProfileType type = profileTypeService
			    .findByValue(ProfileTypeValue.AGENT);
		    this.updateUsersExpirationDate(type, isReadOnly,
			    updateUserExpDateNotNull);
		} else if ((ProfileTypeValue.EMPLOYEE.toString())
			.equalsIgnoreCase(profilType)) {
		    ProfileType type = profileTypeService
			    .findByValue(ProfileTypeValue.EMPLOYEE);
		    this.updateUsersExpirationDate(type, isReadOnly,
			    updateUserExpDateNotNull);
		}
	    }

	    logger.info("Fin du traitement");
	}
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
		    new String[] { "classpath:coreMyec3Context.xml",
			    "classpath*:coreMyec3Context.xml" });

	    userService = (UserService) applicationContext
		    .getBean("userService");

	    profileService = (ProfileService) applicationContext
		    .getBean("profileService");

	    profileTypeService = (ProfileTypeService) applicationContext
		    .getBean("profileTypeService");
	} catch (Exception e) {
	    e.printStackTrace();
	    result = false;
	}

	return result;
    }

    /**
     * Initializes expiration dates
     * 
     * @param expirationStartDateParam
     * @param expirationEndDateParam
     * @return true if initialization was successful, false otherwise
     */
    public boolean initExpirationDates(String expirationStartDateParam,
	    String expirationEndDateParam) {

	boolean result = true;
	try {
	    if (expirationStartDateParam == null) {
		// Pas de date de début d'expiration : par défaut on prend la
		// date de demain
		this.expirationStartDate = EbDate.addDays(EbDate.getDateNow(),
			+1);
	    } else {
		this.expirationStartDate = EbDate.parseDate("yyyyMMdd",
			expirationStartDateParam);
	    }

	    if (expirationEndDateParam != null) {
		this.expirationEndDate = EbDate.parseDate("yyyyMMdd",
			expirationEndDateParam);
	    } else {
		logger.info("Erreur : la date de fin d'expiration n'est pas définie");
		result = false;
	    }

	    // Récupère la liste des dates d'expiration possibles (jours non
	    // ouvrés, non fériés)
	    logger.info("Calcul des dates d'expiration possibles entre le "
		    + sdf.format(this.expirationStartDate) + " et le "
		    + sdf.format(this.expirationEndDate));

	    this.listExpirationDates = EbDate.getListJoursOuvres(
		    expirationStartDate, expirationEndDate);
	    logger.info("  --> Nombre de jours ouvrés, non fériés pour la période : "
		    + this.listExpirationDates.size());

	} catch (Exception e) {
	    e.printStackTrace();
	    result = false;
	}
	return result;
    }

    /**
     * updateExpirationDateUsers
     * 
     * @param profilType
     * @param isReadOnly
     * @param updateUserExpDateNotNull
     */
    public void updateUsersExpirationDate(ProfileType profilType,
	    boolean isReadOnly, boolean updateUserExpDateNotNull) {

	// Initialise les dates à répartir
	ArrayList<DateProfil> listeDateProfil = new ArrayList<DateProfil>();
	StringBuffer bufDates = new StringBuffer(1000);
	for (Date dateARepartir : listExpirationDates) {
	    listeDateProfil.add(new DateProfil(dateARepartir,
		    updateUserExpDateNotNull));
	    bufDates.append(sdf.format(dateARepartir)).append(",");
	}
	logger.info("  --> Liste des dates d'expirations à répartir [ "
		+ bufDates.toString() + " ] ");

	logger.info("Recherche des profils de type : " + profilType.getValue());
	List<Profile> listProfil = profileService
		.findAllProfilesByProfileType(profilType);
	logger.info("  --> Nombre de profil trouvé : " + listProfil.size());

	// Parcours la liste des profils
	for (Profile profil : listProfil) {
	    // Tri par ordre croissant de la somme du nombre d'utilisateurs,
	    // nombre de département, et nombre d'organisation ou d'entreprise
	    Collections.sort(listeDateProfil);

	    // Récupère le premier élément de la liste
	    DateProfil dateProfil = listeDateProfil.get(0);

	    // Positionne le profil à cette date
	    dateProfil.addProfil(profil);
	}

	logger.info("RESUME");

	String labelReference = "ORGN";
	if (profilType.getValue().equals(ProfileTypeValue.EMPLOYEE)) {
	    labelReference = "COMP";
	}

	for (DateProfil dateprofil : listeDateProfil) {
	    StringBuffer buf = new StringBuffer(1000);

	    buf.append("DATE EXPIR [")
		    .append(sdf.format(dateprofil.getExpirationDate()))
		    .append("] NB ").append(profilType.getValue()).append(" [")
		    .append(dateprofil.getNbUser()).append("] NB ")
		    .append(labelReference).append(" [")
		    .append(dateprofil.getNbReference()).append("] NB DEPT [")
		    .append(dateprofil.getNbDepartment()).append("]");

	    buf.append("\n Liste " + labelReference + " :"
		    + dateprofil.getListIdReference());
	    buf.append("\n Liste DEPT : " + dateprofil.getListDepartment());

	    logger.info(buf.toString());

	    // Mise à jour de la date d'expiration pour les utilisateurs
	    if (!dateprofil.getListUser().isEmpty()) {
		for (User user : dateprofil.getListUser()) {
		    if (!isReadOnly) {
			userService.update(user);
			logger.info("  --> update user id[" + user.getId()
				+ "]");
		    } else {
			logger.info("  --> user id[" + user.getId() + "]");
		    }
		}
	    } else {
		logger.info(" Aucun utilisateur à mettre à jour");
	    }
	}
	System.out.println("-------------");
    }

    /**
     * Inner Classe Class to make the distribution of Profil
     * 
     * @author FR19949
     */
    class DateProfil implements Comparable<Object> {

	private Date expirationDate;

	private int nbUser = 0;

	private HashSet<Long> listIdReference = new HashSet<Long>();
	private HashSet<String> listDepartment = new HashSet<String>();
	private ArrayList<User> listUser = new ArrayList<User>();
	private boolean updateUserExpDateNotNull = false;

	/**
	 * Default constructor
	 * 
	 * @param expirationDate
	 */
	public DateProfil(Date expirationDate, boolean updateUserExpDateNotNull) {
	    this.expirationDate = expirationDate;
	    this.updateUserExpDateNotNull = updateUserExpDateNotNull;
	}

	/**
	 * Add profil
	 * 
	 * @param profil
	 * @param updateExpDateNotNull
	 */
	public void addProfil(Profile profil) {

	    User user = null;
	    Long idReference = null;
	    String dpt = "";

	    if (profil instanceof AgentProfile) {
		// Récupération des informations de l'organisme et du
		// département si celui ci existe
		AgentProfile agent = (AgentProfile) profil;
		user = agent.getUser();

		Organism organisme = agent.getOrganismDepartment()
			.getOrganism();
		idReference = organisme.getId();

		String postalCode = organisme.getAddress().getPostalCode();
		if (postalCode != null && postalCode.length() > 2) {
		    dpt = postalCode.substring(0, 2);
		}
	    } else if (profil instanceof EmployeeProfile) {

		// Récupération des informations de l'entreprise et du
		// département si celui ci existe
		EmployeeProfile employee = (EmployeeProfile) profil;
		user = employee.getUser();

		Company company = employee.getCompanyDepartment().getCompany();
		idReference = company.getId();

		String postalCode = company.getAddress().getPostalCode();
		if (postalCode != null && postalCode.length() > 2) {
		    dpt = postalCode.substring(0, 2);
		}
	    }

	    if (user != null
		    && (user.getExpirationDatePassword() == null || this.updateUserExpDateNotNull)) {
		user.setExpirationDatePassword(this.expirationDate);
		listUser.add(user);
		listIdReference.add(idReference);
		listDepartment.add(dpt);

		nbUser++;
	    }
	}

	/**
	 * @return user list
	 */
	public ArrayList<User> getListUser() {
	    return listUser;
	}

	/**
	 * @return the list of organism or company
	 */
	public Set<Long> getListIdReference() {
	    return this.listIdReference;
	}

	/**
	 * @return the number of organism or company
	 */
	public long getNbReference() {
	    return this.listIdReference.size();
	}

	/**
	 * @return the number of department
	 */
	public long getNbDepartment() {
	    return this.listDepartment.size();
	}

	/**
	 * @return the list of department
	 */
	public Set<String> getListDepartment() {
	    return this.listDepartment;
	}

	/**
	 * @return the expiration date
	 */
	public Date getExpirationDate() {
	    return expirationDate;
	}

	/**
	 * @param expirationdate
	 *            the date to set
	 */
	public void setExpirationDate(Date expirationDate) {
	    this.expirationDate = expirationDate;
	}

	/**
	 * @return the number of user
	 */
	public int getNbUser() {
	    return nbUser;
	}

	/**
	 * @param nbUser
	 *            the number of user to set
	 */
	public void setNbUser(int nbUser) {
	    this.nbUser = nbUser;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(Object o) {
	    DateProfil o2 = (DateProfil) o;
	    if ((o2.getNbUser() + o2.getNbReference() + o2.getNbDepartment()) < (this
		    .getNbUser() + this.getNbReference() + this
			.getNbDepartment())) {
		return 1;
	    } else if ((o2.getNbUser() + o2.getNbReference() + o2
		    .getNbDepartment()) == (this.getNbUser()
		    + this.getNbReference() + this.getNbDepartment())) {
		return 0;
	    } else {
		return -1;
	    }
	}
    }

}
