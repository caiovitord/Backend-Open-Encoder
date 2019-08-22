package Persistence.Entities;

import javax.persistence.*;

@Entity
public class UserVideoEncodingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;


    @ManyToOne
    private SimpleUser requestingUser;





    //Constructors
    public UserVideoEncodingRequest(SimpleUser requestingUser) {
        this.requestingUser = requestingUser;
    }

    public UserVideoEncodingRequest() {

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
