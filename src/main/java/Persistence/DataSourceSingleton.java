package Persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DataSourceSingleton {

    private final EntityManagerFactory emf;

    private DataSourceSingleton(){
        emf = Persistence.createEntityManagerFactory("unidade_persistencia_odb");
    }

    public static DataSourceSingleton instance = new DataSourceSingleton();

    public static DataSourceSingleton getInstance(){
        return instance;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

}
