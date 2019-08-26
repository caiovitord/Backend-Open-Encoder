package REST.Controllers;

import REST.Repositories.VideoEncodingRequestRepository;
import Services.Storage.BucketsEnum;
import Persistence.DAO.VideoEncodingRequestDAO;
import Persistence.DataSourceSingleton;
import Persistence.Entities.VideoEncodingRequest;
import Services.Storage.AmazonS3Service;
import Services.Encoding.EncoderService;
import com.bitmovin.api.encoding.status.Task;
import com.bitmovin.api.exceptions.BitmovinApiException;
import com.bitmovin.api.http.RestException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
public class VideoEncodingRequestController {


    private final AmazonS3Service amazonS3Service = AmazonS3Service.getInstance();
    private final EncoderService encoderService = new EncoderService();
    private final VideoEncodingRequestRepository repository = new VideoEncodingRequestRepository();

    public VideoEncodingRequestController() throws IOException {
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/encoder")
    ResponseEntity startEncodingProcess(@RequestBody String fileName) {
        System.out.println("POST /encoder " + fileName);

        VideoEncodingRequest request;
        try {
            request = encoderService.encode(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        if (request != null)
            return ResponseEntity.ok().body(request);
        else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/encoder/{encodingId}")
    ResponseEntity<Task> checkEncodingStatus(@PathVariable String encodingId) throws BitmovinApiException, RestException, UnirestException, IOException, URISyntaxException {
        System.out.println("GET /encoder/{id} " + encodingId);

        Optional<VideoEncodingRequest> request = repository.findById((encodingId));
        if (request.isPresent())
            return ResponseEntity.ok().body(
                    this.encoderService.getEncodingProgressStatus(request.get().getEncodingId())
            );
        else return ResponseEntity.notFound().build();
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/encoder/{encodingId}/manifest")
    ResponseEntity<String> createManifest(@PathVariable String encodingId) throws BitmovinApiException, RestException, UnirestException, IOException, URISyntaxException {
        System.out.println("POST /encoder/{id}/create-manifest " + encodingId);

        Optional<VideoEncodingRequest> request = repository.findById((encodingId));
        if (request.isPresent()) {
            if (!request.get().createdManifest())
                encoderService.createManifest(request.get());

            return ResponseEntity.ok().body(
                    amazonS3Service.getFileUrl(BucketsEnum.OUTPUT_BUCKET, request.get().getOutputPath())
            );
        } else return ResponseEntity.notFound().build();
    }

}
