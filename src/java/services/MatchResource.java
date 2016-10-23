/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import model.Matches;

/**
 * REST Web Service
 *
 * @author asafgolan
 */
@Path("/match")
public class MatchResource {
    
    @EJB
    DBservice dbs;
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of MatchResource
     */
    public MatchResource() {
        //List<Matches> list = dbs.getMatches();
    }

    /**
     * Retrieves representation of an instance of services.MatchResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/html")
    public String getHtml() {
        //TODO return proper representation object
        //throw new UnsupportedOperationException();
        
        java.util.Date date = new java.util.Date();
        System.out.println("current TimeStamp" + date);
        long tmpResult = 0;
        long result = 0;
        int j = 0;
        System.out.println(date);
        List <Matches> list = dbs.getMatches();
        for(int i =0; i < list.size(); i++){
            //System.out.println(list.get(i).getDate());
            if(list.get(i).getDate() != null){
                System.out.println("====================");
                System.out.println("down under...");
                System.out.println(list.get(i).getDate());
                System.out.println("the date retrived from match ...");
                System.out.println(formatTime(list.get(i).getDate()));
                
                System.out.println("current TimeStamp");
                System.out.println(date);
                System.out.println("Time Diff");
                tmpResult = getDateDiff(date,formatTime(list.get(i).getDate()),TimeUnit.MINUTES);
                System.out.println(tmpResult);
                if(tmpResult <= 0){
                    //meaning match has started
                    if(j == 0 || tmpResult > result){
                        System.out.println("J is --> ");
                        j = i;
                        System.out.println(j);
                        result = tmpResult;
                        System.out.println("Final result is  -----> ");
                        System.out.println(result);
                    }
                    
                }
                System.out.println("down under...");
                
                System.out.println("====================");
            }
            
        }
        System.out.println("the curr date : " + date  + "\n" + "The matchStart date : " + formatTime(list.get(j).getDate()) + "\n" + "THE date DIFF : " + tmpResult);
        return list.get(j).getId() ;
    }

    /**
     * PUT method for updating or creating an instance of MatchResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("text/html")
    public void putHtml(String content) {
    }
    
    
    public Date formatTime(String fullDate){
        Date dte = null;
        String truncatedDate = fullDate.substring(0, fullDate.lastIndexOf('-'));
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
                Locale.ENGLISH);
        try {

            dte = format.parse(truncatedDate);
            System.out.println("date=" + dte);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dte;
    }
    
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
        
    
}
