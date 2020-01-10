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
package org.myec3.socle.synchro.scheduler.service.impl;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of the Scheduler Service used during synchronization
 * process. This scheduler called simple scheduler (because it use only one
 * thread) is used to initialize synchronization process (@see JMSListenerImpl).
 * 
 * This scheduler is defined into the application context and use a JobStoreTX
 * to save triggers and jobs into the database. The scheduler configuration is
 * definied into quartzScheduler.properties
 * 
 * @see http://www.quartz-scheduler.org/docs/configuration/ConfigJobStoreTX.html
 * 
 * @author Baptiste Meurant <baptiste.meurant@atosorigin.com>
 * @author Denis Cucchietti <denis.cucchietti@atosorigin.com>
 */
@Component("simpleSchedulerService")
public class SimpleSchedulerServiceImpl extends SchedulerServiceImpl {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Autowired
	@Qualifier("quartzScheduler")
	public void setScheduler(Scheduler scheduler) {
		super.setScheduler(scheduler);
	}

}
