package org.dice_research.opal.launuts.matcher;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.dice_research.opal.launuts.Cache;
import org.dice_research.opal.launuts.Cfg;
import org.dice_research.opal.launuts.Vocabularies;
import org.dice_research.opal.launuts.dbpedia.DbpediaPlaceContainer;
import org.dice_research.opal.launuts.lau.LauContainer;
import org.dice_research.opal.launuts.nuts.NutsContainer;
import org.dice_research.opal.launuts.utils.GeoUtil;

/**
 * Matches labels (region names) of different datasets.
 * 
 * @author Adrian Wilke
 */
public class MatcherVersion2 {

	// Containers for results (URI to URI)
	private HashMap<String, String> lauToDbpediaUris = new HashMap<String, String>();
	private HashMap<String, String> nutsToDbpediaUrisFuzzy = new HashMap<String, String>();
	private HashMap<String, String> nutsToDbpediaUrisStatic = new HashMap<String, String>();
	private HashMap<String, String> nutsToDbpediaUris = new HashMap<String, String>();

	// Results of previous steps (URI to container)
	private Map<String, DbpediaPlaceContainer> dbpediaUriIndex = new HashMap<String, DbpediaPlaceContainer>();
	private Map<String, LauContainer> lauCodeIndex = new HashMap<String, LauContainer>();
	private Map<String, NutsContainer> nutsNotationIndex = new HashMap<String, NutsContainer>();

	// For computation: Labels to lists of URIs
	private Map<String, List<String>> dbpediaLabelToUris = new HashMap<String, List<String>>();
	private Map<String, List<String>> lauNameToUris = new HashMap<String, List<String>>();
	private Map<String, List<String>> nutsLabelToUris = new HashMap<String, List<String>>();

	// Files
	File fileNoMatchSimplified = new File(Cfg.getInstance().get(Cfg.OUT_DIRECTORY), "no-match-simplified.txt");
	File fileNoMatchExact = new File(Cfg.getInstance().get(Cfg.OUT_DIRECTORY), "no-match-labels.txt");

	// Configuration
	private static final int PRINT_MAX_LENGTH = 5;
	public int timeoutAfterLoadingData = 0;

	// Deactivated, as DBpedia sometimes contains wrong data.
	private static final boolean USE_DBPEDIA_NUTS = true;

	public MatcherVersion2 run() throws Exception {

		if (timeoutAfterLoadingData > 0) {
			Cache.getDbpedia(true);
			Cache.getLau(true);
			Cache.getNuts(true);
			Thread.sleep(timeoutAfterLoadingData);
		}

		print("Initial");

		// Get data containers and prepare indexes
		getData();
		print("Got data");

		// Add 16 federal states as static matches
		setFederalStates();
		print("Extracted federal states");

		// Add DBpedia NUTS information
		if (USE_DBPEDIA_NUTS) {
			setStaticNuts();
			print("Static");
		}

		// Prepare labels
		prepareDbpediaLabels(Cache.getDbpedia(true));
		prepareLauLabels(Cache.getLau(true));
		prepareNutsLabels(Cache.getNuts(true).values());
		print("Prepared labels");

		// Compare labels without modifying them
		exactMatching();
		print("Exact matching finished");

		// Simplify labels
		simplifiedMatching();
		print("Simplified matching finished");

		// Combine results
		nutsToDbpediaUris.putAll(nutsToDbpediaUrisFuzzy);
		nutsToDbpediaUris.putAll(nutsToDbpediaUrisStatic);
		StringBuilder stringBuilder = new StringBuilder();
		printAdd(stringBuilder, "nutsToDbpediaUris", nutsToDbpediaUris.size());
		System.out.println(stringBuilder);

		return this;
	}

	/**
	 * LAU URIs to DBpedia URIs.
	 */
	public Map<String, String> getLauToDbpedia() {
		return lauToDbpediaUris;
	}

	/**
	 * NUTS URIs to DBpedia URIs.
	 */
	public Map<String, String> getNutsToDbpedia() {
		return nutsToDbpediaUris;
	}

	private void getData() throws Exception {

		// DBpedia
		for (DbpediaPlaceContainer container : Cache.getDbpedia(true)) {
			dbpediaUriIndex.put(container.uri, container);
		}

		// LAU
		for (LauContainer container : Cache.getLau(true)) {
			lauCodeIndex.put(container.lauCode, container);
		}

		// NUTS
		nutsNotationIndex = Cache.getNuts(true);
	}

