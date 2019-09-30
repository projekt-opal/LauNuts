package org.dice_research.opal.launuts.matcher;

import java.io.File;
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
import org.dice_research.opal.launuts.DbpediaPlaceContainer;
import org.dice_research.opal.launuts.DbpediaRemote;
import org.dice_research.opal.launuts.LauContainer;
import org.dice_research.opal.launuts.LauCsvParser;
import org.dice_research.opal.launuts.NutsContainer;
import org.dice_research.opal.launuts.Vocabularies;
import org.dice_research.opal.launuts.utils.GeoUtil;

/**
 * Matches labels (region names) of different datasets.
 * 
 * @author Adrian Wilke
 */
public class MatcherVersion1 {

	// Results: URIs to URIs
	private HashMap<String, String> lauToDbpediaUris = new HashMap<String, String>();
	private HashMap<String, String> nutsToDbpediaUris = new HashMap<String, String>();

	// URIs to containers
	Map<String, DbpediaPlaceContainer> dbpediaUriIndex;
	Map<String, LauContainer> lauCodeIndex;
	Map<String, NutsContainer> nutsNotationIndex;

	// Labels to lists of URIs
	private Map<String, List<String>> dbpediaLabelToUris;
	private Map<String, List<String>> lauNameToUris;
	private Map<String, List<String>> nutsLabelToUris;

	// Files
	File fileMultipleUris = new File(Cfg.getInstance().get(Cfg.OUT_DIRECTORY), "multiple-uris.htm");
	File fileNoMatchSimplified = new File(Cfg.getInstance().get(Cfg.OUT_DIRECTORY), "no-match-simplified.txt");
	File fileNoMatchLabels = new File(Cfg.getInstance().get(Cfg.OUT_DIRECTORY), "no-match-labels.txt");

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

