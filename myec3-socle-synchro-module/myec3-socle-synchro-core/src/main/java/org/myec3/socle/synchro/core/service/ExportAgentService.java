package org.myec3.socle.synchro.core.service;


public interface ExportAgentService {

    String exportAgent();

    void purgeAndAdd(String content);
}
