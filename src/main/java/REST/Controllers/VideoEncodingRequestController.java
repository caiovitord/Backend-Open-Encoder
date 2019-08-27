package REST.Controllers;

import REST.Repositories.VideoEncodingRequestRepository;
import Services.Encoding.VideoConfigurationEnum;
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
import java.util.Map;
import java.util.Optional;


/**
 * Essa classe é um controller Spring.
 * Ela é responsável por criar os endpoints de encoding da API.
 *
 * Os seus métodos (mapeados em endpoints da API) são responsáveis por
 * criar uma nova requisição de encoding baseado no nome do arquivo,
 * obter informações sobre um encoding, bem como solicitar a geração do arquivo
 * manifest e deletar um encoding.
 *
 */

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class VideoEncodingRequestController {


    private final AmazonS3Service amazonS3Service = AmazonS3Service.getInstance();
    private final EncoderService encoderService = new EncoderService();
    private final VideoEncodingRequestRepository repository = new VideoEncodingRequestRepository();

    public VideoEncodingRequestController() throws IOException {
    }


    @PostMapping("/api/v1/encodings")
    ResponseEntity startEncodingProcess(@RequestBody Map<String, Object> payload) {
        System.out.println("POST /encoder " + payload.get("fileName").toString() + " " +  payload.get("encodingQuality").toString());

        String fileName = payload.get("fileName").toString();
        String encodingQualityStr = payload.get("encodingQuality").toString();

        VideoConfigurationEnum encodingQuality = VideoConfigurationEnum.valueOf(encodingQualityStr);

        VideoEncodingRequest videoEncodingRequest;
        try {
            videoEncodingRequest = encoderService.encode(fileName, encodingQuality);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        if (videoEncodingRequest != null)
            return ResponseEntity.ok().body(videoEncodingRequest);
        else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @GetMapping("/api/v1/encodings/{encodingId}")
    ResponseEntity<Task> checkEncodingStatus(@PathVariable String encodingId) throws BitmovinApiException, RestException, UnirestException, IOException, URISyntaxException {
        System.out.println("GET /encodings/{id} " + encodingId);

        Optional<VideoEncodingRequest> videoEncodingRequest = repository.findById((encodingId));
        if (videoEncodingRequest.isPresent())
            return ResponseEntity.ok().body(
                    this.encoderService.getEncodingProgressStatus(videoEncodingRequest.get().getEncodingId())
            );
        else return ResponseEntity.notFound().build();
    }


    @PostMapping("/api/v1/encodings/{encodingId}/manifest")
    ResponseEntity<String> createManifest(@PathVariable String encodingId) throws BitmovinApiException, RestException, UnirestException, IOException, URISyntaxException {
        System.out.println("POST /api/v1/encodings/{id}/manifest " + encodingId);

        Optional<VideoEncodingRequest> videoEncodingRequest = repository.findById((encodingId));
        if (videoEncodingRequest.isPresent()) {
            if (!videoEncodingRequest.get().createdManifest())
                encoderService.createManifest(videoEncodingRequest.get());

            return ResponseEntity.ok().body(
                    amazonS3Service.getFileUrl(BucketsEnum.OUTPUT_BUCKET, videoEncodingRequest.get().getOutputPath())
            );
        } else return ResponseEntity.notFound().build();
    }


    @DeleteMapping("/api/v1/encodings/{encodingId}")
    ResponseEntity deleteEncoding(@PathVariable String encodingId) {
        System.out.println("DELETE /api/v1/encodings/{id} " + encodingId);

        Optional<VideoEncodingRequest> videoEncodingRequest = repository.findById((encodingId));
        if (videoEncodingRequest.isPresent()) {

            repository.delete(videoEncodingRequest.get());

            return ResponseEntity.noContent().build();
        } else return ResponseEntity.notFound().build();
    }






}
