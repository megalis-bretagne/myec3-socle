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
package org.myec3.socle.webapp.entities;

import java.util.List;

/**
 * This class represents a menu item that is displayed in navigation menu <br />
 * 
 * @author Anthony Colas<anthony.j.colas@atosorigin.com>
 * 
 */
public class MenuItem {

	private String name;

	private Class<?> page;

	private List<MenuItem> children;

	private boolean exactMatch;

	public MenuItem(String name, Class<?> page, boolean exactMatch) {
		super();
		this.name = name;
		this.page = page;
		this.children = null;
		this.setExactMatch(exactMatch);
	}

	public MenuItem(String name, Class<?> page, boolean exactMatch,
			List<MenuItem> children) {
		this(name, page, exactMatch);
		this.children = children;
	}

	/**
	 * @return the menu item name displayed in navigation menu
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the tapestry page associated at the menu item
	 */
	public Class<?> getPage() {
		return this.page;
	}

	public void setPage(Class<?> value) {

		this.page = value;
	}

	/**
	 * @return the list of children menu items associated at the current menu item 
	 */
	public List<MenuItem> getChildren() {
		return this.children;
	}

	public void setChildren(List<MenuItem> value) {
		this.children = value;
	}

	public void setExactMatch(boolean exactMatch) {
		this.exactMatch = exactMatch;
	}

	public boolean isExactMatch() {
		return exactMatch;
	}
}
