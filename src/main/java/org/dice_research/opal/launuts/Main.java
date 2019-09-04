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

	public static void main(String[] args) throws Exception {
		new Main().configure(args).run();
	}

	public File lauCsvFile;
	public File nutsRdfFile;
	public File outTurtleFile;

	public void run() throws Exception {
		ModelBuilder modelBuilder = new ModelBuilder()

				.addNuts(new NutsRdfExtractor(nutsRdfFile.getPath()).extract())

				.addLau(new LauCsvParser().parse(lauCsvFile.getPath()))

				.writeModel(outTurtleFile);

		System.out.println("Size of generated model: " + modelBuilder.getModel().size());
		System.out.println("File: " + outTurtleFile);
	}

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

		outTurtleFile = new File(args[2]);

		return this;
	}
}