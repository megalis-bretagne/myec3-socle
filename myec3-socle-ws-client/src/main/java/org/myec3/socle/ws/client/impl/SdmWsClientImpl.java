package org.myec3.socle.ws.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.ResourceType;
import org.myec3.socle.core.domain.sdm.model.SdmResource;
import org.myec3.socle.core.service.ApplicationService;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.*;
import org.myec3.socle.synchro.core.domain.model.SynchroIdentifiantExterne;
import org.myec3.socle.synchro.core.domain.model.SynchronizationSubscription;
import org.myec3.socle.synchro.core.service.SynchroIdentifiantExterneService;
import org.myec3.socle.ws.client.ResourceWsClient;
import org.myec3.socle.ws.client.constants.WsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Implementation of {@link ResourceWsClient} to contact third-arty application
 * throught HTTPS
 */
@Component("sdmWsClientImpl")
public class SdmWsClientImpl implements ResourceWsClient {

	private static final Logger logger = LoggerFactory.getLogger(SdmWsClientImpl.class);

	private static final String SERVER_UNVAILABLE_ERROR_LABEL = "Server unavailable";
	private static final String SERVER_UNVAILABLE_ERROR_MESSAGE = "Impossible to contact distant server";
	private static final String CONNECTION_EXCEPTION = "java.net.ConnectException: Connection refused";
	private static final String CLIENT_EXCEPTION_ERROR_LABEL = "Client WS Exception";

	private Client clientWs = null;

	@Autowired
	@Qualifier("synchroIdentifiantExterneService")
	private SynchroIdentifiantExterneService synchroIdentifiantExterneService;

	@Autowired
	@Qualifier("applicationService")
	private ApplicationService applicationService;

	/**
	 * Creates and returns JerseyClient
	 *
	 * @return WS client
	 */
	private Client getClientWs() {
		if (this.clientWs == null) {
			this.clientWs = JerseyClientBuilder.newClient();
		}
		return this.clientWs;
	}

	/**
	 * Prepare Header before sending the request
	 *
	 * @param builder  : the builder used to create the request
	 */
	private void prepareHeaderAtexo(Invocation.Builder builder) {
		WebTarget webResource = getClientWs().target(WsConstants.SDM_TOKEN_URL);
		Invocation.Builder builderToken = webResource.request().accept(MediaType.APPLICATION_JSON);
		builderToken.header("externalid", 1122);
		builderToken.header("usertype", "AGENT");
		try{
			Response response = builderToken.get();
			String responseToString = response.readEntity(String.class);
			String[] tab= StringUtils.split(responseToString,"<ticket>");
			String[] tab2 =StringUtils.split(tab[1],"</ticket>");
			builder.header("Authorization", "Bearer " + tab2[0]);
			builder.header("externalid", 1122);
			builder.header("usertype", "AGENT");
		}catch (Exception e){
			logger.error("Erreur lors de la récupération du token SDM",e);
		}
	}

	/**
	 * Return the name of a resource
	 *
	 * @param resource : the resource to send
	 * @return the resource's name
	 */
	private String getResourceName(Resource resource) {
		logger.debug("Processing method getResourceName...");
		String resourceName = null;
		resourceName = resource.getClass().getSimpleName();
		return resourceName.substring(0, 1).toLowerCase() + resourceName.substring(1);
	}

	/**
	 * This method allows to retrieve the {@link HttpStatus} through the Client
	 * Response
	 *
	 * @param response : the client response received
	 * @return the correct {@link HttpStatus} corresponding at the client response
	 *         status received
	 */
	private HttpStatus retrieveHttpStatus(Response response) {
		logger.debug("Processing method retrieveHttpStatus...");
		HttpStatus[] HttpStatusList = HttpStatus.values();
		for (HttpStatus httpStatus : HttpStatusList) {
			if (response.getStatus() == httpStatus.getValue()) {
				logger.debug("HttpStatus contained into client response : {}", httpStatus);
				return httpStatus;
			}
		}
		return null;
	}

