package Persistence.DAO;

import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;
import java.util.List;

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
