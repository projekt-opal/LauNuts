package org.dice_research.opal.launuts;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Local Administrative Units (LAU), reader for XSLX files.
 * 
 * Usage: Set file in {@link #LauXslsReader(File)}, read with
 * {@link #extract()}, get results with {@link #getResults()}.
 * 
 * Dev note: The 2019 file consumed too much memory. Maybe the SXSSF (Streaming
 * Usermodel API) can be used to load it.
 * 
 * @see File source:
 *      https://ec.europa.eu/eurostat/web/nuts/local-administrative-units
 * 
 * @author Adrian Wilke
 */
public class LauXslsReader {

	private static final Logger LOGGER = LogManager.getLogger();

	private File file;
	private Map<String, List<LauContainer>> results;

	public LauXslsReader(File lauXslxFile) {
		this.file = lauXslxFile;
	}

	/**
	 * Extracts data. Filtered by {@link LauContainer#isValid()}.
	 */
	public LauXslsReader extract() {

		results = new TreeMap<>();

		// Open XLSX file
		Workbook workbook;
		try {
			workbook = WorkbookFactory.create(file);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// Go through sheets
		Iterator<Sheet> sheetIt = workbook.sheetIterator();
		while (sheetIt.hasNext()) {
			Sheet sheet = sheetIt.next();
			if (checkSheetFormat(sheet)) {
				results.put(sheet.getSheetName(), extractSheetData(sheet));
			}
		}

		// Finally close
		try {
			workbook.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return this;
	}

	/**
	 * Returns results.
	 * 
	 * Map keys: county codes.
	 */
	public Map<String, List<LauContainer>> getResults() {
		return results;
	}

	private boolean checkSheetFormat(Sheet sheet) {

		// Only country IDs
		if (sheet.getSheetName().length() > 2) {
			LOGGER.info("Skipping sheet: " + sheet.getSheetName());
			return false;
		}

		// Check row names
		Iterator<Row> rowIt = sheet.rowIterator();
		if (!rowIt.hasNext()) {
			LOGGER.info("Skipping empty sheet: " + sheet.getSheetName());
			return false;
		}

		// Example headings:
		// NUTS 3 CODE
		// LAU 2 CODE
		// LAU NAME NATIONAL
		// LAU NAME LATIN
		// CHANGE (Y/N)
		// POPULATION
		// TOTAL AREA

		Row row = rowIt.next();
		Iterator<Cell> cellIt = row.iterator();
		int i = 0;
		while (cellIt.hasNext()) {
			Cell cell = cellIt.next();
			String value = cell.getStringCellValue();
			switch (i) {

			case 0:
				if (!value.contains("NUTS") || !value.contains("CODE")) {
					return false;
				}
				break;

			case 1:
				if (!value.contains("LAU") || !value.contains("CODE")) {
					return false;
				}
				break;

			case 3:
				if (!value.contains("LAU") || !value.contains("NAME") || !value.contains("LATIN")) {
					return false;
				}
				break;

			case 5:
				if (!value.contains("POPULATION")) {
					return false;
				}
				break;

			case 6:
				if (!value.contains("AREA")) {
					return false;
				}
				break;

			default:
				break;
			}

			i++;
		}

		return true;
	}

	private List<LauContainer> extractSheetData(Sheet sheet) {

		List<LauContainer> containers = new LinkedList<>();
		Iterator<Row> rowIt = sheet.rowIterator();

		// Skip first row
		if (rowIt.hasNext()) {
			rowIt.next();
		}

		// Iterate
		while (rowIt.hasNext()) {
			LauContainer container = new LauContainer();
			Row row = rowIt.next();
			Iterator<Cell> cellIt = row.iterator();
			int i = 0;
			while (cellIt.hasNext()) {
				Cell cell = cellIt.next();

				switch (i) {

				case 0:
					container.nutsCode = getCellValue(cell);
					break;

				case 1:
					container.lauCode = getCellValue(cell);
					break;

				case 3:
					container.lauNameLatin = getCellValue(cell);
					break;

				case 5:
					try {
						container.population = Integer.valueOf(getCellValue(cell));
					} catch (NumberFormatException e) {
					}
					break;

				case 6:
					try {
						container.population = Integer.valueOf(getCellValue(cell));
					} catch (NumberFormatException e) {
					}
					break;

				default:
					break;
				}

				i++;
			}
			if (container.isValid()) {
				containers.add(container);
			}
		}

		return containers;
	}

	private String getCellValue(Cell cell) {
		try {
			// Strings and empty cells
			return cell.getStringCellValue().trim();
		} catch (IllegalStateException e) {
			try {
				// Numbers
				return Double.valueOf(cell.getNumericCellValue()).toString();
			} catch (IllegalStateException e2) {
				// Errors
				return "";
			}
		}
	}
}