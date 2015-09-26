package goeuro;

import java.io.InputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainClass {
	private static final String CONFIG_FILE_NAME = "config.properties";
	private static final String PROVIDER_URL_PROPERTY_KEY = "service.provider.url";
	private static final String OUTPUT_FILE = "result.file.name";

	private static final String ID = "_id";
	private static final String NAME = "name";
	private static final String TYPE = "type";
	private static final String GEO_POSITION = "geo_position";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	
	private static char COMMA = ',';
	private static char END_OF_LINE = '\n';
	
	public static void main(String[] args) {
		
		//verify correctness of user input 
		System.out.println("Please use the following pattern: java -jar GoEuroTest.jar CITY_NAME," +
				" where the CITY_NAME defines the name of the city you need information for");
		if(args.length != 1){
			System.out.println("Wrong usage, please provide exactly one city name.");
			System.exit(1);
		}
		
		Properties properties = getProperties();
		URL providerURL = getProviderURL(properties, args[0]);
	    String queryResult = getQueryResults(providerURL);
	    writeResultsToFile(queryResult, properties);
	    System.out.println("Please check the results file: " + properties.getProperty(OUTPUT_FILE));
	}
	

	
	private static Properties getProperties() {
		Properties configurationProperties = new Properties();
		InputStream configInputStream = MainClass.class.getResourceAsStream(CONFIG_FILE_NAME);
		try {
			configurationProperties.load(configInputStream);
		} catch (IOException e) {
			throw new RuntimeException("Cannot load the properties file", e);
		}
	    return configurationProperties;
	}
	
	private static URL getProviderURL(Properties properties, String cityForQuery) {
	    try {
			return new URL(properties.get(PROVIDER_URL_PROPERTY_KEY) + cityForQuery);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Cannot create URL from the property "+PROVIDER_URL_PROPERTY_KEY+" in the the configuration file ", e);
		}
	}
	
    private static String getQueryResults(URL providerURL) {
	    Scanner scanner;
		try {
			scanner = new Scanner(providerURL.openStream(), "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException("Cannot connect to the URL from the property "+PROVIDER_URL_PROPERTY_KEY+" in the the configuration file ", e);
		}
	    StringBuilder builder = new StringBuilder();
	    while (scanner.hasNext()){
	    	builder.append(scanner.nextLine());
	    }       
	    scanner.close();
		return builder.toString();
    }
    
    private static void writeResultsToFile(String queryResults, Properties properties) {
	    JSONArray ar = new JSONArray(queryResults);
	    FileWriter writer;
		try {
			writer = new FileWriter(properties.getProperty(OUTPUT_FILE));
		    for (int i = 0; i < ar.length(); i++) {
		    	
			    JSONObject obj = ar.getJSONObject(i);
			    Object id = obj.get(ID);
			    Object name = obj.get(NAME);
			    Object type = obj.get(TYPE);
			    
			    JSONObject geoPosition = obj.getJSONObject(GEO_POSITION);
			    Object latitude = geoPosition.get(LATITUDE);
			    Object longitude = geoPosition.get(LONGITUDE);
			    writer.append(id.toString());
			    writer.append(COMMA);
			    writer.append(name.toString());
			    writer.append(COMMA);
			    writer.append(type.toString());
			    writer.append(COMMA);
			    writer.append(latitude.toString());
			    writer.append(COMMA);
			    writer.append(longitude.toString());
			    writer.append(END_OF_LINE);
			}
		    writer.flush();
		    writer.close();
		} catch (IOException e) {
			throw new RuntimeException("Cannot write results to the output file, specified by property "+OUTPUT_FILE+" in the the configuration file ", e);
		}

    }
}
