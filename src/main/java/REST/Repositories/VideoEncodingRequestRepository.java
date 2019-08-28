package REST.Repositories;


import Persistence.DAO.VideoEncodingRequestDAO;
import Persistence.DataSourceSingleton;
import Persistence.Entities.VideoEncodingRequest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Classe responsável por acessar os objetos VideoEncodingRequest.
 * Os seus métodos implementam operações CRUD nos objetos
 */
@Repository
public class VideoEncodingRequestRepository implements JpaRepository<VideoEncodingRequest, String> {

    VideoEncodingRequestDAO videoEncodingRequestDAO = new VideoEncodingRequestDAO(DataSourceSingleton.getInstance().getEntityManager());

    public VideoEncodingRequestRepository() {
    }

    @Override
    public Optional<VideoEncodingRequest> findById(String s) {
        return Optional.ofNullable(videoEncodingRequestDAO.find(s));
    }

    @Override
    public <S extends VideoEncodingRequest> S save(S s) {
        videoEncodingRequestDAO.create(s);
        return s;
    }

    @Override
    public void delete(VideoEncodingRequest request) {
        videoEncodingRequestDAO.delete(request);
    }

    @Override
    public List<VideoEncodingRequest> findAll() {
        return videoEncodingRequestDAO.findAll();
    }


    /**Construtor somente utilizado em testes*/
    public VideoEncodingRequestRepository(EntityManager test_persistence_unit) {
        videoEncodingRequestDAO = new VideoEncodingRequestDAO(test_persistence_unit);
    }
    /**
     * Operações não implementadas pois não serão usadas na API REST
     * A exceção é lançada para garantir que o método não será utilizado
     * indevidamente, sem ter sido implementado
     *
     */

    @Override
    public void deleteById(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException();
    }



    @Override
    public boolean existsById(String s) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void flush() {
        throw new UnsupportedOperationException();
    }

    @Override
    public VideoEncodingRequest getOne(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }



    @Override
    public void deleteAll(Iterable<? extends VideoEncodingRequest> iterable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VideoEncodingRequest> findAll(Sort sort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Page<VideoEncodingRequest> findAll(Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VideoEncodingRequest> findAllById(Iterable<String> iterable) {
        throw new UnsupportedOperationException();
    }


    @Override
    public <S extends VideoEncodingRequest> List<S> saveAll(Iterable<S> iterable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends VideoEncodingRequest> S saveAndFlush(S s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends VideoEncodingRequest> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends VideoEncodingRequest> List<S> findAll(Example<S> example) {
        throw new UnsupportedOperationException();
    }



    @Override
    public <S extends VideoEncodingRequest> long count(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends VideoEncodingRequest> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException();
    }


    @Override
    public <S extends VideoEncodingRequest> List<S> findAll(Example<S> example, Sort sort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends VideoEncodingRequest> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteInBatch(Iterable<VideoEncodingRequest> iterable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAllInBatch() {
        throw new UnsupportedOperationException();
    }
}
