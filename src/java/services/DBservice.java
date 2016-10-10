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
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
public Matches insert(Matches m) {
        System.out.println("inserting...");
        System.out.println(m);
        em.persist(m);
        System.out.println("finishe with presist ...");
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
