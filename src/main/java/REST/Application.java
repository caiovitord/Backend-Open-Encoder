package REST;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

/**
* Classe em que o aplicativo Spring começa a execução.
 * O objeto awakeDate guarda quando o servidor foi instanciado
 * E esse dado pode ser obtido pelo endpoint GET /api/v1/
* */
@SpringBootApplication
public class Application {

    public static Date awakeDate;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        awakeDate = new Date();
    }

}
