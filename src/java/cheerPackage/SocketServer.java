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
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
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
 
@ServerEndpoint(value = "/cheer/{match}")
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
        boolean updateMatches = false;
        if(updateMatches){
            try {
                System.out.println("=====================================");       
                String authTokenRaw = requestToornamentAuthToken();            
                String requestURL = "https://api.toornament.com/v1/tournaments" + "?name=" +  URLEncoder.encode(tournamentName, "UTF-8");
                String tournamentInfoRaw = getTournamentInfo(authTokenRaw, requestURL);
                String matchesRaw = getTournamentMatches(authTokenRaw, tournamentInfoRaw);
                insertTournamentMatchesToDB(matchesRaw);
            } catch (JSONException | MalformedJsonException | JsonSyntaxException | UnsupportedEncodingException ex) {
                Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }
        System.out.println("Socket server class initialized.");
    }
    
    public String getTournamentInfo(String authTokenRaw, String URL) throws MalformedJsonException, JsonSyntaxException {           
        StringBuilder sb = new StringBuilder();
        try {
            String authToken = JSONUtils.getJsonAttributeValue(authTokenRaw, "access_token");
            HttpUtility.sendGetRequest(URL , authToken);
            String[] response = HttpUtility.readMultipleLinesRespone();
            for (String line : response) {      
                sb.append(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HttpUtility.disconnect();
        return sb.toString();
    }
    public String getTournamentMatches(String authTokenRaw, String tournamentsArrayRaw) throws MalformedJsonException, JsonSyntaxException{
        String tournament_id = JSONUtils.getValueFromArrayElement(tournamentsArrayRaw, "id", 0);                 
        String requestURL = "https://api.toornament.com/v1/tournaments/" + tournament_id   +"/matches";
        StringBuilder sb = new StringBuilder();               
        try {
            HttpUtility.sendGetRequest(requestURL , JSONUtils.getJsonAttributeValue(authTokenRaw, "access_token"));
            String[] response = HttpUtility.readMultipleLinesRespone();
            for (String line : response) {
                sb.append(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HttpUtility.disconnect();
        return sb.toString();
    }      
    public String requestToornamentAuthToken(){
        StringBuilder result = new StringBuilder();
        Map<String, String> params = new HashMap<String, String>();
        String requestURL = "https://api.toornament.com/oauth/v2/token";
        params.put("client_id", "57e98f68150ba076398b456c5q9hvtpk3tgc00kk8s4sgckow40gs4080wgc48ogsc4wg04o8c");
        params.put("client_secret", "5l9wwq20nugw4gswgoso088gw80wccgcw8sco44oo4g84ooco8");
        params.put("grant_type", "client_credentials");
         
        try {
            HttpUtility.sendPostRequest(requestURL, params);
            String[] response = HttpUtility.readMultipleLinesRespone();
            for (String line : response) {
                result.append(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HttpUtility.disconnect();
        return result.toString();
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
            
            match.setMatchFormat("knockout");
            match.setOpponents(object.get("opponents").toString());
            
            dbs.insert(match);
        }
        
    }
    
    /**
     * Called when a socket connection opened
     * */
    @OnOpen
    public void onOpen(Session session, @PathParam("match") final String match) {
 
        System.out.println(session.getId() + " has opened a connection to match " + match);
 
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
 
        // Put to match
        session.getUserProperties().put("match", match);
        
        // Adding session to session list
        sessions.add(session);
 
        try {
            // Sending session id to the client that just connected
            session.getBasicRemote().sendText(
                    JSONUtils.getClientDetailsJson(session.getId(),
                            "Your session details"));
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        // Notifying all the clients about new person joined
        sendMessageToAll(session.getId(), (String) session.getUserProperties().get("match"), name, " is watching the tournament!", true,
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
        String match = (String) session.getUserProperties().get("match");
 
        // Parsing the json and getting message
        try {
            JSONObject jObj = new JSONObject(message);
            msg = jObj.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        // Sending the message to all clients
        sendMessageToAll(session.getId(), (String) session.getUserProperties().get("match"), nameSessionPair.get(session.getId()),
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
        sendMessageToAll(session.getId(), (String) session.getUserProperties().get("match"), name, " left conversation!", false,
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
     * @param isExitmatchs
     *            flag to identify that a person left the conversation
     * */
    private void sendMessageToAll(String sessionId, String match, String name,
            String message, boolean isNewClient, boolean isExit) {
 
        // Looping through all the sessions and sending the message individually
        for (Session s : sessions) {
            String json = null;
            
            if( !match.equals(s.getUserProperties().get("match")) ){
                continue;
            }
 
            // Checking if the message is about new client joined
            if (isNewClient) {
                json = JSONUtils.getNewClientJson(sessionId, name, message, sessions.size());
 
            } else if (isExit) {
                // Checking if the person left the conversation
                json = JSONUtils.getClientExitJson(sessionId, name, message, sessions.size());
            } else {
                // Normal chat conversation message
                json = JSONUtils.getSendAllMessageJson(sessionId, name, message);
            }
 
            try {
                System.out.println("Sending Message To: " + sessionId + ", " + json);
 
                s.getBasicRemote().sendText(json);
            } catch (IOException e) {
                System.out.println("error in sending. " + s.getId() + ", " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}