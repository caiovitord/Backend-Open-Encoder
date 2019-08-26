package Services;

import Persistence.BucketsEnum;
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

public class AmazonS3Service {

    private static final AmazonS3Service instance = new AmazonS3Service();

    private final AmazonS3 s3client;

    public static final String ACCESS_KEY = "AKIAX4GJZQVTXHFJZVOI";
    public static final String SECRET = "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";

    private AmazonS3Service(){
        AWSCredentials credentials = new BasicAWSCredentials(
                ACCESS_KEY,
                SECRET
        );

        s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
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
