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

	public static final Property PROP_TYPE = org.apache.jena.vocabulary.RDF.type;

	public static final String NS_XSD = "http://www.w3.org/2001/XMLSchema#";

	public static final String NS_DCTERMS = "http://purl.org/dc/terms/";
	public static final Property PROP_REPLACES = ResourceFactory.createProperty(NS_DCTERMS, "replaces");
	public static final Property PROP_ISREPLACEDBY = ResourceFactory.createProperty(NS_DCTERMS, "isReplacedBy");

	public static final String NS_SKOS = org.apache.jena.vocabulary.SKOS.uri;
	public static final Resource RES_CONCEPT = org.apache.jena.vocabulary.SKOS.Concept;
	public static final Property PROP_ALTLABEL = org.apache.jena.vocabulary.SKOS.altLabel;
	public static final Property PROP_BROADER = org.apache.jena.vocabulary.SKOS.broader;
	public static final Property PROP_RELATEDMATCH = org.apache.jena.vocabulary.SKOS.relatedMatch;
	public static final Property PROP_NARROWER = org.apache.jena.vocabulary.SKOS.narrower;
	public static final Property PROP_PREFLABEL = org.apache.jena.vocabulary.SKOS.prefLabel;
	public static final Property PROP_NOTATION = org.apache.jena.vocabulary.SKOS.notation;

	public static final String NS_GEO = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static final Property PROP_LAT = ResourceFactory.createProperty(NS_GEO, "lat");
	public static final Property PROP_LONG = ResourceFactory.createProperty(NS_GEO, "long");

	public static final String NS_OGC = "http://www.opengis.net/ont/geosparql#";

	public static final String NS_EU_NUTS = "http://data.europa.eu/nuts/";
	public static final String NS_EU_NUTS_CODE = NS_EU_NUTS + "code/";
	public static final Property PROP_EU_MERGEDFROM = ResourceFactory.createProperty(NS_EU_NUTS, "mergedFrom");
	public static final Property PROP_EU_MERGEDINTO = ResourceFactory.createProperty(NS_EU_NUTS, "mergedInto");
	public static final Resource RES_EU_DE = ResourceFactory.createResource(NS_EU_NUTS_CODE + "DE");

	public static final String NS_DBR = "http://dbpedia.org/resource/";
	public static final String NS_DBO = "http://dbpedia.org/ontology/";
	public static final Resource RES_PLACE = ResourceFactory.createResource(NS_DBO + "Place");

	public static final String NS_LAUNUTS = "http://projekt-opal.de/launuts/";
	public static final String NS_NUTS = NS_LAUNUTS + "nuts/";
	public static final String NS_LAU = NS_LAUNUTS + "lau/";
	public static final String NS_LAU_DE = NS_LAU + "DE/";
	public static final Resource RES_NUTS = ResourceFactory.createResource(NS_LAUNUTS + "NUTS");
	public static final Resource RES_LAU = ResourceFactory.createResource(NS_LAUNUTS + "LAU");
	public static final Resource RES_NUTS_0 = ResourceFactory.createResource(NS_LAUNUTS + "NUTS-0");
	public static final Resource RES_NUTS_1 = ResourceFactory.createResource(NS_LAUNUTS + "NUTS-1");
	public static final Resource RES_NUTS_2 = ResourceFactory.createResource(NS_LAUNUTS + "NUTS-2");
	public static final Resource RES_NUTS_3 = ResourceFactory.createResource(NS_LAUNUTS + "NUTS-3");
}