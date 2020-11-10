package org.dice_research.opal.launuts;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.process.geometry.GeometryFunctions;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Container for polygon data.
 * 
 * Contains polygon OR multi-polygon.
 *
 * @author Adrian Wilke
 */
public class PolygonContainer implements Serializable {

	private static final long serialVersionUID = 1L;

	public String lauId;
	public String countryId;

	public String polygonGeoJson;
	public String multiPolygonGeoJson;

	public String name;

	public Geometry centroid;

	public boolean isValid() {
		if (lauId == null || lauId.isEmpty()) {
			return false;
		}
		if (countryId == null || countryId.isEmpty()) {
			return false;
		}
		if ((polygonGeoJson == null || polygonGeoJson.isEmpty())
				&& (multiPolygonGeoJson == null || multiPolygonGeoJson.isEmpty())) {
			return false;
		}
		return true;
	}

	public void computeCentroid() {

		// https://howtodoinjava.com/gson/gson-jsonparser/
		JsonArray ja = JsonParser.parseString(polygonGeoJson).getAsJsonArray().get(0).getAsJsonArray();

		// https://docs.geotools.org/stable/userguide/library/jts/geometry.html
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

		List<Coordinate> list = new LinkedList<>();
		for (JsonElement jsonElement : ja) {
			JsonArray jaCoord = jsonElement.getAsJsonArray();
			list.add(new Coordinate(jaCoord.get(1).getAsDouble(), jaCoord.get(0).getAsDouble()));
		}

		LinearRing ring = geometryFactory.createLinearRing(list.toArray(new Coordinate[0]));
		LinearRing holes[] = null;
		Polygon polygon = geometryFactory.createPolygon(ring, holes);

		centroid = GeometryFunctions.centroid(polygon);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(lauId);
		sb.append(" ");
		sb.append(countryId);
		sb.append(" ");
		if (name != null) {
			sb.append(name);
			sb.append(" ");
		}
		return sb.toString();
	}
}