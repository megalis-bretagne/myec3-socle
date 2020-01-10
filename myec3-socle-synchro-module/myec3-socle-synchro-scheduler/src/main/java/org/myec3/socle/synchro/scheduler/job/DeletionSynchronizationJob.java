/**
* Copyright (c) 2011 Atos Bourgogne
* 
* This file is part of MyEc3.
* 
* MyEc3 is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as published by
* the Free Software Foundation.
* 
* MyEc3 is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with MyEc3. If not, see <http://www.gnu.org/licenses/>.
*/
package org.myec3.socle.synchro.scheduler.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * Concrete job implementation for deletion synchronization tasks
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * 
 */
@Component
public class DeletionSynchronizationJob extends GenericSynchronizationJob {

	/**
	 * Method inherited from quartz framework and called when job is ready to be
	 * launched. Calls the correct service depending on the resource concrete type,
	 * and the method in charge of synchronizing deletion events.
	 * 
	 * @inheritDoc
	 */
	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		this.getSynchronizationManager().synchronizeDeletion(
				this.getResource(), this.getListApplicationIdToResynchronize(),
				this.getSynchronizationType(), this.getSendingApplication());
	}
}
