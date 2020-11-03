package org.dice_research.opal.launuts.archive.lau;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

	private List<LauContainer> lauList = new LinkedList<LauContainer>();

	private CSVFormat getCsvFormat() {
		return CSVFormat.EXCEL.withHeader();
	}

	public LauCsvParser parse(String file) throws IOException {
		Reader reader = new FileReader(file);
		Iterable<CSVRecord> records = getCsvFormat().parse(reader);
		for (CSVRecord record : records) {
			LauContainer container = new LauContainer();
			container.lauCode = record.get(HEADER_LAUCODE);
			container.lauNameLatin = record.get(HEADER_LAUNAMELATIN);
			container.lauNameNational = record.get(HEADER_LAUNAMENATIONAL);
			container.nuts3code = record.get(HEADER_NUTS3CODE);
			lauList.add(container);
		}
		return this;
	}

	public List<LauContainer> getLauList() {
		return lauList;
	}

	public static Map<String, LauContainer> createLauCodeToContainer(Collection<LauContainer> lauContainerList) {
		Map<String, LauContainer> map = new HashMap<String, LauContainer>();
		for (LauContainer container : lauContainerList) {
			map.put(container.lauCode, container);
		}
		return map;
	}
}