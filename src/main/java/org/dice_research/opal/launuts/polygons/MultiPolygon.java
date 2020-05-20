package org.dice_research.opal.launuts.polygons;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Multi-polygon represented by a list of polygons.
 * 
 * @author Adrian Wilke
 */
public class MultiPolygon {

	public List<Polygon> polygons;

	//If Multipolygon has only 1 polygon
	public MultiPolygon() {
		polygons = new LinkedList<>();
	}

	//If Multipolygon has several polygons
	public MultiPolygon(int size) {
		polygons = new ArrayList<>(size);
	}

	@Override
	public String toString() {
		return polygons.toString();
	}
}