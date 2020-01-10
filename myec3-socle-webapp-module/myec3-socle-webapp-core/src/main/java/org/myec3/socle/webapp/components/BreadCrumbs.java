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
package org.myec3.socle.webapp.components;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Component class used to display BreadCrumbs<br>
 * 
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/components/BreadCrmbs.tml
 * 
 * @author Anthony Colas <anthony.colas@atos.net>
 * 
 */
public class BreadCrumbs {

	/**
	 * @return the current date
	 */
	public String getCurrentDate() {
		SimpleDateFormat formater = new SimpleDateFormat("dd MMMM yyyy",
				Locale.FRANCE);
		Date date = new Date();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		int today = calendar.get(Calendar.DAY_OF_WEEK);
		StringBuilder sb = new StringBuilder();
		switch (today) {
		case GregorianCalendar.MONDAY:
			sb.append("Lundi");
			break;
		case GregorianCalendar.TUESDAY:
			sb.append("Mardi");
			break;
		case GregorianCalendar.WEDNESDAY:
			sb.append("Mercredi");
			break;
		case GregorianCalendar.THURSDAY:
			sb.append("Jeudi");
			break;
		case GregorianCalendar.FRIDAY:
			sb.append("Vendredi");
			break;
		case GregorianCalendar.SATURDAY:
			sb.append("Samedi");
			break;
		case GregorianCalendar.SUNDAY:
			sb.append("Dimanche");
			break;

		}
		sb.append(" ");
		sb.append(formater.format(date));
		return sb.toString();
	}
}
