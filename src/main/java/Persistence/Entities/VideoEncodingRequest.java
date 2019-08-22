package Persistence.Entities;

import javax.persistence.*;

@Entity
public class VideoEncodingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;


    @ManyToOne
    private SimpleUser requestingUser;





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


}
