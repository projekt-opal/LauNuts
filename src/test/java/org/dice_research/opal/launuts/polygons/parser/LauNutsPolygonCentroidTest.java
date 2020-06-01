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
	public void testCentroidDE713() throws Exception{
		// NUTS code DE: DEF01
		// LAU code DE: 01001000
		//Test 2: Test to check if the LAU and NUTS code are available in list
		assertEquals("8.79162,50.108166", LauNutsObj.getNutsCenterPoints("DE713").toString());
	}

	@Test
	public void testCentroidDE122() throws Exception{
		// NUTS code DE: DEF01
		// LAU code DE: 01001000
		//Test 2: Test to check if the LAU and NUTS code are available in list
		assertEquals("8.39315,49.029606", LauNutsObj.getNutsCenterPoints("DE122").toString());
	}
}
