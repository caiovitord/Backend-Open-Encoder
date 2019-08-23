package REST.Controllers;

import Persistence.DAO.VideoFileDAO;
import Persistence.DataSource;
import Persistence.Entities.SimpleUser;
import Persistence.Entities.VideoFile;
import REST.Exceptions.UserAlreadyExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class VideoEncodingRequestController {

    private final VideoFileDAO videoFileDAO = new VideoFileDAO(DataSource.getInstance().getEntityManager());


    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/encoder")
    ResponseEntity<String> startEncodingProcess(@RequestBody String fileName) {
        System.out.println(fileName);
        VideoFile video = videoFileDAO.find(fileName);
        return ResponseEntity.ok().body(video.getFileName());
    }

}
