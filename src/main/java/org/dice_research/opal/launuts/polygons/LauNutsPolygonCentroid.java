package org.dice_research.opal.launuts.polygons;

import org.dice_research.opal.launuts.polygons.parser.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.util.ArrayDelegatingEList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// https://howtodoinjava.com/library/json-simple-read-write-json-examples/

/*
* Author: Amit Kumar
*
* This file implements the computation of nuts polygons centroid.
*
* Reusing parser code from NutsParser.java
*
* */


public class LauNutsPolygonCentroid  {
	protected static String file_name = "NUT_Polygons.json";
	//JSONParser instance to parse JSON file.
	private static JSONParser json_parser = new JSONParser();
	private static Reader geojson_reader;
	public static int count=0;
	public static List<Point> centroidPoints=new ArrayList<>();
	protected static String feature_id_type = "nut_id";

	public org.dice_research.opal.launuts.polygons.Point getNutsCenterPoints(String nutsCode)
			throws PolygonParserException {

		org.dice_research.opal.launuts.polygons.PolygonParserInterface List = null;
		org.dice_research.opal.launuts.polygons.Point point = new org.dice_research.opal.launuts.polygons.Point();
		try {

			List = null;
			geojson_reader = new FileReader(file_name);
			JSONArray nuts_array = (JSONArray) json_parser.parse(geojson_reader);
			centroidPoints = new ArrayList<>();
			Iterator<JSONObject> nutsIterator = nuts_array.iterator();
			while (nutsIterator.hasNext()) {
				JSONObject nuts = nutsIterator.next();
				if (nuts.get(feature_id_type).toString().equals(nutsCode)) {
					String geometry_type = nuts.get("geometry_type").toString();
					JSONArray outer_ring = (JSONArray) nuts.get("outer_ring");
					if (geometry_type.equals("Polygon")) {
						for (int index = 0; index < outer_ring.size(); index++) {
							JSONArray ring = (JSONArray) outer_ring.get(index);
							org.dice_research.opal.launuts.polygons.Polygon ring_polygon = new org.dice_research.opal.launuts.polygons.Polygon();
							for (int coordinate_index = 0; coordinate_index < ring.size(); coordinate_index++) {
								JSONArray lattitude_longitude = (JSONArray) ring.get(coordinate_index);
								point.latitude += Float.parseFloat(lattitude_longitude.get(0).toString());
								point.longitude += Float.parseFloat(lattitude_longitude.get(1).toString());
							}
							point.latitude = point.latitude / ring.size();
							point.longitude = point.longitude / ring.size();
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return point;
	}
}