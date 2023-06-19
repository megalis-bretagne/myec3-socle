package org.myec3.socle.webapp.utils;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.http.services.Response;
import org.apache.tapestry5.util.TextStreamResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * 
 * StreamResponse to export a csv file
 * 
 * @author A109536
 *
 */
public class XmlStreamResponse implements StreamResponse {
	private static final String CHARSET = "UTF-8";
	private InputStream is;
	private String filename = "default";

	public XmlStreamResponse(String filename, InputStream is) {
		this.is =is;
		this.filename = filename;
	}

	public String getContentType() {
		return "application/xml";
	}

	public InputStream getStream() throws IOException {
		return is;
	}

	public void prepareResponse(Response arg0) {
		arg0.setHeader("Content-Disposition", "attachment; filename=" + filename + ".xml");
	}
}
