package org.dice_research.opal.launuts;

import java.io.File;

/**
 * Main entry point.
 * 
 * Required arguments:
 * 
 * LAU CSV file, EU-28-LAU-2019-NUTS-2016-DE.csv, exported from xslx.
 * 
 * NUTS RDF file, nuts.rdf
 * 
 * Output TURTLE file
 * 
 * @author Adrian Wilke
 */
public class Main {

	private static final boolean PRINT_LAU = false;
	private static final boolean PRINT_NUTS = false;

	public static void main(String[] args) throws Exception {
		new Main().configure(args).run();
	}

	public File lauCsvFile;
	public File nutsRdfFile;
	public File outDirectory;

	private LauCsvParser lauCsvParser;
	private NutsRdfExtractor nutsRdfExtractor;

	private Main configure(String[] args) {
		if (args.length < 3) {
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

		outDirectory = new File(args[2]);
		if (!outDirectory.exists()) {
			outDirectory.mkdirs();
		}

		return this;
	}

	private void run() throws Exception {

		// Extract NUTS RDF
		nutsRdfExtractor = new NutsRdfExtractor(nutsRdfFile.getPath()).extractNuts();

		// Parse LAU CSV
		lauCsvParser = new LauCsvParser().parse(lauCsvFile.getPath());

		// Create new model
		ModelBuilder modelBuilder = new ModelBuilder()

				.addNuts(nutsRdfExtractor.getNutsIndex().values())

				.addLau(lauCsvParser.getLauList())

				.writeModel(outDirectory);

		// Print results
		printNuts();
		printLau();
		System.out.println();
		System.out.println("Size (statements) of generated model: " + modelBuilder.getModel().size());
		System.out.println("Output directory: " + outDirectory);
	}

	private void printLau() {
		if (PRINT_LAU) {
			for (LauContainer lau : lauCsvParser.getLauList()) {
				System.out.println(lau);
			}
			System.out.println(lauCsvParser.getLauList().size());
		}
	}

	private void printNuts() {
		if (PRINT_NUTS) {
			for (NutsContainer nuts : nutsRdfExtractor.getNutsIndex().values()) {
				System.out.println(nuts);
			}
			System.out.println(nutsRdfExtractor.getNutsIndex().size());
		}
	}
}