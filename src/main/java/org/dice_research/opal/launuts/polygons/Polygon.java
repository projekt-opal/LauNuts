package org.dice_research.opal.launuts.polygons;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Polygon represented by a list of points.
 * 
 * @author Adrian Wilke
 */
public class Polygon {

	public List<Point> points;

	public Polygon() {
		points = new LinkedList<>();
	}

	public Polygon(int size) {
		points = new ArrayList<>(size);
	}

	@Override
	public String toString() {
		return points.toString();
	}
}