	public MatcherVersion1 run() throws Exception {

		// Get data
		createDbpediaLabelsToUris();
		createLauNameToUris();
		createNutsLabelsToUris();

		dbpediaUriIndex = DbpediaRemote.createPlacesIndex(Cache.getDbpedia(true));
		lauCodeIndex = LauCsvParser.createLauCodeToContainer(Cache.getLau(true));
		nutsNotationIndex = Cache.getNuts(true);

		// Labels
		List<String> labelsDbpedia = new LinkedList<String>(dbpediaLabelToUris.keySet());
		List<String> labelsLau = new LinkedList<String>(lauNameToUris.keySet());
		List<String> labelsNuts = new LinkedList<String>(nutsLabelToUris.keySet());

		System.out.println();
		System.out.println("LAU: " + lauToDbpediaUris.size());
		System.out.println("NUTS: " + nutsToDbpediaUris.size());
		System.out.println("Remaining LAU labels: " + labelsLau.size());
		System.out.println("Remaining NUTS labels: " + labelsNuts.size());

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(multipleUrisToStringBuilder(dbpediaLabelToUris, "DBpedia"));
		stringBuilder.append(multipleUrisToStringBuilder(lauNameToUris, "LAU"));
		stringBuilder.append(multipleUrisToStringBuilder(nutsLabelToUris, "NUTS"));
		FileUtils.write(fileMultipleUris, stringBuilder, StandardCharsets.UTF_8);

		// ---

		System.out.println();
		System.out.println("Static matches");
		staticMatching();
		System.out.println("LAU: " + lauToDbpediaUris.size());
		System.out.println("NUTS: " + nutsToDbpediaUris.size());
		System.out.println("Remaining LAU labels: " + labelsLau.size());
		System.out.println("Remaining NUTS labels: " + labelsNuts.size());

		// ---

		// Exact matches
		System.out.println();
		System.out.println("Exact matches");

		String label;
		for (int i = labelsLau.size() - 1; i >= 0; i--) {
			label = labelsLau.get(i);
			if (labelsDbpedia.contains(label)) {
				for (String lauUri : lauNameToUris.get(label)) {
					if (dbpediaLabelToUris.get(label).size() > 1) {
						lauToDbpediaUris.put(lauUri, getNearestMatchForLau(lauUri, dbpediaLabelToUris.get(label)));
					} else {
						lauToDbpediaUris.put(lauUri, dbpediaLabelToUris.get(label).get(0));
					}
				}
				labelsLau.remove(i);
			}
		}
		for (int i = labelsNuts.size() - 1; i >= 0; i--) {
			label = labelsNuts.get(i);
			if (labelsDbpedia.contains(label)) {
				for (String nutsUri : nutsLabelToUris.get(label)) {
					if (dbpediaLabelToUris.get(label).size() > 1) {
						nutsToDbpediaUris.put(nutsUri, getNearestMatchForNuts(nutsUri, dbpediaLabelToUris.get(label)));
					} else {
						nutsToDbpediaUris.put(nutsUri, dbpediaLabelToUris.get(label).get(0));
					}

				}
				labelsNuts.remove(i);
			}
		}

		System.out.println("LAU: " + lauToDbpediaUris.size());
		System.out.println("NUTS: " + nutsToDbpediaUris.size());
		System.out.println("Remaining LAU labels: " + labelsLau.size());
		System.out.println("Remaining NUTS labels: " + labelsNuts.size());

		// ---

		// Simplified matches
		System.out.println();
		System.out.println("Simplified matches");

		// Simplified labels to lists of URIs
		Map<String, List<String>> simplifiedDbpedia = simplify(dbpediaLabelToUris, false);
		Map<String, List<String>> simplifiedLau = simplify(lauNameToUris, false);
		Map<String, List<String>> simplifiedNuts = simplify(nutsLabelToUris, false);

		// Simplified labels
		labelsDbpedia = new LinkedList<String>(simplifiedDbpedia.keySet());
		labelsLau = new LinkedList<String>(simplifiedLau.keySet());
		labelsNuts = new LinkedList<String>(simplifiedNuts.keySet());

		for (int i = labelsLau.size() - 1; i >= 0; i--) {
			label = labelsLau.get(i);
			if (labelsDbpedia.contains(label)) {
				for (String lauUri : simplifiedLau.get(label)) {
					if (simplifiedDbpedia.get(label).size() > 1) {
						lauToDbpediaUris.put(lauUri, getNearestMatchForLau(lauUri, simplifiedDbpedia.get(label)));
					} else {
						lauToDbpediaUris.put(lauUri, simplifiedDbpedia.get(label).get(0));
					}
				}
				labelsLau.remove(i);
			}
		}
		for (int i = labelsNuts.size() - 1; i >= 0; i--) {
			label = labelsNuts.get(i);
			if (labelsDbpedia.contains(label)) {
				for (String nutsUri : simplifiedNuts.get(label)) {
					if (simplifiedDbpedia.get(label).size() > 1) {
						nutsToDbpediaUris.put(nutsUri, getNearestMatchForNuts(nutsUri, simplifiedDbpedia.get(label)));
					} else {
						nutsToDbpediaUris.put(nutsUri, simplifiedDbpedia.get(label).get(0));
					}

				}
				labelsNuts.remove(i);
			}
		}
		System.out.println("LAU: " + lauToDbpediaUris.size());
		System.out.println("NUTS: " + nutsToDbpediaUris.size());
		System.out.println("Remaining LAU labels: " + labelsLau.size());
		System.out.println("Remaining NUTS labels: " + labelsNuts.size());

		// --

		stringBuilder = new StringBuilder();
		stringBuilder.append(mapToStringBuilder(simplifiedDbpedia, "DBpedia"));
		stringBuilder.append(mapToStringBuilder(simplifiedLau, "LAU"));
		stringBuilder.append(mapToStringBuilder(simplifiedNuts, "NUTS"));
		FileUtils.write(fileNoMatchSimplified, stringBuilder, StandardCharsets.UTF_8);

		stringBuilder = new StringBuilder();
		stringBuilder.append(collectionToStringBuilder(labelsDbpedia, "DBpedia"));
		stringBuilder.append(collectionToStringBuilder(labelsLau, "LAU"));
		stringBuilder.append(collectionToStringBuilder(labelsNuts, "NUTS"));
		FileUtils.write(fileNoMatchLabels, stringBuilder, StandardCharsets.UTF_8);

		return this;
	}

