package org.dice_research.opal.launuts.polygons.parser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.simple.parser.JSONParser;
import org.dice_research.opal.launuts.polygons.Point;
import org.dice_research.opal.launuts.polygons.PolygonParserException;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * LauParser reads the shape file from Eurostat and extract the polygons for all
 * 11118 laus. Some functionality for this operation has been taken from
 * NutsParser e.g. some methods such as getCoordinatesLatLongFormat(),
 * getInnerRings() have been reused by extending NutsParser. The parsed lau
 * polygons are stored in the file LAU_Polygons.json.
 * 
 * The source/input shape file (from Eurostat) has been compressed and put
 * inside the folder "launuts_geojson_and_shape_files". Before running this app
 * the zip file "lau_1_1_million" must be extracted and the project must be
 * updated.
 * 
 * @author Gourab Sahu
 *
 */

public class LauParser extends NutsParser {

	public LauParser() {
		this.nameOfParserAfterFinalProcessing = "LAU_Polygons.json";
		this.featureIdType = "gisco_id";
	}

	private static GeometryFactory geometryFactory = new GeometryFactory();

	private static JSONObject wktToJSON(String wktParameter) throws IOException, InterruptedException {
		/**
		 * Call to Node.JS library to parse WKT to GeoJSON. The response from Node.Js is
		 * stored in a temporary file in JSON format.
		 */
		ProcessBuilder pb = new ProcessBuilder("node", "wkt_to_json_parser.js", wktParameter);
		pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		pb.redirectError(ProcessBuilder.Redirect.INHERIT);

		Process process = pb.start();
		process.waitFor();

		JSONParser nodeResponseParser = new JSONParser();

		// Read the Node response from this location.
		Reader nodeResponse = new FileReader("src/main/resources/launuts_geojson_and_shape_files/node_response.json");

		JSONObject jsonCoordinates = null;
		try {
			jsonCoordinates = (JSONObject) nodeResponseParser.parse(nodeResponse);
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonCoordinates;
	}

	private static JSONObject fillRemainingPolygonMetadta(JSONObject lauPolygon, JSONArray coordinates,
			SimpleFeature feature) {

		int childrenOfCoordinates = coordinates.size();
		JSONArray holes = new JSONArray(); // inner_rings
		NutsParser lauParser = new NutsParser(); // To reuse some code
		JSONArray coordinatesLatLongFormat = new JSONArray();
		int numberOfInnerRings = 0;
		int outerRingSize = 0;
		JSONArray firstChildOfCoordinates = (JSONArray) coordinates.get(0);

		if (childrenOfCoordinates > 1) {

			for (int arrayIndex = 0; arrayIndex < coordinates.size(); arrayIndex++) {

				// This array might contain 2 sub-arrays(linear ring, holes)
				JSONArray childPolygonCoordinates = (JSONArray) coordinates.get(arrayIndex);
				System.out.println(feature.getAttributes().toArray()[3].toString());
				JSONArray childPolygonInnerRings = null;

				/**
				 * If child_polygon size > 1 then there are inner rings. Then add the inner
				 * rings to the hole array.
				 */
				if (childPolygonCoordinates.size() > 1) {
					childPolygonInnerRings = lauParser.getInnerRings(childPolygonCoordinates);
					holes.add(childPolygonInnerRings);
					numberOfInnerRings = numberOfInnerRings + childPolygonInnerRings.size();
				}
				JSONArray child_polygon_outer_ring = (JSONArray) childPolygonCoordinates.get(0);
				outerRingSize = outerRingSize + child_polygon_outer_ring.size();
				JSONArray childPolygonCoordinatesInLatLongFormat = getCoordinatesLatLongFormat(
						childPolygonCoordinates);
				coordinatesLatLongFormat.add(childPolygonCoordinatesInLatLongFormat);
			}
			if (lauParser.areValidPolygons(coordinatesLatLongFormat, "multipolygon_type"))
				lauPolygon.put("valid_polygon", "true");
			else
				lauPolygon.put("valid_polygon", "false");
			lauPolygon.put("geometry_type", "MultiPolygon");
			lauPolygon.put("coordinates", coordinatesLatLongFormat);
			lauPolygon.put("inner_rings", holes);
			lauPolygon.put("number_of_inner_rings", numberOfInnerRings);
			lauPolygon.put("polygon_points", outerRingSize);
		} else {
			System.out.println(feature.getAttributes().toArray()[3].toString());
			if (lauParser.areValidPolygons(firstChildOfCoordinates, "polygon_type"))
				lauPolygon.put("valid_polygon", "true");
			else
				lauPolygon.put("valid_polygon", "false");
			lauPolygon.put("geometry_type", "Polygon");
			lauPolygon.put("coordinates", getCoordinatesLatLongFormat(firstChildOfCoordinates));

			if (firstChildOfCoordinates.size() > 1)
				holes = lauParser.getInnerRings(coordinates);
			JSONArray polygon_outer_ring = (JSONArray) firstChildOfCoordinates.get(0);
			lauPolygon.put("inner_rings", holes);
			lauPolygon.put("number_of_inner_rings", holes.size());
			lauPolygon.put("polygon_points", polygon_outer_ring.size());
		}

		return lauPolygon;
	}

	public static void createLauPolygons() throws IOException, Exception {

		System.out.println(
				"1. Please ensure that Launuts data in the folder \"resources/launuts_geojson_and_shape_files\" folder has been extracted!!");
		System.out.println(" ");
		System.out.println("2. After extraction, do not forget to refresh the project by pressing Alt+F5");
		System.out.println(" ");
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		File file = new File(new LauParser().getClass().getClassLoader()
				.getResource("launuts_geojson_and_shape_files/lau_1_1_million/LAU_2018.shp").getFile());
		Map<String, Object> map = new HashMap<>();
		map.put("url", file.toURI().toURL());

		DataStore dataStore = DataStoreFinder.getDataStore(map);
		String typeName = dataStore.getTypeNames()[0];
		JSONArray allPolygons = new JSONArray();

		FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
		Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")

		FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
		FeatureIterator<SimpleFeature> features = collection.features();
		int totalNumberOfLaus = 0;
		while (features.hasNext()) {

			SimpleFeature feature = features.next();

			if (feature.getAttributes().toArray()[3].toString().contains("DE_")) {
				// Case study: DE_08326074(MultiPolygon), DE_08326054(Polygon)

				JSONObject lauPolygon = new JSONObject();
				byte[] byteArrray = feature.getAttributes().toArray()[2].toString().getBytes();
				lauPolygon.put("gisco_id", feature.getAttributes().toArray()[3].toString());
				lauPolygon.put("lau_label", feature.getAttributes().toArray()[2].toString().replace("Ã¶", "ö")
						.replace("Ã¤", "ä").replace("Ã¼", "ü").replace("Ã", "Ü").replace("Ã", "Ö"));
				lauPolygon.put("lau_code", feature.getAttributes().toArray()[1].toString());

				WKTReader reader = new WKTReader(geometryFactory);
				MultiPolygon multiPolygon = (MultiPolygon) reader
						.read(feature.getAttributes().toArray()[0].toString());

				String wktParameter = '"' + multiPolygon.toString() + '"';
				JSONObject jsonCoordinates = wktToJSON(wktParameter);

				// This code block is to evaluate whether a geometry is polygon or multipolygon
				JSONArray coordinates = (JSONArray) jsonCoordinates.get("coordinates");
				lauPolygon = fillRemainingPolygonMetadta(lauPolygon, coordinates, feature);
				allPolygons.add(lauPolygon);

				// Runtime visual response
				ObjectMapper mapper = new ObjectMapper();
				System.out.println(mapper.writeValueAsString(lauPolygon));
				System.out.println("Total number of Laus processed: " + totalNumberOfLaus);
				totalNumberOfLaus++;

			}

		}

		features.close();
		dataStore.dispose();

		try (FileWriter json_result = new FileWriter("LAU_Polygons.json")) {
			json_result.write(allPolygons.toJSONString());
			json_result.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<Point> getLauCenterPoints(String lauCode) throws PolygonParserException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.dice_research.opal.launuts.polygons.MultiPolygon getLauPolygon(String lauCode)
			throws PolygonParserException {

		return getNutsPolygon(lauCode);
	}

}
