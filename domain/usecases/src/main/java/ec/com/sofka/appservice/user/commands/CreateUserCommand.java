package ec.com.sofka.appservice.user.commands;

import ec.com.sofka.generics.utils.Command;
import ec.com.sofka.user.Role;

public class CreateUserCommand extends Command {

    private final String firstname;
    private final  String lastname;
    private final String email;
    private final String password;
    private final Role role;

    public CreateUserCommand(String firstname, String lastname, String email, String password, Role role) {
        super(null);
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