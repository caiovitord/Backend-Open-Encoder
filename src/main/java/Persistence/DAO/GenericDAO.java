package Persistence.DAO;

import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
* Essa é a Classe genérica de acesso ao banco de dados.
* Ela é responsável por executar as operações de CRUD nos objetos.
*
* DAO ( Data Access Object ou Objeto de acesso a dados ) é uma Design Pattern.
*
* Essa classe utiliza a Java Persistence API (JPA) para poder
* acessar os objetos guardados no arquivo de banco de dados.
*
* O arquivo de banco de dados é gerenciado pela biblioteca ObjectDB.
 * Link para o site do ObjectDB: https://www.objectdb.com/
*
* A sua configuração é definida no arquivo java/resources/META-INF/persistence.xml
* e também na classe Persistence.DataSourceSingleton
*
* A utilização da Java Persistence API junto com o ObecjtDB é interessante para uma aplicação simples
* pois o ObjectDB provém uma solução livre de tabelas e esquemas, sendo assim, uma implementação mais
* prática e agil para aplicações que não seja imprescindível implementar os princípios A.C.I.D.
*
 */

public abstract class GenericDAO<E, K> {

    protected EntityManager em;


    private Class<E> clazz = (Class<E>) ((ParameterizedType) this.getClass().
            getGenericSuperclass()).
            getActualTypeArguments()[0];

    public GenericDAO(EntityManager em) {
        this.em = em;
    }

    public void create(E entity) {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
    }

    public void update(E entity) {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
        em.merge(entity);
        em.getTransaction().commit();
    }

    public E find(K key) {
        return em.find(clazz, key);
    }

    public void delete(E entity) {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
        em.remove(entity);
        em.getTransaction().commit();
    }

    public void deleteByKey(K key) {
        E obj = this.find(key);
        if (obj != null) {
            this.delete(obj);
        }
    }

    public List<E> findAll() {
        return em.createQuery(
                "select from " + clazz.getSimpleName()
        ).getResultList();
    }

    public int getCount() {
        return (Integer) em.createQuery(
                "select count(*) from " +
                        clazz.getSimpleName()
        ).getSingleResult();
    }


}
