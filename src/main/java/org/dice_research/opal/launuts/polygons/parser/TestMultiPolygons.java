package org.dice_research.opal.launuts.polygons.parser;

import org.dice_research.opal.launuts.polygons.MultiPolygon;
import org.dice_research.opal.launuts.polygons.Point;
import org.dice_research.opal.launuts.polygons.Polygon;

public class TestMultiPolygons {

	/**
	 * This class has been created two test equality of two MultiPolygons A and B.
	 * The latitude and longitude of each point of A and B are compared to check if
	 * they are approximately equal or not with a threshold of 0.5. For example,
	 * 13.154 and 13.611 are equal with the threshold of 0.5.
	 * 
	 * If total percentage of approximately equal points of both A and B are more
	 * than 70% then they are equal MutiPolygons.
	 */

	public boolean areTwoMultiPolygonsEqual(MultiPolygon a, MultiPolygon b) {

		int total_number_of_coordinates_of_any_polygon = 0;
		int total_number_of_approximately_equal_coordinates = 0;
		int total_percentage_of_approximately_equal_coordinates = 0;
		double admissibe_threshold = 0.5;
		int tolerance_for_polygon_size_difference = 2;

		if (b.polygons.size() != a.polygons.size())
			return false;

		else {

			for (int number = 0; number < b.polygons.size(); number++) {

				Polygon polygon_A = a.polygons.get(number);
				Polygon polygon_B = b.polygons.get(number);

				/*
				 * Go ahead only when the two polygon's coordinates number differ maximum by 1
				 */
				if (Math.abs(polygon_A.points.size() - polygon_B.points.size()) > tolerance_for_polygon_size_difference)
					return false;

				else {
					int total_number_of_coordinates_to_check = polygon_A.points.size() > polygon_B.points.size()
							? polygon_A.points.size() - (polygon_A.points.size() - polygon_B.points.size())
							: polygon_B.points.size() > polygon_A.points.size()
									? polygon_B.points.size() - (polygon_B.points.size() - polygon_A.points.size())
									: polygon_B.points.size();

					for (int i = 0; i < total_number_of_coordinates_to_check; i++) {

						Point ith_coordinate_of_A = polygon_A.points.get(i);
						Point ith_coordinate_of_B = polygon_B.points.get(i);
						if (approximatelyEqual(ith_coordinate_of_A.latitude, ith_coordinate_of_B.latitude,
								admissibe_threshold)
								&& approximatelyEqual(ith_coordinate_of_A.longitude, ith_coordinate_of_B.longitude,
										admissibe_threshold))
							total_number_of_approximately_equal_coordinates++;

						total_number_of_coordinates_of_any_polygon++;
					}
				}

			}

			if (total_number_of_coordinates_of_any_polygon > 0)
				total_percentage_of_approximately_equal_coordinates = (total_number_of_approximately_equal_coordinates
						* 100) / total_number_of_coordinates_of_any_polygon;
		}

		System.out.println("tatal coorrdinates :" + total_number_of_coordinates_of_any_polygon);
		System.out.println("total approximate equal coords :" + total_number_of_approximately_equal_coordinates);
		System.out.println("total_percentage_of_approximately_equal_coordinates :"
				+ total_percentage_of_approximately_equal_coordinates);
		System.out.println(" ");

		return total_percentage_of_approximately_equal_coordinates >= 70;
	}

	private static boolean approximatelyEqual(double a, double b, double precision) {
		return (Math.abs(a - b) < precision);
	}

}
