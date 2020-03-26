package org.myec3.socle.synchro.core.service;

import org.myec3.socle.core.domain.model.Resource;

import java.io.IOException;

public interface TraiterReponseSDMService {

   void traiterReponseOk(Resource resource, String responseToString) throws IOException;
}
