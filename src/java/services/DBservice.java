/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Matches;

/**
 *
 * @author asafgolan
 */
@Stateless
public class DBservice {

    
@PersistenceContext
private EntityManager em;
public Matches insert(Matches m) {        
        System.out.println(m);
        if(getMatches().size()>0){
            System.out.println("updating...");
            em.merge(m);
        } else {
            System.out.println("inserting...");
            em.persist(m);
        }
        System.out.println("finished with presist ...");
        return m;
    }

    public void update(Matches m) {
        em.merge(m);
    }

    public void deleteMatches(Matches m) {
        em.remove(em.merge(m));
    }

    public List<Matches> getMatches() {
        return em.createNamedQuery("Matches.findAll").getResultList();
    }

}
