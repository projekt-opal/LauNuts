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
 * Reads points from GeoJson file.
 *
 * @author Adrian Wilke
 */
public class PointsReader {

	private static final Logger LOGGER = LogManager.getLogger();

	private File geoJsonFile;
	private Map<String, PointsContainer> results;

	public PointsReader(File geoJsonFile) {
		this.geoJsonFile = geoJsonFile;
	}

	public PointsReader extract() {

		results = new TreeMap<>();

		try {
			JsonElement root = JsonParser.parseReader(new FileReader(geoJsonFile));
			JsonArray features = root.getAsJsonObject().get("features").getAsJsonArray();

			for (JsonElement jsonElement : features) {
				PointsContainer container = extract(jsonElement.getAsJsonObject());
				if (container.isValid()) {
					results.put(container.nutsId, container);
				} else {
					LOGGER.warn("Skipping invalid container: " + container.nutsId);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return this;
	}

	private PointsContainer extract(JsonObject jo) {
		PointsContainer container = new PointsContainer();
		container.nutsId = jo.get("id").getAsString();

		JsonObject geometry = jo.getAsJsonObject("geometry");
		JsonArray coordinates = geometry.getAsJsonArray("coordinates");
		container.longitude = coordinates.get(0).getAsFloat();
		container.latitude = coordinates.get(1).getAsFloat();

		JsonObject properties = jo.getAsJsonObject("properties");
		container.nameLatin = properties.get("NAME_LATN").getAsString();
		container.level = properties.get("LEVL_CODE").getAsInt();

		return container;
	}

	/**
	 * Returns results.
	 * 
	 * Map keys: NUTS codes.
	 */
	public Map<String, PointsContainer> getResults() {
		return results;
	}

}