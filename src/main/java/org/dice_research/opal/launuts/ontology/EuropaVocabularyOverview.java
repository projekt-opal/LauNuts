package org.dice_research.opal.launuts.ontology;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;

/**
 * Analyses the NUTS RDF file from europa.eu.
 * 
 * To run this, specify the path of the RDF file in system/environment property
 * {@value #KEY_NUTS_RDF_FILE}.
 * 
 * @see https://data.europa.eu/euodp/repository/ec/estat/nuts/nuts.rdf
 * @see https://data.europa.eu/euodp/en/data/dataset/ESTAT-NUTS-classification/resource/fbcdf6e2-f2f8-45d3-81c2-3bc6ab1ba6fb
 *
 * @author Adrian Wilke
 */
public class EuropaVocabularyOverview {

	public static final String KEY_NUTS_RDF_FILE = "NUTSRDFFILE";

	private File file;
	private Map<String, Map<String, Set<String>>> index = new TreeMap<>();

	public static void main(String[] args) {
		new EuropaVocabularyOverview().execute();
	}

	public void execute() {
		Model model = loadModel();
		System.out.println("Model size: " + model.size());

		printTypes(model);

		if (Boolean.TRUE) {
			printPredicates(model);
		}

		collect(model);
		printIndex();
	}

	private Model loadModel() {
		String filename = System.getenv(KEY_NUTS_RDF_FILE);
		if (filename == null) {
			throw new RuntimeException("Could not find property " + KEY_NUTS_RDF_FILE);
		}

		file = new File(filename);
		if (!file.canRead()) {
			throw new RuntimeException("Could not read file " + file.getAbsolutePath());
		}

		return RDFDataMgr.loadModel(file.toURI().toString());
	}

	private void printIndex() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("Types index");
		stringBuilder.append(System.lineSeparator());
		for (Entry<String, Map<String, Set<String>>> subjectEntry : index.entrySet()) {
			for (Entry<String, Set<String>> predicateEntry : subjectEntry.getValue().entrySet()) {
				for (String objectType : predicateEntry.getValue()) {
					stringBuilder.append(subjectEntry.getKey());
					stringBuilder.append(" ");
					stringBuilder.append(predicateEntry.getKey());
					stringBuilder.append(" ");
					stringBuilder.append(objectType);
					stringBuilder.append(System.lineSeparator());
				}
			}
		}
		System.out.println(stringBuilder.toString());
	}

	private void collect(Model model) {

		StmtIterator stmtIterator = model.listStatements();
		while (stmtIterator.hasNext()) {
			Statement statement = stmtIterator.next();

			List<String> subjectTypes = getTypes(model, statement.getSubject());

			String predicate = statement.getPredicate().getURI();

			List<String> objectTypes;
			if (statement.getObject().isResource()) {
				objectTypes = getTypes(model, statement.getObject().asResource());
			} else if (statement.getObject().isLiteral()) {
				objectTypes = new LinkedList<>();
				objectTypes.add("LITERAL");
			} else {
				objectTypes = new LinkedList<>();
				System.err.println("Unexpected object type");
			}

			for (String subjectType : subjectTypes) {
				for (String objectType : objectTypes) {
					addToIndex(subjectType, predicate, objectType);
				}
			}
		}
	}

	private void addToIndex(String subjectType, String predicate, String objectType) {
		if (!index.containsKey(subjectType)) {
			index.put(subjectType, new TreeMap<>());
		}

		Map<String, Set<String>> subjectMap = index.get(subjectType);
		if (!subjectMap.containsKey(predicate)) {
			subjectMap.put(predicate, new TreeSet<>());
		}

		subjectMap.get(predicate).add(objectType);
	}

	private List<String> getTypes(Model model, Resource resource) {
		List<String> types = new LinkedList<>();
		NodeIterator nodeIterator = model.listObjectsOfProperty(resource, RDF.type);
		while (nodeIterator.hasNext()) {
			types.add(nodeIterator.next().asResource().getURI());
		}
		return types;
	}

	private void printTypes(Model model) {
		System.out.println();
		System.out.println("Types");
		NodeIterator typeIterator = model.listObjectsOfProperty(RDF.type);
		while (typeIterator.hasNext()) {
			System.out.println(typeIterator.next());
		}

		System.out.println();
		System.out.println("ConceptSchemes");
		ResIterator conceptSchemeIterator = model.listSubjectsWithProperty(null, SKOS.ConceptScheme);
		while (conceptSchemeIterator.hasNext()) {
			System.out.println(conceptSchemeIterator.next());
		}
	}

	private void printPredicates(Model model) {

		System.out.println();
		System.out.println("Predicates of concepts ?c ?p ?x");
		Set<String> predicatesOfConcepts = new TreeSet<>();
		ResIterator conceptIterator = model.listSubjectsWithProperty(null, SKOS.Concept);
		while (conceptIterator.hasNext()) {
			Resource resource = conceptIterator.next();
			StmtIterator stmtIterator = resource.listProperties();
			while (stmtIterator.hasNext()) {
				predicatesOfConcepts.add(stmtIterator.next().getPredicate().toString());
			}
		}
		for (String predicate : predicatesOfConcepts) {
			System.out.println(predicate);
		}

		System.out.println();
		System.out.println("Predicates pointing to concepts ?x ?p ?c");
		Set<String> predicatesOfConcepts2 = new TreeSet<>();
		conceptIterator = model.listSubjectsWithProperty(null, SKOS.Concept);
		while (conceptIterator.hasNext()) {
			Resource resource = conceptIterator.next();
			StmtIterator stmtIterator = model.listStatements(null, null, resource);
			while (stmtIterator.hasNext()) {
				predicatesOfConcepts2.add(stmtIterator.next().getPredicate().toString());
			}
		}
		for (String predicate : predicatesOfConcepts2) {
			System.out.println(predicate);
		}

		System.out.println();
		System.out.println("Predicates of concept schemes ?cs ?p ?x");
		Set<String> predicatesOfConceptSchemes = new TreeSet<>();
		ResIterator conceptSchemeIterator = model.listSubjectsWithProperty(null, SKOS.ConceptScheme);
		while (conceptSchemeIterator.hasNext()) {
			Resource resource = conceptSchemeIterator.next();
			StmtIterator stmtIterator = resource.listProperties();
			while (stmtIterator.hasNext()) {
				predicatesOfConceptSchemes.add(stmtIterator.next().getPredicate().toString());
			}
		}
		for (String predicate : predicatesOfConceptSchemes) {
			System.out.println(predicate);
		}

		System.out.println();
		System.out.println("Predicates pointing to concept schemes ?x ?p ?cs");
		Set<String> predicatesOfConceptSchemes2 = new TreeSet<>();
		conceptSchemeIterator = model.listSubjectsWithProperty(null, SKOS.ConceptScheme);
		while (conceptSchemeIterator.hasNext()) {
			Resource resource = conceptSchemeIterator.next();
			StmtIterator stmtIterator = model.listStatements(null, null, resource);
			while (stmtIterator.hasNext()) {
				predicatesOfConceptSchemes2.add(stmtIterator.next().getPredicate().toString());
			}
		}
		for (String predicate : predicatesOfConceptSchemes2) {
			System.out.println(predicate);
		}
	}
}