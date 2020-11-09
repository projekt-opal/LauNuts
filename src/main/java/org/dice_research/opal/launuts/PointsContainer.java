package org.dice_research.opal.launuts;

import java.io.Serializable;

public class PointsContainer implements Serializable {

	private static final long serialVersionUID = 1L;

	public String nutsId;
	public Float longitude;
	public Float latitude;

	public String nameLatin;
	public Integer level;

	public boolean isValid() {
		if (nutsId == null || nutsId.isEmpty()) {
			return false;
		}
		if (longitude == null) {
			return false;
		}
		if (latitude == null) {
			return false;
		}
		return true;
	}
}