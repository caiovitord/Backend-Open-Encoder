package Persistence;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;



public class StorageService {

    private final String TEMP_FILES_FOLDER = "tmpfiles";

    public void store(MultipartFile file) {
        //TODO enviar pra AWS
        //Salvar o modelo de videofile
        //Deletar localmente
    }



    public void multipartFileToFile(
            MultipartFile multipart,
            Path dir
    ) throws IOException {
        Path filepath = Paths.get(dir.toString() + '/' + TEMP_FILES_FOLDER, multipart.getOriginalFilename());
        multipart.transferTo(filepath);
    }
}
