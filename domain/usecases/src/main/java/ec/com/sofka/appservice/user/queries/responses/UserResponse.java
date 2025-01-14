package ec.com.sofka.appservice.user.queries.responses;

import ec.com.sofka.user.Role;

public class UserResponse {

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Role role;

    public UserResponse(String firstname, String lastname, String email, String password, Role role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}