	private void prepareDbpediaLabels(List<DbpediaPlaceContainer> containers) {
		for (DbpediaPlaceContainer container : containers) {
			List<String> list = new LinkedList<String>();
			list.add(container.uri);
			if (container.labelDe != null) {
				addListItemsToMap(dbpediaLabelToUris, container.labelDe, list);
			}
			if (container.labelEn != null && !container.labelDe.equals(container.labelDe)) {
				addListItemsToMap(dbpediaLabelToUris, container.labelEn, list);
			}
		}
	}

	private void prepareLauLabels(List<LauContainer> containers) throws Exception {
		lauNameToUris = new HashMap<String, List<String>>();
		for (LauContainer container : containers) {
			List<String> list = new LinkedList<String>();
			list.add(container.getUri());
			addListItemsToMap(lauNameToUris, container.lauNameLatin, list);
		}
	}

	private void prepareNutsLabels(Collection<NutsContainer> containers) throws Exception {
		nutsLabelToUris = new HashMap<String, List<String>>();
		for (NutsContainer container : containers) {
			List<String> list = new LinkedList<String>();
			list.add(container.getUri());
			for (String prefLabel : container.prefLabel) {
				addListItemsToMap(nutsLabelToUris, prefLabel, list);
			}
		}
	}

	/**
	 * Sets 16 federal states.
	 * 
	 * Updates {@link #nutsToDbpediaUrisStatic}.
	 */
	private void setFederalStates() {
		int resultsSize = nutsToDbpediaUrisFuzzy.size();

		// Go through federal states
		for (Entry<String, String> nutsToDbpedia : new StaticMappings().getNutsToDbPediaFederalStates().entrySet()) {

			// Insert NUTS results
			nutsToDbpediaUrisStatic.put(nutsToDbpedia.getKey(), nutsToDbpedia.getValue());
		}

		// Check
		if (resultsSize + 16 != nutsToDbpediaUrisStatic.size()) {
			System.err.println("Warning: Federal state results. " + MatcherVersion2.class.getSimpleName());
		}
	}

	/**
	 * Uses NUTS information extracted from DBpedia.
	 * 
	 * There is sometimes wrong information in DBpedia (state: 2019-10-01).
	 * 
	 * Updates {@link #nutsToDbpediaUrisStatic}.
	 */
	private void setStaticNuts() {
		for (Entry<String, DbpediaPlaceContainer> dbpediaElement : dbpediaUriIndex.entrySet()) {
			String nutsCode = dbpediaElement.getValue().nuts;
			if (nutsCode != null) {
				if (nutsNotationIndex.containsKey(nutsCode)) {
					nutsToDbpediaUrisStatic.put(nutsNotationIndex.get(nutsCode).getUri(), dbpediaElement.getKey());
				} else {
					System.err.println("Warning: Unknown static NUTS. " + nutsCode + " " + dbpediaElement.getKey() + " "
							+ MatcherVersion2.class.getSimpleName());
				}
			}
		}
	}

	/**
	 * Compares labels without modifying them.
	 * 
	 * Updates {@link #lauToDbpediaUris} and {@link #nutsToDbpediaUrisFuzzy}.
	 */
	private void exactMatching() throws IOException {

		// Create lists of labels
		List<String> labelsDbpedia = new LinkedList<String>(dbpediaLabelToUris.keySet());
		List<String> labelsLau = new LinkedList<String>(lauNameToUris.keySet());
		List<String> labelsNuts = new LinkedList<String>(nutsLabelToUris.keySet());

		// LAU + DBpedia matching
		String lauLabel;
		for (int i = labelsLau.size() - 1; i >= 0; i--) {
			lauLabel = labelsLau.get(i);
			if (labelsDbpedia.contains(lauLabel)) {
				// Candidate for match found

				for (String lauUri : lauNameToUris.get(lauLabel)) {
					String nearestMatch = getNearestMatchForLau(lauUri, dbpediaLabelToUris.get(lauLabel));
					if (nearestMatch != null) {
						lauToDbpediaUris.put(lauUri, nearestMatch);
						labelsLau.remove(i);
					}
				}

			}
		}

		// NUTS + DBpedia matching
		String nutsLabel;
		for (int i = labelsNuts.size() - 1; i >= 0; i--) {
			nutsLabel = labelsNuts.get(i);
			if (labelsDbpedia.contains(nutsLabel)) {
				// Candidate for match found

				for (String nutsUri : nutsLabelToUris.get(nutsLabel)) {
					String nearestMatch = getNearestMatchForNuts(nutsUri, dbpediaLabelToUris.get(nutsLabel));
					if (nearestMatch != null) {
						nutsToDbpediaUrisFuzzy.put(nutsUri, nearestMatch);
						labelsNuts.remove(i);
					}
				}

			}
		}

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(collectionToStringBuilder(labelsLau, "LAU"));
		stringBuilder.append(collectionToStringBuilder(labelsNuts, "NUTS"));
		FileUtils.write(fileNoMatchExact, stringBuilder, StandardCharsets.UTF_8);
	}

