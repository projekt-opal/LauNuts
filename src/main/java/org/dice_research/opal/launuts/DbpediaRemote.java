package org.dice_research.opal.launuts;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;

/**
 * Gets data from DBpedia SPARQL endpoint.
 * 
 * @author Adrian Wilke
 */
public class DbpediaRemote {

	private RDFConnection rdfConnection;
	private List<DbpediaPlaceContainer> places = new LinkedList<DbpediaPlaceContainer>();

	private DbpediaRemote queryPlacesInGermany() throws IOException {
		// TODO return max 10,000.
		// TODO lat lon non optional
		connect();
		ResultSet resultSet = execSelect("dbpedia-place-germany");
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			DbpediaPlaceContainer container = new DbpediaPlaceContainer();
			container.uri = querySolution.getResource("place").getURI().toString();
			if (querySolution.getLiteral("labelde") != null)
				container.labelDe = querySolution.getLiteral("labelde").getLexicalForm();
			if (querySolution.getLiteral("labelen") != null)
				container.labelEn = querySolution.getLiteral("labelen").getLexicalForm();
			if (querySolution.getLiteral("lat") != null)
				container.lat = querySolution.getLiteral("lat").getFloat();
			if (querySolution.getLiteral("long") != null)
				container.lon = querySolution.getLiteral("long").getFloat();
			places.add(container);
		}
		return this;
	}

	private ResultSet execSelect(String queryId) throws IOException {
		QueryExecution queryExecution = rdfConnection.query(getSparqlQuery(queryId));
		return queryExecution.execSelect();
	}

	private static String getSparqlQuery(String queryId) throws IOException {
		File file = new File("src/main/resources/org/dice_research/opal/launuts/sparql/" + queryId + ".txt");
		return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
	}

	private DbpediaRemote connect() {
		if (rdfConnection == null) {
			rdfConnection = RDFConnectionRemote.create().destination("http://dbpedia.org/sparql").build();
		}
		return this;
	}

	public List<DbpediaPlaceContainer> getPlaces() throws IOException {
		if (places.isEmpty()) {
			queryPlacesInGermany();
		}
		return places;
	}

	// TODO eventually remove
	public Map<String, String> getLabelUriMap() throws IOException {
		if (places.isEmpty()) {
			queryPlacesInGermany();
		}

		Map<String, String> labels = new HashMap<String, String>();

		// German labels have higher priority and replace englisch labels
		for (DbpediaPlaceContainer container : places) {
			if (container.labelEn != null) {
				labels.put(container.labelEn, container.uri);
			}
			if (container.labelDe != null) {
				labels.put(container.labelDe, container.uri);
			}
		}

		return labels;
	}

	// TODO eventually remove
	public Map<String, List<String>> getUrisToLabels() throws Exception {
		if (places.isEmpty()) {
			queryPlacesInGermany();
		}

		Map<String, List<String>> urisToLabels = new HashMap<String, List<String>>(places.size());
		for (DbpediaPlaceContainer container : places) {
			List<String> list = new LinkedList<String>();
			if (container.labelDe != null) {
				list.add(container.labelDe);
			}
			if (container.labelEn != null) {
				if (!list.contains(container.labelEn)) {
					list.add(container.labelEn);
				}
			}
			urisToLabels.put(container.uri, list);
		}
		return urisToLabels;
	}
}