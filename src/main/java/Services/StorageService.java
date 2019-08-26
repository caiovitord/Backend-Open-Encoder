package Services;

import Persistence.BucketsEnum;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;


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
