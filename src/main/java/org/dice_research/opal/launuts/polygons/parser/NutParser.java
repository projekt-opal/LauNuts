package org.dice_research.opal.launuts.polygons.parser;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import org.json.simple.JSONObject;
import org.dice_research.opal.launuts.lau.LauReaderInterface;
import org.dice_research.opal.launuts.polygons.Point;
import org.dice_research.opal.launuts.polygons.PolygonParserException;
import org.dice_research.opal.launuts.polygons.PolygonParserInterface;
import org.json.simple.JSONArray;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class NutParser implements PolygonParserInterface {

	private String nut_resolutions[] = { "nut_1_1_million", "nut_1_3_million", "nut_1_10_million", "nut_1_20_million",
			"nut_1_60_million" };

	private File source_directory_for_geojson = new File(new NutParser().getClass().getClassLoader()
			.getResource("launuts_geojson_and_shape_files").getFile());

	private String nuts_level[] = { "LEVL_3", "LEVL_2", "LEVL_1", "LEVL_0" };

	private JSONParser parser = new JSONParser();
	private Reader geojson_reader;
	private JSONArray all_nuts_with_polygons = new JSONArray();

	// Nut-id and Nut-name for all nuts.
	private HashMap<String, String> nutId_nutName = new HashMap<String, String>();

	private JSONArray get_inner_rings(JSONArray child_polygon_coordinates_arrays) {

		JSONArray inner_rings = new JSONArray();

		// first array is always outer ring !!
		// The rest are inner rings
		for (int counter = 1; counter < child_polygon_coordinates_arrays.size(); counter++) {

			// This will store inner ring coordinates for one inner-ring at a time
			JSONArray new_inner_ring = new JSONArray();

			JSONArray old_inner_ring = (JSONArray) child_polygon_coordinates_arrays.get(counter);

			for (int count = 0; count < old_inner_ring.size(); count++) {

				/*
				 * This array will be used to change the form of coordinates. It contains same
				 * coordinates as "points" but in different form.
				 */
				JSONArray old_coordinates = (JSONArray) old_inner_ring.get(count);
				JSONArray new_coordinates = new JSONArray();
				new_coordinates.add(old_coordinates.toArray()[1]); // Lattitude
				new_coordinates.add(old_coordinates.toArray()[0]); // Longitude
				new_inner_ring.add(new_coordinates);

			}

			// Finally add all inner_rings or holes to one array of LinearRing.
			inner_rings.add(new_inner_ring);
		}

		return inner_rings;
	}

	/*
	 * Check if a particular NUT exists in "all_nuts_with_polygons". If yes, then
	 * check if the current NUTS has least polygon points and if yes again then
	 * replace the existing same-ID NUTS with current NUT.
	 * 
	 * else if a particular nuts does not exist then simply add that nuts
	 * "all_nuts_with_polygons".
	 */
	private void hasThisNutLeastNumberOfCoordinatesIfTrueThenAdd(JSONArray all_nuts_with_polygons,
			JSONObject a_nut_polygon) {

		String temp_nut_id = a_nut_polygon.get("NUT_id").toString();
		int temp_nut_polygon_points = Integer.parseInt(a_nut_polygon.get("Polygon_Points").toString());

		// Does a similar old nut has least number of points?
		boolean old_existing_nut_has_least_resolution = true;

		// This nut has not been processed before
		boolean is_tempNut_a_newly_discovered_nut = true;
		/*
		 * If new_nuts_has_least_resolution then remove the existing nuts from
		 * all_nuts_with_polygons.
		 */

		JSONObject nut_to_remove = null;

		Iterator<JSONObject> nut_iterator = all_nuts_with_polygons.iterator();
		while (nut_iterator.hasNext()) {

			JSONObject existing_old_nut = nut_iterator.next();
			if (existing_old_nut.get("NUT_id").toString().equals(temp_nut_id)) {

				is_tempNut_a_newly_discovered_nut = false;
				// check if new_nuts has least polygon points
				int existing_old_nut_polygon_points = Integer
						.parseInt(existing_old_nut.get("Polygon_Points").toString());
				System.out.println(
						"NUTS:" + temp_nut_id + " " + " " + "existing_polygon_points=" + existing_old_nut_polygon_points
								+ " " + " " + " " + "new_poly_points=" + temp_nut_polygon_points);
				if (temp_nut_polygon_points < existing_old_nut_polygon_points) {
					old_existing_nut_has_least_resolution = false;
					nut_to_remove = existing_old_nut;
					break;
				}

			}

		}

		if (old_existing_nut_has_least_resolution)
			;
		else {
			all_nuts_with_polygons.remove(nut_to_remove);
			all_nuts_with_polygons.add(a_nut_polygon);
		}

		if (is_tempNut_a_newly_discovered_nut)
			all_nuts_with_polygons.add(a_nut_polygon);

	}

	private void extractNutIdAndNutnameFromCsvForAllNuts() throws FileNotFoundException {
		File resource_folder = source_directory_for_geojson;

		File[] listOfFolders = resource_folder.listFiles();

		/**
		 * Read "NUTS_AT_2016.csv" from the folder "1_60_million" and extract
		 * information and store it in key:value pair i.e NUTS_ID:NUTS_NAME in the
		 * hashmap nutsName.
		 */
		for (File folder : listOfFolders) {
			if (folder.getName().equals("nut_1_60_million")) {
				for (File file : folder.listFiles()) {
					if (file.getName().equals("NUTS_AT_2016.csv")) {
						Scanner csv_scanner = new Scanner(file);
						csv_scanner.useDelimiter(",");
						while (csv_scanner.hasNext()) {
							String csv_data[] = csv_scanner.nextLine().split(",");
							if (csv_data[0].contains("DE")) {
								// If NUTS_NAME like this --> Mainz, Kreisfreie Stadt
								if (csv_data.length == 4)
									nutId_nutName.put(csv_data[1],
											csv_data[2].replace("\"", "") + csv_data[3].replace("\"", ""));
								else
									nutId_nutName.put(csv_data[1], csv_data[2]);
							}
						}
						csv_scanner.close();
					}
				}
			}
		}
	}

	public LinearRing getOuterRing(JSONObject feature, JSONArray child_polygon_coordinates,
			GeometryFactory geometryFactory) {

		LinearRing outer_ring = null;

		// Coordinates in the format: (Latitude, Longitude)
		ArrayList<Coordinate> outer_ring_coordinates_latlong = new ArrayList<Coordinate>();

		// Coordinates in the format: (Longitude, Latitude)
		JSONArray outer_ring_coordinates_longlat = (JSONArray) child_polygon_coordinates.get(0);

		for (int count = 0; count < outer_ring_coordinates_longlat.size(); count++) {

			/*
			 * This array will be used to change the form of coordinates. It contains same
			 * coordinates as "points" but in different form.
			 */
			JSONArray temp_array = (JSONArray) outer_ring_coordinates_longlat.get(count);
			try {
				outer_ring_coordinates_latlong.add(new Coordinate((long) temp_array.get(1), (long) temp_array.get(0)));
			} catch (Exception e) {
				try {
					outer_ring_coordinates_latlong
							.add(new Coordinate((double) temp_array.get(1), (double) temp_array.get(0)));
				} catch (ClassCastException X_double_Y_long) {

//							System.out.println("Solution1: "+Coordinates_array_Long_Lat.get(1).getClass());
//							System.out.println("Solution2: "+Coordinates_array_Long_Lat.get(0).getClass());
					try {
						Long y_cord = (long) temp_array.get(0);
						outer_ring_coordinates_latlong
								.add(new Coordinate((double) temp_array.get(1), y_cord.doubleValue()));
					} catch (ClassCastException X_long_Y_double) {
						Long x_cord = (long) temp_array.get(1);
						outer_ring_coordinates_latlong
								.add(new Coordinate(x_cord.doubleValue(), (double) temp_array.get(0)));
					}
				}
			}
		}

		outer_ring = geometryFactory
				.createLinearRing((Coordinate[]) outer_ring_coordinates_latlong.toArray(new Coordinate[] {}));

		return outer_ring;
	}

	public static void fillOuterRingCoordinates(GeometryFactory geometryFactory, ArrayList<Geometry> geometryList,
			JSONArray outer_ring_coordinates) {

		// Now make a geometrical union out of geometryList
		try {
			GeometryCollection geometryCollection = (GeometryCollection) geometryFactory.buildGeometry(geometryList);

			MultiPolygon final_polygon = (MultiPolygon) geometryCollection.union();

			for (int index = 0; index < final_polygon.getCoordinates().length; index++) {

				// An instance of a coordinate
				JSONArray Coordinates_Lat_Long = new JSONArray();
				// Lattitude
				Coordinates_Lat_Long.add(final_polygon.getCoordinates()[index].getX());
				// Longitude
				Coordinates_Lat_Long.add(final_polygon.getCoordinates()[index].getY());

				outer_ring_coordinates.add(Coordinates_Lat_Long);
			}
		} catch (Exception polygon_to_geometrycollection) {

			Geometry geometryCollection = geometryFactory.buildGeometry(geometryList);

			for (int index = 0; index < geometryCollection.getCoordinates().length; index++) {

				// An instance of a coordinate
				JSONArray Coordinates_Lat_Long = new JSONArray();
				// Lattitude
				Coordinates_Lat_Long.add(geometryCollection.getCoordinates()[index].getX());
				// Longitude
				Coordinates_Lat_Long.add(geometryCollection.getCoordinates()[index].getY());
				outer_ring_coordinates.add(Coordinates_Lat_Long);
			}

		}

	}

	public static boolean areValidPolygons(JSONArray coordinates, String geometry_type) {

		boolean are_valid_polygons = true;

		if (geometry_type.equals("multipolygon_type")) {
			for (int array_index = 0; array_index < coordinates.size(); array_index++) {
				JSONArray child_polygon_coordinates = (JSONArray) coordinates.get(array_index);
				JSONArray outer_ring_coordinates_longlat = (JSONArray) child_polygon_coordinates.get(0);
				if (!(outer_ring_coordinates_longlat.get(0)
						.equals(outer_ring_coordinates_longlat.get(outer_ring_coordinates_longlat.size() - 1)))) {
					are_valid_polygons = false;
					break;
				}
			}
		} else if (geometry_type.equals("polygon_type")) {
			JSONArray outer_ring_coordinates_longlat = (JSONArray) coordinates.get(0);
			if (!(outer_ring_coordinates_longlat.get(0)
					.equals(outer_ring_coordinates_longlat.get(outer_ring_coordinates_longlat.size() - 1))))
				are_valid_polygons = false;

		}
		return are_valid_polygons;
	}

	public void createNutParser() throws ClassCastException, FileNotFoundException {

		extractNutIdAndNutnameFromCsvForAllNuts();

		/*
		 * First start with checking polygons for Level3 nut. For that we first start
		 * with the directory 1_1_million and store the polygons with least number of
		 * points. We then check other folders as well because in lower scale
		 * resolution(such as 1_1_mil) some NUTS might be represented as points which
		 * could be polygon in higher scale(such as in 1_20_mil). Repeat the same
		 * process for all other NUTS.
		 */
		for (int levl_counter = 0; levl_counter < nuts_level.length; levl_counter++) {

			for (int dir_counter = 0; dir_counter < nut_resolutions.length; dir_counter++) {
				System.out.println(source_directory_for_geojson + "\\/" + nut_resolutions[dir_counter]);
				File current_dir = new File(source_directory_for_geojson + "\\/" + nut_resolutions[dir_counter]);
				for (int file_counter = 0; file_counter < current_dir.listFiles().length; file_counter++) {
					String geojson_file = current_dir.listFiles()[file_counter].getName();

					// Files labelled with 4326 contains coordinates in decimal degree.
					if ((geojson_file.contains("RG") && geojson_file.contains("4326")
							&& geojson_file.contains(nuts_level[levl_counter]))
							|| (geojson_file.contains("LB") && geojson_file.contains("4326")
									&& geojson_file.contains(nuts_level[levl_counter]))) {

						System.out.println(current_dir.listFiles()[file_counter].getName().toString() + ":checked");

						try {
							geojson_reader = new FileReader(
									source_directory_for_geojson + "\\/" + nut_resolutions[dir_counter] + "\\/"
											+ current_dir.listFiles()[file_counter].getName().toString());

							JSONObject root_object = (JSONObject) parser.parse(geojson_reader);

							JSONArray features = (JSONArray) root_object.get("features");

							// features is an array of nuts in a GeoJson file
							Iterator<JSONObject> featuresIterator = features.iterator();

							while (featuresIterator.hasNext()) {

								JSONObject feature = featuresIterator.next();

								// We are only interested in NUTS from Germany
								if (feature.get("id").toString().contains("DE")) {

									// An object which will store information about individual nut.
									JSONObject a_nut_polygon = new JSONObject();

									// Carries outer_ring coordinates of a NUT.
									JSONArray outer_ring_coordinates = new JSONArray();

									// Geometry of a feature contains coordinates
									JSONObject json_geometry = (JSONObject) feature.get("geometry");

									/*
									 * This array does not contain the actual coordinates but another array which in
									 * turn might contain 2 arrays(liner ring, holes). In most cases Coordinates
									 * array will contain only single array(outer_ring).
									 */
									JSONArray coordinates = (JSONArray) json_geometry.get("coordinates");

									// Create an arraylist of geometry for storing all polygons
									ArrayList<Geometry> geometryList = new ArrayList<Geometry>();

									// geometryfactory object will be used to perform geometrical operations
									GeometryFactory geometryFactory = new GeometryFactory();

									// These are inner_rings of each Nut.
									JSONArray holes = new JSONArray();

									int number_of_inner_rings = 0;

									/*
									 * If the geometry type is "MultiPolygon"
									 */
									if (json_geometry.get("type").toString().equals("MultiPolygon")) {

										for (int array_index = 0; array_index < coordinates.size(); array_index++) {

											// This array might contain 2 sub-arrays(linear ring, holes)
											JSONArray child_polygon_coordinates = (JSONArray) coordinates
													.get(array_index);

											// Create a linear-ring/outer-ring using the points coordinates array
											LinearRing a_outer_ring = getOuterRing(feature, child_polygon_coordinates,
													geometryFactory);

											// This will store polygon's inner rings for current polygon
											JSONArray child_polygon_inner_rings = null;

											/**
											 * If sub_array size > 1 then there are inner rings. Then add the inner
											 * rings to the hole array.
											 */
											if (child_polygon_coordinates.size() > 1) {
												child_polygon_inner_rings = get_inner_rings(child_polygon_coordinates);
												holes.add(child_polygon_inner_rings);
												number_of_inner_rings = number_of_inner_rings
														+ child_polygon_inner_rings.size();
											}

											/**
											 * Create a polygon from the outer_ring, null= means no hole. There might be
											 * holes present in a polygon but right now, we are interested only in
											 * outer_ring. Holes can be extracted from outer_ring using WKTWriter of
											 * GeoTools.
											 */
											Polygon polygon_from_a_outer_ring = geometryFactory
													.createPolygon(a_outer_ring, null);

											/*
											 * Add all new polygons into a single geometry for conversion of all
											 * polygons into a union of polygons.
											 */
											geometryList.add(polygon_from_a_outer_ring);

										}

										fillOuterRingCoordinates(geometryFactory, geometryList, outer_ring_coordinates);

										// Check for validity of polygon
										if (areValidPolygons(coordinates, "polygon_type"))
											a_nut_polygon.put("Valid_Polygon", "true");
										else
											a_nut_polygon.put("Valid_Polygon", "false");

										a_nut_polygon.put("NUT_name", nutId_nutName.get(feature.get("id").toString()));
										a_nut_polygon.put("Level", nuts_level[levl_counter]);

										// From which shape(LineString,MultiPolygon) polygon was extracted
										a_nut_polygon.put("Geometry_type",
												json_geometry.get("type").toString());
										a_nut_polygon.put("NUT_id", feature.get("id").toString());
										a_nut_polygon.put("File",
												current_dir.listFiles()[file_counter].getName().toString());
										a_nut_polygon.put("Outer_ring", coordinates);
										a_nut_polygon.put("Inner_rings", holes);
										a_nut_polygon.put("Number_of_inner_rings", number_of_inner_rings);
										a_nut_polygon.put("Polygon_Points", outer_ring_coordinates.size());
										hasThisNutLeastNumberOfCoordinatesIfTrueThenAdd(all_nuts_with_polygons,
												a_nut_polygon);

									}

									// If the geometry type is "Polygon"
									if (json_geometry.get("type").toString().equals("Polygon")) {

										// Create a linear-ring/outer-ring using the points coordinates array
										LinearRing a_outer_ring = getOuterRing(feature, coordinates, geometryFactory);

										/**
										 * If sub_array size > 1 then there are inner rings. Then add the inner rings to
										 * the hole array.
										 */
										if (coordinates.size() > 1) {
											holes = get_inner_rings(coordinates);
											number_of_inner_rings = number_of_inner_rings + holes.size();
										}

										/**
										 * Create a polygon from the outer_ring, null= means no hole. There might be
										 * holes present in a polygon but right now, we are interested only in
										 * outer_ring. Holes can be extracted from outer_ring using WKTWriter of
										 * GeoTools.
										 */
										Polygon polygon_from_a_outer_ring = geometryFactory.createPolygon(a_outer_ring,
												null);
										geometryList.add(polygon_from_a_outer_ring);
										fillOuterRingCoordinates(geometryFactory, geometryList, outer_ring_coordinates);

										// Check for validity of polygon
										if (areValidPolygons(coordinates, "polygon_type"))
											a_nut_polygon.put("Valid_Polygon", "true");
										else
											a_nut_polygon.put("Valid_Polygon", "false");

										a_nut_polygon.put("NUT_name", nutId_nutName.get(feature.get("id").toString()));
										a_nut_polygon.put("Level", nuts_level[levl_counter]);

										// From which shape(LineString,MultiPolygon) polygon was extracted
										a_nut_polygon.put("Geometry_type",
												json_geometry.get("type").toString());
										a_nut_polygon.put("NUT_id", feature.get("id").toString());
										a_nut_polygon.put("File",
												current_dir.listFiles()[file_counter].getName().toString());
										a_nut_polygon.put("Outer_ring", coordinates);
										a_nut_polygon.put("Inner_rings", holes);
										a_nut_polygon.put("Number_of_inner_rings", number_of_inner_rings);
										a_nut_polygon.put("Polygon_Points", outer_ring_coordinates.size());
										hasThisNutLeastNumberOfCoordinatesIfTrueThenAdd(all_nuts_with_polygons,
												a_nut_polygon);
									}

								}
							}

						} catch (IOException | ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}

			}

		}

		// Write JSON file
		try (

				FileWriter file = new FileWriter("NUT_Polygons.json")) {
			if (!(all_nuts_with_polygons.isEmpty())) {
				file.write(all_nuts_with_polygons.toJSONString());
				file.flush();
			}

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Point> getNutsCenterPoints(String nutsCode) throws PolygonParserException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.dice_research.opal.launuts.polygons.MultiPolygon getNutsPolygon(String nutsCode)
			throws PolygonParserException {

		try {
			org.dice_research.opal.launuts.polygons.MultiPolygon multi_polygon = null;
			
			geojson_reader = new FileReader("NUT_Polygons.json");
			JSONArray nuts = (JSONArray) parser.parse(geojson_reader);
			
			Iterator<JSONObject> nutsIterator = nuts.iterator();
			while (nutsIterator.hasNext()) {
				JSONObject nut = nutsIterator.next();
				if(nut.get("NUT_id").toString().equals(nutsCode)) {
					String geometry_type = nut.get("Geometry_type").toString();
					JSONArray outer_ring =  (JSONArray) nut.get("Outer_ring");
					if(geometry_type.equals("Polygon")) {	
						multi_polygon = new org.dice_research.opal.launuts.polygons.MultiPolygon();
						for(int index=0; index<outer_ring.size();index++) {
							
							JSONArray ring = (JSONArray) outer_ring.get(index); //Could be an inner or outer ring
							org.dice_research.opal.launuts.polygons.Polygon ring_polygon= new org.dice_research.opal.launuts.polygons.Polygon();
							
							for(int coordinate_index = 0;coordinate_index<ring.size();coordinate_index++)
							{
								org.dice_research.opal.launuts.polygons.Point point = new org.dice_research.opal.launuts.polygons.Point();
								JSONArray lattitude_longitude = (JSONArray) ring.get(coordinate_index);
								point.latitude = Float.parseFloat(lattitude_longitude.get(0).toString());
								point.longitude = Float.parseFloat(lattitude_longitude.get(1).toString());
								ring_polygon.points.add(point);
							}
							multi_polygon.polygons.add(ring_polygon);
						}
					}
					else {						
						multi_polygon = new org.dice_research.opal.launuts.polygons.MultiPolygon(outer_ring.size());
						for(int index=0; index<outer_ring.size();index++) {
							JSONArray child_polygon = (JSONArray) outer_ring.get(index); //child_polygon, may have a hole(inner_ring)
							for (int ring_index=0; ring_index<child_polygon.size();ring_index++) {
								
								JSONArray ring = (JSONArray) outer_ring.get(index); //ring can be an inner_ring or outer_ring
								org.dice_research.opal.launuts.polygons.Polygon ring_polygon= new org.dice_research.opal.launuts.polygons.Polygon();
								
								for(int coordinate_index = 0;coordinate_index<ring.size();coordinate_index++)
								{
									org.dice_research.opal.launuts.polygons.Point point = new org.dice_research.opal.launuts.polygons.Point();
									JSONArray lattitude_longitude = (JSONArray) ring.get(coordinate_index);
									point.latitude = (float) lattitude_longitude.get(0);
									point.longitude = (float) lattitude_longitude.get(1);
									ring_polygon.points.add(point);
								}
								multi_polygon.polygons.add(ring_polygon);
							}
						}
					}
				}
			}
			return multi_polygon;
		} catch (Exception e) {
			
		}
		return null;
	}

	@Override
	public LauReaderInterface setSourceDirectory(File directory) throws PolygonParserException {
		// TODO Auto-generated method stub

		return null;
	}

}
