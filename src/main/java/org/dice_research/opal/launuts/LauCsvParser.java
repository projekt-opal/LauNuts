package org.dice_research.opal.launuts;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * Parses LAU CSV file.
 * 
 * @author Adrian Wilke
 */
public class LauCsvParser {

	public void parse(String file) throws IOException {
		Reader reader = new FileReader(file);
		Iterable<CSVRecord> records = getCsvFormat().parse(reader);
		for (CSVRecord record : records) {
			String lauNameNational = record.get("LAU NAME NATIONAL");

			String simpleName = lauNameNational;
			String[] parts = lauNameNational.split(",");
			if (parts.length > 1) {
				simpleName = parts[0];
			}

			String lauCode = record.get("LAU CODE");

			String nutsCode = record.get("NUTS 3 CODE");

			System.out.println(simpleName + " | " + lauNameNational + " | " + lauCode + " | " + nutsCode);
		}
	}

	private CSVFormat getCsvFormat() {
		return CSVFormat.EXCEL.withHeader();
	}
}