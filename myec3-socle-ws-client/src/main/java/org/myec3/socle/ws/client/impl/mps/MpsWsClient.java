package org.myec3.socle.ws.client.impl.mps;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.CompanyINSEECat;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.domain.model.json.*;
import org.myec3.socle.core.service.InseeLegalCategoryService;
import org.myec3.socle.core.sync.api.HttpStatus;
import org.myec3.socle.ws.client.CompanyWSinfo;
import org.myec3.socle.ws.client.impl.mps.response.ResponseEtablissement;
import org.myec3.socle.ws.client.impl.mps.response.ResponseMandataires;
import org.myec3.socle.ws.client.impl.mps.response.ResponseUniteLegale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

public class MpsWsClient implements CompanyWSinfo {

    public static final String NAFREV_2 = "NAFRev2";
    private static Logger logger = LoggerFactory.getLogger(MpsWsClient.class);

    ResourceBundle bundle = ResourceBundle.getBundle("mpsWs");

    private final String MPS_URL_MANDATAIRES = bundle.getString("mpsUrlMandataires");
    private final String MPS_URL_ENTREPRISES = bundle.getString("mpsUrlEntreprises");
    private final String MPS_URL_ETABLISSEMENT = bundle.getString("mpsUrlEtablissement");
    private final String MPS_TOKEN = bundle.getString("mpsToken");
    private final String MPS_CONTEXT = bundle.getString("mpsContext");
    private final String MPS_OBJECT = bundle.getString("mpsObject");

    private final String MPS_RECIPIENT = bundle.getString("mpsRecipient");

