package Persistence.DAO;

import Persistence.Entities.VideoEncodingRequest;

import javax.persistence.EntityManager;

public class VideoEncodingRequestDAO extends GenericDAO<VideoEncodingRequest, String> {

    public VideoEncodingRequestDAO(EntityManager em) {
        super(em);
    }
}
