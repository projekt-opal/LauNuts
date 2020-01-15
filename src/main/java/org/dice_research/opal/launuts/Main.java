package org.dice_research.opal.launuts;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.dice_research.opal.launuts.dbpedia.DbpediaPlaceContainer;
import org.dice_research.opal.launuts.dbpedia.DbpediaRemote;
import org.dice_research.opal.launuts.lau.LauContainer;
import org.dice_research.opal.launuts.matcher.MatcherVersion2;
import org.dice_research.opal.launuts.matcher.StaticMappings;
import org.dice_research.opal.launuts.nuts.NutsContainer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Main entry point.
 * 
 * @author Adrian Wilke
 */
public class Main {

	public static void main(String[] args) throws Exception {
		new Main().run();
	}

	private void run() throws Exception {

		// Extract NUTS RDF
		Map<String, NutsContainer> nutsIndex = Cache.getNuts(true);

		// NUTS-1 prefLabel
		enhancePrefLabel(nutsIndex);
		removeUnusedNuts(nutsIndex);

		// Parse LAU CSV
		List<LauContainer> lauList = Cache.getLau(true);

		// Parse DBpedia places
		Map<String, DbpediaPlaceContainer> dbpediaIndex = DbpediaRemote.createPlacesIndex(Cache.getDbpedia(true));

		// Match datasets
		// Writes files for analysis
		MatcherVersion2 matcher = new MatcherVersion2().run();
		
		/*
		 * Polygons for NUTs and Laus of Germany has been extracted and stored in 
		 * json files. Read the array and pass it to addGeoData so that polygon
		 * cooridanates can be added in turtle file which is being created with
		 * ModelBuilder.
		 */
		JSONParser parser = new JSONParser();
		Reader nuts_reader,laus_reader;
		
		nuts_reader = new FileReader("NUTS_Polygons.json");
		JSONArray array_polygons_nuts_json = (JSONArray) parser.parse(nuts_reader);
		
		laus_reader = new FileReader("LAUs_Polygons.json");
		JSONArray array_polygons_laus_json = (JSONArray) parser.parse(laus_reader);

		// Create new model
		ModelBuilder modelBuilder = new ModelBuilder()

				.addNuts(nutsIndex.values(),array_polygons_nuts_json)

				.addLau(lauList,array_polygons_laus_json)

				.addGeoData(dbpediaIndex, matcher.getNutsToDbpedia(), matcher.getLauToDbpedia())

				.writeModel(new File(Cfg.getInstance().get(Cfg.OUT_DIRECTORY)));

		// Statistics
		// Writes statistics about data types
		String statistics = new Statistics(modelBuilder.getModel()).compute().getString();
		System.out.println();
		System.out.println(statistics);
		FileUtils.write(new File(Cfg.getInstance().get(Cfg.OUT_DIRECTORY), "statistics.txt"), statistics,
				StandardCharsets.UTF_8);

		// Analysis
		// Writes files for analysis of DBpedia mappings
		Analysis.writeLauMappings(matcher.getLauToDbpedia());
		Analysis.writeNutsMappings(matcher.getNutsToDbpedia());
		Analysis.writeMultipleUsage(matcher.getNutsToDbpedia(), matcher.getLauToDbpedia());
	}

	private void enhancePrefLabel(Map<String, NutsContainer> nutsIndex) {
		for (Entry<String, String> nutsUri2prefLabel : new StaticMappings().getNutsCodeToPrefLabel().entrySet()) {
			NutsContainer nutsContainer = nutsIndex.get(nutsUri2prefLabel.getKey());
			nutsContainer.prefLabel = new HashSet<String>();
			nutsContainer.prefLabel.add(nutsUri2prefLabel.getValue());
		}
	}

	void removeUnusedNuts(Map<String, NutsContainer> nutsIndex) {
		for (String nutsCode : new StaticMappings().getUnusedNutsCodes()) {
			nutsIndex.remove(nutsCode);
		}
	}
}