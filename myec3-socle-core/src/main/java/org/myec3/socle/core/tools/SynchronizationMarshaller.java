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
package org.myec3.socle.core.tools;

import org.apache.activemq.util.ByteArrayInputStream;
import org.myec3.socle.core.domain.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class SynchronizationMarshaller {

	private static final Logger log = LoggerFactory
			.getLogger(SynchronizationMarshaller.class);

	@SuppressWarnings("rawtypes")
	private static Class[] classes = { AdminProfile.class, AgentProfile.class,
			EmployeeProfile.class, Customer.class, ProjectAccount.class, Person.class,
			Organism.class, Company.class, OrganismDepartment.class,
			CompanyDepartment.class, Establishment.class, Structure.class, User.class, Role.class,
			Application.class, StructureRelations.class };

	/**
	 * Default constructor. Do nothing.
	 */
	private SynchronizationMarshaller() {
	}

	/**
	 * Return a marshall expression of the given {@link Resource}
	 * 
	 * @param resource
	 *            : the {@link Resource} to marshall
	 * @return a ByteArray stream
	 */
	public static ByteArrayOutputStream marshalResource(Resource resource) {

		JAXBContext jaxbContext;
		Marshaller marshaller;
		OutputStream baOutputStream = null;

		try {
			// We use a deep copy of the resource in order
			jaxbContext = JAXBContext.newInstance(classes);
			marshaller = jaxbContext.createMarshaller();
			baOutputStream = new ByteArrayOutputStream();

			marshaller.marshal(resource, baOutputStream);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
		} catch (JAXBException e) {
			log.error(
					"An error has occured during marshal the resource with ID : "
							+ resource.getId(), e);
		} catch (Exception e) {
			log.error(
					"An error has occured during marshal the resource with ID : "
							+ resource.getId(), e);
		}

		return (ByteArrayOutputStream) baOutputStream;
	}

	/**
	 * Return an object of a marshalled resource
	 * 
	 * @param xml
	 *            : Marshalled resource
	 * @return Unmarshalled object
	 */
	public static Object unmarshalResource(String xml) {

		JAXBContext jaxbContext;
		Unmarshaller unmarshaller;
		InputStream resourceIS;
		Object unmarshalResource = null;

		try {
			jaxbContext = JAXBContext.newInstance(classes);
			unmarshaller = jaxbContext.createUnmarshaller();
			resourceIS = new ByteArrayInputStream(xml.getBytes());
			unmarshalResource = unmarshaller.unmarshal(resourceIS);
		} catch (JAXBException e) {
			log.error("An error has occured during unmarshal the String xml"
					+ e);
		} catch (Exception e) {
			log.error("An error has occured during unmarshal the String xml"
					+ e);
		}

		return unmarshalResource;
	}
}
