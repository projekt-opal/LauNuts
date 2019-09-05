package org.dice_research.opal.launuts;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

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

		// Parse LAU CSV
		List<LauContainer> lauList = Cache.getLau(true);

		// Parse DBpedia places
		Map<String, DbpediaPlaceContainer> dbpediaIndex = DbpediaRemote.createPlacesIndex(Cache.getDbpedia(true));

		// Match datasets
		Matcher matcher = new Matcher().run();

		// Create new model
		ModelBuilder modelBuilder = new ModelBuilder()

				.addNuts(nutsIndex.values())

				.addLau(lauList)

				.addGeoData(dbpediaIndex, matcher.getNutsToDbpedia(), matcher.getLauToDbpedia())

				.writeModel(new File(Cfg.getInstance().get(Cfg.OUT_DIRECTORY)));

		// Statistics
		String statistics = new Statistics(modelBuilder.getModel()).compute().getString();
		System.out.println(statistics);
		FileUtils.write(new File(Cfg.getInstance().get(Cfg.OUT_DIRECTORY), "statistics.txt"), statistics,
				StandardCharsets.UTF_8);
	}

}