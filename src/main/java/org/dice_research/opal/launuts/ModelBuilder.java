package org.dice_research.opal.launuts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.dice_research.opal.launuts.dbpedia.DbpediaPlaceContainer;
import org.dice_research.opal.launuts.lau.LauContainer;
import org.dice_research.opal.launuts.nuts.NutsContainer;

import io.github.galbiston.geosparql_jena.implementation.datatype.WKTDatatype;
import io.github.galbiston.geosparql_jena.implementation.vocabulary.Geo;

public class ModelBuilder {

	private Model model = ModelFactory.createDefaultModel();
	private Map<String, Resource> nuts3map = new HashMap<String, Resource>();

	private final static boolean ADD_TYPE_CONCEPT = false;
	private final static boolean ADD_NARROWER = false;

	ModelBuilder() {
		model.setNsPrefix("dct", Vocabularies.NS_DCTERMS);
		model.setNsPrefix("launuts", Vocabularies.NS_LAUNUTS);
		model.setNsPrefix("lau", Vocabularies.NS_LAU);
		model.setNsPrefix("nuts", Vocabularies.NS_EU_NUTS);
		model.setNsPrefix("skos", Vocabularies.NS_SKOS);
		model.setNsPrefix("xsd", Vocabularies.NS_XSD);
		model.setNsPrefix("ogc", Vocabularies.NS_OGC);
		model.setNsPrefix("geo", Vocabularies.NS_GEO);
		model.setNsPrefix("dbr", Vocabularies.NS_DBR);
		model.setNsPrefix("dbo", Vocabularies.NS_DBO);

		// Additional prefixes to reduce model size
		model.setNsPrefix("laude", Vocabularies.NS_LAU_DE);
		model.setNsPrefix("nutscode", Vocabularies.NS_EU_NUTS_CODE);
	}

	public ModelBuilder addNuts(Collection<NutsContainer> nutsCollection) {
		for (NutsContainer container : nutsCollection) {

			Resource nuts = getModel().createResource(container.getUri());

			if (ADD_TYPE_CONCEPT) {
				getModel().add(nuts, Vocabularies.PROP_TYPE, Vocabularies.RES_CONCEPT);
			}
			getModel().add(nuts, Vocabularies.PROP_TYPE, Vocabularies.RES_NUTS);
			if (container.nutsLevel == null) {
				System.err.println("Warning: no level for container " + container.getUri());
			} else {
				switch (container.nutsLevel) {
				case 0:
					getModel().add(nuts, Vocabularies.PROP_TYPE, Vocabularies.RES_NUTS_0);
					break;
				case 1:
					getModel().add(nuts, Vocabularies.PROP_TYPE, Vocabularies.RES_NUTS_1);
					break;
				case 2:
					getModel().add(nuts, Vocabularies.PROP_TYPE, Vocabularies.RES_NUTS_2);
					break;
				case 3:
					getModel().add(nuts, Vocabularies.PROP_TYPE, Vocabularies.RES_NUTS_3);
					break;
				default:
					break;
				}
			}

			if (container.parent != null) {
				// Not available for root
				getModel().add(nuts, Vocabularies.PROP_BROADER, model.getResource(container.parent.getUri()));
				if (ADD_NARROWER) {
					getModel().add(model.getResource(container.parent.getUri()), Vocabularies.PROP_NARROWER, nuts);
				}
			}

			getModel().add(nuts, Vocabularies.PROP_NOTATION, getModel().createLiteral(container.notation));

			if (container.prefLabel.size() == 1) {
				// Add only label
				String prefLabel = container.prefLabel.iterator().next();
				String simpleName = NutsContainer.toSimpleName(prefLabel);
				getModel().add(nuts, Vocabularies.PROP_PREFLABEL, getModel().createLiteral(simpleName));
				if (!simpleName.equals(prefLabel)) {
					getModel().add(nuts, Vocabularies.PROP_ALTLABEL, getModel().createLiteral(prefLabel));
				}
			} else {
				// Get shortest label
				String shortestLabel = NutsContainer.toSimpleName(container.prefLabel.iterator().next());
				for (String prefLabel : container.prefLabel) {
					prefLabel = NutsContainer.toSimpleName(prefLabel);
					if (prefLabel.length() < shortestLabel.length()) {
						shortestLabel = prefLabel;
					}
				}
				// Use short form of label as preferred label
				getModel().add(nuts, Vocabularies.PROP_PREFLABEL, getModel().createLiteral(shortestLabel));
				// Add other variants
				for (String prefLabel : container.prefLabel) {
					if (!shortestLabel.equals(prefLabel)) {
						getModel().add(nuts, Vocabularies.PROP_ALTLABEL, getModel().createLiteral(prefLabel));
					}
				}
			}

			nuts3map.put(container.notation, nuts);
		}
		return this;
	}

