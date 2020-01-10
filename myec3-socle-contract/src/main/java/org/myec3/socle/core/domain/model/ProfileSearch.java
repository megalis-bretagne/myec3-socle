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

package org.myec3.socle.core.domain.model;

/**
 * This class represents the User Search result Object.<br>
 * 
 * 
 * @author Matthieu GASPARD <matthieu.gaspard@worldline.com>
 * 
 */
public class ProfileSearch {
    
    private Profile searchProfile;
    private Structure searchStructure;
    
    /**
     * @return the searchProfile
     */
    public Profile getSearchProfile() {
        return searchProfile;
    }
    
    /**
     * @param searchProfile the searchProfile to set
     */
    public void setSearchProfile(Profile searchProfile) {
        this.searchProfile = searchProfile;
    }
    
    /**
     * @return the searchStructure
     */
    public Structure getSearchStructure() {
        return searchStructure;
    }
    
    /**
     * @param searchStructure the searchStructure to set
     */
    public void setSearchStructure(Structure searchStructure) {
        this.searchStructure = searchStructure;
    }
}
