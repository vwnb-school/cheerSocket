package lifeCycle;

import cheerPackage.HttpUtility;
import cheerPackage.SocketServer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Startup;
import javax.ejb.Singleton;
import javax.inject.Inject;
import model.Matches;
import org.json.JSONArray;
import org.json.JSONException;
import services.DBservice;

@Singleton
@Startup
public class LifecycleBean {
    
   private String res;
    private String tournamentName = "MLG Columbus 2018 CS:GO Major";
    //ViewController viewCtrl = new ViewController();
    //ViewController viewCtrl;
    
    @EJB
    DBservice dbs;
    
    
  @PostConstruct
  public void init() {
    /* Startup stuff here. */
      
      System.out.println("\n"+ "\n"+ "\n"+ "\n"+ "\n"+ "\n"+ "\n");
       System.out.println("I AM THE SERVER STARTING BE WAARE :::::::");
       System.out.println("\n"+ "\n"+ "\n"+ "\n"+ "\n"+ "\n"+ "\n");
       /**Map<String, String> params = new HashMap<String, String>();
        
       String requestURL = "https://api.toornament.com/oauth/v2/token";
        params.put("client_id", "57e98f68150ba076398b456c5q9hvtpk3tgc00kk8s4sgckow40gs4080wgc48ogsc4wg04o8c");
        params.put("client_secret", "5l9wwq20nugw4gswgoso088gw80wccgcw8sco44oo4g84ooco8");
        params.put("grant_type", "client_credentials");
         
        try {
            System.out.println("IM sending post req ...");
            HttpUtility.sendPostRequest(requestURL, params);
            String[] response = HttpUtility.readMultipleLinesRespone();
            for (String line : response) {
                res = line;
                System.out.println("here is the response from the req ...");
                System.out.println(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HttpUtility.disconnect();
        //JsonParser parser = new JsonParser();
        //JsonElement json = parser.parse(res);
        //JsonObject object = json.getAsJsonObject();
        //object.toString();
        //String token = object.get("access_token").getAsString();
        String token = null;
        try {
            token = getAccessToken(res,"access_token");
            System.out.println("AND FERE IS THE TOKEN ...");
            System.out.println(token);
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

  @PreDestroy
  public void destroy() {
    // Shutdown stuff here 
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
            
            //match.setTimezone("FI");
            //if(object.get("timeZone").getAsString() == null){
            //    System.out.println("THE TIMEZONE...");
            //    match.setTimezone("FI");
            //}else{
            //    match.setTimezone(object.get("timeZone").getAsString());
            //}
            
            match.setMatchFormat("knockout");
            match.setOpponents(object.get("opponents").toString());
            //Date today = new Date();
            //today.setHours(0); today.setMinutes(0); today.setSeconds(0);
            //match.setDate(today);
            //viewCtrl.insertMatches(match);
            dbs.insert(match);
        }**/
    }
}