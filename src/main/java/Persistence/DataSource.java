package Persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DataSource {

    private final EntityManagerFactory emf;

    private DataSource(){
        emf = Persistence.createEntityManagerFactory("unidade_persistencia_odb");
    }

    public static DataSource instance = new DataSource();

    public static DataSource getInstance(){
        return instance;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

}
