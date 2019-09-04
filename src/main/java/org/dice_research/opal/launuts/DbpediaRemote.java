package org.dice_research.opal.launuts;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

	public static final String labelsFilename = "dbpediaLabels.txt";

	private RDFConnection rdfConnection;
	private List<DbpediaPlaceContainer> dbpediaPlaces = new LinkedList<DbpediaPlaceContainer>();

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("Please provide arguments.");
			System.exit(1);
		}

		File labelsFile = new File(args[0]);
		labelsFile.getParentFile().mkdirs();

		new DbpediaRemote().connect().getPlacesInGermany().exportLabels(labelsFile);
		System.out.println("Labels file: " + labelsFile.getPath());
	}

	private DbpediaRemote getPlacesInGermany() throws IOException {
		ResultSet resultSet = execSelect("dbpedia-place-germany");
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			DbpediaPlaceContainer container = new DbpediaPlaceContainer();
			container.place = querySolution.getResource("place").getURI().toString();
			if (querySolution.getLiteral("labelde") != null)
				container.labelDe = querySolution.getLiteral("labelde").getLexicalForm();
			if (querySolution.getLiteral("labelen") != null)
				container.labelEn = querySolution.getLiteral("labelen").getLexicalForm();
			if (querySolution.getLiteral("lat") != null)
				container.lat = querySolution.getLiteral("lat").getFloat();
			if (querySolution.getLiteral("long") != null)
				container.lon = querySolution.getLiteral("long").getFloat();
			dbpediaPlaces.add(container);
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
		rdfConnection = RDFConnectionRemote.create().destination("http://dbpedia.org/sparql").build();
		return this;
	}

	public List<DbpediaPlaceContainer> getDbpediaPlaces() {
		return dbpediaPlaces;
	}

	private void exportLabels(File labelsFile) throws IOException {
		Set<String> labels = new HashSet<String>();
		for (DbpediaPlaceContainer container : dbpediaPlaces) {
			if (container.labelDe != null) {
				labels.add(container.labelDe);
			}
			if (container.labelEn != null) {
				labels.add(container.labelEn);
			}
		}
		FileUtils.writeLines(labelsFile, labels);
	}
}