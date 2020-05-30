package org.dice_research.opal.launuts.polygons.parser;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.dice_research.opal.launuts.polygons.MultiPolygon;
import org.dice_research.opal.launuts.polygons.Point;
import org.dice_research.opal.launuts.polygons.Polygon;
import org.dice_research.opal.launuts.polygons.PolygonParserException;

public class TestMultiPolygons {

	/*
	 * This class has been created two test equality of two MultiPolygons A and B.
	 * The latitude and longitude of each point of A and B are compared to check if
	 * they are approximately equal or not with a threshold of 0.03. For example,
	 * 13.154 and 13.611 are equal with the threshold of 0.03.
	 * 
	 * If total percentage of approximately equal points of both A and B are more
	 * than 70% then they are equal MutiPolygons.
	 */
	//public static void main(String[] args) throws PolygonParserException {

	public boolean AreTwoPolygonsEqual(MultiPolygon a, MultiPolygon b) {

		int total_number_of_coordinates_of_any_polygon = 0;
		int total_number_of_approximately_equal_coordinates = 0;
		int total_percentage_of_approximately_equal_coordinates = 0;
//		NutsParser nut_parser = new NutsParser();
//		MultiPolygon a = nut_parser.getMultiPolygonFromHole("DEG0P", 1);
//		MultiPolygon b = nut_parser.getNutsPolygon("DEG0N");

		if (b.polygons.size() != a.polygons.size()) {
			return false;
			//System.out.println("false");
		}

		else {

			for (int number = 0; number < b.polygons.size(); number++) {

				Polygon polygon_A = a.polygons.get(number);
				Polygon polygon_B = b.polygons.get(number);

				/*
				 * Go ahead only when the two polygon's coordinates number differ maximum by 1
				 */
				if (Math.abs(polygon_A.points.size() - polygon_B.points.size()) > 1) {
					//System.out.println("false");
					return false;
				}

				else {
					int total_number_of_coordinates_to_check = polygon_A.points.size() > polygon_B.points.size()
							? polygon_A.points.size() - 1
							: polygon_B.points.size() > polygon_A.points.size() ? polygon_B.points.size() - 1
									: polygon_B.points.size();


					for (int i = 0; i < total_number_of_coordinates_to_check; i++) {

						Point ith_coordinate_of_A = polygon_A.points.get(i);
						Point ith_coordinate_of_B = polygon_B.points.get(i);
						if (approximatelyEqual(ith_coordinate_of_A.latitude, ith_coordinate_of_B.latitude, 0.5)
								&& approximatelyEqual(ith_coordinate_of_A.longitude, ith_coordinate_of_B.longitude,
										0.8))
							total_number_of_approximately_equal_coordinates++;

						total_number_of_coordinates_of_any_polygon++;
					}
				}

			}

			total_percentage_of_approximately_equal_coordinates = (total_number_of_approximately_equal_coordinates
					/ total_number_of_coordinates_of_any_polygon) * 100;
		}

		System.out.println("tatal coorrdinates :" + total_number_of_coordinates_of_any_polygon);
		System.out
				.println("total approximate equal coords :" + total_number_of_approximately_equal_coordinates);
		System.out.println("total_percentage_of_approximately_equal_coordinates :" + total_percentage_of_approximately_equal_coordinates);
		System.out.println(" ");
		
		if (total_percentage_of_approximately_equal_coordinates >= 70) {
			//System.out.println("true");
			return true;
		}

		else {
			//System.out.println("false");
			return false;
		}

	}

	static boolean approximatelyEqual(double a, double b, double precision) {
		if (Math.abs(a - b) < precision)
			return true;
		else
			return false;
	}

}
