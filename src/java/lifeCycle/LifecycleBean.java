package lifeCycle;

import HttpUtil.HttpUtility;
import cheerPackage.JSONUtils;
import cheerPackage.SocketServer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
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
        System.out.println("\n \n \n \n \n \n \n");
        System.out.println("I AM THE SERVER STARTING BE WAARE :::::::");
        System.out.println("\n \n \n \n \n \n \n");
        boolean updateMatches = true;
        if (updateMatches) {
            System.out.println("Updating local database...");
            try {
                System.out.println("=====================================");
                String authTokenRaw = requestToornamentAuthToken();
                String requestURL = "https://api.toornament.com/v1/tournaments" + "?name=" + URLEncoder.encode(tournamentName, "UTF-8");
                String tournamentInfoRaw = getTournamentInfo(authTokenRaw, requestURL);
                String matchesRaw = getTournamentMatches(authTokenRaw, tournamentInfoRaw);
                insertTournamentMatchesToDB(matchesRaw);
            } catch (JSONException | MalformedJsonException | JsonSyntaxException | UnsupportedEncodingException ex) {
                Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }
    }

    @PreDestroy
    public void destroy() {
        // Shutdown stuff here 
    } 

    public String getTournamentInfo(String authTokenRaw, String URL) throws MalformedJsonException, JsonSyntaxException {
        StringBuilder sb = new StringBuilder();
        try {
            String authToken = JSONUtils.getJsonAttributeValue(authTokenRaw, "access_token");
            HttpUtility.sendGetRequest(URL, authToken);
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

    public String getTournamentMatches(String authTokenRaw, String tournamentsArrayRaw) throws MalformedJsonException, JsonSyntaxException {
        String tournament_id = JSONUtils.getValueFromArrayElement(tournamentsArrayRaw, "id", 0);
        String requestURL = "https://api.toornament.com/v1/tournaments/" + tournament_id + "/matches";
        StringBuilder sb = new StringBuilder();
        try {
            HttpUtility.sendGetRequest(requestURL, JSONUtils.getJsonAttributeValue(authTokenRaw, "access_token"));
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

    public String requestToornamentAuthToken() {
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

    public void insertTournamentMatchesToDB(String res) throws JSONException {
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
}