	/**
	 * Build the response message before sending it for error checking
	 *
	 * @param response   : the response returned by the ws client
	 * @param methodType : the {@link MethodType} used to perform the request
	 * @param resource
	 * @return a {@link ResponseMessage} used by the synchronization module in order
	 *         to log the request and perform error handling if necessary
	 * @throws Exception
	 */
	private ResponseMessage buildResponseMessage(Response response, MethodType methodType, Resource resource) throws Exception {
		ResponseMessage responseMsg = new ResponseMessage();
		// Set HttpStatus depending on client response value
		responseMsg.setHttpStatus(this.retrieveHttpStatus(response));

		if (!(responseMsg.getHttpStatus() == null)) {
			if (responseMsg.getHttpStatus().getValue() > HttpStatus.NO_CONTENT.getValue()) {
				logger.debug("[buildResponseMessage] HttpStatus returned by webService Client: {}",
						responseMsg.getHttpStatus().getValue());

				String responseToString = response.readEntity(String.class);
				// Display response content
				logResponseContent(responseToString);

				// Set entity error contained into client response to response
				// message
				// Jersey has difficulties to convert the response to Error natively. We are
				// doing it now by ourselves

				logger.debug("Content of response in string : " + responseToString);

/*
				ObjectMapper mapper = new XmlMapper();
				mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				mapper.registerModule(new JaxbAnnotationModule());
				Error error = mapper.readValue(responseToString, Error.class);
*/

				//responseMsg.setError(error);


				//exemple erreur
				//{"mpe":{"reponse":{"errors":["Aucun service avec l'id 0 dans l'organisme b3v"],"status":400,"type":"http:\/\/localhost:8000\/docs\/errors#validation_error","title":"There was a validation error","statutReponse":"KO"}}}

				if ( !StringUtils.isEmpty(responseToString)){
					ObjectMapper mapper = new ObjectMapper();
					Map<String, String> map = mapper.readValue(responseToString, Map.class);
					Object mpe = map.get("mpe");
					LinkedHashMap<String, Object> mpeHM = (LinkedHashMap<String, Object>) mpe;

					Object reponse = mpeHM.get("reponse");
					LinkedHashMap<String, Object> reponseHM = (LinkedHashMap<String, Object>) reponse;

					String erreurs = "";
					if (reponseHM.get("errors") instanceof ArrayList){
						ArrayList<String> listeDesErreurs = (ArrayList<String>)reponseHM.get("errors");
						erreurs=String.join("-",listeDesErreurs);
					}else if (reponseHM.get("errors") instanceof LinkedHashMap){
						Object listeDesErreurs = reponseHM.get("errors");
						LinkedHashMap<String, ArrayList<String>> listeDesErreursHM = (LinkedHashMap<String, ArrayList<String>>) listeDesErreurs;
						for(String key :listeDesErreursHM.keySet()){
							erreurs+=" " + String.join("-",listeDesErreursHM.get(key));
						}
					}else if (reponseHM.get("errors")  instanceof String ){
						erreurs= (String)reponseHM.get("errors");
					}

					Integer status = (Integer)reponseHM.get("status");
					String type = (String)reponseHM.get("type");
					String title = (String)reponseHM.get("title");
					String statutReponse = (String)reponseHM.get("statutReponse");

					Error error = new Error();
					responseMsg.setError(error);
					error.setErrorMessage(erreurs);
					error.setErrorLabel(title);

					//mapping à faire
					error.setErrorCode(ErrorCodeType.FORMAT_ERROR);
				} else{
					Error error = new Error();
					responseMsg.setError(error);
					error.setErrorMessage("Pas de message d'erreur");
					error.setErrorLabel("Pas de message d'erreur");
					//mapping à faire
					error.setErrorCode(ErrorCodeType.INTERNAL_CLIENT_ERROR);
				}
				// Fill the method used during the request
				responseMsg.getError().setMethodType(methodType);
			} else {
				// No error occurred during the request
				String responseToString = response.readEntity(String.class);
				// Display response content
				logResponseContent(responseToString);

				//traiter la reponse de la SDM
				traiterReponseOk(resource, responseToString);

				responseMsg.setError(null);
			}
		} else {
			throw new Exception("The HTTP status returned is not managed : " + response.getStatus());
		}
		return responseMsg;
	}

