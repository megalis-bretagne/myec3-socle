package org.myec3.socle.webapp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.http.services.Response;
import org.apache.tapestry5.util.TextStreamResponse;

/**
 * 
 * StreamResponse to export a csv file
 * 
 * @author A109536
 *
 */
public class CsvStreamResponse implements StreamResponse {
	private static final String CHARSET = "UTF-8";
	private InputStream is;
	private String filename = "default";

	public CsvStreamResponse(StringWriter sw, String... args) throws IOException {
		// We use an intermediate TextStreamResponse to obtain an InputStream
		// from the StringWriter
		TextStreamResponse tsr = new TextStreamResponse("text/plain", CHARSET, "\ufeff" + sw.toString());

		this.is = tsr.getStream();

		if (args != null) {
			this.filename = args[0];
		}
	}

	public String getContentType() {
		return "text/csv";
	}

	public InputStream getStream() throws IOException {
		return is;
	}

	public void prepareResponse(Response arg0) {
		arg0.setHeader("Content-Disposition", "attachment; filename=" + filename + ".csv");
	}
}
