package Services.Storage;

import Configuration.AppConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.File;

/**
 * Classe que implementa o Design Pattern Singleton.
 *
 * Essa classe serve para acessar o serviço de Armazenamento S3 da Amazon.
 * A classe implementa o serviço de realizar uploads de arquivos de vídeo.
 */

public class AmazonS3Service {

    private static final AmazonS3Service instance = new AmazonS3Service();

    private final AmazonS3 s3client;

    static final String AWS_INPUT_BUCKET_NAME = AppConfiguration.AWS_INPUT_BUCKET_NAME;
    static final String AWS_OUTPUT_BUCKET_NAME = AppConfiguration.AWS_OUTPUT_BUCKET_NAME;


    public static final String AWS_ACCESS_KEY = AppConfiguration.AWS_ACCESS_KEY;
    public static final String AWS_SECRET = AppConfiguration.AWS_SECRET;

    private AmazonS3Service(){

        Regions AWS_BUCKET_REGION = Regions.US_EAST_1;

        AWSCredentials credentials = new BasicAWSCredentials(
                AWS_ACCESS_KEY,
                AWS_SECRET
        );

        s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(AWS_BUCKET_REGION)
                .build();
    }


    public static AmazonS3Service getInstance(){
        return instance;
    }

    public PutObjectResult uploadObject(BucketsEnum bucket, File file){
        PutObjectResult result = s3client.putObject(new PutObjectRequest( bucket.bucketName,
                file.getName(),
                file).withCannedAcl(CannedAccessControlList.PublicRead)
        );
        return result;
    }

    public String getFileUrl(BucketsEnum bucket, String fileName){
        if(bucket == BucketsEnum.INPUT_BUCKET)
            return "https://open-encoder-input.s3.amazonaws.com/" + fileName;
        else
            return "https://open-encoder-output.s3.amazonaws.com/" + fileName + "/manifest.m3u8";
    }

}
