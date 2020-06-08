package org.dice_research.opal.launuts.polygons.parser;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.FileNotFoundException;

public class NutsParser implements PolygonParserInterface {

	private static String nuts_resolutions[] = { "nuts_1_1_million", "nuts_1_3_million", "nuts_1_10_million",
			"nuts_1_20_million", "nuts_1_60_million" };

	private static File source_directory_for_geojson = new File(
			new NutsParser().getClass().getClassLoader().getResource("launuts_geojson_and_shape_files").getFile());

	private static String nuts_level[] = { "LEVL_3", "LEVL_2", "LEVL_1", "LEVL_0" };

	private static JSONParser json_parser = new JSONParser();
	private static Reader geojson_reader;
	private static JSONArray all_nuts_with_polygons = new JSONArray();
	protected static String name_of_parser_after_final_processing = "NUTS_Polygons.json";
	protected static String feature_id_type = "nuts_id";

	// Nuts-id and Nuts-name for all nuts.
	private static HashMap<String, String> nutsId_nutsName = new HashMap<String, String>();

	public static JSONArray getInnerRings(JSONArray child_polygon_coordinates_arrays) {

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
	 * Check if a particular NUTS exists in "all_nuts_with_polygons". If yes, then
	 * check if the current NUTS has least polygon points and if yes again then
	 * replace the existing same-ID NUTS with current NUTS.
	 * 
	 * else if a particular nuts does not exist then simply add that nuts
	 * "all_nuts_with_polygons".
	 */
	private static void hasThisNutsLeastNumberOfCoordinatesIfTrueThenAdd(JSONArray all_nuts_with_polygons,
			JSONObject a_nuts_polygon) {

		String temp_nut_id = a_nuts_polygon.get("nuts_id").toString();
		int temp_nut_polygon_points = Integer.parseInt(a_nuts_polygon.get("polygon_points").toString());

		// Does a similar old nuts has least number of points?
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
			if (existing_old_nut.get("nuts_id").toString().equals(temp_nut_id)) {

				is_tempNut_a_newly_discovered_nut = false;
				// check if new_nuts has least polygon points
				int existing_old_nut_polygon_points = Integer
						.parseInt(existing_old_nut.get("polygon_points").toString());
				System.out.println(
						"NUTS:" + temp_nut_id + " " + " " + "existing_polygon_points=" + existing_old_nut_polygon_points
								+ " " + " " + " " + "new_polygon_points=" + temp_nut_polygon_points);
				if (temp_nut_polygon_points < existing_old_nut_polygon_points) {
					old_existing_nut_has_least_resolution = false;
					nut_to_remove = existing_old_nut;
					break;
				}

			}

		}

		if (!old_existing_nut_has_least_resolution) {
			all_nuts_with_polygons.remove(nut_to_remove);
			all_nuts_with_polygons.add(a_nuts_polygon);
		}

		if (is_tempNut_a_newly_discovered_nut)
			all_nuts_with_polygons.add(a_nuts_polygon);

	}

	private static void extractNutsIdAndNutsnameFromCsvForAllNuts() throws FileNotFoundException {
		File resource_folder = source_directory_for_geojson;

		File[] listOfFolders = resource_folder.listFiles();

		/**
		 * Read "NUTS_AT_2016.csv" from the folder "1_60_million" and extract
		 * information and store it in key:value pair i.e NUTS_ID:NUTS_NAME in the
		 * hashmap nutsName.
		 */
		for (File folder : listOfFolders) {
			if (folder.getName().equals("nuts_1_60_million")) {
				for (File file : folder.listFiles()) {
					if (file.getName().equals("NUTS_AT_2016.csv")) {
						Scanner csv_scanner = new Scanner(file);
						csv_scanner.useDelimiter(",");
						while (csv_scanner.hasNext()) {
							String csv_data[] = csv_scanner.nextLine().split(",");
							if (csv_data[0].contains("DE")) {
								// If NUTS_NAME like this --> Mainz, Kreisfreie Stadt
								if (csv_data.length == 4)
									nutsId_nutsName.put(csv_data[1],
											csv_data[2].replace("\"", "") + csv_data[3].replace("\"", ""));
								else
									nutsId_nutsName.put(csv_data[1], csv_data[2]);
							}
						}
						csv_scanner.close();
					}
				}
			}
		}
	}

