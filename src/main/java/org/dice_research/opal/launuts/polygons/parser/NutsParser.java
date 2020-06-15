package org.dice_research.opal.launuts.polygons.parser;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.json.simple.JSONObject;
import org.dice_research.opal.launuts.lau.LauReaderInterface;
import org.dice_research.opal.launuts.polygons.Point;
import org.dice_research.opal.launuts.polygons.Polygon;
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

/**
 * The NutParse reads GeoJson files from Eurostat and extract polygons for 455
 * nuts of Germany. The GeoJson files from Eurostat are in different resolution
 * e.g. from 1:1 million to 1:60 million. We are considering only those polygons
 * which have less number of coordinates in its outer ring. To do this we are
 * using a library from GeoTools. The parsed polygons are stored in a file
 * called NUTS_Polygons.json in the root folder of this application.
 * 
 * The source/input GeoJson files (from Eurostat) have been put in 4 zip files
 * in the folder "launuts_geojson_and_shape_files". Before this program is run
 * all the zip files with nuts must be extracted.
 * 
 * @author Gourab Sahu
 */

public class NutsParser implements PolygonParserInterface {

	private static String nutsResolutions[] = { "nuts_1_1_million", "nuts_1_3_million", "nuts_1_10_million",
			"nuts_1_20_million", "nuts_1_60_million" };

	private static File sourceDirectoryForGeojson = new File(
			new NutsParser().getClass().getClassLoader().getResource("launuts_geojson_and_shape_files").getFile());

	private String nutsLevel[] = { "LEVL_3", "LEVL_2", "LEVL_1", "LEVL_0" };

	private JSONParser jsonParser = new JSONParser();
	private Reader geojsonReader;
	private JSONArray allNutsWithPolygons = new JSONArray();
	protected String nameOfParserAfterFinalProcessing;
	protected String featureIdType;

	public NutsParser() {
		this.nameOfParserAfterFinalProcessing = "NUTS_Polygons.json";
		this.featureIdType = "nuts_id";
	}

	// Nuts-id and Nuts-name for all nuts.
	private static HashMap<String, String> nutsIdNutsName = new HashMap<String, String>();

	public JSONArray getInnerRings(JSONArray childPolygonCoordinatesArrays) {

		JSONArray innerRings = new JSONArray();

		// First array is always outer ring !!
		// The rest are inner rings
		for (int counter = 1; counter < childPolygonCoordinatesArrays.size(); counter++) {

			// This will store inner ring coordinates for one inner-ring at a time
			JSONArray newInnerRing = new JSONArray();

			JSONArray oldInnerRing = (JSONArray) childPolygonCoordinatesArrays.get(counter);

			for (int count = 0; count < oldInnerRing.size(); count++) {

				/*
				 * This array will be used to change the form of coordinates. It contains same
				 * coordinates as "points" but in different form.
				 */
				JSONArray old_coordinates = (JSONArray) oldInnerRing.get(count);
				JSONArray new_coordinates = new JSONArray();
				new_coordinates.add(old_coordinates.toArray()[1]); // Lattitude
				new_coordinates.add(old_coordinates.toArray()[0]); // Longitude
				newInnerRing.add(new_coordinates);

			}

			// Finally add all inner_rings or holes to one array of LinearRing.
			innerRings.add(newInnerRing);
		}

		return innerRings;
	}

