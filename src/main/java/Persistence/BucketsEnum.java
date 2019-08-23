package Persistence;

public enum BucketsEnum {
    INPUT_BUCKET("open-encoder-input"),
    OUTPUT_BUCKET("open-encoder-output");

    public final String bucketName;

    public final String bucketHost;

    BucketsEnum(String bucketName) {
        this.bucketName = bucketName;
        this.bucketHost  = bucketName + ".s3.amazonaws.com";
    }
}
