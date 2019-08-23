package Persistence.DAO;

import Persistence.Entities.VideoFile;

import javax.persistence.EntityManager;

public class VideoFileDAO extends GenericDAO<VideoFile, Long> {
    public VideoFileDAO(EntityManager em) {
        super(em);
    }
}
