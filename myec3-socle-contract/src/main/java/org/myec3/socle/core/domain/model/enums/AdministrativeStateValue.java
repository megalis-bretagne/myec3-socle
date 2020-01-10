package org.myec3.socle.core.domain.model.enums;

import java.util.EnumMap;

public enum AdministrativeStateValue {
	
	STATUS_ACTIVE("Actif"),
	STATUS_CEASED("Cessée"),
	STATUS_UNKNOWN("Non connu");
	
	private final String label;

	/**
	 * Constructor. Initialize the label of the administrative state value.
	 * 
	 * @param label
	 *            : the label of the administrative state value
	 */
	private AdministrativeStateValue(String label) {
		this.label = label;
	}

	/**
	 * @return the label of the administrative state value
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.label;
	}

	/**
	 * 
	 * @return
	 */
	public static EnumMap<AdministrativeStateValue, String> getEnumMap() {
		EnumMap<AdministrativeStateValue, String> statusMap = new EnumMap<AdministrativeStateValue, String>(
				AdministrativeStateValue.class);
		for (AdministrativeStateValue type : AdministrativeStateValue.values()) {
			statusMap.put(type, type.getLabel());
		}
		return statusMap;
	}

	/**
	 * 
	 * @param administrativeStateValue
	 * @return
	 */
	public static AdministrativeStateValue getTypeValue(String administrativeStateValue) {
		for (AdministrativeStateValue entry : AdministrativeStateValue.values()) {
			if (entry.toString().equals(administrativeStateValue)) {
				return entry;
			}
		}
		return null;
	}
	
}
