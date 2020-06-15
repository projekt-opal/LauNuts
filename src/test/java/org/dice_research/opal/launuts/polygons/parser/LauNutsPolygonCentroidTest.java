package org.dice_research.opal.launuts.polygons.parser;

import org.dice_research.opal.launuts.polygons.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/*
 * Author: Amit Kumar
 *
 * This file implements the test cases for computation of nuts polygons centroid.
 *
 * */


public class LauNutsPolygonCentroidTest {
    LauNutsPolygonCentroid LauNutsObj = new LauNutsPolygonCentroid();

    @Test
    public void testCentroidComputationDE713() throws Exception{
        // NUTS code DE: DEF01
        //Test 2: Test to check if the correct centroid computation is done
        //Case: Polygon
        //Testing type: Positive Unit testing

        assertEquals("50.108166,8.79162", LauNutsObj.getNutsCenterPoint("DE713").toString());
    }

    @Test
    public void testCentroidComputationDE122() throws Exception {
        // NUTS code DE: DEF01
        //Test 2: Test to check if the correct centroid computation is done
        //Case: Polygon
        //Testing type: Positive Unit testing

        assertEquals("49.029606,8.39315", LauNutsObj.getNutsCenterPoint("DE122").toString());
    }

    @Test
    public void testCentroidComputationDEG() throws Exception {
        // NUTS code DE: DEF01
        //Test 2: Test to check if the correct centroid computation is done
        //Case: Polygon
        //Testing type: Positive Unit testing

        assertEquals("50.90602,11.272815", LauNutsObj.getNutsCenterPoint("DEG").toString());
    }

    @Test
    public void testNegativeCentroidComputationDEG() throws Exception {
        // NUTS code DE: DEF01
        //Test 2: Test to check if the correct centroid computation is done
        //Case: Polygon
        //Testing type: Negative Unit testing

        assertNotEquals("0,0", LauNutsObj.getNutsCenterPoint("DEG").toString());
    }

    @Test
    public void testCentroidComputationDE() throws Exception {
        // NUTS code DE: DE
        //Test 2: Test to check if the correct centroid computation is done
        //Case: Multipolygon
        //Testing type: Positive Unit testing

        assertEquals("50.76833,10.384371", LauNutsObj.getNutsCenterPoint("DE").toString());
    }

    @Test
    public void testCentroidComputationDEF() throws Exception {
        // NUTS code DE: DEF
        //Test 2: Test to check if the correct centroid computation is done
        //Case: Multipolygon
        //Testing type: Positive Unit testing

        assertEquals("50.76833,10.384371", LauNutsObj.getNutsCenterPoint("DE").toString());
    }

    @Test
    public void testNegativeCentroidComputationDEF() throws Exception {
        // NUTS code DE: DEF
        //Test 2: Test to check if the correct centroid computation is done
        //Case: Multipolygon
        //Testing type: Negative Unit testing

        assertNotEquals("0,0", LauNutsObj.getNutsCenterPoint("DE").toString());
    }
}