	NutsContainer getNutsLevel1(String nutsCode) {
		return nutsNotationIndex.get(nutsCode.substring(0, 3));
	}

	DbpediaPlaceContainer getDbpedia(String nutsCode) {
		return dbpediaUriIndex.get(nutsToDbpediaUris.get(Vocabularies.NS_EU_NUTS_CODE + nutsCode));
	}

	/**
	 * Gets nearest DBpedia URI for given LAU URI.
	 */
	private String getNearestMatchForLau(String lauUri, List<String> dbpediaUris) {
		String lauCode = lauUri.substring(lauUri.lastIndexOf('/') + 1);
		LauContainer lauContainer = lauCodeIndex.get(lauCode);
		DbpediaPlaceContainer parentDbpedia = getDbpedia(getNutsLevel1(lauContainer.nuts3code).notation);
		return getNearestMatch(parentDbpedia, dbpediaUris);
	}

	/**
	 * Gets nearest DBpedia URI for given NUTS URI.
	 */
	private String getNearestMatchForNuts(String nutsUri, List<String> dbpediaUris) {
		String nutsCode = nutsUri.substring(nutsUri.lastIndexOf('/') + 1);
		DbpediaPlaceContainer parentDbpedia = getDbpedia(getNutsLevel1(nutsCode).notation);
		return getNearestMatch(parentDbpedia, dbpediaUris);
	}

	/**
	 * Gets nearest DBpedia URI for source DBpedia URI.
	 */
	private String getNearestMatch(DbpediaPlaceContainer sourceDbpedia, List<String> dbpediaUris) {
		double minDistance = Double.MAX_VALUE;
		String minDbpediaUri = null;
		for (String dbpediaUri : dbpediaUris) {
			DbpediaPlaceContainer dbpedia = dbpediaUriIndex.get(dbpediaUri);
			double distance = GeoUtil.getDistance(sourceDbpedia.lat, sourceDbpedia.lon, dbpedia.lat, dbpedia.lon);
			if (distance < minDistance) {
				minDistance = distance;
				minDbpediaUri = dbpedia.uri;
			}
		}
		return minDbpediaUri;
	}

