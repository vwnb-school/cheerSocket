/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cheerPackage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import org.json.JSONException;
import org.json.JSONObject;




/**
 *
 * @author villekem
 */
public class JSONUtils {

    // flags to identify the kind of json response on client side
    private static final String FLAG_SELF = "self", FLAG_NEW = "new",
            FLAG_MESSAGE = "message", FLAG_EXIT = "exit";
 
    private JSONUtils() {
    }
 
    /**
     * Json when client needs it's own session details
     * */
    public static String getClientDetailsJson(String sessionId, String message) {
        String json = null;
 
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_SELF);
            jObj.put("sessionId", sessionId);
            jObj.put("message", message);
 
            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        return json;
    }
 
    /**
     * Json to notify all the clients about new person joined
     * */
    public static String getNewClientJson(String sessionId, String name,
            String message, int onlineCount) {
        String json = null;
 
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_NEW);
            jObj.put("name", name);
            jObj.put("sessionId", sessionId);
            jObj.put("message", message);
            jObj.put("onlineCount", onlineCount);
 
            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        return json;
    }
 
    /**
     * Json when the client exits the socket connection
     * */
    public static String getClientExitJson(String sessionId, String name,
            String message, int onlineCount) {
        String json = null;
 
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_EXIT);
            jObj.put("name", name);
            jObj.put("sessionId", sessionId);
            jObj.put("message", message);
            jObj.put("onlineCount", onlineCount);
 
            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        return json;
    }    

    /**
     * JSON when message needs to be sent to all the clients
     * */
    public static String getSendAllMessageJson(String sessionId, String fromName, String message) {
        String json = null;
 
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("flag", FLAG_MESSAGE);
            jObj.put("sessionId", sessionId);
            jObj.put("name", fromName);
            jObj.put("message", message);
 
            json = jObj.toString();
 
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        return json;
    }
    
    public static String getJsonAttributeValue(String rawJson, String attribute) throws MalformedJsonException, JsonSyntaxException {
        //just a single Json Object
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(rawJson);
        if (json.isJsonObject()) {
            return json.getAsJsonObject().get(attribute).getAsString();
        } else if (json.isJsonPrimitive()) {
            return json.getAsString();
        } else {
            System.out.println("This function only works on Json objects and primitives, use getValieFromArrayElement for arrays");
            return null;
        }
    }

    public static String getValueFromArrayElement(String jsonArrayString, String attribute, int index) throws MalformedJsonException, JsonSyntaxException {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(jsonArrayString);
        if (json.isJsonArray()) {
            JsonElement firstItem = json.getAsJsonArray().get(index);
            if (firstItem.isJsonPrimitive()) {
                return firstItem.getAsString();
            } else if (firstItem.isJsonObject()) {
                return firstItem.getAsJsonObject().get(attribute).getAsString();
            } else {
                System.out.println("This function only goes in 1 level (from Array to Object in array, or primitive).");
                return null;
            }
        } else {
            System.out.println("This function only works on Json arrays.");
            return null;
        }
    }
}
