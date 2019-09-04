package org.dice_research.opal.launuts;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * Parses LAU CSV file.
 * 
 * @author Adrian Wilke
 */
public class LauCsvParser {

	public final static String HEADER_LAUCODE = "LAU CODE";
	public final static String HEADER_LAUNAMELATIN = "LAU NAME LATIN";
	public final static String HEADER_LAUNAMENATIONAL = "LAU NAME NATIONAL";
	public final static String HEADER_NUTS3CODE = "NUTS 3 CODE";

	private CSVFormat getCsvFormat() {
		return CSVFormat.EXCEL.withHeader();
	}

	public List<LauContainer> parse(String file) throws IOException {
		List<LauContainer> list = new LinkedList<LauContainer>();
		Reader reader = new FileReader(file);
		Iterable<CSVRecord> records = getCsvFormat().parse(reader);
		for (CSVRecord record : records) {
			LauContainer container = new LauContainer();
			container.lauCode = record.get(HEADER_LAUCODE);
			container.lauNameLatin = record.get(HEADER_LAUNAMELATIN);
			container.lauNameNational = record.get(HEADER_LAUNAMENATIONAL);
			container.nuts3code = record.get(HEADER_NUTS3CODE);
			list.add(container);
		}
		return list;
	}
}