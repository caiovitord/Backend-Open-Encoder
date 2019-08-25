package Persistence.Entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.sql.Date;

@Entity
public class VideoEncodingRequest {

    @Id
    private String encodingId;


    @ManyToOne
    private SimpleUser requestingUser;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @NotNull
    private String outputPath;
    @NotNull
    private String audioStreamId;
    @NotNull
    private String fmp4AudioMuxinId;
    @NotNull
    private String streamVideoId;
    @NotNull
    private String videoMuxinId;

    @NotNull
    private boolean createdManifest;

    public VideoEncodingRequest(String encodingId, String outputPath, String audioStreamId, String fmp4AudioMuxinId, String streamVideoId, String videoMuxinId, boolean createdManifest) {
        this.encodingId = encodingId;
        this.outputPath = outputPath;
        this.audioStreamId = audioStreamId;
        this.fmp4AudioMuxinId = fmp4AudioMuxinId;
        this.streamVideoId = streamVideoId;
        this.videoMuxinId = videoMuxinId;
        this.createdManifest = createdManifest;
        this.createdAt = new Date(new java.util.Date().getTime());
    }



    public VideoEncodingRequest() {

    }


    public String getEncodingId() {
        return encodingId;
    }

    public void setEncodingId(String encodingId) {
        this.encodingId = encodingId;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getAudioStreamId() {
        return audioStreamId;
    }

    public void setAudioStreamId(String audioStreamId) {
        this.audioStreamId = audioStreamId;
    }

    public String getFmp4AudioMuxinId() {
        return fmp4AudioMuxinId;
    }

    public void setFmp4AudioMuxinId(String fmp4AudioMuxinId) {
        this.fmp4AudioMuxinId = fmp4AudioMuxinId;
    }

    public String getStreamVideoId() {
        return streamVideoId;
    }

    public void setStreamVideoId(String streamVideoId) {
        this.streamVideoId = streamVideoId;
    }

    public String getVideoMuxinId() {
        return videoMuxinId;
    }

    public void setVideoMuxinId(String videoMuxinId) {
        this.videoMuxinId = videoMuxinId;
    }

    public boolean createdManifest() {
        return createdManifest;
    }

    public void setCreatedManifest(boolean createdManifest) {
        this.createdManifest = createdManifest;
    }
}
