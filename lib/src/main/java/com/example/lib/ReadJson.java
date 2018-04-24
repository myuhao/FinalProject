package com.example.lib;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * The class that handles getting json results out.
 */
public class ReadJson {

    /**
     * Empty ctor.
     */
    public ReadJson() {

    }

    /**
     * Pass in the response string frow the search query, return the NDB number that can be used
     * for furthre serach.
     * @param json json string
     * @return the USDA NDB number as String.
     */
    public static String getNDB(final String json) {
        if (json != null) {
            JsonParser parser = new JsonParser();
            JsonObject results = parser.parse(json).getAsJsonObject();
            JsonObject list = results.get("list").getAsJsonObject();
            JsonArray items = list.get("item").getAsJsonArray();
            JsonObject item = items.get(0).getAsJsonObject();
            System.out.println(item.toString());
            String NDB = item.get("ndbno").getAsString();
            return NDB;
        }
        return null;
    }

    /**
     * Return the name of the product from the NDB search w/o formatting.
     * @param json the json string
     * @return the name of the product, null if not found.
     */
    public static String getName(final String json) {
        if (json != null) {
            JsonParser parser = new JsonParser();
            JsonObject results = parser.parse(json).getAsJsonObject();
            JsonObject report = results.get("report").getAsJsonObject();
            JsonObject food = report.get("food").getAsJsonObject();
            String name = food.get("name").getAsString();
            return name;
        }
        return null;
    }

    /**
     * Get any category of the nutrient value from the json String.
     * Energy in kcal: 208
     * Protein in g: 203
     * Sugar in g; 260
     * @param json the json string
     * @param id the lookup id.
     * @return the nutrient value per 100g of the product. -1 if not found.
     */
    public static int getCalPer100g(final String json, final int id) {
        if (json != null) {
            JsonParser parser = new JsonParser();
            JsonObject results = parser.parse(json).getAsJsonObject();
            JsonObject report = results.get("report").getAsJsonObject();
            JsonObject food = report.get("food").getAsJsonObject();
            JsonArray nutrients = food.get("nutrients").getAsJsonArray();
            for (int i = 0; i < nutrients.size(); i++) {
                JsonObject currentNut = nutrients.get(i).getAsJsonObject();
                if (currentNut.get("nutrient_id").getAsInt() == id) {
                    return currentNut.get("value").getAsInt();
                }
            }
        }
        return -1;
    }
}
