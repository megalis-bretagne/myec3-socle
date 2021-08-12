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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.myec3.socle.core.domain.model.adapter.DateAdapter;
import org.myec3.socle.core.domain.model.enums.Article;
import org.myec3.socle.core.domain.model.enums.OrganismINSEECat;
import org.myec3.socle.core.domain.model.enums.OrganismNafCode;
import org.myec3.socle.core.domain.model.enums.StructureINSEECat;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;

/**
 * This class represents a public organism, i.e. a public structure, city,
 * department, etc. <br/>
 *
 * An organism contains {@link AgentProfile} and
 * {@link OrganismDepartment}<br />
 *
 * This class is synchronized.<br />
 *
 * This class is audited by global audit mechanism<br />
 *
 * @author Loïc Frering <loic.frering@atosorigin.com>
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Entity
@XmlRootElement
@Audited
@Synchronized
public class Organism extends Structure {

	private static final long serialVersionUID = -1048517499855786930L;
	private Boolean member;
	private Article article;
	private OrganismNafCode apeCode;
	private OrganismINSEECat legalCategory;
	private String college;
	private Integer contributionAmount;
	private Integer officialPopulation;
	private Integer budget;
	private List<OrganismDepartment> departments = new ArrayList<OrganismDepartment>();
	private Customer customer;
	private String apiKey;
	private String nic;
	private Set<OrganismStatus> organismStatuses = new HashSet<OrganismStatus>();
	private Date ideoSignatureDate;

	/**
	 * Default constructor
	 */
	public Organism() {
		super();
	}

	/**
	 * Membership of an organism
	 *
	 * @return true if the organism is member of MyEc3, false otherwise
	 */
	@NotNull
	@Column(name = "membership", nullable = false)
	@XmlElement(required = true)
	public Boolean getMember() {
		return member;
	}

	public void setMember(Boolean member) {
		this.member = member;
	}

	/**
	 * Article of the organism. Allows to identify the gender of the organism name.
	 * <br/>
	 * <br/>
	 * For possible values, see {@link Article}
	 *
	 * @return the article of the organism
	 * @see Article
	 */
	@Enumerated(EnumType.STRING)
	@XmlElement(required = false)
	@Column(nullable = true)
	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	/**
	 * Ape code (called also Naf code) of the organism (issued by INSEE). <br/>
	 * <br/>
	 * For possible values, see {@link OrganismNafCode}
	 *
	 * @return five-character code
	 * @see OrganismNafCode
	 */
	@Enumerated(EnumType.STRING)
	@XmlElement(required = false)
	@Column(nullable = true)
	public OrganismNafCode getApeCode() {
		return apeCode;
	}

	public void setApeCode(OrganismNafCode apeCode) {
		this.apeCode = apeCode;
	}

	/**
	 * INSEE legal category associated to this organism. <br/>
	 * <br/>
	 * For possible values, see {@link OrganismINSEECat}
	 *
	 * @return the legal category of the organism.
	 * @see OrganismINSEECat
	 */
	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	@XmlElement(required = true)
	public OrganismINSEECat getLegalCategory() {
		return legalCategory;
	}

	public void setLegalCategory(OrganismINSEECat legalCategory) {
		this.legalCategory = legalCategory;
	}

	/**
	 * Organism college category. A college is a group of organisms holding commons
	 * characteristics. <br/>
	 * <br/>
	 * For possible values, see {@link OrganismCollegeCat}
	 *
	 * @return the college category for this organsm
	 * @see OrganismCollegeCat
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public String getCollege() {
		return college;
	}

	public void setCollege(String college) {
		this.college = college;
	}

	public void setContributionAmount(Integer contributionAmount) {
		this.contributionAmount = contributionAmount;
	}

	/**
	 * Amount of the contribution payed, each year, by the organism to the MyEc3
	 * organization.
	 *
	 * @return the amount for this organism.
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public Integer getContributionAmount() {
		return contributionAmount;
	}

	public void setOfficialPopulation(Integer officialPopulation) {
		this.officialPopulation = officialPopulation;
	}

	/**
	 * Official citizen population directly or indirectly managed by this organism.
	 *
	 * @return the official population number.
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public Integer getOfficialPopulation() {
		return officialPopulation;
	}

	public void setBudget(Integer budget) {
		this.budget = budget;
	}

	/**
	 * Buget for this organism, for one year.
	 *
	 * @return the budget of this organism.
	 */
	@Column(nullable = true)
	@XmlElement(required = false)
	public Integer getBudget() {
		return budget;
	}

	/**
	 * List of departments (or services) that form the organism hierarchical
	 * structure.
	 *
	 * @return the list of departments for this organism
	 */
	@OneToMany(mappedBy = "organism", cascade = { CascadeType.ALL })
	@XmlTransient
	@JsonIgnore
	public List<OrganismDepartment> getDepartments() {
		return departments;
	}

	public void setDepartments(List<OrganismDepartment> departments) {
		this.departments = departments;
	}

	/**
	 * Add a new department to this organism, even if the department is already in
	 * list.
	 *
	 * @param department : the new department to add
	 * @return the modified organism
	 */
	public Organism addDepartment(OrganismDepartment department) {
		this.departments.add(department);
		return this;
	}

	/**
	 * Add new departments to this organism, even if departments are already in
	 * list.
	 *
	 * @param departments : list of departments to add
	 * @return the modified organism
	 */
	public Organism addDepartments(List<OrganismDepartment> departments) {
		this.departments.addAll(departments);
		return this;
	}

	/**
	 * Remove a given department from the organism departments list. If the
	 * department does not exist, nothing is done
	 *
	 * @param department : department to remove
	 * @return the modified organism
	 */
	public Organism removeDepartment(OrganismDepartment department) {
		this.departments.remove(department);
		return this;
	}

	/**
	 * Remove all departments from organism departments list
	 *
	 * @return the modified organism
	 */
	public Organism clearDepartments() {
		this.departments.clear();
		return this;
	}

	/**
	 *
	 * @return all the statutes and the linked date of the organism
	 */
	@OneToMany(mappedBy = "organism", cascade = { CascadeType.ALL }, orphanRemoval = true)
	@XmlElementWrapper(name = "organismStatuses", required = false)
	@XmlElement(required = false)
	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonIgnore
	public Set<OrganismStatus> getOrganismStatus() {
		return organismStatuses;
	}

	public void setOrganismStatus(Set<OrganismStatus> organismStatuses) {
		this.organismStatuses = organismStatuses;
	}

	/**
	 * Add a status and the linked date to an organism
	 * 
	 * @param organismStatus
	 * @return the modified organism
	 */
	public Organism addOrganismStatus(OrganismStatus organismStatus) {
		this.organismStatuses.add(organismStatus);
		return this;
	}

	/**
	 * Remove a status and the linked date from an organism
	 * 
	 * @param organismStatus
	 * @return the modified organism
	 */
	public Organism removeOrganismStatus(OrganismStatus organismStatus) {
		this.organismStatuses.remove(organismStatus);
		return this;
	}

	/**
	 * Remove all organismStatus from organismStatus set
	 *
	 * @return the modified organism
	 */
	public void clearOrganismStatus() {
		this.organismStatuses.clear();
	}

	/**
	 * Add a list of organismStatus to an organism
	 * 
	 * @param organismStatus
	 * @return the modified organism
	 */
	public void addAllOrganismStatus(List<OrganismStatus> organismStatus) {
		this.organismStatuses.addAll(organismStatus);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return false in any case
	 */
	@Transient
	@Override
	public boolean isCompany() {
		return Boolean.FALSE;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return true in any case
	 */
	@Transient
	@Override
	public boolean isOrganism() {
		return Boolean.TRUE;
	}

	/**
	 * @return the customer responsible of the organism.
	 */
	@ManyToOne
	@JoinColumn(nullable = false)
	@XmlElement(required = true)
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transient
	@Override
	public StructureINSEECat getStrutureLegalCategory() {
		return this.getLegalCategory();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStrutureLegalCategory(StructureINSEECat strutureLegalCategory) {
		Assert.isInstanceOf(OrganismINSEECat.class, strutureLegalCategory);
		this.setLegalCategory((OrganismINSEECat) strutureLegalCategory);
	}

	@Column(nullable = true)
	@XmlElement(required = false)
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	@Column(nullable = true)
	@XmlElement(required = false)
	public String getNic() {
		return this.nic;
	}

	public void setNic(String organismNic) {
		this.nic = organismNic;
	}

	@Column(nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	@XmlElement(required = false)
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getIdeoSignatureDate() {
		return ideoSignatureDate;
	}

	public void setIdeoSignatureDate(Date beginMembershipDate) {
		this.ideoSignatureDate = beginMembershipDate;
	}
}