	/**
	 * Check if a particular NUTS exists in "all_nuts_with_polygons". If yes, then
	 * check if the current NUTS has least polygon points and if yes again then
	 * replace the existing same-ID NUTS with current NUTS.
	 * 
	 * else if a particular nuts does not exist then simply add that nuts
	 * "all_nuts_with_polygons".
	 */
	private void hasThisNutsLeastNumberOfCoordinatesIfTrueThenAdd(JSONArray allNutsWithPolygons,
			JSONObject aNutsPolygon) {

		String tempNutId = aNutsPolygon.get("nuts_id").toString();
		int tempNutPolygonPoints = Integer.parseInt(aNutsPolygon.get("polygon_points").toString());

		// Does a similar old nuts has least number of points?
		boolean oldExistingNutHasLeastResolution = true;

		// This nut has not been processed before
		boolean isTempNutNewlyDiscoveredNut = true;
		/*
		 * If new_nuts_has_least_resolution then remove the existing nuts from
		 * all_nuts_with_polygons.
		 */

		JSONObject nutsToRemove = null;

		Iterator<JSONObject> nutIterator = allNutsWithPolygons.iterator();
		while (nutIterator.hasNext()) {

			JSONObject existing_old_nut = nutIterator.next();
			if (existing_old_nut.get("nuts_id").toString().equals(tempNutId)) {

				isTempNutNewlyDiscoveredNut = false;
				// check if new_nuts has least polygon points
				int existing_old_nut_polygon_points = Integer
						.parseInt(existing_old_nut.get("polygon_points").toString());
				System.out.println(
						"NUTS:" + tempNutId + " " + " " + "existing_polygon_points=" + existing_old_nut_polygon_points
								+ " " + " " + " " + "new_polygon_points=" + tempNutPolygonPoints);
				if (tempNutPolygonPoints < existing_old_nut_polygon_points) {
					oldExistingNutHasLeastResolution = false;
					nutsToRemove = existing_old_nut;
					break;
				}

			}

		}

		if (!oldExistingNutHasLeastResolution) {
			allNutsWithPolygons.remove(nutsToRemove);
			allNutsWithPolygons.add(aNutsPolygon);
		}

		if (isTempNutNewlyDiscoveredNut)
			allNutsWithPolygons.add(aNutsPolygon);

	}

	private void extractNutsIdAndNutsnameFromCsvForAllNuts() throws FileNotFoundException {
		File resourceFolder = sourceDirectoryForGeojson;

		File[] listOfFolders = resourceFolder.listFiles();

		/*
		 * Read "NUTS_AT_2016.csv" from the folder "1_60_million" and extract
		 * information and store it in key:value pair i.e NUTS_ID:NUTS_NAME in the
		 * hashmap nutsName.
		 */
		for (File folder : listOfFolders) {
			if (folder.getName().equals("nuts_1_60_million")) {
				for (File file : folder.listFiles()) {
					if (file.getName().equals("NUTS_AT_2016.csv")) {
						Scanner csvScanner = new Scanner(file);
						csvScanner.useDelimiter(",");
						while (csvScanner.hasNext()) {
							String csvData[] = csvScanner.nextLine().split(",");
							if (csvData[0].contains("DE")) {
								// If NUTS_NAME like this --> Mainz, Kreisfreie Stadt
								if (csvData.length == 4)
									nutsIdNutsName.put(csvData[1],
											csvData[2].replace("\"", "") + csvData[3].replace("\"", ""));
								else
									nutsIdNutsName.put(csvData[1], csvData[2]);
							}
						}
						csvScanner.close();
					}
				}
			}
		}
	}

	/**
	 * This method takes coordinates which are in the form of longitude, latitude
	 * and convert them to latitude, longitude format.
	 * 
	 * @param coordinates
	 * @return {@link JSONArray}
	 */
	public static JSONArray getCoordinatesLatLongFormat(JSONArray coordinates) {

		// This will store inner ring coordinates for one inner-ring at a time
		JSONArray coordinatesInLatLongFormat = new JSONArray();

		for (int i = 0; i < coordinates.size(); i++) {

			JSONArray oldChildCoordinates = (JSONArray) coordinates.get(i); // Could be outer_ring or inner_ring
			JSONArray newChildCoordinates = new JSONArray();

			for (int count = 0; count < oldChildCoordinates.size(); count++) {

				/*
				 * This array will be used to change the form of coordinates. It contains same
				 * coordinates as "points" but in different form.
				 */

				JSONArray oldCoordinates = (JSONArray) oldChildCoordinates.get(count);
				JSONArray newCoordinates = new JSONArray();
				if (Double.parseDouble(oldCoordinates.toArray()[1].toString()) > Double
						.parseDouble(oldCoordinates.toArray()[0].toString())) {
					newCoordinates.add(oldCoordinates.toArray()[1]); // Lattitude
					newCoordinates.add(oldCoordinates.toArray()[0]); // Longitude
					newChildCoordinates.add(newCoordinates);
				}

			}
			coordinatesInLatLongFormat.add(newChildCoordinates);
		}

		return coordinatesInLatLongFormat;

	}

