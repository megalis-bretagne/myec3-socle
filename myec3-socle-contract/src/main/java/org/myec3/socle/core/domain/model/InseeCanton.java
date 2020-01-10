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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * This class represents an INSEE Region.
 * 
 * @author Matthieu GASPARD <matthieu.gaspard@worldline.com>
 * 
 */
@Entity
public class InseeCanton implements PE {

	private Long id;

	// Code région
	private Long region;

	// Code département
	private String dep;

	// Code canton
	private Long canton;

	// Composition communale du canton
	private Long typct;

	// Code de la commune bureau centraliseur du canton
	private String burcentral;

	// Type de nom en clair
	private String tncc;

	// Article en majuscules
	private String artmaj;

	// Libellé en lettres majuscules
	private String ncc;

	// Article enrichi
	private String artmin;

	// Libellé enrichi
	private String nccenr;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(nullable = false)
	public Long getRegion() {
		return region;
	}

	public void setRegion(Long region) {
		this.region = region;
	}

	@Column(nullable = false)
	public String getDep() {
		return dep;
	}

	public void setDep(String dep) {
		this.dep = dep;
	}

	@Column(nullable = false)
	public Long getCanton() {
		return canton;
	}

	public void setCanton(Long canton) {
		this.canton = canton;
	}

	@Column(nullable = false)
	public Long getTypct() {
		return typct;
	}

	public void setTypct(Long typct) {
		this.typct = typct;
	}

	@Column(nullable = false)
	public String getBurcentral() {
		return burcentral;
	}

	public void setBurcentral(String burcentral) {
		this.burcentral = burcentral;
	}

	@Column(nullable = false)
	public String getTncc() {
		return tncc;
	}

	public void setTncc(String tncc) {
		this.tncc = tncc;
	}

	@Column(nullable = true)
	public String getArtmaj() {
		return artmaj;
	}

	public void setArtmaj(String artmaj) {
		this.artmaj = artmaj;
	}

	@Column(nullable = false)
	public String getNcc() {
		return ncc;
	}

	public void setNcc(String ncc) {
		this.ncc = ncc;
	}

	@Column(nullable = true)
	public String getArtmin() {
		return artmin;
	}

	public void setArtmin(String artmin) {
		this.artmin = artmin;
	}

	@Column(nullable = false)
	public String getNccenr() {
		return nccenr;
	}

	public void setNccenr(String nccenr) {
		this.nccenr = nccenr;
	}

}
