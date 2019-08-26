package Persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


/**
* Essa classe, que implementa o Design Pattern Singleton, serve
* para fornecer uma referência da classe EntityManger,  bem como configurar
* a API de persistência JPA .
*
* O EntityManger é uma classe da JPA, e fornece acesso direto
* aos objetos do banco de dados (que é realizado nas classes contidas em Persistence.DAO)
* */
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
