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
import org.json.JSONException;
import org.json.JSONObject;
 
import com.google.common.collect.Maps;
import javax.annotation.PostConstruct;
import javax.websocket.server.PathParam;
import javax.ejb.EJB;
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
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
 
    // Mapping between session and person name
    private static final HashMap<String, String> nameSessionPair = new HashMap<String, String>();

    // Getting query params
    public static Map<String, String> getQueryMap(String query) {
        Map<String, String> map = Maps.newHashMap();
        if (query != null) {           
            if (query.contains("&")){
                String[] params = query.split("&");
                for (String param : params) {
                    String[] nameval = param.split("=");
                    map.put(nameval[0], nameval[1]);
                }
            } else {
                String[] nameval = query.split("=");
                map.put(nameval[0], nameval[1]);
            }        
        } else {
            System.out.println("Query "+query+" did not have any parameters.");
        }
        return map;
    }
   
    @PostConstruct
    public void init() {
        System.out.println("Socket server class initialized.");
    }
    
    /**
     * Called when a socket connection opened
     * */
    @OnOpen
    public void onOpen(Session session, @PathParam("match") final String match) {
 
        System.out.println(session.getId() + " has opened a connection to match " + match);
 
        Map<String, String> queryParams = getQueryMap(session.getQueryString());
 
        String name = "defaultsessionname";
 
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
            session.getBasicRemote().sendText(JSONUtils.createClientDetailsJson(session.getId(), "Welcome to the tournament. Sorry, no time to add dank memes."));
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendMessageToAll(session.getId(), (String) session.getUserProperties().get("match"), name, " is watching the tournament!", true, false);
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
 
        String team = null;
        String match = (String) session.getUserProperties().get("match");
 
        // Parsing the json and getting message
        try {
            JSONObject jObj = new JSONObject(message);
            team = jObj.getString("team");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String name = nameSessionPair.get(session.getId());
 
        // Sending the message to all clients
        sendMessageToAll(session.getId(), (String) session.getUserProperties().get("match"), nameSessionPair.get(session.getId()),
                name + " is cheering for team "+team, false, false);
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
                json = JSONUtils.createNewClientJsonString(sessionId, name, message, sessions.size());
 
            } else if (isExit) {
                // Checking if the person left the conversation
                json = JSONUtils.createClientExitJsonString(sessionId, name, message, sessions.size());
            } else {
                // Normal chat conversation message
                json = JSONUtils.createSendAllMessageJsonString(sessionId, name, message);
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