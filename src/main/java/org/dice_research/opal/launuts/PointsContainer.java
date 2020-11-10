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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(nutsId);
		sb.append(" [");
		sb.append(longitude);
		sb.append(",");
		sb.append(latitude);
		sb.append("] ");
		if (nameLatin != null) {
			sb.append(nameLatin);
			sb.append(" ");
		}
		if (level != null) {
			sb.append(level);
			sb.append(" ");
		}
		return sb.toString();
	}
}