package org.dice_research.opal.launuts;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;

/**
 * Extracts data from NUTS RDF.
 * 
 * @author Adrian Wilke
 */
public class NutsRdf {

	public static final String NS_SKOS = "http://www.w3.org/2004/02/skos/core#";
	public static final Property PROP_NARROWER = ResourceFactory.createProperty(NS_SKOS, "narrower");
	public static final Property PROP_PREFLABEL = ResourceFactory.createProperty(NS_SKOS, "prefLabel");

	public static final String NS_NUTS = "http://data.europa.eu/nuts/";
	public static final Resource RES_DE = ResourceFactory.createResource(NS_NUTS + "code/DE");

	private Model model;

	/**
	 * Loads NUTS RDF file to model.
	 */
	public void loadModel(String nutsRdfFile) {
		this.model = ModelFactory.createDefaultModel();
		RDFDataMgr.read(this.model, new File(nutsRdfFile).toURI().toString());
	}

	/**
	 * Looks for resources with property narrower. Adds their URIs and labels to
	 * map.
	 */
	Map<Resource, String> getNarrower(Resource resource) {
		Map<Resource, String> map = new HashMap<Resource, String>();
		NodeIterator nodeIterator = model.listObjectsOfProperty(resource, PROP_NARROWER);
		while (nodeIterator.hasNext()) {
			Resource object = nodeIterator.next().asResource();
			NodeIterator prefLabelIterator = model.listObjectsOfProperty(object, PROP_PREFLABEL);
			if (prefLabelIterator.hasNext()) {
				map.put(object, prefLabelIterator.next().asLiteral().toString());
			}
		}
		return map;
	}
}