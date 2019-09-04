package org.dice_research.opal.launuts;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;

/**
 * Extracts data from NUTS RDF.
 * 
 * Creates map of NUTS3 data, which can be received via {@link #getNuts3Map()}.
 * 
 * @author Adrian Wilke
 */
public class NutsRdfExtractor {

	private Model model;

	/**
	 * Loads NUTS RDF file to model.
	 */
	public NutsRdfExtractor(String nutsRdfFile) {
		loadModel(nutsRdfFile);
	}

	/**
	 * Extracts RDF data into container objects.
	 */
	public List<NutsContainer> extract() throws Exception {
		List<NutsContainer> list = new LinkedList<NutsContainer>();

		List<Resource> nuts1 = getNarrower(Vocabularies.RES_DE);
		for (Resource res1 : nuts1) {
			list.add(createContainer(res1));

			List<Resource> nuts2 = getNarrower(res1);
			for (Resource res2 : nuts2) {
				list.add(createContainer(res2));

				List<Resource> nuts3 = getNarrower(res2);
				for (Resource res3 : nuts3) {
					list.add(createContainer(res3));
				}

			}
		}

		return list;
	}

	/**
	 * Resource to container.
	 */
	NutsContainer createContainer(Resource resource) throws Exception {
		NutsContainer container = new NutsContainer();

		String uri = resource.getURI().toString();
		if (uri.startsWith(Vocabularies.NS_NUTS)) {
			container.key = uri.substring(Vocabularies.NS_NUTS.length());
		} else {
			throw new Exception("Unexpected URI: " + uri);
		}

		NodeIterator nodeIterator = model.listObjectsOfProperty(resource, Vocabularies.PROP_NOTATION);
		if (nodeIterator.hasNext()) {
			container.notation = nodeIterator.next().asLiteral().toString();
		}
		if (nodeIterator.hasNext()) {
			System.err.println("Multiple notations for " + uri);
		}

		nodeIterator = model.listObjectsOfProperty(resource, Vocabularies.PROP_PREFLABEL);
		if (nodeIterator.hasNext()) {
			container.prefLabel = nodeIterator.next().asLiteral().toString();
		}
		if (nodeIterator.hasNext()) {
			System.err.println("Multiple prefLabels for " + uri);
		}

		return container;
	}

	/**
	 * Looks for resources with property narrower.
	 */
	public List<Resource> getNarrower(Resource resource) {
		List<Resource> list = new LinkedList<Resource>();
		NodeIterator nodeIterator = model.listObjectsOfProperty(resource, Vocabularies.PROP_NARROWER);
		while (nodeIterator.hasNext()) {
			Resource object = nodeIterator.next().asResource();
			NodeIterator prefLabelIterator = model.listObjectsOfProperty(object, Vocabularies.PROP_PREFLABEL);
			if (prefLabelIterator.hasNext()) {
				list.add(object);
			}
		}
		return list;
	}

	/**
	 * Loads NUTS RDF file to model.
	 */
	private void loadModel(String nutsRdfFile) {
		this.model = ModelFactory.createDefaultModel();
		RDFDataMgr.read(this.model, new File(nutsRdfFile).toURI().toString());
	}
}