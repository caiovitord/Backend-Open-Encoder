package Persistence.DAO;

import Persistence.Entities.VideoEncodingRequest;

import javax.persistence.EntityManager;


/**
* Implementação da classe Genérica de Acesso aos Objetos.
* Nenhuma sobrecarga de métodos foi necessária, pois não foi utilizada nenhuma lógica diferente
 * da implementada na classe genérica.
 *
 * Essa classe serve somente para podermos criar uma instância de GenericDAO, já que a
 * classe é abstrata, e não pode ser instanciada.
* */
public class VideoEncodingRequestDAO extends GenericDAO<VideoEncodingRequest, String> {

    public VideoEncodingRequestDAO(EntityManager em) {
        super(em);
    }

    public VideoEncodingRequest findByEncodingId(String encodingId){
        try {
            return (VideoEncodingRequest) em.createQuery(
                    "select vr from VideoEncodingRequest vr where vr.encodingId = :encodingId"
            ).setParameter("encodingId", encodingId).getSingleResult();
        }catch (com.objectdb.o._NoResultException e){
            return null;
        }
    }

    public VideoEncodingRequest findByInputFilename(String fileName){
        try {
            return (VideoEncodingRequest) em.createQuery(
                    "select vr from VideoEncodingRequest vr where vr.inputFileName = :fileName"
            ).setParameter("fileName", fileName).getSingleResult();
        }catch (com.objectdb.o._NoResultException e){
            return null;
        }
    }
}
