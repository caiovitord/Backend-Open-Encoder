package Persistence;

public enum BucketsEnum {
    INPUT_BUCKET("open-encoder-input"),
    OUTPUT_BUCKET("open-encoder-output");

    public final String bucketName;

    BucketsEnum(String bucketName) {
        this.bucketName = bucketName;
    }
}
