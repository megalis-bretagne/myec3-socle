package org.myec3.socle.ws.client;

import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.json.ApiGouvActivitePrincipale;
import org.myec3.socle.core.service.InseeLegalCategoryService;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface CompanyWSinfo {

    Company updateCompanyInfo(Company company,
                              InseeLegalCategoryService inseeLegalCategoryService);

    Establishment updateEstablishmentInfo(Establishment establishment,
                                          Company company) throws FileNotFoundException, IOException;

    Establishment getEstablishment(String siret)
            throws FileNotFoundException, IOException;

    Company updateCompany(String siren, InseeLegalCategoryService inseeLegalCategoryService);

    /**
     * Check if company with siren exist
     *
     * @param company
     * @return true is exist
     */
    boolean companyExist(Company company);

    /**
     * Check if establishment with siret exist
     *
     * @param establishment
     * @return true is exist
     */
    boolean establishmentExist(Establishment establishment);

}
