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
package org.myec3.socle.core.sync.api;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is used to communicate with webservice server for complex queries.
 * 
 * Class unused from now
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@XmlRootElement
public class Message {

	private Long id;
	private List<Long> listOfIds;
	private ClassType classType;

	@XmlElement(required = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlElement(required = false)
	public List<Long> getListOfIds() {
		return listOfIds;
	}

	public void setListOfIds(List<Long> listofids) {
		this.listOfIds = listofids;
	}

	@XmlElement(required = false)
	public ClassType getClassType() {
		return classType;
	}

	public void setClassType(ClassType classType) {
		this.classType = classType;
	}
}
