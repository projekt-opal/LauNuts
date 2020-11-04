package org.dice_research.opal.launuts;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.SKOS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Adds triples to RDF model.
 *
 * @author Adrian Wilke
 */
public class ModelBuilder {

	private static final Logger LOGGER = LogManager.getLogger();
	private Model model;
	private Set<LauContainer> skippedLauCodes = new HashSet<>();

	public ModelBuilder() {
		model = ModelFactory.createDefaultModel();
		model.setNsPrefix("nuts", Constants.PREFIX_NUTS_CODE);
	}

	public ModelBuilder addNuts(Model nutsModel) {
		model.add(nutsModel);
		return this;
	}

	public ModelBuilder addLau(Map<String, List<LauContainer>> lau) {
		for (Entry<String, List<LauContainer>> lauEntry : lau.entrySet()) {
			String countryCode = lauEntry.getKey();
			String countryPrefix = Constants.PREFIX_LAU + countryCode + "/";
			boolean countryAdded = false;
			for (LauContainer lauContainer : lauEntry.getValue()) {
				Resource nutsRes = ResourceFactory.createResource(Constants.PREFIX_NUTS_CODE + lauContainer.nutsCode);
				if (model.containsResource(nutsRes)) {

					Resource lauRes = ResourceFactory.createResource(countryPrefix + lauContainer.lauCode);

					model.add(nutsRes, SKOS.narrower, lauRes);
					model.add(lauRes, SKOS.broader, nutsRes);

					model.add(lauRes, SKOS.notation, lauContainer.lauCode);

					model.addLiteral(lauRes, SKOS.prefLabel,
							ResourceFactory.createPlainLiteral(lauContainer.lauNameLatin));
					countryAdded = true;
				} else {
					skippedLauCodes.add(lauContainer);
					LOGGER.warn("Skipping LAU " + lauContainer.lauCode + ", NUTS not found: " + lauContainer.nutsCode);
				}
			}

			if (countryAdded) {
				model.setNsPrefix(countryCode.toLowerCase(), countryPrefix);
			}
		}
		return this;
	}

	public Model getModel() {
		return model;
	}
}