	private void simplifiedMatching() throws IOException {

		// Create lists of simplified labels
		Map<String, List<String>> simplifiedDbpedia = simplify(dbpediaLabelToUris, false);
		Map<String, List<String>> simplifiedLau = simplify(lauNameToUris, false);
		Map<String, List<String>> simplifiedNuts = simplify(nutsLabelToUris, false);
		List<String> labelsDbpedia = new LinkedList<String>(simplifiedDbpedia.keySet());
		List<String> labelsLau = new LinkedList<String>(simplifiedLau.keySet());
		List<String> labelsNuts = new LinkedList<String>(simplifiedNuts.keySet());

		// LAU + DBpedia matching
		String lauLabel;
		for (int i = labelsLau.size() - 1; i >= 0; i--) {
			lauLabel = labelsLau.get(i);
			if (labelsDbpedia.contains(lauLabel)) {
				// Candidate for match found

				for (String lauUri : simplifiedLau.get(lauLabel)) {
					String nearestMatch = getNearestMatchForLau(lauUri, simplifiedDbpedia.get(lauLabel));
					if (nearestMatch != null) {
						lauToDbpediaUris.put(lauUri, nearestMatch);
					}
				}
				labelsLau.remove(i);
			}
		}

		// NUTS + DBpedia matching
		String nutsLabel;
		for (int i = labelsNuts.size() - 1; i >= 0; i--) {
			nutsLabel = labelsNuts.get(i);
			if (labelsDbpedia.contains(nutsLabel)) {
				// Candidate for match found

				for (String nutsUri : simplifiedNuts.get(nutsLabel)) {
					String nearestMatch = getNearestMatchForNuts(nutsUri, simplifiedDbpedia.get(nutsLabel));
					if (nearestMatch != null) {
						nutsToDbpediaUrisFuzzy.put(nutsUri, nearestMatch);
					}
				}
				labelsNuts.remove(i);
			}
		}

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(collectionToStringBuilder(labelsLau, "LAU"));
		stringBuilder.append(collectionToStringBuilder(labelsNuts, "NUTS"));
		FileUtils.write(fileNoMatchSimplified, stringBuilder, StandardCharsets.UTF_8);
	}

	// Checks keys of given map. If the key contains characters like ',' or '(', the
	// key is shortened. Key and related values are added to a new map.
	private Map<String, List<String>> simplify(Map<String, List<String>> map, boolean addOnlySimplified) {
		Map<String, List<String>> simplifiedMap = new HashMap<String, List<String>>();
		for (String key : map.keySet()) {
			String newKey = key.toLowerCase();
			boolean simplified = false;
			if (newKey.contains(",")) {
				newKey = newKey.substring(0, newKey.indexOf(",")).trim();
				simplified = true;
			}
			if (newKey.contains("(")) {
				newKey = newKey.substring(0, newKey.indexOf("(")).trim();
				simplified = true;
			}
			if (newKey.contains("/")) {
				newKey = newKey.substring(0, newKey.indexOf("/")).trim();
				simplified = true;
			}
			if (newKey.contains(" a.")) {
				newKey = newKey.substring(0, newKey.indexOf(" a.")).trim();
				simplified = true;
			}
			if (newKey.contains(" i.")) {
				newKey = newKey.substring(0, newKey.indexOf(" i.")).trim();
				simplified = true;
			}
			if (newKey.contains(" b.")) {
				newKey = newKey.substring(0, newKey.indexOf(" b.")).trim();
				simplified = true;
			}
			if (newKey.contains(" v.")) {
				newKey = newKey.substring(0, newKey.indexOf(" v.")).trim();
				simplified = true;
			}
			if (simplified || !addOnlySimplified) {
				addListItemsToMap(simplifiedMap, newKey, map.get(key));
			}
		}
		return simplifiedMap;
	}

	/**
	 * Gets nearest DBpedia URI for given LAU URI.
	 */
	private String getNearestMatchForLau(String lauUri, List<String> dbpediaUris) {
		String lauCode = lauUri.substring(lauUri.lastIndexOf('/') + 1);
		LauContainer lauContainer = lauCodeIndex.get(lauCode);
		DbpediaPlaceContainer dbpediaFederalState = getDbpedia(getNutsLevel1(lauContainer.nuts3code).notation);
		return getNearestMatch(dbpediaFederalState, dbpediaUris);
	}

