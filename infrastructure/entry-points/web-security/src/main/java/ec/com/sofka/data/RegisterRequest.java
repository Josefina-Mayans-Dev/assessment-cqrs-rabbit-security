package ec.com.sofka.data;

import ec.com.sofka.user.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for creating a user")
public class RegisterRequest {

    @NotNull(message = "firstname cannot be null")
    @Schema(description = "Firstname assigned to the user", example = "Cersei")
    private String firstname;

    @NotNull(message = "lastname cannot be null")
    @Schema(description = "Lastname assigned to the user", example = "Lannister")
    private String lastname;

    @NotNull(message = "Email cannot be null")
    @Schema(description = "Email assigned to the user", example = "cersei@lannister.com")
    private String email;

    @NotNull(message = "password name cannot be null")
    @Schema(description = "Password  for the user", example = "12345")
    private String password;

    @NotNull(message = "Role name cannot be null")
    @Schema(description = "Roles  for the user", example = "ADMIN,USER")
    private Role role;

    public RegisterRequest(String firstname, String lastname, String email, String password, Role role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public RegisterRequest(){}

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }
}