	/**
	 * Inserts static NUTS-1/DBpedia URIs to {@link #nutsToDbpediaUris}.
	 * 
	 * Removes NUTS entries from {@link #nutsLabelToUris}.
	 */
	private void staticMatching() {
		List<String> nutsLabelsToRemove = new LinkedList<String>();
		for (Entry<String, String> n2d : new StaticMappings().getNutsToDbPediaFederalStates().entrySet()) {

			// Insert static URIs
			nutsToDbpediaUris.put(n2d.getKey(), n2d.getValue());

			// Remember label to remove
			for (Entry<String, List<String>> nutsEntry : nutsLabelToUris.entrySet()) {
				if (nutsEntry.getValue().contains(n2d.getKey())) {
					nutsLabelsToRemove.add(nutsEntry.getKey());
					break;
				}
			}
			if (nutsLabelsToRemove.size() == 16) {
				break;
			}
		}

		// Remove NUTS label
		int nutsSize = nutsLabelToUris.size();
		for (String nutsLabel : nutsLabelsToRemove) {
			nutsLabelToUris.remove(nutsLabel);
		}
		if (nutsLabelsToRemove.size() != 16) {
			System.err.println("Warning: NUTS-1 static URIs not complete. " + MatcherVersion1.class.getSimpleName());
		}
		if (nutsSize != nutsLabelToUris.size() + 16) {
			System.err.println("Warning: NUTS-1 labels not complete. " + MatcherVersion1.class.getSimpleName());
		}

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
				addListItemsToMap(simplifiedMap, newKey, map.get(key), false);
			}
		}
		return simplifiedMap;
	}

	StringBuilder multipleUrisToStringBuilder(Map<String, List<String>> map, String type) {
		StringBuilder stringBuilder = new StringBuilder();
		for (Entry<String, List<String>> entry : map.entrySet()) {
			if (entry.getValue().size() > 1 || entry.getValue().isEmpty()) {
				if (type.equals("DBpedia")) {
					stringBuilder.append(type + "  " + entry.getKey() + " ");
					for (String uri : entry.getValue()) {
						stringBuilder.append("<a href=\"" + uri + "\">" + uri + "</a> &nbsp; ");
					}
					stringBuilder.append("<br />\n");
				} else if (type.equals("LAU") || type.equals("NUTS")) {
					stringBuilder.append(type + "  " + entry.getKey() + "  ");
					for (String uri : entry.getValue()) {
						stringBuilder.append(uri.substring(uri.lastIndexOf("/")) + " &nbsp; ");
					}
					stringBuilder.append("<br />\n");
				} else {
					System.err
							.println("Warning: Unknown type " + type + " in " + MatcherVersion1.class.getSimpleName());
					stringBuilder.append("<br />\n");
				}
			}
		}
		return stringBuilder;
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

	private void addListItemsToMap(Map<String, List<String>> map, String key, List<String> list,
			boolean checkDuplicates) {
		if (!map.containsKey(key)) {
			map.put(key, new LinkedList<String>());
		}
		if (checkDuplicates) {
			List<String> mapList = map.get(key);
			for (String listItem : list) {
				if (!mapList.contains(listItem)) {
					mapList.add(listItem);
				}
			}
		} else {
			map.get(key).addAll(list);
		}
	}

	/**
	 * Creates {@link #dbpediaLabelToUris}.
	 */
	private void createDbpediaLabelsToUris() throws Exception {
		dbpediaLabelToUris = new HashMap<String, List<String>>();
		int counter = 0;
		for (DbpediaPlaceContainer container : Cache.getDbpedia(true)) {
			boolean added = false;
			List<String> list = new LinkedList<String>();
			list.add(container.uri);
			if (container.labelDe != null) {
				addListItemsToMap(dbpediaLabelToUris, container.labelDe, list, true);
				added = true;
			}
			if (container.labelEn != null && !container.labelDe.equals(container.labelDe)) {
				addListItemsToMap(dbpediaLabelToUris, container.labelEn, list, true);
				added = true;
			}
			if (added) {
				counter++;
			}
		}
		System.out.println("Loaded DBPedia. Labels: " + dbpediaLabelToUris.size() + ", URIs: " + counter);
	}

	/**
	 * Creates {@link #lauNameToUris}.
	 */
	private void createLauNameToUris() throws Exception {
		int counter = 0;
		lauNameToUris = new HashMap<String, List<String>>();
		for (LauContainer container : Cache.getLau(true)) {
			counter++;
			List<String> list = new LinkedList<String>();
			list.add(container.getUri());
			addListItemsToMap(lauNameToUris, container.lauNameLatin, list, true);
		}
		System.out.println("Loaded LAU. Labels: " + lauNameToUris.size() + ", URIs: " + counter);
	}

	/**
	 * Creates {@link #nutsLabelToUris}.
	 */
	private void createNutsLabelsToUris() throws Exception {
		int counter = 0;
		nutsLabelToUris = new HashMap<String, List<String>>();
		for (NutsContainer container : Cache.getNuts(true).values()) {
			counter++;
			List<String> list = new LinkedList<String>();
			list.add(container.getUri());
			for (String prefLabel : container.prefLabel) {
				addListItemsToMap(nutsLabelToUris, prefLabel, list, true);
			}
		}
		System.out.println("Loaded NUTS. Labels: " + nutsLabelToUris.size() + ", URIs: " + counter);
	}
}