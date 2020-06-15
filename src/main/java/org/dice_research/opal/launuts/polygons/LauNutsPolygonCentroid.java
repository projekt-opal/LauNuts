package org.dice_research.opal.launuts.polygons;

import org.dice_research.opal.launuts.polygons.parser.*;
import java.io.IOException;
import java.util.List;

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

public class LauNutsPolygonCentroid extends NutsParser {
    org.dice_research.opal.launuts.polygons.MultiPolygon MultiPolygon = new org.dice_research.opal.launuts.polygons.MultiPolygon();
    org.dice_research.opal.launuts.polygons.Point point = new org.dice_research.opal.launuts.polygons.Point();
    Point pointCentroid = null;

    public Point getNutsCenterPoint(String nutsCode) {
        // This function calls getNutsPolygon in NutsParser and fetch the polygon found.
        //Input: Nuts code
        //Output: Centroid Point

        try {

            // Parsing nutsCode to getNutsPolygon to fetch polygon by iterating the JSON file.
            MultiPolygon = getNutsPolygon(nutsCode);

            // Calling function which will take polygon as input and compute the centroid
            pointCentroid = computeCentroidMultiPolygon(MultiPolygon);

            return pointCentroid;
        } catch (Exception e) {

        }
        return null;
    }

    public Point computeCentroidMultiPolygon(MultiPolygon multiPolygon) throws IOException, ParseException, org.locationtech.jts.io.ParseException {
        try {
            Polygon multiPolygonArray = MultiPolygon.polygons.get(0);

            for (int i = 0; i < multiPolygonArray.points.size(); i++) {


                org.dice_research.opal.launuts.polygons.Point lat_long = multiPolygonArray.points.get(i);
                point.latitude += lat_long.latitude;
                point.longitude += lat_long.longitude;
            }
            //Taking a mean of lat and long points to compute centroid.
            // This implementation for computation of centroid will be changed.
            point.latitude = point.latitude / multiPolygonArray.points.size();
            point.longitude = point.longitude / multiPolygonArray.points.size();

            return point;
        } catch (Exception e) {
        }
        return null;
    }

}

