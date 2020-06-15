package org.dice_research.opal.launuts;

import java.util.LinkedList;
import java.util.List;

public class GeoContainer {

	public class CoordinateTupel {
		public float x;
		public float y;

		public CoordinateTupel() {
		}

		public CoordinateTupel(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	List<List<CoordinateTupel>> polygones = new LinkedList<List<CoordinateTupel>>();
	public String nutsCode;
	public String nutsName;
	public int nutsLevel;
}