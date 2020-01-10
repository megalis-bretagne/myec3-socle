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
package org.myec3.socle.core.domain.model.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * An enumeration of authorized mime type for IE
 * 
 * @author Maxime Capelle <maxime.capelle@atosorigin.com>
 * 
 */
public enum AuthorizedMimeType {

	/*
	 * add a non-standard MIME type for IE: image/pjpeg and image/x-png
	 */
	JPEG("image/jpeg", "image/pjpeg");
	// GIF("image/gif","image/gif"),
	// PNG("image/png","image/x-png");

	private final String label;
	private final String labelIE;

	/**
	 * Constructor
	 * 
	 * @param label
	 *            : label of the Mime type
	 * @param labelForIE
	 *            : label of the Mime type for Internet explorer browser
	 */
	private AuthorizedMimeType(String label, String labelForIE) {
		this.label = label;
		this.labelIE = labelForIE;
	}

	public String getLabel() {
		return label;
	}

	public String getLabelIE() {
		return labelIE;
	}

	public static List<String> getValuesList() {
		ArrayList<String> valuesList = new ArrayList<String>();
		AuthorizedMimeType[] values = AuthorizedMimeType.values();
		for (AuthorizedMimeType authorizedMimeType : values) {
			valuesList.add(authorizedMimeType.label);
			valuesList.add(authorizedMimeType.labelIE);
		}

		return valuesList;
	}

	public static AuthorizedMimeType getTypeByLabel(String label) {
		AuthorizedMimeType[] values = AuthorizedMimeType.values();
		for (AuthorizedMimeType authorizedMimeType : values) {
			if (authorizedMimeType.getLabel().equals(label)
					|| (authorizedMimeType.getLabelIE().equals(label))) {
				return authorizedMimeType;
			}
		}

		return null;
	}
}
