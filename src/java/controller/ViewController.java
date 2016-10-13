/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
//import javax.persistence.EntityManager;
//import javax.persistence.EntityTransaction;
//import javax.persistence.Persistence;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import model.Matches;
import services.DBservice;
import org.hibernate.validator.constraints.Email;

/**
 *
 * @author asafgolan
 */
@Named(value = "viewController")
@SessionScoped
public class ViewController implements Serializable {

    @EJB
    private DBservice dBservice;
    
    private List<Matches> listMatches;
    private Matches matches;
    /**
     * Creates a new instance of ViewController
     */
    public ViewController() {
    }
    
    @PostConstruct
    public void init(){
        matches = new Matches();
        listMatches = dBservice.getMatches();
    }
    
    public Matches getMatches(){
        return matches;
    }
    public List<Matches> getListMatches() {
       return listMatches;
    }
    
    /**public void listBy (boolean orderByName){
        listMatches = DBservice.getMatches(orderByName);
     * @param m}**/
    
    public void insertMatches(Matches m){
           System.out.print("from VIEW CONTROLLER ....");
           System.out.println(m.getId());
           System.out.println(m.getType());
           System.out.println(m.getDiscipline());
           System.out.println(m.getStatus());
           System.out.println(m.getTournamentId());
           System.out.println(m.getNumber());
           System.out.println(m.getStageNumber());
           System.out.println(m.getRoundNumber());
           System.out.println(m.getGroupNumber());
           //System.out.println(m.getDate());
           System.out.println(m.getTimezone());
           System.out.println(m.getMatchFormat());
           System.out.println(m.getOpponents());
           System.out.println("im stuck here"); 
           //System.out.println(DBservice.insert(matches));
           dBservice.insert(m);
           //DBservice db = new DBservice() ;
           //db.insert(m);
           //DBservice.insert(m);
    }
    /**public void updatePeople(People p){
       sessionbean.update(p);
    }
    
    public void deletePeople(People p){
        sessionbean.deletePeople(p);
        listPeople.remove(p);
    }**/
    
    
}
