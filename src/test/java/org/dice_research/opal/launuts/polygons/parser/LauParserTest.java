package org.dice_research.opal.launuts.polygons.parser;

import org.dice_research.opal.launuts.polygons.MultiPolygon;
import org.dice_research.opal.launuts.polygons.Polygon;
import org.dice_research.opal.launuts.polygons.PolygonParserException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * In this test class, we will perform two tests.
 * 
 * In the first test, the integrity of the holes in LAU_Polygons.json will be
 * confirmed .
 * 
 * The second test is to confirm the validity of a few important methods which
 * were used in LauParser class to extract polygons from a shape file provides
 * from Eurostat.
 * 
 * @author Gourab Sahu
 *
 */

public class LauParserTest {

	public static LauParser lau_parser = new LauParser();
	public TestMultiPolygons tester = new TestMultiPolygons();
	MultiPolygon a;
	MultiPolygon b;

	@Before
	public void setUp() throws Exception {
		a = new MultiPolygon();
		b = new MultiPolygon();
	}

	@After
	public void tearDown() {
		a = null;
		b = null;
	}

	/*
	 * The three holes of Baden-Baden(DE_08211000) are 3 child polygons of Sinzheim
	 * (DE_08216049)
	 */
	@Test
	public void testCaseForHoleTestingOfBadenBaden() throws PolygonParserException {
		Polygon first_hole_of_Baden_Baden_stadt = lau_parser.getHole("DE_08211000", 1, 1);
		Polygon first_polygon_of_Sinzheim = lau_parser.getOuterRingOfChildPolygonOfMultipolygon("DE_08216049", 1);

		Polygon second_hole_of_Baden_Baden_stadt = lau_parser.getHole("DE_08211000", 1, 2);
		Polygon third_polygon_of_Sinzheim = lau_parser.getOuterRingOfChildPolygonOfMultipolygon("DE_08216049", 3);

		Polygon third_hole_of_Baden_Baden_stadt = lau_parser.getHole("DE_08211000", 1, 3);
		Polygon second_polygon_of_Sinzheim = lau_parser.getOuterRingOfChildPolygonOfMultipolygon("DE_08216049", 2);
		
		a.polygons.add(first_hole_of_Baden_Baden_stadt);
		a.polygons.add(second_hole_of_Baden_Baden_stadt);
		a.polygons.add(third_hole_of_Baden_Baden_stadt);
		b.polygons.add(first_polygon_of_Sinzheim);
		b.polygons.add(third_polygon_of_Sinzheim);
		b.polygons.add(second_polygon_of_Sinzheim);
		
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(a, b));
	}

	/*
	 * The one hole inside Kelheim, st(DE_09273137) is the second polygon of
	 * Abensberg,st(DE_09273111).
	 */
	@Test
	public void testCaseForHoleTestingOfKelheim() throws PolygonParserException {
		Polygon first_hole_of_Kelheim_st = lau_parser.getHole("DE_09273137", 1, 1);
		Polygon second_polygon_of_Abensberg = lau_parser.getOuterRingOfChildPolygonOfMultipolygon("DE_09273111", 2);
		
		a.polygons.add(first_hole_of_Kelheim_st);
		b.polygons.add(second_polygon_of_Abensberg);
		
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(a, b));
		
	}

	/*
	 * The first and the second hole of the 5th polygon of
	 * "Gdefr. Geb. (Lkr Bayreuth)" (DE_09472444) matches with the first and the
	 * second polygon of Bischofsgrün (DE_09472121).
	 * 
	 * The third polygon of "Gdefr. Geb. (Lkr Bayreuth)" (DE_09472444) matches with
	 * the first polygon of the lau Warmensteinach (DE_09472198).
	 * 
	 * The fourth polygon of "Gdefr. Geb. (Lkr Bayreuth)" (DE_09472444) matches with
	 * the second polygon of the lau "Goldkronach, st" (DE_09472143).
	 */
	@Test
	public void testCaseForHoleTestingOfLkrBayreuth() throws PolygonParserException {

		// DE_09472444's 5th polygon's inner_ring is the 2nd ring in the inner_rings
		// array.

		Polygon first_hole_of_5th_polygon_of_DE_09472444 = lau_parser.getHole("DE_09472444", 2, 1);
		Polygon first_polygon_of_Bischofsgrün = lau_parser.getOuterRingOfChildPolygonOfMultipolygon("DE_09472121", 1);

		Polygon second_hole_of_5th_polygon_of_DE_09472444 = lau_parser.getHole("DE_09472444", 2, 2);
		Polygon second_polygon_of_Bischofsgrün = lau_parser.getOuterRingOfChildPolygonOfMultipolygon("DE_09472121", 2);

		Polygon third_hole_of_5th_polygon_of_DE_09472444 = lau_parser.getHole("DE_09472444", 2, 3);
		Polygon first_polygon_of_Warmensteinach = lau_parser.getOuterRingOfChildPolygonOfMultipolygon("DE_09472198", 1);

		Polygon fourth_hole_of_5th_polygon_of_DE_09472444 = lau_parser.getHole("DE_09472444", 2, 4);
		Polygon second_polygon_of_Goldkronach_st = lau_parser.getOuterRingOfChildPolygonOfMultipolygon("DE_09472143",
				2);
		
		a.polygons.add(first_hole_of_5th_polygon_of_DE_09472444);
		a.polygons.add(second_hole_of_5th_polygon_of_DE_09472444);
		a.polygons.add(third_hole_of_5th_polygon_of_DE_09472444);
		a.polygons.add(fourth_hole_of_5th_polygon_of_DE_09472444);
		b.polygons.add(first_polygon_of_Bischofsgrün);
		b.polygons.add(second_polygon_of_Bischofsgrün);
		b.polygons.add(first_polygon_of_Warmensteinach);
		b.polygons.add(second_polygon_of_Goldkronach_st);
		
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(a, b));
	}

	/*
	 * The first and second holes of Tetenbüll (DE_01054135) are the second and the
	 * third child polygons of Oldenswort (DE_01054095) respectively.
	 */
	@Test
	public void testCaseForHoleTestingOfTetenbüll() throws PolygonParserException {
		Polygon first_hole_of_Tetenbüll = lau_parser.getHole("DE_01054135", 1, 1);
		Polygon second_polygon_of_Oldenswort = lau_parser.getOuterRingOfChildPolygonOfMultipolygon("DE_01054095", 2);

		Polygon second_hole_of_Tetenbüll = lau_parser.getHole("DE_01054135", 1, 2);
		Polygon third_polygon_of_Oldenswort = lau_parser.getOuterRingOfChildPolygonOfMultipolygon("DE_01054095", 3);
		
		a.polygons.add(first_hole_of_Tetenbüll);
		a.polygons.add(second_hole_of_Tetenbüll);
		b.polygons.add(second_polygon_of_Oldenswort);
		b.polygons.add(third_polygon_of_Oldenswort);
		
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(a, b));
	}

}
