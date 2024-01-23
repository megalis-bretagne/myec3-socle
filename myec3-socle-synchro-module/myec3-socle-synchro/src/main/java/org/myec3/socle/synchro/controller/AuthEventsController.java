package org.myec3.socle.synchro.controller;

import lombok.Data;
import org.myec3.socle.core.domain.model.ConnectionInfos;
import org.myec3.socle.core.domain.model.User;
import org.myec3.socle.core.service.ConnectionInfosService;
import org.myec3.socle.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Data
class LoginEvent {
    String realmId;
    String clientId;
    String userId;
    String ipAddress;
    long time;
    String error;
    Map<String, String> details;
}

@RestController
@RequestMapping("/auth-events")
public class AuthEventsController {

    private static final Logger logger = LoggerFactory.getLogger(AuthEventsController.class);

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @Autowired
    @Qualifier("connectionInfosService")
    private ConnectionInfosService connectionInfosService;

    /**
     * An endpoint to ingest login events from Keycloak.
     */
    @PostMapping("/events")
    @Transactional
    public ResponseEntity<String> handleEvent(@RequestBody LoginEvent event) {
        String username = event.getDetails().get("username");
        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            logger.warn("Auth event ignored: no user found for username \"" + username + "\"");
            return ResponseEntity.ok().build();
        }
        connectionInfosService.updateUserLastConnectionTime(user, event.getTime());
        return ResponseEntity.ok().build();
    }

}