	public static JSONArray getCoordinatesLatLongFormat(JSONArray coordinates) {

		// This will store inner ring coordinates for one inner-ring at a time
		JSONArray coordinates_in_lat_long_format = new JSONArray();
		
		for(int i=0; i<coordinates.size();i++) {
			
			JSONArray old_child_coordinates = (JSONArray) coordinates.get(i); //Could be outer_ring or inner_ring
			JSONArray new_child_coordinates = new JSONArray();
			
			for (int count = 0; count < old_child_coordinates.size(); count++) {

				/*
				 * This array will be used to change the form of coordinates. It contains same
				 * coordinates as "points" but in different form.
				 */

				JSONArray old_coordinates = (JSONArray) old_child_coordinates.get(count);
				JSONArray new_coordinates = new JSONArray();
				new_coordinates.add(old_coordinates.toArray()[1]); // Lattitude
				new_coordinates.add(old_coordinates.toArray()[0]); // Longitude
				new_child_coordinates.add(new_coordinates);

			}
			coordinates_in_lat_long_format.add(new_child_coordinates);
		}

		return coordinates_in_lat_long_format;

	}

	public static boolean areValidPolygons(JSONArray coordinates, String geometry_type) {

		boolean are_valid_polygons = false;

		if ("multipolygon_type".equals(geometry_type)) {
			for (int array_index = 0; array_index < coordinates.size(); array_index++) {
				JSONArray child_polygon_coordinates = (JSONArray) coordinates.get(array_index);
				JSONArray outer_ring_coordinates_longlat = (JSONArray) child_polygon_coordinates.get(0);
				if (!(outer_ring_coordinates_longlat.get(0)
						.equals(outer_ring_coordinates_longlat.get(outer_ring_coordinates_longlat.size() - 1)))) {
					are_valid_polygons = false;
					break;
				}
			}
			are_valid_polygons = true;
		} else if ("polygon_type".equals(geometry_type)) {
			JSONArray outer_ring_coordinates_longlat = (JSONArray) coordinates.get(0);
			if (!(outer_ring_coordinates_longlat.get(0)
					.equals(outer_ring_coordinates_longlat.get(outer_ring_coordinates_longlat.size() - 1))))
				are_valid_polygons = false;
			else
				are_valid_polygons = true;

		}
		return are_valid_polygons;
	}

