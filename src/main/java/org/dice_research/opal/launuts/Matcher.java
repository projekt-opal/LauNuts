package org.dice_research.opal.launuts;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

/**
 * Matches labels (region names) of different datasets.
 * 
 * @author Adrian Wilke
 */
public class Matcher {

	public static void main(String[] args) throws Exception {
		new Matcher().run();
	}

	// Labels to URIs
	private Map<String, List<String>> dbpedia;
	private Map<String, List<String>> lau;
	private Map<String, List<String>> nuts;

	// Results
	private HashMap<String, String> lauToDbpediaMap = new HashMap<String, String>();
	private HashMap<String, String> nutsToDbpediaMap = new HashMap<String, String>();

	// Files
	File fileMultipleUris = new File(Cfg.getInstance().get(Cfg.OUT_DIRECTORY), "multiple-uris.htm");
	File fileNoMatch = new File(Cfg.getInstance().get(Cfg.OUT_DIRECTORY), "no-match.txt");
	File fileNoMatchLabels = new File(Cfg.getInstance().get(Cfg.OUT_DIRECTORY), "no-match-labels.txt");

	public Map<String, String> getLauToDbpedia() {
		return lauToDbpediaMap;
	}

	public Map<String, String> getNutsToDbpedia() {
		return nutsToDbpediaMap;
	}

	public Matcher run() throws Exception {

		// Get data
		getDbpedia();
		getLau();
		getNuts();

		// TODO Check difficult candidates. Currently, the first candidates of
		// DBpedia is used below 'get(0)'.
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(multipleUrisToStringBuilder(dbpedia, "DBpedia"));
		stringBuilder.append(multipleUrisToStringBuilder(lau, "LAU"));
		stringBuilder.append(multipleUrisToStringBuilder(nuts, "NUTS"));
		FileUtils.write(fileMultipleUris, stringBuilder, StandardCharsets.UTF_8);

		// ---

		staticMatching();

		// ---

		// Exact matches
		System.out.println();
		System.out.println("Exact matches");

		// Labels
		List<String> labelsDbpedia = new LinkedList<String>(dbpedia.keySet());
		List<String> labelsLau = new LinkedList<String>(lau.keySet());
		List<String> labelsNuts = new LinkedList<String>(nuts.keySet());

		String label;
		for (int i = labelsLau.size() - 1; i >= 0; i--) {
			label = labelsLau.get(i);
			if (labelsDbpedia.contains(label)) {
				for (String lauUri : lau.get(label)) {
					lauToDbpediaMap.put(lauUri, dbpedia.get(label).get(0));
				}
				labelsLau.remove(i);
			}
		}
		for (int i = labelsNuts.size() - 1; i >= 0; i--) {
			label = labelsNuts.get(i);
			if (labelsDbpedia.contains(label)) {
				for (String nutsUri : nuts.get(label)) {
					nutsToDbpediaMap.put(nutsUri, dbpedia.get(label).get(0));
				}
				labelsNuts.remove(i);
			}
		}
		System.out.println("LAU: " + lauToDbpediaMap.size());
		System.out.println("NUTS: " + nutsToDbpediaMap.size());
		System.out.println("Remaining LAU labels: " + labelsLau.size());
		System.out.println("Remaining NUTS labels: " + labelsNuts.size());

		// ---

		System.out.println();
		System.out.println("Simplified matches");

		Map<String, List<String>> simplifiedDbpedia = simplify(dbpedia, false);
		Map<String, List<String>> simplifiedLau = simplify(lau, false);
		Map<String, List<String>> simplifiedNuts = simplify(nuts, false);

		labelsDbpedia = new LinkedList<String>(simplifiedDbpedia.keySet());
		labelsLau = new LinkedList<String>(simplifiedLau.keySet());
		labelsNuts = new LinkedList<String>(simplifiedNuts.keySet());

		for (int i = labelsLau.size() - 1; i >= 0; i--) {
			label = labelsLau.get(i);
			if (labelsDbpedia.contains(label)) {
				for (String lauUri : simplifiedLau.get(label)) {
					lauToDbpediaMap.put(lauUri, simplifiedDbpedia.get(label).get(0));
				}
				labelsLau.remove(i);
			}
		}
		for (int i = labelsNuts.size() - 1; i >= 0; i--) {
			label = labelsNuts.get(i);
			if (labelsDbpedia.contains(label)) {
				for (String nutsUri : simplifiedNuts.get(label)) {
					nutsToDbpediaMap.put(nutsUri, simplifiedDbpedia.get(label).get(0));
				}
				labelsNuts.remove(i);
			}
		}
		System.out.println("LAU: " + lauToDbpediaMap.size());
		System.out.println("NUTS: " + nutsToDbpediaMap.size());
		System.out.println("Remaining LAU labels: " + labelsLau.size());
		System.out.println("Remaining NUTS labels: " + labelsNuts.size());

		// --

		// TODO Check candidates without matches
		stringBuilder = new StringBuilder();
		stringBuilder.append(mapToStringBuilder(simplifiedDbpedia, "DBpedia"));
		stringBuilder.append(mapToStringBuilder(simplifiedLau, "LAU"));
		stringBuilder.append(mapToStringBuilder(simplifiedNuts, "NUTS"));
		FileUtils.write(fileNoMatch, stringBuilder, StandardCharsets.UTF_8);

		stringBuilder = new StringBuilder();
		stringBuilder.append(collectionToStringBuilder(labelsLau, "LAU"));
		stringBuilder.append(collectionToStringBuilder(labelsNuts, "NUTS"));
		FileUtils.write(fileNoMatchLabels, stringBuilder, StandardCharsets.UTF_8);

		return this;
	}