	/**
	 * Méthode qui permet de parser la réponse Ok de la salle des marchés
	 * Mets à jour la table SynchroIdentifiantExterne
	 * @param resource
	 * @param responseToString
	 * @throws java.io.IOException
	 */
	private void traiterReponseOk(Resource resource, String responseToString) throws java.io.IOException {
		if ( !StringUtils.isEmpty(responseToString)) {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> map = mapper.readValue(responseToString, Map.class);
			Object mpe = map.get("mpe");
			LinkedHashMap<String, Object> mpeHM = (LinkedHashMap<String, Object>) mpe;
			Object reponse = mpeHM.get("reponse");
			LinkedHashMap<String, Object> reponseHM = (LinkedHashMap<String, Object>) reponse;

			if (AgentProfile.class.equals(resource.getClass())) {
				if (reponseHM.get("Agent") instanceof LinkedHashMap) {
					Object agent = reponseHM.get("Agent");
					LinkedHashMap<String,Object> agentHM = (LinkedHashMap<String,Object>) agent;

					SynchroIdentifiantExterne s =synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.AGENT_PROFILE);
					if (s==null){
						Application sdmApplication = applicationService.findByName("SDM");
						SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
						synchro.setApplication(sdmApplication);
						synchro.setTypeRessource(ResourceType.AGENT_PROFILE);
						synchro.setIdSocle(resource.getId());
						if (agentHM.get("id") !=null){
							synchro.setIdAppliExterne(Long.valueOf((Integer)agentHM.get("id")));
						}
						synchroIdentifiantExterneService.create(synchro);
					}else {
						s.setIdSocle(resource.getId());
						if (agentHM.get("id") !=null){
							s.setIdAppliExterne(Long.valueOf((Integer)agentHM.get("id")));
						}
						synchroIdentifiantExterneService.update(s);

					}
				} else {
					logger.error("Probleme de parsing de la reponse OK {}",responseToString);
				}
			}
			if (EmployeeProfile.class.equals(resource.getClass())) {
				if (reponseHM.get("Inscrit") instanceof LinkedHashMap) {
					Object inscrit = reponseHM.get("Inscrit");
					LinkedHashMap<String,Object> inscritHM = (LinkedHashMap<String,Object>) inscrit;

					SynchroIdentifiantExterne s =synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.EMPLOYEE_PROFILE);
					if (s==null){
						Application sdmApplication = applicationService.findByName("SDM");
						SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
						synchro.setApplication(sdmApplication);
						synchro.setTypeRessource(ResourceType.EMPLOYEE_PROFILE);
						synchro.setIdSocle(resource.getId());
						if (inscritHM.get("id") !=null){
							synchro.setIdAppliExterne(Long.valueOf((Integer)inscritHM.get("id")));
						}
						synchroIdentifiantExterneService.create(synchro);
					}else {
						s.setIdSocle(resource.getId());
						if (inscritHM.get("id") !=null){
							s.setIdAppliExterne(Long.valueOf((Integer)inscritHM.get("id")));
						}
						synchroIdentifiantExterneService.update(s);

					}
				} else {
					logger.error("Probleme de parsing de la reponse OK {}",responseToString);
				}
			}
			if (Organism.class.equals(resource.getClass())) {
				//{"mpe":{"reponse":{"statutReponse":"OK","organisme":[{"id":null,"acronyme":"a1b","categorieInsee":null,"sigle":"Syndicat Mixte M\u00e9galis Bretagne","siren":"253514491","denomination":"","nic":"00047","adresse":{"rue":"","codePostal":"35510","ville":"CESSON SEVIGNE","pays":"France"},"description":null,"logo":[]}]}}}
				if (reponseHM.get("organisme") instanceof ArrayList) {
					Object organisme = reponseHM.get("organisme");
					ArrayList<LinkedHashMap<String,String>> organismeHM = (ArrayList<LinkedHashMap<String,String>>) organisme;
					for (LinkedHashMap<String,String> orga: organismeHM) {
						SynchroIdentifiantExterne s =synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.ORGANISM);
						if (s==null){
							Application sdmApplication = applicationService.findByName("SDM");
							SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
							synchro.setApplication(sdmApplication);
							synchro.setTypeRessource(ResourceType.ORGANISM);
							synchro.setIdSocle(resource.getId());
							if (orga.get("id") !=null){
								synchro.setIdAppliExterne(Long.valueOf(orga.get("id")));
							}
							if (orga.get("acronyme") !=null){
								synchro.setAcronyme(String.valueOf(orga.get("acronyme")));
							}
							synchroIdentifiantExterneService.create(synchro);
						}else {
							s.setIdSocle(resource.getId());
							s.setAcronyme(String.valueOf(orga.get("acronyme")));
							synchroIdentifiantExterneService.update(s);
						}
					}
				} else {
					logger.error("Probleme de parsing de la reponse OK {}",responseToString);
				}
			}
			if (Company.class.equals(resource.getClass())) {
				if (reponseHM.get("Entreprise") instanceof LinkedHashMap) {
					Object entreprise = reponseHM.get("Entreprise");
					LinkedHashMap<String,Object> ent = (LinkedHashMap<String,Object>) entreprise;

					SynchroIdentifiantExterne s =synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.COMPANY);
					if (s==null){
						Application sdmApplication = applicationService.findByName("SDM");
						SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
						synchro.setApplication(sdmApplication);
						synchro.setTypeRessource(ResourceType.COMPANY);
						synchro.setIdSocle(resource.getId());
						if (ent.get("id") !=null){
							synchro.setIdAppliExterne(Long.valueOf((Integer)ent.get("id")));
						}
						synchroIdentifiantExterneService.create(synchro);
					}else {
						s.setIdSocle(resource.getId());
						if (ent.get("id") !=null){
							s.setIdAppliExterne(Long.valueOf((Integer)ent.get("id")));
						}
						synchroIdentifiantExterneService.update(s);

					}
				} else {
					logger.error("Probleme de parsing de la reponse OK {}",responseToString);
				}
			}
			if (OrganismDepartment.class.equals(resource.getClass())) {
				if (reponseHM.get("service") instanceof LinkedHashMap) {
					Object service = reponseHM.get("service");
					LinkedHashMap<String,Object> serv = (LinkedHashMap<String,Object>) service;

					SynchroIdentifiantExterne s =synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.ORGANISM_DEPARTMENT);
					if (s==null){
						Application sdmApplication = applicationService.findByName("SDM");
						SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
						synchro.setApplication(sdmApplication);
						synchro.setTypeRessource(ResourceType.ORGANISM_DEPARTMENT);
						synchro.setIdSocle(resource.getId());
						if (serv.get("id") !=null){
							synchro.setIdAppliExterne(Long.valueOf((Integer)serv.get("id")));
						}
						synchroIdentifiantExterneService.create(synchro);
					}else {
						s.setIdSocle(resource.getId());
						if (serv.get("id") !=null){
							s.setIdAppliExterne(Long.valueOf((Integer)serv.get("id")));
						}
						synchroIdentifiantExterneService.update(s);

					}
				} else {
					logger.error("Probleme de parsing de la reponse OK {}",responseToString);
				}
			}
			if (Establishment.class.equals(resource.getClass())) {
				if (reponseHM.get("Etablissement") instanceof LinkedHashMap) {
					Object etablissement = reponseHM.get("Etablissement");
					LinkedHashMap<String,Object> etab = (LinkedHashMap<String,Object>) etablissement;

					SynchroIdentifiantExterne s =synchroIdentifiantExterneService.findByIdSocle(resource.getId(), ResourceType.ESTABLISHMENT);
					if (s==null){
						Application sdmApplication = applicationService.findByName("SDM");
						SynchroIdentifiantExterne synchro = new SynchroIdentifiantExterne();
						synchro.setApplication(sdmApplication);
						synchro.setTypeRessource(ResourceType.ESTABLISHMENT);
						synchro.setIdSocle(resource.getId());
						if (etab.get("id") !=null){
							synchro.setIdAppliExterne(Long.valueOf((Integer)etab.get("id")));
						}
						synchroIdentifiantExterneService.create(synchro);
					}else {
						s.setIdSocle(resource.getId());
						if (etab.get("id") !=null){
							s.setIdAppliExterne(Long.valueOf((Integer)etab.get("id")));
						}
						synchroIdentifiantExterneService.update(s);

					}
				} else {
					logger.error("Probleme de parsing de la reponse OK {}",responseToString);
				}
			}

		}
	}

	/**
	 * Set {@link Error} into {@link ResponseMessage} if the http status returned
	 * equals 500 or 503
	 *
	 * @param resource   : the {@link Resource} concerned by the request
	 * @param httpStatus : the httpStatus returned by the client
	 * @param methodType : the type of REST request used
	 * @return a ResponseMessage
	 */
	private ResponseMessage buildServerErrorMessage(Resource resource, HttpStatus httpStatus, ErrorCodeType errorCode,
													MethodType methodType, String errorLabel, String errorMessage) {
		ResponseMessage responseMsg = new ResponseMessage();
		// Create error
		Error error = new Error();
		error.setErrorLabel(errorLabel);
		error.setErrorMessage(errorMessage);
		error.setResourceId(resource.getId());
		error.setMethodType(methodType);
		error.setErrorCode(errorCode);

		// Create responseMessage
		responseMsg.setHttpStatus(httpStatus);
		responseMsg.setError(error);

		return responseMsg;
	}
	public ResponseMessage post(Resource resource, SdmResource resourceSDM,SynchronizationSubscription synchronizationSubscription) {
		WebTarget webResource = getClientWs().target(synchronizationSubscription.getUri());

		Invocation.Builder builder = webResource.request().accept(MediaType.APPLICATION_JSON);
		//patch pour ajouter les trucs dans le header
		//Authorization, externalId et usertype
		prepareHeaderAtexo(builder);
		try {
			logger.debug("[POST] on URI: {}", synchronizationSubscription.getUri());
			Response response = builder.post(Entity.json(resourceSDM));

			return buildResponseMessage(response, MethodType.POST, resource);
		} catch (ClientErrorException ex) {
			if (ex.getMessage().contains(CONNECTION_EXCEPTION)) {
				logger.error("[POST][ConnectException] Server Unavailable HTTP status 503", ex);
				return this.buildServerErrorMessage(resource, HttpStatus.SERVER_UNAVAILABLE, null, MethodType.POST,
						SERVER_UNVAILABLE_ERROR_LABEL, SERVER_UNVAILABLE_ERROR_MESSAGE);
			} else {
				logger.error("[POST] Exception in sync", ex);
				return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST,
						ErrorCodeType.INTERNAL_CLIENT_ERROR, MethodType.POST, CLIENT_EXCEPTION_ERROR_LABEL,
						ex.getMessage());
			}
		} catch (Exception ex) {
			logger.error("[POST] Exception in sync", ex);
			return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST,
					ErrorCodeType.INTERNAL_CLIENT_ERROR,
					MethodType.POST, CLIENT_EXCEPTION_ERROR_LABEL, ex.getMessage());
		}
	}

	public ResponseMessage put(Resource resource, SdmResource resourceSDM,SynchronizationSubscription synchronizationSubscription) {
		WebTarget webResource = getClientWs().target(synchronizationSubscription.getUri());

		Invocation.Builder builder = webResource.request().accept(MediaType.APPLICATION_JSON);
		//patch pour ajouter les trucs dans le header
		//Authorization, externalId et usertype
		prepareHeaderAtexo(builder);
		try {
			logger.debug("[PUT] on URI: {}", synchronizationSubscription.getUri());
			Response response = builder.put(Entity.json(resourceSDM));

			return buildResponseMessage(response, MethodType.PUT, resource);
		} catch (ClientErrorException ex) {
			if (ex.getMessage().contains(CONNECTION_EXCEPTION)) {
				logger.error("[PUT][ConnectException] Server Unavailable HTTP status 503", ex);
				return this.buildServerErrorMessage(resource, HttpStatus.SERVER_UNAVAILABLE, null, MethodType.PUT,
						SERVER_UNVAILABLE_ERROR_LABEL, SERVER_UNVAILABLE_ERROR_MESSAGE);
			} else {
				logger.error("[PUT] Exception in sync", ex);
				return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST,
						ErrorCodeType.INTERNAL_CLIENT_ERROR, MethodType.POST, CLIENT_EXCEPTION_ERROR_LABEL,
						ex.getMessage());
			}
		} catch (Exception ex) {
			logger.error("[PUT] Exception in sync", ex);
			return this.buildServerErrorMessage(resource, HttpStatus.BAD_REQUEST,
					ErrorCodeType.INTERNAL_CLIENT_ERROR,
					MethodType.POST, CLIENT_EXCEPTION_ERROR_LABEL, ex.getMessage());
		}
	}

	/**
	 * This method allows to log the content of the client response received
	 *
	 * @param response : the Response body received as a string
	 */
	private void logResponseContent(String response) {
//		try {
//			// We display the content of inputStream
//			OutputStream outStream = System.out;
//			try (Writer w = new OutputStreamWriter(outStream, "UTF-8")) {
//				w.write(response);
//			}
//		} catch (Exception e) {
//			logger.error("Failed to write response content : ", e);
//		}

		System.out.println(response);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response get(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response get(Long id, SynchronizationSubscription synchronizationSubscription) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage post(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		WebTarget webResource = getClientWs().target(synchronizationSubscription.getUri());

		throw new UnsupportedOperationException("Not implemented.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseMessage put(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public ResponseMessage putComplete(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public ResponseMessage delete(Resource resource, SynchronizationSubscription synchronizationSubscription) {
		throw new UnsupportedOperationException("Not implemented.");
	}

}