	/**
	 * This method checks whether a polygon or a multipolygon is valid or not. The
	 * 2nd parameter should be either "polygon_type" or "multipolygon_type".
	 * 
	 * @param coordinates
	 * @param geometry_type
	 * @return boolean
	 */
	public boolean areValidPolygons(JSONArray coordinates, String geometry_type) {

		boolean areValidPolygons = false;

		if ("multipolygon_type".equalsIgnoreCase(geometry_type)) {
			for (int arrayIndex = 0; arrayIndex < coordinates.size(); arrayIndex++) {
				JSONArray childPolygonCoordinates = (JSONArray) coordinates.get(arrayIndex);
				JSONArray outerRingCoordinatesLongLat = (JSONArray) childPolygonCoordinates.get(0);
				if (!(outerRingCoordinatesLongLat.get(0)
						.equals(outerRingCoordinatesLongLat.get(outerRingCoordinatesLongLat.size() - 1)))) {
					areValidPolygons = false;
					break;
				}
			}
			areValidPolygons = true;
		} else if ("polygon_type".equalsIgnoreCase(geometry_type)) {
			JSONArray outerRingCoordinatesLonglat = (JSONArray) coordinates.get(0);
			if (!(outerRingCoordinatesLonglat.get(0)
					.equals(outerRingCoordinatesLonglat.get(outerRingCoordinatesLonglat.size() - 1))))
				areValidPolygons = false;
			else
				areValidPolygons = true;

		}
		return areValidPolygons;
	}

