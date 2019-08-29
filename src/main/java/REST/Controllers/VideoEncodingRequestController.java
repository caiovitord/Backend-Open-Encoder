package REST.Controllers;

import Persistence.Entities.VideoEncodingRequest;
import REST.Repositories.VideoEncodingRequestRepository;
import Services.Encoding.EncoderService;
import Services.Encoding.VideoConfigurationEnum;
import Services.Storage.AmazonS3Service;
import Services.Storage.BucketsEnum;
import com.bitmovin.api.encoding.status.Task;
import com.bitmovin.api.exceptions.BitmovinApiException;
import com.bitmovin.api.http.RestException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;


/**
 * Classe responsável por criar os endpoints de encoding da API REST.
 * <p>
 * Os seus métodos (mapeados em endpoints) são responsáveis por
 * criar uma nova requisição de encoding baseado no nome do arquivo,
 * obter informações sobre um encoding, bem como solicitar a geração do arquivo
 * manifest e deletar um encoding.
 */

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class VideoEncodingRequestController {


    private final AmazonS3Service amazonS3Service = AmazonS3Service.getInstance();
    private final EncoderService encoderService = new EncoderService();

    @Autowired
    private VideoEncodingRequestRepository repository;

    public VideoEncodingRequestController() throws IOException {
    }

    //Método que cria requisição de encoding baseado no nome do arquivo e na qualidade do encoding desejada
    @PostMapping("/api/v1/encodings")
    ResponseEntity createEncoding(@RequestBody Map<String, Object> payload) {
        System.out.println("POST /encodings " + payload.get("fileName").toString() + " " + payload.get("encodingQuality").toString());

        String fileName = payload.get("fileName").toString();
        String encodingQualityStr = payload.get("encodingQuality").toString();

        VideoConfigurationEnum encodingQuality = VideoConfigurationEnum.valueOf(encodingQualityStr);


        Optional<VideoEncodingRequest> videoEncodingRequest = repository.findByInputFileName(fileName);
        if (videoEncodingRequest.isPresent()) {
            try {
                VideoEncodingRequest req = videoEncodingRequest.get();
                encoderService.encode(fileName, encodingQuality, req);

                return ResponseEntity.ok().body(req);

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }else
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

    }


    //Método que cria retorna o encoding por meio do encodingId
    @GetMapping("/api/v1/encodings/{encodingId}")
    ResponseEntity<VideoEncodingRequest> getEncodingById(@PathVariable String encodingId) throws BitmovinApiException, RestException, UnirestException, IOException, URISyntaxException {
        System.out.println("GET /encodings/{id} " + encodingId);

        Optional<VideoEncodingRequest> videoEncodingRequest = repository.findById((encodingId));
        return videoEncodingRequest.map(
                request -> ResponseEntity.ok().body(request))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Método que retorna o status do encoding
    @GetMapping("/api/v1/encodings/{encodingId}/status")
    ResponseEntity<Task> getEncodingStatus(@PathVariable String encodingId) throws BitmovinApiException, RestException, UnirestException, IOException, URISyntaxException {
        System.out.println("GET /encodings/{id}/status " + encodingId);

        Optional<VideoEncodingRequest> videoEncodingRequest = repository.findById((encodingId));
        if (videoEncodingRequest.isPresent())
            return ResponseEntity.ok().body(
                    this.encoderService.getEncodingProgressStatus(videoEncodingRequest.get().getEncodingId())
            );
        else return ResponseEntity.notFound().build();
    }

    //Método que retorna o link  do resultado do encoding
    @GetMapping("/api/v1/encodings/{encodingId}/link")
    ResponseEntity<String> getEncodingLink(@PathVariable String encodingId) {
        System.out.println("GET /encodings/{id} " + encodingId);

        Optional<VideoEncodingRequest> videoEncodingRequest = repository.findById((encodingId));
        if (videoEncodingRequest.isPresent())
            return ResponseEntity.ok().body(amazonS3Service.getFileUrl(BucketsEnum.OUTPUT_BUCKET, encodingId));
        else return ResponseEntity.notFound().build();
    }


    //Método que solicita a criação do arquivo manifest e finaliza o processo de encoding
    @PostMapping("/api/v1/encodings/{encodingId}/manifest")
    ResponseEntity<String> createEncodingManifest(@PathVariable String encodingId) throws BitmovinApiException, RestException, UnirestException, IOException, URISyntaxException {
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

    //Método que deleta um encodingRequest pelo id
    @DeleteMapping("/api/v1/encodings/{encodingId}")
    ResponseEntity deleteEncodingById(@PathVariable String encodingId) {
        System.out.println("DELETE /api/v1/encodings/{id} " + encodingId);

        Optional<VideoEncodingRequest> videoEncodingRequest = repository.findById((encodingId));
        if (videoEncodingRequest.isPresent()) {

            repository.delete(videoEncodingRequest.get());

            return ResponseEntity.noContent().build();
        } else return ResponseEntity.notFound().build();
    }


}
