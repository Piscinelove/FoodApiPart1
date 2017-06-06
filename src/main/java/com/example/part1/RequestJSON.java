package com.example.part1;

import java.net.URI;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.bson.Document;
import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class RequestJSON {

	//API KEY
	private static final String API_KEY = "Token token=73ff0559aaff864556656698a95cd4a7";
	//GENERATED EXCLUDE
	private static final String EXCLUDE = "barcode%2Cdisplay_name_translations%2Corigin_translations%2Cstatus%2Calcohol_by_volume%2Cimages%2Ccategories%2Cthumb%2Cmedium%2Clarge%2Cxlarge%2Ccreated_at%2Cupdated_at";

	// JSON KEYS
	private static final String KEY_DATA = "data";
	private static final String KEY_ID = "id";
	private static final String KEY_NAME_TRANSLATIONS = "name_translations";
	private static final String KEY_FR = "fr";
	private static final String KEY_INGREDIENTS_TRANLSATIONS = "ingredients_translations";
	private static final String KEY_QUANTITY = "quantity";
	private static final String KEY_UNIT = "unit";
	private static final String KEY_PORTION_QUANTITY = "portion_quantity";
	private static final String KEY_PORTION_UNIT = "portion_unit";
	private static final String KEY_NUTRIENTS = "nutrients";
	private static final String KEY_PER_HUNDRED = "per_hundred";
	private static final String KEY_PER_PORTION = "per_portion";
	private static final String KEY_PER_DAY = "per_day";

	// JSON STRING
	private static String informations;

	// JSON VALUES
	private static List<Food> products = new ArrayList<Food>();

	// CLIENT VARIABLES
	private static Client client;
	private static ClientConfig config;
	private static WebTarget target;

	// JSON OBJECTS
	private static JSONObject data;
	private static JSONArray dataArray;
	private static JSONObject product;
	private static JSONObject nutrientsJSON;
	private static JSONObject nutrientsElementsJSON;

	public static void main(String[] args) throws JSONException {

		// GET JSON STRING
		informations = getAllProducts();
		// SAVE JSON DATA
		data = new JSONObject(informations);

		// GET ONLY FRENCH TRANSLATIONS
		getAllFrenchProducts();
		
		//SAVE TO MONGODB
		saveData();

	}

	//GET URI OF OPENFOOD
	private static URI getURI() {
		return UriBuilder.fromUri("https://www.openfood.ch/").build();
	}
	
	//GET URI OF OUR WEBSERVICE
	private static URI getWebServiceURI() {
		return UriBuilder.fromUri("http://localhost:8080/").build();
	}


	//GET ALL PRODUCTS
	private static String getAllProducts() {
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);

		target = client.target(getURI());

		String data = target.path("api").path("v3").path("products").queryParam("excludes", EXCLUDE)
				.queryParam("page%5Bsize%5D", "200").request().accept(MediaType.TEXT_PLAIN)
				.header("Accept", "application/json").header("Authorization", API_KEY).get(String.class);

		return data;
	}

	//RETRIEVE ONLY THE PRODUCTS WITH A FRENCH TRANSLATION
	private static void getAllFrenchProducts() throws JSONException {
		dataArray = data.getJSONArray(KEY_DATA);

		for (int i = 0; i < dataArray.length(); i++) {
			product = dataArray.getJSONObject(i);

			// CREATE A NEW FOOD OBJECT
			Food food = new Food();
			food.set_id(String.valueOf(product.getInt(KEY_ID)));
			// TAKE ONLY FR TRANSLATIONS
			if (product.getJSONObject(KEY_NAME_TRANSLATIONS).has(KEY_FR))
				food.setName(product.getJSONObject(KEY_NAME_TRANSLATIONS).getString(KEY_FR));
			else
				continue;

			if (product.getJSONObject(KEY_INGREDIENTS_TRANLSATIONS).has(KEY_FR))
				food.setIngredients_translations(product.getJSONObject(KEY_INGREDIENTS_TRANLSATIONS).getString(KEY_FR));
			else
				continue;

			food.setQuantity(product.getInt(KEY_QUANTITY));
			food.setUnit(product.getString(KEY_UNIT));
			food.setPortion_quantity(product.getInt(KEY_PORTION_QUANTITY));
			food.setPortion_unit(product.getString(KEY_PORTION_UNIT));

			// CREATE A NEW NUTRIENT OBJECT
			Map<String, NutrientsElements> nutrients = new HashMap<String, NutrientsElements>();
			nutrientsJSON = product.getJSONObject(KEY_NUTRIENTS);
			Iterator keys = nutrientsJSON.keys();

			while (keys.hasNext()) {
				String currentKey = keys.next().toString();
				NutrientsElements nutrientsElements = new NutrientsElements();
				nutrientsElementsJSON = nutrientsJSON.getJSONObject(currentKey);

				nutrientsElements.setName_translations(nutrientsElementsJSON.getJSONObject(KEY_NAME_TRANSLATIONS).getString(KEY_FR));
				nutrientsElements.setUnit(nutrientsElementsJSON.getString(KEY_UNIT));

				if (nutrientsElementsJSON.get(KEY_PER_HUNDRED).equals(null))
					nutrientsElements.setPer_hundred(0.0);
				else
					nutrientsElements.setPer_hundred(nutrientsElementsJSON.getDouble(KEY_PER_HUNDRED));

				if (nutrientsElementsJSON.get(KEY_PER_PORTION).equals(null))
					nutrientsElements.setPer_portion(0.0);
				else
					nutrientsElements.setPer_portion(nutrientsElementsJSON.getDouble(KEY_PER_PORTION));

				if (nutrientsElementsJSON.get(KEY_PER_DAY).equals(null))
					nutrientsElements.setPer_day(0.0);
				else
					nutrientsElements.setPer_day(nutrientsElementsJSON.getInt(KEY_PER_DAY));

				nutrients.put(currentKey, nutrientsElements);

			}

			food.setNutrients(nutrients);
			products.add(food);
		}

	}

	//SAVING THE DATA RETRIEVED USING OUR API
	//MANGODB MUST BE LAUNCHED
	//THE API MUST BE LAUNCHED
	public static void saveData() {
		
		System.out.println("----> INITIALISING CONNECTION...");
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		target = client.target(getWebServiceURI());
		System.out.println("----> CONNECTION SUCCESSFULL !");
		
		Builder path = target.path("food").request().accept(MediaType.TEXT_PLAIN)
				.header("Accept", "application/json");
		
		for (Food food : products) {
			System.out.println("----> CREATING A FOOD : " + food.getName());
			ObjectMapper mapper = new ObjectMapper();
			String foodJSONString;
			try {
				foodJSONString = mapper.writeValueAsString(food);
				Response response = path.post(Entity.json(foodJSONString));
				System.out.println("----> CREATION OF FOOD SUCCESSFULL");
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("INSTALLATION SUCCESSFULL");
		
		
	}
	
	//DELTE ALL UNNECESSARY ACCENTS
	public static String unaccent(String src) {
		return Normalizer.normalize(src, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("\\s+", "_").replaceAll("\\(", "").replaceAll("\\)", "");
	}
	
	

}
