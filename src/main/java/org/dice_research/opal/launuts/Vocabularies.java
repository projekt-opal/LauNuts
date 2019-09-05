package org.dice_research.opal.launuts;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Used RDF vocabularies.
 * 
 * Also see org.apache.jena.vocabulary.*
 * 
 * @author Adrian Wilke
 */
public abstract class Vocabularies {

	public static final String NS_DCTERMS = "http://purl.org/dc/terms/";
	public static final Property PROP_REPLACES = ResourceFactory.createProperty(NS_DCTERMS, "replaces");
	public static final Property PROP_ISREPLACEDBY = ResourceFactory.createProperty(NS_DCTERMS, "isReplacedBy");

	public static final String NS_SKOS = "http://www.w3.org/2004/02/skos/core#";
	public static final Property PROP_NARROWER = ResourceFactory.createProperty(NS_SKOS, "narrower");
	public static final Property PROP_PREFLABEL = ResourceFactory.createProperty(NS_SKOS, "prefLabel");
	public static final Property PROP_NOTATION = ResourceFactory.createProperty(NS_SKOS, "notation");

	public static final String NS_NUTS = "http://data.europa.eu/nuts/";
	public static final String NS_NUTS_CODE = NS_NUTS + "code/";
	public static final Property PROP_MERGEDFROM = ResourceFactory.createProperty(NS_NUTS, "mergedFrom");
	public static final Property PROP_MERGEDINTO = ResourceFactory.createProperty(NS_NUTS, "mergedInto");
	public static final Resource RES_DE = ResourceFactory.createResource(NS_NUTS_CODE + "DE");

	public static final String NS_LAU = "http://projekt-opal.de/lau/";
	public static final String NS_LAU_DE = "http://projekt-opal.de/lau/DE/";

	public static final String NS_GEO = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static final Property PROP_LAT = ResourceFactory.createProperty(NS_GEO, "lat");
	public static final Property PROP_LONG = ResourceFactory.createProperty(NS_GEO, "long");

	public static final String NS_XSD = "http://www.w3.org/2001/XMLSchema#";
	
	public static final String NS_OGC = "http://www.opengis.net/ont/geosparql#";
	
}