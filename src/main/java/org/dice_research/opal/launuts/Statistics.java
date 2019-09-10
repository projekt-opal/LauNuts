package org.dice_research.opal.launuts;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;

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
		compute(model.getResource(Vocabularies.RES_EU_DE.getURI()), 0);

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

		if (resource.hasProperty(Vocabularies.PROP_RELATEDMATCH)) {
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
		stringBuilder.append("Countries: " + counterLevel.get(0) + " (Germany)");
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("NUTS-1:    " + counterLevel.get(1) + " (Federal states)");
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("NUTS-2:    " + counterLevel.get(2) + " (Government regions)");
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("NUTS-3:    " + counterLevel.get(3) + " (Districts)");
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("LAU:       " + counterLevel.get(4) + " (Municipalities)");
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("Total:     " + counterNodes);
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("DBpedia GeoData:    " + counterGeo);
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("No DBpedia GeoData: " + counterNoGeo);
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("Triples: " + model.size());
		return stringBuilder.toString();
	}
}