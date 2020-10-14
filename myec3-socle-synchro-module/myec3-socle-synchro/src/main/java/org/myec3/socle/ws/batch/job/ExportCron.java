package org.myec3.socle.ws.batch.job;


import org.myec3.socle.synchro.core.service.ExportAgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExportCron {
    private static Logger logger = LoggerFactory.getLogger(ExportCron.class);

    @Autowired
    @Qualifier("exportAgentService")
    private ExportAgentService exportAgentService;

    public void exportAgent() {
        logger.info("Starting AUTO exportAgent.");
        String content = exportAgentService.exportAgent();

        if (content!=null) {
            logger.info("Starting AUTO purge exportCSV.");
            exportAgentService.purgeAndAdd(content);
        }

        logger.info("FIN AUTO exportAgent.");
    }


}