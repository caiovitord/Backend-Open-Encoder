package Services.Storage;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Essa classe serve como um serviço de armazenamento local de arquivos
 *
 * A classe é responsável por receber um arquivo, por meio de uma requisição POST
 * feita pelo usuário e em seguida enviar o arquivo para a AWS S3. Após o envio ter sido feito
 * a classe deleta o arquivo enviado do disco local.
 *
 * A classe funciona como uma interface para utilizar a classe AmazonS3Service
 *
 */

public class StorageService {

    private final String TEMP_FILES_FOLDER = "tmpfiles";

    private final AmazonS3Service amazonS3Service = AmazonS3Service.getInstance();

    public String store(MultipartFile multipartFile) {
        File file;
        try {
            file = this.multipartFileToFile(multipartFile);

            amazonS3Service.uploadObject(BucketsEnum.INPUT_BUCKET, file);

            file.delete();
            return file.getName();
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }


    private File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        String uniqueFileName = this.generateUniqueFileName(multipartFile.getOriginalFilename());
        File file = new File(TEMP_FILES_FOLDER, uniqueFileName);

        FileUtils.touch(file);
        FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());
        System.out.println("Created file " + uniqueFileName);
        return file;
    }

    private String generateUniqueFileName(String fileName){
        return new Date().getTime() + fileName;
    }


}
