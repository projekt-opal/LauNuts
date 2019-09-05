package org.dice_research.opal.launuts;

import java.io.File;
import java.util.List;
import java.util.Map;

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

		// Create new model
		ModelBuilder modelBuilder = new ModelBuilder()

				.addNuts(nutsIndex.values())

				.addLau(lauList)

				.writeModel(new File(Cfg.getInstance().get(Cfg.OUT_DIRECTORY)));

		// Print results
		System.out.println("Size (statements) of generated model: " + modelBuilder.getModel().size());
	}

}