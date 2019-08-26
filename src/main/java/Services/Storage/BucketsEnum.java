package Services.Storage;

/**
 * Classe que serve como uma enumeração simples
 * para buckets de entrada e saída
 */
public enum BucketsEnum {
    INPUT_BUCKET(AmazonS3Service.AWS_INPUT_BUCKET_NAME),
    OUTPUT_BUCKET(AmazonS3Service.AWS_OUTPUT_BUCKET_NAME);

    public final String bucketName;
    public final String bucketHost;

    BucketsEnum(String bucketName) {
        this.bucketName = bucketName;
        this.bucketHost  = bucketName + ".s3.amazonaws.com";
    }
}
