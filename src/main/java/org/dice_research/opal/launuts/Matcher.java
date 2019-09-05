package org.dice_research.opal.launuts;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

	public HashMap<String, String> getLauToDbpedia() {
		return lauToDbpediaMap;
	}

	public HashMap<String, String> getNutsToDbpedia() {
		return nutsToDbpediaMap;
	}

	public void run() throws Exception {

		// Get data
		getDbpedia();
		getLau();
		getNuts();

		// TODO Check difficult candidates. Currently, below the first candidate
		// 'get(0)' is used.
		if (Boolean.FALSE) {
			printMultipleUris(dbpedia, "DBpedia");
			printMultipleUris(lau, "LAU");
			printMultipleUris(nuts, "NUTS");
		}

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
		Map<String, List<String>> simplifiedLau = simplify(lau, true);
		Map<String, List<String>> simplifiedNuts = simplify(nuts, true);

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
		if (Boolean.FALSE) {
			printMap(simplifiedDbpedia, "DBpedia");
			printMap(simplifiedLau, "LAU");
			printMap(simplifiedNuts, "NUTS");
		}
		if (Boolean.FALSE) {
			printCollection(labelsLau, "LAU");
			printCollection(labelsNuts, "NUTS");
		}
	}

	private Map<String, List<String>> simplify(Map<String, List<String>> map, boolean addOnlySimplified) {
		Map<String, List<String>> simplifiedMap = new HashMap<String, List<String>>();
		for (String key : map.keySet()) {
			String newKey = key;
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

	void printMultipleUris(Map<String, List<String>> map, String heading) {
		for (Entry<String, List<String>> entry : map.entrySet()) {
			if (entry.getValue().size() > 1 || entry.getValue().isEmpty()) {
				System.out.println(heading + "  " + entry.getKey() + "  " + entry.getValue());
			}
		}
	}

	void printMap(Map<String, List<String>> map, String heading) {
		for (Entry<String, List<String>> entry : map.entrySet()) {
			System.out.println(heading + "  " + entry.getKey() + "  " + entry.getValue());
		}
	}

	void printCollection(Collection<String> collection, String heading) {
		for (String string : collection) {
			System.out.println(heading + "  " + string);
		}
	}

	private void addListItemsToMap(Map<String, List<String>> map, String key, List<String> list) {
		if (!map.containsKey(key)) {
			map.put(key, new LinkedList<String>());
		}
		map.get(key).addAll(list);
	}

	private void getDbpedia() throws Exception {
		dbpedia = new HashMap<String, List<String>>();
		int counter = 0;
		for (DbpediaPlaceContainer container : Cache.getDbpedia(true)) {
			boolean added = false;
			List<String> list = new LinkedList<String>();
			list.add(container.uri);
			if (container.labelDe != null) {
				addListItemsToMap(dbpedia, container.labelDe, list);
				added = true;
			}
			if (container.labelEn != null && !container.labelDe.equals(container.labelDe)) {
				addListItemsToMap(dbpedia, container.labelEn, list);
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
			addListItemsToMap(lau, container.lauNameLatin, list);
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
				addListItemsToMap(nuts, prefLabel, list);
			}
		}
		System.out.println("Loaded NUTS. Labels: " + nuts.size() + ", URIs: " + counter);
	}
}