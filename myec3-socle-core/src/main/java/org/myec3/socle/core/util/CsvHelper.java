package org.myec3.socle.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.myec3.socle.core.tools.UnaccentLetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

public class CsvHelper {

	private static Logger logger = LoggerFactory.getLogger(CsvHelper.class);
	private static final String FILE_ENCODING = "UTF-8";
	private static final String FILE_EXTENSION = ".csv";
	private static final char FILE_SEPARATOR = ';';

	/**
	 * @param filePath
	 * @param fileName
	 * @return True if file exists false otherwise.
	 */
	public static Boolean isFileExists(String filePath, String fileName) {
		File tmpFile = new File(filePath + fileName + FILE_EXTENSION);
		if (tmpFile.exists()) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * @param filePath
	 * @return True if file exists false otherwise.
	 */
	public static Boolean isFileExists(String filePath) {
		File tmpFile = new File(filePath);
		if (tmpFile.exists()) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * @param filePath
	 * @param fileName
	 * @return a file corresponding at the resource path and file name given
	 */
	public static File getResource(String filePath, String fileName) {
		File file = new File(filePath + fileName + FILE_EXTENSION);
		return file;
	}

	/**
	 * @param file
	 *            : the file to count number of lines
	 * @return the number of lines of the current file
	 * @throws Exception
	 */
	public static int countLines(File file) throws Exception {
		int cpt = 0;
		CSVReader reader = null;
		char csvSeparator = ';';
		reader = new CSVReader(new FileReader(file),csvSeparator);
		String[] nextLine;

		while ((nextLine = reader.readNext()) != null) {
			if (nextLine.length > 0) {
				cpt++;
			}
		}
		logger.info("File " + file.getName() + " contains " + cpt + " lines.");
		return cpt;
	}

	public static int countLines(InputStream inputStream) throws Exception {
		int cpt = 0;
		CSVReader reader = null;
		reader = new CSVReader(new InputStreamReader(inputStream));
		while ((reader.readNext()) != null) {
			cpt++;
		}
		logger.info("File contains " + cpt + " lines.");
		return cpt;
	}

	/**
	 * @return the list of authorized mime type for csv extension
	 */
	public static List<String> getAuthorizedMimeType() {
		List<String> authorizedCsvMimeType = new ArrayList<String>();
		authorizedCsvMimeType.add("application/csv-tab-delimited-table");
		authorizedCsvMimeType.add("text/csv");
		authorizedCsvMimeType.add("application/vnd.ms-excel");
		/**
		 * Allow this content type because jcarnot and VGoujon from CG59 when they were uploading csv, detected mime type is wrong on Firefox & IE
		 * Nevertheless, check is done on the file extension (csv accepted only)
		 */
		authorizedCsvMimeType.add("text/html");
		authorizedCsvMimeType.add("application/octet-stream");
		return authorizedCsvMimeType;
	}

	/**
	 * Delete file
	 * 
	 * @param filePath
	 *            : path of the file
	 * @throws IOException
	 */
	public static void deleteFile(String filePath) throws IOException {
		File file = new File(filePath);		
		FileUtils.forceDelete(file);
	}

	public static void deleteFile(File file) throws IOException {
		file.setWritable(true);
		FileUtils.forceDelete(file);
	}

	/**
	 * Allows to retrieve file's headers (the first line)
	 * 
	 * @param file
	 *            : the file to read
	 * @return file's headers
	 * @throws Exception
	 */
	public static String[] getHeaders(File file) throws Exception {
		CSVReader reader = null;
		String[] nextLine;
		reader = new CSVReader(new InputStreamReader(new FileInputStream(
				file.getAbsolutePath()), FILE_ENCODING), FILE_SEPARATOR);
		nextLine = reader.readNext();
		return nextLine;
	}

	public static String[] getHeaders(InputStream inputStream) throws Exception {
		CSVReader reader = null;
		String[] nextLine;
		reader = new CSVReader(new InputStreamReader((inputStream),
				FILE_ENCODING), FILE_SEPARATOR);
		nextLine = reader.readNext();
		return nextLine;
	}

	public static List<Map<String, String>> getMappedData(File file)
			throws Exception {
		// Init the size of mappedData
		List<Map<String, String>> mappedData = new ArrayList<Map<String, String>>(
				countLines(file));
		int countLine = 0;
		// We retrieve headers length
		String[] headers = getHeaders(file);
		int headersLength = headers.length;

		// We read the file
		CSVReader reader = null;
		reader = new CSVReader(new InputStreamReader(new FileInputStream(
				file.getAbsolutePath()), FILE_ENCODING), FILE_SEPARATOR);
		List<String[]> values = reader.readAll();

		for (String[] oneData : values) {
			countLine++;
			if (oneData.length == headersLength) {
				final Map<String, String> map = new HashMap<String, String>();
				for (int i = 0; i < headersLength; i++) {
					final String key = UnaccentLetter.unaccentString(headers[i]).toLowerCase();
					final String value = oneData[i];
					map.put(key, value);
				}
				mappedData.add(map);
			} else {
				logger.warn("Line " + countLine
						+ " doesn't respect the number of columns. Line skipped.");
			}
		}

		// Remove headers
		mappedData.remove(0);

		return mappedData;
	}

	/**
	 * @return csv extension
	 */
	public static String getExtension() {
		return FILE_EXTENSION;
	}

	/**
	 * @param extension
	 *            : the extension to check
	 * @return true if extension is correct, false otherwise
	 */
	public static Boolean isCsvExtension(String extension) {
		if (extension != null) {
			return FILE_EXTENSION.equals(extension);
		}
		return Boolean.FALSE;
	}
}
