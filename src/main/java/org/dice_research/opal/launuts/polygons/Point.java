package org.dice_research.opal.launuts.polygons;

/**
 * Point or coordinate.
 * 
 * @author Adrian Wilke
 */
public class Point {

	public float latitude;
	public float longitude;

	@Override
	public String toString() {
		return latitude + "," + longitude;
	}
}