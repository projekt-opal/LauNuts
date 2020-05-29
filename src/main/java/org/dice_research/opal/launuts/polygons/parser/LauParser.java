package org.dice_research.opal.launuts.polygons.parser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
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
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LauParser extends NutsParser{
	
	static {
		name_of_parser_after_final_processing = "LAU_Polygons.json";
		feature_id_type = "gisco_id";
	}
	
	static GeometryFactory geometryFactory = new GeometryFactory();
		
	private static JSONObject wktToJSON(String wkt_parameter, int total_number_of_laus) throws IOException, InterruptedException {
		/**
		 * Call to Node.JS library to parse WKT to GeoJSON. The response from Node.Js is
		 * stored in a temporary file in JSON format.
		 */
		ProcessBuilder pb = new ProcessBuilder("node", "wkt_to_json_parser.js", wkt_parameter);
		pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		pb.redirectError(ProcessBuilder.Redirect.INHERIT);

		Process process = pb.start();
		// String output = IOUtils.toString(process.getInputStream(),
		// StandardCharsets.UTF_8);
		if (total_number_of_laus > 10)
			process.destroy();
		else
			process.waitFor();

		JSONParser node_response_parser = new JSONParser();

		// Read the Node response from this location.
		Reader node_response = new FileReader(
				"src/main/resources/launuts_geojson_and_shape_files/node_response.json");

		JSONObject json_coordinates = null;
		try {
			json_coordinates = (JSONObject) node_response_parser.parse(node_response);
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json_coordinates;
	}
	
	private static JSONObject fill_polygon_metadta_innerRing_geometryType_polygonPoints(JSONArray coordinates, SimpleFeature feature) {
		
		JSONObject a_lau_polygon = new JSONObject();
		int children_of_coordinates = coordinates.size();
		JSONArray holes = new JSONArray(); //inner_rings
		NutsParser lau_parser = new NutsParser(); //To reuse some code
		
		//A list for storing polygons
		ArrayList<Geometry> geometryList = new ArrayList<Geometry>();
		
		//Stores polygon cooridnates of each LAU.
		JSONArray outer_ring_coordinates = new JSONArray();
		
		int number_of_inner_rings = 0;
		JSONArray first_child_of_coordinates = (JSONArray) coordinates.get(0);

		if (children_of_coordinates > 1) {
			a_lau_polygon.put("geometry_type", "MultiPolygon");
			a_lau_polygon.put("coordinates", coordinates);
			for (int array_index = 0; array_index < coordinates.size(); array_index++) 
			{
				
				// This array might contain 2 sub-arrays(linear ring, holes)
				JSONArray child_polygon_coordinates = (JSONArray) coordinates
						.get(array_index);
				System.out.println(feature.getAttributes().toArray()[3].toString());
				LinearRing a_outer_ring = lau_parser.getOuterRing(child_polygon_coordinates, geometryFactory);
				JSONArray child_polygon_inner_rings = null;

				/**
				 * If child_polygon size > 1 then there are inner rings. Then add the inner
				 * rings to the hole array.
				 */
				if (child_polygon_coordinates.size() > 1) {
					child_polygon_inner_rings = lau_parser.get_inner_rings(child_polygon_coordinates);
						holes.add(child_polygon_inner_rings);
						number_of_inner_rings = number_of_inner_rings + child_polygon_inner_rings.size();
				}
				Polygon polygon_from_a_outer_ring = geometryFactory.createPolygon(a_outer_ring, null);
				geometryList.add(polygon_from_a_outer_ring);
			}
			lau_parser.fillOuterRingCoordinates(geometryFactory,geometryList,outer_ring_coordinates);	
			a_lau_polygon.put("inner_rings", holes);
			a_lau_polygon.put("number_of_inner_rings", number_of_inner_rings);
			a_lau_polygon.put("polygon_points", outer_ring_coordinates.size());
		} else {
			System.out.println(feature.getAttributes().toArray()[3].toString());
			LinearRing a_outer_ring = lau_parser.getOuterRing(first_child_of_coordinates, geometryFactory);
			Polygon polygon_from_a_outer_ring = geometryFactory.createPolygon(a_outer_ring , null);
			geometryList.add(polygon_from_a_outer_ring);
			lau_parser.fillOuterRingCoordinates(geometryFactory,geometryList,outer_ring_coordinates);
			a_lau_polygon.put("geometry_type", "Polygon");
			a_lau_polygon.put("coordinates", first_child_of_coordinates);
			
			if(coordinates.size()>1)
				holes = lau_parser.get_inner_rings(coordinates);
			a_lau_polygon.put("inner_rings", holes);
			a_lau_polygon.put("number_of_inner_rings", holes.size());
		}
		a_lau_polygon.put("polygon_points", outer_ring_coordinates.size());
		return a_lau_polygon;
	}

	public static void createLauPolygons() throws IOException, Exception {
		
		System.out.println("1. Please ensure that Launuts data in the folder \"resources/launuts_geojson_and_shape_files\" folder has been extracted!!");
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
		JSONArray all_polygons = new JSONArray();

		FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
		Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")

		FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
		FeatureIterator<SimpleFeature> features = collection.features();
		int total_number_of_laus = 0;
		while (features.hasNext()) {

			SimpleFeature feature = features.next();

			if (feature.getAttributes().toArray()[3].toString().contains("DE_")) {
			// Case study: DE_08326074(MultiPolygon), DE_08326054(Polygon)

				JSONObject a_lau_polygon = new JSONObject();
				byte[] byteArrray = feature.getAttributes().toArray()[2].toString().getBytes();
				a_lau_polygon.put("gisco_id", feature.getAttributes().toArray()[3].toString());
				a_lau_polygon.put("lau_label", feature.getAttributes().toArray()[2].toString().replace("Ã¶", "ö")
						.replace("Ã¤", "ä").replace("Ã¼", "ü").replace("Ã", "Ü").replace("Ã", "Ö"));
				a_lau_polygon.put("lau_code", feature.getAttributes().toArray()[1].toString());

				WKTReader reader = new WKTReader(geometryFactory);
				MultiPolygon multi_polygon = (MultiPolygon) reader
						.read(feature.getAttributes().toArray()[0].toString());

				String wkt_parameter = '"' + multi_polygon.toString() + '"';
				JSONObject json_coordinates = wktToJSON(wkt_parameter, total_number_of_laus);
				
				
				
				//This code block is to evaluate whether a geometry is polygon or multipolygon
				JSONArray coordinates = (JSONArray) json_coordinates.get("coordinates");
				a_lau_polygon = fill_polygon_metadta_innerRing_geometryType_polygonPoints(coordinates, feature);			
				all_polygons.add(a_lau_polygon);

				// Runtime visual response
				ObjectMapper mapper = new ObjectMapper();
				System.out.println(mapper.writeValueAsString(a_lau_polygon));
				System.out.println("Total number of Laus processed: " + total_number_of_laus);
				total_number_of_laus++;

			}

		}

		features.close();
		dataStore.dispose();

		try (FileWriter json_result = new FileWriter("LAU_Polygons.json")) {
			json_result.write(all_polygons.toJSONString());
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
