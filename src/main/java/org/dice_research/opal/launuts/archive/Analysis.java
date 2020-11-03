package org.dice_research.opal.launuts.archive;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.dice_research.opal.launuts.archive.lau.LauContainer;
import org.dice_research.opal.launuts.archive.lau.LauCsvParser;
import org.dice_research.opal.launuts.archive.nuts.NutsContainer;

public class Analysis {

	private static File fileMappingsNuts = new File(Cfg.getInstance().get(Cfg.OUT_DIRECTORY), "mappings-nuts.html");
	private static File fileMappingsLau = new File(Cfg.getInstance().get(Cfg.OUT_DIRECTORY), "mappings-lau.html");
	private static File fileMultipleDbpedia = new File(Cfg.getInstance().get(Cfg.OUT_DIRECTORY),
			"multiple-dbpedia-usages.txt");

	public static void writeMultipleUsage(Map<String, String> nutsToDbpediaUris, Map<String, String> lauToDbpediaUris)
			throws IOException {
		Map<String, List<String>> dbpedia = new HashMap<String, List<String>>();
		List<String> list;

		for (Entry<String, String> entry : nutsToDbpediaUris.entrySet()) {
			if (dbpedia.containsKey(entry.getValue())) {
				list = dbpedia.get(entry.getValue());
			} else {
				list = new LinkedList<String>();
			}
			list.add(entry.getKey());
			dbpedia.put(entry.getValue(), list);
		}

		for (Entry<String, String> entry : lauToDbpediaUris.entrySet()) {
			if (dbpedia.containsKey(entry.getValue())) {
				list = dbpedia.get(entry.getValue());
			} else {
				list = new LinkedList<String>();
			}
			list.add(entry.getKey());
			dbpedia.put(entry.getValue(), list);
		}

		StringBuilder stringBuilder = new StringBuilder();
		for (Entry<String, List<String>> entry : dbpedia.entrySet()) {
			if (entry.getValue().size() > 1) {
				stringBuilder.append(entry.getKey());
				stringBuilder.append(System.lineSeparator());
				for (String string : entry.getValue()) {
					stringBuilder.append(string.substring(string.lastIndexOf("/") + 1));
					stringBuilder.append("  ");
				}
				stringBuilder.append(System.lineSeparator());
				stringBuilder.append(System.lineSeparator());
			}
		}
		FileUtils.write(fileMultipleDbpedia, stringBuilder, StandardCharsets.UTF_8);
	}

	public static void writeNutsMappings(Map<String, String> nutsToDbpediaUris) throws Exception {
		Map<String, NutsContainer> nutsIndex = Cache.getNuts(true);

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(
				"<!DOCTYPE html><head><title>NUTS</title><style>tr:nth-child(odd){background-color:#eee}</style></head><body><table>");
		stringBuilder.append("<tr>");
		stringBuilder.append("<th>NUTS</th>");
		stringBuilder.append("<th>DBpedia</th>");
		stringBuilder.append("</tr>");
		TreeSet<String> nutsSet = new TreeSet<String>(nutsToDbpediaUris.keySet());
		for (String nutsUri : nutsSet) {
			String dbpediaUri = nutsToDbpediaUris.get(nutsUri);
			NutsContainer nutsContainer = nutsIndex.get(nutsUri.substring(nutsUri.lastIndexOf("/") + 1));
			stringBuilder.append("<tr>");
			stringBuilder.append("<td>");
			stringBuilder.append(nutsContainer.notation);
			stringBuilder.append(" ");
			stringBuilder.append(nutsContainer.prefLabel.iterator().next());
			stringBuilder.append("</td>");
			stringBuilder.append("<td>");
			stringBuilder.append("<a href=\"");
			stringBuilder.append(dbpediaUri);
			stringBuilder.append("\">");
			stringBuilder.append(dbpediaUri.substring(dbpediaUri.lastIndexOf("/") + 1));
			stringBuilder.append("</a>");
			stringBuilder.append("</td>");
			stringBuilder.append("</tr>");
			stringBuilder.append(System.lineSeparator());
		}
		stringBuilder.append("</table></body></html>");
		FileUtils.write(fileMappingsNuts, stringBuilder, StandardCharsets.UTF_8);
	}

	public static void writeLauMappings(Map<String, String> lauToDbpediaUris) throws Exception {
		Map<String, LauContainer> lauIndex = LauCsvParser.createLauCodeToContainer(Cache.getLau(true));

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(
				"<!DOCTYPE html><head><title>LAU</title><style>tr:nth-child(odd){background-color:#eee}</style></head><body><table>");
		stringBuilder.append("<tr>");
		stringBuilder.append("<th>LAU</th>");
		stringBuilder.append("<th>DBpedia</th>");
		stringBuilder.append("</tr>");
		TreeSet<String> lauSet = new TreeSet<String>(lauToDbpediaUris.keySet());
		for (String lauUri : lauSet) {
			String dbpediaUri = lauToDbpediaUris.get(lauUri);
			LauContainer lauContainer = lauIndex.get(lauUri.substring(lauUri.lastIndexOf("/") + 1));
			stringBuilder.append("<tr>");
			stringBuilder.append("<td>");
			stringBuilder.append(lauContainer.lauCode);
			stringBuilder.append(" ");
			stringBuilder.append(lauContainer.lauNameLatin);
			stringBuilder.append("</td>");
			stringBuilder.append("<td>");
			stringBuilder.append("<a href=\"");
			stringBuilder.append(dbpediaUri);
			stringBuilder.append("\">");
			stringBuilder.append(dbpediaUri.substring(dbpediaUri.lastIndexOf("/") + 1));
			stringBuilder.append("</a>");
			stringBuilder.append("</td>");
			stringBuilder.append("</tr>");
			stringBuilder.append(System.lineSeparator());
		}
		stringBuilder.append("</table></body></html>");
		FileUtils.write(fileMappingsLau, stringBuilder, StandardCharsets.UTF_8);
	}

}