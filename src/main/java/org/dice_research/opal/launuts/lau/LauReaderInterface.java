package org.dice_research.opal.launuts.lau;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Interface for LAU reader.
 * 
 * TODO This is a development recommendation, changes are allowed.
 * 
 * @see https://ec.europa.eu/eurostat/web/nuts/local-administrative-units
 *      "Correspondence table LAU – NUTS 2016, EU-28 and EFTA / available Candidate Countries; 2019; 23 MB"
 * @see https://hobbitdata.informatik.uni-leipzig.de/OPAL/LauNuts/Sources/
 * 
 * @author Adrian Wilke
 */
public interface LauReaderInterface {

	/**
	 * Sets source directory for LAU reader. Should contain XLSX and/or CSV files.
	 */
	public LauReaderInterface setLauSourceDirectory(File directory) throws LauReaderException;

	/**
	 * Returns a list of available country IDs.
	 */
	public List<String> getCountryIds() throws LauReaderException;

	/**
	 * Gets a map NUTS-code to list of LAU codes for the given countryId.
	 */
	public Map<String, List<String>> getCodes(String countryId) throws LauReaderException;

	/**
	 * Returns a container object containing parsed data for a country.
	 * 
	 * TODO Dev note: Changing the structure auf {@link LauContainer} is absolutely
	 * allowed.
	 */
	public LauContainer getData(String nutsCode, String lauCode) throws LauReaderException;

	/**
	 * Returns a list of keys, which are generated using the headings of spreadsheet
	 * rows. Data for each country uses the same keys. The keys are also used in LAU
	 * container objects.
	 */
	public List<String> getKeys() throws LauReaderException;
}
