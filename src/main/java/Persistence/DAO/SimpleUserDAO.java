package Persistence.DAO;

import Persistence.Entities.SimpleUser;

import javax.persistence.EntityManager;

public class SimpleUserDAO extends GenericDAO<SimpleUser, String> {

    public SimpleUserDAO(EntityManager em) {
        super(em);
    }
}
