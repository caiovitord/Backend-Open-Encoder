package REST.Controllers;

import Services.Storage.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * Classe Spring Controller
 *
 * Essa classe serve para criar um endpoint na API rest.
 *
 * O endpoint criado por essa classe é responsável por receber um arquivo
 * por meio de uma requisição POST, e utiliza o StorageService para enviar
 * o arquivo para a AWS S3.
 * Este arquivo servirá posteriormente como input do encoding.
 */

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class FileUploadController {

    private final StorageService storageService = new StorageService();

    public FileUploadController() {
    }


    @PostMapping("/api/v1/files/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {

        return ResponseEntity
                .ok().header("Content-Type"," text/html; charset=utf-8" )
                .body( storageService.store(file));
    }

}