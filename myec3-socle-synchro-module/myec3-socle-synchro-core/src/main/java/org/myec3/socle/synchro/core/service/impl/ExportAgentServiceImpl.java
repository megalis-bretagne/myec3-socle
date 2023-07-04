package org.myec3.socle.synchro.core.service.impl;

import au.com.bytecode.opencsv.CSVWriter;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.EtatExport;
import org.myec3.socle.core.service.*;
import org.myec3.socle.core.service.impl.ExportAgentCSVGeneratorServiceImpl;
import org.myec3.socle.synchro.core.service.ExportAgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;

@Service("exportAgentService")
public class ExportAgentServiceImpl implements ExportAgentService {

    private static final Logger logger = LoggerFactory.getLogger(ExportAgentServiceImpl.class);

    @Autowired
    @Qualifier("organismService")
    private OrganismService organismService;

    @Autowired
    @Qualifier("applicationService")
    private ApplicationService applicationService;

    @Autowired
    @Qualifier("exportCSVService")
    private ExportCSVService exportCSVService;

    @Autowired
    @Qualifier("exportAgentCSVGeneratorService")
    private ExportAgentCSVGeneratorServiceImpl exportAgentCSVGeneratorService;


    @Override
    @Transactional(readOnly = false)
    public void purgeAndAdd(String content) {
        List<ExportCSV> exportCSVList =  exportCSVService.findExportCSVByEtat(EtatExport.AF);

        Date dateExport =new Date(System.currentTimeMillis());
        boolean first= true;

        for(ExportCSV exportCSV : exportCSVList) {
            if (first){
                exportCSV.setEtat(EtatExport.OK);
                exportCSV.setDateExport(dateExport);
                exportCSV.setContent(content);
                exportCSVService.update(exportCSV);
            }else{
                exportCSVService.deleteById(exportCSV.getId());
            }
        }

        List<Long> listId = exportCSVService.findAllIdOrderbyDateDemande();

        if  (listId.size()>5){
            int nbEltASupprimer=listId.size()-5;

            for (int i = 0; i < nbEltASupprimer; i++){
                exportCSVService.deleteById(listId.get(i));
            }
        }

    }

    @Override
    @Transactional(readOnly = true)
    public String exportAgent() {

        //On sélectionne toutes les demande d'export
        List<ExportCSV> exportCSVList =  exportCSVService.findExportCSVByEtat(EtatExport.AF);

        if ( exportCSVList !=null && exportCSVList.size()> 0){
            logger.info("There is an agent export to be done");

            try {
                StringWriter sw = new StringWriter();
                List<Application> applicationsList = applicationService.findAll();
                List<Organism> organismsList = organismService.findAll();

                try (CSVWriter csvWriter = new CSVWriter(sw, ExportAgentCSVGeneratorService.CSV_DEFAULT_SEPARATOR)) {
                    exportAgentCSVGeneratorService.writeCsv(organismsList, applicationsList, csvWriter);
                }

                return sw.toString();

            } catch (IllegalArgumentException e) {
                logger.debug(e.getMessage());
                return null;

            } catch (Exception e) {
                logger.error("An unexpected error has occured during the validation of the file to upload {} ",
                        e.getMessage());
                return null;
            }
        }
        return null;


    }

}
