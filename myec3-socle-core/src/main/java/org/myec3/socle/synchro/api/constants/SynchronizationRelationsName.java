package org.myec3.socle.synchro.api.constants;

public enum SynchronizationRelationsName {


	ROLES("roles"),
	CHILD_STRUCTURES("childStructures"),
	APPLICATIONS("applications"),
	RESPONSIBLES("responsibles");

	private final String value;

	private SynchronizationRelationsName(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
