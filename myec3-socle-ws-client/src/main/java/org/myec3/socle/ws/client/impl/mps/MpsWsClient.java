package org.myec3.socle.ws.client.impl.mps;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.ws.rs.core.Response.Status;

import org.myec3.socle.core.domain.model.Company;
import org.myec3.socle.core.domain.model.Establishment;
import org.myec3.socle.core.domain.model.Person;
import org.myec3.socle.core.domain.model.enums.CompanyINSEECat;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.service.InseeLegalCategoryService;
import org.myec3.socle.ws.client.CompanyWSinfo;
import org.myec3.socle.ws.client.impl.mps.response.ResponseEntreprises;
import org.myec3.socle.ws.client.impl.mps.response.ResponseEtablissements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MpsWsClient implements CompanyWSinfo {

	private static Logger logger = LoggerFactory.getLogger(MpsWsClient.class);

	ResourceBundle bundle = ResourceBundle.getBundle("mpsWs");

	private String mpsUrlEntreprises = bundle.getString("mpsUrlEntreprises");
	private String mpsUrlEtablissement = bundle.getString("mpsUrlEtablissement");
	private String mpsToken = bundle.getString("mpsToken");
	private String mpsContext = bundle.getString("mpsContext");
	private String mpsRecipient = bundle.getString("mpsRecipient");
	private String mpsObject = bundle.getString("mpsObject");

	// use Enterprise WebService
	public ResponseEntreprises getInfoEntreprises(String companySiren) {
		String baseUrl = mpsUrlEntreprises;
		ObjectFactory factory = new ObjectFactory();
		String url = baseUrl + companySiren + "?" + mpsToken + mpsContext + mpsRecipient + companySiren + mpsObject;

		ResponseEntreprises response = factory.createResponseEntreprises();
		HttpURLConnection conn = null;

		try {
			URL urlTemp = new URL(url);

			logger.info("Asking Entreprises Webservice on url : " + url);
			conn = (HttpURLConnection) urlTemp.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(8000); // set timeout to 8 seconds

			// Get the raw MPS response
			InputStream responseTmp = conn.getInputStream();

			// Convert JSON response to string for Jackson parsing
			String jsonReply = this.getStringFromInputStream(responseTmp);
			ObjectMapper mapper = new ObjectMapper();

			// Do not stack on unknown / null values
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

			// Match MPS response with ReponseEntreprise class
			response = mapper.readValue(jsonReply, ResponseEntreprises.class);

			// Temporary debug to view response content
			logger.info("Json representation : " + response.toString());

			// close connection after retrieving query result (avoid time wait close
			// connection on server)
			conn.disconnect();

		} catch (MalformedURLException e) {
			logger.error("Not a valid URL: " + url, e);
		} catch (IOException e) {
			logger.error("Unable to connect to : " + url, e);
		} finally {
			// close connection properly if not closed yet
			if (conn != null) {
				conn.disconnect();
			}
		}
		return response;

	}

	// use Establishments WebService
	private ResponseEtablissements getInfoEtablissements(String siret_siege_social) {
		String baseUrl = mpsUrlEtablissement;
		ObjectFactory factory = new ObjectFactory();
		String url = baseUrl + siret_siege_social + "?" + mpsToken + mpsContext + mpsRecipient + siret_siege_social
				+ mpsObject;
		;

		ResponseEtablissements response = factory.createResponseEtablissements();
		HttpURLConnection conn = null;

		try {
			URL urlTemp = new URL(url);

			logger.info("Asking Etablissements Webservice on url : " + url);
			conn = (HttpURLConnection) urlTemp.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(8000); // set timeout to 5 seconds

			// Get the raw MPS response
			InputStream responseTmp = conn.getInputStream();

			// Convert JSON response to string for Jackson parsing
			String jsonReply = this.getStringFromInputStream(responseTmp);
			ObjectMapper mapper = new ObjectMapper();

			// Do not stack on unknown / null values
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

			// Match MPS response with ReponseEntreprise class
			response = mapper.readValue(jsonReply, ResponseEtablissements.class);

			// Temporary debug to view response content
			logger.info("ResponseEtablissements DTO representation : " + response.getEtablissement().toString());

			// close connection after retrieving query result (avoid time wait close
			// connection on server)
			conn.disconnect();

		} catch (MalformedURLException e) {
			logger.error("Not a valid URL: " + url);
		} catch (SocketTimeoutException e) { // MPS too long to aswer
			logger.error("Timeout. Unable to connect to : " + url);
			response.setGateway_error_code(Status.INTERNAL_SERVER_ERROR);
		} catch (FileNotFoundException e) { // No establishment for this siret
			logger.error("Unable to connect to : " + url + ". Wrong Siret");
			response.setGateway_error_code(Status.NOT_FOUND);
		} catch (IOException e) { // default
			logger.error("Unable to connect to : " + url);
			response.setGateway_error_code(Status.INTERNAL_SERVER_ERROR);
		} finally {
			// close connection properly if not closed yet
			if (conn != null) {
				conn.disconnect();
			}
		}
		return response;
	}

	public Company updateCompanyInfo(Company company, InseeLegalCategoryService inseeLegalCategoryService) {
		logger.info("Update Company " + company.getSiren() + " Information with the MPS WS ");

		// Call MPS to get the Company informations
		ResponseEntreprises responseEntreprises = this.getInfoEntreprises(company.getSiren());
		company = responseEntreprises.getEntreprise();

		this.setCompanyMissingFields(responseEntreprises, company, inseeLegalCategoryService);

		return company;
	}

	public Establishment updateEstablishmentInfo(Establishment establishment, Company company) throws IOException {
		if (establishment.getSiret() != null) {
			String finalAddresse = "";
			logger.info("update establishment information : " + establishment.getSiret());

			// Call MPS to get the Establishment informations
			ResponseEtablissements responseEtablissements = this.getInfoEtablissements(establishment.getSiret());

			if (responseEtablissements.getGateway_error_code() != null) {
				switch (responseEtablissements.getGateway_error_code()) {
				case INTERNAL_SERVER_ERROR:
					throw new IOException();
				case NOT_FOUND:
					throw new FileNotFoundException();
				default:
					throw new IOException();
				}

			}

			establishment = responseEtablissements.getEtablissement();
			establishment.setCompany(company);

			if (responseEtablissements.getEtablissement().getDiffusableInformations() == null
					|| responseEtablissements.getEtablissement().getDiffusableInformations().equals(Boolean.TRUE)) {
				establishment.setDiffusableInformations(Boolean.TRUE);
			}

			if (responseEtablissements.getEtablissement().getAddress().getStreetNumber() != null) {
				finalAddresse += responseEtablissements.getEtablissement().getAddress().getStreetNumber();
			}

			if (responseEtablissements.getEtablissement().getAddress().getStreetType() != null) {
				if (finalAddresse != "") {
					finalAddresse += " " + responseEtablissements.getEtablissement().getAddress().getStreetType();
				} else {
					finalAddresse += responseEtablissements.getEtablissement().getAddress().getStreetType();
				}
			}

			if (responseEtablissements.getEtablissement().getAddress().getStreetName() != null) {
				if (finalAddresse != "") {
					finalAddresse += " " + responseEtablissements.getEtablissement().getAddress().getStreetName();
				} else {
					finalAddresse += responseEtablissements.getEtablissement().getAddress().getStreetName();
				}
			}

			establishment.getAddress().setPostalAddress(finalAddresse);
			if (responseEtablissements.getEtablissement().getSiret() != null) {
				establishment.setNic(responseEtablissements.getEtablissement().getSiret().substring(9, 14));
			}

		} else {
			logger.info("SIRET invalid or null !");
		}
		return establishment;
	}

	public Establishment getEstablishment(String siret) throws IOException {
		Establishment establishment = new Establishment();
		if (siret != null) {
			logger.info("Getting establishment information : " + siret);

			// Call MPS to get the Establishment informations
			ResponseEtablissements responseEtablissements = this.getInfoEtablissements(siret);

			if (responseEtablissements.getGateway_error_code() != null) {
				switch (responseEtablissements.getGateway_error_code()) {
				case INTERNAL_SERVER_ERROR:
					throw new IOException();
				case NOT_FOUND:
					throw new FileNotFoundException();
				default:
					throw new IOException();
				}

			} else {
				establishment = responseEtablissements.getEtablissement();
				this.setEstablishmentMissingFields(responseEtablissements, establishment);
			}
		} else {
			logger.info("SIRET invalid or null !");
		}
		return establishment;
	}

	public Company updateCompany(String siren, InseeLegalCategoryService inseeLegalCategoryService) {
		Company company = new Company();
		if (siren != null) {
			logger.info("Updating company : " + siren);

			// Call MPS to get the Company informations
			ResponseEntreprises responseEntreprise = this.getInfoEntreprises(siren);

			company = responseEntreprise.getEntreprise();
			this.setCompanyMissingFields(responseEntreprise, company, inseeLegalCategoryService);

		} else {
			logger.info("SIREN invalid or null !");
		}
		return company;
	}

	private static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}

	private void setCompanyMissingFields(ResponseEntreprises responseEntreprises, Company company,
			InseeLegalCategoryService inseeLegalCategoryService) {

		// diffusable information
		if (responseEntreprises.getEtablissement_siege().getDiffusableInformations() == null
				|| responseEntreprises.getEtablissement_siege().getDiffusableInformations().equals(Boolean.TRUE)) {
			company.setDiffusableInformations(Boolean.TRUE);
		}

		// nic
		if (company.getNic() == null || company.getNic().isEmpty()) {
			company.setNic(responseEntreprises.getEtablissement_siege().getSiret().substring(9, 14));
		}

		// postal address
		if (company.getAddress().getPostalAddress() == null) {
			String postalAddress = "";

			if (responseEntreprises.getEtablissement_siege().getAddress().getStreetNumber() != null) {
				postalAddress += responseEntreprises.getEtablissement_siege().getAddress().getStreetNumber() + " ";
			}

			if (responseEntreprises.getEtablissement_siege().getAddress().getStreetType() != null) {
				postalAddress += responseEntreprises.getEtablissement_siege().getAddress().getStreetType() + " ";
			}

			if (responseEntreprises.getEtablissement_siege().getAddress().getStreetName() != null) {
				postalAddress += responseEntreprises.getEtablissement_siege().getAddress().getStreetName();
			}
			company.getAddress().setPostalAddress(postalAddress);
		}

		// postal code
		if (company.getAddress().getPostalCode() == null) {
			company.getAddress()
					.setPostalCode(responseEntreprises.getEtablissement_siege().getAddress().getPostalCode());
		}

		// city
		if (company.getAddress().getCity() == null) {
			company.getAddress().setCity(responseEntreprises.getEtablissement_siege().getAddress().getCity());
		}

		// canton
		if (company.getAddress().getCanton() == null) {
			company.getAddress().setCanton("Aucun");
		}

		// country
		if (company.getAddress().getCountry() == null) {
			company.getAddress().setCountry(Country.FR);
		}

		// raison sociale
		if (responseEntreprises.getEntreprise().getName() != null) {
			company.setLabel(responseEntreprises.getEntreprise().getName());
		} else {
			logger.info("Label is null");
		}

		// catégorie juridique
		if (responseEntreprises.getEntreprise().getLegalCategoryString() != null) {
			String companyLegalCategory = inseeLegalCategoryService
					.findParentByLabel(responseEntreprises.getEntreprise().getLegalCategoryString());
			if (companyLegalCategory != null) {
				CompanyINSEECat companyINSEECat = CompanyINSEECat.getByValue(companyLegalCategory);
				if (companyINSEECat != null) {
					company.setLegalCategory(companyINSEECat);
				} else {
					// set to other InseeLegalCategory because we are not able to
					// retrieve a known INSEECat
					company.setLegalCategory(CompanyINSEECat.AUTRE);
					logger.info("Unknown InseeLegalCategory ... Set InseeLegalCategory to default OTHER");
				}
			}
		} else {
			logger.info("Company InseeLegalCategory is null. Setting it to 'Autre'");
			CompanyINSEECat companyINSEECat = CompanyINSEECat.getByValue("AUTRE");
			company.setLegalCategory(companyINSEECat);
		}

		// code naf
		if (responseEntreprises.getEtablissement_siege() != null
				&& responseEntreprises.getEtablissement_siege().getApeCode() != null
				&& responseEntreprises.getEtablissement_siege().getApeCode().length() == 5) {
			company.setApeCode(responseEntreprises.getEtablissement_siege().getApeCode());
			if (responseEntreprises.getEtablissement_siege().getApeNafLabel() != null) {
				company.setApeNafLabel(responseEntreprises.getEtablissement_siege().getApeNafLabel());
			}
		} else {
			logger.info("Company NAF is invalid or null");
		}

		// foreign identifier ?
		if (responseEntreprises.getEntreprise().getSiren() != null) {
			company.setForeignIdentifier(Boolean.FALSE);
		}

		// Moral Responsible
		for (Person person : company.getResponsibles()) {
			if (person.getName() == null && (person.getMoralName() == null || person.getMoralName().isEmpty())) {
				person.setName(person.getFirstname() + " " + person.getLastname());

			}
			if (person.getMoralName() != null && !person.getMoralName().isEmpty()) {
				person.setFirstname(person.getMoralName());
				person.setName(person.getMoralName());
			}
			person.setLabel(person.getName());
		}
	}

	private void setEstablishmentMissingFields(ResponseEtablissements responseEtablissements,
			Establishment establishment) {
		// postal address
		if (establishment.getAddress().getPostalAddress() == null) {
			String postalAddress = "";

			if (responseEtablissements.getEtablissement().getAddress().getStreetNumber() != null) {
				postalAddress += responseEtablissements.getEtablissement().getAddress().getStreetNumber() + " ";
			}

			if (responseEtablissements.getEtablissement().getAddress().getStreetType() != null) {
				postalAddress += responseEtablissements.getEtablissement().getAddress().getStreetType() + " ";
			}

			if (responseEtablissements.getEtablissement().getAddress().getStreetName() != null) {
				postalAddress += responseEtablissements.getEtablissement().getAddress().getStreetName();
			}
			establishment.getAddress().setPostalAddress(postalAddress);
		}

		// postal code
		if (establishment.getAddress().getPostalCode() == null) {
			establishment.getAddress()
					.setPostalCode(responseEtablissements.getEtablissement().getAddress().getPostalCode());
		}

		// city
		if (establishment.getAddress().getCity() == null) {
			establishment.getAddress().setCity(responseEtablissements.getEtablissement().getAddress().getCity());
		}

		// country
		if (establishment.getAddress().getCountry() == null) {
			establishment.getAddress().setCountry(Country.FR);
		}

		// canton
		if (establishment.getAddress().getCanton() == null) {
			establishment.getAddress().setCanton("Aucun");
		}
	}
}
