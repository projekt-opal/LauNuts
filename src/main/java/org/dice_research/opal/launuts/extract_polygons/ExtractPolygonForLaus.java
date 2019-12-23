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
import java.util.Iterator;
import java.io.File;
import java.io.FileNotFoundException;

public class ExtractPolygonForLaus {

	public static LinearRing[] return_array_of_inner_rings(GeometryFactory geometryFactory,
			JSONArray sub_array_of_coordinates, LinearRing inner_rings[]) {

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

	public static void main(String[] args) throws ClassCastException {

		JSONParser parser = new JSONParser();
		Reader reader;
		JSONArray all_laus_with_polygons = new JSONArray();

		try {
			reader = new FileReader("C:\\Users\\Gourab\\eclipse-workspace\\LauNuts\\Lau_1_1_million\\LAU_2018.json");

			JSONObject jsonObject = (JSONObject) parser.parse(reader);

			JSONArray FeatureList = (JSONArray) jsonObject.get("features");

			// features is an array of json objects.
			Iterator<JSONObject> JsonFeaturesObjectsIterator = FeatureList.iterator();

			while (JsonFeaturesObjectsIterator.hasNext()) {

				// get next features object
				JSONObject feature_object = JsonFeaturesObjectsIterator.next();

				JSONObject feature_properties = (JSONObject) feature_object.get("properties");

				// We are only interested in NUTS from Germany
				if (feature_properties.get("GISCO_ID").toString().contains("DE")) {

					// An object which will store information about individual LAU.
					JSONObject lau_with_polygon = new JSONObject();

					// To store polygon cooridnate points of each nuts.
					JSONArray PolygonCoordinates = new JSONArray();

					// get geometry object
					JSONObject json_geometry = (JSONObject) feature_object.get("geometry");

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
						// Add level of the NUTS to the current object
						// lau_with_polygon.put("Level", nuts_level[levl_counter]);
						/*
						 * Add information: From which shape(LineString,MultiPolygon) polygon was
						 * extracted
						 */
						lau_with_polygon.put("Initial_geometry_type", json_geometry.get("type").toString());

						// Add ID of the LAU to the current object
						lau_with_polygon.put("LAU_CODE", feature_properties.get("LAU_CODE").toString());
						// Add label of the LAU to the current object
						lau_with_polygon.put("LAU_LABEL", feature_properties.get("LAU_LABEL").toString());
						// Add gisco_code of the LAU to the current object
						lau_with_polygon.put("GISCO_ID", feature_properties.get("GISCO_ID").toString());

						// Create an arraylist of geometry for storing all polygons
						ArrayList<Geometry> geometry = new ArrayList<Geometry>();

						// geometryfactory object will be used to perform geometrical operations
						GeometryFactory geometryFactory = new GeometryFactory();

						for (int array_index = 0; array_index < Coordinates.size(); array_index++) {

							// This array might contain 2 sub-arrays(linear ring, holes)
							JSONArray sub_array_of_coordinates = (JSONArray) Coordinates.get(array_index);

							// points will store polygon's outer ring coordinates for current Lau
							ArrayList<Coordinate> outer_ring_points = new ArrayList<Coordinate>();

							// This will store polygon's inner ring for current polygon
							LinearRing inner_rings[] = null;

							// Linear-ring array/Outer-ring array of coordinates
							JSONArray outer_ring_coordinates = (JSONArray) sub_array_of_coordinates.get(0);

							// If sub_array size > 1 then there are inner rings
							if (sub_array_of_coordinates.size() > 1)
								inner_rings = return_array_of_inner_rings(geometryFactory, sub_array_of_coordinates,
										inner_rings);

							for (int count = 0; count < outer_ring_coordinates.size(); count++) {

								/*
								 * This array will be used to change the form of coordinates. It contains same
								 * coordinates as "points" but in different form.
								 */
								JSONArray coordinates_array = (JSONArray) outer_ring_coordinates.get(count);
								try {
									// Form changed to latitude, longitude
									outer_ring_points.add(new Coordinate((long) coordinates_array.get(1),
											(long) coordinates_array.get(0)));
								} catch (Exception e) {
									try {
										outer_ring_points.add(new Coordinate((double) coordinates_array.get(1),
												(double) coordinates_array.get(0)));
									} catch (ClassCastException X_double_Y_long) {

//															System.out.println("Solution1: "+Coordinates_array_Long_Lat.get(1).getClass());
//															System.out.println("Solution2: "+Coordinates_array_Long_Lat.get(0).getClass());
										try {
											Long y_cord = (long) coordinates_array.get(0);
											outer_ring_points.add(new Coordinate((double) coordinates_array.get(1),
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
							LinearRing outer_ring = geometryFactory
									.createLinearRing((Coordinate[]) outer_ring_points.toArray(new Coordinate[] {}));

							// Then create a polygon from linear ring, null= means no hole
							Polygon existing_polygon = geometryFactory.createPolygon(outer_ring, inner_rings);

							/*
							 * Add all new polygons into a single geometry for conversion of all polygons
							 * into a union of polygons.
							 */
							geometry.add(existing_polygon);
						}
						
					
							GeometryCollection geometryCollection = (GeometryCollection) geometryFactory
									.buildGeometry(geometry);
							MultiPolygon final_polygon = (MultiPolygon) geometryCollection.union();

							for (int index = 0; index < final_polygon.getCoordinates().length; index++) {
								// This array will store coordinates in correct format
								JSONArray Coordinates_Lat_Long = new JSONArray();
								// Lattitude
								Coordinates_Lat_Long.add(final_polygon.getCoordinates()[index].getX());
								// Longitude
								Coordinates_Lat_Long.add(final_polygon.getCoordinates()[index].getY());
								PolygonCoordinates.add(Coordinates_Lat_Long);
							}
						

						
						lau_with_polygon.put("Coordinates", PolygonCoordinates);
						lau_with_polygon.put("Polygon_Points", PolygonCoordinates.size());
						all_laus_with_polygons.add(lau_with_polygon);

					}

					// If the geometry type is "Polygon"
					if (json_geometry.get("type").toString().equals("Polygon")) {

						/*
						 * Add information: From which shape(LineString,MultiPolygon) polygon was
						 * extracted
						 */
						lau_with_polygon.put("Initial_geometry_type", json_geometry.get("type").toString());

						// Add ID of the LAU to the current object
						lau_with_polygon.put("LAU_CODE", feature_properties.get("LAU_CODE").toString());
						// Add label of the LAU to the current object
						lau_with_polygon.put("LAU_LABEL", feature_properties.get("LAU_LABEL").toString());
						// Add gisco_code of the LAU to the current object
						lau_with_polygon.put("GISCO_ID", feature_properties.get("GISCO_ID").toString());

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
							inner_rings = return_array_of_inner_rings(geometryFactory, Coordinates, inner_rings);
						}

						JSONArray ExteriorRingCoordinates = (JSONArray) Coordinates.get(0);
						Iterator<JSONArray> LongLatIter = ExteriorRingCoordinates.iterator();
						while (LongLatIter.hasNext()) {

							// this array has cooridnates in long,lat format
							JSONArray Coordinates_array_Long_Lat = LongLatIter.next();

							try {
								// Form changed to latitude, longitude
								outer_ring_points.add(new Coordinate((long) Coordinates_array_Long_Lat.get(1),
										(long) Coordinates_array_Long_Lat.get(0)));
							} catch (Exception e) {
								try {
									outer_ring_points.add(new Coordinate((double) Coordinates_array_Long_Lat.get(1),
											(double) Coordinates_array_Long_Lat.get(0)));
								} catch (ClassCastException X_double_Y_long) {

//														System.out.println("Solution1: "+Coordinates_array_Long_Lat.get(1).getClass());
//														System.out.println("Solution2: "+Coordinates_array_Long_Lat.get(0).getClass());
									try {
										Long y_cord = (long) Coordinates_array_Long_Lat.get(0);
										outer_ring_points.add(new Coordinate((double) Coordinates_array_Long_Lat.get(1),
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
						LinearRing outer_ring = geometryFactory
								.createLinearRing((Coordinate[]) outer_ring_points.toArray(new Coordinate[] {}));

						// Then create a polygon from linear ring, null= means no hole
						Polygon existing_polygon = geometryFactory.createPolygon(outer_ring, inner_rings);

						for (int index = 0; index < existing_polygon.getCoordinates().length; index++) {
							// This array will store coordinates in correct format
							JSONArray Coordinates_Lat_Long = new JSONArray();
							// Lattitude
							Coordinates_Lat_Long.add(existing_polygon.getCoordinates()[index].getX());
							// Longitude
							Coordinates_Lat_Long.add(existing_polygon.getCoordinates()[index].getY());
							PolygonCoordinates.add(Coordinates_Lat_Long);
						}

						lau_with_polygon.put("Coordinates", PolygonCoordinates);
						lau_with_polygon.put("Polygon_Points", PolygonCoordinates.size());
						all_laus_with_polygons.add(lau_with_polygon);
					}

				}
			}

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Write JSON file
		try (FileWriter file = new FileWriter("C:\\Users\\Gourab\\eclipse-workspace\\LauNuts\\LAUs_Polygons.json")) {
			if (!(all_laus_with_polygons.isEmpty())) {
				file.write(all_laus_with_polygons.toJSONString());
				file.flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
