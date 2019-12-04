package org.dice_research.opal.launuts.nuts;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.dice_research.opal.launuts.Vocabularies;

/**
 * Extracts data from NUTS RDF.
 * 
 * @author Adrian Wilke
 */
public class NutsRdfExtractor {

	private static final boolean PRINT_REPLACEMENT_INFO = true;

	private Model model;
	private Map<String, NutsContainer> nutsIndex = new HashMap<String, NutsContainer>();
	private Map<String, String> replacedBy = new HashMap<String, String>();
	private Map<String, String> mergedInto = new HashMap<String, String>();

	/**
	 * Loads NUTS RDF file to model.
	 */
	public NutsRdfExtractor(String nutsRdfFile) {
		loadModel(nutsRdfFile);
	}

	/**
	 * Extracts RDF data into container objects.
	 */
	public NutsRdfExtractor extractNuts() throws Exception {
		NutsContainer container0 = createContainer(replaceDeprecated(Vocabularies.RES_EU_DE), 0);
		nutsIndex.put(container0.notation, container0);

		List<Resource> nuts1 = getNarrower(Vocabularies.RES_EU_DE);
		for (Resource res1 : nuts1) {
			NutsContainer container1 = createContainer(replaceDeprecated(res1), 1);
			container1.parent = container0;
			nutsIndex.put(container1.notation, container1);

			List<Resource> nuts2 = getNarrower(res1);
			for (Resource res2 : nuts2) {
				NutsContainer container2 = createContainer(replaceDeprecated(res2), 2);
				container2.parent = container1;
				nutsIndex.put(container2.notation, container2);

				List<Resource> nuts3 = getNarrower(res2);
				for (Resource res3 : nuts3) {
					NutsContainer container3 = createContainer(replaceDeprecated(res3), 3);
					container3.parent = container2;
					nutsIndex.put(container3.notation, container3);
				}
			}
		}
		return this;
	}

	/**
	 * If NUTS is replaced by another or merged into another, the resource is also
	 * replaced.
	 */
	private Resource replaceDeprecated(Resource resource) {
		NodeIterator nodeIterator = model.listObjectsOfProperty(resource, Vocabularies.PROP_EU_MERGEDINTO);
		if (nodeIterator.hasNext()) {
			Resource newResource = nodeIterator.next().asResource();
			if (PRINT_REPLACEMENT_INFO) {
				System.out.println("Replacing " + NutsContainer.uriToNutsCode(resource.getURI()) + " with "
						+ NutsContainer.uriToNutsCode(newResource.getURI()));
			}
			return newResource;
		}

		nodeIterator = model.listObjectsOfProperty(resource, Vocabularies.PROP_ISREPLACEDBY);
		if (nodeIterator.hasNext()) {
			Resource newResource = nodeIterator.next().asResource();
			if (PRINT_REPLACEMENT_INFO) {
				System.out.println("Replacing " + NutsContainer.uriToNutsCode(resource.getURI()) + " with "
						+ NutsContainer.uriToNutsCode(newResource.getURI()));
			}
			return newResource;
		}

		return resource;

	}

	/**
	 * Resource to container.
	 */
	NutsContainer createContainer(Resource resource, Integer nutsLevel) throws Exception {
		NutsContainer container = new NutsContainer();
		String uri = resource.getURI().toString();

		NodeIterator nodeIterator = model.listObjectsOfProperty(resource, Vocabularies.PROP_NOTATION);
		if (nodeIterator.hasNext()) {
			container.notation = nodeIterator.next().asLiteral().toString();
		}
		if (nodeIterator.hasNext()) {
			System.err.println("Multiple notations for " + uri);
		}

		nodeIterator = model.listObjectsOfProperty(resource, Vocabularies.PROP_PREFLABEL);
		while (nodeIterator.hasNext()) {
			container.prefLabel.add(nodeIterator.next().asLiteral().toString().trim());
		}

		if (nutsLevel == null) {
			throw new RuntimeException("No NUTS level: " + uri);
		} else {
			container.nutsLevel = nutsLevel;
		}

		// Replaced NUTS

		nodeIterator = model.listObjectsOfProperty(resource, Vocabularies.PROP_REPLACES);
		while (nodeIterator.hasNext()) {
			container.replaces.add(NutsContainer.uriToNutsCode(nodeIterator.next().toString()));
		}

		ResIterator resIterator = model.listSubjectsWithProperty(Vocabularies.PROP_REPLACES, resource);
		if (resIterator.hasNext()) {
			String replacebByCode = NutsContainer.uriToNutsCode(resIterator.next().asResource().toString());
			container.replacedBy = replacebByCode;
			replacedBy.put(container.notation, replacebByCode);
		}
		if (resIterator.hasNext()) {
			System.err.println("Multiple replacements for " + uri);
		}

		// Merged NUTS

		nodeIterator = model.listObjectsOfProperty(resource, Vocabularies.PROP_EU_MERGEDFROM);
		while (nodeIterator.hasNext()) {
			container.mergedFrom.add(NutsContainer.uriToNutsCode(nodeIterator.next().toString()));
		}

		resIterator = model.listSubjectsWithProperty(Vocabularies.PROP_EU_MERGEDFROM, resource);
		if (resIterator.hasNext()) {
			String mergedIntoCode = NutsContainer.uriToNutsCode(resIterator.next().asResource().toString());
			container.mergedInto = mergedIntoCode;
			mergedInto.put(container.notation, mergedIntoCode);
		}
		if (resIterator.hasNext()) {
			System.err.println("Multiple merges for " + uri);
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

	public NutsContainer getNuts(String key) {
		return nutsIndex.get(key);
	}

	/**
	 * NUTS notation to container.
	 */
	public Map<String, NutsContainer> getNutsIndex() {
		return nutsIndex;
	}

	public Map<String, String> getReplacedBy() {
		return replacedBy;
	}

	public Map<String, String> getMergedInto() {
		return mergedInto;
	}

}