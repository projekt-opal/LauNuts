package org.dice_research.opal.launuts;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Used RDF vocabularies.
 * 
 * @author Adrian Wilke
 */
public abstract class Vocabularies {

	public static final String NS_SKOS = "http://www.w3.org/2004/02/skos/core#";
	public static final Property PROP_NARROWER = ResourceFactory.createProperty(NS_SKOS, "narrower");
	public static final Property PROP_PREFLABEL = ResourceFactory.createProperty(NS_SKOS, "prefLabel");
	public static final Property PROP_NOTATION = ResourceFactory.createProperty(NS_SKOS, "notation");

	public static final String NS_NUTS = "http://data.europa.eu/nuts/";
	public static final Resource RES_DE = ResourceFactory.createResource(NS_NUTS + "code/DE");

	public static final String NS_LAU = "http://projekt-opal.de/lau/";
}