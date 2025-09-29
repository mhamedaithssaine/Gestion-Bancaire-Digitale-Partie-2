package model;

import java.util.UUID;

public class User {

    private UUID id ;
    private String fullname;
    private String email;
    private String password;
    private String role ;

    public User( String fullname, String email,String password, String role) {
        this.id = UUID.randomUUID();
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.role = role ;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullname;
    }

    public void setFullName(String fullName)
    {
        this.fullname = fullName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole ()
    {
        return role;
    }
    public void setRole (String role)
    {
        this.role = role ;
    }

}
