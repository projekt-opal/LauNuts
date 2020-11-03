package org.dice_research.opal.launuts;

public class LauContainer {

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