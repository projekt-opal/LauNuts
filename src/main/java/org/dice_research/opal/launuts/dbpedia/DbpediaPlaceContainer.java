package org.dice_research.opal.launuts.dbpedia;

import java.io.Serializable;

/**
 * Data container used in {@link DbpediaRemote}.
 *
 * @author Adrian Wilke
 */
public class DbpediaPlaceContainer implements Serializable {

	private static final long serialVersionUID = 1L;

	public String uri;
	public String labelDe;
	public String labelEn;
	public float lat;
	public float lon;
	public String nuts;

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("uri: ");
		stringBuilder.append(uri);
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("labelDe: ");
		stringBuilder.append(labelDe);
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("labelEn: ");
		stringBuilder.append(labelEn);
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("lat: ");
		stringBuilder.append(lat);
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("lon: ");
		stringBuilder.append(lon);
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("nuts: ");
		stringBuilder.append(nuts);
		stringBuilder.append(System.lineSeparator());

		return stringBuilder.toString();
	}
}