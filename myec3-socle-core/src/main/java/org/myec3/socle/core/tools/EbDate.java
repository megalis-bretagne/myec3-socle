package org.myec3.socle.core.tools;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EbDate {

	private static Locale locale = java.util.Locale.FRANCE;

	/**
	 * Timezone francaise.
	 */
	public static final TimeZone TIMEZONE_FRANCAISE = TimeZone
			.getTimeZone("Europe/Paris");

	/**
	 * Constante période journalier.
	 */
	public static final int PERIODE_JOURNALIERE = 1;

	/**
	 * Constante période hebdomadaire.
	 */
	public static final int PERIODE_HEBDOMADAIRE = 2;

	/**
	 * Constante période mensuel.
	 */
	public static final int PERIODE_MENSUELLE = 3;

	/**
	 * Constante période trimestriel.
	 */
	public static final int PERIODE_TRIMESTRIELLE = 4;

	/**
	 * Constante période semestriel.
	 */
	public static final int PERIODE_SEMESTRIELLE = 5;

	/**
	 * Constante période annuel.
	 */
	public static final int PERIODE_ANNUELLE = 6;

	/**
	 * Returns the current date.
	 * 
	 * @return the current date.
	 */
	public static Date getDateNow() {
		return new Date(System.currentTimeMillis());
	}

	/**
	 * Returns the current date.
	 * 
	 * @return the current date.
	 */
	public static Timestamp getTimestampNow() {
		return new Timestamp(System.currentTimeMillis());
	}

	/**
	 * Créé une date correspondant aux jour/mois/année passés en paramètres.
	 * 
	 * @param jour
	 *            Jour.
	 * @param mois
	 *            Mois (à partir de 0 jusqu'à 11).
	 * @param annee
	 *            Année.
	 * @return Date créée.
	 */
	public static Date getDate(int jour, int mois, int annee) {
		Calendar cal = Calendar.getInstance();
		cal.set(annee, mois, jour);
		cal.setTimeZone(TIMEZONE_FRANCAISE);
		return cal.getTime();
	}

	/**
	 * Adds the given date a given number of days. The number may be negative.
	 * 
	 * @param date
	 *            Date.
	 * @param nbDays
	 *            Number of days to add to the date.
	 * @return the modified date.
	 */
	public static Date addDays(Date date, int nbDays) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, nbDays);

		if (date instanceof Timestamp)
			return new Timestamp(calendar.getTimeInMillis());
		else
			return calendar.getTime();
	}

	/**
	 * Convertit une String (contenant une date) en Date.
	 * 
	 * @param format
	 *            Pattern à lire (ex. "DD/MM/YYYY") , voir SimpleDateFormat.
	 * @param date
	 *            Chaîne contenant la date à lire.
	 * @return Date lue.
	 */
	public static Date parseDate(String format, String date)
			throws ParseException {
		DateFormat df = new SimpleDateFormat(format, locale);
		df.setTimeZone(TIMEZONE_FRANCAISE);
		df.setLenient(false);
		return df.parse(date);
	}

	/**
	 * Mets des champs à jour à partir d'une date donnée.
	 * 
	 * @param date
	 *            la Date de depart
	 * @param fields
	 *            Indique quels champs modifier: 1e dimension: identifie le
	 *            champ (avec les constantes de Calendar); 2e dimension: la
	 *            valeur a mettre a jour
	 * @param timeZone
	 *            la TimeZone a utiliser
	 * @return La date mise à jour.
	 */
	public static Date setFields(Date date, int[][] fields, TimeZone timeZone) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.setTimeZone(timeZone);

		for (int i = 0; i < fields.length; i++)
			c.set(fields[i][0], fields[i][1]);

		if (date instanceof Timestamp)
			return new Timestamp(c.getTimeInMillis());
		else
			return c.getTime();
	}

	/**
	 * Modifie la date donnée pour la mettre à minuit début de journée.
	 * 
	 * @param date
	 *            Une date.
	 * @return La date mise à minuit.
	 */
	public static Date setMinuitDebut(Date date) {
		int[][] fields = { { Calendar.HOUR_OF_DAY, 0 }, { Calendar.MINUTE, 0 },
				{ Calendar.SECOND, 0 }, { Calendar.MILLISECOND, 0 } };
		return setFields(date, fields, TIMEZONE_FRANCAISE);
	}

	/**
	 * Modifie la date donnée pour la mettre à minuit en fin de journée.
	 * 
	 * @param date
	 *            Une date.
	 * @return La date mise à minuit.
	 */
	public static Date setMinuitFin(Date date) {
		int[][] fields = { { Calendar.HOUR_OF_DAY, 23 },
				{ Calendar.MINUTE, 59 }, { Calendar.SECOND, 59 },
				{ Calendar.MILLISECOND, 999 } };
		return setFields(date, fields, TIMEZONE_FRANCAISE);
	}

	/**
	 * Calcule l'année d'une date donnée.
	 * 
	 * @param date
	 *            Date quelconque de l'année.
	 * @return L'année correspondante.
	 */
	public static int getAnnee(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TIMEZONE_FRANCAISE);
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	/**
	 * Cette méthode renvoie la différence en jours ouvrés pleins entre deux
	 * dates. Si l'une des deux dates en paramètres est null, la méthode renvoie
	 * 0.
	 * 
	 * @param dateDebut
	 * @param dateFin
	 * @return int
	 */
	public static int getDifferenceEnJoursOuvres(Date dateDebut, Date dateFin) {
		int nbJoursOuvres = 0;

		if (dateDebut != null && dateFin != null) {
			if (dateFin.before(dateDebut)) {
				Date dateTemp = dateDebut;
				dateDebut = dateFin;
				dateFin = dateTemp;
			}

			// recadrage du premier jour si férié ou non ouvré
			if (isJourFerie(dateDebut)) {
				dateDebut = setMinuitDebut(addDays(dateDebut, 1));
			}
			// recadrage du dernier jour si férié ou non ouvré
			if (isJourFerie(dateFin)) {
				dateFin = setMinuitFin(addDays(dateFin, -1));
			}

			// Nombre de jours fériés ou non ouvrés
			int nbJoursNonOuvres = getNbJoursNonOuvres(dateDebut, dateFin);
			int nbJoursEcart = getDifferenceEnJours(dateDebut, dateFin);

			nbJoursOuvres = nbJoursEcart - nbJoursNonOuvres;
		}

		return nbJoursOuvres;
	}

	/**
	 * Détermine si la date donnée est un jour férié ou non
	 * 
	 * @param date
	 *            Date
	 * @return true si la date est un jour férié, false sinon
	 */
	public static boolean isJourFerie(Date date) {
		boolean retour = false;
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);

		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
				|| calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			retour = true;
		} else {
			if (isJourFerieCalendrier(date)) {
				retour = true;
			}
		}

		return retour;
	}

	/**
	 * Détermine si la date donnée est un jour férié du calendrier ou non
	 * 
	 * @param date
	 *            Date
	 * @return true si la date est un jour férié du calendrier, false sinon
	 */
	public static boolean isJourFerieCalendrier(Date date) {
		boolean retour = false;
		int annee = getAnnee(date);
		Date dateDebutDuJour = setMinuitDebut(date);
		// 1er janvier
		Date jourDeLan = getDate(1, 1 - 1, annee);
		// 1er mai
		Date feteTravail = getDate(1, 5 - 1, annee);
		// 8 mai
		Date victoire1945 = getDate(8, 5 - 1, annee);
		// 14 juillet
		Date feteNationale = getDate(14, 7 - 1, annee);
		// 15 aout
		Date assomption = getDate(15, 8 - 1, annee);
		// 1er novembre
		Date toussaint = getDate(1, 11 - 1, annee);
		// 11 novembre
		Date armistice1918 = getDate(11, 11 - 1, annee);
		// 25 decembre
		Date noel = getDate(25, 12 - 1, annee);
		Date paques = calculDatePaques(annee);
		Date lundiPaques = addDays(paques, 1);
		Date ascension = addDays(paques, 39);
		Date pentecote = addDays(ascension, 10);
		Date lundiPentecote = addDays(pentecote, 1);
		Date[] dateJoursFeriesCalendrier = { jourDeLan, feteTravail,
				victoire1945, feteNationale, assomption, toussaint,
				armistice1918, noel, lundiPaques, ascension, lundiPentecote };

		for (Date dateFerieCalendrier : dateJoursFeriesCalendrier) {
			Date dateDebutJourFerieCalendrier = EbDate
					.setMinuitDebut(dateFerieCalendrier);
			if (Math.abs(EbDate.getDifferenceEnJours(dateDebutDuJour,
					dateDebutJourFerieCalendrier)) < 1) {
				retour = true;
			}
		}
		return retour;
	}

	/**
	 * Cette méthode renvoie le nombre de jours non ouvres d'un intervalle de
	 * date, c'est-à-dire le nombre de jours fériés ou non ouvrés.
	 * 
	 * @param dateDebut
	 *            date de début d'intervallle
	 * @param dateFin
	 *            date de fin d'intervallle
	 * @return int
	 */
	public static int getNbJoursNonOuvres(Date dateDebut, Date dateFin) {
		int nbJoursNonOuvres = 0;

		if (dateDebut != null && dateFin != null) {
			if (dateFin.before(dateDebut)) {
				Date dateTemp = dateDebut;
				dateDebut = dateFin;
				dateFin = dateTemp;
			}
			dateDebut = setMinuitDebut(dateDebut);
			dateFin = setMinuitFin(dateFin);

			Date dateEnCours = dateDebut;

			while (dateEnCours.before(dateFin)) {
				if (isJourFerie(dateEnCours))
					nbJoursNonOuvres++;
				dateEnCours = addDays(dateEnCours, 1);
			}

		}
		return nbJoursNonOuvres;
	}

	public static List<Date> getListJoursOuvres(Date dateDebut, Date dateFin) {
		List<Date> listJoursOuvres = new ArrayList<Date>();

		if (dateDebut != null && dateFin != null) {
			if (dateFin.before(dateDebut)) {
				Date dateTemp = dateDebut;
				dateDebut = dateFin;
				dateFin = dateTemp;
			}
			dateDebut = setMinuitDebut(dateDebut);
			dateFin = setMinuitFin(dateFin);

			Date dateEnCours = dateDebut;

			while (dateEnCours.before(dateFin)) {
				if (!isJourFerie(dateEnCours))
					listJoursOuvres.add(dateEnCours);
				dateEnCours = addDays(dateEnCours, 1);
			}
		}
		return listJoursOuvres;
	}

	/**
	 * Calcule le nombre de jours qui séparent deux dates.
	 * 
	 * @param dateDebut
	 *            Date de début.
	 * @param dateFin
	 *            Date de fin.
	 * @return Nombre de jours qui séparent deux dates.
	 */
	public static int getDifferenceEnJours(Date dateDebut, Date dateFin) {
		return getDifference(dateDebut, dateFin, PERIODE_JOURNALIERE);
	}

	/**
	 * Calcule le nombre de periode de difference en fonction du type de
	 * période.
	 * 
	 * @param debut
	 *            Date de début de période.
	 * @param fin
	 *            Date de fin de période.
	 * @param echantillonnage
	 *            L'echantillonnage à prendre en compte.
	 * @return Le nombre d'echantillons qui sépare deux dates, 0 correspondant
	 *         au cas où la date données appartient ou est égal à l'échantillon.
	 * @throws Exception
	 */
	public static int getDifference(Date debut, Date fin, int periode) {
		int difference, field, amount;

		switch (periode) {
		case PERIODE_JOURNALIERE:
			field = Calendar.DATE;
			amount = 1;
			break;

		case PERIODE_HEBDOMADAIRE:
			field = Calendar.WEEK_OF_YEAR;
			amount = 1;
			break;

		case PERIODE_MENSUELLE:
			field = Calendar.MONTH;
			amount = 1;
			break;

		case PERIODE_TRIMESTRIELLE:
			field = Calendar.MONTH;
			amount = 3;
			break;

		case PERIODE_SEMESTRIELLE:
			field = Calendar.MONTH;
			amount = 6;
			break;

		default:
		case PERIODE_ANNUELLE:
			field = Calendar.YEAR;
			amount = 1;
			break;
		}

		if (debut.after(fin)) {
			Date temp = debut;
			debut = fin;
			fin = temp;
		}

		Calendar c = new GregorianCalendar();
		c.setTime(debut);

		Date d = debut;
		for (difference = 0; !fin.before(d); difference++) {
			c.add(field, amount);
			d = c.getTime();
		}
		return difference - 1;
	}

	/**
	 * Calcul de la date de Pâques selon l'algorithme de Thomas O'Beirne
	 * 
	 * @param annee
	 *            Année
	 * @return Date de Pâques
	 */
	private static Date calculDatePaques(int annee) {
		int cal1, cal2, cal3, cal4, cal5, p;
		Date finMars = EbDate.getDate(31, 2, annee);
		Date paques;

		// Utilisons l'algorithme de Thomas O’Beirne
		annee = annee - 1900;
		cal1 = annee % 19;
		cal2 = cal1 * 7 + 1;
		cal2 = cal2 / 19;

		cal3 = (11 * cal1) - cal2 + 4;
		cal3 = cal3 % 29;

		cal4 = annee / 4;

		cal5 = annee - cal3 + cal4 + 31;
		cal5 = cal5 % 7;

		p = 25 - cal3 - cal5;

		paques = addDays(finMars, p);

		return paques;
	}
}
