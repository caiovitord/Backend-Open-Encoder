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
}
