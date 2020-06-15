package org.dice_research.opal.launuts.polygons.parser;

import org.dice_research.opal.launuts.polygons.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
 * Author: Amit Kumar
 *
 * This file implements the test cases for computation of nuts polygons centroid.
 *
 * */


public class LauNutsPolygonCentroidTest {
	LauNutsPolygonCentroid LauNutsObj = new LauNutsPolygonCentroid();

	@Test
	public void testcentroidcomputationDE713() throws Exception{
		// NUTS code DE: DEF01
		//Test 2: Test to check if the correct centroid computation is done
		assertEquals("8.79162,50.108166", LauNutsObj.iteratingNuts("DE713").toString());
	}

	@Test
	public void testcentroidcomputationDE122() throws Exception {
		// NUTS code DE: DEF01
		//Test 2: Test to check if the correct centroid computation is done
		assertEquals("8.39315,49.029606", LauNutsObj.iteratingNuts("DE122").toString());
	}

	@Test
	public void testcentroidcomputationDEG() throws Exception {
		// NUTS code DE: DEF01
		//Test 2: Test to check if the correct centroid computation is done
		assertEquals("11.272815,50.90602", LauNutsObj.iteratingNuts("DEG").toString());
	}

	@Test
	public void testcentroidcomputationDE() throws Exception {
		// NUTS code DE: DE
		//Test 2: Test to check if the correct centroid computation is done
		assertEquals("11.272815,50.90602", LauNutsObj.iteratingNuts("DE").toString());
	}

	@Test
	public void testcentroidcomputationDEF() throws Exception {
		// NUTS code DE: DEF
		//Test 2: Test to check if the correct centroid computation is done
		assertEquals("11.272815,50.90602", LauNutsObj.iteratingNuts("DE").toString());
	}

}

