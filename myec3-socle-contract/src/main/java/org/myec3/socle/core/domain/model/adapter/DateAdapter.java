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
package org.myec3.socle.core.domain.model.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This class allows to override  marshal and unmarshal methods for
 * attributes of type Date in order to choose the format of the date to send into the
 * XML.
 * 
 * To Use this adapter you must add the annotation :
 * 
 * @XmlJavaTypeAdapter(DateAdapter.class) over the attribute to send into the
 *                                        XML
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 * 
 */
public class DateAdapter extends XmlAdapter<String, Date> {

	// The desired format of date to send
	private String pattern = "dd/MM/yyyy";

	/**
	 * Marshal the Date
	 * 
	 * @param date
	 *            : The date to marshal
	 * @return a string containing the date formatted.
	 */
	@Override
	public String marshal(Date date) throws Exception {
		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * Unmarshal a Date
	 * 
	 * @param dateString
	 *            : The String to unmarshal
	 * @return a java Date object
	 */
	@Override
	public Date unmarshal(String dateString) throws Exception {
		return new SimpleDateFormat(pattern).parse(dateString);
	}
}
