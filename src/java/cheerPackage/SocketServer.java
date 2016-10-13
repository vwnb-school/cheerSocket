/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cheerPackage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
 
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import HttpUtil.HttpUtility; 
import org.json.JSONException;
import org.json.JSONObject;
 
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.websocket.server.PathParam;
import org.json.JSONArray;
import controller.ViewController;
import java.util.Date;
import javax.ejb.EJB;
import model.Matches;
import services.DBservice;
 
@ServerEndpoint(value = "/chat/{room}")
public class SocketServer {
    private String res;
    private String tournamentName = "MLG Columbus 2018 CS:GO Major";
    //ViewController viewCtrl = new ViewController();
    //ViewController viewCtrl;
    @EJB
    DBservice dbs;
    
    // set to store all the live sessions
    private static final Set<Session> sessions = Collections
            .synchronizedSet(new HashSet<Session>());
 
    // Mapping between session and person name
    private static final HashMap<String, String> nameSessionPair = new HashMap<String, String>();
 
    private JSONUtils jsonUtils = new JSONUtils();
 
    // Getting query params
    public static Map<String, String> getQueryMap(String query) {
        Map<String, String> map = Maps.newHashMap();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] nameval = param.split("=");
                map.put(nameval[0], nameval[1]);
            }
        }
        return map;
    }
   
    @PostConstruct
    public void init() {
        
         
        System.out.println("=====================================");
         
        // test sending POST request
        Map<String, String> params = new HashMap<String, String>();
        String requestURL = "https://api.toornament.com/oauth/v2/token";
        params.put("client_id", "57e98f68150ba076398b456c5q9hvtpk3tgc00kk8s4sgckow40gs4080wgc48ogsc4wg04o8c");
        params.put("client_secret", "5l9wwq20nugw4gswgoso088gw80wccgcw8sco44oo4g84ooco8");
        params.put("grant_type", "client_credentials");
         
        try {
            HttpUtility.sendPostRequest(requestURL, params);
            String[] response = HttpUtility.readMultipleLinesRespone();
            for (String line : response) {
                res = line;
                //System.out.println(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HttpUtility.disconnect();
        /**JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(res);
        JsonObject object = json.getAsJsonObject();
        object.toString();**/
        //String token = object.get("access_token").getAsString();
        String token = null;
        try {
            token = getAccessToken(res,"access_token");
            //System.out.println(gson.toJson(resWithBearer));
            //System.out.println("HERE IS MY TOKEN");
            //System.out.println(token);
        } catch (JSONException ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            requestURL = "https://api.toornament.com/v1/tournaments" + "?name=" +  URLEncoder.encode(tournamentName, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            HttpUtility.sendGetRequest(requestURL , token);
            String[] response = HttpUtility.readMultipleLinesRespone();
            for (String line : response) {
                //System.out.println("HERE the tournament with requested name...");
                res= line;
                
                //System.out.println(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HttpUtility.disconnect();
        String tournament_id = null;
        try {
            tournament_id =getMatchesByTurnament_id(res, "id");
            } catch (JSONException ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        requestURL = "https://api.toornament.com/v1/tournaments/" + tournament_id   +"/matches";
        try {
            HttpUtility.sendGetRequest(requestURL , token);
            String[] response = HttpUtility.readMultipleLinesRespone();
            for (String line : response) {
                res = line;
                System.out.println("HERE IS ALL MATCHESSSS  ::::::.....");
                System.out.println(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HttpUtility.disconnect();
        
   
        try {
            insertTournamentMatchesToDB(res);
        } catch (JSONException ex) {
            System.out.println("THROWING EXCEPTION");
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
            
        }
       
         
    
    }
    public String getAccessToken (String res, String title) throws JSONException{
        String value = null;
            //just a single Json Object
            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(res);
            System.out.println(json);
            System.out.println("HERE IS MY value ---->>>");  
            JsonObject object = json.getAsJsonObject();
            System.out.println(object.toString());
            object.toString();
            value = object.get(title).getAsString();
            
            return value;
        }
    public String getMatchesByTurnament_id(String res, String title) throws JSONException{
        String value = null;
        if(res.charAt(0)=='['){
            JSONArray jsonArray = new JSONArray(res); 
            System.out.println("\n"+"\n"+"\n"+"\n"+"\n"+"\n"+"\n"+"\n");
            System.out.println("PRINT JSON ARRAY -->>>>>");
            System.out.println(jsonArray.getJSONObject(0).get(title));
            value = (String) jsonArray.getJSONObject(0).get(title);
         }
        return value; 
    }
    
    public void insertTournamentMatchesToDB(String res) throws JSONException{
        
        //cant deal with null information -->>>>>
        Matches match = new Matches();
        System.out.println("calling from socketServer ...");
        //convert res json Array
        JSONArray jsonArray = new JSONArray(res);
        //iterate JSONobj in the array inserting the to DB..
        for (int i = 0; i < jsonArray.length(); i++) {
            //System.out.println("from for loop the OBJECT");
            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(jsonArray.get(i).toString());
            JsonObject object = json.getAsJsonObject();
            object.toString();
            match.setId(object.get("id").getAsString());
            match.setType(object.get("type").getAsString());
            match.setDiscipline(object.get("discipline").getAsString());
            match.setStatus(object.get("status").getAsString());
            match.setTournamentId(object.get("tournament_id").getAsString());
            match.setNumber(object.get("number").getAsInt());
            match.setStageNumber(object.get("stage_number").getAsInt());
            match.setGroupNumber(object.get("group_number").getAsInt());
            match.setRoundNumber(object.get("round_number").getAsInt());
            
            match.setTimezone("FI");
            /**if(object.get("timeZone").getAsString() == null){
                System.out.println("THE TIMEZONE...");
                match.setTimezone("FI");
            }else{
                match.setTimezone(object.get("timeZone").getAsString());
            }**/
            
            match.setMatchFormat("knockout");
            match.setOpponents(object.get("opponents").toString());
            /**Date today = new Date();
            today.setHours(0); today.setMinutes(0); today.setSeconds(0);
            match.setDate(today);**/
            //viewCtrl.insertMatches(match);
            dbs.insert(match);
        }
        
    }
    
 
    /**
     * Called when a socket connection opened
     * */
    @OnOpen
    public void onOpen(Session session, @PathParam("room") final String room) {
 
        System.out.println(session.getId() + " has opened a connection in room " + room);
 
        Map<String, String> queryParams = getQueryMap(session.getQueryString());
 
        String name = "";
 
        if (queryParams.containsKey("name")) {
 
            // Getting client name via query param
            name = queryParams.get("name");
            try {
                name = URLDecoder.decode(name, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
 
            // Mapping client name and session id
            nameSessionPair.put(session.getId(), name);
        }
 
        // Put to room
        session.getUserProperties().put("room", room);
        
        // Adding session to session list
        sessions.add(session);
 
        try {
            // Sending session id to the client that just connected
            session.getBasicRemote().sendText(
                    jsonUtils.getClientDetailsJson(session.getId(),
                            "Your session details"));
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        // Notifying all the clients about new person joined
        sendMessageToAll(session.getId(), (String) session.getUserProperties().get("room"), name, " joined conversation!", true,
                false);
 
    }
 
    /**
     * method called when new message received from any client
     * 
     * @param message
     *            JSON message from client
     * */
    @OnMessage
    public void onMessage(String message, Session session) {
 
        System.out.println("Message from " + session.getId() + ": " + message);
 
        String msg = null;
        String room = (String) session.getUserProperties().get("room");
 
        // Parsing the json and getting message
        try {
            JSONObject jObj = new JSONObject(message);
            msg = jObj.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        // Sending the message to all clients
        sendMessageToAll(session.getId(), (String) session.getUserProperties().get("room"), nameSessionPair.get(session.getId()),
                msg, false, false);
    }
 
    /**
     * Method called when a connection is closed
     * */
    @OnClose
    public void onClose(Session session) {
 
        System.out.println("Session " + session.getId() + " has ended");
 
        // Getting the client name that exited
        String name = nameSessionPair.get(session.getId());
 
        // removing the session from sessions list
        sessions.remove(session);
 
        // Notifying all the clients about person exit
        sendMessageToAll(session.getId(), (String) session.getUserProperties().get("room"), name, " left conversation!", false,
                true);
 
    }
 
    /**
     * Method to send message to all clients
     * 
     * @param sessionId
     * @param message
     *            message to be sent to clients
     * @param isNewClient
     *            flag to identify that message is about new person joined
     * @param isExitrooms
     *            flag to identify that a person left the conversation
     * */
    private void sendMessageToAll(String sessionId, String room, String name,
            String message, boolean isNewClient, boolean isExit) {
 
        // Looping through all the sessions and sending the message individually
        for (Session s : sessions) {
            String json = null;
            
            if( !room.equals(s.getUserProperties().get("room")) ){
                continue;
            }
 
            // Checking if the message is about new client joined
            if (isNewClient) {
                json = jsonUtils.getNewClientJson(sessionId, name, message,
                        sessions.size());
 
            } else if (isExit) {
                // Checking if the person left the conversation
                json = jsonUtils.getClientExitJson(sessionId, name, message,
                        sessions.size());
            } else {
                // Normal chat conversation message
                json = jsonUtils
                        .getSendAllMessageJson(sessionId, name, message);
            }
 
            try {
                System.out.println("Sending Message To: " + sessionId + ", "
                        + json);
 
                s.getBasicRemote().sendText(json);
            } catch (IOException e) {
                System.out.println("error in sending. " + s.getId() + ", "
                        + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}