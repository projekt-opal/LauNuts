import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.dice_research.opal.launuts.GeoContainer;
import org.dice_research.opal.launuts.GeoContainer.CoordinateTupel;
import org.dice_research.opal.launuts.NutsContainer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Tmp {

	public static final String PREFIX = "NUTS_";
	public static final String SPATIALTYPE_BOUNDARIES_MULTILINES = "BN";
	public static final String SPATIALTYPE_REGIONS_MULTIPOLYGONS = "RG";
	public static final String SPATIALTYPE_LABELS_POINTS = "LB";
	public static final String POSTFIX = "_01M_2016_4326_LEVL_2.geojson";

	// codes in rg
	// https://stackoverflow.com/questions/39352962/r-find-match-nuts-code-in-shapefile

	public void parse(File geoJsonFile) throws JSONException, IOException {
		JSONObject joGeoJson = new JSONObject(FileUtils.readFileToString(geoJsonFile, StandardCharsets.UTF_8));

		JSONArray jaFeatures = joGeoJson.getJSONArray("features");
		for (int f = 0; f < jaFeatures.length(); f++) {
			JSONObject joItem = jaFeatures.getJSONObject(f);
			JSONObject joProperties = joItem.getJSONObject("properties");

			// Only Germany
			if (joProperties.getString("CNTR_CODE").equals("DE")) {

				// Item
				GeoContainer geoContainer = new GeoContainer();
				geoContainer.nutsCode = joProperties.getString("NUTS_ID");
				geoContainer.nutsLevel = joProperties.getInt("LEVL_CODE");
				geoContainer.nutsName = joProperties.getString("NUTS_NAME");

				JSONArray jaCoordinates = joItem.getJSONObject("geometry").getJSONArray("coordinates");
				for (int c = 0; c < jaCoordinates.length(); c++) {

					// Polygones
					JSONArray jaPolygons = jaCoordinates.getJSONArray(c);
					for (int p = 0; p < jaPolygons.length(); p++) {

						JSONArray jaPolygon = jaPolygons.getJSONArray(p);
						System.out.println("> " + jaPolygon);

						// TODO: seems like its not a polygon in every case

//						GeoContainer.CoordinateTupel tupel = geoContainer.new CoordinateTupel();
//						tupel.x=jaTupel.get(0);
//						tupel.y=jaTupel.getFloat(1);
//						System.out.println("> "+jaTupel);
//						System.out.println(jaTupel.getJSONArray(0));
					}
				}

			}
		}
	}

	public static void main(String[] args) throws IOException {

		Tmp tmp = new Tmp();
		tmp.parse(new File("/home/adi/DICE/Data/LauNuts/in/ref-nuts-2016-01m.geojson/" + PREFIX
				+ SPATIALTYPE_REGIONS_MULTIPOLYGONS + POSTFIX));

		if (Boolean.TRUE)
			return;
		String string = FileUtils.readFileToString(new File("/home/adi/DICE/Data/LauNuts/in/ref-nuts-2016-01m.geojson/"
				+ PREFIX + SPATIALTYPE_REGIONS_MULTIPOLYGONS + POSTFIX), "utf-8");

		JSONObject jo = new JSONObject(string);
		System.out.println(jo.keySet());

		System.out.print("crs  ");
		System.out.println(jo.get("crs"));
		System.out.print("type");
		System.out.println(jo.get("type"));

		JSONArray ja = jo.getJSONArray("features");
		for (int i = 0; i < 100; i++) {
			printDeFeatures(ja.getJSONObject(i));
//			printJo(ja.getJSONObject(i));
		}
		// --

//		string = FileUtils.readFileToString(
//				new File("/home/adi/DICE/Data/LauNuts/in/ref-nuts-2016-01m.geojson/NUTS_BN_01M_2016_3035.geojson"),
//				"utf-8");
//		x = new JSONObject(string);
//		System.out.println(x);

	}

	private static void printJo(JSONObject jo) {
		for (String key : jo.keySet()) {
			System.out.print(key + "  ");
			System.out.println(jo.get(key));
		}
	}

	private static void printDeFeatures(JSONObject jo) {
		String cntrCode = jo.getJSONObject("properties").getString("CNTR_CODE");
		if (!cntrCode.equals("DE")) {
			return;
		}
		for (String key : jo.keySet()) {
			System.out.print(key + "  ");
			System.out.println(jo.get(key));
		}
	}

}
