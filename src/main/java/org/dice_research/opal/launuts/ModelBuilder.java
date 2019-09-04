package org.dice_research.opal.launuts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public class ModelBuilder {

	private Model model = ModelFactory.createDefaultModel();
	private Map<String, Resource> nuts3map = new HashMap<String, Resource>();

	public ModelBuilder addNuts(Collection<NutsContainer> nutsCollection) {
		for (NutsContainer container : nutsCollection) {
			Resource nuts = getModel().createResource(container.getUri());
			getModel().add(nuts, Vocabularies.PROP_NOTATION, getModel().createLiteral(container.notation));
			getModel().add(nuts, Vocabularies.PROP_PREFLABEL, getModel().createLiteral(container.prefLabel));
			nuts3map.put(container.notation, nuts);
		}

		return this;
	}

	public ModelBuilder addLau(List<LauContainer> lauList) {
		for (LauContainer container : lauList) {
			Resource lau = getModel().createResource(container.getUri());
			if (nuts3map.containsKey(container.nuts3code)) {
				getModel().add(nuts3map.get(container.nuts3code), Vocabularies.PROP_NARROWER, lau);
				getModel().add(lau, Vocabularies.PROP_PREFLABEL, getModel().createLiteral(container.getSimpleName()));
			} else {
				System.err.println("Unknown NUTS3 code: " + container.nuts3code + " for " + container.lauCode);
				continue;
			}

		}
		return this;
	}

	public Model getModel() {
		return model;
	}

	public ModelBuilder writeModel(File file) throws FileNotFoundException {
		FileOutputStream outputStream = new FileOutputStream(file);
		RDFDataMgr.write(outputStream, model, Lang.TURTLE);
		return this;
	}

}