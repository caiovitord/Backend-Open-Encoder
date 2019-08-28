package REST.Controllers;

import REST.Application;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * Essa classe do tipo Spring Controller serve para criar um endpoint na API rest.
 *
 * O endpoint criado por essa classe é responsável por receber uma requisição
 * no diretório raiz da API e do servidor.
 * Estes endpoints servem como verificação simples e imediata se o servidor está de pé
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class ServerAwakeMessageController {

    //Métodos que servem como verificação de funcionamento correto do servidor
    @GetMapping("/api/v1/")
    public ResponseEntity<String> answerServerHelloAndAwakeTime() {
        return ResponseEntity
                .ok().header("Content-Type"," text/html; charset=utf-8" )
                .body("Hello! I'm awake! I've been working since " + Application.awakeDate);
    }

    //Métodos que servem como verificação de funcionamento correto do servidor
    @GetMapping("/")
    public ResponseEntity<String> answerServerHello() {
        return ResponseEntity
                .ok().header("Content-Type"," text/html; charset=utf-8" )
                .body("Hello, I'm here!");
    }

}