	public static void createNutPolygons() throws ClassCastException, FileNotFoundException {

		System.out.println(
				"1. Please ensure that Launuts data in the folder \"resources/launuts_geojson_and_shape_files\" folder has been extracted!!");
		System.out.println(" ");
		System.out.println(
				"2. Please make sure that we have nut_1_1_million, nut_1_3_million,....,nut_1_60_million folders inside the folder \"launuts_geojson_and_shape_files\".");
		System.out.println(" ");
		System.out.println("3. After extraction, do not forget to refresh the project by pressing Alt+F5");
		System.out.println(" ");
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		extractNutsIdAndNutsnameFromCsvForAllNuts();

		/*
		 * First start with checking polygons for level3 nut. For that we first start
		 * with the directory 1_1_million and store the polygons with least number of
		 * points. We then check other folders as well because in lower scale
		 * resolution(such as 1_1_mil) some NUTS might be represented as points which
		 * could be polygon in higher scale(such as in 1_20_mil). Repeat the same
		 * process for all other NUTS.
		 */
		for (int levl_counter = 0; levl_counter < nuts_level.length; levl_counter++) {

			for (int dir_counter = 0; dir_counter < nuts_resolutions.length; dir_counter++) {
				System.out.println(source_directory_for_geojson + "/" + nuts_resolutions[dir_counter]);
				File current_dir = new File(source_directory_for_geojson + "/" + nuts_resolutions[dir_counter]);
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
									source_directory_for_geojson + "/" + nuts_resolutions[dir_counter] + "/"
											+ current_dir.listFiles()[file_counter].getName().toString());

							JSONObject root_object = (JSONObject) json_parser.parse(geojson_reader);

							JSONArray features = (JSONArray) root_object.get("features");

							// features is an array of nuts in a GeoJson file
							Iterator<JSONObject> featuresIterator = features.iterator();

							while (featuresIterator.hasNext()) {

								JSONObject feature = featuresIterator.next();

								// We are only interested in NUTS from Germany
								if (feature.get("id").toString().contains("DE")) {

									// An object which will store information about individual nut.
									JSONObject a_nuts_polygon = new JSONObject();

									// Geometry of a feature contains coordinates
									JSONObject json_geometry = (JSONObject) feature.get("geometry");

									/*
									 * This array does not contain the actual coordinates but another array which in
									 * turn might contain 2 arrays(liner ring, holes). In most cases Coordinates
									 * array will contain only single array(outer_ring).
									 */
									JSONArray coordinates_long_lat_format = (JSONArray) json_geometry
											.get("coordinates");
									JSONArray coordinates_in_lat_long_format = new JSONArray();


									// These are inner_rings of each Nut.
									JSONArray holes = new JSONArray();

									int number_of_inner_rings = 0;
									int outer_ring_size = 0;

									/*
									 * If the geometry type is "MultiPolygon"
									 */
									if ("MultiPolygon".equalsIgnoreCase(json_geometry.get("type").toString())) {

										for (int array_index = 0; array_index < coordinates_long_lat_format
												.size(); array_index++) {

											// This array might contain 2 sub-arrays(linear ring, holes)
											JSONArray child_polygon_coordinates = (JSONArray) coordinates_long_lat_format
													.get(array_index);

											// This will store polygon's inner rings for current polygon
											JSONArray child_polygon_inner_rings = null;

											/**
											 * If sub_array size > 1 then there are inner rings. Then add the inner
											 * rings to the hole array.
											 */
											if (child_polygon_coordinates.size() > 1) {
												child_polygon_inner_rings = getInnerRings(child_polygon_coordinates);
												holes.add(child_polygon_inner_rings);
												number_of_inner_rings = number_of_inner_rings
														+ child_polygon_inner_rings.size();
											}

											JSONArray child_polygon_outer_ring = (JSONArray) child_polygon_coordinates
													.get(0);
											outer_ring_size = outer_ring_size + child_polygon_outer_ring.size();
											JSONArray child_polygon_coordinates_in_lat_long_format = getCoordinatesLatLongFormat(child_polygon_coordinates);
											coordinates_in_lat_long_format.add(child_polygon_coordinates_in_lat_long_format);

										}

										// Check for validity of polygon
										if (areValidPolygons(coordinates_long_lat_format, "polygon_type"))
											a_nuts_polygon.put("valid_polygon", "true");
										else
											a_nuts_polygon.put("valid_polygon", "false");

										a_nuts_polygon.put("nuts_name",
												nutsId_nutsName.get(feature.get("id").toString()));
										a_nuts_polygon.put("level", nuts_level[levl_counter]);

										// From which shape(LineString,MultiPolygon) polygon was extracted
										a_nuts_polygon.put("geometry_type", json_geometry.get("type").toString());
										a_nuts_polygon.put("nuts_id", feature.get("id").toString());
										a_nuts_polygon.put("File",
												current_dir.listFiles()[file_counter].getName().toString());
										a_nuts_polygon.put("coordinates", coordinates_in_lat_long_format);
										a_nuts_polygon.put("inner_rings", holes);
										a_nuts_polygon.put("number_of_inner_rings", number_of_inner_rings);
										a_nuts_polygon.put("polygon_points", outer_ring_size);
										hasThisNutsLeastNumberOfCoordinatesIfTrueThenAdd(all_nuts_with_polygons,
												a_nuts_polygon);

									}

									// If the geometry type is "Polygon"
									if ("Polygon".equalsIgnoreCase(json_geometry.get("type").toString())) {

										/**
										 * If sub_array size > 1 then there are inner rings. Then add the inner rings to
										 * the hole array.
										 */
										if (coordinates_long_lat_format.size() > 1) {
											holes = getInnerRings(coordinates_long_lat_format);
											number_of_inner_rings = number_of_inner_rings + holes.size();
										}

										JSONArray polygon_outer_ring = (JSONArray) coordinates_long_lat_format.get(0);
										outer_ring_size = outer_ring_size + polygon_outer_ring.size();
										coordinates_in_lat_long_format= getCoordinatesLatLongFormat(coordinates_long_lat_format);

										// Check for validity of polygon
										if (areValidPolygons(coordinates_long_lat_format, "polygon_type"))
											a_nuts_polygon.put("valid_polygon", "true");
										else
											a_nuts_polygon.put("valid_polygon", "false");

										a_nuts_polygon.put("nuts_name",
												nutsId_nutsName.get(feature.get("id").toString()));
										a_nuts_polygon.put("level", nuts_level[levl_counter]);

										// From which shape(LineString,MultiPolygon) polygon was extracted
										a_nuts_polygon.put("geometry_type", json_geometry.get("type").toString());
										a_nuts_polygon.put("nuts_id", feature.get("id").toString());
										a_nuts_polygon.put("File",
												current_dir.listFiles()[file_counter].getName().toString());
										a_nuts_polygon.put("coordinates", coordinates_in_lat_long_format);
										a_nuts_polygon.put("inner_rings", holes);
										a_nuts_polygon.put("number_of_inner_rings", number_of_inner_rings);
										a_nuts_polygon.put("polygon_points", outer_ring_size);
										hasThisNutsLeastNumberOfCoordinatesIfTrueThenAdd(all_nuts_with_polygons,
												a_nuts_polygon);
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

				FileWriter file = new FileWriter("NUTS_Polygons.json")) {
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

			geojson_reader = new FileReader(name_of_parser_after_final_processing);
			JSONArray nuts_array = (JSONArray) json_parser.parse(geojson_reader);

			Iterator<JSONObject> nutsIterator = nuts_array.iterator();
			while (nutsIterator.hasNext()) {
				JSONObject nuts = nutsIterator.next();
				if (nuts.get(feature_id_type).toString().equals(nutsCode)) {
					String geometry_type = nuts.get("geometry_type").toString();
					JSONArray coordinates = (JSONArray) nuts.get("coordinates");
					if ("Polygon".equalsIgnoreCase(geometry_type)) {
						multi_polygon = new org.dice_research.opal.launuts.polygons.MultiPolygon();
						for (int index = 0; index < coordinates.size(); index++) {

							JSONArray ring = (JSONArray) coordinates.get(index); // Could be an inner or outer ring
							org.dice_research.opal.launuts.polygons.Polygon ring_polygon = getDicePolygonFromCoordinates(
									ring);
							multi_polygon.polygons.add(ring_polygon);
						}
					} else {
						multi_polygon = new org.dice_research.opal.launuts.polygons.MultiPolygon(coordinates.size());
						for (int index = 0; index < coordinates.size(); index++) {
							JSONArray child_polygon = (JSONArray) coordinates.get(index); // child_polygon, may have a
																							// hole(inner_ring)
							for (int ring_index = 0; ring_index < child_polygon.size(); ring_index++) {

								JSONArray ring = (JSONArray) coordinates.get(index); // ring can be an inner_ring or
																						// outer_ring
								org.dice_research.opal.launuts.polygons.Polygon ring_polygon = getDicePolygonFromCoordinates(
										ring);
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
		source_directory_for_geojson = directory;
		return null;
	}

	public org.dice_research.opal.launuts.polygons.MultiPolygon getMultiPolygonFromHole(String nutscode,
			int hole_number) throws PolygonParserException {

		org.dice_research.opal.launuts.polygons.MultiPolygon multi_polygon_from_hole = null;
		try {
			geojson_reader = new FileReader(name_of_parser_after_final_processing);
			JSONArray nuts_array = (JSONArray) json_parser.parse(geojson_reader);

			Iterator<JSONObject> nutsIterator = nuts_array.iterator();
			while (nutsIterator.hasNext()) {
				JSONObject nuts = nutsIterator.next();
				if (nutscode.equalsIgnoreCase(nuts.get(feature_id_type).toString())) {
					JSONArray inner_rings = (JSONArray) nuts.get("inner_rings");
					JSONArray hole_coordinates = (JSONArray) inner_rings.get(hole_number - 1);
					org.dice_research.opal.launuts.polygons.Polygon dice_polygon = getDicePolygonFromCoordinates(
							hole_coordinates);
					multi_polygon_from_hole = new org.dice_research.opal.launuts.polygons.MultiPolygon();
					multi_polygon_from_hole.polygons.add(dice_polygon);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Please ensure that the hole_number is correct.");

		}
		return multi_polygon_from_hole;
	}

	public org.dice_research.opal.launuts.polygons.Polygon getDicePolygonFromCoordinates(JSONArray ring) {

		org.dice_research.opal.launuts.polygons.Polygon dice_polygon = new org.dice_research.opal.launuts.polygons.Polygon();

		for (int coordinate_index = 0; coordinate_index < ring.size(); coordinate_index++) {
			Point point = new Point();
			JSONArray lattitude_longitude = (JSONArray) ring.get(coordinate_index);
			point.latitude = Float.parseFloat(lattitude_longitude.get(0).toString());
			point.longitude = Float.parseFloat(lattitude_longitude.get(1).toString());
			dice_polygon.points.add(point);
		}
		return dice_polygon;
	}

}
