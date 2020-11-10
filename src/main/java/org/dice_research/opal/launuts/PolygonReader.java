package org.dice_research.opal.launuts;

import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Reads polygons from GeoJson file.
 *
 * @author Adrian Wilke
 */
public class PolygonReader {

	private static final Logger LOGGER = LogManager.getLogger();

	private File geoJsonFile;
	private Map<String, Map<String, PolygonContainer>> results;

	public PolygonReader(File geoJsonFile) {
		this.geoJsonFile = geoJsonFile;
	}

	public PolygonReader extract() {

		results = new TreeMap<>();

		try {
			JsonElement root = JsonParser.parseReader(new FileReader(geoJsonFile));
			JsonArray features = root.getAsJsonObject().get("features").getAsJsonArray();

			for (JsonElement jsonElement : features) {

				PolygonContainer container = extract(jsonElement.getAsJsonObject());
				if (container.isValid()) {

					if (!results.containsKey(container.countryId)) {
						results.put(container.countryId, new TreeMap<>());
					}

					results.get(container.countryId).put(container.lauId, container);

				} else {
					LOGGER.warn("Skipping invalid container: " + container.countryId + " " + container.lauId + " "
							+ container.polygonGeoJson);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return this;
	}

	private PolygonContainer extract(JsonObject jo) {
		PolygonContainer container = new PolygonContainer();

		JsonObject properties = jo.getAsJsonObject("properties");
		container.lauId = properties.get("LAU_CODE").getAsString();
		container.countryId = properties.get("CNTR_CODE").getAsString();
		container.name = properties.get("LAU_NAME").getAsString();

		JsonObject geometry = jo.getAsJsonObject("geometry");
		JsonArray coordinates = geometry.getAsJsonArray("coordinates");
		if (geometry.get("type").getAsString().equals("Polygon")) {
			container.polygonGeoJson = coordinates.toString();
		} else if (geometry.get("type").getAsString().equals("MultiPolygon")) {
			container.multiPolygonGeoJson = coordinates.toString();
		} else {
			LOGGER.warn("Unknown type: " + geometry.get("type") + " " + container.countryId + " " + container.lauId);
		}

		if (container.polygonGeoJson != null) {
			container.computeCentroid();
		}

		return container;
	}

	/**
	 * Returns results.
	 * 
	 * Map keys: Country codes, LAU codes.
	 */
	public Map<String, Map<String, PolygonContainer>> getResults() {
		return results;
	}

}