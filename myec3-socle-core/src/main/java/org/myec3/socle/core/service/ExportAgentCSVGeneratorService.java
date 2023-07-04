package org.myec3.socle.core.service;

import au.com.bytecode.opencsv.CSVWriter;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Organism;

import java.io.IOException;
import java.util.List;

/**
 * Service responsible to export a list of agents in CSV format.
 */
public interface ExportAgentCSVGeneratorService {
	char CSV_DEFAULT_SEPARATOR = ';';

	/**
	 * Export all the agents of the organisms in CSV format and append the result to the passed csvWriter.
	 *
	 * @param organismsList list of organisms to export
	 * @param applicationsList list of applications to dynamically determine the role columns to include in the output
	 * @param csvWriter writer where the output will be appended
	 * @throws IOException in case of error
	 */
	void writeCsv(List<Organism> organismsList, List<Application> applicationsList, CSVWriter csvWriter) throws IOException;
}
