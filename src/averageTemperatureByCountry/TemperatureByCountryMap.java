package averageTemperatureByCountry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;

import processing.core.PApplet;

public class TemperatureByCountryMap extends PApplet{
	private static final long serialVersionUID = 1L;
	
	UnfoldingMap map;
	Map<String, Float> countryTempMap;
	List<Feature> countries;
	List<Marker> countriesMarkers;
	
	public void setup() {
		countryTempMap = new HashMap<String, Float>();
		countries = new ArrayList<Feature>();
		countriesMarkers = new ArrayList<Marker>();
		size(800, 600, OPENGL);
		this.background(200, 200, 200);
		map = new UnfoldingMap(this, 50, 50, 700, 500, new Google.GoogleMapProvider());
		MapUtils.createDefaultEventDispatcher(this, map);
		countries = GeoJSONReader.loadData(this, "../data/countries.geo.json");
		countriesMarkers = MapUtils.createSimpleMarkers(countries);
		map.addMarkers(countriesMarkers);
		initCountryTempMapObject();
		shadeCountries();
	}
	
	public void draw() {
		map.draw();
	}

	//reads data from a file and populates the map based on country code and temperature
	private void initCountryTempMapObject(){
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(
					"../data/country_code_temp.txt"));
			String line = reader.readLine();
			while (line != null) {
				String[] lineValues = line.split(",");				
				countryTempMap.put(lineValues[0], Float.valueOf(lineValues[1]));
				line = reader.readLine();
			}			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//for each marker, if there is a corresponding value in the map object then shade the map based on the temperatures
	//if there is no corresponding value in the map object, shade it gray
	private void shadeCountries(){
		for(Marker marker : countriesMarkers){
			String countryId = marker.getId();
			if(countryTempMap.containsKey(countryId)){
				float aveTemp = countryTempMap.get(countryId);
				int colorLevel = (int) map(aveTemp, -5f, 28.25f, 10, 255);
				marker.setColor(color(colorLevel, 100, 255-colorLevel));
			}
			else{
				marker.setColor(color(150, 150, 150));
			}
		}
	}

}
