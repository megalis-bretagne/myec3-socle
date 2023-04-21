package org.myec3.socle.webapp.pages.user;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.inject.Named;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.myec3.socle.core.domain.model.*;
import org.myec3.socle.core.domain.model.enums.EtatExport;
import org.myec3.socle.core.service.*;
import org.myec3.socle.webapp.pages.AbstractPage;
import org.myec3.socle.webapp.utils.CsvStreamResponse;

public class Export extends AbstractPage {

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Inject
	@Named("exportCSVService")
	private ExportCSVService exportCSVService;

	@Inject
	private BeanModelSource beanModelSource;

	@Persist
	private List<ExportCSV> exportCSVResult;

	@SuppressWarnings("unused")
	@Property
	private Integer rowIndex;

	@SuppressWarnings("unused")
	@Property
	private ExportCSV exportCSVRow;

	@OnEvent(EventConstants.ACTIVATE)
	public void Activation() {
		super.initUser();
		exportCSVResult = exportCSVService.findAllWithoutContent();
	}

	/**
	 * @return : bean model
	 */
	public BeanModel<ExportCSV> getGridModel() {
		BeanModel<ExportCSV> model = this.beanModelSource.createDisplayModel(
				ExportCSV.class, this.getMessages());
		//model.get("etat")
		model.add("actions", null);
		model.include("id","dateDemande","dateExport","etat","actions");
		return model;
	}

	/**
	 * @return the list of companies found
	 */
	public List<ExportCSV> getExportCSVResult() {
		return exportCSVResult;
	}

	/**
	 * @return the number of companies found
	 */
	public Integer getResultsNumber() {
		if (null == this.exportCSVResult)
			return 0;
		return this.exportCSVResult.size();
	}


	/**
	 *
	 * Generate a csv file by filling an hashmap. Keys are attribute of the header,
	 * values are the values for the attribute
	 *
	 * @throws IOException
	 */
	@OnEvent(value = EventConstants.SUCCESS, component = "agent_export_form")
	public Object onSuccess() throws IOException {

		this.setSuccessMessage("export demandé");

		ExportCSV exportCSV = new ExportCSV();
		exportCSV.setEtat(EtatExport.AF);
		exportCSV.setDateDemande(new Date(System.currentTimeMillis()));

		exportCSVService.create(exportCSV);
		return this;

	}

	public Object onDownload(Long id){
		CsvStreamResponse csr = null;
		try {
			ExportCSV exportCSV = exportCSVService.findOne(id);
			StringWriter sw = new StringWriter();
			sw.write(exportCSV.getContent());
			sw.close();
			csr = new CsvStreamResponse(sw, "export_agent");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return csr;
	}


	// Getters n Setters
	public String getSuccessMessage() {
		return this.successMessage;
	}

	public void setSuccessMessage(String message) {
		this.successMessage = message;
	}
}