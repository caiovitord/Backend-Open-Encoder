package Persistence.Services;

import Persistence.BucketsEnum;
import Persistence.DAO.VideoFileDAO;
import Persistence.DataSource;
import Persistence.Entities.VideoFile;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.apache.commons.io.FileUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;


public class StorageService {

    private final String TEMP_FILES_FOLDER = "tmpfiles";
    private final AmazonS3Service amazonS3Service = AmazonS3Service.getInstance();
    private final VideoFileDAO videoFileDAO = new VideoFileDAO(DataSource.getInstance().getEntityManager());

    public String store(MultipartFile multipartFile) {
        File file;
        try {
            file = this.multipartFileToFile(multipartFile);

            amazonS3Service.uploadObject(BucketsEnum.INPUT_BUCKET, file);

            this.createAndSaveVideoFileEntity(file.getName(), BucketsEnum.INPUT_BUCKET);

            file.delete();
            return file.getName();
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        }



        //TODO enviar pra AWS
        //Salvar o modelo de videofile
        //Deletar localmente
    }

    private void createAndSaveVideoFileEntity(String name,  BucketsEnum bucket) {
        VideoFile videoFile = new VideoFile(name, bucket);
        videoFileDAO.create(videoFile);
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
