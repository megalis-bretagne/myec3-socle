package org.myec3.socle.ws.utils;

public enum ApplicationName {
	TDT("S2low");

	private String name;

	private ApplicationName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