    // use Enterprise WebService
    public ResponseUniteLegale getInfoEntreprises(String companySiren) throws IOException {

        ObjectFactory factory = new ObjectFactory();
        String url = this.getUrlEntreprise(companySiren);
        ResponseUniteLegale response;
        logger.info("Create connection for :" + url);
        HttpURLConnection conn;
        try {
            URL urlTemp = new URL(url);
                /*   Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("172.31.6.15", 3128));
            conn = (HttpURLConnection) new URL(url).openConnection(proxy);*/
            conn = (HttpURLConnection) urlTemp.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty(HttpHeaders.AUTHORIZATION, "Bearer " + MPS_TOKEN);
            conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json");
            conn.setConnectTimeout(8000); // set timeout to 8 seconds
        } catch (MalformedURLException e) {
            logger.error("Not a valid URL: " + url, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unable to connect to : " + url, e);
            throw e;
        }
        logger.info("Asking Entreprises Webservice on url : ");

        // Get the raw MPS response
        InputStream responseTmp = conn.getInputStream();
        logger.info("Convert JSON response to string for Jackson parsing");
        // Convert JSON response to string for Jackson parsing
        String jsonReply = this.getStringFromInputStream(responseTmp);
        // Temporary debug to view response content
        logger.info("Json representation : " + jsonReply);
        ObjectMapper mapper = new ObjectMapper();

        // Do not stack on unknown / null values
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Match MPS response with ReponseEntreprise class
        response = mapper.readValue(jsonReply, ResponseUniteLegale.class);
        logger.info("ResponseUniteLegale DTO representation : " + response.toString());
        conn.disconnect();
        return response;
    }

    public ResponseMandataires getInfoMandataires(String companySiren) throws IOException {

        ObjectFactory factory = new ObjectFactory();
        String url = this.getUrlMandataires(companySiren);
        ResponseMandataires response;
        logger.info("Create connection for :" + url);
        HttpURLConnection conn;
        try {
            URL urlTemp = new URL(url);
         /*   Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("172.31.6.15", 3128));
            conn = (HttpURLConnection) new URL(url).openConnection(proxy);*/
            conn = (HttpURLConnection) urlTemp.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty(HttpHeaders.AUTHORIZATION, "Bearer " + MPS_TOKEN);
            conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json");
            conn.setConnectTimeout(8000); // set timeout to 8 seconds
        } catch (MalformedURLException e) {
            logger.error("Not a valid URL: " + url, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unable to connect to : " + url, e);
            throw e;
        }

        logger.info("Asking Mandataires Webservice on url");
        // Get the raw MPS response
        InputStream responseTmp = conn.getInputStream();
        // Convert JSON response to string for Jackson parsing
        logger.info("Convert JSON response to string for Jackson parsing");
        String jsonReply = this.getStringFromInputStream(responseTmp);
        // Temporary debug to view response content
        logger.info("Json representation : " + jsonReply);
        ObjectMapper mapper = new ObjectMapper();
        // Do not stack on unknown / null values
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // Match MPS response with ReponseEntreprise class
        response = mapper.readValue(jsonReply, ResponseMandataires.class);
        logger.info("ResponseMandataires DTO representation : " + response.toString());
        // close connection properly if not closed yet
        conn.disconnect();
        return response;
    }

    // use Establishments WebService
    public ResponseEtablissement getInfoEtablissements(String siegeSocialSiret) throws IOException {

        ObjectFactory factory = new ObjectFactory();
        String url = this.getUrlEtablissement(siegeSocialSiret);
        ResponseEtablissement response;
        logger.info("Create connection for :" + url);
        HttpURLConnection conn;
        try {
            URL urlTemp = new URL(url);
                 /*   Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("172.31.6.15", 3128));
            conn = (HttpURLConnection) new URL(url).openConnection(proxy);*/
            conn = (HttpURLConnection) urlTemp.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty(HttpHeaders.AUTHORIZATION, "Bearer " + MPS_TOKEN);
            conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json");
            conn.setConnectTimeout(8000); // set timeout to 8 seconds
        } catch (MalformedURLException e) {
            logger.error("Not a valid URL: " + url, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unable to connect to : " + url, e);
            throw e;
        }
        logger.info("Asking Etablissements Webservice on url");
        InputStream responseTmp = conn.getInputStream();
        // Convert JSON response to string for Jackson parsing
        logger.info("Convert JSON response to string for Jackson parsing");
        String jsonReply = this.getStringFromInputStream(responseTmp);
        // Temporary debug to view response content
        logger.info("Json representation : " + jsonReply);
        ObjectMapper mapper = new ObjectMapper();
        // Do not stack on unknown / null values
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // Match MPS response with ReponseEntreprise class
        response = mapper.readValue(jsonReply, ResponseEtablissement.class);
        // Temporary debug to view response content
        logger.info("ResponseEtablissements DTO representation : " + response.toString());
        // close connection properly if not closed yet
        conn.disconnect();
        return response;
    }

    @Override
    public Company updateCompanyInfo(Company company, InseeLegalCategoryService inseeLegalCategoryService) {
        logger.info("Update Company " + company.getSiren() + " Information with the MPS WS ");
        // Call MPS to get the Company informations
        logger.info("Search for entreprise matching siren : " + company.getSiren());
        try {
            ResponseUniteLegale responseEntreprises = responseEntreprises = this.getInfoEntreprises(company.getSiren());
            logger.info("Search for 'unité legale' matching siren : " + company.getSiren());
            ResponseEtablissement responseEtablissement = this.getInfoEtablissements(responseEntreprises.getData().getSiretSiegeSocial());
            logger.info("Search for mandataires matching siren : " + company.getSiren());
            List<ApiGouvMandataireSocial> mandataireSocials = null;
            try {
                ResponseMandataires responseMandataires = this.getInfoMandataires(company.getSiren());
                mandataireSocials = responseMandataires.getData();
            }catch (Exception e){
                logger.warn("Api do not found any matching mandataire :", e);
            }
            company = convertUniteLegaleToCompany(responseEntreprises.getData(), responseEtablissement.getData(), responseEtablissement.getMeta(), mandataireSocials);
            this.setCompanyMissingFields(responseEntreprises, responseEtablissement, company, inseeLegalCategoryService);
        } catch (Exception e) {
            logger.error("Error happen during api.gouv.fr call :", e);
        }
        logger.info("Consolidates company : " + company.getSiren());
        return company;

    }

    @Override
    public Establishment updateEstablishmentInfo(Establishment establishment, Company company) throws IOException {
        if (establishment.getSiret() != null) {
            String finalAddresse = "";
            logger.info("update establishment information : " + establishment.getSiret());

            // Call MPS to get the Establishment informations
            ResponseEtablissement responseEtablissements = this.getInfoEtablissements(establishment.getSiret());

            establishment = convertEtablissementToEtablishment(responseEtablissements.getData(), responseEtablissements.getMeta());
            establishment.setCompany(company);

            if (responseEtablissements.getData().getDiffusableCommercialement() == null
                    || responseEtablissements.getData().getDiffusableCommercialement().equals(Boolean.TRUE)) {
                establishment.setDiffusableInformations(Boolean.TRUE);
            }

            if (responseEtablissements.getData().getAdresse().getNumeroVoie() != null) {
                finalAddresse += responseEtablissements.getData().getAdresse().getNumeroVoie();
            }

            if (responseEtablissements.getData().getAdresse().getTypeVoie() != null) {
                if (finalAddresse != "") {
                    finalAddresse += " " + responseEtablissements.getData().getAdresse().getTypeVoie();
                } else {
                    finalAddresse += responseEtablissements.getData().getAdresse().getTypeVoie();
                }
            }

            if (responseEtablissements.getData().getAdresse().getLibelleVoie() != null) {
                if (finalAddresse != "") {
                    finalAddresse += " " + responseEtablissements.getData().getAdresse().getLibelleVoie();
                } else {
                    finalAddresse += responseEtablissements.getData().getAdresse().getLibelleVoie();
                }
            }

            establishment.getAddress().setPostalAddress(finalAddresse);
            if (responseEtablissements.getData().getSiret() != null) {
                establishment.setNic(responseEtablissements.getData().getSiret().substring(9, 14));
            }

        } else {
            logger.info("SIRET invalid or null !");
        }
        return establishment;
    }

    @Override
    public Establishment getEstablishment(String siret) throws IOException {
        Establishment establishment = new Establishment();
        if (siret != null) {
            logger.info("Getting establishment information : " + siret);

            // Call MPS to get the Establishment informations
            ResponseEtablissement responseEtablissements = this.getInfoEtablissements(siret);
            establishment = convertEtablissementToEtablishment(responseEtablissements.getData(), responseEtablissements.getMeta());
            this.setEstablishmentMissingFields(responseEtablissements, establishment);
        } else {
            logger.info("SIRET invalid or null !");
        }
        return establishment;
    }


    @Override
    public Company updateCompany(String siren, InseeLegalCategoryService inseeLegalCategoryService) {
        Company company = new Company();
        if (siren != null) {
            logger.info("Updating company : " + siren);
            company.setSiren(siren);
            try {
                // Call MPS to get the Company informations
                ResponseUniteLegale responseEntreprises = responseEntreprises = this.getInfoEntreprises(company.getSiren());
                logger.info("Search for 'unité legale' matching siren : " + company.getSiren());
                ResponseEtablissement responseEtablissement = this.getInfoEtablissements(responseEntreprises.getData().getSiretSiegeSocial());
                logger.info("Search for mandataires matching siren : " + company.getSiren());
                List<ApiGouvMandataireSocial> mandataireSocials = null;
                try {
                    ResponseMandataires responseMandataires = this.getInfoMandataires(company.getSiren());
                    mandataireSocials = responseMandataires.getData();
                }catch (Exception e){
                    logger.warn("Api do not found any matching mandataire :", e);
                }
                company = convertUniteLegaleToCompany(responseEntreprises.getData(), responseEtablissement.getData(), responseEtablissement.getMeta(), mandataireSocials);
                this.setCompanyMissingFields(responseEntreprises, responseEtablissement, company, inseeLegalCategoryService);
            } catch (Exception e) {
                logger.error("Error happen during api.gouv.fr call :", e);
            }
        } else {
            logger.info("SIREN invalid or null !");
        }
        return company;
    }

    @Override
    public boolean companyExist(Company company) {
        logger.info("Check Company with siren " + company.getSiren() + " exist");
        boolean exist = false;
        String url = this.getUrlEntreprise(company.getSiren());
        HttpURLConnection conn;
        try {
                 /*   Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("172.31.6.15", 3128));
            conn = (HttpURLConnection) new URL(url).openConnection(proxy);*/
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty(HttpHeaders.AUTHORIZATION, "Bearer " + MPS_TOKEN);
            conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json");
            conn.setConnectTimeout(8000); // set timeout to 8 seconds
            exist = conn.getResponseCode() >= HttpStatus.OK.getValue() && conn.getResponseCode() <= HttpStatus.NO_CONTENT.getValue();
            conn.disconnect();
        } catch (MalformedURLException e) {
            logger.error("Not a valid URL: " + url, e);
        } catch (Exception e) {
            logger.error("Unable to connect to : " + url, e);
        }
        return exist;
    }

    @Override
    public boolean establishmentExist(Establishment establishment) {
        logger.info("Check establishment with siret " + establishment.getSiret() + " exist");
        boolean exist = false;
        String url = this.getUrlEtablissement(establishment.getSiret());
        HttpURLConnection conn;
        try {
                 /*   Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("172.31.6.15", 3128));
            conn = (HttpURLConnection) new URL(url).openConnection(proxy);*/
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty(HttpHeaders.AUTHORIZATION, "Bearer " + MPS_TOKEN);
            conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json");
            conn.setConnectTimeout(8000); // set timeout to 8 seconds
            exist = conn.getResponseCode() >= HttpStatus.OK.getValue() && conn.getResponseCode() <= HttpStatus.NO_CONTENT.getValue();
            conn.disconnect();
        } catch (MalformedURLException e) {
            logger.error("Not a valid URL: " + url, e);
        } catch (Exception e) {
            logger.error("Unable to connect to : " + url, e);
        } finally {
        }

        return exist;
    }

    private String getUrlMandataires(String companySiren) {

        String baseUrl = MPS_URL_MANDATAIRES.replace("COMPANY_SIREN", companySiren);
        HashMap<String, String> paramsUrl = new HashMap<>();
        paramsUrl.put("context", MPS_CONTEXT);
        paramsUrl.put("recipient", MPS_RECIPIENT);
        paramsUrl.put("object", MPS_OBJECT);

        return paramsUrl.keySet().stream()
                .map(key -> key + "=" + encodeValue(paramsUrl.get(key)))
                .collect(joining("&", baseUrl + "?", ""));
    }

    private String getUrlEntreprise(String companySiren) {

        String baseUrl = MPS_URL_ENTREPRISES.replace("COMPANY_SIREN", companySiren);
        HashMap<String, String> paramsUrl = new HashMap<>();
        paramsUrl.put("context", MPS_CONTEXT);
        paramsUrl.put("recipient", MPS_RECIPIENT);
        paramsUrl.put("object", MPS_OBJECT);

        return paramsUrl.keySet().stream()
                .map(key -> key + "=" + encodeValue(paramsUrl.get(key)))
                .collect(joining("&", baseUrl + "?", ""));
    }


    private String getUrlEtablissement(String siegeSocialSiret) {

        String baseUrl = MPS_URL_ETABLISSEMENT.replace("SIEGE_SOCIAL_SIRET", siegeSocialSiret);
        HashMap<String, String> paramsUrl = new HashMap<>();
        paramsUrl.put("context", MPS_CONTEXT);
        paramsUrl.put("recipient", MPS_RECIPIENT);
        paramsUrl.put("object", MPS_OBJECT);

        return paramsUrl.keySet().stream()
                .map(key -> key + "=" + encodeValue(paramsUrl.get(key)))
                .collect(joining("&", baseUrl + "?", ""));
    }

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }


    private static String getStringFromInputStream(InputStream is) {
        String text = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        return text;
    }

    private void setCompanyMissingFields(ResponseUniteLegale responseEntreprises, ResponseEtablissement
            responseEtablissement, Company company, InseeLegalCategoryService inseeLegalCategoryService) {

        // diffusable information
        if (responseEntreprises.getData().diffusableCommercialement == null
                || responseEntreprises.getData().diffusableCommercialement.equals(Boolean.TRUE)) {
            company.setDiffusableInformations(Boolean.TRUE);
        }

        // nic
        if (company.getNic() == null || company.getNic().isEmpty()) {
            company.setNic(responseEntreprises.getData().siretSiegeSocial.substring(9, 14));
        }

        // postal address
        if (company.getAddress() != null) {
            if (company.getAddress().getPostalAddress() == null) {
                String postalAddress = "";

                if (responseEtablissement.getData().getAdresse().getNumeroVoie() != null) {
                    postalAddress += responseEtablissement.getData().getAdresse().getNumeroVoie() + " ";
                }

                if (responseEtablissement.getData().getAdresse().getTypeVoie() != null) {
                    postalAddress += responseEtablissement.getData().getAdresse().getTypeVoie() + " ";
                }

                if (responseEtablissement.getData().getAdresse().getLibelleVoie() != null) {
                    postalAddress += responseEtablissement.getData().getAdresse().getLibelleVoie();
                }
                company.getAddress().setPostalAddress(postalAddress);
            }

            // postal code
            if (company.getAddress().getPostalCode() == null) {
                company.getAddress()
                        .setPostalCode(responseEtablissement.getData().getAdresse().getCodePostal());
            }
            //si pas de code postal dans la reponse on met 00000
            if (company.getAddress().getPostalCode() == null) {
                company.getAddress()
                        .setPostalCode("00000");
            }

            // city
            if (company.getAddress().getCity() == null) {
                company.getAddress().setCity(responseEtablissement.getData().getAdresse().getLibelleCommune());
            }

            // canton
            if (company.getAddress().getCanton() == null) {
                company.getAddress().setCanton("Aucun");
            }

            // country
            if (company.getAddress().getCountry() == null) {
                company.getAddress().setCountry(Country.FR);
            }
        }
        // raison sociale
        if (responseEntreprises.getData().getPersonneMoraleAttributs() != null) {
            company.setLabel(responseEntreprises.getData().getPersonneMoraleAttributs().getRaisonSociale());
        } else {
            logger.info("Label is null");
        }

        // catégorie juridique
        if (responseEntreprises.getData().getFormeJuridique() != null) {
            CompanyINSEECat companyINSEECat = CompanyINSEECat.getByCode(responseEntreprises.getData().getFormeJuridique().getCode());
            if (companyINSEECat != null) {
                company.setLegalCategory(companyINSEECat);
            } else {
                // set to other InseeLegalCategory because we are not able to
                // retrieve a known INSEECat
                company.setLegalCategory(CompanyINSEECat.AUTRE);
                logger.info("Unknown InseeLegalCategory ... Set InseeLegalCategory to default OTHER");
            }
        } else {
            logger.info("Company InseeLegalCategory is null. Setting it to 'Autre'");
            CompanyINSEECat companyINSEECat = CompanyINSEECat.getByValue("AUTRE");
            company.setLegalCategory(companyINSEECat);
        }

        // foreign identifier ?
        if (responseEntreprises.getData().getSiren() != null) {
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

    private void setEstablishmentMissingFields(ResponseEtablissement responseEtablissements,
                                               Establishment establishment) {
        // postal address
        if (establishment.getAddress() != null) {
            if (establishment.getAddress().getPostalAddress() == null) {
                String postalAddress = "";
                if (responseEtablissements.getData().getAdresse() != null) {
                    if (responseEtablissements.getData().getAdresse().getNumeroVoie() != null) {
                        postalAddress += responseEtablissements.getData().getAdresse().getNumeroVoie() + " ";
                    }

                    if (responseEtablissements.getData().getAdresse().getTypeVoie() != null) {
                        postalAddress += responseEtablissements.getData().getAdresse().getTypeVoie() + " ";
                    }

                    if (responseEtablissements.getData().getAdresse().getLibelleVoie() != null) {
                        postalAddress += responseEtablissements.getData().getAdresse().getLibelleVoie();
                    }
                    establishment.getAddress().setPostalAddress(postalAddress);
                }
            }

            // postal code
            if (establishment.getAddress().getPostalCode() == null) {
                establishment.getAddress()
                        .setPostalCode(responseEtablissements.getData().getAdresse().getCodePostal());
            }

            // city
            if (establishment.getAddress().getCity() == null) {
                establishment.getAddress().setCity(responseEtablissements.getData().getAdresse().getLibelleCommune());
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

    public static Company convertUniteLegaleToCompany(ApiGouvUniteLegale uniteLegale, ApiGouvEtablissement etablissement, ApiGouvMeta
            meta, List<ApiGouvMandataireSocial> mandatairesSociaux) {

        Company company = new Company(uniteLegale.getFormeJuridique().getLibelle(), "");
        company.setLabel(uniteLegale.getPersonneMoraleAttributs() != null ? uniteLegale.getPersonneMoraleAttributs().getRaisonSociale() : "");
        company.setName(uniteLegale.getPersonneMoraleAttributs() != null ? uniteLegale.getPersonneMoraleAttributs().getRaisonSociale() : "");
        company.setSiren(uniteLegale.getSiren());
        company.setSiretHeadOffice(uniteLegale.getSiretSiegeSocial());
        company.setApeCode(convertMyec3NafFormat(uniteLegale.getActivitePrincipale()));
        company.setApeNafLabel(uniteLegale.getActivitePrincipale() != null ? uniteLegale.getActivitePrincipale().getLibelle() : "");
        company.setLegalCategoryString(uniteLegale.getActivitePrincipale() != null ? uniteLegale.getActivitePrincipale().getLibelle() : "");
        company.setCreationDate(convertLongToDate(uniteLegale.getDateCreation()));
        company.setLastUpdate(meta.getDateDerniereMiseAjourAsDate());

        if (mandatairesSociaux != null) {
            List<Person> persons = new ArrayList<>();
            for (ApiGouvMandataireSocial mandataireSocial : mandatairesSociaux) {
                if (mandataireSocial.getData().getNom() != null) {
                    persons.add(convertMandataireSocialToPerson(mandataireSocial));
                }
            }
            if (persons.size() > 0){
                company.setResponsibles(persons);
            }
        }
        logger.info("New company generated from api.gouv.fr  :" + company.getSiren());
        company.setEnabled(true);
        company.setCreatedDate(new Date());
        return company;
    }

    private static Date convertLongToDate(String dateaslong) {
        Date date = null;
        if (dateaslong != null) {
            date = new Date(TimeUnit.SECONDS.toMillis(Long.valueOf(dateaslong)));
        }
        return date;
    }

    public static String convertMyec3NafFormat(ApiGouvActivitePrincipale activitePrincipale) {
        String codeNAF = "";
        if (activitePrincipale != null) {
            if (NAFREV_2.equals(activitePrincipale.getNomenclature())) {
                logger.info("Company activity received in NAFREV_2 format, processing conversion :", activitePrincipale.getCode());
                codeNAF = activitePrincipale.getCode().replace(".", "");
                logger.info("Company activity convert to :", codeNAF);
            }else{
                logger.info("Company activity received in other format, processing conversion :", activitePrincipale.getCode());
                codeNAF = activitePrincipale.getCode().replace(".", "");
                logger.info("Company activity convert to :", codeNAF); 
            }
        } else {
            logger.warn("Company activity not defined in data.");
        }
        return codeNAF;
    }

    public static Establishment convertEtablissementToEtablishment(ApiGouvEtablissement etablissement, ApiGouvMeta
            meta) {
        String raisonSociale = etablissement.getUniteLegale() != null && etablissement.getUniteLegale().getPersonneMoraleAttributs() != null ? etablissement.getUniteLegale().getPersonneMoraleAttributs().raisonSociale: "";
        Establishment establishment = new Establishment(raisonSociale, raisonSociale);
        establishment.setSiret(etablissement.getSiret());
        establishment.setIsHeadOffice(Boolean.valueOf(etablissement.getSiegeSocial()));
        establishment.setApeCode(convertMyec3NafFormat(etablissement.getActivitePrincipale()));
        establishment.setApeNafLabel(etablissement.getActivitePrincipale() != null ? etablissement.getActivitePrincipale().getLibelle() : "");
        establishment.setDiffusableInformations(Boolean.valueOf(etablissement.getDiffusableCommercialement()));
        establishment.setAddress(convertAdresseToAddress(etablissement.getAdresse()));
        establishment.setPays(convertAdresseToPaysImplantation(etablissement.getAdresse()));

        establishment.setLastUpdate(meta.getDateDerniereMiseAjourAsDate());
        logger.info("New establishment generated from api.gouv WS :" + establishment.getSiret());
        return establishment;
    }

    public static Address convertAdresseToAddress(ApiGouvAdresse apiGouvAdresse) {
        Address adresse = new Address();
        adresse.setStreetNumber(apiGouvAdresse.getNumeroVoie());
        adresse.setStreetType(apiGouvAdresse.getTypeVoie());
        adresse.setStreetName(apiGouvAdresse.getLibelleVoie());
        adresse.setInsee(apiGouvAdresse.getCodeCommune());
        adresse.setPostalCode(apiGouvAdresse.getCodePostal());
        adresse.setCity(apiGouvAdresse.getLibelleCommune());
        if (apiGouvAdresse.codePaysEtranger == null) {
            adresse.setCountry(Country.FR);
        }
        logger.info("New adresse generated from api.gouv WS :" + adresse.getStreetName());
        return adresse;
    }

    public static PaysImplantation convertAdresseToPaysImplantation(ApiGouvAdresse adresse) {
        if (adresse.codePaysEtranger == null) {
            logger.info("Pas de paysImplantation api.gouv WS");
            PaysImplantation paysImplantation = new PaysImplantation();
            paysImplantation.setCode("FR");
            paysImplantation.setValue("FRANCE");
            return paysImplantation;
        }
        PaysImplantation paysImplantation = new PaysImplantation();
        paysImplantation.setCode(adresse.getCodePaysEtranger());
        paysImplantation.setValue(adresse.libellePaysEtranger);
        logger.info("New paysImplantation generated from api.gouv WS :" + paysImplantation.getValue());
        return paysImplantation;
    }

    public static Person convertMandataireSocialToPerson(ApiGouvMandataireSocial mandataireSocial) {
        Person person = new Person();
        person.setFirstname(mandataireSocial.getData().getPrenom());
        person.setLastname(mandataireSocial.getData().getNom());
        person.setName(mandataireSocial.getData().getNom()+" "+mandataireSocial.getData().getPrenom());
        person.setType(mandataireSocial.getData().getType());
        person.setFunction(mandataireSocial.getData().getFonction());
        person.setMoralName(mandataireSocial.getData().getRaisonSociale());
        logger.info("New person generated from api.gouv WS :" + person.getName());
        return person;
    }

}
