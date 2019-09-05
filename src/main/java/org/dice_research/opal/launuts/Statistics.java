package org.dice_research.opal.launuts;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;

import io.github.galbiston.geosparql_jena.implementation.vocabulary.Geo;

public class Statistics {

	private Model model;
	private int counterGeo = 0;
	private int counterNoGeo = 0;
	private int counterNodes = 0;
	private Map<Integer, Integer> counterLevel = new HashMap<Integer, Integer>();

	public Statistics(Model model) {
		this.model = model;
	}

	public Statistics compute() {
		compute(model.getResource(Vocabularies.RES_DE.getURI()), 0);

		if (counterLevel.containsKey(5)) {
			System.err.println("Warning: Statistics counted level 5");
		}

		return this;
	}

	private void compute(Resource resource, int level) {
		count(resource, level);
		ResIterator resIterator = model.listSubjectsWithProperty(org.apache.jena.vocabulary.SKOS.broader, resource);

		level++;
		while (resIterator.hasNext()) {
			compute(resIterator.next(), level);
		}
	}

	private void count(Resource resource, int level) {
		counterNodes++;

		if (resource.hasProperty(Geo.HAS_GEOMETRY_PROP)) {
			counterGeo++;
		} else {
			counterNoGeo++;
		}

		if (!counterLevel.containsKey(level)) {
			counterLevel.put(level, 0);
		}
		counterLevel.put(level, counterLevel.get(level) + 1);
	}

	public String getString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Geo-Level:  " + counterLevel.get(0));
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("NUTS-1:     " + counterLevel.get(1));
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("NUTS-2:     " + counterLevel.get(2));
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("NUTS-3:     " + counterLevel.get(3));
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("LAU:        " + counterLevel.get(4));
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("Total:      " + counterNodes);
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("GeoData:    " + counterGeo);
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("No GeoData: " + counterNoGeo);
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("Triples:    " + model.size());
		return stringBuilder.toString();
	}
}