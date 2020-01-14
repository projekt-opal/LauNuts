package org.dice_research.opal.launuts.extract_polygons;

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
import org.json.simple.JSONArray;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class ExtractPolygonForNuts {

	/*
	 * Check if a particular NUTS exists in "all_nuts_with_polygons". If exists,
	 * then check if the current NUTS has least polygon points and if yes replace
	 * the existing same-ID NUTS with current NUTS.
	 * 
	 * else if a particular nuts does not exist then simply add that nuts
	 * "all_nuts_with_polygons".
	 */
	public static void check_and_add_nuts_with_polygons(JSONArray all_nuts_with_polygons,
			JSONObject individual_level_NutsWithPolygon) {

		String new_nuts_id = individual_level_NutsWithPolygon.get("NUTS_ID").toString();
		int new_nuts_polygon_points = Integer
				.parseInt(individual_level_NutsWithPolygon.get("Polygon_Points").toString());

		// is this nuts a new one? or does this nuts has least polygon points?
		boolean existing_nuts_has_least_resolution = true;
		boolean completely_new_nuts = true;
		/*
		 * If new_nuts_has_least_resolution then remove the existing nuts from
		 * all_nuts_with_polygons.
		 */

		JSONObject nuts_to_remove = null;

		Iterator<JSONObject> ObjectsIterator = all_nuts_with_polygons.iterator();
		while (ObjectsIterator.hasNext()) {

			JSONObject existing_object = ObjectsIterator.next();
			if (existing_object.get("NUTS_ID").toString().equals(new_nuts_id)) {

				completely_new_nuts = false;
				// check if new_nuts has least polygon points
				int existing_polygon_points = Integer.parseInt(existing_object.get("Polygon_Points").toString());
				System.out.println("NUTS:" + new_nuts_id + " " + " " + "existing_polygon_points="
						+ existing_polygon_points + " " + " " + " " + "new_poly_points=" + new_nuts_polygon_points);
				if (new_nuts_polygon_points < existing_polygon_points) {
					existing_nuts_has_least_resolution = false;
					nuts_to_remove = existing_object;
					break;
				}

			}

		}

		if (existing_nuts_has_least_resolution)
			;
		else {
			all_nuts_with_polygons.remove(nuts_to_remove);
			all_nuts_with_polygons.add(individual_level_NutsWithPolygon);
		}

		if (completely_new_nuts)
			all_nuts_with_polygons.add(individual_level_NutsWithPolygon);

	}

	public static LinearRing[] return_array_of_inner_rings(GeometryFactory geometryFactory,
			JSONArray sub_array_of_coordinates, LinearRing inner_rings[], String nuts_id, String file) {

		inner_rings = new LinearRing[sub_array_of_coordinates.size() - 1];

		// first array is always outer ring !!
		// The rest are inner rings
		for (int counter = 1; counter < sub_array_of_coordinates.size(); counter++) {

			// This will store inner ring coordinates for one inner-ring at a time
			ArrayList<Coordinate> inner_ring_points = new ArrayList<Coordinate>();

			JSONArray inner_ring_coordinates = (JSONArray) sub_array_of_coordinates.get(counter);

			for (int count = 0; count < inner_ring_coordinates.size(); count++) {

				/*
				 * This array will be used to change the form of coordinates. It contains same
				 * coordinates as "points" but in different form.
				 */
				JSONArray coordinates_array = (JSONArray) inner_ring_coordinates.get(count);

				try {
					// Form changed to latitude, longitude
					inner_ring_points
							.add(new Coordinate((long) coordinates_array.get(1), (long) coordinates_array.get(0)));
				} catch (Exception e) {
					try {
						inner_ring_points.add(
								new Coordinate((double) coordinates_array.get(1), (double) coordinates_array.get(0)));
					} catch (ClassCastException X_double_Y_long) {

//							System.out.println("Solution1: "+Coordinates_array_Long_Lat.get(1).getClass());
//							System.out.println("Solution2: "+Coordinates_array_Long_Lat.get(0).getClass());
						try {
							Long y_cord = (long) coordinates_array.get(0);
							inner_ring_points
									.add(new Coordinate((double) coordinates_array.get(1), y_cord.doubleValue()));
						} catch (ClassCastException X_long_Y_double) {
							Long x_cord = (long) coordinates_array.get(1);
							inner_ring_points
									.add(new Coordinate(x_cord.doubleValue(), (double) coordinates_array.get(0)));
						}
					}
				}

			}

			// Create a inner-ring using the inner_ring_coordinates array
			LinearRing inner_ring = geometryFactory
					.createLinearRing((Coordinate[]) inner_ring_points.toArray(new Coordinate[] {}));
			// Finally add all inner_rings to one array of LinearRing.

			inner_rings[counter - 1] = inner_ring;
		}

		return inner_rings;
	}

	public static void main(String[] args) throws ClassCastException, FileNotFoundException {

		/*
		 * First extract nuts-name for all nuts ID from provided csv file
		 * "NUTS_AT_2016.csv"
		 */
		HashMap<String, String> nutsName = new HashMap<String, String>();
		Scanner csv_scanner = new Scanner(
				new File("C:\\Users\\Gourab\\eclipse-workspace\\LauNuts\\1_60_million\\NUTS_AT_2016.csv"));
		csv_scanner.useDelimiter(",");
		while (csv_scanner.hasNext()) {
			String csv_data[] = csv_scanner.nextLine().split(",");
			if (csv_data[1].contains("DE")) {
				// If NUTS_NAME like this --> Mainz, Kreisfreie Stadt
				if (csv_data.length == 4)
					nutsName.put(csv_data[1], csv_data[2].replace("\"", "") + csv_data[3].replace("\"", ""));
				else
					nutsName.put(csv_data[1], csv_data[2]);
			}
		}
		csv_scanner.close();

		// Directories to check
		String directories[] = { "1_1_million", "1_3_million", "1_10_million", "1_20_million", "1_60_million" };
		// String directories[] = { "1_1_million"};
		// Which NUTS Level to check?
		String nuts_level[] = { "LEVL_3", "LEVL_2", "LEVL_1", "LEVL_0" };

		JSONParser parser = new JSONParser();
		Reader reader;
		JSONArray all_nuts_with_polygons = new JSONArray();

		/*
		 * First start with checking polygon points for NUTS3. For that we first check
		 * the directory 1_1_million and note down the polygon with least points. We
		 * then check other folders as well because in lower scale resolution(such as
		 * 1_1_mil) some NUTS might be represented as points which could be polygon in
		 * higher scale(such as 1_20_mil). Repeat the same process for all other NUTS.
		 */
		for (int levl_counter = 0; levl_counter < nuts_level.length; levl_counter++) {

			for (int dir_counter = 0; dir_counter < directories.length; dir_counter++) {

				File current_dir = new File(
						"C:\\Users\\Gourab\\eclipse-workspace\\LauNuts\\" + directories[dir_counter]);
				for (int file_counter = 0; file_counter < current_dir.listFiles().length; file_counter++) {

					if ((current_dir.listFiles()[file_counter].getName().contains("RG")
							&& current_dir.listFiles()[file_counter].getName().contains(nuts_level[levl_counter]))
							|| (current_dir.listFiles()[file_counter].getName().contains("LB")
									&& current_dir.listFiles()[file_counter].getName()
											.contains(nuts_level[levl_counter]))) {

						System.out.println(current_dir.listFiles()[file_counter].getName().toString() + ":checked");

						try {
							reader = new FileReader(
									"C:\\Users\\Gourab\\eclipse-workspace\\LauNuts\\" + directories[dir_counter] + "\\"
											+ current_dir.listFiles()[file_counter].getName().toString());

							JSONObject jsonObject = (JSONObject) parser.parse(reader);

							JSONArray FeatureList = (JSONArray) jsonObject.get("features");

							// features is an array of json objects.
							Iterator<JSONObject> JsonFeaturesObjectsIterator = FeatureList.iterator();

							while (JsonFeaturesObjectsIterator.hasNext()) {

								// get next features object
								JSONObject FeatureObject = JsonFeaturesObjectsIterator.next();

								// We are only interested in NUTS from Germany
								if (FeatureObject.get("id").toString().contains("DE")) {

									// An object which will store information about individual nuts.
									JSONObject individual_level_NutsWithPolygon = new JSONObject();

									// To store polygon cooridnate points of each nuts.
									JSONArray PolygonCoordinates = new JSONArray();

									// get geometry object
									JSONObject json_geometry = (JSONObject) FeatureObject.get("geometry");

									/*
									 * This array does not contain the actual coordinates but another array which in
									 * turn might contain 2 arrays(liner ring, holes). In most cases Coordinates
									 * array will contain only single array(linear ring).
									 */
									JSONArray Coordinates = (JSONArray) json_geometry.get("coordinates");

									/*
									 * If the geometry type is "MultiPolygon"
									 */
									if (json_geometry.get("type").toString().equals("MultiPolygon")) {

										// Add name of the nuts
										individual_level_NutsWithPolygon.put("NUTS_NAME",
												nutsName.get(FeatureObject.get("id").toString()));

										// Add level of the NUTS to the current object
										individual_level_NutsWithPolygon.put("Level", nuts_level[levl_counter]);
										/*
										 * Add information: From which shape(LineString,MultiPolygon) polygon was
										 * extracted
										 */
										individual_level_NutsWithPolygon.put("Initial_geometry_type",
												json_geometry.get("type").toString());

										// Add ID of the NUTS to the current object
										individual_level_NutsWithPolygon.put("NUTS_ID",
												FeatureObject.get("id").toString());

										// Create an arraylist of geometry for storing all polygons
										ArrayList<Geometry> geometry = new ArrayList<Geometry>();

										// geometryfactory object will be used to perform geometrical operations
										GeometryFactory geometryFactory = new GeometryFactory();

										for (int array_index = 0; array_index < Coordinates.size(); array_index++) {

											// This array might contain 2 sub-arrays(linear ring, holes)
											JSONArray sub_array_of_coordinates = (JSONArray) Coordinates
													.get(array_index);

											// points will store polygon's outer ring coordinates for current NUTS
											ArrayList<Coordinate> outer_ring_points = new ArrayList<Coordinate>();

											// This will store polygon's inner ring for current polygon
											LinearRing inner_rings[] = null;

											// Linear-ring array/Outer-ring array of coordinates
											JSONArray outer_ring_coordinates = (JSONArray) sub_array_of_coordinates
													.get(0);

											// If sub_array size > 1 then there are inner rings
											if (sub_array_of_coordinates.size() > 1)
												inner_rings = return_array_of_inner_rings(geometryFactory,
														sub_array_of_coordinates, inner_rings,
														FeatureObject.get("id").toString(),
														current_dir.listFiles()[file_counter].getName().toString());

											for (int count = 0; count < outer_ring_coordinates.size(); count++) {

												/*
												 * This array will be used to change the form of coordinates. It
												 * contains same coordinates as "points" but in different form.
												 */
												JSONArray coordinates_array = (JSONArray) outer_ring_coordinates
														.get(count);
												try {
													// Form changed to latitude, longitude
													outer_ring_points
															.add(new Coordinate((long) coordinates_array.get(1),
																	(long) coordinates_array.get(0)));
												} catch (Exception e) {
													try {
														outer_ring_points
																.add(new Coordinate((double) coordinates_array.get(1),
																		(double) coordinates_array.get(0)));
													} catch (ClassCastException X_double_Y_long) {

//															System.out.println("Solution1: "+Coordinates_array_Long_Lat.get(1).getClass());
//															System.out.println("Solution2: "+Coordinates_array_Long_Lat.get(0).getClass());
														try {
															Long y_cord = (long) coordinates_array.get(0);
															outer_ring_points.add(
																	new Coordinate((double) coordinates_array.get(1),
																			y_cord.doubleValue()));
														} catch (ClassCastException X_long_Y_double) {
															Long x_cord = (long) coordinates_array.get(1);
															outer_ring_points.add(new Coordinate(x_cord.doubleValue(),
																	(double) coordinates_array.get(0)));
														}
													}
												}
											}
											// Create a linear-ring/outer-ring using the points coordinates array
											LinearRing outer_ring = geometryFactory.createLinearRing(
													(Coordinate[]) outer_ring_points.toArray(new Coordinate[] {}));

											// Then create a polygon from linear ring, null= means no hole
											Polygon existing_polygon = geometryFactory.createPolygon(outer_ring,
													inner_rings);

											/*
											 * Add all new polygons into a single geometry for conversion of all
											 * polygons into a union of polygons.
											 */
											geometry.add(existing_polygon);
											try {
												GeometryCollection geometryCollection = (GeometryCollection) geometryFactory
														.buildGeometry(geometry);
												MultiPolygon final_polygon = (MultiPolygon) geometryCollection.union();

												for (int index = 0; index < final_polygon
														.getCoordinates().length; index++) {
													// This array will store coordinates in correct format
													JSONArray Coordinates_Lat_Long = new JSONArray();
													// Lattitude
													Coordinates_Lat_Long
															.add(final_polygon.getCoordinates()[index].getX());
													// Longitude
													Coordinates_Lat_Long
															.add(final_polygon.getCoordinates()[index].getY());
													PolygonCoordinates.add(Coordinates_Lat_Long);
												}
											} catch (Exception polygon_to_geometrycollection) {

												Geometry geometryCollection = geometryFactory.buildGeometry(geometry);
												// MultiPolygon final_polygon = (MultiPolygon)
												// geometryCollection.union();

												for (int index = 0; index < geometryCollection
														.getCoordinates().length; index++) {
													// This array will store coordinates in correct format
													JSONArray Coordinates_Lat_Long = new JSONArray();
													// Lattitude
													Coordinates_Lat_Long
															.add(geometryCollection.getCoordinates()[index].getX());
													// Longitude
													Coordinates_Lat_Long
															.add(geometryCollection.getCoordinates()[index].getY());
													PolygonCoordinates.add(Coordinates_Lat_Long);
												}

											}

										}
										// Check for validity of polygon
										if (PolygonCoordinates.get(0)
												.equals(PolygonCoordinates.get(PolygonCoordinates.size() - 1)))
											individual_level_NutsWithPolygon.put("Valid_Polygon", "true");
										else
											individual_level_NutsWithPolygon.put("Valid_Polygon", "false");

										individual_level_NutsWithPolygon.put("File",
												current_dir.listFiles()[file_counter].getName().toString());
										individual_level_NutsWithPolygon.put("Coordinates", PolygonCoordinates);
										individual_level_NutsWithPolygon.put("Polygon_Points",
												PolygonCoordinates.size());
										check_and_add_nuts_with_polygons(all_nuts_with_polygons,
												individual_level_NutsWithPolygon);

									}

									// If the geometry type is "Polygon"
									if (json_geometry.get("type").toString().equals("Polygon")) {

										// Add name of the nuts
										individual_level_NutsWithPolygon.put("NUTS_NAME",
												nutsName.get(FeatureObject.get("id").toString()));

										// Add level of the NUTS to the current object
										individual_level_NutsWithPolygon.put("Level", nuts_level[levl_counter]);
										/*
										 * Add information: From which shape(LineString,MultiPolygon) polygon was
										 * extracted
										 */
										individual_level_NutsWithPolygon.put("Initial_geometry_type",
												json_geometry.get("type").toString());

										// Add ID of the NUTS to the current object
										individual_level_NutsWithPolygon.put("NUTS_ID",
												FeatureObject.get("id").toString());

										// points will store polygon's outer ring coordinates for current polygon
										ArrayList<Coordinate> outer_ring_points = new ArrayList<Coordinate>();

										// This will store polygon's inner ring for current polygon
										LinearRing inner_rings[] = null;

										// geometryfactory object will be used to perform geometrical operations
										GeometryFactory geometryFactory = new GeometryFactory();

										/*
										 * If array size is 2 then the 1st array is exterior-ring coordinates and the
										 * 2nd array is interior-ring(hole) coordinates. Else Coordinates has no holes.
										 */
										if (Coordinates.size() > 1) {
											inner_rings = return_array_of_inner_rings(geometryFactory, Coordinates,
													inner_rings, FeatureObject.get("id").toString(),
													current_dir.listFiles()[file_counter].getName().toString());
										}

										JSONArray ExteriorRingCoordinates = (JSONArray) Coordinates.get(0);
										Iterator<JSONArray> LongLatIter = ExteriorRingCoordinates.iterator();
										while (LongLatIter.hasNext()) {

											// this array has cooridnates in long,lat format
											JSONArray Coordinates_array_Long_Lat = LongLatIter.next();

											try {
												// Form changed to latitude, longitude
												outer_ring_points
														.add(new Coordinate((long) Coordinates_array_Long_Lat.get(1),
																(long) Coordinates_array_Long_Lat.get(0)));
											} catch (Exception e) {
												try {
													outer_ring_points.add(
															new Coordinate((double) Coordinates_array_Long_Lat.get(1),
																	(double) Coordinates_array_Long_Lat.get(0)));
												} catch (ClassCastException X_double_Y_long) {

//														System.out.println("Solution1: "+Coordinates_array_Long_Lat.get(1).getClass());
//														System.out.println("Solution2: "+Coordinates_array_Long_Lat.get(0).getClass());
													try {
														Long y_cord = (long) Coordinates_array_Long_Lat.get(0);
														outer_ring_points.add(new Coordinate(
																(double) Coordinates_array_Long_Lat.get(1),
																y_cord.doubleValue()));
													} catch (ClassCastException X_long_Y_double) {
														Long x_cord = (long) Coordinates_array_Long_Lat.get(1);
														outer_ring_points.add(new Coordinate(x_cord.doubleValue(),
																(double) Coordinates_array_Long_Lat.get(0)));
													}
												}
											}

										}

										// Create a linear-ring/outer-ring using the points coordinates array
										LinearRing outer_ring = geometryFactory.createLinearRing(
												(Coordinate[]) outer_ring_points.toArray(new Coordinate[] {}));

										// Then create a polygon from linear ring, null= means no hole
										Polygon existing_polygon = geometryFactory.createPolygon(outer_ring,
												inner_rings);

										for (int index = 0; index < existing_polygon.getCoordinates().length; index++) {
											// This array will store coordinates in correct format
											JSONArray Coordinates_Lat_Long = new JSONArray();
											// Lattitude
											Coordinates_Lat_Long.add(existing_polygon.getCoordinates()[index].getX());
											// Longitude
											Coordinates_Lat_Long.add(existing_polygon.getCoordinates()[index].getY());
											PolygonCoordinates.add(Coordinates_Lat_Long);
										}

										// Check for validity of polygon
										if (PolygonCoordinates.get(0)
												.equals(PolygonCoordinates.get(PolygonCoordinates.size() - 1)))
											individual_level_NutsWithPolygon.put("Valid_Polygon", "true");
										else
											individual_level_NutsWithPolygon.put("Valid_Polygon", "false");

										individual_level_NutsWithPolygon.put("File",
												current_dir.listFiles()[file_counter].getName().toString());
										individual_level_NutsWithPolygon.put("Coordinates", PolygonCoordinates);
										individual_level_NutsWithPolygon.put("Polygon_Points",
												PolygonCoordinates.size());
										check_and_add_nuts_with_polygons(all_nuts_with_polygons,
												individual_level_NutsWithPolygon);
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
		try (FileWriter file = new FileWriter("C:\\Users\\Gourab\\eclipse-workspace\\LauNuts\\NUTS_Polygons.json")) {
			if (!(all_nuts_with_polygons.isEmpty())) {
				file.write(all_nuts_with_polygons.toJSONString());
				file.flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
