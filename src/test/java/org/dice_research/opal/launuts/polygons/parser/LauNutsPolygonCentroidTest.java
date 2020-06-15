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
    public org.dice_research.opal.launuts.polygons.Point centroidPoint= null;
    @Test
    public void testCentroidComputationDE713() throws Exception {
        // NUTS code DE: DEF01
        //Test 2: Test to check if the correct centroid computation is done
        //Case: Polygon
        //Testing type: Positive Unit testing

        centroidPoint = LauNutsObj.getNutsCenterPoint("DE713");
        System.out.println("Centroid Point : "+ centroidPoint);
        assertEquals("50.108166,8.79162", centroidPoint.toString());
    }

    @Test
    public void testCentroidComputationDE122() throws Exception {
        // NUTS code DE: DEF01
        //Test 2: Test to check if the correct centroid computation is done
        //Case: Polygon
        //Testing type: Positive Unit testing

        centroidPoint = LauNutsObj.getNutsCenterPoint("DE122");
        System.out.println("Centroid Point : "+ centroidPoint);
        assertEquals("49.029606,8.39315", centroidPoint.toString());
    }

    @Test
    public void testCentroidComputationDEG() throws Exception {
        // NUTS code DE: DEF01
        //Test 2: Test to check if the correct centroid computation is done
        //Case: Polygon
        //Testing type: Positive Unit testing

        centroidPoint = LauNutsObj.getNutsCenterPoint("DEG");
        System.out.println("Centroid Point : "+ centroidPoint);
        assertEquals("50.90602,11.272815", centroidPoint.toString());
    }

    @Test
    public void testNegativeCentroidComputationDEG() throws Exception {
        // NUTS code DE: DEF01
        //Test 2: Test to check if the correct centroid computation is done
        //Case: Polygon
        //Testing type: Negative Unit testing

        centroidPoint = LauNutsObj.getNutsCenterPoint("DEG");
        assertNotEquals("0,0", centroidPoint.toString());
    }

    @Test
    public void testCentroidComputationDE() throws Exception {
        // NUTS code DE: DE
        //Test 2: Test to check if the correct centroid computation is done
        //Case: Multipolygon
        //Testing type: Positive Unit testing

        centroidPoint = LauNutsObj.getNutsCenterPoint("DE");
        System.out.println("Centroid Point : "+ centroidPoint);
        assertEquals("50.76833,10.384371", centroidPoint.toString());
    }

    @Test
    public void testCentroidComputationDEF() throws Exception {
        // NUTS code DE: DEF
        //Test 2: Test to check if the correct centroid computation is done
        //Case: Multipolygon
        //Testing type: Positive Unit testing

        centroidPoint = LauNutsObj.getNutsCenterPoint("DEF");
        System.out.println("Centroid Point : "+ centroidPoint);
        assertEquals("54.17623,9.850034", centroidPoint.toString());
    }

    @Test
    public void testNegativeCentroidComputationDEF() throws Exception {
        // NUTS code DE: DEF
        //Test 2: Test to check if the correct centroid computation is done
        //Case: Multipolygon
        //Testing type: Negative Unit testing

        centroidPoint = LauNutsObj.getNutsCenterPoint("DE");
        assertNotEquals("0,0", centroidPoint.toString());
    }
}