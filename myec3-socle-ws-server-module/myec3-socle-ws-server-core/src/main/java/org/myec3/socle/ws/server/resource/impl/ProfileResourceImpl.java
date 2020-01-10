/**
 * 
 */
package org.myec3.socle.ws.server.resource.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myec3.socle.core.domain.model.Profile;
import org.myec3.socle.core.domain.model.enums.Country;
import org.myec3.socle.core.service.ProfileService;
import org.myec3.socle.ws.server.dto.FullProfileDto;
import org.myec3.socle.ws.server.dto.ProfileDto;
import org.myec3.socle.ws.server.resource.ProfilResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


/**
 * Methods uses for Monmaximilien mon-compte
 *
 * @author Charles Bourrée <charles@bourree.worldline.com>
 */
@RestController
public class ProfileResourceImpl implements ProfilResource {
	
	private static final Logger logger = LogManager.getLogger(ProfileResourceImpl.class);


	@Autowired
	@Qualifier("profileService")
	private ProfileService profileService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity getProfile(Long profileId) {
		try {
			FullProfileDto profile = new FullProfileDto(profileService.findOne(profileId));
			return ResponseEntity.ok(profile);
		} catch (Exception e) {
			logger.debug("Error get profile " + profileId + " : " + e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public ResponseEntity updateProfile(Long profileId, FullProfileDto newProfile) {
		try {
			Profile oldProfile = profileService.findOne(profileId);
			
			oldProfile.getUser().setFirstname(newProfile.getFirstname());
			oldProfile.getUser().setLastname(newProfile.getLastname());
			oldProfile.setFunction(newProfile.getFunction());
			oldProfile.getAddress().setPostalAddress(newProfile.getAddress());
			oldProfile.getAddress().setPostalCode(newProfile.getPostalCode());
			oldProfile.getAddress().setCity(newProfile.getCity());
			oldProfile.getAddress().setCountry(Country.valueOf(newProfile.getCountry()));
			oldProfile.setEmail(newProfile.getEmail());
			oldProfile.setCellPhone(newProfile.getCellPhone());
			oldProfile.setPhone(newProfile.getPhone());
			oldProfile.setFax(newProfile.getFax());
			
			Profile updatedProfile = profileService.update(oldProfile);
			return ResponseEntity.ok(new FullProfileDto(updatedProfile));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}
	
		/**
	 * {@inheritDoc}
	 */
	@Override
	public ResponseEntity getInformations(@RequestHeader("uid") Long profileId) {
		try {
			//AgentProfile agentProfile = agentProfileService.findOne(profileId);
			Profile profile = profileService.findOne(profileId);
			return ResponseEntity.ok(new ProfileDto(profile.getLabel(), profile.getEmail(),
					profile.getUsername(), profile.getUser().getFirstname(),
					profile.getUser().getLastname(), profile.isAgent()));
		} catch (Exception ex) {
			return ResponseEntity.notFound().build();
		}
	}

}
