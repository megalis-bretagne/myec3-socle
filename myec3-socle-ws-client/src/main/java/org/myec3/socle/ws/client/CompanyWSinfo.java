package org.myec3.socle.ws.client;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.service.InseeLegalCategoryService;

public interface CompanyWSinfo {

	public Company updateCompanyInfo(Company company,
			InseeLegalCategoryService inseeLegalCategoryService);

	public Establishment updateEstablishmentInfo(Establishment establishment,
			Company company) throws FileNotFoundException, IOException;

	public Establishment getEstablishment(String siret)
			throws FileNotFoundException, IOException;

	public Company updateCompany(String siren, InseeLegalCategoryService inseeLegalCategoryService);
}
