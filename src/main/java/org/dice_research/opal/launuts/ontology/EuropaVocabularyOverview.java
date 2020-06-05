package org.dice_research.opal.launuts.ontology;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
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

	public static void main(String[] args) {
		new EuropaVocabularyOverview().execute();
	}

	public void execute() {
		String filename = System.getenv(KEY_NUTS_RDF_FILE);
		if (filename == null) {
			System.err.println("Could not find property " + KEY_NUTS_RDF_FILE);
			return;
		}

		file = new File(filename);
		if (!file.canRead()) {
			System.err.println("Could not read file " + file.getAbsolutePath());
			return;
		}

		Model model = RDFDataMgr.loadModel(file.toURI().toString());

		System.out.println("Model size: " + model.size());

		System.out.println();
		System.out.println("Types");
		NodeIterator nodeIterator = model.listObjectsOfProperty(RDF.type);
		while (nodeIterator.hasNext()) {
			System.out.println(nodeIterator.next());
		}

		System.out.println();
		System.out.println("ConceptSchemes");
		ResIterator resIterator = model.listSubjectsWithProperty(null, SKOS.ConceptScheme);
		while (resIterator.hasNext()) {
			System.out.println(resIterator.next());
		}

		System.out.println();
		System.out.println("Predicates");
		Set<String> predicates = new TreeSet<>();
		resIterator = model.listSubjectsWithProperty(null, SKOS.Concept);
		while (resIterator.hasNext()) {
			Resource resource = resIterator.next();
			StmtIterator stmtIterator = resource.listProperties();
			while (stmtIterator.hasNext()) {
				predicates.add(stmtIterator.next().getPredicate().toString());
			}
		}
		for (String predicate : predicates) {
			System.out.println(predicate);
		}

	}

}