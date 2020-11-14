package org.dice_research.opal.launuts;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.SKOS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Generating text file for OPAL metadata refinement.
 * 
 * @author Adrian Wilke
 */
public class OpalGenerator {

	private static final Logger LOGGER = LogManager.getLogger();
	public static final String NUTS_URI_PREFIX = "http://data.europa.eu/nuts/code/";

	private Map<String, PointsContainer> nuts = new HashMap<>();
	private Map<String, PolygonContainer> lau = new HashMap<>();

	public OpalGenerator collectNuts(Model nutsModel, String nutsCode, Map<String, PointsContainer> nutsGeo) {
		addNuts(nutsCode, nutsGeo);

		Resource resource = nutsModel.getResource("http://data.europa.eu/nuts/code/" + nutsCode);
		recursiveCollectNuts(nutsModel, resource, nutsGeo);
		LOGGER.info("Collected " + nutsCode + ", overall NUTS geo: " + nuts.size());

		return this;
	}

	private void recursiveCollectNuts(Model nutsModel, Resource resource, Map<String, PointsContainer> nutsGeo) {
		StmtIterator it = resource.listProperties(SKOS.narrower);
		while (it.hasNext()) {
			RDFNode narrower = it.next().getObject();
			if (narrower.isResource()) {
				String uri = narrower.asResource().getURI();
				if (uri.startsWith(NUTS_URI_PREFIX)) {
					addNuts(uri.substring(NUTS_URI_PREFIX.length()), nutsGeo);
				}
				recursiveCollectNuts(nutsModel, narrower.asResource(), nutsGeo);
			}
		}
	}

	private void addNuts(String nutsCode, Map<String, PointsContainer> nutsGeo) {
		if (nutsGeo.containsKey(nutsCode)) {
			nuts.put(nutsCode, nutsGeo.get(nutsCode));
		} else {
			LOGGER.warn("NUTS geo not found " + nutsCode);
		}
	}

	public OpalGenerator collectLau(Map<String, List<LauContainer>> lau, String countryCode,
			Map<String, Map<String, PolygonContainer>> lauGeo) {
		Map<String, PolygonContainer> lauGeoCountry = lauGeo.get(countryCode);

		for (LauContainer lauContainer : lau.get(countryCode)) {
			if (nuts.keySet().contains(lauContainer.nutsCode)) {

				String lauCode = lauContainer.lauCode;
				if (countryCode.equals("AT") && lauCode.endsWith(".0")) {
					lauCode = lauCode.substring(0, lauCode.length() - 2);
				}

				if (lauGeoCountry.containsKey(lauCode)) {
					this.lau.put(lauCode, lauGeoCountry.get(lauCode));
				} else {
					LOGGER.warn("LAU geo not found " + lauContainer.lauCode + " / " + lauCode);
				}

			} else {
				LOGGER.warn("No NUTS found for " + lauContainer.lauCode + " " + lauContainer.nutsCode);
			}
		}

		LOGGER.info("Collected " + countryCode + ", overall LAU geo: " + this.lau.size());
		return this;
	}

	public void createOpalData() {
		Map<String, String> data = new TreeMap<>();
		for (PointsContainer container : nuts.values()) {
			String name = stripName(container.nameLatin);
			if (!data.containsKey(name)) {
				data.put(name, container.latitude + "\n" + container.longitude);
			}
		}
		for (PolygonContainer container : lau.values()) {
			String name = stripName(container.name);
			if (!data.containsKey(name)) {
				if (container.centroid != null)
					data.put(name, container.centroid.getCoordinate().x + "\n" + container.centroid.getCoordinate().y);
			}
		}

		StringBuilder stringBuilder = new StringBuilder();
		for (Entry<String, String> entry : data.entrySet()) {
			stringBuilder.append(entry.getKey());
			stringBuilder.append("\n");
			stringBuilder.append(entry.getValue());
			stringBuilder.append("\n");
		}

		File file = new File(Files.FINAL_OPAL);
		try {
			FileUtils.write(file, stringBuilder.toString(), StandardCharsets.UTF_8, false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		LOGGER.info("Wrote OPAL file " + file.getAbsolutePath());
		LOGGER.info("Entries: " + data.size());
	}

	private String stripName(String name) {
		if (name.contains(",")) {
			return (name.substring(0, name.indexOf(',')));
		} else {
			return name.trim();
		}
	}

}