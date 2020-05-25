package org.dice_research.opal.launuts.dbpedia;

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
	private static final int maxResults = 10000;

	private DbpediaRemote queryPlacesInGermany() throws IOException {
		int numberOfResults = maxResults;
		int offset = 0;
		while (numberOfResults == maxResults) {
			numberOfResults = subQueryPlacesInGermany(offset);
			offset += maxResults;
		}
		return this;
	}

	private int subQueryPlacesInGermany(int offset) throws IOException {
		connect();
		String query = getSparqlQuery("dbpedia-place-germany");
		query = query.replace("OFFSET 0", "OFFSET " + offset);
		ResultSet resultSet = execSelect(query);
		int counter = 0;
		while (resultSet.hasNext()) {
			counter++;
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
			if (querySolution.getLiteral("nuts") != null)
				container.nuts = querySolution.getLiteral("nuts").getLexicalForm();
			places.add(container);
		}
		return counter;
	}

	private ResultSet execSelect(String query) throws IOException {
		QueryExecution queryExecution = rdfConnection.query(query);
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

	public static Map<String, DbpediaPlaceContainer> createPlacesIndex(List<DbpediaPlaceContainer> placesList) {
		Map<String, DbpediaPlaceContainer> map = new HashMap<String, DbpediaPlaceContainer>();
		for (DbpediaPlaceContainer container : placesList) {
			map.put(container.uri, container);
		}
		return map;
	}

}