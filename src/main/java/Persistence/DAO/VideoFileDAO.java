package Persistence.DAO;

import Persistence.Entities.VideoFile;
import com.amazonaws.services.rekognition.model.Video;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class VideoFileDAO extends GenericDAO<VideoFile, Long> {
    public VideoFileDAO(EntityManager em) {
        super(em);
    }


    public VideoFile find(String fileName) {
        TypedQuery<VideoFile> query = em.createQuery(
                "select video from VideoFile video where video.fileName = :name"
                ,VideoFile.class);
        return query.setParameter("name", fileName).getSingleResult();
    }

}
