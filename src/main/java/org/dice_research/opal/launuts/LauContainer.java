package org.dice_research.opal.launuts;

import java.io.Serializable;

public class LauContainer implements Serializable {

	private static final long serialVersionUID = 1L;

	public String nutsCode;
	public String lauCode;
	public String lauNameLatin;
	public Integer population;
	public Integer area;

	public boolean isValid() {
		if (nutsCode == null || nutsCode.isEmpty()) {
			return false;
		}
		if (lauCode == null || lauCode.isEmpty()) {
			return false;
		}
		if (lauNameLatin == null || lauNameLatin.isEmpty()) {
			return false;
		}
		return true;
	}
}