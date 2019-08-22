package Persistence.Entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SimpleUser {

    @Id
    private String username;



    //Constructors
    public SimpleUser(String username) {
        this.username = username;
    }


    public SimpleUser() {
    }


    //Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