	/**
	 * Inserts static NUTS-1/DBpedia URIs to {@link #nutsToDbpediaMap}.
	 * 
	 * Removes NUTS entries from {@link #nuts}.
	 */
	private void staticMatching() {
		System.out.println();
		System.out.println("Static matches");

		List<String> nutsLabelsToRemove = new LinkedList<String>();
		for (Entry<String, String> n2d : new Mapping().getNutsToDbPediaFederalStates().entrySet()) {

			// Insert static URIs
			nutsToDbpediaMap.put(n2d.getKey(), n2d.getValue());

			// Remember label to remove
			for (Entry<String, List<String>> nutsEntry : nuts.entrySet()) {
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
		int nutsSize = nuts.size();
		for (String nutsLabel : nutsLabelsToRemove) {
			nuts.remove(nutsLabel);
		}
		if (nutsLabelsToRemove.size() != 16) {
			System.err.println("Warning: NUTS-1 static URIs not complete. " + Matcher.class.getSimpleName());
		}
		if (nutsSize != nuts.size() + 16) {
			System.err.println("Warning: NUTS-1 labels not complete. " + Matcher.class.getSimpleName());
		}

	}

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
					System.err.println("Warning: Unknown type " + type + " in " + Matcher.class.getSimpleName());
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

	private void getDbpedia() throws Exception {
		dbpedia = new HashMap<String, List<String>>();
		int counter = 0;
		for (DbpediaPlaceContainer container : Cache.getDbpedia(true)) {
			boolean added = false;
			List<String> list = new LinkedList<String>();
			list.add(container.uri);
			if (container.labelDe != null) {
				addListItemsToMap(dbpedia, container.labelDe, list, true);
				added = true;
			}
			if (container.labelEn != null && !container.labelDe.equals(container.labelDe)) {
				addListItemsToMap(dbpedia, container.labelEn, list, true);
				added = true;
			}
			if (added) {
				counter++;
			}
		}
		System.out.println("Loaded DBPedia. Labels: " + dbpedia.size() + ", URIs: " + counter);
	}

	private void getLau() throws Exception {
		int counter = 0;
		lau = new HashMap<String, List<String>>();
		for (LauContainer container : Cache.getLau(true)) {
			counter++;
			List<String> list = new LinkedList<String>();
			list.add(container.getUri());
			addListItemsToMap(lau, container.lauNameLatin, list, true);
		}
		System.out.println("Loaded LAU. Labels: " + lau.size() + ", URIs: " + counter);
	}

	private void getNuts() throws Exception {
		int counter = 0;
		nuts = new HashMap<String, List<String>>();
		for (NutsContainer container : Cache.getNuts(true).values()) {
			counter++;
			List<String> list = new LinkedList<String>();
			list.add(container.getUri());
			for (String prefLabel : container.prefLabel) {
				addListItemsToMap(nuts, prefLabel, list, true);
			}
		}
		System.out.println("Loaded NUTS. Labels: " + nuts.size() + ", URIs: " + counter);
	}
}