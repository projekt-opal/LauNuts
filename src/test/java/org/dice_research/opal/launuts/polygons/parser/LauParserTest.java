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

	public static LauParser lauParser = new LauParser();
	public TestMultiPolygons tester = new TestMultiPolygons();
	MultiPolygon firstMultipolygon;
	MultiPolygon SecondMultiPolygon;

	@Before
	public void setUp() throws Exception {
		firstMultipolygon = new MultiPolygon();
		SecondMultiPolygon = new MultiPolygon();
	}

	@After
	public void tearDown() {
		firstMultipolygon = null;
		SecondMultiPolygon = null;
	}

	/*
	 * The three holes of Baden-Baden(DE_08211000) are 3 child polygons of Sinzheim
	 * (DE_08216049)
	 */
	@Test
	public void testCaseForHoleTestingOfBadenBaden() throws PolygonParserException {
		final Polygon FIRST_HOLE_OF_BADEN_BADEN_STADT = lauParser.getHole("DE_08211000", 1, 1);
		final Polygon FIRST_POLYGON_OF_SINZHEIM = lauParser.getOuterRingOfChildPolygonOfMultipolygon("DE_08216049", 1);

		final Polygon SECOND_HOLE_OF_BADEN_BADEN_STADT = lauParser.getHole("DE_08211000", 1, 2);
		final Polygon THIRD_POLYGON_OF_SINZHEIM = lauParser.getOuterRingOfChildPolygonOfMultipolygon("DE_08216049", 3);

		final Polygon THIRD_HOLE_OF_BADEN_BADEN_STADT = lauParser.getHole("DE_08211000", 1, 3);
		final Polygon SECOND_POLYGON_OF_SINZHEIM = lauParser.getOuterRingOfChildPolygonOfMultipolygon("DE_08216049", 2);
		
		firstMultipolygon.polygons.add(FIRST_HOLE_OF_BADEN_BADEN_STADT);
		firstMultipolygon.polygons.add(SECOND_HOLE_OF_BADEN_BADEN_STADT);
		firstMultipolygon.polygons.add(THIRD_HOLE_OF_BADEN_BADEN_STADT);
		SecondMultiPolygon.polygons.add(FIRST_POLYGON_OF_SINZHEIM);
		SecondMultiPolygon.polygons.add(THIRD_POLYGON_OF_SINZHEIM);
		SecondMultiPolygon.polygons.add(SECOND_POLYGON_OF_SINZHEIM);
		
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(firstMultipolygon, SecondMultiPolygon));
	}

	/*
	 * The one hole inside Kelheim, st(DE_09273137) is the second polygon of
	 * Abensberg,st(DE_09273111).
	 */
	@Test
	public void testCaseForHoleTestingOfKelheim() throws PolygonParserException {
		final Polygon FIRST_HOLE_OF_KELHEIM_ST = lauParser.getHole("DE_09273137", 1, 1);
		final Polygon SECOND_POLYGON_OF_ABENSBERG = lauParser.getOuterRingOfChildPolygonOfMultipolygon("DE_09273111", 2);
		
		firstMultipolygon.polygons.add(FIRST_HOLE_OF_KELHEIM_ST);
		SecondMultiPolygon.polygons.add(SECOND_POLYGON_OF_ABENSBERG);
		
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(firstMultipolygon, SecondMultiPolygon));
		
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

		final Polygon FIRST_HOLE_OF_5TH_POLYGON_OF_DE_09472444 = lauParser.getHole("DE_09472444", 2, 1);
		final Polygon FIRST_POLYGON_OF_BISCHOFSGRÜN = lauParser.getOuterRingOfChildPolygonOfMultipolygon("DE_09472121", 1);

		final Polygon SECOND_HOLE_OF_5TH_POLYGON_OF_DE_09472444 = lauParser.getHole("DE_09472444", 2, 2);
		final Polygon SECOND_POLYGON_OF_BISCHOFSGRÜN = lauParser.getOuterRingOfChildPolygonOfMultipolygon("DE_09472121", 2);

		final Polygon THIRD_HOLE_OF_5TH_POLYGON_OF_DE_09472444 = lauParser.getHole("DE_09472444", 2, 3);
		final Polygon FIRST_POLYGON_OF_WARMENSTEINACH = lauParser.getOuterRingOfChildPolygonOfMultipolygon("DE_09472198", 1);

		final Polygon FOURTH_HOLE_OF_5TH_POLYGON_OF_DE_09472444 = lauParser.getHole("DE_09472444", 2, 4);
		final Polygon SECOND_POLYGON_OF_GOLDKRONACH_ST = lauParser.getOuterRingOfChildPolygonOfMultipolygon("DE_09472143",
				2);
		
		firstMultipolygon.polygons.add(FIRST_HOLE_OF_5TH_POLYGON_OF_DE_09472444);
		firstMultipolygon.polygons.add(SECOND_HOLE_OF_5TH_POLYGON_OF_DE_09472444);
		firstMultipolygon.polygons.add(THIRD_HOLE_OF_5TH_POLYGON_OF_DE_09472444);
		firstMultipolygon.polygons.add(FOURTH_HOLE_OF_5TH_POLYGON_OF_DE_09472444);
		SecondMultiPolygon.polygons.add(FIRST_POLYGON_OF_BISCHOFSGRÜN);
		SecondMultiPolygon.polygons.add(SECOND_POLYGON_OF_BISCHOFSGRÜN);
		SecondMultiPolygon.polygons.add(FIRST_POLYGON_OF_WARMENSTEINACH);
		SecondMultiPolygon.polygons.add(SECOND_POLYGON_OF_GOLDKRONACH_ST);
		
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(firstMultipolygon, SecondMultiPolygon));
	}

	/*
	 * The first and second holes of Tetenbüll (DE_01054135) are the second and the
	 * third child polygons of Oldenswort (DE_01054095) respectively.
	 */
	@Test
	public void testCaseForHoleTestingOfTetenbüll() throws PolygonParserException {
		final Polygon FIRST_HOLE_OF_TETENBÜLL = lauParser.getHole("DE_01054135", 1, 1);
		final Polygon SECOND_POLYGON_OF_OLDENSWORT = lauParser.getOuterRingOfChildPolygonOfMultipolygon("DE_01054095", 2);

		final Polygon SECOND_HOLE_OF_TETENBÜLL = lauParser.getHole("DE_01054135", 1, 2);
		final Polygon THIRD_POLYGON_OF_OLDENSWORT = lauParser.getOuterRingOfChildPolygonOfMultipolygon("DE_01054095", 3);
		
		firstMultipolygon.polygons.add(FIRST_HOLE_OF_TETENBÜLL);
		firstMultipolygon.polygons.add(SECOND_HOLE_OF_TETENBÜLL);
		SecondMultiPolygon.polygons.add(SECOND_POLYGON_OF_OLDENSWORT);
		SecondMultiPolygon.polygons.add(THIRD_POLYGON_OF_OLDENSWORT);
		
		Assert.assertEquals(true, tester.areTwoMultiPolygonsEqual(firstMultipolygon, SecondMultiPolygon));
	}
	
	
	/*
	 * Albersdorf(DE_01051001) has no holes.
	 * Barkenholm (DE_01051005) is a polygon and does not exist as a hole inside Albersdorf.
	 * 
	 */
	@Test
	public void testCaseForFalseHoleTestingOfAlbersdorf() throws PolygonParserException {
		final Polygon FIRST_HOLE_OF_ALBERSDORF = lauParser.getHole("DE_01051001", 1, 1);
		final MultiPolygon POLYGON_OF_BARKENHOLM = lauParser.getNutsPolygon("DE_01051005");
		
		firstMultipolygon.polygons.add(FIRST_HOLE_OF_ALBERSDORF);
		
		Assert.assertEquals(false, tester.areTwoMultiPolygonsEqual(firstMultipolygon, POLYGON_OF_BARKENHOLM));
		
	}

}
