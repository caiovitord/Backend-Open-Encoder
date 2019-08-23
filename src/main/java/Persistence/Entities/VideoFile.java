package Persistence.Entities;

import Persistence.BucketsEnum;

import javax.persistence.*;

@Entity
public class VideoFile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String fileName;

    @Enumerated(EnumType.STRING)
    private BucketsEnum bucket;

    public VideoFile() {
    }

    public VideoFile(String fileName, BucketsEnum bucket) {
        this.fileName = fileName;
        this.bucket = bucket;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public BucketsEnum getBucket() {
        return bucket;
    }

    public void setBucket(BucketsEnum bucket) {
        this.bucket = bucket;
    }
}
