package REST.Repositories;


import Persistence.Entities.SimpleUser;
import Persistence.Entities.VideoEncodingRequest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class VideoEncodingRequestRepository implements JpaRepository<VideoEncodingRequest, String> {

    @Override
    public Optional<VideoEncodingRequest> findById(String s) {
        //TODO
        return Optional.empty();
    }

    @Override
    public <S extends VideoEncodingRequest> S save(S s) {
        //TODO
        return null;
    }


    @Override
    public long count() {
        //TODO
        return 0;
    }

    @Override
    public void deleteById(String id) {
        //TODO
    }


    @Override
    public List<VideoEncodingRequest> findAll() {
        //TODO
        return null;
    }


    /*
     *
     * Operações não implementadas pois não serão usadas na API REST
     *
     */

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
    public void delete(VideoEncodingRequest simpleUser) {
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
