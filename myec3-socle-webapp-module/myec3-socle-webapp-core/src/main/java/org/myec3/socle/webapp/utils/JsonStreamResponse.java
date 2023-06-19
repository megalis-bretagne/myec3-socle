package org.myec3.socle.webapp.utils;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.http.services.Response;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * StreamResponse to export a csv file
 * 
 * @author A109536
 *
 */
public class JsonStreamResponse implements StreamResponse {
	private static final String CHARSET = "UTF-8";
	private InputStream is;
	private String filename = "default";

	public JsonStreamResponse(String filename, InputStream is) {
		this.is =is;
		this.filename = filename;
	}

	public String getContentType() {
		return "application/json";
	}

	public InputStream getStream() throws IOException {
		return is;
	}

	public void prepareResponse(Response arg0) {
		arg0.setHeader("Content-Disposition", "attachment; filename=" + filename + ".json");
	}
}
