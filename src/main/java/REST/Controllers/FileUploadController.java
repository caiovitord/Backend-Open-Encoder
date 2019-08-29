package REST.Controllers;

import Persistence.Entities.VideoEncodingRequest;
import REST.Repositories.VideoEncodingRequestRepository;
import Services.Storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
 *
 * Essa classe do tipo Spring Controller serve para criar um endpoint na API rest.
 *
 * O endpoint criado por essa classe é responsável por receber um arquivo
 * por meio de uma requisição POST, e utiliza o StorageService para enviar
 * o arquivo para a AWS S3.
 * Este arquivo enviado servirá posteriormente como input do encoding.
 */

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class FileUploadController {

    private final StorageService storageService = new StorageService();

    @Autowired
    private VideoEncodingRequestRepository videoEncodingRequestRepository;

    public FileUploadController() {
    }


    @PostMapping("/api/v1/files")
    public ResponseEntity<String> createFile(@RequestParam("file") MultipartFile file) {
        String resultFileName = storageService.store(file);

        VideoEncodingRequest request = new VideoEncodingRequest();
        request.setInputFileName(resultFileName);

        videoEncodingRequestRepository.save(request);

        return ResponseEntity
                .ok().header("Content-Type"," text/html; charset=utf-8" )
                .body(resultFileName);
    }

}