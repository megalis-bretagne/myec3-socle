//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.05.10 at 11:31:03 AM CEST 
//

package org.myec3.socle.ws.client.impl.mps;

import org.myec3.socle.ws.client.impl.mps.response.ResponseEntreprises;
import org.myec3.socle.ws.client.impl.mps.response.ResponseEtablissements;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the org.myec3.structures.ref.domain.model
 * package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package: org.myec3.structures.ref.domain.model
	 * 
	 */
	public ObjectFactory() {
	}

	public ResponseEtablissements createResponseEtablissements() {
		return new ResponseEtablissements();
	}

	public ResponseEntreprises createResponseEntreprises() {
		return new ResponseEntreprises();
	}

}
