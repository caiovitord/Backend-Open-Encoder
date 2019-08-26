package REST.Controllers;

import Services.Storage.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileUploadController {

    private final StorageService storageService = new StorageService();

    public FileUploadController() {
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/file")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {

        return ResponseEntity
                .ok()
                .header("Content-Type"," text/html; charset=utf-8" )
                .body( storageService.store(file));
    }

}