package org.myec3.socle.synchro.core.service;

import org.myec3.socle.core.domain.model.Resource;
import org.myec3.socle.core.sync.api.Error;
import org.myec3.socle.core.sync.api.ResponseMessage;

import java.io.IOException;

public interface TraiterReponseSDMService {

   void traiterReponseOk(Resource resource, String responseToString) throws IOException;

   Error traiterReponseKo(String responseToString, ResponseMessage responseMsg) throws IOException;
}
