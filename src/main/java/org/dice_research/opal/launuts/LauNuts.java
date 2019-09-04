package org.dice_research.opal.launuts;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.rdf.model.Resource;

/**
 * Main entry point.
 * 
 * Required arguments:
 * 
 * LAU CSV file, EU-28-LAU-2019-NUTS-2016-DE.csv, exported from xslx.
 * 
 * NUTS RDF file, nuts.rdf
 * 
 * @author Adrian Wilke
 */
public class LauNuts {

	public static void main(String[] args) throws IOException {
		new LauNuts().configure(args).run();
	}

	private File lauCsvFile;
	private File nutsRdfFile;

	private LauNuts configure(String[] args) {
		if (args.length < 2) {
			System.err.println("Please provide input files.");
			System.exit(1);
		}

		lauCsvFile = new File(args[0]);
		if (!lauCsvFile.canRead()) {
			System.err.println("Can not read LAU CSV file. " + lauCsvFile);
			System.exit(1);
		}

		nutsRdfFile = new File(args[1]);
		if (!nutsRdfFile.canRead()) {
			System.err.println("Can not read NUTS RDF file. " + nutsRdfFile);
			System.exit(1);
		}

		return this;
	}

	public void run() throws IOException {
		parseLau();
		extractNuts();
	}

	private void parseLau() throws IOException {
		LauCsvParser csvParser = new LauCsvParser();
		List<LauContainer> lauList = csvParser.parse(lauCsvFile.getPath());
		for (LauContainer lauContainer : lauList) {
			System.out.println(lauContainer);
		}
		System.out.println(lauList.size());
	}

	private void extractNuts() {
		NutsRdfExtractor nutsRdf = new NutsRdfExtractor(nutsRdfFile.getPath());

		int counter = 0;
		Map<Resource, String> nuts1 = nutsRdf.getNarrower(Vocabularies.RES_DE);
		for (Entry<Resource, String> entry1 : nuts1.entrySet()) {
			System.out.println(entry1.getKey() + " " + entry1.getValue());
			counter++;

			Map<Resource, String> nuts2 = nutsRdf.getNarrower(entry1.getKey());
			for (Entry<Resource, String> entry2 : nuts2.entrySet()) {
				System.out.println(entry2.getKey() + " " + entry2.getValue());
				counter++;

				Map<Resource, String> nuts3 = nutsRdf.getNarrower(entry2.getKey());
				for (Entry<Resource, String> entry3 : nuts3.entrySet()) {
					System.out.println(entry3.getKey() + " " + entry3.getValue());
					counter++;
				}

			}
		}
		System.out.println(counter);
	}
}