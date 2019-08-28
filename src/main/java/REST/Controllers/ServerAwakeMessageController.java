package REST.Controllers;

import REST.Application;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class ServerAwakeMessageController {

    @GetMapping("/api/v1/")
    public ResponseEntity<String> awakeTime() {
        return ResponseEntity
                .ok().header("Content-Type"," text/html; charset=utf-8" )
                .body("Hello! I'm awake! I've been working since " + Application.awakeDate);
    }

    @GetMapping("/")
    public ResponseEntity<String> hello() {
        return ResponseEntity
                .ok().header("Content-Type"," text/html; charset=utf-8" )
                .body("Hello, I'm here!");
    }

}
