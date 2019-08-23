package Persistence.Entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Entity
public class VideoEncodingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;


    @ManyToOne
    private SimpleUser requestingUser;

    @NotNull
    private long inputVideoFileId;

    private long outputVideoFileId;




    //Constructors
    public VideoEncodingRequest(SimpleUser requestingUser) {
        this.requestingUser = requestingUser;
    }

    public VideoEncodingRequest() {

    }


    //Getter and Setters
    public SimpleUser getRequestingUser() {
        return requestingUser;
    }

    public void setRequestingUser(SimpleUser requestingUser) {
        this.requestingUser = requestingUser;
    }

    public String getId() {
        return id;
    }

    public long getInputVideoFileId() {
        return inputVideoFileId;
    }

    public void setInputVideoFileId(long inputVideoFileId) {
        this.inputVideoFileId = inputVideoFileId;
    }

    public long getOutputVideoFileId() {
        return outputVideoFileId;
    }

    public void setOutputVideoFileId(long outputVideoFileId) {
        this.outputVideoFileId = outputVideoFileId;
    }
}
