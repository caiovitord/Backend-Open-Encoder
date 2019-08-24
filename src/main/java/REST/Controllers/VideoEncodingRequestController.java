package REST.Controllers;

import Persistence.DAO.VideoFileDAO;
import Persistence.DataSource;
import Persistence.Entities.VideoEncodingRequest;
import Persistence.Entities.VideoFile;
import Services.Encoding.EncoderService;
import com.bitmovin.api.exceptions.BitmovinApiException;
import com.bitmovin.api.http.RestException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
public class VideoEncodingRequestController {

    private final VideoFileDAO videoFileDAO = new VideoFileDAO(DataSource.getInstance().getEntityManager());
    //private final AmazonS3Service amazonS3Service = AmazonS3Service.getInstance();
    private final EncoderService encoderService = new EncoderService();

    public VideoEncodingRequestController() throws URISyntaxException, BitmovinApiException, UnirestException, IOException {
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/encoder")
    ResponseEntity<VideoEncodingRequest> startEncodingProcess(@RequestBody String fileName) {
        System.out.println("POST /encoder " + fileName);

        VideoFile video = videoFileDAO.find(fileName);
        //String fileInputUrl = amazonS3Service.getFileUrl(BucketsEnum.INPUT_BUCKET, fileName);

        VideoEncodingRequest request = null;
        try {
            request = encoderService.encode(fileName);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (BitmovinApiException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RestException e) {
            e.printStackTrace();
        }

        if(request != null)
            return ResponseEntity.ok().body(request);
        else return ResponseEntity.status(500).body(null);
    }

}