	/**
	 * Gets nearest DBpedia URI for given NUTS URI.
	 */
	private String getNearestMatchForNuts(String nutsUri, List<String> dbpediaUris) {
		String nutsCode = nutsUri.substring(nutsUri.lastIndexOf('/') + 1);
		DbpediaPlaceContainer dbpediaFederalState = getDbpedia(getNutsLevel1(nutsCode).notation);
		return getNearestMatch(dbpediaFederalState, dbpediaUris);
	}

	/**
	 * Gets nearest DBpedia URI for source DBpedia URI.
	 */
	private String getNearestMatch(DbpediaPlaceContainer dbpediaFederalState, List<String> dbpediaUris) {
		double minDistance = Double.MAX_VALUE;
		String minDbpediaUri = null;

		for (String dbpediaUri : dbpediaUris) {
			DbpediaPlaceContainer dbpedia = dbpediaUriIndex.get(dbpediaUri);

			// Do not use candidate, if it is not in current federal state.
			if (dbpedia.nuts != null && dbpediaFederalState.nuts != null) {
				if (dbpedia.nuts.startsWith(dbpediaFederalState.nuts)) {
					continue;
				}
			}

			// Select candidate with minimum distance.
			double distance = GeoUtil.getDistance(dbpediaFederalState.lat, dbpediaFederalState.lon, dbpedia.lat,
					dbpedia.lon);
			if (distance < minDistance) {
				minDistance = distance;
				minDbpediaUri = dbpedia.uri;
			}
		}

		return minDbpediaUri;
	}

	private void addListItemsToMap(Map<String, List<String>> map, String key, List<String> list) {
		if (!map.containsKey(key)) {
			map.put(key, new LinkedList<String>());
		}
		List<String> mapList = map.get(key);
		for (String listItem : list) {
			if (!mapList.contains(listItem)) {
				mapList.add(listItem);
			}
		}
	}

	StringBuilder mapToStringBuilder(Map<String, List<String>> map, String heading) {
		StringBuilder stringBuilder = new StringBuilder();
		for (Entry<String, List<String>> entry : map.entrySet()) {
			stringBuilder.append(heading + "  " + entry.getKey() + "  " + entry.getValue() + "\n");
		}
		return stringBuilder;
	}

	StringBuilder collectionToStringBuilder(Collection<String> collection, String heading) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String string : collection) {
			stringBuilder.append(heading + "  " + string + "\n");
		}
		return stringBuilder;
	}

	private NutsContainer getNutsLevel1(String nutsCode) {
		return nutsNotationIndex.get(nutsCode.substring(0, 3));
	}

	private DbpediaPlaceContainer getDbpedia(String nutsCode) {
		return dbpediaUriIndex.get(nutsToDbpediaUrisStatic.get(Vocabularies.NS_EU_NUTS_CODE + nutsCode));
	}

	void print(String headline) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("> ");
		stringBuilder.append(headline);
		stringBuilder.append(System.lineSeparator());
		// Results of previous steps (URI to container)
		printAdd(stringBuilder, "dbpediaUriIndex", dbpediaUriIndex.size());
		printAdd(stringBuilder, "lauCodeIndex", lauCodeIndex.size());
		printAdd(stringBuilder, "nutsNotationIndex", nutsNotationIndex.size());
		stringBuilder.append(System.lineSeparator());
		// For computation: Labels to lists of URIs
		printAdd(stringBuilder, "dbpediaLabelToUris", dbpediaLabelToUris.size());
		printAdd(stringBuilder, "lauNameToUris", lauNameToUris.size());
		printAdd(stringBuilder, "nutsLabelToUris", nutsLabelToUris.size());
		stringBuilder.append(System.lineSeparator());
		// Containers for results (URI to URI)
		printAdd(stringBuilder, "lauToDbpediaUris", lauToDbpediaUris.size());
		printAdd(stringBuilder, "nutsToDbpediaUris", nutsToDbpediaUrisFuzzy.size());
		printAdd(stringBuilder, "nutsToDbpediaUrisStatic", nutsToDbpediaUrisStatic.size());
		System.out.println(stringBuilder.toString());
	}

	void printAdd(StringBuilder stringBuilder, String label, int size) {
		for (int i = String.valueOf(size).length(); i < PRINT_MAX_LENGTH; i++) {
			stringBuilder.append(" ");
		}
		stringBuilder.append(size);

		stringBuilder.append(" ");
		stringBuilder.append(label);
		stringBuilder.append(System.lineSeparator());
	}

}