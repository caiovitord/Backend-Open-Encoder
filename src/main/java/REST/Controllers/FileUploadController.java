package REST.Controllers;

import Services.StorageService;
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


    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        //TODO
        //Resource file = storageService.loadAsResource(filename);
        return null;
       /* return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);*/
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/file")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        return ResponseEntity
                .ok()
                .header("Content-Type"," text/html; charset=utf-8" )
                .body( storageService.store(file));
    }

}