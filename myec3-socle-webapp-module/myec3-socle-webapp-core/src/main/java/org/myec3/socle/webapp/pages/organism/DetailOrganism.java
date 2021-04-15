/**
 * Copyright (c) 2011 Atos Bourgogne
 * <p>
 * This file is part of MyEc3.
 * <p>
 * MyEc3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as published by
 * the Free Software Foundation.
 * <p>
 * MyEc3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.myec3.socle.webapp.pages.organism;

import lombok.Getter;
import lombok.Setter;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.core.domain.model.Organism;
import org.myec3.socle.core.domain.model.OrganismStatus;
import org.myec3.socle.core.service.OrganismService;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.pages.Index;

import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Page used to display organism's informations {@link Organism}<br />
 *
 * Corresponding tapestry template file is :
 * src/main/resources/org/myec3/socle/webapp/pages/organism/DetailOrganism.tml<br
 * />
 *
 * @see securityMyEc3Context.xml to know profiles authorized to display this
 *      page<br />
 *
 * @author Anthony Colas <anthony.j.colas@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 *
 */
public class DetailOrganism extends AbstractPage {

  @Getter
  @Setter
  @Persist(PersistenceConstants.FLASH)
  private String successMessage;

  /**
   * Business Service providing methods and specifics operations on
   * {@link Organism} objects
   */
  @Inject
  @Named("organismService")
  private OrganismService organismService;

  @Getter
  @Setter
  private Organism organism;

  // Only used in Tapestry
  @Property
  private OrganismStatus organismStatusLoop;

  @Property
  private List<OrganismStatus> organismStatusList;

  @OnEvent(EventConstants.ACTIVATE)
  public void Activation() {
    super.initUser();
  }

  @OnEvent(EventConstants.ACTIVATE)
  public Object onActivate() {
    return Index.class;
  }

  @OnEvent(EventConstants.ACTIVATE)
  public Object onActivate(Long id) {
    this.organism = this.organismService.findOne(id);

    organismStatusList = new ArrayList<>(this.organism.getOrganismStatus());
    // Sort the list to print status according to their date in tapestry
    Collections.sort(organismStatusList, new Comparator<OrganismStatus>() {
      public int compare(OrganismStatus o1, OrganismStatus o2) {
        return o1.getDate().compareTo(o2.getDate());
      }
    });

    if (null == this.organism) {
      return Index.class;
    }

    // Check if loggedUser can access at this organism details
    return this.hasRights(this.organism);
  }

  @OnEvent(EventConstants.PASSIVATE)
  public Long onPassivate() {
    return (this.organism != null) ? this.organism.getId() : null;
  }

  public String getLegalCategoryLabel() {
    return this.getMessages().get(this.organism.getLegalCategory().name());
  }

  public String getApeCodeLabel() {
    return this.getMessages().get(this.organism.getApeCode().name());
  }

  public SimpleDateFormat getDateFormat() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    return dateFormat;
  }
}
