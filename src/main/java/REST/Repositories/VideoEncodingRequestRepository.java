package REST.Repositories;


import Persistence.Entities.SimpleUser;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public class VideoEncodingRequestRepository implements JpaRepository<SimpleUser, String> {


    @Override
    public Optional<SimpleUser> findById(String s) {
        //TODO
        return Optional.empty();
    }

    @Override
    public <S extends SimpleUser> S save(S s) {
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
    public List<SimpleUser> findAll() {
        //TODO
        return null;
    }



    @Override
    public SimpleUser getOne(String id) {
        //TODO
        return null;
    }




    @Override
    public boolean existsById(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flush() {
        //TODO
        //Finaliza as operações e commita
    }


    //Operações não implementadas pois não serão usadas na API REST

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }



    @Override
    public void delete(SimpleUser simpleUser) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void deleteAll(Iterable<? extends SimpleUser> iterable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SimpleUser> findAll(Sort sort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Page<SimpleUser> findAll(Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SimpleUser> findAllById(Iterable<String> iterable) {
        throw new UnsupportedOperationException();
    }


    @Override
    public <S extends SimpleUser> List<S> saveAll(Iterable<S> iterable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends SimpleUser> S saveAndFlush(S s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends SimpleUser> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends SimpleUser> List<S> findAll(Example<S> example) {
        throw new UnsupportedOperationException();
    }



    @Override
    public <S extends SimpleUser> long count(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends SimpleUser> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException();
    }


    @Override
    public <S extends SimpleUser> List<S> findAll(Example<S> example, Sort sort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends SimpleUser> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteInBatch(Iterable<SimpleUser> iterable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAllInBatch() {
        throw new UnsupportedOperationException();
    }

}
