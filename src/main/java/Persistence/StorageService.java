package Persistence;

import org.apache.commons.io.FileUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;


public class StorageService {

    private final String TEMP_FILES_FOLDER = "tmpfiles";

    public String store(MultipartFile file) {
        try {
            return this.multipartFileToFile(file);



        } catch (IOException e) {
            e.printStackTrace();
            return "ERR_NO_FILE_CREATED";
        }

        //TODO enviar pra AWS
        //Salvar o modelo de videofile
        //Deletar localmente
    }

    private String multipartFileToFile(MultipartFile multipartFile) throws IOException {
        String uniqueFileName = this.generateUniqueFileName(multipartFile.getOriginalFilename());
        File file = new File(TEMP_FILES_FOLDER, uniqueFileName);
        FileUtils.touch(file);
        FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());
        System.out.println("Created file " + uniqueFileName);
        return uniqueFileName;
    }

    private String generateUniqueFileName(String fileName){
        return new Date().getTime() + fileName;
    }


}
