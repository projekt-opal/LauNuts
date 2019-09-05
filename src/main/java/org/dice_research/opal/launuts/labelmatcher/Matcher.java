//package org.dice_research.opal.launuts.labelmatcher;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//import java.util.SortedSet;
//import java.util.TreeSet;
//
///**
// * Compares labels of datasets.
// * 
// * @author Adrian Wilke
// */
//public class Matcher {
//
//	public static void main(String[] args) throws IOException {
//
//
//	}
//
//	// Label to URI
//	Map<String, String> dbpedia;
//	Map<String, String> lau;
//	Map<String, String> nuts;
//
//	// Label (in lower case) to URI
//	Map<String, String> dbpediaLowercase;
//	Map<String, String> lauLowercase;
//	Map<String, String> nutsLowercase;
//
//	// Label in lower case
//	Set<String> exactMatchesLau = new HashSet<String>();
//	Set<String> exactMatchesNuts = new HashSet<String>();
//
//
//	private Matcher createLowercaseMaps() {
//		dbpediaLowercase = new HashMap<String, String>();
//		for (Entry<String, String> entry : dbpedia.entrySet()) {
//			String key = entry.getKey().toLowerCase();
//			if (dbpediaLowercase.containsKey(key)) {
//				if (!entry.getValue().equals(dbpediaLowercase.get(key))) {
//					System.err.println("Warning: Duplicate DBpedia key: " + key + "  " + entry.getValue() + "  "
//							+ dbpediaLowercase.get(key));
//				}
//			}
//			dbpediaLowercase.put(key, entry.getValue());
//		}
//		System.out.println("DBpedia lowercase: " + dbpediaLowercase.size() + ", uppercase: " + dbpedia.size());
//
//		lauLowercase = new HashMap<String, String>();
//		for (Entry<String, String> entry : lau.entrySet()) {
//			String key = entry.getKey().toLowerCase();
//			if (lauLowercase.containsKey(key)) {
//				if (!entry.getValue().equals(lauLowercase.get(key))) {
//					System.err.println("Warning: Duplicate LAU key: " + key + "  " + entry.getValue() + "  "
//							+ lauLowercase.get(key));
//				}
//			}
//			lauLowercase.put(key, entry.getValue());
//		}
//		System.out.println("LAU lowercase: " + lauLowercase.size() + ", uppercase: " + lau.size());
//
//		nutsLowercase = new HashMap<String, String>();
//		for (Entry<String, String> entry : nuts.entrySet()) {
//			String key = entry.getKey().toLowerCase();
//			if (nutsLowercase.containsKey(key)) {
//				if (!entry.getValue().equals(nutsLowercase.get(key))) {
//					System.err.println("Warning: Duplicate NUTS key: " + key + "  " + entry.getValue() + "  "
//							+ nutsLowercase.get(key));
//				}
//			}
//			nutsLowercase.put(key, entry.getValue());
//		}
//		System.out.println("NUTS lowercase: " + nutsLowercase.size() + ", uppercase: " + nuts.size());
//
//		return this;
//	}
//
//	private void match() {
//
//		// Labels, which can be removed after match
//
//		SortedSet<String> lauLabelPool = new TreeSet<String>(lauLowercase.keySet());
//		SortedSet<String> nutsLabelPool = new TreeSet<String>(nutsLowercase.keySet());
//
//		// Exact matches
//
//		for (String label : lauLabelPool) {
//			if (dbpediaLowercase.containsKey(label)) {
//				exactMatchesLau.add(label);
//			}
//		}
//		for (String label : exactMatchesLau) {
//			lauLabelPool.remove(label);
//		}
//		System.out.println("Exact matches LAU: " + exactMatchesLau.size() + ", remaining: " + lauLabelPool.size());
//
//		for (String label : nutsLabelPool) {
//			if (dbpediaLowercase.containsKey(label)) {
//				exactMatchesNuts.add(label);
//			}
//		}
//		for (String label : exactMatchesNuts) {
//			nutsLabelPool.remove(label);
//		}
//		System.out.println("Exact matches NUTS: " + exactMatchesNuts.size() + ", remaining: " + nutsLabelPool.size());
//
//		print(lauLabelPool, "LAU");
//		print(simplify(lauLabelPool), "LAU");
//		print(nutsLabelPool, "NUTS");
//		print(simplify(nutsLabelPool), "NUTS");
//	}
//
//	private List<String> simplify(Collection<String> collection) {
//		List<String> list = new ArrayList<String>(collection);
//		for (int i = 0; i < list.size(); i++) {
//			if (list.get(i).contains(",")) {
//				list.set(i, list.get(i).substring(0, list.get(i).indexOf(",")).trim());
//			}
//			if (list.get(i).contains("(")) {
//				list.set(i, list.get(i).substring(0, list.get(i).indexOf("(")).trim());
//			}
//		}
//		return list;
//	}
//
////
////	// Add shorted labels
////	Map<String, String> labels2 = new HashMap<String, String>();
////	for (Entry<String, String> entry : labels.entrySet()) {
////		if (entry.getKey().contains("(")) {
////			labels2.put(entry.getKey().substring(0, entry.getKey().indexOf("(")).trim(), entry.getValue());
////		}
////		if (entry.getKey().contains(",")) {
////			labels2.put(entry.getKey().substring(0, entry.getKey().indexOf(",")).trim(), entry.getValue());
////		}
////	}
////	labels.putAll(labels2);
////
////	// Export
////}
//
//	private void print(Collection<String> collection, String optionalHeader) {
//		StringBuilder stringBuilder = new StringBuilder();
//		stringBuilder.append(System.lineSeparator());
//		stringBuilder.append(System.lineSeparator());
//		if (optionalHeader != null && !optionalHeader.isEmpty()) {
//			stringBuilder.append(optionalHeader);
//			stringBuilder.append(System.lineSeparator());
//		}
//		for (String string : collection) {
//			stringBuilder.append(string);
//			stringBuilder.append(System.lineSeparator());
//		}
//		stringBuilder.append(collection.size());
//		stringBuilder.append(System.lineSeparator());
//		stringBuilder.append(System.lineSeparator());
//		stringBuilder.append(System.lineSeparator());
//		System.out.println(stringBuilder);
//	}
//}