	public void createNutPolygons() throws ClassCastException, FileNotFoundException {

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
		for (int levlCounter = 0; levlCounter < nutsLevel.length; levlCounter++) {

			for (int dirCounter = 0; dirCounter < nutsResolutions.length; dirCounter++) {
				System.out.println(sourceDirectoryForGeojson + "/" + nutsResolutions[dirCounter]);
				File currentDir = new File(sourceDirectoryForGeojson + "/" + nutsResolutions[dirCounter]);
				for (int fileCounter = 0; fileCounter < currentDir.listFiles().length; fileCounter++) {
					String geojsonFile = currentDir.listFiles()[fileCounter].getName();

					// Files labelled with 4326 contains coordinates in decimal degree.
					if ((geojsonFile.contains("RG") && geojsonFile.contains("4326")
							&& geojsonFile.contains(nutsLevel[levlCounter]))
							|| (geojsonFile.contains("LB") && geojsonFile.contains("4326")
									&& geojsonFile.contains(nutsLevel[levlCounter]))) {

						System.out.println(currentDir.listFiles()[fileCounter].getName().toString() + ":checked");

						try {
							geojsonReader = new FileReader(sourceDirectoryForGeojson + "/" + nutsResolutions[dirCounter]
									+ "/" + currentDir.listFiles()[fileCounter].getName().toString());

							JSONObject rootObject = (JSONObject) jsonParser.parse(geojsonReader);

							JSONArray features = (JSONArray) rootObject.get("features");

							// features is an array of nuts in a GeoJson file
							Iterator<JSONObject> featuresIterator = features.iterator();

							while (featuresIterator.hasNext()) {

								JSONObject feature = featuresIterator.next();

								// We are only interested in NUTS from Germany
								if (feature.get("id").toString().contains("DE")) {

									// An object which will store information about individual nut.
									JSONObject nutsPolygon = new JSONObject();

									// Geometry of a feature contains coordinates
									JSONObject jsonGeometry = (JSONObject) feature.get("geometry");

									/*
									 * This array does not contain the actual coordinates but another array which in
									 * turn might contain 2 arrays(liner ring, holes). In most cases Coordinates
									 * array will contain only single array(outer_ring).
									 */
									JSONArray coordinatesLongLatFormat = (JSONArray) jsonGeometry.get("coordinates");
									JSONArray coordinatesLatLongFormat = new JSONArray();

									// These are inner_rings of each Nut.
									JSONArray holes = new JSONArray();

									int numberOfInnerRings = 0;
									int outerRingSize = 0;

									/*
									 * If the geometry type is "MultiPolygon"
									 */
									if ("MultiPolygon".equalsIgnoreCase(jsonGeometry.get("type").toString())) {

										for (int arrayIndex = 0; arrayIndex < coordinatesLongLatFormat
												.size(); arrayIndex++) {

											// This array might contain 2 sub-arrays(linear ring, holes)
											JSONArray childPolygonCoordinates = (JSONArray) coordinatesLongLatFormat
													.get(arrayIndex);

											// This will store polygon's inner rings for current polygon
											JSONArray childPolygonInnerRings = null;

											/**
											 * If sub_array size > 1 then there are inner rings. Then add the inner
											 * rings to the hole array.
											 */
											if (childPolygonCoordinates.size() > 1) {
												childPolygonInnerRings = getInnerRings(childPolygonCoordinates);
												holes.add(childPolygonInnerRings);
												numberOfInnerRings = numberOfInnerRings + childPolygonInnerRings.size();
											}

											JSONArray childPolygonOuterRing = (JSONArray) childPolygonCoordinates
													.get(0);
											outerRingSize = outerRingSize + childPolygonOuterRing.size();
											JSONArray childPolygonCoordinatesLatLongFormat = getCoordinatesLatLongFormat(
													childPolygonCoordinates);
											coordinatesLatLongFormat.add(childPolygonCoordinatesLatLongFormat);

										}

										// Check for validity of polygon
										if (areValidPolygons(coordinatesLongLatFormat, "multipolygon_type"))
											nutsPolygon.put("valid_polygon", "true");
										else
											nutsPolygon.put("valid_polygon", "false");

										nutsPolygon.put("nuts_name", nutsIdNutsName.get(feature.get("id").toString()));
										nutsPolygon.put("level", nutsLevel[levlCounter]);

										// From which shape(LineString,MultiPolygon) polygon was extracted
										nutsPolygon.put("geometry_type", jsonGeometry.get("type").toString());
										nutsPolygon.put("nuts_id", feature.get("id").toString());
										nutsPolygon.put("File",
												currentDir.listFiles()[fileCounter].getName().toString());
										nutsPolygon.put("coordinates", coordinatesLatLongFormat);
										nutsPolygon.put("inner_rings", holes);
										nutsPolygon.put("number_of_inner_rings", numberOfInnerRings);
										nutsPolygon.put("polygon_points", outerRingSize);
										hasThisNutsLeastNumberOfCoordinatesIfTrueThenAdd(allNutsWithPolygons,
												nutsPolygon);

									}

									// If the geometry type is "Polygon"
									if ("Polygon".equalsIgnoreCase(jsonGeometry.get("type").toString())) {

										/**
										 * If sub_array size > 1 then there are inner rings. Then add the inner rings to
										 * the hole array.
										 */
										if (coordinatesLongLatFormat.size() > 1) {
											holes = getInnerRings(coordinatesLongLatFormat);
											numberOfInnerRings = numberOfInnerRings + holes.size();
										}

										JSONArray polygonOuterRing = (JSONArray) coordinatesLongLatFormat.get(0);
										outerRingSize = outerRingSize + polygonOuterRing.size();
										coordinatesLatLongFormat = getCoordinatesLatLongFormat(
												coordinatesLongLatFormat);

										// Check for validity of polygon
										if (areValidPolygons(coordinatesLongLatFormat, "polygon_type"))
											nutsPolygon.put("valid_polygon", "true");
										else
											nutsPolygon.put("valid_polygon", "false");

										nutsPolygon.put("nuts_name", nutsIdNutsName.get(feature.get("id").toString()));
										nutsPolygon.put("level", nutsLevel[levlCounter]);

										// From which shape(LineString,MultiPolygon) polygon was extracted
										nutsPolygon.put("geometry_type", jsonGeometry.get("type").toString());
										nutsPolygon.put("nuts_id", feature.get("id").toString());
										nutsPolygon.put("File",
												currentDir.listFiles()[fileCounter].getName().toString());
										nutsPolygon.put("coordinates", coordinatesLatLongFormat);
										nutsPolygon.put("inner_rings", holes);
										nutsPolygon.put("number_of_inner_rings", numberOfInnerRings);
										nutsPolygon.put("polygon_points", outerRingSize);
										hasThisNutsLeastNumberOfCoordinatesIfTrueThenAdd(allNutsWithPolygons,
												nutsPolygon);
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
			if (!(allNutsWithPolygons.isEmpty())) {
				file.write(allNutsWithPolygons.toJSONString());
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

	/**
	 * This method returns both outer_rings and inner_rings all combined in a
	 * multipolygon a concept similar to "Well known text".
	 */
	@Override
	public org.dice_research.opal.launuts.polygons.MultiPolygon getNutsPolygon(String nutsCode)
			throws PolygonParserException {

		try {
			org.dice_research.opal.launuts.polygons.MultiPolygon multiPolygon = null;

			geojsonReader = new FileReader(this.nameOfParserAfterFinalProcessing);
			JSONArray nutsArray = (JSONArray) jsonParser.parse(geojsonReader);

			Iterator<JSONObject> nutsIterator = nutsArray.iterator();
			while (nutsIterator.hasNext()) {
				JSONObject nuts = nutsIterator.next();
				if (nuts.get(this.featureIdType).toString().equals(nutsCode)) {
					String geometryType = nuts.get("geometry_type").toString();
					JSONArray coordinates = (JSONArray) nuts.get("coordinates");
					if ("Polygon".equalsIgnoreCase(geometryType)) {
						multiPolygon = new org.dice_research.opal.launuts.polygons.MultiPolygon();
						for (int index = 0; index < coordinates.size(); index++) {

							JSONArray ring = (JSONArray) coordinates.get(index); // Could be an inner or outer ring
							org.dice_research.opal.launuts.polygons.Polygon ringPolygon = getDicePolygonFromCoordinates(
									ring);
							multiPolygon.polygons.add(ringPolygon);
						}
					} else {
						multiPolygon = new org.dice_research.opal.launuts.polygons.MultiPolygon();

						for (int index = 0; index < coordinates.size(); index++) {
							// child_polygon, may have a hole(inner_ring)
							JSONArray childPolygon = (JSONArray) coordinates.get(index);
							for (int ringIndex = 0; ringIndex < childPolygon.size(); ringIndex++) {

								// ring can be an inner_ring or outer_ring
								JSONArray ring = (JSONArray) childPolygon.get(ringIndex);
								org.dice_research.opal.launuts.polygons.Polygon ringPolygon = getDicePolygonFromCoordinates(
										ring);
								multiPolygon.polygons.add(ringPolygon);
							}
						}
					}
				}
			}
			return multiPolygon;
		} catch (Exception e) {

		}
		return null;
	}

	@Override
	public LauReaderInterface setSourceDirectory(File directory) throws PolygonParserException {
		// TODO Auto-generated method stub
		sourceDirectoryForGeojson = directory;
		return null;
	}

	/**
	 * This method returns a hole from a polygon. It can also return a hole from a
	 * child polygon of a multipolygon.
	 * 
	 * @return Polygon
	 */
	public org.dice_research.opal.launuts.polygons.Polygon getHole(String nutsCode, int polygonNumber, int holeNumber)
			throws PolygonParserException {

		org.dice_research.opal.launuts.polygons.Polygon polygonFromHole = new org.dice_research.opal.launuts.polygons.Polygon();
		try {
			geojsonReader = new FileReader(this.nameOfParserAfterFinalProcessing);
			JSONArray nutsArray = (JSONArray) jsonParser.parse(geojsonReader);

			Iterator<JSONObject> nutsIterator = nutsArray.iterator();
			while (nutsIterator.hasNext()) {
				JSONObject nuts = nutsIterator.next();
				if (nutsCode.equalsIgnoreCase(nuts.get(this.featureIdType).toString())) {
					JSONArray innerRings = (JSONArray) nuts.get("inner_rings");

					if (!innerRings.isEmpty()) {
						if ("polygon".equalsIgnoreCase(nuts.get("geometry_type").toString()) && polygonNumber == 1) {

							JSONArray hole_coordinates = (JSONArray) innerRings.get(holeNumber - 1);
							polygonFromHole = getDicePolygonFromCoordinates(hole_coordinates);
						} else {
							JSONArray inner_rings_child_polygon = (JSONArray) innerRings.get(polygonNumber - 1);
							JSONArray hole_coordinates = (JSONArray) inner_rings_child_polygon.get(holeNumber - 1);
							polygonFromHole = getDicePolygonFromCoordinates(hole_coordinates);
						}
					}

					else
						return polygonFromHole;
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Please ensure that the hole_number is correct.");

		}
		return polygonFromHole;
	}

	public org.dice_research.opal.launuts.polygons.Polygon getDicePolygonFromCoordinates(JSONArray ring) {

		org.dice_research.opal.launuts.polygons.Polygon dicePolygon = new org.dice_research.opal.launuts.polygons.Polygon();

		for (int coordinateIndex = 0; coordinateIndex < ring.size(); coordinateIndex++) {
			Point point = new Point();
			JSONArray lattitudeLongitude = (JSONArray) ring.get(coordinateIndex);
			point.latitude = Float.parseFloat(lattitudeLongitude.get(0).toString());
			point.longitude = Float.parseFloat(lattitudeLongitude.get(1).toString());
			dicePolygon.points.add(point);
		}
		return dicePolygon;
	}

	/**
	 * This method return an outer_ring of a child polygon of a multipolygon.
	 * 
	 * @return Polygon
	 */
	Polygon getOuterRingOfChildPolygonOfMultipolygon(String nutsCode, int polygonNumber) {

		org.dice_research.opal.launuts.polygons.Polygon childPolygonOuterRing = new org.dice_research.opal.launuts.polygons.Polygon();
		try {
			geojsonReader = new FileReader(this.nameOfParserAfterFinalProcessing);
			JSONArray nutsArray = (JSONArray) jsonParser.parse(geojsonReader);

			Iterator<JSONObject> nutsIterator = nutsArray.iterator();
			while (nutsIterator.hasNext()) {
				JSONObject nuts = nutsIterator.next();
				if (nutsCode.equalsIgnoreCase(nuts.get(this.featureIdType).toString())) {
					if (nuts.get("geometry_type").toString().equalsIgnoreCase("multipolygon")) {
						JSONArray coordinates = (JSONArray) nuts.get("coordinates");
						JSONArray childPolygonCoordinates = (JSONArray) coordinates.get(polygonNumber - 1);
						JSONArray childPolygonOuterRingCoordinates = (JSONArray) childPolygonCoordinates.get(0);
						childPolygonOuterRing = getDicePolygonFromCoordinates(childPolygonOuterRingCoordinates);
					} else
						return childPolygonOuterRing;
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Please ensure that the id (nuts_id or lau_id) and the polygon number are correct.");

		}

		return childPolygonOuterRing;
	}

}
