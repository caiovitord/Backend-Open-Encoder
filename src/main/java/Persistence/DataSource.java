package Persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DataSource {

    private final EntityManager em;

    private DataSource(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unidade_persistencia_odb");
        em = emf.createEntityManager();
        emf.close();
    }

    public static DataSource instance = new DataSource();

    public static DataSource getInstance(){
        return instance;
    }

    public EntityManager getEntityManager() {
        return em;
    }

}
