package REST.Controllers;

import Persistence.BucketsEnum;
import Persistence.DAO.VideoEncodingRequestDAO;
import Persistence.DataSourceSingleton;
import Persistence.Entities.VideoEncodingRequest;
import Services.AmazonS3Service;
import Services.Encoding.EncoderService;
import com.bitmovin.api.encoding.status.Task;
import com.bitmovin.api.exceptions.BitmovinApiException;
import com.bitmovin.api.http.RestException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
public class VideoEncodingRequestController {

    private final VideoEncodingRequestDAO videoEncodingRequestDAO = new VideoEncodingRequestDAO(DataSourceSingleton.getInstance().getEntityManager());

    private final AmazonS3Service amazonS3Service = AmazonS3Service.getInstance();
    private final EncoderService encoderService = new EncoderService();

    public VideoEncodingRequestController() throws URISyntaxException, BitmovinApiException, UnirestException, IOException {
    }





    @CrossOrigin(origins = "*")
    @PostMapping("/encoder")
    ResponseEntity startEncodingProcess(@RequestBody String fileName) {
        System.out.println("POST /encoder " + fileName);


        VideoEncodingRequest request ;
        try {
            request = encoderService.encode(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }

        if(request != null)
            return ResponseEntity.ok().body(request);
        else return ResponseEntity.status(500).body(null);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/encoder/{encodingId}")
    ResponseEntity<Task> checkEncodingStatus(@PathVariable String encodingId) throws BitmovinApiException, RestException, UnirestException, IOException, URISyntaxException {
        System.out.println("GET /encoder/{id} " + encodingId);

        VideoEncodingRequest request = videoEncodingRequestDAO.find(encodingId);
        if(request != null){
            return ResponseEntity.ok().body(this.encoderService.getStatusAndProgressOfEncoding(request.getEncodingId()));
        }else return ResponseEntity.notFound().build();
    }



    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/encoder/{encodingId}/manifest")
    ResponseEntity<String> createManifest(@PathVariable String encodingId) throws BitmovinApiException, RestException, UnirestException, IOException, URISyntaxException {
        System.out.println("POST /encoder/{id}/create-manifest " + encodingId);

        VideoEncodingRequest request = videoEncodingRequestDAO.find(encodingId);
        if(request != null){
            if(!request.createdManifest())
                encoderService.createManifest(request);
            return ResponseEntity.ok().body(amazonS3Service.getFileUrl(BucketsEnum.OUTPUT_BUCKET, request.getOutputPath()));
        }else return ResponseEntity.notFound().build();
    }

}
