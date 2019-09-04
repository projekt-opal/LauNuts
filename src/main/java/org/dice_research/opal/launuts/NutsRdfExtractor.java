package org.dice_research.opal.launuts;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;

/**
 * Extracts data from NUTS RDF.
 * 
 * @author Adrian Wilke
 */
public class NutsRdfExtractor {

	private Model model;

	/**
	 * Loads NUTS RDF file to model.
	 */
	public NutsRdfExtractor(String nutsRdfFile) {
		loadModel(nutsRdfFile);
	}

	/**
	 * Looks for resources with property narrower. Adds their URIs and labels to
	 * map.
	 */
	public Map<Resource, String> getNarrower(Resource resource) {
		Map<Resource, String> map = new HashMap<Resource, String>();
		NodeIterator nodeIterator = model.listObjectsOfProperty(resource, Vocabularies.PROP_NARROWER);
		while (nodeIterator.hasNext()) {
			Resource object = nodeIterator.next().asResource();
			NodeIterator prefLabelIterator = model.listObjectsOfProperty(object, Vocabularies.PROP_PREFLABEL);
			if (prefLabelIterator.hasNext()) {
				map.put(object, prefLabelIterator.next().asLiteral().toString());
			}
		}
		return map;
	}

	/**
	 * Loads NUTS RDF file to model.
	 */
	private void loadModel(String nutsRdfFile) {
		this.model = ModelFactory.createDefaultModel();
		RDFDataMgr.read(this.model, new File(nutsRdfFile).toURI().toString());
	}
}