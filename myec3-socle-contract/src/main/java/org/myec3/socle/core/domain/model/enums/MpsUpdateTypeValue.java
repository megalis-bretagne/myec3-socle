package org.myec3.socle.core.domain.model.enums;

public enum MpsUpdateTypeValue {

    // the MpsUpdateType is a manual
    MANUAL("1"),
    // the MpsUpdateType is set with ConnectionInfo
    CONNECTIONINFO("2"),
    //the MpsUpdateType is Automatic
    AUTOMATIC("3");

    
    private final String label;

    private MpsUpdateTypeValue(String label) {
	this.label = label;
    }

    public String getLabel() {
	return label;
    }

    @Override
    public String toString() {
	return this.name();
    }
}