	public ModelBuilder addLau(List<LauContainer> lauList) {
		for (LauContainer container : lauList) {
			Resource lau = getModel().createResource(container.getUri());
			if (nuts3map.containsKey(container.nuts3code)) {

				if (ADD_TYPE_CONCEPT) {
					getModel().add(lau, Vocabularies.PROP_TYPE, Vocabularies.RES_CONCEPT);
				}
				getModel().add(lau, Vocabularies.PROP_TYPE, Vocabularies.RES_LAU);

				getModel().add(lau, Vocabularies.PROP_BROADER, nuts3map.get(container.nuts3code));
				if (ADD_NARROWER) {
					getModel().add(nuts3map.get(container.nuts3code), Vocabularies.PROP_NARROWER, lau);
				}

				getModel().add(lau, Vocabularies.PROP_NOTATION, getModel().createLiteral(container.lauCode));

				getModel().add(lau, Vocabularies.PROP_PREFLABEL, getModel().createLiteral(container.getSimpleName()));
				if (!container.getSimpleName().equals(container.lauNameLatin)) {
					getModel().add(lau, Vocabularies.PROP_ALTLABEL, getModel().createLiteral(container.lauNameLatin));
				}
			} else {
				System.err.println("Unknown NUTS3 code: " + container.nuts3code + " for " + container.lauCode);
				continue;
			}
		}
		return this;
	}

	public ModelBuilder addGeoData(Map<String, DbpediaPlaceContainer> dbpediaIndex, Map<String, String> nutsToDbpedia,
			Map<String, String> lauToDbpedia) {

		for (Entry<String, String> nuts2dbp : nutsToDbpedia.entrySet()) {
			Resource res = ResourceFactory.createResource(nuts2dbp.getKey());
			if (getModel().containsResource(res) && dbpediaIndex.containsKey(nuts2dbp.getValue())) {
				Resource dbpediaRes = getDbpediaResource(dbpediaIndex.get(nuts2dbp.getValue()));
				getModel().add(res, Vocabularies.PROP_RELATEDMATCH, dbpediaRes);
			}
		}

		for (Entry<String, String> lau2dbp : lauToDbpedia.entrySet()) {
			Resource res = ResourceFactory.createResource(lau2dbp.getKey());
			if (getModel().containsResource(res) && dbpediaIndex.containsKey(lau2dbp.getValue())) {
				Resource dbpediaRes = getDbpediaResource(dbpediaIndex.get(lau2dbp.getValue()));
				getModel().add(res, Vocabularies.PROP_RELATEDMATCH, dbpediaRes);
			}
		}

		return this;
	}

	Resource getDbpediaResource(DbpediaPlaceContainer dbpediaPlaceContainer) {
		Resource res = ResourceFactory.createResource(dbpediaPlaceContainer.uri);
		if (getModel().containsResource(res)) {
			return res;
		} else {
			getModel().add(res, Vocabularies.PROP_TYPE, Vocabularies.RES_PLACE);

			Literal wkt = ResourceFactory.createTypedLiteral(
					"POINT(" + dbpediaPlaceContainer.lat + " " + dbpediaPlaceContainer.lon + ")", WKTDatatype.INSTANCE);
			getModel().addLiteral(res, Geo.HAS_GEOMETRY_PROP, wkt);
			getModel().addLiteral(res, Vocabularies.PROP_LAT, dbpediaPlaceContainer.lat);
			getModel().addLiteral(res, Vocabularies.PROP_LONG, dbpediaPlaceContainer.lon);
			return res;
		}
	}

	public Model getModel() {
		return model;
	}

	public String getTurtleComment() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("# LAU and NUTS data for Germany\n");
		stringBuilder.append("# \n");
		stringBuilder.append("# Local Administrative Units (LAU)\n");
		stringBuilder.append("# Nomenclature of Territorial Units for Statistics (NUTS)\n");
		stringBuilder.append("# https://ec.europa.eu/eurostat/web/nuts/overview\n");
		stringBuilder.append("# \n");
		stringBuilder.append("# Data:\n");
		stringBuilder.append("# https://hobbitdata.informatik.uni-leipzig.de/OPAL/\n");
		stringBuilder.append("# \n");
		stringBuilder.append("# Generator software: \n");
		stringBuilder.append("# Data Science Group (DICE) at Paderborn University\n");
		stringBuilder.append("# Open Data Portal Germany (OPAL), Adrian Wilke\n");
		stringBuilder.append("# https://github.com/projekt-opal/LauNuts\n");
		stringBuilder.append("# \n");

		return stringBuilder.toString();
	}

	public ModelBuilder writeModel(File outputDirectory) throws IOException {
		File file = new File(outputDirectory, "launuts.ttl");
		file.getParentFile().mkdirs();
		FileOutputStream outputStream = new FileOutputStream(file);
		outputStream.write(getTurtleComment().getBytes());
		RDFDataMgr.write(outputStream, model, Lang.TURTLE);
		return this;
	}

}