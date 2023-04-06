package org.myec3.socle.webapp.pages.organism.agent.export;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.myec3.socle.webapp.pages.AbstractPage;

/**
 * Abstract page containing commons attributes and methods used in all pages of
 * the import.<br />
 * 
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
public class AbstractImport extends AbstractPage {

	@Inject
	private ComponentResources componentResources;

	@Inject
	private Messages messages;

	@Persist(PersistenceConstants.FLASH)
	private String successMessage;

	@Persist(PersistenceConstants.FLASH)
	private String errorMessage;

	@InjectPage
	private Report reportPage;

	public Report getReportPage() {
		return reportPage;
	}

	/**
	 * @return TRUE if the application must display the CREATE menu on the page
	 */
	public Boolean getDisplayCreateMenu() {
		return this.reportPage.getDisplayCreateMenu();
	}

	/**
	 * @return TRUE if the application must display the MODIFY menu on the page
	 */
	public Boolean getDisplayModifyMenu() {
		return this.reportPage.getDisplayModifyMenu();
	}

	/**
	 * @return TRUE if the application must display the DELETE menu on the page
	 */
	public Boolean getDisplayDeleteMenu() {
		return this.reportPage.getDisplayDeleteMenu();
	}

	/**
	 * @return number of agents to create
	 */
	public int getNumberOfAgentsToCreate() {
		return this.reportPage.getNumberOfAgentsToCreate();
	}

	/**
	 * @return number of agents to modify
	 */
	public int getNumberOfAgentsToModify() {
		return this.reportPage.getNumberOfAgentsToModify();
	}

	/**
	 * @return number of agents to delete
	 */
	public int getNumberOfAgentsToDelete() {
		return this.reportPage.getNumberOfAgentsToDelete();
	}

	/**
	 * @return the message object used to read into propertie file
	 */
	public Messages getMessages() {
		return messages;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	/**
	 * @return the success message to display on the page
	 */
	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	/**
	 * @return the error message to display on the page
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * Method used to clear persistent params annoted by @Persist
	 */
	public void clearPersistentParams() {
		this.componentResources.discardPersistentFieldChanges();
	}
}
